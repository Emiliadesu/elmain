package me.zhengjie.service.dto;

import lombok.Data;

import java.util.List;

/**
 * @author luob
 * @description
 * @date 2022/4/15
 */
@Data
public class OrderReturnOutDto {

    /** 订单号 */
    private String orderNo;

    private String tradeReturnNo;

    private Integer status;

    private String shopCode;

    private String shopName;

    private String sExpressNo;

    private String checkResult;

    private String checkType;

    private String isBorder;

    private String takeTime;

    private String checkTime;

    private String declareEndTime;

    private String closeTime;

    private List<OrderReturnDetailsOutDto> itemList;
}
