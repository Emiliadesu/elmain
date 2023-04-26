
/**
 业务表
 */

/**
供应商表
 */
CREATE TABLE `bus_supplier_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `supplier_name` varchar(64) NOT NULL COMMENT '供应商名称',
  `nick_name` varchar(64) NOT NULL COMMENT '供应商简称',
  `supplier_type` varchar(10) NOT NULL COMMENT '供应商类别',
  `contacts` varchar(64) DEFAULT NULL COMMENT '联系人',
  `telphone` varchar(64) DEFAULT NULL COMMENT '联系电话',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='供应商信息';
ALTER TABLE `bus_supplier_info` ADD COLUMN `code` varchar(32) NOT NULL COMMENT '代码' after supplier_type;
/**
2021-3-27 18点更新
 */
ALTER TABLE `bus_supplier_info` ADD COLUMN `code` varchar(32) NOT NULL COMMENT '代码' after `supplier_type`;




/**
港到仓清关管理表
 */
DROP TABLE IF EXISTS  `bus_clear_info`;
CREATE TABLE `bus_clear_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `clear_no` varchar(64) NOT NULL COMMENT '单据编号',
  `status` varchar(32) NOT NULL COMMENT '状态',
  `customers_id` bigint(20) NOT NULL COMMENT '客户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `clear_company_id` bigint(20) NOT NULL COMMENT '清关抬头_ID',
  `supplier_id` bigint(20) NOT NULL COMMENT '报关行ID',
  `bus_type` varchar(10) NOT NULL COMMENT '业务类型',
  `declare_mode` varchar(10) NOT NULL COMMENT '申报模式',
  `bill_no` varchar(64) NOT NULL COMMENT '提单号',
  `entry_no` varchar(64) DEFAULT NULL COMMENT '报关单号',
  `decl_no` varchar(64) DEFAULT NULL COMMENT '报检单号',
  `trans_way` varchar(10) NOT NULL COMMENT '运输方式',
  `ship_country` varchar(10) NOT NULL COMMENT '启运国',
  `in_port` varchar(10) NOT NULL COMMENT '入境口岸',
  `qd_code` varchar(64) DEFAULT NULL COMMENT 'QD单号',
  `sku_num` int(11) NOT NULL COMMENT '预估SKU数量',
  `total_num` int(11) NOT NULL COMMENT '预估件数',
  `groos_weight` decimal(18,2) NOT NULL COMMENT '毛重',
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `pruduct` varchar(64) NOT NULL COMMENT '主要产品',
  `in_warehose` varchar(64) DEFAULT NULL COMMENT '入库仓',
  `container` varchar(64) DEFAULT NULL COMMENT '箱型',
  `clear_data_link` varchar(128) DEFAULT NULL COMMENT '清关资料链接',
  `draft_declare_data_link` varchar(128) DEFAULT NULL COMMENT '概报放行单链接',
  `entry_data_link` varchar(128) DEFAULT NULL COMMENT '报关报检单链接',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `expect_arrival_time` datetime DEFAULT NULL COMMENT '预估到港日期',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `clear_no` (`clear_no`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='清关管理';
ALTER TABLE `bus_clear_info` ADD COLUMN `ref_order_no` varchar(128) DEFAULT NULL COMMENT '关联单据号' after bus_type;
ALTER TABLE `bus_clear_info` ADD COLUMN `ref_order_type` int(11) DEFAULT NULL COMMENT '关联单据类型' after ref_order_no;
ALTER TABLE `bus_clear_info` ADD COLUMN `contract_no` varchar(64) DEFAULT NULL COMMENT '合同号' after clear_no;
ALTER TABLE `bus_clear_info` ADD COLUMN `ref_qd_code` varchar(128) DEFAULT NULL COMMENT '关联单证编码' after ref_order_no;
ALTER TABLE `bus_clear_info` ADD COLUMN `trade_type` varchar(10) DEFAULT NULL COMMENT '贸易类型' after bus_type;
ALTER TABLE `bus_clear_info` ADD COLUMN `sum_money` decimal(18,4) DEFAULT NULL COMMENT '总金额' after status;

ALTER TABLE `bus_clear_info` ADD COLUMN `in_process_bl` varchar(128) DEFAULT NULL COMMENT '头程提单号' after container;
ALTER TABLE `bus_clear_info` ADD COLUMN `switched_bl` varchar(128) DEFAULT NULL COMMENT '二程提单号' after in_process_bl;
ALTER TABLE `bus_clear_info` ADD COLUMN `books_no` varchar(64) DEFAULT NULL COMMENT '账册编号' after switched_bl;
ALTER TABLE `bus_clear_info` ADD COLUMN `ref_enterprise_code` varchar(64) DEFAULT NULL COMMENT '转关企业编码' after books_no;
ALTER TABLE `bus_clear_info` ADD COLUMN `ref_books_no` varchar(64) DEFAULT NULL COMMENT '关联账册编号' after books_no;
ALTER TABLE `bus_clear_info` ADD COLUMN `trade_country` varchar(64) DEFAULT NULL COMMENT '贸易国' after ref_books_no;
ALTER TABLE `bus_clear_info` ADD COLUMN `ship_port` varchar(64) DEFAULT NULL COMMENT '启运港' after trade_country;
ALTER TABLE `bus_clear_info` ADD COLUMN `order_source` varchar(10) DEFAULT NULL COMMENT '单据来源' after trade_country;

ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN declare_mode varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '申报模式';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN contract_no varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '合同号';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN bill_no varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '提单号';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN groos_weight decimal(6,3) NULL COMMENT '毛重';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN currency varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '币种';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN sku_num int(11) NULL COMMENT '预估SKU数量';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN total_num int(11) NULL COMMENT '预估件数';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN pruduct varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '主要产品';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN in_port varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '入境口岸';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN trans_way varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '运输方式';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN ship_country varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '启运国';
ALTER TABLE eladmin.bus_clear_info MODIFY COLUMN supplier_id bigint(20) NULL COMMENT '报关行ID';

ALTER TABLE `bus_clear_details` ADD COLUMN `outer_goods_no` varchar(64) DEFAULT NULL COMMENT '外部货号' after `goods_no`;

/**
清关明细表
 */
DROP TABLE IF EXISTS  `bus_clear_details`;
CREATE TABLE `bus_clear_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `clear_id` bigint(20)  NOT NULL COMMENT '清关ID',
  `clear_no` varchar(64) NOT NULL COMMENT '单据编号',
  `seq_no` int(11) DEFAULT NULL COMMENT '序号',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '货品ID',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `hs_code` varchar(64) DEFAULT NULL COMMENT 'HS编码',
  `goods_name` varchar(1024) DEFAULT NULL COMMENT '商品名称',
  `net_weight` varchar(64) DEFAULT NULL COMMENT '净重（千克）',
  `gross_weight` varchar(64) DEFAULT NULL COMMENT '毛重（千克）',
  `legal_unit` varchar(64) DEFAULT NULL COMMENT '法一单位',
  `legal_unit_code` varchar(64) DEFAULT NULL COMMENT '法一单位代码',
  `legal_num` varchar(64) DEFAULT NULL COMMENT '法一数量',
  `second_unit` varchar(64) DEFAULT NULL COMMENT '法二单位',
  `second_unit_code` varchar(64) DEFAULT NULL COMMENT '法二单位代码',
  `second_num` varchar(64) DEFAULT NULL COMMENT '法二数量',
  `qty` varchar(10) DEFAULT NULL COMMENT '数量',
  `unit` varchar(10) DEFAULT NULL COMMENT '计量单位',
  `price` varchar(64) DEFAULT NULL COMMENT '商品单价',
  `total_price` varchar(64) DEFAULT NULL COMMENT '商品总价',
  `property` varchar(64) DEFAULT NULL COMMENT '规格型号',
  `currency` varchar(64) DEFAULT NULL COMMENT '币种',
  `make_country` varchar(64) DEFAULT NULL COMMENT '原产国',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3940 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='清关明细';


/**
港到仓清关箱型明细表
 */
DROP TABLE IF EXISTS  `bus_clear_container`;
CREATE TABLE `bus_clear_container` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `clear_id` bigint(20)  NOT NULL COMMENT '清关ID',
  `container_no` varchar(64) NOT NULL COMMENT '箱号',
  `container_type` varchar(32) NOT NULL COMMENT '箱型',
  `pack_way` varchar(32) NOT NULL COMMENT '打包方式',
  `pack_num` integer NOT NULL COMMENT '打包数量',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='清关箱型信息';
ALTER TABLE `bus_clear_container` ADD COLUMN `plate_no` varchar(64) NOT NULL COMMENT '车牌' after container_no;
ALTER TABLE `bus_clear_container` ADD COLUMN `share_flag` varchar(10) NOT NULL COMMENT '是否拼车' after plate_no;

ALTER TABLE `bus_clear_container` ADD COLUMN `car_type` varchar(64) NOT NULL COMMENT '车型' after clear_id;
ALTER TABLE `bus_clear_container` ADD COLUMN `plan_car_type` varchar(10) NOT NULL COMMENT '排车方' after car_type;


/**
港到仓操作节点表
 */
DROP TABLE IF EXISTS  `bus_clear_opt_log`;
CREATE TABLE `bus_clear_opt_log` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `clear_id` bigint(20)  NOT NULL COMMENT '清关ID',
  `opt_node` varchar(32) NOT NULL COMMENT '操作节点',
  `opt_time` datetime NOT NULL COMMENT '操作时间',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='清关操作日志';

ALTER TABLE `bus_clear_opt_log` ADD COLUMN `req_msg` text DEFAULT NULL COMMENT '请求报文' after remark;
ALTER TABLE `bus_clear_opt_log` ADD COLUMN `res_msg` text DEFAULT NULL COMMENT '返回报文' after req_msg;


/**
跨境订单表
 */
DROP TABLE IF EXISTS  `bus_cross_border_order`;
CREATE TABLE `bus_cross_border_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `status` int(11) NOT NULL COMMENT '状态',
  `up_status` varchar(64) DEFAULT NULL COMMENT '上游订单状态',
  `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `shop_id` varchar(128) DEFAULT NULL COMMENT '店铺ID',
  `platform_shop_id` varchar(64) DEFAULT NULL COMMENT '平台店铺ID',
  `clear_company_id` bigint(20) DEFAULT NULL COMMENT '清关抬头ID',
  `platform_code` varchar(64) DEFAULT NULL COMMENT '电商平台代码',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '承运商ID',
  `cross_border_no` varchar(64) DEFAULT NULL COMMENT '交易号',
  `ebp_code` varchar(128) DEFAULT NULL COMMENT '电商平台代码',
  `ebp_name` varchar(128) DEFAULT NULL COMMENT '电商平台名称',
  `order_form` varchar(128) DEFAULT NULL COMMENT '平台跨境购编码',
  `declare_no` varchar(64) DEFAULT NULL COMMENT '申报单号',
  `declare_status` varchar(32) DEFAULT NULL COMMENT '清关状态',
  `declare_msg` varchar(1024) DEFAULT NULL COMMENT '清关信息',
  `invt_no` varchar(64) DEFAULT NULL COMMENT '总署清单编号',
  `order_create_time` datetime DEFAULT NULL COMMENT '订单创建时间',
  `err_msg` varchar(1024) DEFAULT NULL COMMENT '异常信息',
  `payment` varchar(10) DEFAULT NULL COMMENT '实付金额',
  `post_fee` varchar(10) DEFAULT NULL COMMENT '运费',
  `buyer_account` varchar(128) DEFAULT NULL COMMENT '买家账号',
  `tariff_amount` varchar(10) DEFAULT NULL COMMENT '关税',
  `added_value_tax_amount` varchar(10) DEFAULT NULL COMMENT '增值税',
  `consumption_duty_amount` varchar(10) DEFAULT NULL COMMENT '消费税',
  `tax_amount` varchar(10) DEFAULT NULL COMMENT '总税额',
  `net_weight` varchar(10) DEFAULT NULL COMMENT '净重（千克）',
  `gross_weight` varchar(10) DEFAULT NULL COMMENT '毛重（千克）',
  `dis_amount` varchar(10) DEFAULT NULL COMMENT '优惠金额合计',
  `pre_sell` varchar(10) DEFAULT NULL COMMENT '是否预售',
  `freeze_reason` varchar(1024) DEFAULT NULL COMMENT '冻结原因',
  `buyer_remark` varchar(1024) DEFAULT NULL COMMENT '客户备注',
  `buyer_phone` varchar(32) DEFAULT NULL COMMENT '订购人电话',
  `buyer_id_num` varchar(32) DEFAULT NULL COMMENT '订购人身份证号码',
  `buyer_name` varchar(32) DEFAULT NULL COMMENT '订购人姓名',
  `books_no` varchar(64) DEFAULT NULL COMMENT '账册编号',
  `pay_code` varchar(32) DEFAULT NULL COMMENT '支付方式',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `payment_no` varchar(64) DEFAULT NULL COMMENT '支付单号',
  `order_seq_no` varchar(64) DEFAULT NULL COMMENT '支付交易号',
  `logistics_no` varchar(32) DEFAULT NULL COMMENT '运单号',
  `province` varchar(32) DEFAULT NULL COMMENT '省',
  `city` varchar(32) DEFAULT NULL COMMENT '市',
  `district` varchar(32) DEFAULT NULL COMMENT '区',
  `consignee_name` varchar(128) DEFAULT NULL COMMENT '收货人',
  `consignee_addr` varchar(128) DEFAULT NULL COMMENT '收货地址',
  `consignee_tel` varchar(32) DEFAULT NULL COMMENT '收货电话',
  `add_mark` varchar(32) DEFAULT NULL COMMENT '大头笔',
  `received_back_time` datetime DEFAULT NULL COMMENT '接单回传时间',
  `clear_start_time` datetime DEFAULT NULL COMMENT '清关开始时间',
  `clear_start_back_time` datetime DEFAULT NULL COMMENT '清关开始回传时间',
  `clear_success_time` datetime DEFAULT NULL COMMENT '清关完成时间',
  `clear_success_back_time` datetime DEFAULT NULL COMMENT '清关完成回传时间',
  `pack_time` datetime DEFAULT NULL COMMENT '打包时间',
  `pack_back_time` datetime DEFAULT NULL COMMENT '打包完成回传时间',
  `weighing_time` datetime DEFAULT NULL COMMENT '称重时间',
  `deliver_time` datetime DEFAULT NULL COMMENT '出库时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1222 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='跨境订单';

/**2021-05-07 快递信息、预计发货时间**/
ALTER TABLE `bus_cross_border_order` ADD COLUMN `logistics_status` int(11) DEFAULT NULL COMMENT '快递状态' after logistics_no;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `logistics_msg` text DEFAULT NULL COMMENT '快递信息' after logistics_status;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `exp_deliver_time` datetime DEFAULT NULL COMMENT '预计发货时间' after add_mark;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `logistics_collect_time` datetime DEFAULT NULL COMMENT '快递揽收时间' after deliver_time;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `logistics_sign_time` datetime DEFAULT NULL COMMENT '快递签收时间' after logistics_collect_time;
/**2021-05-07 快递信息、预计发货时间**/
/**2021-07-12 WMS信息**/
ALTER TABLE `bus_cross_border_order` ADD COLUMN `wms_status` varchar(32) DEFAULT NULL COMMENT 'WMS状态' after invt_no;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `send_pick_flag` varchar(32) DEFAULT "0" COMMENT '拣货发送指令' after wms_status;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `is_wave` varchar(10) DEFAULT "0" COMMENT '是否产生波次' after send_pick_flag;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `wave_no` varchar(64) DEFAULT NULL COMMENT '波次号' after is_wave;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `is_print` varchar(10) DEFAULT "0" COMMENT '是否打印' after wave_no;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `pick_print_info` varchar(256) DEFAULT NULL COMMENT '拣选单打印信息' after wave_no;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `bill_print_info` varchar(256) DEFAULT NULL COMMENT '面单打印信息' after pick_print_info;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `print_time` datetime DEFAULT NULL COMMENT '打印时间' after bill_print_info;
/**2021-07-12 WMS信息**/
ALTER TABLE `bus_cross_border_order` ADD COLUMN `theory_weight` varchar(64) DEFAULT NULL COMMENT '包裹理论重量' after pack_weight;


/**
跨境订单明细表
 */
DROP TABLE IF EXISTS  `bus_cross_border_order_details`;
CREATE TABLE `bus_cross_border_order_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '货品ID',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `hs_code` varchar(64) DEFAULT NULL COMMENT 'HS编码',
  `font_goods_name` varchar(1024) DEFAULT NULL COMMENT '前端商品名称',
  `goods_name` varchar(1024) DEFAULT NULL COMMENT '商品名称',
  `qty` varchar(10) DEFAULT NULL COMMENT '数量',
  `unit` varchar(10) DEFAULT NULL COMMENT '计量单位',
  `payment` varchar(10) DEFAULT NULL COMMENT '实付总价',
  `dutiable_value` varchar(10) DEFAULT NULL COMMENT '完税单价',
  `dutiable_total_value` varchar(10) DEFAULT NULL COMMENT '完税总价',
  `tariff_amount` varchar(10) DEFAULT NULL COMMENT '关税',
  `added_value_tax_amount` varchar(10) DEFAULT NULL COMMENT '增值税',
  `consumption_duty_amount` varchar(10) DEFAULT NULL COMMENT '消费税',
  `tax_amount` varchar(10) DEFAULT NULL COMMENT '总税额',
  `make_country` varchar(64) DEFAULT NULL COMMENT '原产国',
  `net_weight` varchar(10) DEFAULT NULL COMMENT '净重（千克）',
  `gross_weight` varchar(10) DEFAULT NULL COMMENT '毛重（千克）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3940 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='跨境订单明细';

DROP TABLE IF EXISTS  `bus_mq_log`;
CREATE TABLE `bus_mq_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `topic` varchar(64) NOT NULL COMMENT 'topic',
  `tag` varchar(64) NOT NULL COMMENT 'tag',
  `msg_key` varchar(64) DEFAULT NULL COMMENT 'key',
  `body` text NOT NULL COMMENT '消息内容',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `success` varchar(64) DEFAULT NULL COMMENT '是否成功',
  `msg` varchar(1024) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='消息日志表';


DROP TABLE IF EXISTS  `bus_order_log`;
CREATE TABLE `bus_order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `opt_node` varchar(64) DEFAULT NULL COMMENT '操作节点',
  `req_msg` text COMMENT '请求报文',
  `res_msg` text COMMENT '返回报文',
  `key_word` text COMMENT '关键字',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `success` varchar(64) DEFAULT NULL COMMENT '是否成功',
  `msg` varchar(1024) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2116 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='订单日志';
ALTER TABLE `bus_order_log` ADD COLUMN `cost_time` bigint(20) DEFAULT NULL COMMENT '花费时间' after msg;
ALTER TABLE `bus_order_log` ADD COLUMN `cost_time_msg` varchar(1024) DEFAULT NULL COMMENT '时间描述' after cost_time;

/**
拉单日志表
 */
DROP TABLE IF EXISTS  `bus_pull_order_log`;
CREATE TABLE `bus_pull_order_log` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20)  DEFAULT NULL COMMENT '店铺ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `page_no` INTEGER  DEFAULT NULL COMMENT '页面',
  `page_size` INTEGER  DEFAULT NULL COMMENT '页大小',
  `next_page` varchar(64)  DEFAULT NULL COMMENT '是否有下一页',
  `total` varchar(64)  DEFAULT NULL COMMENT '总数',
  `result` varchar(64)  DEFAULT NULL COMMENT '结果',
  `res_msg` text DEFAULT NULL COMMENT '返回报文',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='拉单日志';

