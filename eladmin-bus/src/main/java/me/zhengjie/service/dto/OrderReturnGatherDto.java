package me.zhengjie.service.dto;

import lombok.Data;
import me.zhengjie.domain.OrderReturn;

/**
 * @author luob
 * @description
 * @date 2022/4/26
 */
@Data
public class OrderReturnGatherDto extends OrderReturn {

    private String ids;

    private String goodsNo;

    private String barCode;

    private String goodsName;

    private Integer qty;
}
