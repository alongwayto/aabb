package cn.coderstory.springboot.device.controller;

import cn.coderstory.springboot.device.entity.DeviceInfo;
import cn.coderstory.springboot.device.mapper.DeviceInfoMapper;
import cn.coderstory.springboot.device.mapper.DeviceStatusRecordMapper;
import cn.coderstory.springboot.device.service.FaultService;
import cn.coderstory.springboot.vo.ApiResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "数据分析", description = "设备使用率、故障统计、维护成本分析")
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final DeviceInfoMapper deviceInfoMapper;
    private final FaultService faultService;
    private final DeviceStatusRecordMapper deviceStatusRecordMapper;

    @Operation(summary = "设备概览统计")
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        Map<String, Object> data = new HashMap<>();
        long total = deviceInfoMapper.selectCount(new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getDeleted, 0));
        data.put("totalDevices", total);
        long online = deviceInfoMapper.selectCount(new LambdaQueryWrapper<DeviceInfo>()
                .eq(DeviceInfo::getDeleted, 0).eq(DeviceInfo::getOnlineStatus, 1));
        data.put("onlineDevices", online);
        data.put("onlineRate", total > 0 ? Math.round(online * 100.0 / total) : 0);
        long running = deviceInfoMapper.selectCount(new LambdaQueryWrapper<DeviceInfo>()
                .eq(DeviceInfo::getDeleted, 0).eq(DeviceInfo::getStatus, 1));
        data.put("runningDevices", running);
        long fault = deviceInfoMapper.selectCount(new LambdaQueryWrapper<DeviceInfo>()
                .eq(DeviceInfo::getDeleted, 0).eq(DeviceInfo::getStatus, 2));
        data.put("faultDevices", fault);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "故障类型统计（饼图）")
    @GetMapping("/fault-type")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFaultTypeStat() {
        return ResponseEntity.ok(ApiResponse.success(faultService.getFaultTypeStat()));
    }

    @Operation(summary = "故障趋势（柱图）")
    @GetMapping("/fault-trend")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFaultTrend(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(faultService.getFaultTrend(days)));
    }

    @Operation(summary = "设备状态分布")
    @GetMapping("/device-status-dist")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDeviceStatusDist() {
        return ResponseEntity.ok(ApiResponse.success(deviceStatusRecordMapper.selectDeviceStatusSummary()));
    }
}
