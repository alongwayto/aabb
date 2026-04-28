package cn.coderstory.springboot.device.service.impl;

import cn.coderstory.springboot.device.entity.DeviceOperationLog;
import cn.coderstory.springboot.device.mapper.DeviceOperationLogMapper;
import cn.coderstory.springboot.device.service.DeviceOpLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceOpLogServiceImpl implements DeviceOpLogService {

    private final DeviceOperationLogMapper logMapper;

    @Override
    public void log(String operationType, String targetType, Long targetId, String targetName,
                    Long operatorId, String operatorName, String operationDesc, String ip) {
        DeviceOperationLog log = new DeviceOperationLog();
        log.setOperationType(operationType);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setTargetName(targetName);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationDesc(operationDesc);
        log.setIp(ip);
        logMapper.insert(log);
    }

    @Override
    public IPage<DeviceOperationLog> getLogs(Page<DeviceOperationLog> page, String targetType, Long targetId) {
        LambdaQueryWrapper<DeviceOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (targetType != null && !targetType.isEmpty()) {
            wrapper.eq(DeviceOperationLog::getTargetType, targetType);
        }
        if (targetId != null) {
            wrapper.eq(DeviceOperationLog::getTargetId, targetId);
        }
        wrapper.orderByDesc(DeviceOperationLog::getCreateTime);
        return logMapper.selectPage(page, wrapper);
    }
}
