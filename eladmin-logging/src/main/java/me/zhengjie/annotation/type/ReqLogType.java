package me.zhengjie.annotation.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跨境商品状态
 */
@Getter
@AllArgsConstructor
public enum ReqLogType {

    KJG_DEC_ORDER("KJG_DEC_ORDER", "跨境购订单申报"),

    KJG_DEC_CANCEL("KJG_DEC_CANCEL", "跨境购订单取消"),

    KJG_DELIVER("KJG_DELIVER", "跨境购订单出库"),

    DOUYIN_PULL_ORDER("DOUYIN_PULL_ORDER", "抖音拉单"),

    DOUYIN_CONFIRM_ORDER("DOUYIN_CONFIRM_ORDER", "抖音接单回传"),

    DOUYIN_CONFIRM_DEC_START("DOUYIN_CONFIRM_DEC_START", "抖音清关开始回传"),

    DOUYIN_CONFIRM_DEC_END("DOUYIN_CONFIRM_DEC_END", "抖音清关完成回传"),

    DOUYIN_CONFIRM_PACK("DOUYIN_CONFIRM_PACK", "抖音打包回传"),

    DOUYIN_CONFIRM_DELIVER("DOUYIN_CONFIRM_DELIVER", "抖音出库回传"),

    DEWU_PACK_INO("DEWU_PACK_INO", "得物回传包裹信息"),;

    private final String code;
    private final String description;
}
