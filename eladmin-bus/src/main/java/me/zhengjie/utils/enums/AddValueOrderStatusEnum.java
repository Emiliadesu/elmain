package me.zhengjie.utils.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 枚举备注
 */
@Getter
@AllArgsConstructor
public enum AddValueOrderStatusEnum {

    STATUS_900(900, "创建"),

    STATUS_945(945, "完成");

    private final Integer code;
    private final String description;
}
