package me.zhengjie.utils.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 枚举备注
 */
@Getter
@AllArgsConstructor
public enum AddValueOrderTypeEnum {

    STATUS_100(100, "雀巢贴标"),

    STATUS_110(110, "普通加工"),

    STATUS_115(115, "残转良"),

    STATUS_120(120, "来货加工"),

    STATUS_125(125, "计费"),

    STATUS_130(130, "其他");

    private final Integer code;
    private final String description;
}