/**
清关状态查询日志表
 */
DROP TABLE IF EXISTS  `bus_query_mft_log`;
CREATE TABLE `bus_query_mft_log` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `page_no` INTEGER  DEFAULT NULL COMMENT '页面',
  `page_size` INTEGER  DEFAULT NULL COMMENT '页大小',
  `next_page` varchar(64)  DEFAULT NULL COMMENT '是否有下一页',
  `result` varchar(64)  DEFAULT NULL COMMENT '结果',
  `res_msg` text DEFAULT NULL COMMENT '返回报文',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='清关状态查询日志表';

DROP TABLE IF EXISTS  `bus_order_deliver_log`;
CREATE TABLE `bus_order_deliver_log` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20)  DEFAULT NULL COMMENT '店铺ID',
  `user_name` varchar(64)  DEFAULT NULL COMMENT '扫描人',
  `order_no` varchar(64)  DEFAULT NULL COMMENT '订单号',
  `mail_no` varchar(64)  DEFAULT NULL COMMENT '运单号',
  `req_msg` text  DEFAULT NULL COMMENT '请求报文',
  `res_msg` text DEFAULT NULL COMMENT '返回报文',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `cost_time` bigint(20) DEFAULT NULL COMMENT '花费时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='订单出库日志';


/**
店铺授权信息表
 */
