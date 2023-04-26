package me.zhengjie.utils.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务类型枚举
 */
@Getter
@AllArgsConstructor
public enum BusTypeEnum {

    MAIL_GSF("MAIL_GSF", "OUT", "顺丰国际获取面单"),

    MAIL_EMS("MAIL_EMS", "OUT", "邮政获取面单"),

    DEC_EMS("DEC_EMS", "OUT", "邮政运单申报"),

    MAIL_JD("MAIL_JD", "OUT", "京东获取面单"),

    DEC_JD("DEC_JD", "OUT", "京东运单申报"),

    DOUYIN_OUT("DOUYIN_OUT", "OUT", "抖音请求"),

    DOUYIN_RETURN("DOUYIN_RETURN", "IN", "抖音退货下发"),

    DOUYIN_MSG("DOUYIN_MSG", "IN", "抖音消息推送"),

    KJG_MSG("KJG_MSG", "IN", "跨境购消息推送"),

    KJG_API("KJG_API", "IN", "跨境购消息推送1"),

    MAIL_GZTO("MAIL_GZTO", "OUT", "中通国际获取面单"),

    MAIL_DMZTO("MAIL_DMZTO", "OUT", "中通国际获取面单"),

    CNLINK_OUT("CNLINK_OUT", "OUT", "菜鸟请求");

    private final String type;//类型
    private final String direction;// 方向
    private final String description;// 描述
}
