package cn.coderstory.springboot.device.service.impl;

import cn.coderstory.springboot.device.entity.DeviceInfo;
import cn.coderstory.springboot.device.entity.DeviceStatusRecord;
import cn.coderstory.springboot.device.mapper.DeviceInfoMapper;
import cn.coderstory.springboot.device.mapper.DeviceStatusRecordMapper;
import cn.coderstory.springboot.device.service.DeviceStatusService;
import cn.coderstory.springboot.device.ws.DeviceStatusWebSocketHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceStatusServiceImpl implements DeviceStatusService {

    private static final String REDIS_DEVICE_STATUS_PREFIX = "device:status:";
    private static final String REDIS_DEVICE_HEARTBEAT_PREFIX = "device:heartbeat:";
    private static final long STATUS_EXPIRE_SECONDS = 300;
    private static final long HEARTBEAT_EXPIRE_SECONDS = 120;

    private final StringRedisTemplate stringRedisTemplate;
    private final DeviceInfoMapper deviceInfoMapper;
    private final DeviceStatusRecordMapper deviceStatusRecordMapper;
    private final ObjectMapper objectMapper;

    // Break circular dependency: use setter injection
    private DeviceStatusWebSocketHandler webSocketHandler;

    @Autowired
    public void setWebSocketHandler(DeviceStatusWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void updateDeviceStatus(String deviceNo, Map<String, Object> status) {
        try {
            status.put("deviceNo", deviceNo);
            status.put("updateTime", LocalDateTime.now().toString());
            String json = objectMapper.writeValueAsString(status);
            stringRedisTemplate.opsForValue().set(REDIS_DEVICE_STATUS_PREFIX + deviceNo, json, STATUS_EXPIRE_SECONDS, TimeUnit.SECONDS);

            DeviceStatusRecord record = new DeviceStatusRecord();
            record.setDeviceNo(deviceNo);
            record.setRecordTime(LocalDateTime.now());
            if (status.containsKey("onlineStatus")) {
                record.setOnlineStatus(((Number) status.get("onlineStatus")).intValue());
            }
            if (status.containsKey("runStatus")) {
                record.setRunStatus(((Number) status.get("runStatus")).intValue());
            }
            if (status.containsKey("temperature")) {
                record.setTemperature(new BigDecimal(status.get("temperature").toString()));
            }
            if (status.containsKey("cpuUsage")) {
                record.setCpuUsage(new BigDecimal(status.get("cpuUsage").toString()));
            }
            if (status.containsKey("memoryUsage")) {
                record.setMemoryUsage(new BigDecimal(status.get("memoryUsage").toString()));
            }
            LambdaQueryWrapper<DeviceInfo> w = new LambdaQueryWrapper<>();
            w.eq(DeviceInfo::getDeviceNo, deviceNo);
            DeviceInfo device = deviceInfoMapper.selectOne(w);
            if (device != null) {
                record.setDeviceId(device.getId());
                device.setOnlineStatus(record.getOnlineStatus() != null ? record.getOnlineStatus() : 0);
                deviceInfoMapper.updateById(device);
                deviceStatusRecordMapper.insert(record);
            } else {
                log.warn("Device not found for deviceNo: {}, skipping status record insert", deviceNo);
            }

            if (webSocketHandler != null) {
                webSocketHandler.broadcast(Map.of("type", "update", "data", status));
            }
        } catch (Exception e) {
            log.error("Failed to update device status for {}", deviceNo, e);
        }
    }

    @Override
    public Map<String, Object> getDeviceStatus(String deviceNo) {
        try {
            String json = stringRedisTemplate.opsForValue().get(REDIS_DEVICE_STATUS_PREFIX + deviceNo);
            if (json != null) {
                return objectMapper.readValue(json, new TypeReference<>() {});
            }
        } catch (Exception e) {
            log.error("Failed to get device status from Redis for {}", deviceNo, e);
        }
        return Map.of("deviceNo", deviceNo, "onlineStatus", 0);
    }

    @Override
    public Map<String, Object> getAllDeviceStatusSnapshot() {
        Map<String, Object> snapshot = new HashMap<>();
        try {
            org.springframework.data.redis.core.Cursor<String> cursor = stringRedisTemplate.scan(
                    org.springframework.data.redis.core.ScanOptions.scanOptions()
                            .match(REDIS_DEVICE_STATUS_PREFIX + "*")
                            .count(100)
                            .build());
            while (cursor.hasNext()) {
                String key = cursor.next();
                String deviceNo = key.substring(REDIS_DEVICE_STATUS_PREFIX.length());
                snapshot.put(deviceNo, getDeviceStatus(deviceNo));
            }
        } catch (Exception e) {
            log.error("Failed to get all device status snapshot", e);
        }
        return snapshot;
    }

    @Override
    public void recordHeartbeat(String deviceNo) {
        stringRedisTemplate.opsForValue().set(REDIS_DEVICE_HEARTBEAT_PREFIX + deviceNo,
                String.valueOf(System.currentTimeMillis()), HEARTBEAT_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public boolean isDeviceOnline(String deviceNo) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(REDIS_DEVICE_HEARTBEAT_PREFIX + deviceNo));
    }

    @Scheduled(fixedDelay = 60000)
    public void checkDeviceHeartbeats() {
        try {
            LambdaQueryWrapper<DeviceInfo> w = new LambdaQueryWrapper<>();
            w.eq(DeviceInfo::getDeleted, 0);
            List<DeviceInfo> devices = deviceInfoMapper.selectList(w);
            for (DeviceInfo device : devices) {
                boolean online = isDeviceOnline(device.getDeviceNo());
                if (device.getOnlineStatus() != null && device.getOnlineStatus() == 1 && !online) {
                    device.setOnlineStatus(0);
                    deviceInfoMapper.updateById(device);
                    if (webSocketHandler != null) {
                        webSocketHandler.broadcast(Map.of(
                                "type", "offline",
                                "data", Map.of("deviceNo", device.getDeviceNo(), "onlineStatus", 0)
                        ));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Heartbeat check failed", e);
        }
    }
}
