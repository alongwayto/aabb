package cn.coderstory.springboot.device.mapper;

import cn.coderstory.springboot.device.entity.DeviceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeviceInfoMapper extends BaseMapper<DeviceInfo> {
    IPage<Map<String, Object>> selectPageWithCategory(Page<Map<String, Object>> page,
                                                       @Param("deviceNo") String deviceNo,
                                                       @Param("deviceName") String deviceName,
                                                       @Param("categoryId") Long categoryId,
                                                       @Param("status") Integer status,
                                                       @Param("department") String department);

    List<Map<String, Object>> selectAllForExport(@Param("deviceNo") String deviceNo,
                                                  @Param("deviceName") String deviceName,
                                                  @Param("categoryId") Long categoryId,
                                                  @Param("status") Integer status,
                                                  @Param("department") String department);
}
