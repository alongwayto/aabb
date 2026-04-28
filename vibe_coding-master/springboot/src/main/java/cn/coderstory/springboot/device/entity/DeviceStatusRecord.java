package cn.coderstory.springboot.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("device_status_record")
public class DeviceStatusRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long deviceId;
    private String deviceNo;
    /** 在线状态: 0=离线,1=在线 */
    private Integer onlineStatus;
    /** 运行状态: 0=停止,1=运行,2=故障 */
    private Integer runStatus;
    private BigDecimal temperature;
    private BigDecimal cpuUsage;
    private BigDecimal memoryUsage;
    /** 其他运行参数JSON */
    private String params;
    private LocalDateTime recordTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
