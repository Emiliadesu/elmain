package me.zhengjie.service.dto;

import lombok.Data;

/**
 * @author luob
 * @description
 * @date 2022/4/15
 */
@Data
public class OrderReturnDetailsOutDto {

    /** 货品编码 */
    private String goodsCode;

    /** 海关货号 */
    private String goodsNo;

    /** 商品名称 */
    private String goodsName;

    /** 数量 */
    private String qty;
}
