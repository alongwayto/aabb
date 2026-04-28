package cn.coderstory.springboot.device.service;

import cn.coderstory.springboot.device.entity.DeviceCategory;
import cn.coderstory.springboot.device.entity.DeviceInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

public interface DeviceService {
    IPage<Map<String, Object>> getDevicePage(Page<Map<String, Object>> page, String deviceNo, String deviceName,
                                              Long categoryId, Integer status, String department);
    DeviceInfo getDeviceById(Long id);
    DeviceInfo saveDevice(DeviceInfo device);
    DeviceInfo updateDevice(DeviceInfo device);
    void deleteDevice(Long id);
    List<DeviceCategory> listCategories();
    DeviceCategory saveCategory(DeviceCategory category);
    void deleteCategory(Long id);
    List<Map<String, Object>> exportDevices(String deviceNo, String deviceName, Long categoryId, Integer status, String department);
    void importDevices(List<Map<Integer, String>> rows);
}
