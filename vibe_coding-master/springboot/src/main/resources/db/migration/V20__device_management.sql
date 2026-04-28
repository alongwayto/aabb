-- 设备分类表
CREATE TABLE IF NOT EXISTS `device_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `code` varchar(50) NOT NULL COMMENT '分类编码',
  `parent_id` bigint DEFAULT 0 COMMENT '父分类ID',
  `description` varchar(500) DEFAULT NULL COMMENT '分类描述',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `deleted` tinyint DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备分类';

-- 设备信息表
CREATE TABLE IF NOT EXISTS `device_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `device_no` varchar(50) NOT NULL COMMENT '设备编号',
  `device_name` varchar(200) NOT NULL COMMENT '设备名称',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `serial_no` varchar(100) DEFAULT NULL COMMENT '序列号',
  `manufacturer` varchar(200) DEFAULT NULL COMMENT '生产厂商',
  `location` varchar(500) DEFAULT NULL COMMENT '位置',
  `department` varchar(200) DEFAULT NULL COMMENT '所属部门',
  `responsible_person` varchar(100) DEFAULT NULL COMMENT '负责人',
  `responsible_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0=停用,1=运行,2=维修,3=报废',
  `online_status` tinyint DEFAULT 0 COMMENT '在线状态: 0=离线,1=在线',
  `purchase_date` date DEFAULT NULL COMMENT '购入日期',
  `warranty_expire` date DEFAULT NULL COMMENT '保修到期日',
  `purchase_price` decimal(12,2) DEFAULT NULL COMMENT '购置金额',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_no` (`device_no`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_department` (`department`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息';

-- 设备状态记录表（运行参数）
CREATE TABLE IF NOT EXISTS `device_status_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `device_no` varchar(50) NOT NULL COMMENT '设备编号',
  `online_status` tinyint DEFAULT 0 COMMENT '在线状态: 0=离线,1=在线',
  `run_status` tinyint DEFAULT 0 COMMENT '运行状态: 0=停止,1=运行,2=故障',
  `temperature` decimal(8,2) DEFAULT NULL COMMENT '温度',
  `cpu_usage` decimal(5,2) DEFAULT NULL COMMENT 'CPU使用率',
  `memory_usage` decimal(5,2) DEFAULT NULL COMMENT '内存使用率',
  `params` json DEFAULT NULL COMMENT '其他运行参数(JSON)',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_record_time` (`record_time`),
  KEY `idx_device_no` (`device_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备状态记录';

-- 故障报告表
CREATE TABLE IF NOT EXISTS `fault_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fault_no` varchar(50) NOT NULL COMMENT '故障单号',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `device_no` varchar(50) NOT NULL COMMENT '设备编号',
  `device_name` varchar(200) DEFAULT NULL COMMENT '设备名称',
  `fault_type` varchar(100) DEFAULT NULL COMMENT '故障类型',
  `fault_level` tinyint DEFAULT 1 COMMENT '故障级别: 1=低,2=中,3=高,4=紧急',
  `fault_desc` varchar(2000) DEFAULT NULL COMMENT '故障描述',
  `reporter_id` bigint DEFAULT NULL COMMENT '上报人ID',
  `reporter_name` varchar(100) DEFAULT NULL COMMENT '上报人姓名',
  `report_time` datetime DEFAULT NULL COMMENT '上报时间',
  `assignee_id` bigint DEFAULT NULL COMMENT '指派人ID',
  `assignee_name` varchar(100) DEFAULT NULL COMMENT '指派人姓名',
  `assign_time` datetime DEFAULT NULL COMMENT '指派时间',
  `handler_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `handler_name` varchar(100) DEFAULT NULL COMMENT '处理人姓名',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_desc` varchar(2000) DEFAULT NULL COMMENT '处理描述',
  `resolve_time` datetime DEFAULT NULL COMMENT '解决时间',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0=待处理,1=已派单,2=处理中,3=已解决,4=已关闭',
  `cost` decimal(10,2) DEFAULT NULL COMMENT '维修费用',
  `deleted` tinyint DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fault_no` (`fault_no`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_status` (`status`),
  KEY `idx_report_time` (`report_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故障报告';

-- 操作日志扩展表（设备操作）
CREATE TABLE IF NOT EXISTS `device_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型',
  `target_type` varchar(50) NOT NULL COMMENT '目标类型: DEVICE/FAULT',
  `target_id` bigint DEFAULT NULL COMMENT '目标ID',
  `target_name` varchar(200) DEFAULT NULL COMMENT '目标名称',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) DEFAULT NULL COMMENT '操作人姓名',
  `operation_desc` varchar(1000) DEFAULT NULL COMMENT '操作描述',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备操作日志';
