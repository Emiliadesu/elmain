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
package me.zhengjie.repository;

import me.zhengjie.domain.LogisticsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-12-02
**/
public interface LogisticsInfoRepository extends JpaRepository<LogisticsInfo, Long>, JpaSpecificationExecutor<LogisticsInfo> {
    /**
    * 根据 Name 查询
    * @param name /
    * @return /
    */
    LogisticsInfo findByName(String name);
    /**
    * 根据 Code 查询
    * @param code /
    * @return /
    */
    LogisticsInfo findByCode(String code);
    /**
    * 根据 KjgCode 查询
    * @param kjg_code /
    * @return /
    */
    LogisticsInfo findByKjgCode(String kjg_code);

    LogisticsInfo findByDefault01(String carrierCode);
}