package me.zhengjie.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author 王淼
 * @date 2020年12月18日13:58:45
 */
@Getter
@Setter
public class WmsInstockSmallDto implements Serializable {
    private Integer id;

    /** 入库单号 */
    private String inOrderSn;
}