DROP TABLE IF EXISTS  `bus_shop_token`;
CREATE TABLE `bus_shop_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shop_id` bigint(20)  NOT NULL COMMENT '店铺id',
  `shop_name` varchar(64) DEFAULT NULL COMMENT '店铺名',
  `platform_shop_id` varchar(64) DEFAULT NULL COMMENT '平台店铺ID',
  `client_id` varchar(64) DEFAULT NULL COMMENT '应用id',
  `client_secret` varchar(64) DEFAULT NULL COMMENT '应用secret',
  `code` varchar(64) DEFAULT NULL COMMENT '授权码',
  `access_token` varchar(64) DEFAULT NULL COMMENT '令牌',
  `refresh_token` varchar(64) DEFAULT NULL COMMENT '刷新令牌',
  `code_get_time` datetime DEFAULT NULL COMMENT '授权码获取时间',
  `refresh_time` datetime DEFAULT NULL COMMENT '令牌刷新时间',
  `token_time` bigint(20) DEFAULT NULL COMMENT 'token有效期',
  `platform` varchar(32) DEFAULT NULL COMMENT '电商平台代码',
  `pub_key` varchar(512) DEFAULT NULL,
  `pri_key` varchar(1024) DEFAULT NULL,
  `pull_order_able` varchar(2) NOT NULL DEFAULT '1' COMMENT '是否允许拉单操作',
  `is_push_to_cn` varchar(8) DEFAULT '0' COMMENT '是否推送至菜鸟',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='店铺token';


/**
店铺信息表
 */
DROP TABLE IF EXISTS  `bus_shop_info`;
CREATE TABLE `bus_shop_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cust_id` varchar(32) NOT NULL COMMENT '客户ID',
  `platform_id` bigint(20) DEFAULT NULL COMMENT '电商平台',
  `platform_code` varchar(32) DEFAULT NULL COMMENT '电商平代码',
  `code` varchar(50) NOT NULL COMMENT '店铺代码',
  `name` varchar(50) NOT NULL COMMENT '店铺名',
  `platform` varchar(32) DEFAULT NULL COMMENT '电商平台',
  `service_type` varchar(8) NOT NULL COMMENT '业务类型',
  `service_id` bigint(20) DEFAULT NULL COMMENT '业务抬头id',
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=324 DEFAULT CHARSET=utf8 COMMENT='店铺信息';
-- ALTER TABLE `bus_shop_info` ADD COLUMN `logistics_msg` varchar(255) DEFAULT NULL COMMENT '快递公司' after `create_user_id`;




/**
平台信息表
 */
CREATE TABLE `bus_platform` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `plaf_name` varchar(128) DEFAULT NULL COMMENT '平台名',
  `plaf_nick_name` varchar(128) DEFAULT NULL COMMENT '平台别名',
  `plaf_code` varchar(32) DEFAULT NULL COMMENT '平台代码',
  `books_no` varchar(32) DEFAULT NULL COMMENT '账册编码',
  `ebp_code` varchar(32) NOT NULL,
  `ebp_name` varchar(32) NOT NULL,
  `order_form` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='电商平台表';

DROP TABLE IF EXISTS  `bus_user_customer`;
CREATE TABLE `bus_user_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='用户客户关系';


/**2021-04-13 退货需求**/
CREATE TABLE `bus_order_return` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `trade_return_no` varchar(64) DEFAULT NULL COMMENT '退货单号',
  `status` int(11) NOT NULL COMMENT '状态',
  `logistics_no` varchar(64) DEFAULT NULL COMMENT '物流订单号',
  `order_no` varchar(64) DEFAULT NULL COMMENT '原订单号',
  `declare_no` varchar(64) DEFAULT NULL COMMENT '申报单号',
  `tax_amount` varchar(10) DEFAULT NULL COMMENT '总税额',
  `is_border` varchar(10) DEFAULT NULL COMMENT '是否入区',
  `after_sales_type` varchar(10) DEFAULT NULL COMMENT '售后类型',
  `after_sales_no` varchar(64) DEFAULT NULL COMMENT '售后单号',
  `sales_customs_time` datetime DEFAULT NULL COMMENT '订单清关时间',
  `sales_deliver_time` datetime DEFAULT NULL COMMENT '订单放行时间',
  `s_express_no` varchar(64) DEFAULT NULL COMMENT '正向物流单号',
  `s_express_name` varchar(64) DEFAULT NULL COMMENT '正向物流公司',
  `r_express_no` varchar(64) DEFAULT NULL COMMENT '逆向物流单号',
  `r_express_name` varchar(64) DEFAULT NULL COMMENT '逆向物流公司',
  `return_type` varchar(10) DEFAULT NULL COMMENT '退货类型',
  `check_result` varchar(10) DEFAULT NULL COMMENT '质检结果',
  `declare_status` varchar(32) DEFAULT NULL COMMENT '清关状态',
  `declare_msg` varchar(1024) DEFAULT NULL COMMENT '清关信息',
  `take_time` datetime DEFAULT NULL COMMENT '收货时间',
  `take_back_time` datetime DEFAULT NULL COMMENT '收货回传时间',
  `check_time` datetime DEFAULT NULL COMMENT '质检完成时间',
  `check_back_time` datetime DEFAULT NULL COMMENT '质检完成回传时间',
  `declare_start_time` datetime DEFAULT NULL COMMENT '申报开始时间',
  `declare_start_back_time` datetime DEFAULT NULL COMMENT '申报开始回传时间',
  `declare_end_time` datetime DEFAULT NULL COMMENT '申报完成时间',
  `declare_end_back_time` datetime DEFAULT NULL COMMENT '申报完成回传时间',
  `bonded_ground_time` datetime DEFAULT NULL COMMENT '保税仓上架时间',
  `bonded_ground_back_time` datetime DEFAULT NULL COMMENT '保税仓上架回传时间',
  `tally_time` datetime DEFAULT NULL COMMENT '理货完成时间',
  `tally_back_time` datetime DEFAULT NULL COMMENT '理货完成回传时间',
  `return_ground_time` datetime DEFAULT NULL COMMENT '退货仓上架时间',
  `return_ground_back_time` datetime DEFAULT NULL COMMENT '退货仓上架回传时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_back_time` datetime DEFAULT NULL COMMENT '取消回传时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `logistics_no` (`logistics_no`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 COMMENT='订单退货';
ALTER TABLE `bus_order_return` ADD COLUMN `sales_deliver_time` datetime DEFAULT NULL COMMENT '订单放行时间' after `sales_customs_time`;
ALTER TABLE `bus_order_return` ADD COLUMN `is_over_time` varchar(10) DEFAULT NULL COMMENT '是否超时' after `create_time`;


DROP TABLE IF EXISTS  `bus_order_return_details`;
CREATE TABLE `bus_order_return_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `return_id` bigint(20) DEFAULT NULL COMMENT '退货单ID',
  `logistics_no` varchar(64) NOT NULL COMMENT '物流订单号',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '货品ID',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `bar_code` varchar(64) DEFAULT NULL COMMENT '条形码',
  `hs_code` varchar(64) DEFAULT NULL COMMENT 'HS编码',
  `font_goods_name` varchar(1024) DEFAULT NULL COMMENT '前端商品名称',
  `goods_name` varchar(1024) DEFAULT NULL COMMENT '商品名称',
  `qty` varchar(10) DEFAULT NULL COMMENT '数量',
  `tax_amount` varchar(10) DEFAULT NULL COMMENT '总税额',
  `normal_num` int(11) DEFAULT NULL COMMENT '正品数量',
  `damaged_num` int(11) DEFAULT NULL COMMENT '残品数量',
  `total_num` int(11) DEFAULT NULL COMMENT '总数量',
  `damaged_reason` varchar(1024) DEFAULT NULL COMMENT '残次原因',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3962 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='订单退货明细';


DROP TABLE IF EXISTS  `bus_order_return_log`;
CREATE TABLE `bus_order_return_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `return_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `return_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `opt_node` varchar(64) DEFAULT NULL COMMENT '操作节点',
  `req_msg` text COMMENT '请求报文',
  `res_msg` text COMMENT '返回报文',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `success` varchar(64) DEFAULT NULL COMMENT '是否成功',
  `msg` varchar(1024) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2158 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='退货单日志';

/**2021-04-13 退货需求**/


