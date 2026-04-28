package cn.coderstory.springboot.device.controller;

import cn.coderstory.springboot.device.entity.DeviceCategory;
import cn.coderstory.springboot.device.entity.DeviceInfo;
import cn.coderstory.springboot.device.service.DeviceService;
import cn.coderstory.springboot.vo.ApiResponse;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Tag(name = "设备管理", description = "设备信息 CRUD、导入导出")
@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "分页查询设备列表")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> listDevices(
            @RequestParam(required = false) String deviceNo,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<Map<String, Object>> pageParam = new Page<>(page, size);
        IPage<Map<String, Object>> result = deviceService.getDevicePage(pageParam, deviceNo, deviceName, categoryId, status, department);
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        data.put("pages", result.getPages());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "获取设备详情")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceInfo>> getDevice(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(deviceService.getDeviceById(id)));
    }

    @Operation(summary = "新增设备")
    @PostMapping
    public ResponseEntity<ApiResponse<DeviceInfo>> createDevice(@RequestBody DeviceInfo device) {
        return ResponseEntity.ok(ApiResponse.success(deviceService.saveDevice(device)));
    }

    @Operation(summary = "更新设备信息")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceInfo>> updateDevice(@PathVariable Long id, @RequestBody DeviceInfo device) {
        device.setId(id);
        return ResponseEntity.ok(ApiResponse.success(deviceService.updateDevice(device)));
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @Operation(summary = "获取设备分类列表")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<DeviceCategory>>> listCategories() {
        return ResponseEntity.ok(ApiResponse.success(deviceService.listCategories()));
    }

    @Operation(summary = "新增设备分类")
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<DeviceCategory>> createCategory(@RequestBody DeviceCategory category) {
        return ResponseEntity.ok(ApiResponse.success(deviceService.saveCategory(category)));
    }

    @Operation(summary = "删除设备分类")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        deviceService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @Operation(summary = "导出设备列表（Excel）")
    @GetMapping("/export")
    public void exportDevices(
            @RequestParam(required = false) String deviceNo,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String department,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("设备列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        List<Map<String, Object>> data = deviceService.exportDevices(deviceNo, deviceName, categoryId, status, department);
        List<List<Object>> rows = new ArrayList<>();
        for (Map<String, Object> item : data) {
            List<Object> row = new ArrayList<>();
            row.add(item.get("device_no"));
            row.add(item.get("device_name"));
            row.add(item.get("category_name"));
            row.add(item.get("model"));
            row.add(item.get("location"));
            row.add(item.get("department"));
            row.add(item.get("responsible_person"));
            row.add(item.get("status") != null ? getStatusName(((Number) item.get("status")).intValue()) : "");
            rows.add(row);
        }
        List<List<String>> head = Arrays.asList(
                Collections.singletonList("设备编号"),
                Collections.singletonList("设备名称"),
                Collections.singletonList("设备分类"),
                Collections.singletonList("型号"),
                Collections.singletonList("位置"),
                Collections.singletonList("部门"),
                Collections.singletonList("负责人"),
                Collections.singletonList("状态")
        );
        EasyExcel.write(response.getOutputStream()).head(head).sheet("设备列表").doWrite(rows);
    }

    @Operation(summary = "下载导入模板")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("设备导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        List<List<String>> head = Arrays.asList(
                Collections.singletonList("设备编号*"),
                Collections.singletonList("设备名称*"),
                Collections.singletonList("型号"),
                Collections.singletonList("位置"),
                Collections.singletonList("所属部门"),
                Collections.singletonList("负责人")
        );
        List<List<Object>> rows = Collections.singletonList(Arrays.asList("DEV-001", "示例设备", "Model-X", "1楼机房", "技术部", "张三"));
        EasyExcel.write(response.getOutputStream()).head(head).sheet("模板").doWrite(rows);
    }

    @Operation(summary = "批量导入设备")
    @PostMapping("/import")
    public ResponseEntity<ApiResponse<String>> importDevices(@RequestParam("file") MultipartFile file) throws IOException {
        List<Map<Integer, String>> rows = new ArrayList<>();
        EasyExcel.read(file.getInputStream(), new AnalysisEventListener<Map<Integer, String>>() {
            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                rows.add(data);
            }
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {}
        }).sheet().headRowNumber(1).doRead();
        deviceService.importDevices(rows);
        return ResponseEntity.ok(ApiResponse.success("导入成功，共 " + rows.size() + " 条", null));
    }

    private String getStatusName(int status) {
        return switch (status) {
            case 0 -> "停用";
            case 1 -> "运行";
            case 2 -> "维修";
            case 3 -> "报废";
            default -> "未知";
        };
    }
}
