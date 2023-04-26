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

import me.zhengjie.domain.Config;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ConfigRepository;
import me.zhengjie.service.ConfigService;
import me.zhengjie.service.dto.ConfigDto;
import me.zhengjie.service.dto.ConfigQueryCriteria;
import me.zhengjie.service.mapstruct.ConfigMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author 王淼
* @date 2020-11-24
**/
@CacheConfig(cacheNames = "config")
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;
    private final ConfigMapper configMapper;
    private final RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(ConfigQueryCriteria criteria, Pageable pageable){
        Page<Config> page = configRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(configMapper::toDto));
    }

    @Override
    public List<ConfigDto> queryAll(ConfigQueryCriteria criteria){
        return configMapper.toDto(configRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ConfigDto findById(Integer id) {
        Config config = configRepository.findById(id).orElseGet(Config::new);
        ValidationUtil.isNull(config.getId(),"Config","id",id);
        return configMapper.toDto(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConfigDto create(Config resources) {
        return configMapper.toDto(configRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Config resources) {
        Config config = configRepository.findById(resources.getId()).orElseGet(Config::new);
        ValidationUtil.isNull( config.getId(),"Config","id",resources.getId());
        config.copy(resources);
        configRepository.save(config);
        redisUtils.del("config::k:" + resources.getK());
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            configRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ConfigDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ConfigDto config : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("k", config.getK());
            map.put("v", config.getV());
            map.put("描述", config.getDes());
            map.put(" modifyTime",  config.getModifyTime());
            map.put(" modifyUser",  config.getModifyUser());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Cacheable(key = "'k:' + #p0")
    @Override
    public Config queryByK(String k) {
        return configRepository.findByK(k);
    }
}