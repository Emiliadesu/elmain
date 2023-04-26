package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跨境退货单状态
 */
@Getter
@AllArgsConstructor
public enum CBReturnOrderStatusEnum {

    STATUS_300(300, "接单"),//创建货品，导入或者通过接口传入

    STATUS_305(305, "已收货"),

    STATUS_310(310, "收货回传"),

    STATUS_315(315, "质检完成"),

    STATUS_317(317, "质检完成回传"),

    STATUS_325(325, "清关开始"),

    STATUS_330(330, "清关开始回传"),

    STATUS_335(335, "清关完成"),

    STATUS_337(337, "清关完成回传"),

    STATUS_340(340, "清关异常"),

    STATUS_345(345, "清关异常回传"),

    STATUS_350(350, "已汇波"),

    STATUS_355(355, "预处理完成"),

    STATUS_900(900, "关单"),

    STATUS_888(888, "取消");

    private final Integer code;

    private final String description;

    public static String getDesc(Integer code) {
        for (CBReturnOrderStatusEnum cbReturnOrderStatusEnum : CBReturnOrderStatusEnum.values()) {
            if (cbReturnOrderStatusEnum.getCode().intValue() == code.intValue()) {
                return cbReturnOrderStatusEnum.getDescription();
            }
        }
        return null;
    }
}
