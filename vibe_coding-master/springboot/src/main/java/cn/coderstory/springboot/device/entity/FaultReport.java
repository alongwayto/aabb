package cn.coderstory.springboot.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fault_report")
public class FaultReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String faultNo;
    private Long deviceId;
    private String deviceNo;
    private String deviceName;
    private String faultType;
    /** 故障级别: 1=低,2=中,3=高,4=紧急 */
    private Integer faultLevel;
    private String faultDesc;
    private Long reporterId;
    private String reporterName;
    private LocalDateTime reportTime;
    private Long assigneeId;
    private String assigneeName;
    private LocalDateTime assignTime;
    private Long handlerId;
    private String handlerName;
    private LocalDateTime handleTime;
    private String handleDesc;
    private LocalDateTime resolveTime;
    /** 状态: 0=待处理,1=已派单,2=处理中,3=已解决,4=已关闭 */
    private Integer status;
    private BigDecimal cost;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
