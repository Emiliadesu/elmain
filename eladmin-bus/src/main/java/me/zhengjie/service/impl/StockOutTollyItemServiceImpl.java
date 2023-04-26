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

import me.zhengjie.domain.StockOutTollyItem;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StockOutTollyItemRepository;
import me.zhengjie.service.StockOutTollyItemService;
import me.zhengjie.service.dto.StockOutTollyItemDto;
import me.zhengjie.service.dto.StockOutTollyItemQueryCriteria;
import me.zhengjie.service.mapstruct.StockOutTollyItemMapper;
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
* @author wangm
* @date 2021-03-28
**/
@Service
@RequiredArgsConstructor
public class StockOutTollyItemServiceImpl implements StockOutTollyItemService {

    private final StockOutTollyItemRepository stockOutTollyItemRepository;
    private final StockOutTollyItemMapper stockOutTollyItemMapper;

    @Override
    public Map<String,Object> queryAll(StockOutTollyItemQueryCriteria criteria, Pageable pageable){
        Page<StockOutTollyItem> page = stockOutTollyItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(stockOutTollyItemMapper::toDto));
    }

    @Override
    public List<StockOutTollyItemDto> queryAll(StockOutTollyItemQueryCriteria criteria){
        return stockOutTollyItemMapper.toDto(stockOutTollyItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StockOutTollyItemDto findById(Long id) {
        StockOutTollyItem stockOutTollyItem = stockOutTollyItemRepository.findById(id).orElseGet(StockOutTollyItem::new);
        ValidationUtil.isNull(stockOutTollyItem.getId(),"StockOutTollyItem","id",id);
        return stockOutTollyItemMapper.toDto(stockOutTollyItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockOutTollyItemDto create(StockOutTollyItem resources) {
        return stockOutTollyItemMapper.toDto(stockOutTollyItemRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockOutTollyItem resources) {
        StockOutTollyItem stockOutTollyItem = stockOutTollyItemRepository.findById(resources.getId()).orElseGet(StockOutTollyItem::new);
        ValidationUtil.isNull( stockOutTollyItem.getId(),"StockOutTollyItem","id",resources.getId());
        stockOutTollyItem.copy(resources);
        stockOutTollyItemRepository.save(stockOutTollyItem);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stockOutTollyItemRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StockOutTollyItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockOutTollyItemDto stockOutTollyItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("理货单号", stockOutTollyItem.getStockOutTolly().getTallyOrderSn());
            map.put("理货sku", stockOutTollyItem.getSkuNo());
            map.put("理货数量", stockOutTollyItem.getTallyNum());
            map.put("正品数量", stockOutTollyItem.getAvaliableNum());
            map.put("残品数量", stockOutTollyItem.getDefectNum());
            map.put("单箱数量，箱规", stockOutTollyItem.getNumPerBox());
            map.put("过期日期", stockOutTollyItem.getExpiryDate());
            map.put("生产日期", stockOutTollyItem.getProductDate());
            map.put("批次号", stockOutTollyItem.getBatchNo());
            map.put("箱数", stockOutTollyItem.getBoxNum());
            map.put("毛重(g)", stockOutTollyItem.getGrossWeight());
            map.put("托盘尺寸", stockOutTollyItem.getTraySize());
            map.put("托盘材质", stockOutTollyItem.getTrayMaterial());
            map.put(" trayNo",  stockOutTollyItem.getTrayNo());
            map.put(" purchaseOrderSn",  stockOutTollyItem.getPurchaseOrderSn());
            map.put(" inWarehouseTime",  stockOutTollyItem.getInWarehouseTime());
            map.put("是否已上传SN码", stockOutTollyItem.getUploaded());
            map.put("货号仓库用", stockOutTollyItem.getGoodsNo());
            map.put("国际码", stockOutTollyItem.getBarcode());
            map.put("通知数量", stockOutTollyItem.getPreTallyNum());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
