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

import cn.hutool.core.collection.CollectionUtil;
import me.zhengjie.domain.WmsInstock;
import me.zhengjie.domain.WmsInstockItem;
import me.zhengjie.service.WmsInstockService;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.WmsInstockItemRepository;
import me.zhengjie.service.WmsInstockItemService;
import me.zhengjie.service.dto.WmsInstockItemDto;
import me.zhengjie.service.dto.WmsInstockItemQueryCriteria;
import me.zhengjie.service.mapstruct.WmsInstockItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
* @author 王淼
* @date 2020-12-08
**/
@Service
@RequiredArgsConstructor
public class WmsInstockItemServiceImpl implements WmsInstockItemService {

    private final WmsInstockItemRepository wmsInstockItemRepository;
    private final WmsInstockItemMapper wmsInstockItemMapper;

    @Autowired
    private WmsInstockService wmsInstockService;

    @Override
    public Map<String,Object> queryAll(WmsInstockItemQueryCriteria criteria, Pageable pageable){
        Page<WmsInstockItem> page = wmsInstockItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wmsInstockItemMapper::toDto));
    }

    @Override
    public List<WmsInstockItemDto> queryAll(WmsInstockItemQueryCriteria criteria){
        return wmsInstockItemMapper.toDto(wmsInstockItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WmsInstockItemDto findById(Long id) {
        WmsInstockItem wmsInstockItem = wmsInstockItemRepository.findById(id).orElseGet(WmsInstockItem::new);
        ValidationUtil.isNull(wmsInstockItem.getId(),"WmsInstockItem","id",id);
        return wmsInstockItemMapper.toDto(wmsInstockItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsInstockItemDto create(WmsInstockItem resources) {
        return wmsInstockItemMapper.toDto(wmsInstockItemRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WmsInstockItem resources) {
        WmsInstockItem wmsInstockItem = wmsInstockItemRepository.findById(resources.getId()).orElseGet(WmsInstockItem::new);
        ValidationUtil.isNull( wmsInstockItem.getId(),"WmsInstockItem","id",resources.getId());
        wmsInstockItem.setId(resources.getId());
        wmsInstockItemRepository.save(wmsInstockItem);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            wmsInstockItemRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WmsInstockItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        WmsInstock wmsInstock=null;
        for (WmsInstockItemDto wmsInstockItem : all) {
            if (wmsInstock==null)
                wmsInstock=wmsInstockService.queryById(wmsInstockItem.getInId());
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("入库单号", wmsInstock.getInOrderSn());
            map.put("供应商货号", wmsInstockItem.getSkuNo());
            map.put("仓库货号", wmsInstockItem.getGoodsNo());
            map.put("商品行", wmsInstockItem.getGoodsLineNo());
            map.put("条码", wmsInstockItem.getBarCode());
            map.put("商品名称", wmsInstockItem.getGoodsName());
            map.put("商品数量", wmsInstockItem.getQty());
            map.put("商品Id", wmsInstockItem.getItemId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<WmsInstockItemDto> createBatch(List<WmsInstockItem> itemList) {
        if (CollectionUtil.isEmpty(itemList))
            return null;
        List<WmsInstockItemDto>resList=new ArrayList<>();
        for (WmsInstockItem wmsInstockItem : itemList) {
            resList.add(create(wmsInstockItem));
        }
        return resList;
    }

    @Override
    public List<WmsInstockItem> queryAllByInId(Long inId){
        WmsInstockItem item=new WmsInstockItem();
        item.setInId(inId);
        return wmsInstockItemRepository.findAll(Example.of(item));
    }
}
