/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.domain;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-12-08
**/
@Entity
@Getter
@Setter
@Table(name="bus_wms_instock")
public class WmsInstock implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id", hidden = true)
    private Long id;

    @Column(name = "asn_no")
    @ApiModelProperty(value = "asn订单号")
    private String asnNo;

    @Column(name = "in_order_sn",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "入库单号，商家自行生成的唯一单号")
    private String inOrderSn;

    @Column(name = "in_order_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "单据类型，0:采购入库 1:调拨入库 2:销退入库")
    private String inOrderType;

    @Column(name = "original_no")
    @ApiModelProperty(value = "原单号(销退入库需填写)")
    private String originalNo;

    @Column(name = "declare_no")
    @ApiModelProperty(value = "报关单号")
    private String declareNo;

    @Column(name = "inspect_no")
    @ApiModelProperty(value = "报检单号")
    private String inspectNo;

    @Column(name = "expect_arrive_time")
    @ApiModelProperty(value = "预期到货时间(yyyy-MM-dd HH:mm:ss)")
    private String expectArriveTime;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "tally_way")
    @ApiModelProperty(value = "理货维度：0:件1:箱2:拖。暂时默认件")
    private String tallyWay;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "create_customer")
    @ApiModelProperty(value = "创建商家")
    private String createCustomer;

    @Column(name = "in_status")
    @ApiModelProperty(value = "入库通知单状态")
    private String inStatus;

    @Column(name = "status_time")
    @ApiModelProperty(value = "状态最后变化时间戳")
    private Long statusTime;

    @Column(name = "goods_upper")
    @ApiModelProperty(value = "收货完成时间")
    private Timestamp goodsUpper;

    @Column(name = "actual_arrive_time")
    @ApiModelProperty(value = "实际到货时间")
    private Timestamp actualArriveTime;

    @Column(name = "sync_complete")
    @ApiModelProperty(value = "同步流程是否完成")
    private Boolean syncComplete;

    @Column(name = "file_links")
    @ApiModelProperty(value = "附件链接")
    private String fileLinks;

    @Column(name = "channel")
    @ApiModelProperty(value = "channel")
    private String channel;

    @Transient
    private ShopInfo shopInfo;

    @Column(name = "shop_info_id")
    private Long shopId;

    @Transient
    @JSONField(name = "skuDetails")
    private List<WmsInstockItem> itemList;

    @Column(name = "po_no")
    @ApiModelProperty(value = "po单号")
    private String poNo;

    @Column(name = "merchant_id")
    @ApiModelProperty(value = "货主企业编码")
    private String merchantId;

    @Column(name = "warehouse_id")
    @ApiModelProperty(value = "仓库代码")
    private String warehouseId;

    @Column(name = "account_number")
    @ApiModelProperty(value = "账册编码")
    private String accountNumber;

    @Column(name = "supervise_code")
    @ApiModelProperty(value = "监管代码")
    private String superviseCode;

    @Column(name = "tenant_code")
    @ApiModelProperty(value = "租户编码")
    private String tenantCode;

    public void copy(WmsInstock source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
