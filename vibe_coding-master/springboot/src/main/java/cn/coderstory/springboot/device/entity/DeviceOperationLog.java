package cn.coderstory.springboot.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("device_operation_log")
public class DeviceOperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String operationType;
    private String targetType;
    private Long targetId;
    private String targetName;
    private Long operatorId;
    private String operatorName;
    private String operationDesc;
    private String ip;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
