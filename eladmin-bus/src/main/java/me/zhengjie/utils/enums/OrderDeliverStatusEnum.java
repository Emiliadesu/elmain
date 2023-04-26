package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跨境商品状态
 */
@Getter
@AllArgsConstructor
public enum OrderDeliverStatusEnum {

    ZTO("ZTO", "中通"),

    YUNDA("YUNDA", "韵达"),

    SF("SF", "顺丰"),

    EMS("EMS", "EMS"),

    JD("JD", "京东"),

    CHECK("CHECK", "抽检"),

    ERROR("ERROR", "系统异常"),

    CANCEL("ERROR", "订单已取消");

    private final String code;
    private final String description;
}
