package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 入库单流转状态
 */
@Getter
@AllArgsConstructor
public enum OutBoundStatusEnum {

    STATUS_700(700, "创建"),

    STATUS_715(715, "接单确认"),// 此状态会推单到WMS

    STATUS_730(730, "理货开始"),

    STATUS_735(735, "理货开始回传"),

    STATUS_740(740, "理货完成"),

    STATUS_745(745, "理货完成回传"),

    STATUS_746(746, "理货代审核"),

    STATUS_747(747, "理货审核通过"),

    STATUS_748(748, "理货审核拒绝"),

    STATUS_750(750, "出库完成"),

    STATUS_755(755, "出库完成回传"),

    STATUS_888(888, "取消"),

    STATUS_999(999, "冻结");

    private final Integer code;
    private final String description;

    public static String getDesc(Integer code) {
        for (OutBoundStatusEnum outBoundStatusEnum : OutBoundStatusEnum.values()) {
            if (outBoundStatusEnum.getCode().intValue() == code.intValue()) {
                return outBoundStatusEnum.getDescription();
            }
        }
        return null;
    }
}
