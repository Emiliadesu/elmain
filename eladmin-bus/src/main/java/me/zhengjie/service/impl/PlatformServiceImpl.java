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

import me.zhengjie.domain.Platform;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PlatformRepository;
import me.zhengjie.service.PlatformService;
import me.zhengjie.service.dto.PlatformDto;
import me.zhengjie.service.dto.PlatformQueryCriteria;
import me.zhengjie.service.mapstruct.PlatformMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author 王淼
* @date 2020-10-12
**/
@CacheConfig(cacheNames = "platform")
@Service
@RequiredArgsConstructor
public class PlatformServiceImpl implements PlatformService {

    private final PlatformRepository platformRepository;
    private final PlatformMapper platformMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(PlatformQueryCriteria criteria, Pageable pageable){
        Page<Platform> page = platformRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(platformMapper::toDto));
    }

    @Override
    public List<PlatformDto> queryAll(PlatformQueryCriteria criteria){
        return platformMapper.toDto(platformRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PlatformDto findById(Long id) {
        Platform platform = platformRepository.findById(id).orElseGet(Platform::new);
        ValidationUtil.isNull(platform.getId(),"Platform","id",id);
        return platformMapper.toDto(platform);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PlatformDto create(Platform resources) {
        redisUtils.del("platform::id:" + resources.getId());
        redisUtils.del("platform::nickName:" + resources.getPlafNickName());
        return platformMapper.toDto(platformRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Platform resources) {
        Platform platform = platformRepository.findById(resources.getId()).orElseGet(Platform::new);
        ValidationUtil.isNull( platform.getId(),"Platform","id",resources.getId());
        platform.copy(resources);
        platformRepository.save(platform);
        redisUtils.del("platform::id:" + resources.getId());
        redisUtils.del("platform::nickName:" + resources.getPlafNickName());
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            platformRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PlatformDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PlatformDto platform : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("平台名", platform.getPlafName());
            map.put("平台别名", platform.getPlafNickName());
            map.put("平台代码", platform.getPlafCode());
            map.put(" ebpCode",  platform.getEbpCode());
            map.put(" ebpName",  platform.getEbpName());
            map.put(" orderForm",  platform.getOrderForm());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Platform findByCode(String platformCode) {
        Platform platform=new Platform();
        platform.setPlafCode(platformCode);
        Optional<Platform> platOpt = platformRepository.findOne(Example.of(platform));
        if (platOpt.isPresent()){
            return platOpt.get();
        }
        return null;
    }

    @Cacheable(key = "'id:' + #p0")
    @Override
    public Platform queryByPlafCode(String code) {
        return platformRepository.findByPlafCode(code);
    }

    @Override
    public Platform queryById(long id) {
        return platformRepository.findById(id).orElse(null);
    }

    @Cacheable(key = "'nickName:' + #p0")
    @Override
    public Platform queryByPlafNickName(String plafNickName) {
        return platformRepository.findByPlafNickName(plafNickName);
    }
}
