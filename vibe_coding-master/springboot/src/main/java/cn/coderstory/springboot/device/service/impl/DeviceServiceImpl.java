package cn.coderstory.springboot.device.service.impl;

import cn.coderstory.springboot.device.entity.DeviceCategory;
import cn.coderstory.springboot.device.entity.DeviceInfo;
import cn.coderstory.springboot.device.mapper.DeviceCategoryMapper;
import cn.coderstory.springboot.device.mapper.DeviceInfoMapper;
import cn.coderstory.springboot.device.service.DeviceService;
import cn.coderstory.springboot.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceInfoMapper deviceInfoMapper;
    private final DeviceCategoryMapper deviceCategoryMapper;

    @Override
    public IPage<Map<String, Object>> getDevicePage(Page<Map<String, Object>> page, String deviceNo, String deviceName,
                                                     Long categoryId, Integer status, String department) {
        return deviceInfoMapper.selectPageWithCategory(page, deviceNo, deviceName, categoryId, status, department);
    }

    @Override
    public DeviceInfo getDeviceById(Long id) {
        DeviceInfo device = deviceInfoMapper.selectById(id);
        if (device == null) {
            throw BusinessException.notFound("设备不存在");
        }
        return device;
    }

    @Override
    @Transactional
    public DeviceInfo saveDevice(DeviceInfo device) {
        if (device.getDeviceNo() == null || device.getDeviceNo().isEmpty()) {
            throw BusinessException.badRequest("设备编号不能为空");
        }
        LambdaQueryWrapper<DeviceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceInfo::getDeviceNo, device.getDeviceNo());
        if (deviceInfoMapper.selectCount(wrapper) > 0) {
            throw BusinessException.conflict("设备编号已存在");
        }
        deviceInfoMapper.insert(device);
        return device;
    }

    @Override
    @Transactional
    public DeviceInfo updateDevice(DeviceInfo device) {
        if (device.getId() == null) {
            throw BusinessException.badRequest("设备ID不能为空");
        }
        DeviceInfo existing = deviceInfoMapper.selectById(device.getId());
        if (existing == null) {
            throw BusinessException.notFound("设备不存在");
        }
        deviceInfoMapper.updateById(device);
        return device;
    }

    @Override
    @Transactional
    public void deleteDevice(Long id) {
        DeviceInfo device = deviceInfoMapper.selectById(id);
        if (device == null) {
            throw BusinessException.notFound("设备不存在");
        }
        deviceInfoMapper.deleteById(id);
    }

    @Override
    public List<DeviceCategory> listCategories() {
        LambdaQueryWrapper<DeviceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DeviceCategory::getSortOrder);
        return deviceCategoryMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public DeviceCategory saveCategory(DeviceCategory category) {
        deviceCategoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        deviceCategoryMapper.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> exportDevices(String deviceNo, String deviceName, Long categoryId, Integer status, String department) {
        return deviceInfoMapper.selectAllForExport(deviceNo, deviceName, categoryId, status, department);
    }

    @Override
    @Transactional
    public void importDevices(List<Map<Integer, String>> rows) {
        for (Map<Integer, String> row : rows) {
            DeviceInfo device = new DeviceInfo();
            device.setDeviceNo(row.getOrDefault(0, "DEV-" + UUID.randomUUID().toString().substring(0, 8)));
            device.setDeviceName(row.getOrDefault(1, ""));
            device.setModel(row.getOrDefault(2, null));
            device.setLocation(row.getOrDefault(3, null));
            device.setDepartment(row.getOrDefault(4, null));
            device.setResponsiblePerson(row.getOrDefault(5, null));
            device.setStatus(0);
            device.setOnlineStatus(0);
            try {
                LambdaQueryWrapper<DeviceInfo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DeviceInfo::getDeviceNo, device.getDeviceNo());
                if (deviceInfoMapper.selectCount(wrapper) == 0) {
                    deviceInfoMapper.insert(device);
                }
            } catch (Exception e) {
                log.warn("导入设备失败: {}", device.getDeviceNo(), e);
            }
        }
    }
}
