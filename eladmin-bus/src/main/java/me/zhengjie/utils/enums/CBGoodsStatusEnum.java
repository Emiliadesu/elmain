package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跨境商品状态
 */
@Getter
@AllArgsConstructor
public enum CBGoodsStatusEnum {

    STATUS_100(100, "创建"),//创建货品，导入或者通过接口传入

    STATUS_110(110, "审核不过"),// 审核不过

    STATUS_115(115, "申报成功"),

    STATUS_130(130, "备案完成");

    private final Integer code;
    private final String description;
}
