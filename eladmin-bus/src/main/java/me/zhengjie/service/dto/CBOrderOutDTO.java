package me.zhengjie.service.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 外部查询
 */
@Data
public class CBOrderOutDTO {

    /** 订单号 */
    private String orderNo;

    private Integer status;

    /** 运单号 */
    private String logisticsNo;

    private String logisticsName = "中通速递";

    /** 实付金额 */
    private String payment;

    private String shopCode;

    private String shopName;

    /** 出库时间 */
    private String deliverTime;

    private String orderCreateTime;

    private String consigneeName;

    private String consigneeTel;

    private String province;

    private String city;

    private String district;

    private String consigneeAddr;

    private String payCode;

    private String payTime;

    private String postFee;

    private String taxAmount;

    private String disAmount;

    private String declareNo;

    private String declareStatus;

    private String declareMsg;

    private List<CBOrderOutDetailsDTO> itemList;
}