/**2021-04-15 商品基本信息**/
DROP TABLE IF EXISTS  `bus_base_sku`;
CREATE TABLE `bus_base_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `customers_id` bigint(20) NOT NULL COMMENT '客户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `status` int(11) NOT NULL COMMENT '状态',
  `goods_code` varchar(64) NOT NULL COMMENT '商品编码',
  `bar_code` varchar(64) DEFAULT NULL COMMENT '条形码',
  `goods_name` varchar(128) NOT NULL COMMENT '商品名称',
  `sn_control` varchar(10) NOT NULL COMMENT '是否SN管理',
  `sale_l` decimal(18,4) DEFAULT NULL COMMENT '长',
  `sale_w` decimal(18,4) DEFAULT NULL COMMENT '宽',
  `sale_h` decimal(18,4) DEFAULT NULL COMMENT '高',
  `sale_volume` decimal(18,4) DEFAULT NULL COMMENT '体积',
  `sale_weight` decimal(18,4) DEFAULT NULL COMMENT '重量',
  `pack_l` decimal(18,4) DEFAULT NULL COMMENT '箱长',
  `pack_w` decimal(18,4) DEFAULT NULL COMMENT '箱宽',
  `pack_h` decimal(18,4) DEFAULT NULL COMMENT '箱高',
  `pack_volume` decimal(18,4) DEFAULT NULL COMMENT '箱体积',
  `pack_weight` decimal(18,4) DEFAULT NULL COMMENT '箱重',
  `pack_num` int(11) DEFAULT NULL COMMENT '箱规',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `hs_code` varchar(64) DEFAULT NULL COMMENT 'HS编码',
  `goods_name_c` varchar(256) NOT NULL COMMENT '海关备案名中文',
  `goods_name_e` varchar(256) DEFAULT NULL COMMENT '海关备案名英文',
  `net_weight` decimal(18,4) DEFAULT NULL COMMENT '净重',
  `gross_weight` decimal(18,4) DEFAULT NULL COMMENT '毛重',
  `legal_unit` varchar(64) DEFAULT NULL COMMENT '法一单位',
  `legal_unit_code` varchar(64) DEFAULT NULL COMMENT '法一单位代码',
  `legal_num` decimal(18,4) DEFAULT NULL COMMENT '法一数量',
  `second_unit` varchar(64) DEFAULT NULL COMMENT '法二单位',
  `second_unit_code` varchar(64) DEFAULT NULL COMMENT '法二单位代码',
  `second_num` decimal(18,4) DEFAULT NULL COMMENT '法二数量',
  `supplier` varchar(128) DEFAULT NULL COMMENT '供应商名称',
  `brand` varchar(128) DEFAULT NULL COMMENT '品牌',
  `property` varchar(64) DEFAULT NULL COMMENT '规格型号',
  `make_contry` varchar(64) DEFAULT NULL COMMENT '原产地',
  `guse` varchar(128) DEFAULT NULL COMMENT '用途',
  `gcomposition` varchar(1024) DEFAULT NULL COMMENT '成分',
  `gfunction` varchar(128) DEFAULT NULL COMMENT '功能',
  `unit` varchar(64) DEFAULT NULL COMMENT '申报单位',
  `unit_code` varchar(64) DEFAULT NULL COMMENT '申报单位代码',
  `remark` varchar(1024) DEFAULT NULL COMMENT '商品备注',
  `auditing_fail_reason` varchar(1024) DEFAULT NULL COMMENT '审核不过原因',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COMMENT='商品档案';
ALTER TABLE `bus_base_sku` ADD COLUMN `outer_goods_no` varchar(64) DEFAULT NULL COMMENT '外部货号' after `goods_no`;
ALTER TABLE `bus_base_sku` ADD COLUMN `lifecycle` INTEGER DEFAULT NULL COMMENT '保质期天数' after `gross_weight`;

ALTER TABLE `bus_base_sku` ADD COLUMN `register_type` varchar(10) DEFAULT NULL COMMENT '备案模式' after `remark`;
ALTER TABLE `bus_base_sku` ADD COLUMN `is_new` varchar(10) DEFAULT NULL COMMENT '是否新品' after `register_type`;
ALTER TABLE `bus_shop_info` ADD COLUMN `register_type` varchar(10) DEFAULT NULL COMMENT '备案模式' after `service_id`;

ALTER TABLE `bus_base_sku` ADD COLUMN `books_no` varchar(64) DEFAULT NULL COMMENT '账册编号' after is_new;
ALTER TABLE `bus_base_sku` ADD COLUMN `record_no` varchar(64) DEFAULT NULL COMMENT '备案序号' after books_no;
ALTER TABLE `bus_base_sku` ADD COLUMN `make_contry_code` varchar(64) DEFAULT NULL COMMENT '原产国代码' after make_contry;
ALTER TABLE `bus_base_sku` ADD COLUMN `is_gift` varchar(10) DEFAULT NULL COMMENT '是否赠品' after auditing_fail_reason;


DROP TABLE IF EXISTS `bus_sku_log`;
CREATE TABLE `bus_sku_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `sku_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `status` varchar(64) DEFAULT NULL COMMENT '操作节点',
  `req_msg` text COMMENT DEFAULT NUL '请求报文',
  `res_msg` text COMMENT DEFAULT NUL '返回报文',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `success` varchar(64) DEFAULT NULL COMMENT '是否成功',
  `msg` varchar(1024) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2116 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='商品日志';

/**2021-04-15 商品基本信息**/

/**2021-04-28 业务日志**/
DROP TABLE IF EXISTS `bus_request_log`;
CREATE TABLE `bus_request_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bus_type` varchar(64) DEFAULT NULL COMMENT '业务类型',
  `bus_type_name` varchar(64) DEFAULT NULL COMMENT '业务类型描述',
  `bus_no` bigint(20) DEFAULT NULL COMMENT '业务单号',
  `req_url` varchar(128) DEFAULT NULL COMMENT '请求地址',
  `req_msg` text COMMENT '请求报文',
  `res_msg` text COMMENT '返回报文',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `cost_time` bigint(20) DEFAULT NULL COMMENT '请求耗时',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2116 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='业务请求日志';
/**2021-04-28 业务请求日志**/


DROP TABLE IF EXISTS  `bus_inbound_order`;
CREATE TABLE `bus_inbound_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '入库单号',
  `out_no` varchar(64) DEFAULT NULL COMMENT '外部单号',
  `wms_no` varchar(64) DEFAULT NULL COMMENT 'WMS单号',
  `order_type` varchar(10) DEFAULT NULL COMMENT '单据类型',
  `status` int(11) NOT NULL COMMENT '状态',
  `original_no` varchar(64) DEFAULT NULL COMMENT '原单号',
  `declare_no` varchar(64) DEFAULT NULL COMMENT '报关单号',
  `inspect_no` varchar(64) DEFAULT NULL COMMENT '报检单号',
  `expect_arrive_time` datetime DEFAULT NULL COMMENT '预期到货时间',
  `tally_way` varchar(10) DEFAULT '0' COMMENT '理货维度',
  `pallet_num` INTEGER DEFAULT NULL COMMENT '托数',
  `box_num` INTEGER DEFAULT NULL COMMENT '箱数',
  `expect_sku_num` INTEGER DEFAULT NULL COMMENT '预期SKU数',
  `grounding_sku_num` INTEGER DEFAULT NULL COMMENT '上架SKU数',
  `expect_total_num` INTEGER DEFAULT NULL COMMENT '预期总件数',
  `grounding_total_num` INTEGER DEFAULT NULL COMMENT '上架总件数',
  `grounding_normal_num` INTEGER DEFAULT NULL COMMENT '上架正品件数',
  `grounding_damaged_num` INTEGER DEFAULT NULL COMMENT '上架残品件数',
  `confirm_by` varchar(255) DEFAULT NULL COMMENT '接单确认人',
  `confirm_time` datetime DEFAULT NULL COMMENT '接单确认时间',
  `arrive_by` varchar(255) DEFAULT NULL COMMENT '到货登记人',
  `car_number` varchar(255) DEFAULT NULL COMMENT '车牌号',
  `arrive_time` datetime DEFAULT NULL COMMENT '到货时间',
  `arrive_back_time` datetime DEFAULT NULL COMMENT '到货回传时间',
  `tally_by` varchar(255) DEFAULT NULL COMMENT '理货人',
  `tally_start_time` datetime DEFAULT NULL COMMENT '理货开始时间',
  `tally_start_back_time` datetime DEFAULT NULL COMMENT '理货开始回传时间',
  `tally_end_time` datetime DEFAULT NULL COMMENT '理货结束时间',
  `tally_end_back_time` datetime DEFAULT NULL COMMENT '理货结束回传时间',
  `take_by` varchar(255) DEFAULT NULL COMMENT '收货人',
  `take_time` datetime DEFAULT NULL COMMENT '收货完成时间',
  `take_back_time` datetime DEFAULT NULL COMMENT '收货完成回传时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='入库单';


DROP TABLE IF EXISTS  `bus_inbound_order_details`;
CREATE TABLE `bus_inbound_order_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '入库单号',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '货品ID',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `bar_code` varchar(64) DEFAULT NULL COMMENT '条码',
  `goods_name` varchar(64) DEFAULT NULL COMMENT '商品名称',
  `goods_line_no` varchar(64) DEFAULT NULL COMMENT '商品行',
  `expect_num` INTEGER DEFAULT NULL COMMENT '预期到货数量',
  `take_num` INTEGER DEFAULT NULL COMMENT '实际收货数量',
  `lack_num` INTEGER DEFAULT NULL COMMENT '短少数量',
  `normal_num` INTEGER DEFAULT NULL COMMENT '正品数量',
  `damaged_num` INTEGER DEFAULT NULL COMMENT '残品数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='入库单明细';

DROP TABLE IF EXISTS  `bus_inbound_order_log`;
CREATE TABLE `bus_inbound_order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `opt_node` varchar(64) DEFAULT NULL COMMENT '操作节点',
  `req_msg` text COMMENT '请求报文',
  `res_msg` text COMMENT '返回报文',
  `key_word` text COMMENT '关键字',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `success` varchar(64) DEFAULT NULL COMMENT '是否成功',
  `msg` varchar(1024) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2116 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='订单日志';

