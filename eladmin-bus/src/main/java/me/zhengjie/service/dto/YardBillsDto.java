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

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-16
**/
@Data
public class YardBillsDto implements Serializable {

    private Long id;

    /** 箱号 */
    private String boxNum;

    /** 船名航次 */
    private String flight;

    /** 客户 */
    private String customer;

    /** 联系电话 */
    private String telPhone;

    /** 进场日期 */
    private String inDate;

    /** 出场日期 */
    private String outDate;

    /** 落箱费 */
    private String dropBox;

    /** 滞箱费 */
    private String demurrage;

    /** 还箱费 */
    private String refund;

    /** 附加费 */
    private String addCost;

    /** 费用总计 */
    private String total;

    /** 联系场地 */
    private String sendAddress;

    /** 场地联系电话 */
    private String sendTel;

    /** 凭据添加时间 */
    private Timestamp createTime;
}