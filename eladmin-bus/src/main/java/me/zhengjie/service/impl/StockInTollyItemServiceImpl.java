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
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StockInTollyItemRepository;
import me.zhengjie.service.dto.StockInTollyItemDto;
import me.zhengjie.service.dto.StockInTollyItemQueryCriteria;
import me.zhengjie.service.mapstruct.StockInTollyItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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
* @date 2021-03-23
**/
@Service
@RequiredArgsConstructor
public class StockInTollyItemServiceImpl implements StockInTollyItemService {

    private final StockInTollyItemRepository stockInTollyItemRepository;
    private final StockInTollyItemMapper stockInTollyItemMapper;

    @Autowired
    private StockInTollyService stockInTollyService;

    @Autowired
    private WmsInstockService wmsInstockService;

    @Autowired
    private WmsInstockItemService wmsInstockItemService;

    @Autowired
    private TrsDetailService trsDetailService;

    @Override
    public Map<String,Object> queryAll(StockInTollyItemQueryCriteria criteria, Pageable pageable){
        Page<StockInTollyItem> page = stockInTollyItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(stockInTollyItemMapper::toDto));
    }

    @Override
    public List<StockInTollyItemDto> queryAll(StockInTollyItemQueryCriteria criteria){
        return stockInTollyItemMapper.toDto(stockInTollyItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StockInTollyItemDto findById(Long id) {
        StockInTollyItem stockInTollyItem = stockInTollyItemRepository.findById(id).orElseGet(StockInTollyItem::new);
        ValidationUtil.isNull(stockInTollyItem.getId(),"StockInTollyItem","id",id);
        return stockInTollyItemMapper.toDto(stockInTollyItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockInTollyItemDto create(StockInTollyItem resources) {
        return stockInTollyItemMapper.toDto(stockInTollyItemRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockInTollyItem resources) {
        StockInTollyItem stockInTollyItem = stockInTollyItemRepository.findById(resources.getId()).orElseGet(StockInTollyItem::new);
        ValidationUtil.isNull( stockInTollyItem.getId(),"StockInTollyItem","id",resources.getId());
        stockInTollyItem.copy(resources);
        stockInTollyItemRepository.save(stockInTollyItem);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stockInTollyItemRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StockInTollyItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockInTollyItemDto stockInTollyItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("通知数量", stockInTollyItem.getPreTallyNum());
            map.put("商品id", stockInTollyItem.getProductId());
            map.put("供应商id", stockInTollyItem.getSupplierId());
            map.put("生产商id", stockInTollyItem.getMerchantId());
            map.put("合同号", stockInTollyItem.getPoCode());
            map.put("采购价", stockInTollyItem.getPurchasePrice());
            map.put("批号（WMS操作批次号）", stockInTollyItem.getLotNo());
            map.put("资金方企业id", stockInTollyItem.getFundProviderId());
            map.put("提单号大", stockInTollyItem.getBlNo1());
            map.put("提单号小", stockInTollyItem.getBlNo2());
            map.put("PO单号", stockInTollyItem.getPoNo());
            map.put("店铺id", stockInTollyItem.getShopId());
            map.put("是否朔源", stockInTollyItem.getIsTraceSrc());
            map.put(" reheckPicUrls",  stockInTollyItem.getReheckPicUrls());
            map.put("理货单号", stockInTollyItem.getStockInTolly().getTallyOrderSn());
            map.put("理货数量", stockInTollyItem.getQtyReceived());
            map.put("是否需要回传序列号", stockInTollyItem.getNeedSn());
            map.put("商品行", stockInTollyItem.getGoodsLine());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<StockInTollyItem> queryByStockId(Long inTallyId) {
        StockInTollyItem item=new StockInTollyItem();
        item.setInTallyId(inTallyId);
        return stockInTollyItemRepository.findAll(Example.of(item));
    }

    /**
     * 更新理货统计
     * @param stockId
     */
    @Override
    public void updateStockItem(Long stockId){
        StockInTolly stockInTolly=stockInTollyService.findById(stockId);
        if (stockInTolly==null)
            throw new BadRequestException("理货数据不存在");
        WmsInstock wmsInstock=wmsInstockService.queryById(stockInTolly.getInOrderId());
        List<StockInTollyItem>itemList=queryByStockId(stockInTolly.getId());
        stockInTolly.setTotalNum(0);
        if (CollectionUtil.isEmpty(itemList)){
            //第一次理货
            List<WmsInstockItem>wmsInstockItemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
            if (CollectionUtil.isEmpty(wmsInstockItemList))
                throw new BadRequestException("通知单空明细");
            for (WmsInstockItem item : wmsInstockItemList) {
                TrsDetail trsDetail=new TrsDetail();
                StockInTolly st=new StockInTolly();
                st.setId(stockId);
                trsDetail.setStockInTolly(st);
                trsDetail.setDocLineId(item.getGoodsLineNo());
                List<TrsDetail>trsDetailList=trsDetailService.queryList(trsDetail);
                if (CollectionUtil.isEmpty(trsDetailList))
                    throw new BadRequestException("理货明细为空");
                StockInTollyItem stockInTollyItem=new StockInTollyItem();
                stockInTollyItem.setInTallyId(stockInTolly.getId());
                stockInTollyItem.setPreTallyNum(item.getQty());
                stockInTollyItem.setProductId(item.getProductId());
                stockInTollyItem.setMerchantId(wmsInstock.getMerchantId());
                stockInTollyItem.setPoNo(wmsInstock.getPoNo());
                stockInTollyItem.setNeedSn(item.getNeedSn());
                stockInTollyItem.setGoodsLine(item.getGoodsLineNo());
                stockInTollyItem.setQtyReceived(0);
                for (TrsDetail detail : trsDetailList) {
                    stockInTolly.setTotalNum(stockInTolly.getTotalNum()+detail.getTransactionQty());
                    stockInTollyItem.setQtyReceived(stockInTollyItem.getQtyReceived()+detail.getTransactionQty());
                }
                create(stockInTollyItem);
            }
        }else {
            //非第一次理货，需修改
            List<WmsInstockItem>wmsInstockItemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
            if (CollectionUtil.isEmpty(wmsInstockItemList))
                throw new BadRequestException("通知单空明细");
            for (WmsInstockItem item : wmsInstockItemList) {
                TrsDetail trsDetail=new TrsDetail();
                StockInTolly st=new StockInTolly();
                st.setId(stockId);
                trsDetail.setStockInTolly(st);
                trsDetail.setDocLineId(item.getGoodsLineNo());
                List<TrsDetail>trsDetailList=trsDetailService.queryList(trsDetail);
                if (CollectionUtil.isEmpty(trsDetailList))
                    throw new BadRequestException("理货明细为空");
                StockInTollyItem stockInTollyItem=new StockInTollyItem();
                stockInTollyItem.setInTallyId(stockInTolly.getId());
                stockInTollyItem.setPreTallyNum(item.getQty());
                stockInTollyItem.setProductId(item.getProductId());
                stockInTollyItem.setMerchantId(wmsInstock.getMerchantId());
                stockInTollyItem.setPoNo(wmsInstock.getPoNo());
                stockInTollyItem.setNeedSn(item.getNeedSn());
                stockInTollyItem.setGoodsLine(item.getGoodsLineNo());
                StockInTollyItem entity=stockInTollyItemRepository.findOne(Example.of(stockInTollyItem)).orElse(null);
                stockInTollyItem.setQtyReceived(0);
                for (TrsDetail detail : trsDetailList) {
                    stockInTolly.setTotalNum(stockInTolly.getTotalNum()+detail.getTransactionQty());
                    stockInTollyItem.setQtyReceived(stockInTollyItem.getQtyReceived()+detail.getTransactionQty());
                }
                if (entity!=null){
                    entity.setQtyReceived(stockInTollyItem.getQtyReceived());
                    update(entity);
                }else
                    create(stockInTollyItem);
            }
        }
    }
}
