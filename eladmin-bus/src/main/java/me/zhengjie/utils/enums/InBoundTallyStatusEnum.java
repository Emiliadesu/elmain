package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 入库单流转状态
 */
@Getter
@AllArgsConstructor
public enum InBoundTallyStatusEnum {

    STATUS_700(700, "理货开始"),

    STATUS_715(715, "审核中"),

    STATUS_720(720, "审核通过"),

    STATUS_725(725, "审核不过"),

    STATUS_730(730, "理货完成");

    private final Integer code;
    private final String description;
}
