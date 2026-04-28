package cn.coderstory.springboot.device.mapper;

import cn.coderstory.springboot.device.entity.DeviceOperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceOperationLogMapper extends BaseMapper<DeviceOperationLog> {
}
