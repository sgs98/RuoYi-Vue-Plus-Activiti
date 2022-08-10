CREATE TABLE `act_business_rule` (
 `id` BIGINT(20) NOT NULL COMMENT 'id',
 `bean_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '全类名',
 `method` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '方法名',
 `param` VARCHAR(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '参数',
 `remark` VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
 `create_time` DATETIME NOT NULL COMMENT '创建时间',
 `update_time` DATETIME NOT NULL COMMENT '更新时间',
 `create_by` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
 `update_by` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人',
 PRIMARY KEY (`id`) USING BTREE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='业务规则';
INSERT INTO `act_business_rule` (`id`, `bean_name`, `method`, `param`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`) VALUES(1471758168763731969,'workflowRuleUserComponent','queryUserById','[{\"paramType\":\"Long\",\"param\":\"userId\",\"remark\":\"用户id\",\"orderNo\":1}]','按id查询人员','2021-12-17 16:24:26','2022-07-15 21:54:29','admin','admin');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES(1543513641015955457,'工作流程分类','act_category','0','admin','2022-07-03 16:35:04','admin','2022-07-03 16:35:04','工作流程分类');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES(1543513861028171778,'0','OA','oa','act_category','','primary','N','0','admin','2022-07-03 16:35:56','admin','2022-07-03 17:15:54',NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES(1543513955815247873,'1','财务','finance','act_category','','primary','N','0','admin','2022-07-03 16:36:19','admin','2022-07-03 16:36:19',NULL);

ALTER TABLE act_node_assignee ADD auto_complete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '自动审批,0不自动办理,1自动办理,当前节点自动办理';

alter table act_node_assignee rename column full_class_id to business_rule_id;

DROP TABLE IF EXISTS `sys_report_view`;
CREATE TABLE `sys_report_view`  (
                                    `id` bigint(20) NOT NULL COMMENT 'id',
                                    `report_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '报表id',
                                    `report_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '报表名称',
                                    `create_time` datetime(0) NOT NULL COMMENT '创建时间',
                                    `update_time` datetime(0) NOT NULL COMMENT '修改时间',
                                    `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
                                    `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人',
                                    `order_no` int(10) NULL DEFAULT NULL COMMENT '排序',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '报表查看' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_report_view
-- ----------------------------
INSERT INTO `sys_report_view` VALUES (1556998684639973378, '716974933112426496', '操作日志', '2022-08-09 21:39:49', '2022-08-09 21:40:51', 'admin', 'admin', 1);


delete from sys_menu where menu_id in (1538090530318184449,1556235156136067072,1556235156136067073,1556235156136067074,1556235156136067075,1556235156136067076,1556235156136067077);

INSERT INTO `ruoyi-activiti6`.`sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1538090530318184449, '设计报表', 1538090311987884033, 1, 'jmreport', 'jmreport/index', NULL, 1, 0, 'C', '0', '0', NULL, '#', 'admin', '2022-06-18 17:25:33', 'admin', '2022-08-06 19:42:27', '');
INSERT INTO `ruoyi-activiti6`.`sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1556235156136067072, '报表查看', 1538090311987884033, 2, 'reportView', 'jmreport/view', NULL, 1, 0, 'C', '0', '0', 'report:reportView:list', '#', 'admin', '2022-08-07 19:53:34', 'admin', '2022-08-07 19:55:51', '报表查看菜单');
INSERT INTO `ruoyi-activiti6`.`sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1556235156136067073, '报表查看查询', 1556235156136067072, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'report:reportView:query', '#', 'admin', '2022-08-07 19:53:34', '', NULL, '');
INSERT INTO `ruoyi-activiti6`.`sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1556235156136067074, '报表查看新增', 1556235156136067072, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'report:reportView:add', '#', 'admin', '2022-08-07 19:53:34', '', NULL, '');
INSERT INTO `ruoyi-activiti6`.`sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1556235156136067075, '报表查看修改', 1556235156136067072, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'report:reportView:edit', '#', 'admin', '2022-08-07 19:53:34', '', NULL, '');
INSERT INTO `ruoyi-activiti6`.`sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1556235156136067076, '报表查看删除', 1556235156136067072, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'report:reportView:remove', '#', 'admin', '2022-08-07 19:53:34', '', NULL, '');
INSERT INTO `ruoyi-activiti6`.`sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1556235156136067077, '报表查看导出', 1556235156136067072, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'report:reportView:export', '#', 'admin', '2022-08-07 19:53:34', '', NULL, '');