DROP TABLE IF EXISTS  `bus_inbound_tally`;
CREATE TABLE `bus_inbound_tally` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '入库单ID',
  `order_no` varchar(64) NOT NULL COMMENT '入库单号',
  `tally_no` varchar(64) NOT NULL COMMENT '理货单号',
  `status` int(11) NOT NULL COMMENT '理货状态',
  `tally_count` INTEGER DEFAULT NULL COMMENT '理货次数',
  `pallet_num` INTEGER DEFAULT NULL COMMENT '托数',
  `box_num` INTEGER DEFAULT NULL COMMENT '箱数',
  `expect_sku_num` INTEGER DEFAULT NULL COMMENT '预期SKU数',
  `tally_sku_num` INTEGER DEFAULT NULL COMMENT '理货SKU数',
  `expect_total_num` INTEGER DEFAULT NULL COMMENT '预期总件数',
  `tally_total_num` INTEGER DEFAULT NULL COMMENT '理货总件数',
  `tally_normal_num` INTEGER DEFAULT NULL COMMENT '理货正品件数',
  `tally_damaged_num` INTEGER DEFAULT NULL COMMENT '理货残品件数',
  `tally_start_time` datetime DEFAULT NULL COMMENT '理货开始时间',
  `tally_end_time` datetime DEFAULT NULL COMMENT '理货结束时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='入库理货单';

DROP TABLE IF EXISTS  `bus_inbound_tally_details`;
CREATE TABLE `bus_inbound_tally_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tally_id` bigint(20) NOT NULL COMMENT '理货ID',
  `tally_no` varchar(64) NOT NULL COMMENT '理货单号',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '货品ID',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `bar_code` varchar(64) DEFAULT NULL COMMENT '条码',
  `goods_name` varchar(64) DEFAULT NULL COMMENT '商品名称',
  `goods_quality` int(11) NOT NULL COMMENT '货品属性',
  `tally_num` INTEGER DEFAULT NULL COMMENT '理货数量',
  `product_date` varchar(64) DEFAULT NULL COMMENT '生产日期',
  `expiry_date` varchar(64) DEFAULT NULL COMMENT '失效日期',
  `batch_no` varchar(64) DEFAULT NULL COMMENT '生产批次',
  `pic_url` varchar(256) DEFAULT NULL COMMENT '图片地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='入库理货单明细';


DROP TABLE IF EXISTS  `bus_daily_stock`;
CREATE TABLE `bus_daily_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `day_time` varchar(64) DEFAULT NULL COMMENT '时间',
  `wms_customers_code` varchar(64) DEFAULT NULL COMMENT 'WMS货主代码',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '商品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '货号',
  `bar_code` varchar(64) DEFAULT NULL COMMENT '条码',
  `goods_name` varchar(256) DEFAULT NULL COMMENT '名称',
  `wms_goods_name` varchar(256) DEFAULT NULL COMMENT 'WMS商品名称',
  `location` varchar(64) DEFAULT NULL COMMENT '库位',
  `batch_no` varchar(64) DEFAULT NULL COMMENT '批次号',
  `qty` int(11) DEFAULT NULL COMMENT '库存数量',
  `wms_time` datetime DEFAULT NULL COMMENT 'WMS保存时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='日结余库存';




DROP TABLE IF EXISTS  `bus_outbound_order`;
CREATE TABLE `bus_outbound_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '入库单号',
  `out_no` varchar(64) DEFAULT NULL COMMENT '外部单号',
  `wms_no` varchar(64) DEFAULT NULL COMMENT 'WMS单号',
  `order_type` varchar(10) DEFAULT NULL COMMENT '单据类型',
  `status` int(11) NOT NULL COMMENT '状态',
  `original_no` varchar(64) DEFAULT NULL COMMENT '原单号',
  `expect_deliver_time` datetime DEFAULT NULL COMMENT '预期发货时间',
  `tally_way` varchar(10) DEFAULT '0' COMMENT '理货维度',
  `pallet_num` INTEGER DEFAULT NULL COMMENT '托数',
  `box_num` INTEGER DEFAULT NULL COMMENT '箱数',
  `expect_sku_num` INTEGER DEFAULT NULL COMMENT '预期SKU数',
  `deliver_sku_num` INTEGER DEFAULT NULL COMMENT '出库SKU数',
  `expect_total_num` INTEGER DEFAULT NULL COMMENT '预期总件数',
  `deliver_total_num` INTEGER DEFAULT NULL COMMENT '出库总件数',
  `deliver_normal_num` INTEGER DEFAULT NULL COMMENT '出库正品件数',
  `deliver_damaged_num` INTEGER DEFAULT NULL COMMENT '出库残品件数',
  `confirm_by` varchar(255) DEFAULT NULL COMMENT '接单确认人',
  `confirm_time` datetime DEFAULT NULL COMMENT '接单确认时间',
  `tally_by` varchar(255) DEFAULT NULL COMMENT '理货人',
  `tally_start_time` datetime DEFAULT NULL COMMENT '理货开始时间',
  `tally_start_back_time` datetime DEFAULT NULL COMMENT '理货开始回传时间',
  `tally_end_time` datetime DEFAULT NULL COMMENT '理货结束时间',
  `tally_end_back_time` datetime DEFAULT NULL COMMENT '理货结束回传时间',
  `deliver_by` varchar(255) DEFAULT NULL COMMENT '收货人',
  `deliver_time` datetime DEFAULT NULL COMMENT '收货完成时间',
  `deliver_back_time` datetime DEFAULT NULL COMMENT '收货完成回传时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='出库单';


DROP TABLE IF EXISTS  `bus_outbound_order_details`;
CREATE TABLE `bus_outbound_order_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '入库单号',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '货品ID',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `bar_code` varchar(64) DEFAULT NULL COMMENT '条码',
  `goods_name` varchar(64) DEFAULT NULL COMMENT '商品名称',
  `goods_line_no` varchar(64) DEFAULT NULL COMMENT '商品行',
  `expect_num` INTEGER DEFAULT NULL COMMENT '预期发货数量',
  `deliver_num` INTEGER DEFAULT NULL COMMENT '实际发货数量',
  `lack_num` INTEGER DEFAULT NULL COMMENT '短少数量',
  `normal_num` INTEGER DEFAULT NULL COMMENT '正品数量',
  `damaged_num` INTEGER DEFAULT NULL COMMENT '残品数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='出库单明细';

DROP TABLE IF EXISTS  `bus_outbound_order_log`;
CREATE TABLE `bus_outbound_order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `opt_node` varchar(64) DEFAULT NULL COMMENT '操作节点',
  `req_msg` text COMMENT '请求报文',
  `res_msg` text COMMENT '返回报文',
  `key_word` text COMMENT '关键字',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `success` varchar(64) DEFAULT NULL COMMENT '是否成功',
  `msg` varchar(1024) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2116 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='出库单日志';

DROP TABLE IF EXISTS  `bus_pack_check`;
CREATE TABLE `bus_pack_check` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `logistics_no` varchar(64) NOT NULL COMMENT '运单号',
  `remark` varchar(255) NOT NULL COMMENT '说明',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='包裹抽检';

