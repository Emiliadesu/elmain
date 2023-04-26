package me.zhengjie.support.oms;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author luob
 * @description
 * @date 2022/2/13
 */
@Data
public class OrderMain {

    /**
     * 订单编号
     */
    @JSONField(name = "orderNo")
    @NotBlank
    private String orderNo;

    /**
     * 店铺编码
     */
    @NotBlank
    @JSONField(name = "shopCode")
    private String shopCode;

    /**
     * 清关订单号
     */
    @NotBlank
    @JSONField(name = "declareNo")
    private String declareNo;

    /**
     * 购物网站编码
     */
    @NotBlank
    @JSONField(name = "platformCode")
    private String platformCode;

    /**
     * 订单创建时间
     */
    @NotBlank
    @JSONField(name = "createTime")
    private String createTime;

    /**
     * 购物网站买家账号
     */
    @JSONField(name = "buyerAccount")
    private String buyerAccount;

    /**
     * 订购人姓名
     */
    @NotBlank
    @JSONField(name = "buyerName")
    private String buyerName;

    /**
     * 订购人电话
     */
    @NotBlank
    @JSONField(name = "buyerPhone")
    private String buyerPhone;

    /**
     * 订购人身份证号码
     */
    @NotBlank
    @JSONField(name = "buyerIdNum")
    private String buyerIdNum;

    /**
     * 订单总金额(商品总金额+运费+税费-优惠)
     */
    @NotNull
    @JSONField(name = "totalAmount")
    private BigDecimal totalAmount;


    /**
     * 订单实付金额
     */
    @NotNull
    @JSONField(name = "payment")
    private BigDecimal payment;

    /**
     * 运费(若无传0)
     */
    @NotNull
    @JSONField(name = "fee")
    private BigDecimal fee;

    /**
     * 是否预售，1-是 0-否
     */
    @JSONField(name = "preSell", defaultValue = "0")
    private String preSell;

    /**
     * 预计出库时间
     */
    @JSONField(name = "expectDeliverTime")
    private String expectDeliverTime;

    /**
     * 税费
     */
    @NotNull
    @JSONField(name = "taxAmount")
    private BigDecimal taxAmount;

    /**
     * 优惠金额
     */
    @NotNull
    @JSONField(name = "discount")
    private BigDecimal discount;

    /**
     * 收件人姓名
     */
    @NotBlank
    @JSONField(name = "consigneeName")
    private String consigneeName;

    /**
     * 收件人电话
     */
    @NotBlank
    @JSONField(name = "consigneePhone")
    private String consigneePhone;

    /**
     * 省
     */
    @NotBlank
    @JSONField(name = "province")
    private String province;

    /**
     * 市
     */
    @NotBlank
    @JSONField(name = "city")
    private String city;

    /**
     * 区
     */
    @NotBlank
    @JSONField(name = "district")
    private String district;

    /**
     * 详细地址
     */
    @NotBlank
    @JSONField(name = "address")
    private String address;

    /**
     * 支付流水号
     */
    @NotBlank
    @JSONField(name = "paymentNo")
    private String paymentNo;

    /**
     * 支付时间
     */
    @NotBlank
    @JSONField(name = "payTime")
    private String payTime;

    /**
     * 支付方式,ALIPAY-支付宝、WEIXIN-微信支付
     */
    @NotBlank
    @JSONField(name = "payType")
    private String payType;

    @Valid
    @JSONField(name = "skuDetails")
    private List<OrderChild> skuDetails;

}
