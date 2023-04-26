package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跨境订单流转状态
 */
@Getter
@AllArgsConstructor
public enum HeZhuInfoStatusEnum {

    STATUS_800(800, "创建"),

    STATUS_815(815, "清关开始"),

    STATUS_820(820, "申报中"),

    STATUS_825(825, "清关完成"),

    STATUS_830(830, "服务完成");

    private final Integer code;
    private final String description;

    public static String getDesc(Integer code) {
        for (HeZhuInfoStatusEnum cbOrderStatusEnum : HeZhuInfoStatusEnum.values()) {
            if (cbOrderStatusEnum.getCode().intValue() ==  code.intValue()) {
                return cbOrderStatusEnum.getDescription();
            }
        }
        return null;
    }
}
