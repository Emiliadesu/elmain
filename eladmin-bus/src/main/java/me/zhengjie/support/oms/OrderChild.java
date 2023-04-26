package me.zhengjie.support.oms;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author luob
 * @description
 * @date 2022/2/13
 */
@Data
public class OrderChild {

    /**
     * 货号
     */
    @JSONField(name = "goodsNo")
    @NotBlank
    private String goodsNo;

    /**
     * 数量
     */
    @NotNull
    @JSONField(name = "qty")
    private Long qty;

    /**
     * 申报单价
     */
    @NotNull
    @JSONField(name = "declarePrice")
    private BigDecimal declarePrice;

    /**
     * 税费
     */
    @NotNull
    @JSONField(name = "tax")
    private BigDecimal tax;

}
