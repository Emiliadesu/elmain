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

import me.zhengjie.domain.WmsOutstockItem;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.WmsOutstockItemRepository;
import me.zhengjie.service.WmsOutstockItemService;
import me.zhengjie.service.dto.WmsOutstockItemDto;
import me.zhengjie.service.dto.WmsOutstockItemQueryCriteria;
import me.zhengjie.service.mapstruct.WmsOutstockItemMapper;
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
* @date 2020-12-18
**/
@Service
@RequiredArgsConstructor
public class WmsOutstockItemServiceImpl implements WmsOutstockItemService {

    private final WmsOutstockItemRepository wmsOutstockItemRepository;
    private final WmsOutstockItemMapper wmsOutstockItemMapper;

    @Override
    public Map<String,Object> queryAll(WmsOutstockItemQueryCriteria criteria, Pageable pageable){
        Page<WmsOutstockItem> page = wmsOutstockItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wmsOutstockItemMapper::toDto));
    }

    @Override
    public List<WmsOutstockItemDto> queryAll(WmsOutstockItemQueryCriteria criteria){
        return wmsOutstockItemMapper.toDto(wmsOutstockItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WmsOutstockItemDto findById(Long id) {
        WmsOutstockItem wmsOutstockItem = wmsOutstockItemRepository.findById(id).orElseGet(WmsOutstockItem::new);
        ValidationUtil.isNull(wmsOutstockItem.getId(),"WmsOutstockItem","id",id);
        return wmsOutstockItemMapper.toDto(wmsOutstockItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsOutstockItemDto create(WmsOutstockItem resources) {
        return wmsOutstockItemMapper.toDto(wmsOutstockItemRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WmsOutstockItem resources) {
        WmsOutstockItem wmsOutstockItem = wmsOutstockItemRepository.findById(resources.getId()).orElseGet(WmsOutstockItem::new);
        ValidationUtil.isNull( wmsOutstockItem.getId(),"WmsOutstockItem","id",resources.getId());
        wmsOutstockItem.copy(resources);
        wmsOutstockItemRepository.save(wmsOutstockItem);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            wmsOutstockItemRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WmsOutstockItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WmsOutstockItemDto wmsOutstockItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("出库单号，商家自行生成的唯一单号", wmsOutstockItem.getOutOrderSn());
            map.put("仓库货号", wmsOutstockItem.getGoodsNo());
            map.put("商品行", wmsOutstockItem.getGoodsLineNo());
            map.put("供应商货号", wmsOutstockItem.getSkuNo());
            map.put("条码", wmsOutstockItem.getBarCode());
            map.put("商品名称", wmsOutstockItem.getGoodsName());
            map.put("商品数量", wmsOutstockItem.getQty());
            map.put("正品数量", wmsOutstockItem.getNondefectiveNum());
            map.put("残品数量", wmsOutstockItem.getDamageNum());
            map.put("指定生产批次", wmsOutstockItem.getBatchNo());
            map.put("指定效期开始(yyyy-MM-dd HH:mm:ss)", wmsOutstockItem.getExpStartTime());
            map.put("商品id", wmsOutstockItem.getItemId());
            map.put("指定效期结束(yyyy-MM-dd HH:mm:ss)", wmsOutstockItem.getExpEndTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<WmsOutstockItem> queryByOutId(Long outStockId) {
        WmsOutstockItem item=new WmsOutstockItem();
        item.setOutId(outStockId);
        return wmsOutstockItemRepository.findAll(Example.of(item));
    }
}
