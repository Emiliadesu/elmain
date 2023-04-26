package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跨境商品状态
 */
@Getter
@AllArgsConstructor
public enum DYOrderStatusEnum {

    STATUS_100(100, "支付单申报中"),

    STATUS_101(101, "支付单申报成功"),

    STATUS_102(102, "支付单申报失败"),//

    STATUS_111(111, "服务商接单成功"),

    STATUS_122(122, "已发货"),

    STATUS_131(131, "清关中"),

    STATUS_132(132, "清关成功"),

//    STATUS_132(132, "拣货开始"),
//
//    STATUS_132(132, "拣货完成"),

    STATUS_133(133, "清关失败");

    private final Integer code;
    private final String description;
}