DROP TABLE IF EXISTS  `bus_pack_check_details`;
CREATE TABLE `bus_pack_check_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `check_id` bigint(20) NOT NULL COMMENT '主单ID',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `bar_code` varchar(64) NOT NULL COMMENT '条码',
  `expect_qty` INTEGER NOT NULL COMMENT '预期数量',
  `current_qty` INTEGER NOT NULL COMMENT '当前数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='包裹抽检明细';


DROP TABLE IF EXISTS  `bus_add_value_order`;
CREATE TABLE `bus_add_value_order` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺',
  `status` int(20) NOT NULL COMMENT '状态',
  `order_no` varchar(255) NOT NULL COMMENT '订单号',
  `out_order_no` varchar(255) DEFAULT NULL COMMENT '外部单号',
  `warehouse` int(64) DEFAULT NULL COMMENT '仓区',
  `type` int(20) NOT NULL COMMENT '类型',
  `add_code` varchar(255) NOT NULL COMMENT '增值编码',
  `add_name` varchar(255) DEFAULT NULL COMMENT '增值名称',
  `ref_no` varchar(255) DEFAULT NULL COMMENT '关联单号',
  `ref_type` int(10) DEFAULT NULL COMMENT '关联单类型',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='增值单据';
ALTER TABLE `bus_add_value_order` ADD COLUMN `finish_qty` int(11) DEFAULT NULL COMMENT '实际完后数量' after `add_name`;

DROP TABLE IF EXISTS  `bus_add_value_order_details`;
CREATE TABLE `bus_add_value_order_details` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `goods_id` varchar(255) NOT NULL COMMENT '商品ID ',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `bar_code` varchar(64) DEFAULT NULL COMMENT '条码',
  `goods_name` varchar(64) DEFAULT NULL COMMENT '商品名称',
  `qty` int(11) NOT NULL COMMENT '数量',
  `default01` varchar(255) DEFAULT NULL COMMENT '客户批次号',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='单据明细';

ALTER TABLE `bus_add_value_order_details` ADD COLUMN `finish_qty` int(11) DEFAULT NULL COMMENT '实际完后数量' after `qty`;
ALTER TABLE `bus_add_value_order` ADD COLUMN `finish_qty` int(11) DEFAULT NULL COMMENT '实际完后数量' after `add_name`;



DROP TABLE IF EXISTS  `bus_customs_code`;
CREATE TABLE `bus_customs_code` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(64) NOT NULL COMMENT '类型 ',
  `type_des` varchar(64) NOT NULL COMMENT '类型描述',
  `code` varchar(64) NOT NULL COMMENT '代码',
  `des` varchar(128) NOT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='海关代码表';


DROP TABLE IF EXISTS  `bus_hezhu_info`;
CREATE TABLE `bus_hezhu_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `clear_id` bigint(20) NOT NULL COMMENT '清关ID',
  `order_no` varchar(64) NOT NULL COMMENT '单据编号',
  `clear_no` varchar(64) NOT NULL COMMENT '清关单号',
  `status` int(20) NOT NULL COMMENT '状态',
  `trade_type` varchar(10) DEFAULT NULL COMMENT '贸易类型',
  `customs_status` int(20) DEFAULT NULL COMMENT '海关状态',
  `customers_id` bigint(20) NOT NULL COMMENT '客户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `clear_company_id` bigint(20) NOT NULL COMMENT '清关抬头ID',
  `bus_type` varchar(10) NOT NULL COMMENT '业务类型',
  `sku_num` int(11) DEFAULT NULL COMMENT '预估SKU数量',
  `total_num` int(11) DEFAULT NULL COMMENT '预估件数',
  `gross_weight` decimal(18,5) DEFAULT NULL COMMENT '毛重',
  `net_weight` decimal(18,5) DEFAULT NULL COMMENT '净重',
  `total_price` decimal(18,5) DEFAULT NULL COMMENT '总金额',
  `in_ware_hose` varchar(64) DEFAULT NULL COMMENT '入库仓',
  `entry_no` varchar(64) DEFAULT NULL COMMENT '报关单号',
  `decl_no` varchar(64) DEFAULT NULL COMMENT '报检单号',
  `regulatory_way` varchar(10) DEFAULT NULL COMMENT '监管方式',
  `clear_type` varchar(10) DEFAULT NULL COMMENT '报关类型',
  `qd_code` varchar(64) DEFAULT NULL COMMENT 'QD单号',
  `ref_qd_code` varchar(128) DEFAULT NULL COMMENT '关联单证编码',
  `books_no` varchar(64) DEFAULT NULL COMMENT '账册编号',
  `ref_books_no` varchar(64) DEFAULT NULL COMMENT '关联账册编号',
  `trans_way` varchar(10) DEFAULT NULL COMMENT '运输方式',
  `in_port` varchar(10) DEFAULT NULL COMMENT '进境关别',
  `ship_country` varchar(10) DEFAULT NULL COMMENT '启运国',
  `clear_start_time` datetime DEFAULT NULL COMMENT '清关开始时间',
  `clear_end_time` datetime DEFAULT NULL COMMENT '清关完成时间',
  `finish_time` datetime DEFAULT NULL COMMENT '服务完成时间',
  `order_source` varchar(10) DEFAULT NULL COMMENT '单据来源',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='核注单管理';



DROP TABLE IF EXISTS `bus_hezhu_details`;
CREATE TABLE `bus_hezhu_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '清关ID',
  `order_no` varchar(64) NOT NULL COMMENT '单据编号',
  `seq_no` int(11) DEFAULT NULL COMMENT '序号',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '货品ID',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `outer_goods_no` varchar(64) DEFAULT NULL COMMENT '外部货号',
  `hs_code` varchar(64) DEFAULT NULL COMMENT 'HS编码',
  `goods_name` varchar(1024) DEFAULT NULL COMMENT '商品名称',
  `record_no` varchar(64) DEFAULT NULL COMMENT '备案序号',
  `net_weight` varchar(64) DEFAULT NULL COMMENT '净重（千克）',
  `gross_weight` varchar(64) DEFAULT NULL COMMENT '毛重（千克）',
  `legal_unit` varchar(64) DEFAULT NULL COMMENT '法一单位',
  `legal_unit_code` varchar(64) DEFAULT NULL COMMENT '法一单位代码',
  `legal_num` varchar(64) DEFAULT NULL COMMENT '法一数量',
  `second_unit` varchar(64) DEFAULT NULL COMMENT '法二单位',
  `second_unit_code` varchar(64) DEFAULT NULL COMMENT '法二单位代码',
  `second_num` varchar(64) DEFAULT NULL COMMENT '法二数量',
  `qty` varchar(64) DEFAULT NULL COMMENT '数量',
  `unit` varchar(10) DEFAULT NULL COMMENT '计量单位',
  `price` varchar(64) DEFAULT NULL COMMENT '商品单价',
  `total_price` varchar(64) DEFAULT NULL COMMENT '商品总价',
  `property` varchar(1024) DEFAULT NULL COMMENT '规格型号',
  `currency` varchar(64) DEFAULT NULL COMMENT '币种',
  `make_country` varchar(64) DEFAULT NULL COMMENT '原产国',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4050 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='核注单明细';

DROP TABLE IF EXISTS  `bus_hezhu_order`;
CREATE TABLE `bus_hezhu_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '单据编号',
  `ref_order_no` varchar(64) DEFAULT NULL COMMENT '关联单据号',
  `ref_order_type` varchar(10) DEFAULT NULL COMMENT '关联单据类型',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='核注关联订单';

DROP TABLE IF EXISTS  `bus_hezhu_log`;
CREATE TABLE `bus_hezhu_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `status` varchar(64) DEFAULT NULL COMMENT '状态',
  `opt_node` varchar(64) DEFAULT NULL COMMENT '操作节点',
  `req_msg` text COMMENT '请求报文',
  `res_msg` text COMMENT '返回报文',
  `key_word` text COMMENT '关键字',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `success` varchar(64) DEFAULT NULL COMMENT '是否成功',
  `msg` varchar(1024) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2116 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='核注单日志';


ALTER TABLE `bus_clear_info` ADD COLUMN `net_weight` decimal(18,2) DEFAULT NULL COMMENT '净重（千克）' after `groos_weight`;
ALTER TABLE `bus_clear_details` ADD COLUMN `record_no` varchar(64) DEFAULT NULL COMMENT '备案序号' after `qty`;

DROP TABLE IF EXISTS  `bus_trans_info`;
CREATE TABLE `bus_trans_info` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `clear_id` bigint(20) NOT NULL COMMENT '清关ID',
  `order_no` varchar(64) NOT NULL COMMENT '单据编号',
  `clear_no` varchar(64) NOT NULL COMMENT '清关单号',
  `status` int(20) NOT NULL COMMENT '状态',
  `trade_type` varchar(10) DEFAULT NULL COMMENT '贸易类型',
  `customers_id` bigint(20) NOT NULL COMMENT '客户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `sku_num` int(11) DEFAULT NULL COMMENT '预估SKU数量',
  `total_num` int(11) DEFAULT NULL COMMENT '预估件数',
  `pack_way` varchar(32) DEFAULT NULL COMMENT '打包方式',
  `pack_num` integer DEFAULT NULL COMMENT '打包数量',
  `plan_car_type` varchar(10) DEFAULT NULL COMMENT '排车方',
  `share_flag` varchar(10) DEFAULT NULL COMMENT '是否拼车',
  `order_source` varchar(10) DEFAULT NULL COMMENT '单据来源',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='运输信息';


DROP TABLE IF EXISTS  `bus_trans_details`;
CREATE TABLE `bus_trans_details` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20)  NOT NULL COMMENT '单据ID',
  `order_no` varchar(64) default NULL COMMENT '单据编号',
  `container_no` varchar(64) NOT NULL COMMENT '箱号',
  `container_type` varchar(32) NOT NULL COMMENT '箱型',
  `plan_car_type` varchar(10) NOT NULL COMMENT '排车方',
  `plate_no` varchar(64) NOT NULL COMMENT '车牌',
  `car_type` varchar(64) NOT NULL COMMENT '车型',
  `share_flag` varchar(10) NOT NULL COMMENT '是否拼车',
  `pack_way` varchar(32) NOT NULL COMMENT '打包方式',
  `pack_num` integer NOT NULL COMMENT '打包数量',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='车辆明细';

DROP TABLE IF EXISTS  `bus_trans_log`;
CREATE TABLE `bus_trans_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `status` varchar(64) DEFAULT NULL COMMENT '状态',
  `opt_node` varchar(64) DEFAULT NULL COMMENT '操作节点',
  `req_msg` text COMMENT '请求报文',
  `res_msg` text COMMENT '返回报文',
  `key_word` text COMMENT '关键字',
  `host` varchar(64) DEFAULT NULL COMMENT '执行机器',
  `success` varchar(64) DEFAULT NULL COMMENT '是否成功',
  `msg` varchar(1024) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2116 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='运输单日志';


DROP TABLE IF EXISTS  `bus_sorting_rule`;
CREATE TABLE `bus_sorting_rule` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `rule_name` varchar(64) NOT NULL COMMENT '规则名称',
  `rule_code` varchar(64) NOT NULL COMMENT '规则代码',
  `rule_code1` varchar(64) DEFAULT NULL COMMENT '规则代码1(跨境购)',
  `rule_code2` varchar(64) DEFAULT NULL COMMENT '规则代码2(菜鸟)',
  `status` varchar(10) NOT NULL COMMENT '状态',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='分拣规则表';

DROP TABLE IF EXISTS  `bus_sorting_line`;
CREATE TABLE `bus_sorting_line` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `line_name` varchar(64) NOT NULL COMMENT '分拣线名称',
  `line_code` varchar(64) NOT NULL COMMENT '分拣线代码',
  `area` varchar(64) NOT NULL COMMENT '绑定区域',
  `user_id` varchar(64) NOT NULL COMMENT '绑定用户ID',
  `status` varchar(10) NOT NULL COMMENT '状态',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='分拣线';

