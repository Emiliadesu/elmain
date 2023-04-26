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

import me.zhengjie.domain.BRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
public interface BRoleRepository extends JpaRepository<BRole, Long>, JpaSpecificationExecutor<BRole> {
    /**
     * 根据用户ID查询
     * @param id 用户ID
     * @return /
     */
    @Query(value = "SELECT r.* FROM sys_role r, sys_users_roles u WHERE " +
            "r.role_id = u.role_id AND u.user_id = ?1 AND r.name= ?2",nativeQuery = true)
    BRole findByUserIdAndName(Long id, String name);
}
