package cn.coderstory.springboot.device.mapper;

import cn.coderstory.springboot.device.entity.FaultReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FaultReportMapper extends BaseMapper<FaultReport> {
    IPage<FaultReport> selectPageWithFilter(Page<FaultReport> page,
                                             @Param("deviceNo") String deviceNo,
                                             @Param("faultType") String faultType,
                                             @Param("status") Integer status,
                                             @Param("faultLevel") Integer faultLevel);

    List<Map<String, Object>> selectFaultTypeStat();

    List<Map<String, Object>> selectFaultTrend(@Param("days") int days);
}
