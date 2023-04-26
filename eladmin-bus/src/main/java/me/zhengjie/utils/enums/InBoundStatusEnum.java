package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zhengjie.support.douyin.CommonTokenCreateRequest;

/**
 * 入库单流转状态
 */
@Getter
@AllArgsConstructor
public enum InBoundStatusEnum {

    STATUS_600(600, "创建"),

    STATUS_615(615, "接单确认"),// 此状态会推单到WMS

    STATUS_620(620, "到货"),

    STATUS_625(625, "到货回传"),

    STATUS_630(630, "理货开始"),

    STATUS_635(635, "理货开始回传"),

    STATUS_640(640, "理货完成"),

    STATUS_645(645, "理货完成回传"),

    STATUS_646(646, "待理货审核"),

    STATUS_647(647, "理货审核通过"),

    STATUS_648(648, "理货审核拒绝"),

    STATUS_650(650, "收货完成"),

    STATUS_655(655, "收货完成回传"),

    STATUS_888(888, "取消"),
    STATUS_999(999, "冻结");

    private final Integer code;
    private final String description;

    public static String getDesc(Integer code) {
        for (InBoundStatusEnum inBoundStatusEnum : InBoundStatusEnum.values()) {
            if (inBoundStatusEnum.getCode().intValue() == code.intValue()) {
                return inBoundStatusEnum.getDescription();
            }
        }
        return null;
    }
}


