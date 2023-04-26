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
import cn.hutool.json.JSONObject;
import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.domain.StackStockRecord;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.CustomerKeyService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.service.dto.InvLotLocIdAtt;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StackStockRecordRepository;
import me.zhengjie.service.StackStockRecordService;
import me.zhengjie.service.dto.StackStockRecordDto;
import me.zhengjie.service.dto.StackStockRecordQueryCriteria;
import me.zhengjie.service.mapstruct.StackStockRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
* @author wangm
* @date 2021-07-16
**/
@Service
@RequiredArgsConstructor
public class StackStockRecordServiceImpl implements StackStockRecordService {

    private final StackStockRecordRepository stackStockRecordRepository;
    private final StackStockRecordMapper stackStockRecordMapper;

    @Autowired
    private CustomerKeyService customerKeyService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Override
    public Map<String,Object> queryAll(StackStockRecordQueryCriteria criteria, Pageable pageable){
        Page<StackStockRecord> page = stackStockRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(stackStockRecordMapper::toDto));
    }

    @Override
    public Map<String,Object> queryPage(String atTime, List<String> goodsNos, String shopId, Pageable pageable){
        Page<StackStockRecord> page;
        if (CollectionUtil.isNotEmpty(goodsNos)&&StringUtil.isNotBlank(shopId))
            page = stackStockRecordRepository.queryPage(atTime,shopId,goodsNos,pageable);
        else if (CollectionUtil.isNotEmpty(goodsNos))
            page = stackStockRecordRepository.queryPage(atTime,goodsNos,pageable);
        else if (StringUtil.isNotBlank(shopId))
            page = stackStockRecordRepository.queryPage(atTime,shopId,pageable);
        else
            page = stackStockRecordRepository.queryPage(atTime,pageable);
        return PageUtil.toPage(page.map(stackStockRecordMapper::toDto));
    }

    @Override
    public List<StackStockRecordDto> queryAll(StackStockRecordQueryCriteria criteria){
        return stackStockRecordMapper.toDto(stackStockRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StackStockRecordDto findById(Long id) {
        StackStockRecord stackStockRecord = stackStockRecordRepository.findById(id).orElseGet(StackStockRecord::new);
        ValidationUtil.isNull(stackStockRecord.getId(),"StackStockRecord","id",id);
        return stackStockRecordMapper.toDto(stackStockRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StackStockRecordDto create(StackStockRecord resources) {
        return stackStockRecordMapper.toDto(stackStockRecordRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StackStockRecord resources) {
        StackStockRecord stackStockRecord = stackStockRecordRepository.findById(resources.getId()).orElseGet(StackStockRecord::new);
        ValidationUtil.isNull( stackStockRecord.getId(),"StackStockRecord","id",resources.getId());
        stackStockRecord.copy(resources);
        stackStockRecordRepository.save(stackStockRecord);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stackStockRecordRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StackStockRecordDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StackStockRecordDto stackStockRecord : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("wms批次号", stackStockRecord.getBatchNo());
            map.put("海关货号", stackStockRecord.getSku());
            map.put("入库时间", stackStockRecord.getFirstReceiveDate());
            map.put("入库单号", stackStockRecord.getAsnCode());
            map.put("是否坏品", stackStockRecord.getIsDamaged());
            map.put("可用库存", stackStockRecord.getStockQty());
            map.put("库位托盘数量", stackStockRecord.getPlateNum());
            map.put("快照时间", stackStockRecord.getCreateDate());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void recordZhuoZStackStock() {
        CustomerKeyDto customerKey=customerKeyService.findByCustCode("3302461510");
        List<ShopInfo>shopInfoList=shopInfoService.queryByCusId(32L);
        if (CollectionUtil.isEmpty(shopInfoList))
            return;
        List<StackStockRecord>recordList=new ArrayList<>();
        List<String>productCodes=new ArrayList<>();
        String recordDate=DateUtils.formatDate(new Date());
        for (ShopInfo shopInfo : shopInfoList) {
            List<BaseSku>baseSkuList=baseSkuService.queryByShopId(shopInfo.getId());
            if (CollectionUtil.isEmpty(baseSkuList))
                return;
            for (BaseSku baseSku : baseSkuList) {
                productCodes.add(baseSku.getGoodsNo());
            }
            int pageNo=1;
            int pageCount=-1;
            do {
                JSONObject stockData= FluxUtils.queryStock(pageNo,500,productCodes,customerKey);
                int total=stockData.getInt("total");
                if (total<=0)
                    break;
                if (pageCount==-1){
                    pageCount=total%500!=0?total/500+1:total/500;
                }
                List<InvLotLocIdAtt>records=stockData.getJSONArray("records").toList(InvLotLocIdAtt.class);
                if (CollectionUtil.isEmpty(records))
                    break;
                for (InvLotLocIdAtt record : records) {
                    StackStockRecord stackStockRecord=new StackStockRecord();
                    stackStockRecord.setAsnCode(record.getLotAtt04());
                    stackStockRecord.setBatchNo(record.getLotNum());
                    stackStockRecord.setCreateDate(recordDate);
                    stackStockRecord.setFirstReceiveDate(record.getLotAtt03());
                    stackStockRecord.setIsDamaged(StringUtil.equals("良品",record.getLotAtt08())?"1":"0");
                    stackStockRecord.setPlateNum(record.getLocationNum());
                    stackStockRecord.setShopCode(shopInfo.getCode());
                    stackStockRecord.setSku(record.getSku());
                    stackStockRecord.setStockQty(record.getQty());
                    stackStockRecord.setCustomerBatch(record.getLotAtt09());
                    recordList.add(stackStockRecord);
                }
                stackStockRecordRepository.saveAll(recordList);
                recordList.clear();
                pageNo++;
            }while (pageNo<=pageCount);
            productCodes.clear();
        }
    }

    @Override
    public StackStockRecord queryByLotNo(String lotNo) {
        StackStockRecord record=new StackStockRecord();
        record.setBatchNo(lotNo);
        return stackStockRecordRepository.findOne(Example.of(record)).orElse(null);
    }
}
