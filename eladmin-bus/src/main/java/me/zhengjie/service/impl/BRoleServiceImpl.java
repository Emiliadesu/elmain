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
package me.zhengjie.service.impl;


import lombok.RequiredArgsConstructor;
import me.zhengjie.domain.BRole;

import me.zhengjie.repository.BRoleRepository;
import me.zhengjie.service.BRoleService;

import org.springframework.cache.annotation.CacheConfig;

import org.springframework.stereotype.Service;


/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "role")
public class BRoleServiceImpl implements BRoleService {

    private final BRoleRepository broleRepository;

    @Override
    public BRole findByUserIdAndName(Long id, String name) {
        return broleRepository.findByUserIdAndName(id, name);
    }

  }
