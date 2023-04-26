package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跨境订单流转状态
 */
@Getter
@AllArgsConstructor
public enum CBOrderStatusEnum {

    STATUS_200(200, "接单"),//从平台拉单，或者开放接口接收订单

    STATUS_201(201, "接单异常回传"),

    STATUS_205(205, "支付单推送"),//给推送支付单

    STATUS_215(215, "接单回传"),// 给有接单回传需求的平台、无需求直接置为215

    STATUS_220(220, "清关开始"),// 获取申报所需信息(备案信息、税费计算、运单号)，申报海关

    STATUS_225(225, "清关开始回传"),// 给有清关开始回传回传需求的平台、无需求直接置为225

    STATUS_227(227, "清关异常回传"),

    STATUS_230(230, "清关完成"),// 查询海关单证放行状态

    STATUS_235(235, "清关完成回传"),// 回传有清关放行需求的平台，无需求直接置为235

    STATUS_236(236, "拣货开始回传"),

    STATUS_2361(2361, "拣货完成回传"),

    STATUS_237(237, "打包完成"),// 回传打包完成

    STATUS_240(240, "称重完成"),// 需要查询平台状态是否允许出库

    STATUS_245(245, "出库"),// 回传给平台已打包

    STATUS_880(880, "开始撤单"),// 订单撤单开始

    STATUS_882(882, "撤单失败"),// 订单撤单失败

    STATUS_884(884, "撤单成功"),// 订单撤单成功

    STATUS_886(886, "关单成功"),// 订单关单成功

    STATUS_888(888, "取消"),// 订单取消

    STATUS_999(999, "冻结"),// 违规订单进入冻结状态

    STATUS_777(777, "预售等待");// 预售等待

    private final Integer code;
    private final String description;

    public static String getDesc(Integer code) {
        for (CBOrderStatusEnum cbOrderStatusEnum : CBOrderStatusEnum.values()) {
            if (cbOrderStatusEnum.getCode().intValue() == code.intValue()) {
                return cbOrderStatusEnum.getDescription();
            }
        }
        return null;
    }
}
