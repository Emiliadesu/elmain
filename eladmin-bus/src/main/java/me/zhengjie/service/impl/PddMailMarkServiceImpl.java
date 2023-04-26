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

import me.zhengjie.domain.PddMailMark;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PddMailMarkRepository;
import me.zhengjie.service.PddMailMarkService;
import me.zhengjie.service.dto.PddMailMarkDto;
import me.zhengjie.service.dto.PddMailMarkQueryCriteria;
import me.zhengjie.service.mapstruct.PddMailMarkMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author wangm
* @date 2021-06-10
**/
@Service
@RequiredArgsConstructor
public class PddMailMarkServiceImpl implements PddMailMarkService {

    private final PddMailMarkRepository pddMailMarkRepository;
    private final PddMailMarkMapper pddMailMarkMapper;

    @Override
    public Map<String,Object> queryAll(PddMailMarkQueryCriteria criteria, Pageable pageable){
        Page<PddMailMark> page = pddMailMarkRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(pddMailMarkMapper::toDto));
    }

    @Override
    public List<PddMailMarkDto> queryAll(PddMailMarkQueryCriteria criteria){
        return pddMailMarkMapper.toDto(pddMailMarkRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PddMailMarkDto findById(Long id) {
        PddMailMark pddMailMark = pddMailMarkRepository.findById(id).orElseGet(PddMailMark::new);
        ValidationUtil.isNull(pddMailMark.getId(),"PddMailMark","id",id);
        return pddMailMarkMapper.toDto(pddMailMark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PddMailMarkDto create(PddMailMark resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId());
        return pddMailMarkMapper.toDto(pddMailMarkRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PddMailMark resources) {
        PddMailMark pddMailMark = pddMailMarkRepository.findById(resources.getId()).orElseGet(PddMailMark::new);
        ValidationUtil.isNull( pddMailMark.getId(),"PddMailMark","id",resources.getId());
        pddMailMark.copy(resources);
        pddMailMarkRepository.save(pddMailMark);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            pddMailMarkRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PddMailMarkDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PddMailMarkDto pddMailMark : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("拼多多订单号", pddMailMark.getOrderSn());
            map.put("运单号", pddMailMark.getMailNo());
            map.put("大头笔", pddMailMark.getAddMark());
            map.put("店铺code", pddMailMark.getShopCode());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public PddMailMark queryByOrderSn(String orderSn) {
        PddMailMark mailMark=new PddMailMark();
        mailMark.setOrderSn(orderSn);
        return pddMailMarkRepository.findOne(Example.of(mailMark)).orElse(null);
    }
}