DROP TABLE IF EXISTS  `bus_sorting_line_chute_code`;
CREATE TABLE `bus_sorting_line_chute_code` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `line_id` bigint(20) NOT NULL COMMENT '分拣线ID',
  `user_id` varchar(64) NOT NULL COMMENT '绑定用户ID',
  `chute_code` varchar(64) NOT NULL COMMENT '格口代码',
  `chute_name` varchar(64) NOT NULL COMMENT '格口名称',
  `rule_id` bigint(20) NOT NULL COMMENT '规则ID',
  `rule_name` varchar(64) NOT NULL COMMENT '规则名称',
  `rule_code` varchar(64) NOT NULL COMMENT '规则代码',
  `rule_code1` varchar(64) DEFAULT NULL COMMENT '规则代码1(跨境购)',
  `rule_code2` varchar(64) DEFAULT NULL COMMENT '规则代码2(菜鸟)',
  `status` varchar(10) NOT NULL COMMENT '状态',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='分拣线格口';

ALTER TABLE `bus_order_deliver_log` ADD COLUMN `weight` varchar(64) DEFAULT NULL COMMENT '重量' after `mail_no`;
ALTER TABLE `bus_order_deliver_log` ADD COLUMN `rule_code` varchar(64) DEFAULT NULL COMMENT '规则代码' after `weight`;
ALTER TABLE `bus_order_deliver_log` ADD COLUMN `platform_code` varchar(64) DEFAULT NULL COMMENT '电商平台' after `shop_id`;

ALTER TABLE `bus_cross_border_order` ADD COLUMN `default01` varchar(32) DEFAULT NULL COMMENT '预留字段1' after platform_status;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `default02` varchar(32) DEFAULT NULL COMMENT '预留字段2' after default01;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `default03` varchar(32) DEFAULT NULL COMMENT '预留字段3' after default02;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `default04` varchar(32) DEFAULT NULL COMMENT '预留字段4' after default03;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `default05` varchar(32) DEFAULT NULL COMMENT '预留字段5' after default04;

ALTER TABLE `bus_cross_border_order` ADD COLUMN `logistics_code` varchar(64) DEFAULT NULL COMMENT '快递公司代码' after default04;
ALTER TABLE `bus_cross_border_order` ADD COLUMN `logistics_name` varchar(64) DEFAULT NULL COMMENT '快递公司名称' after logistics_code;
ALTER TABLE `bus_shop_info` ADD COLUMN `kjg_code` varchar(64) DEFAULT NULL COMMENT '跨境购店铺代码' after register_type;

ALTER TABLE `bus_cross_border_order` ADD COLUMN `so_no` varchar(64) DEFAULT NULL COtomersMMENT 'SO单号' after wave_no;


