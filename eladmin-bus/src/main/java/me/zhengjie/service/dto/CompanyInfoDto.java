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
* @author luob
* @date 2020-10-09
**/
@Data
public class CompanyInfoDto implements Serializable {

    /** ID */
    private Long companyId;

    /** 商家名称 */
    private String companyName;

    /** 业务类别 */
    private String busType;

    /** 进仓库区 */
    private String inArea;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Timestamp updateTime;
}
