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

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-12-04
**/
@Entity
@Data
@Table(name="bus_wms_outstock")
public class WmsOutstock implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "so_no")
    @ApiModelProperty(value = "so订单号")
    private String soNo;

    @Column(name = "out_order_sn",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "出库单号")
    private String outOrderSn;

    @Column(name = "flux_order_no")
    @ApiModelProperty(value = "出库单号")
    private String fluxOrderNo;

    @Column(name = "out_order_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "单据类型")
    private String outOrderType;

    @Column(name = "receiver")
    @ApiModelProperty(value = "出库联系人")
    private String receiver;

    @Column(name = "phone")
    @ApiModelProperty(value = "出库联系电话")
    private String phone;

    @Column(name = "address")
    @ApiModelProperty(value = "配送地址")
    private String address;

    @Column(name = "transport_way")
    @ApiModelProperty(value = "运输方式")
    private String transportWay;

    @Column(name = "pallet")
    @ApiModelProperty(value = "托盘要求")
    private String pallet;

    @Column(name = "palletized_height")
    @ApiModelProperty(value = "打托高度")
    private String palletizedHeight;

    @Column(name = "expect_tally_time")
    @ApiModelProperty(value = "预期理货完成时间(yyyy-MM-dd HH:mm:ss)")
    private String expectTallyTime;

    @Column(name = "expect_ship_time")
    @ApiModelProperty(value = "预期出库时间")
    private String expectShipTime;

    @Column(name = "tally_way")
    @ApiModelProperty(value = "理货维度")
    private String tallyWay;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "file_link")
    @ApiModelProperty(value = "附件地址(理货要求等)")
    private String fileLink;

    @Column(name = "create_time",nullable = false)
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "create_customer",nullable = false)
    @ApiModelProperty(value = "创建商家")
    private String createCustomer;

    @Column(name = "out_status",nullable = false)
    @ApiModelProperty(value = "出库通知单状态")
    private String outStatus;

    @Column(name = "goods_upper")
    @ApiModelProperty(value = "出库时间")
    private Timestamp goodsUpper;

    @Column(name = "status_time",nullable = false)
    @ApiModelProperty(value = "状态最后变化时间戳")
    private Long statusTime;

    @Column(name = "sync_complete",nullable = false)
    @ApiModelProperty(value = "同步流程是否完成")
    private Boolean syncComplete;

    @Column(name = "channel")
    @ApiModelProperty(value = "渠道")
    private String channel;

    @Column(name = "in_tally_sn")
    @ApiModelProperty(value = "入库理货单号-调拨单完成之前必须有")
    private String inTallySn;

    @Column(name = "out_tally_sn")
    @ApiModelProperty(value = "出库理货单号")
    private String outTallySn;

    @Transient
    private ShopInfo shopInfo;

    @Column(name = "shop_info_id")
    private Long shopId;

    @Transient
    @JSONField(name = "skuDetails")
    private List<WmsOutstockItem> itemList;

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

    @Column(name = "load_no")
    @ApiModelProperty(value = "装载单号")
    private String loadNo;

    @Column(name = "enable_stock")
    @ApiModelProperty(value = "是否允许出库")
    private String enableStock;

    public void copy(WmsOutstock source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
