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
package me.zhengjie.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import me.zhengjie.base.BaseUpdateEntity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @website https://el-admin.vip
 * @description /
 * @author luob
 * @date 2020-10-09
 **/
@Entity
@Data
@Table(name="bus_clear_company_info")
public class ClearCompanyInfo extends BaseUpdateEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clear_company_id")
    @ApiModelProperty(value = "ID")
    private Long clearCompanyId;

    @Column(name = "clear_company_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商家名称")
    private String clearCompanyName;

    @Column(name = "kjg_user")
    @ApiModelProperty(value = "跨境购用户名")
    private String kjgUser;

    @Column(name = "kjg_key")
    @ApiModelProperty(value = "跨境购秘钥")
    private String kjgKey;

    @Column(name = "customs_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "海关编码")
    private String customsCode;

    public void copy(ClearCompanyInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
