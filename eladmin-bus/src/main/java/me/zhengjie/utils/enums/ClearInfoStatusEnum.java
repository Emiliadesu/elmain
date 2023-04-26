package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zhengjie.utils.StringUtils;

/**
 * 跨境订单流转状态
 */
@Getter
@AllArgsConstructor
public enum ClearInfoStatusEnum {

    STATUS_CREATE("CREATE", "创建"),

    STATUS_SUB_DATA("SUB_DATA", "提交资料"),

    STATUS_GOODS_ARRIVE("GOODS_ARRIVE", "货物到港"),

    STATUS_CLEAR_START("CLEAR_START", "清关开始"),

    STATUS_DRAFT_PASS("DRAFT_PASS", "概报放行"),

    STATUS_CLEAR_PASS("CLEAR_PASS", "清关完成"),

    STATUS_CHECK("CHECK", "查验"),

    STATUS_DONE("DONE", "服务完成");

    private final String code;
    private final String description;

    public static String getDesc(String code) {
        for (ClearInfoStatusEnum cbOrderStatusEnum : ClearInfoStatusEnum.values()) {
            if (StringUtils.equals(cbOrderStatusEnum.getCode(), code)) {
                return cbOrderStatusEnum.getDescription();
            }
        }
        return null;
    }
}
