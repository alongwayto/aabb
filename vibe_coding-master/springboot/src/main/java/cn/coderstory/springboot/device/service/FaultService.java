package cn.coderstory.springboot.device.service;

import cn.coderstory.springboot.device.entity.FaultReport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

public interface FaultService {
    IPage<FaultReport> getFaultPage(Page<FaultReport> page, String deviceNo, String faultType,
                                     Integer status, Integer faultLevel);
    FaultReport getFaultById(Long id);
    FaultReport reportFault(FaultReport fault);
    FaultReport assignFault(Long id, Long assigneeId, String assigneeName);
    FaultReport handleFault(Long id, Long handlerId, String handlerName, String handleDesc);
    FaultReport resolveFault(Long id, String resolveDesc, java.math.BigDecimal cost);
    FaultReport closeFault(Long id);
    List<Map<String, Object>> getFaultTypeStat();
    List<Map<String, Object>> getFaultTrend(int days);
}
