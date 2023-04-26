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
import me.zhengjie.domain.StockAttr;
import me.zhengjie.domain.StockAttrNotice;
import me.zhengjie.domain.StockAttrNoticeDetail;
import me.zhengjie.service.StockAttrService;
import me.zhengjie.support.zhuozhi.ZhuozhiSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StockAttrNoticeRepository;
import me.zhengjie.service.StockAttrNoticeService;
import me.zhengjie.service.dto.StockAttrNoticeDto;
import me.zhengjie.service.dto.StockAttrNoticeQueryCriteria;
import me.zhengjie.service.mapstruct.StockAttrNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author wangm
* @date 2021-04-15
**/
@Service
@RequiredArgsConstructor
public class StockAttrNoticeServiceImpl implements StockAttrNoticeService {

    private final StockAttrNoticeRepository stockAttrNoticeRepository;
    private final StockAttrNoticeMapper stockAttrNoticeMapper;
    @Autowired
    private StockAttrService stockAttrService;

    @Autowired
    private ZhuozhiSupport zhuozhiSupport;

    @Override
    public Map<String,Object> queryAll(StockAttrNoticeQueryCriteria criteria, Pageable pageable){
        Page<StockAttrNotice> page = stockAttrNoticeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(stockAttrNoticeMapper::toDto));
    }

    @Override
    public List<StockAttrNoticeDto> queryAll(StockAttrNoticeQueryCriteria criteria){
        return stockAttrNoticeMapper.toDto(stockAttrNoticeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StockAttrNoticeDto findById(Long id) {
        StockAttrNotice stockAttrNotice = stockAttrNoticeRepository.findById(id).orElseGet(StockAttrNotice::new);
        ValidationUtil.isNull(stockAttrNotice.getId(),"StockAttrNotice","id",id);
        return stockAttrNoticeMapper.toDto(stockAttrNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockAttrNoticeDto create(StockAttrNotice resources) {
        resources.setSrvlogId(System.currentTimeMillis());
        resources.setCreateTime(DateUtils.now());
        resources.setCompleteOpter(SecurityUtils.getCurrentUsername());
        resources.setIsComplete("0");
        return stockAttrNoticeMapper.toDto(stockAttrNoticeRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockAttrNotice resources) {
        StockAttrNotice stockAttrNotice = stockAttrNoticeRepository.findById(resources.getId()).orElseGet(StockAttrNotice::new);
        ValidationUtil.isNull( stockAttrNotice.getId(),"StockAttrNotice","id",resources.getId());
        stockAttrNotice.copy(resources);
        stockAttrNoticeRepository.save(stockAttrNotice);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stockAttrNoticeRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StockAttrNoticeDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockAttrNoticeDto stockAttrNotice : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("仓库单据id", stockAttrNotice.getSrvlogId());
            map.put("仓库id", stockAttrNotice.getWarehouseId());
            map.put("租户编号", stockAttrNotice.getTenantCode());
            map.put("通知时间", stockAttrNotice.getCreateTime());
            map.put("是否完成", stockAttrNotice.getIsComplete());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void complete(Long id) {
        StockAttrNotice notice=new StockAttrNotice();
        notice.setId(id);
        notice.setIsComplete("1");
        notice.setCompleteOpter(SecurityUtils.getCurrentUsername());
        update(notice);

    }

    @Override
    public Map<String,Object> push(Long id) {
        StockAttrNotice attrNotice=stockAttrNoticeRepository.findById(id).orElse(null);
        Map<String,Object>resMap=new HashMap<>();
        if (attrNotice==null) {
            resMap.put("is_success", false);
            resMap.put("msg","无效数据");
            return resMap;
        }
        if (CollectionUtil.isEmpty(attrNotice.getItems())) {
            resMap.put("is_success", false);
            resMap.put("msg","没有明细");
            return resMap;
        }
        List<StockAttr>attrList=new ArrayList<>();
        for (StockAttrNoticeDetail item : attrNotice.getItems()) {
            StockAttr source=stockAttrService.queryByWmsBatch(item.getLotNo());
            item.setBookNo(source.getBookNo());
            StockAttr stockAttr=new StockAttr();
            if (StringUtil.equals(source.getWmsBatchNo(),item.getLotNo())){
                stockAttr.setId(source.getId());
            }
            stockAttr.setCustomerBatchNo(item.getCustomerBatchNo());
            stockAttr.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            stockAttr.setSubType(source.getSubType());
            stockAttr.setWmsBatchNo(item.getWmsBatchCode());
            stockAttr.setSuperviseCode(source.getSuperviseCode());
            stockAttr.setInOrderSn(source.getInOrderSn());
            stockAttr.setBookNo(item.getBookNo());
            if (attrList.size()<1){
                attrList.add(stockAttr);
            }
        }
        try {
            zhuozhiSupport.stockAttr(attrNotice);
        }catch (Exception e){
            resMap.put("is_success", false);
            resMap.put("msg","失败："+e.getMessage());
            return resMap;
        }
        for (StockAttr stockAttr : attrList) {
            if (stockAttr.getId()==null)
                stockAttrService.create(stockAttr);
        }
        attrNotice.setIsComplete("1");
        update(attrNotice);
        resMap.put("is_success", true);
        resMap.put("msg","成功");
        return resMap;
    }
}
