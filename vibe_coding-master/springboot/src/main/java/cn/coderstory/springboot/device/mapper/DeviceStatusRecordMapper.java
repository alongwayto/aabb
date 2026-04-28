package cn.coderstory.springboot.device.mapper;

import cn.coderstory.springboot.device.entity.DeviceStatusRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface DeviceStatusRecordMapper extends BaseMapper<DeviceStatusRecord> {
    List<Map<String, Object>> selectOnlineRateTrend(@Param("deviceId") Long deviceId,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    List<Map<String, Object>> selectDeviceStatusSummary();
}
