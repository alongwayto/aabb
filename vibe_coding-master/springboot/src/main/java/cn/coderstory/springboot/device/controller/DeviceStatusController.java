package cn.coderstory.springboot.device.controller;

import cn.coderstory.springboot.device.mapper.DeviceStatusRecordMapper;
import cn.coderstory.springboot.device.service.DeviceStatusService;
import cn.coderstory.springboot.vo.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "设备状态监控", description = "实时状态查询、历史趋势")
@RestController
@RequestMapping("/api/device-status")
@RequiredArgsConstructor
public class DeviceStatusController {

    private final DeviceStatusService deviceStatusService;
    private final DeviceStatusRecordMapper deviceStatusRecordMapper;

    @Operation(summary = "上报设备状态（心跳+参数）")
    @PostMapping("/report")
    public ResponseEntity<ApiResponse<Void>> reportStatus(@RequestBody Map<String, Object> status) {
        String deviceNo = (String) status.get("deviceNo");
        deviceStatusService.recordHeartbeat(deviceNo);
        deviceStatusService.updateDeviceStatus(deviceNo, status);
        return ResponseEntity.ok(ApiResponse.success("状态上报成功", null));
    }

    @Operation(summary = "查询单设备实时状态")
    @GetMapping("/{deviceNo}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus(@PathVariable String deviceNo) {
        return ResponseEntity.ok(ApiResponse.success(deviceStatusService.getDeviceStatus(deviceNo)));
    }

    @Operation(summary = "查询所有设备状态快照")
    @GetMapping("/snapshot")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSnapshot() {
        return ResponseEntity.ok(ApiResponse.success(deviceStatusService.getAllDeviceStatusSnapshot()));
    }

    @Operation(summary = "查询设备在线率趋势")
    @GetMapping("/{deviceId}/trend")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOnlineRateTrend(
            @PathVariable Long deviceId,
            @RequestParam(defaultValue = "24") int hours) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusHours(hours);
        List<Map<String, Object>> trend = deviceStatusRecordMapper.selectOnlineRateTrend(deviceId, start, end);
        return ResponseEntity.ok(ApiResponse.success(trend));
    }

    @Operation(summary = "查询所有设备状态汇总")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDeviceStatusSummary() {
        return ResponseEntity.ok(ApiResponse.success(deviceStatusRecordMapper.selectDeviceStatusSummary()));
    }
}
