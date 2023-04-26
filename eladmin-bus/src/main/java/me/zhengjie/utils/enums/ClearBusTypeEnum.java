package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zhengjie.utils.StringUtils;

/**
 * 跨境订单流转状态
 */
@Getter
@AllArgsConstructor
public enum ClearBusTypeEnum {

    TYPE_01("01", "跨境一线进境"),

    TYPE_02("02", "入区区间结转"),

    TYPE_03("03", "入区区内结转"),

    TYPE_04("04", "出区区间结转"),

    TYPE_05("05", "出区区内结转"),

    TYPE_06("06", "退运出境"),

    TYPE_07("07", "销毁"),

    TYPE_08("08", "一般贸易进口"),

    TYPE_09("09", "一般贸易出口"),

    TYPE_10("10", "转口");

    private final String code;
    private final String description;

    public static String getDesc(String code) {
        for (ClearBusTypeEnum cbOrderStatusEnum : ClearBusTypeEnum.values()) {
            if (StringUtils.equals(cbOrderStatusEnum.getCode(), code)) {
                return cbOrderStatusEnum.getDescription();
            }
        }
        return null;
    }
}