DROP TABLE IF EXISTS  `bus_deposit`;
CREATE TABLE `bus_deposit` (
    `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `customers_id` bigint(20) NOT NULL COMMENT '客户ID',
    `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
    `amount` decimal(18,6) NOT NULL COMMENT '金额',
    `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='保证金管理';
ALTER TABLE eladmin.bus_deposit ADD CONSTRAINT bus_deposit_un UNIQUE KEY (shop_id);

DROP TABLE IF EXISTS  `bus_deposit_log`;
CREATE TABLE `bus_deposit_log` (
   `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
   `deposit_id` bigint(20) NOT NULL COMMENT '保证金ID',
   `type` varchar(10) NOT NULL COMMENT '变动类型',
   `order_no` varchar(64) DEFAULT NULL COMMENT '关联订单',
   `change_amount` decimal(18,6) NOT NULL COMMENT '金额',
   `current_amount` decimal(18,6) NOT NULL COMMENT '当前金额',
   `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
   `create_time` datetime NOT NULL COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='保证金管理日志';


ALTER TABLE `bus_mq_log` ADD COLUMN `msg_id` varchar(128) DEFAULT NULL COMMENT 'msgId' after tag;


ALTER TABLE `bus_order_return` ADD COLUMN `invt_no` varchar(64) DEFAULT NULL COMMENT '总署清单编号' after declare_no;
ALTER TABLE `bus_order_return` ADD COLUMN `remark` varchar(1024) DEFAULT NULL COMMENT '备注说明' after is_over_time;
ALTER TABLE `bus_order_return` ADD COLUMN `platform_code` varchar(1024) DEFAULT NULL COMMENT '平台' after invt_no;
ALTER TABLE `bus_order_return` ADD COLUMN `order_source` varchar(10) DEFAULT NULL COMMENT '单据来源' after is_over_time;
ALTER TABLE `bus_order_return` ADD COLUMN `wms_no` varchar(64) DEFAULT NULL COMMENT 'WMS单号' after is_over_time;
ALTER TABLE `bus_order_return` ADD COLUMN `is_wave` varchar(10) DEFAULT NULL COMMENT '产生波次' after wms_no;
ALTER TABLE `bus_order_return` ADD COLUMN `close_time` datetime DEFAULT NULL COMMENT '关单时间' after is_wave;

DROP TABLE IF EXISTS  `bus_logistics_info`;
CREATE TABLE `bus_logistics_info` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(64) NOT NULL COMMENT '名称',
  `code` varchar(32) NOT NULL COMMENT '代码',
  `customs_name` varchar(64) DEFAULT NULL COMMENT '海关备案名称',
  `customs_code` varchar(32) DEFAULT NULL COMMENT '海关备案代码',
  `kjg_code` varchar(32) DEFAULT NULL COMMENT '跨境购代码',
  `kjg_name` varchar(32) DEFAULT NULL COMMENT '跨境购名称',
  `default01` varchar(64) DEFAULT NULL COMMENT '默认字段1(抖音代码)',
  `default02` varchar(64) DEFAULT NULL COMMENT '默认字段2(拼多多代码)',
  `default03` varchar(64) DEFAULT NULL COMMENT '默认字段3',
  `default04` varchar(64) DEFAULT NULL COMMENT '默认字段4',
  `default05` varchar(64) DEFAULT NULL COMMENT '默认字段5',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY (`name`),
  UNIQUE KEY (`code`),
  UNIQUE KEY (`kjg_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='快递管理';

ALTER TABLE `bus_douyin_mail_mark` ADD COLUMN `province` varchar(32) DEFAULT NULL COMMENT '省' after consignee_address;
ALTER TABLE `bus_douyin_mail_mark` ADD COLUMN `city` varchar(32) DEFAULT NULL COMMENT '市' after province;
ALTER TABLE `bus_douyin_mail_mark` ADD COLUMN `district` varchar(32) DEFAULT NULL COMMENT '区' after city;
ALTER TABLE `bus_douyin_mail_mark` ADD COLUMN `consignee_addr` varchar(128) DEFAULT NULL COMMENT '收货地址' after district;
ALTER TABLE `bus_douyin_mail_mark` ADD COLUMN `supplier_id` bigint(20) DEFAULT NULL COMMENT '承运商ID' after shop_id;


DROP TABLE IF EXISTS  `bus_order_tax_account`;
CREATE TABLE `bus_order_tax_account` (
  `id` bigint(20)  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `lp_no` varchar(64) DEFAULT NULL COMMENT 'LP单号',
  `mail_no` varchar(64) DEFAULT NULL COMMENT '运单号',
  `invt_no` varchar(64) DEFAULT NULL COMMENT '总署清单编号',
  `add_tax` decimal(18,6) DEFAULT NULL COMMENT '增值税',
  `consumption_tax` decimal(18,6) DEFAULT NULL COMMENT '消费税',
  `tax_amount` decimal(18,6) DEFAULT NULL COMMENT '总税',
  `order_time` datetime DEFAULT NULL COMMENT '订单出库时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='订单税费对账';


DROP TABLE IF EXISTS  `bus_complaint`;
CREATE TABLE `bus_complaint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `logistics_no` varchar(32) DEFAULT NULL COMMENT '运单号',
  `status` int(11) NOT NULL COMMENT '状态',
  `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `shop_id` varchar(128) DEFAULT NULL COMMENT '店铺ID',
  `platform_code` varchar(64) DEFAULT NULL COMMENT '电商平台代码',


  `province` varchar(32) DEFAULT NULL COMMENT '省',
  `city` varchar(32) DEFAULT NULL COMMENT '市',
  `district` varchar(32) DEFAULT NULL COMMENT '区',
  `payment` varchar(10) DEFAULT NULL COMMENT '实付总价',
  `consignee_name` varchar(128) DEFAULT NULL COMMENT '收货人',
  `consignee_addr` varchar(128) DEFAULT NULL COMMENT '收货地址',
  `consignee_tel` varchar(32) DEFAULT NULL COMMENT '收货电话',
  `deliver_time` datetime DEFAULT NULL COMMENT '出库时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1222 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='跨境订单';

DROP TABLE IF EXISTS  `bus_complaint_details`;
CREATE TABLE `bus_complaint_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '货品ID',
  `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
  `font_goods_name` varchar(1024) DEFAULT NULL COMMENT '前端商品名称',
  `goods_name` varchar(1024) DEFAULT NULL COMMENT '商品名称',
  `qty` varchar(10) DEFAULT NULL COMMENT '数量',
  `payment` varchar(10) DEFAULT NULL COMMENT '实付总价',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3940 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='跨境订单明细';


ALTER TABLE `bus_logistics_inshops` ADD COLUMN `logistics_id` bigint(20) NOT NULL COMMENT '物流公司ID' after `ID`;



CREATE TABLE `bus_logistics_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(64) NOT NULL COMMENT '名称',
  `code` varchar(32) NOT NULL COMMENT '代码',
  `customs_name` varchar(64) DEFAULT NULL COMMENT '海关备案名称',
  `customs_code` varchar(32) DEFAULT NULL COMMENT '海关备案代码',
  `kjg_code` varchar(32) DEFAULT NULL COMMENT '跨境购代码',
  `kjg_name` varchar(32) DEFAULT NULL COMMENT '跨境购名称',
  `default01` varchar(64) DEFAULT NULL COMMENT '默认字段1(抖音代码)',
  `default02` varchar(64) DEFAULT NULL COMMENT '默认字段2(拼多多代码)',
  `default03` varchar(64) DEFAULT NULL COMMENT '默认字段3',
  `default04` varchar(64) DEFAULT NULL COMMENT '默认字段4',
  `default05` varchar(64) DEFAULT NULL COMMENT '默认字段5',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `kjg_code` (`kjg_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='快递管理';

-- eladmin.bus_logistics_inshops definition

CREATE TABLE `bus_logistics_inshops` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
 `logistics_id` bigint(20) NOT NULL COMMENT '物流公司ID',
 `logistics_name` varchar(32) DEFAULT NULL COMMENT '物流公司',
 `logistics_code` varchar(32) DEFAULT NULL COMMENT '物流代码',
 `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
 `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
 `name` varchar(50) DEFAULT NULL COMMENT '店铺名',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='店铺选择快递物流';

-- eladmin.bus_logistics_unarrive_place definition

CREATE TABLE `bus_logistics_unarrive_place` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `logistics_id` bigint(20) DEFAULT NULL COMMENT '物流ID',
    `logistics_name` varchar(32) DEFAULT NULL COMMENT '物流公司',
    `logistics_code` varchar(32) DEFAULT NULL COMMENT '物流代码',
    `province` varchar(32) DEFAULT NULL COMMENT '省',
    `city` varchar(32) DEFAULT NULL COMMENT '市',
    `district` varchar(32) DEFAULT NULL COMMENT '区',
    `reason` varchar(255) DEFAULT NULL COMMENT '原因',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 COMMENT='物流不可到区域';

ALTER TABLE `bus_gift_info` ADD COLUMN `gift_id` bigint(20) NOT NULL COMMENT '赠品ID' after `status`;
ALTER TABLE `bus_gift_info` ADD COLUMN `gift_no` varchar(32) NOT NULL COMMENT '赠品货号' after `status`;
ALTER TABLE `bus_gift_info` ADD COLUMN `main_no` varchar(32) default NULL COMMENT '主品货号' after `status`;
ALTER TABLE `bus_gift_info` ADD COLUMN `main_code` varchar(32) default NULL COMMENT '主品条码' after `status`;
ALTER TABLE `bus_gift_info` ADD COLUMN `main_name` varchar(128) default NULL COMMENT '主品名称' after `status`;
ALTER TABLE `bus_gift_info` ADD COLUMN `place_counts` int(10) NOT NULL COMMENT '放置数量' after `status`;

ALTER TABLE `bus_base_sku` ADD COLUMN `part_no` varchar(64) default NULL COMMENT '料号' after `goods_no`;
ALTER TABLE `bus_base_sku` ADD COLUMN `declare_element` text default NULL COMMENT '申报要素' after `property`;


ALTER TABLE `bus_cross_border_order` ADD COLUMN `order_msg` text DEFAULT NULL COMMENT '订单报文' after logistics_status;


ALTER TABLE `bus_daily_stock` ADD COLUMN `customers_name` varchar(128) DEFAULT NULL COMMENT '客户名称' after shop_id;
ALTER TABLE `bus_daily_stock` ADD COLUMN `shop_name` varchar(128) DEFAULT NULL COMMENT '店铺名称' after customers_name;
ALTER TABLE `bus_daily_stock` ADD COLUMN `stock_status` varchar(10) DEFAULT NULL COMMENT '商品状态' after batch_no;


ALTER TABLE `bus_daily_stock` ADD COLUMN `prod_time` varchar(64) DEFAULT NULL COMMENT '生产日期' after stock_status;
ALTER TABLE `bus_daily_stock` ADD COLUMN `exp_time` varchar(64) DEFAULT NULL COMMENT '失效日期' after prod_time;
ALTER TABLE `bus_daily_stock` ADD COLUMN `in_time` varchar(64) DEFAULT NULL COMMENT '入库日期' after exp_time;



CREATE TABLE `bus_data_order` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
     `order_no` varchar(64) NOT NULL COMMENT '订单号',
     `cross_border_no` varchar(64) DEFAULT NULL COMMENT '交易号',
     `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
     `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
     `customers_name` varchar(128) DEFAULT NULL COMMENT '客户名称',
     `shop_name` varchar(128) DEFAULT NULL COMMENT '店铺名称',
     `clear_company_id` bigint(20) DEFAULT NULL COMMENT '清关抬头ID',
     `clear_company_name` varchar(128) DEFAULT NULL COMMENT '清关抬头名称',
     `platform_code` varchar(64) DEFAULT NULL COMMENT '电商平台代码',
     `platform_name` varchar(64) DEFAULT NULL COMMENT '电商平台名称',
     `supplier_id` bigint(20) DEFAULT NULL COMMENT '承运商ID',
     `supplier_name` varchar(64) DEFAULT NULL COMMENT '承运商名称',
     `logistics_no` varchar(32) DEFAULT NULL COMMENT '运单号',
     `declare_no` varchar(64) DEFAULT NULL COMMENT '申报单号',
     `invt_no` varchar(64) DEFAULT NULL COMMENT '总署清单编号',
     `order_create_time` datetime DEFAULT NULL COMMENT '订单创建时间',
     `payment` varchar(10) DEFAULT NULL COMMENT '实付金额',
     `tariff_amount` varchar(10) DEFAULT NULL COMMENT '关税',
     `added_value_tax_amount` varchar(10) DEFAULT NULL COMMENT '增值税',
     `consumption_duty_amount` varchar(10) DEFAULT NULL COMMENT '消费税',
     `tax_amount` varchar(10) DEFAULT NULL COMMENT '总税额',
     `province` varchar(32) DEFAULT NULL COMMENT '省',
     `city` varchar(32) DEFAULT NULL COMMENT '市',
     `district` varchar(32) DEFAULT NULL COMMENT '区',
     `material_code` varchar(32) DEFAULT NULL COMMENT '包材编码',
     `four_pl` varchar(32) DEFAULT NULL COMMENT '抖音4PL单',
     `pack_weight` varchar(32) DEFAULT NULL COMMENT '包裹重量',
     `clear_start_time` datetime DEFAULT NULL COMMENT '清关开始时间',
     `clear_success_time` datetime DEFAULT NULL COMMENT '清关完成时间',
     `deliver_time` datetime DEFAULT NULL COMMENT '出库时间',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='数据订单';

CREATE TABLE `bus_data_order_detail` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
     `order_no` varchar(64) NOT NULL COMMENT '订单号',
     `cross_border_no` varchar(64) DEFAULT NULL COMMENT '交易号',
     `customers_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
     `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
     `customers_name` varchar(128) DEFAULT NULL COMMENT '客户名称',
     `shop_name` varchar(128) DEFAULT NULL COMMENT '店铺名称',
     `logistics_no` varchar(32) DEFAULT NULL COMMENT '运单号',
     `goods_id` bigint(20) DEFAULT NULL COMMENT '货品ID',
     `goods_code` varchar(64) DEFAULT NULL COMMENT '货品编码',
     `goods_no` varchar(64) DEFAULT NULL COMMENT '海关货号',
     `hs_code` varchar(64) DEFAULT NULL COMMENT 'HS编码',
     `font_goods_name` varchar(1024) DEFAULT NULL COMMENT '前端商品名称',
     `goods_name` varchar(1024) DEFAULT NULL COMMENT '商品名称',
     `qty` varchar(10) DEFAULT NULL COMMENT '数量',
     `payment` varchar(10) DEFAULT NULL COMMENT '实付总价',
     `dutiable_value` varchar(10) DEFAULT NULL COMMENT '完税单价',
     `dutiable_total_value` varchar(10) DEFAULT NULL COMMENT '完税总价',
     `tariff_amount` varchar(10) DEFAULT NULL COMMENT '关税',
     `added_value_tax_amount` varchar(10) DEFAULT NULL COMMENT '增值税',
     `consumption_duty_amount` varchar(10) DEFAULT NULL COMMENT '消费税',
     `consumable_material_code` varchar(32) DEFAULT NULL COMMENT '耗材编码',
     `consumable_material_num` varchar(32) DEFAULT NULL COMMENT '耗材数量',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='数据订单明细';


CREATE TABLE `bus_return_gather` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gather_no` varchar(64) NOT NULL COMMENT '提总单号',
  `wms_no` varchar(64) DEFAULT NULL COMMENT 'WMS单号',
  `customers_id` bigint(20) NOT NULL COMMENT '客户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `customers_name` varchar(128) NOT NULL COMMENT '客户名称',
  `shop_name` varchar(128) NOT NULL COMMENT '店铺名称',
  `sku_num` integer NOT NULL COMMENT 'SKU数量',
  `total_num` integer NOT NULL COMMENT '总数量',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `pre_handle_time` datetime DEFAULT NULL COMMENT '预处理完成时间',
  `close_time` datetime DEFAULT NULL COMMENT '关单时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='退货提总单';

CREATE TABLE `bus_return_gather_detail` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
 `gather_id` bigint(20) NOT NULL COMMENT '提总单ID',
 `gather_no` varchar(64) NOT NULL COMMENT '提总单号',
 `goods_no` varchar(64) NOT NULL COMMENT '货号',
 `qty` integer NOT NULL COMMENT '数量',
 `goods_name` varchar(256) NOT NULL COMMENT '名称',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='退货提总明细';


ALTER TABLE `bus_order_return` ADD COLUMN `four_pl` varchar(32) DEFAULT NULL COMMENT '抖音4PL单';

ALTER TABLE `bus_cross_border_order` ADD COLUMN `logistics_four_pl` varchar(32) DEFAULT NULL COMMENT '快递4PL' after `four_pl`;

ALTER TABLE `bus_package_info` ADD COLUMN `add_value` varchar(10) DEFAULT NULL COMMENT '是否增值' after `weight`;


