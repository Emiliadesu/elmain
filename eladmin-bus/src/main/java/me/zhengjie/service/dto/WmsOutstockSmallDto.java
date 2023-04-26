package me.zhengjie.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 王淼
 * @date 2020年12月18日13:58:45
 */
@Data
public class WmsOutstockSmallDto implements Serializable {
    private Integer id;

    /** 出库单号 */
    private String outOrderSn;
}
