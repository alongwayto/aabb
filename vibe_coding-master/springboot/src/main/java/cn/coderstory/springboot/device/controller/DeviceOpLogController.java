package cn.coderstory.springboot.device.controller;

import cn.coderstory.springboot.device.entity.DeviceOperationLog;
import cn.coderstory.springboot.device.service.DeviceOpLogService;
import cn.coderstory.springboot.vo.ApiResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "设备操作日志", description = "设备相关操作日志查询")
@RestController
@RequestMapping("/api/device-log")
@RequiredArgsConstructor
public class DeviceOpLogController {

    private final DeviceOpLogService deviceOpLogService;

    @Operation(summary = "分页查询设备操作日志")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLogs(
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<DeviceOperationLog> pageParam = new Page<>(page, size);
        IPage<DeviceOperationLog> result = deviceOpLogService.getLogs(pageParam, targetType, targetId);
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
