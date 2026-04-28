-- 插入设备管理系统菜单
INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `icon`, `sort_order`) VALUES
(0, '设备管理', '/device', 'Monitor', 10),
(0, '状态监控', '/monitor/device', 'DataAnalysis', 11),
(0, '故障管理', '/fault', 'Warning', 12),
(0, '数据分析', '/analysis', 'TrendCharts', 13);
