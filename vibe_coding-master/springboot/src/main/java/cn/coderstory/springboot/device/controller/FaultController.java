package cn.coderstory.springboot.device.controller;

import cn.coderstory.springboot.device.entity.FaultReport;
import cn.coderstory.springboot.device.service.FaultService;
import cn.coderstory.springboot.vo.ApiResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "故障管理", description = "故障上报、派单、处理流程")
@RestController
@RequestMapping("/api/fault")
@RequiredArgsConstructor
public class FaultController {

    private final FaultService faultService;

    @Operation(summary = "分页查询故障列表")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> listFaults(
            @RequestParam(required = false) String deviceNo,
            @RequestParam(required = false) String faultType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer faultLevel,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<FaultReport> pageParam = new Page<>(page, size);
        IPage<FaultReport> result = faultService.getFaultPage(pageParam, deviceNo, faultType, status, faultLevel);
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "获取故障详情")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FaultReport>> getFault(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(faultService.getFaultById(id)));
    }

    @Operation(summary = "上报故障")
    @PostMapping
    public ResponseEntity<ApiResponse<FaultReport>> reportFault(@RequestBody FaultReport fault) {
        return ResponseEntity.ok(ApiResponse.success(faultService.reportFault(fault)));
    }

    @Operation(summary = "派单处理")
    @PutMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<FaultReport>> assignFault(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        Long assigneeId = req.get("assigneeId") != null ? ((Number) req.get("assigneeId")).longValue() : null;
        String assigneeName = (String) req.get("assigneeName");
        return ResponseEntity.ok(ApiResponse.success(faultService.assignFault(id, assigneeId, assigneeName)));
    }

    @Operation(summary = "开始处理")
    @PutMapping("/{id}/handle")
    public ResponseEntity<ApiResponse<FaultReport>> handleFault(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        Long handlerId = req.get("handlerId") != null ? ((Number) req.get("handlerId")).longValue() : null;
        String handlerName = (String) req.get("handlerName");
        String handleDesc = (String) req.get("handleDesc");
        return ResponseEntity.ok(ApiResponse.success(faultService.handleFault(id, handlerId, handlerName, handleDesc)));
    }

    @Operation(summary = "解决故障")
    @PutMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<FaultReport>> resolveFault(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        String resolveDesc = (String) req.get("resolveDesc");
        java.math.BigDecimal cost = req.get("cost") != null
                ? new java.math.BigDecimal(req.get("cost").toString()) : null;
        return ResponseEntity.ok(ApiResponse.success(faultService.resolveFault(id, resolveDesc, cost)));
    }

    @Operation(summary = "关闭故障单")
    @PutMapping("/{id}/close")
    public ResponseEntity<ApiResponse<FaultReport>> closeFault(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(faultService.closeFault(id)));
    }

    @Operation(summary = "故障类型统计")
    @GetMapping("/stat/type")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFaultTypeStat() {
        return ResponseEntity.ok(ApiResponse.success(faultService.getFaultTypeStat()));
    }

    @Operation(summary = "故障趋势（近N天）")
    @GetMapping("/stat/trend")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFaultTrend(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(faultService.getFaultTrend(days)));
    }
}
