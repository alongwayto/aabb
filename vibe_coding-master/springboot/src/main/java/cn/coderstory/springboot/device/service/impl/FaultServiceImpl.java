package cn.coderstory.springboot.device.service.impl;

import cn.coderstory.springboot.device.entity.FaultReport;
import cn.coderstory.springboot.device.mapper.FaultReportMapper;
import cn.coderstory.springboot.device.service.FaultService;
import cn.coderstory.springboot.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaultServiceImpl implements FaultService {

    private final FaultReportMapper faultReportMapper;

    @Override
    public IPage<FaultReport> getFaultPage(Page<FaultReport> page, String deviceNo, String faultType,
                                            Integer status, Integer faultLevel) {
        return faultReportMapper.selectPageWithFilter(page, deviceNo, faultType, status, faultLevel);
    }

    @Override
    public FaultReport getFaultById(Long id) {
        FaultReport fault = faultReportMapper.selectById(id);
        if (fault == null) {
            throw BusinessException.notFound("故障记录不存在");
        }
        return fault;
    }

    @Override
    @Transactional
    public FaultReport reportFault(FaultReport fault) {
        fault.setFaultNo("FT" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        fault.setStatus(0);
        fault.setReportTime(LocalDateTime.now());
        faultReportMapper.insert(fault);
        return fault;
    }

    @Override
    @Transactional
    public FaultReport assignFault(Long id, Long assigneeId, String assigneeName) {
        FaultReport fault = getFaultById(id);
        if (fault.getStatus() != 0) {
            throw BusinessException.badRequest("只能派单给待处理的故障");
        }
        fault.setAssigneeId(assigneeId);
        fault.setAssigneeName(assigneeName);
        fault.setAssignTime(LocalDateTime.now());
        fault.setStatus(1);
        faultReportMapper.updateById(fault);
        return fault;
    }

    @Override
    @Transactional
    public FaultReport handleFault(Long id, Long handlerId, String handlerName, String handleDesc) {
        FaultReport fault = getFaultById(id);
        fault.setHandlerId(handlerId);
        fault.setHandlerName(handlerName);
        fault.setHandleDesc(handleDesc);
        fault.setHandleTime(LocalDateTime.now());
        fault.setStatus(2);
        faultReportMapper.updateById(fault);
        return fault;
    }

    @Override
    @Transactional
    public FaultReport resolveFault(Long id, String resolveDesc, BigDecimal cost) {
        FaultReport fault = getFaultById(id);
        fault.setHandleDesc(resolveDesc);
        fault.setResolveTime(LocalDateTime.now());
        fault.setCost(cost);
        fault.setStatus(3);
        faultReportMapper.updateById(fault);
        return fault;
    }

    @Override
    @Transactional
    public FaultReport closeFault(Long id) {
        FaultReport fault = getFaultById(id);
        fault.setStatus(4);
        faultReportMapper.updateById(fault);
        return fault;
    }

    @Override
    public List<Map<String, Object>> getFaultTypeStat() {
        return faultReportMapper.selectFaultTypeStat();
    }

    @Override
    public List<Map<String, Object>> getFaultTrend(int days) {
        return faultReportMapper.selectFaultTrend(days);
    }
}
