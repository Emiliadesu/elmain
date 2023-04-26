package me.zhengjie.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HomeDto implements Serializable {

    //总单量
    private Integer orderTotalCount;

    //已发单量
    private Integer orderDeliverCount;

    //未发单量
    private Integer orderWaitCount;

    //取消单量
    private Integer orderCancelCount;
}
