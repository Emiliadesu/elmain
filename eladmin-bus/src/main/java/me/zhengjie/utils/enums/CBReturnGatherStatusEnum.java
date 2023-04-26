package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跨境退货单汇总状态
 */
@Getter
@AllArgsConstructor
public enum CBReturnGatherStatusEnum {

    STATUS_300(300, "创建"),

    STATUS_315(315, "预处理完成"),

    STATUS_320(320, "关单");

    private final Integer code;

    private final String description;

    public static String getDesc(Integer code) {
        for (CBReturnGatherStatusEnum cbReturnOrderStatusEnum : CBReturnGatherStatusEnum.values()) {
            if (cbReturnOrderStatusEnum.getCode().intValue() == code.intValue()) {
                return cbReturnOrderStatusEnum.getDescription();
            }
        }
        return null;
    }
}
