package cn.coderstory.springboot.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("device_info")
public class DeviceInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceNo;
    private String deviceName;
    private Long categoryId;
    private String model;
    private String serialNo;
    private String manufacturer;
    private String location;
    private String department;
    private String responsiblePerson;
    private String responsiblePhone;
    /** 状态: 0=停用,1=运行,2=维修,3=报废 */
    private Integer status;
    /** 在线状态: 0=离线,1=在线 */
    private Integer onlineStatus;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpire;
    private BigDecimal purchasePrice;
    private String tags;
    private String remark;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
