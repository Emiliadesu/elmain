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

import me.zhengjie.domain.ReturnGoodsItem;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ReturnGoodsItemRepository;
import me.zhengjie.service.ReturnGoodsItemService;
import me.zhengjie.service.dto.ReturnGoodsItemDto;
import me.zhengjie.service.dto.ReturnGoodsItemQueryCriteria;
import me.zhengjie.service.mapstruct.ReturnGoodsItemMapper;
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
* @author lh
* @date 2021-01-04
**/
@Service
@RequiredArgsConstructor
public class ReturnGoodsItemServiceImpl implements ReturnGoodsItemService {

    private final ReturnGoodsItemRepository returnGoodsItemRepository;
    private final ReturnGoodsItemMapper returnGoodsItemMapper;

    @Override
    public Map<String,Object> queryAll(ReturnGoodsItemQueryCriteria criteria, Pageable pageable){
        Page<ReturnGoodsItem> page = returnGoodsItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(returnGoodsItemMapper::toDto));
    }

    @Override
    public List<ReturnGoodsItemDto> queryAll(ReturnGoodsItemQueryCriteria criteria){
        return returnGoodsItemMapper.toDto(returnGoodsItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ReturnGoodsItemDto findById(Long id) {
        ReturnGoodsItem returnGoodsItem = returnGoodsItemRepository.findById(id).orElseGet(ReturnGoodsItem::new);
        ValidationUtil.isNull(returnGoodsItem.getId(),"ReturnGoodsItem","id",id);
        return returnGoodsItemMapper.toDto(returnGoodsItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnGoodsItemDto create(ReturnGoodsItem resources) {
        return returnGoodsItemMapper.toDto(returnGoodsItemRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ReturnGoodsItem resources) {
        ReturnGoodsItem returnGoodsItem = returnGoodsItemRepository.findById(resources.getId()).orElseGet(ReturnGoodsItem::new);
        ValidationUtil.isNull( returnGoodsItem.getId(),"ReturnGoodsItem","id",resources.getId());
        returnGoodsItem.copy(resources);
        returnGoodsItemRepository.save(returnGoodsItem);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            returnGoodsItemRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ReturnGoodsItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReturnGoodsItemDto returnGoodsItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("退货id", returnGoodsItem.getReturnGoodsId());
            map.put("海关货号", returnGoodsItem.getSku());
            map.put("条码", returnGoodsItem.getBarCode());
            map.put(" skuName",  returnGoodsItem.getSkuName());
            map.put("货品数量", returnGoodsItem.getNum());
            map.put("货品状态", returnGoodsItem.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}