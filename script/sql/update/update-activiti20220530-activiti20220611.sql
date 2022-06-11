update act_node_assignee set is_back=0 where is_back is null;
update act_node_assignee set multiple=0 where multiple is null;

alter table act_business_status add create_by VARCHAR(255) not null comment '创建人';
alter table act_business_status add update_by VARCHAR(255) not null comment '更新人';
update act_business_status set create_by = 'admin' ,update_by = 'admin';

update act_business_status set create_time = SYSDATE() ,update_time = SYSDATE();
alter table act_business_status modify create_time datetime not null comment '创建时间';
alter table act_business_status modify update_time datetime not null comment '更新时间';


alter table act_task_node add create_by VARCHAR(255) not null comment '创建人';
alter table act_task_node add update_by VARCHAR(255) not null comment '更新人';
update act_task_node set create_by = 'admin' ,update_by = 'admin';

update act_task_node set create_time = SYSDATE() ,update_time = SYSDATE();
alter table act_task_node modify create_time datetime not null comment '创建时间';
alter table act_task_node modify update_time datetime not null comment '更新时间';



alter table act_node_assignee modify is_back bigint(1) not null default 0 comment '是否可退回,0不可退回,1可退回';
alter table act_node_assignee modify is_show bigint(1) not null default 1 comment '是否弹窗选人,0不弹,1弹窗';
alter table act_node_assignee modify multiple bigint(1) not null default 0 comment '是否会签,0非会签,1会签';

alter table act_node_assignee add is_delegate bigint(1) not null default 0 comment '是否可委托,0不可委托,1可委托';
alter table act_node_assignee add is_transmit bigint(1) not null default 0 comment '是否可转办,0不可转办,1可转办';
alter table act_node_assignee add is_copy bigint(1) not null default 0 comment '是否可抄送,0不可抄送,1可抄送';

alter table act_node_assignee add add_multi_instance bigint(1) not null  comment '是否可加签,0不可加签,1可加签';
alter table act_node_assignee add delete_multi_instance bigint(1) not null comment '是否可减签,0不可减签,1可减签';

alter table act_business_status add suspended_reason VARCHAR(1000)  comment '挂起或激活原因';

commit;






