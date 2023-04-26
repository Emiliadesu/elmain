/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-09-04
**/
@Data
public class TransDetailsDto implements Serializable {

    /** ID */
    private Long id;

    private Long orderId;

    private String orderNo;

    /** 箱号 */
    private String containerNo;

    /** 箱型 */
    private String containerType;

    /** 排车方 */
    private String planCarType;

    /** 车牌 */
    private String plateNo;

    /** 车型 */
    private String carType;

    /** 是否拼车 */
    private String shareFlag;

    /** 打包方式 */
    private String packWay;

    /** 打包数量 */
    private Integer packNum;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Timestamp updateTime;
}