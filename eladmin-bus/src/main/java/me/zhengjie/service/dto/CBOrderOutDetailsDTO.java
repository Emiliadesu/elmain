package me.zhengjie.service.dto;

import lombok.Data;

@Data
public class CBOrderOutDetailsDTO {

    /** 货品编码 */
    private String goodsCode;

    /** 海关货号 */
    private String goodsNo;

    private String fontGoodsName;

    /** 商品名称 */
    private String goodsName;

    /** 实付总价 */
    private String payment;

    /** 数量 */
    private String qty;

    private String dutiableValue;

    private String taxAmount;
}
