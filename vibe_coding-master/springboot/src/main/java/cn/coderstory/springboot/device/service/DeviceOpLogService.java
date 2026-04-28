package cn.coderstory.springboot.device.service;

import cn.coderstory.springboot.device.entity.DeviceOperationLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface DeviceOpLogService {
    void log(String operationType, String targetType, Long targetId, String targetName,
             Long operatorId, String operatorName, String operationDesc, String ip);
    IPage<DeviceOperationLog> getLogs(Page<DeviceOperationLog> page, String targetType, Long targetId);
}
