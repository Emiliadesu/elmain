package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 国内订单流转状态
 */
@Getter
@AllArgsConstructor
public enum DomesticOrderStatusEnum {

    STATUS_200(200, "接单"),//从平台拉单，或者开放接口接收订单

    STATUS_215(215, "接单回传"),

    STATUS_220(220, "审核通过"),

    STATUS_235(235, "预处理完成"),

    STATUS_240(240, "称重完成"),// 需要查询平台状态是否允许出库

    STATUS_245(245, "出库"),// 回传给平台已打包

    STATUS_888(888, "取消"),// 订单取消

    STATUS_999(999, "冻结");// 违规订单进入冻结状态

    private final Integer code;
    private final String description;

    public static String getDesc(Integer code) {
        for (DomesticOrderStatusEnum cbOrderStatusEnum : DomesticOrderStatusEnum.values()) {
            if (cbOrderStatusEnum.getCode().intValue() == code.intValue()) {
                return cbOrderStatusEnum.getDescription();
            }
        }
        return null;
    }
}
