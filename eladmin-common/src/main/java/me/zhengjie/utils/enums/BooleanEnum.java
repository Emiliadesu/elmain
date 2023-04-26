package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BooleanEnum {

    /* 旧邮箱修改邮箱 */
    SUCCESS("1", "成功"),

    /* 通过邮箱修改密码 */
    FAIL("0", "失败");

    private final String code;
    private final String description;


}
