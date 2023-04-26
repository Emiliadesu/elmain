package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zhengjie.utils.StringUtils;

/**
 * 跨境订单流转状态
 */
@Getter
@AllArgsConstructor
public enum ClearTransStatusEnum {

    STATUS_200(200, "待排车"),

    STATUS_215(215, "已排车"),

    STATUS_220(220, "已提货"),

    STATUS_225(225, "已到仓"),

    STATUS_230(230, "服务完成");

    private final Integer code;
    private final String description;

    public static String getDesc(Integer code) {
        for (ClearTransStatusEnum cbOrderStatusEnum : ClearTransStatusEnum.values()) {
            if (cbOrderStatusEnum.getCode().intValue() == code.intValue()) {
                return cbOrderStatusEnum.getDescription();
            }
        }
        return null;
    }
}
