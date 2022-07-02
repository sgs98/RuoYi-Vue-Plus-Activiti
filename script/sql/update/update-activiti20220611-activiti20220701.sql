INSERT INTO `sys_menu` VALUES (1538090311987884033, '报表管理', 0, 9, 'report', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'chart', 'admin', '2022-06-18 17:24:41', 'admin', '2022-06-18 17:24:41', '');
INSERT INTO `sys_menu` VALUES (1538090530318184449, '设计报表', 1538090311987884033, 3, 'http://localhost:8080/jmreport/list', NULL, NULL, 1, 0, 'M', '0', '0', NULL, '#', 'admin', '2022-06-18 17:25:33', 'admin', '2022-06-18 17:25:33', '');
INSERT INTO `sys_menu` VALUES (1538094466311778306, '系统日志', 1538090311987884033, 2, 'http://localhost:8080/jmreport/view/698070126881910784', NULL, NULL, 1, 0, 'M', '0', '0', NULL, '#', 'admin', '2022-06-18 17:41:12', 'admin', '2022-06-18 17:41:12', '');

-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values(1537763506351398912, '消息通知', '2000', '1', 'message', 'workflow/message/index', 1, 0, 'C', '0', '0', 'workflow:message:list', '#', 'admin', sysdate(), '', null, '消息通知菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values(1537763506351398913, '消息通知查询', 1537763506351398912, '1',  '#', '', 1, 0, 'F', '0', '0', 'workflow:message:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values(1537763506351398914, '消息通知新增', 1537763506351398912, '2',  '#', '', 1, 0, 'F', '0', '0', 'workflow:message:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values(1537763506351398915, '消息通知修改', 1537763506351398912, '3',  '#', '', 1, 0, 'F', '0', '0', 'workflow:message:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values(1537763506351398916, '消息通知删除', 1537763506351398912, '4',  '#', '', 1, 0, 'F', '0', '0', 'workflow:message:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values(1537763506351398917, '消息通知导出', 1537763506351398912, '5',  '#', '', 1, 0, 'F', '0', '0', 'workflow:message:export',       '#', 'admin', sysdate(), '', null, '');

DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` bigint(19) UNSIGNED NOT NULL COMMENT '主键',
  `send_id` bigint(19) NOT NULL COMMENT '消息发送者id',
  `record_id` bigint(20) NOT NULL COMMENT '消息接收者id',
  `type` bigint(10) NOT NULL COMMENT '消息类型1：站内信，2：邮件，3：短信',
  `status` tinyint(1) NOT NULL COMMENT '阅读状态0：未读，1：已读',
  `message_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息内容',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人',
  `read_time` datetime(0) NULL DEFAULT NULL COMMENT '阅读时间',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息通知' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `sys_dict_type` VALUES (1537766261421912066, '消息类型', 'sys_message', '0', 'admin', '2022-06-17 19:57:02', 'admin', '2022-06-17 19:57:02', '消息类型列表');
INSERT INTO `sys_dict_type` VALUES (1537799829238005761, '站内信阅读状态', 'read_status', '0', 'admin', '2022-06-17 22:10:25', 'admin', '2022-06-17 22:10:36', '站内信阅读状态列表');
INSERT INTO `sys_dict_data` VALUES (1537766488677691394, 0, '站内信', '1', 'sys_message', NULL, 'primary', 'N', '0', 'admin', '2022-06-17 19:57:56', 'admin', '2022-06-17 19:57:56', NULL);
INSERT INTO `sys_dict_data` VALUES (1537766555945938946, 1, '邮件', '2', 'sys_message', NULL, 'success', 'N', '0', 'admin', '2022-06-17 19:58:12', 'admin', '2022-06-17 19:58:12', NULL);
INSERT INTO `sys_dict_data` VALUES (1537766634446532610, 2, '短信', '3', 'sys_message', NULL, 'info', 'N', '0', 'admin', '2022-06-17 19:58:31', 'admin', '2022-06-17 22:26:50', NULL);
INSERT INTO `sys_dict_data` VALUES (1537800002466955266, 0, '已读', '1', 'read_status', NULL, 'primary', 'N', '0', 'admin', '2022-06-17 22:11:06', 'admin', '2022-06-18 17:54:47', NULL);
INSERT INTO `sys_dict_data` VALUES (1537800099497984001, 1, '未读', '0', 'read_status', NULL, 'danger', 'N', '0', 'admin', '2022-06-17 22:11:29', 'admin', '2022-06-18 17:54:53', NULL);

alter table act_node_assignee add task_listener VARCHAR(4000)  comment '任务监听';
