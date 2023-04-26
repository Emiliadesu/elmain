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

import me.zhengjie.domain.AddValueOrderDetails;
import me.zhengjie.domain.CrossBorderOrderDetails;
import me.zhengjie.domain.GiftDetails;
import me.zhengjie.service.dto.OrderReturnDetailsQueryCriteria;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.GiftDetailsRepository;
import me.zhengjie.service.GiftDetailsService;
import me.zhengjie.service.dto.GiftDetailsDto;
import me.zhengjie.service.dto.GiftDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.GiftDetailsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
* @author leningzhou
* @date 2021-12-27
**/
@Service
@RequiredArgsConstructor
public class GiftDetailsServiceImpl implements GiftDetailsService {

    private final GiftDetailsRepository giftDetailsRepository;
    private final GiftDetailsMapper giftDetailsMapper;

    @Override
    public Map<String,Object> queryAll(GiftDetailsQueryCriteria criteria, Pageable pageable){
        Page<GiftDetails> page = giftDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(giftDetailsMapper::toDto));
    }

    @Override
    public List<GiftDetailsDto> queryAll(GiftDetailsQueryCriteria criteria){
        return giftDetailsMapper.toDto(giftDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public GiftDetailsDto findById(Long id) {
        GiftDetails giftDetails = giftDetailsRepository.findById(id).orElseGet(GiftDetails::new);
        ValidationUtil.isNull(giftDetails.getId(),"GiftDetails","id",id);
        return giftDetailsMapper.toDto(giftDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GiftDetailsDto create(GiftDetails resources) {
        return giftDetailsMapper.toDto(giftDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(GiftDetails resources) {
        GiftDetails giftDetails = giftDetailsRepository.findById(resources.getId()).orElseGet(GiftDetails::new);
        ValidationUtil.isNull( giftDetails.getId(),"GiftDetails","id",resources.getId());
        giftDetails.copy(resources);
        giftDetailsRepository.save(giftDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            giftDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<GiftDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (GiftDetailsDto giftDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赠品ID", giftDetails.getGiftId());
            map.put("赠品条码", giftDetails.getGiftCode());
            map.put("赠品名称", giftDetails.getGiftName());
            map.put("商品编码", giftDetails.getGoodsCode());
            map.put("商品名称",giftDetails.getGoodsName());
            map.put("放置数量", giftDetails.getPlaceCounts());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<GiftDetails> queryByGiftId(Long giftId) {
        GiftDetailsQueryCriteria criteria = new GiftDetailsQueryCriteria();
        criteria.setGiftId(giftId);
        List<GiftDetails> list = giftDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
        return list;
    }
}