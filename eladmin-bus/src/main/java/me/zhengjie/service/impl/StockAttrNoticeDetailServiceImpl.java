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

import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.StockAttrNoticeDetail;
import me.zhengjie.domain.StockInTolly;
import me.zhengjie.domain.WmsInstock;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.StockInTollyService;
import me.zhengjie.service.WmsInstockService;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StockAttrNoticeDetailRepository;
import me.zhengjie.service.StockAttrNoticeDetailService;
import me.zhengjie.service.dto.StockAttrNoticeDetailDto;
import me.zhengjie.service.dto.StockAttrNoticeDetailQueryCriteria;
import me.zhengjie.service.mapstruct.StockAttrNoticeDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
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
* @date 2021-04-15
**/
@Service
@RequiredArgsConstructor
public class StockAttrNoticeDetailServiceImpl implements StockAttrNoticeDetailService {

    private final StockAttrNoticeDetailRepository stockAttrNoticeDetailRepository;
    private final StockAttrNoticeDetailMapper stockAttrNoticeDetailMapper;
    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private WmsInstockService wmsInstockService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private StockInTollyService stockInTollyService;

    @Override
    public Map<String,Object> queryAll(StockAttrNoticeDetailQueryCriteria criteria, Pageable pageable){
        Page<StockAttrNoticeDetail> page = stockAttrNoticeDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(stockAttrNoticeDetailMapper::toDto));
    }

    @Override
    public List<StockAttrNoticeDetailDto> queryAll(StockAttrNoticeDetailQueryCriteria criteria){
        return stockAttrNoticeDetailMapper.toDto(stockAttrNoticeDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StockAttrNoticeDetailDto findById(Long id) {
        StockAttrNoticeDetail stockAttrNoticeDetail = stockAttrNoticeDetailRepository.findById(id).orElseGet(StockAttrNoticeDetail::new);
        ValidationUtil.isNull(stockAttrNoticeDetail.getId(),"StockAttrNoticeDetail","id",id);
        return stockAttrNoticeDetailMapper.toDto(stockAttrNoticeDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockAttrNoticeDetailDto create(StockAttrNoticeDetail resources) {
        if (StringUtil.isEmpty(resources.getSeq())){
            resources.setSeq(System.currentTimeMillis()+"");
            resources.setDocCode(resources.getSeq());
        }else {
            resources.setDocCode(resources.getSeq());
        }
        //此时这个productId的值不是真正的productId(现场可能是外箱看到的货号)
        BaseSku baseSku= baseSkuService.queryByOutGoodsNo(resources.getProductId());
        if (baseSku==null){
            //仓库货号
            baseSku=baseSkuService.queryByGoodsNo(resources.getProductId());
        }
        resources.setProductId(baseSku.getGoodsCode());//真正的productId
        if (resources.getProductionTime()==null){
            if (baseSku.getLifecycle()==null||baseSku.getLifecycle()<=0)
                throw new BadRequestException("没有维护保质期,无法推算生产日期");
            resources.setProductionTime(new Timestamp(resources.getExpireTime().getTime()-baseSku.getLifecycle()*24L*3600L*1000L));
        }
        return stockAttrNoticeDetailMapper.toDto(stockAttrNoticeDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockAttrNoticeDetail resources) {
        StockAttrNoticeDetail stockAttrNoticeDetail = stockAttrNoticeDetailRepository.findById(resources.getId()).orElseGet(StockAttrNoticeDetail::new);
        ValidationUtil.isNull( stockAttrNoticeDetail.getId(),"StockAttrNoticeDetail","id",resources.getId());
        stockAttrNoticeDetail.copy(resources);
        stockAttrNoticeDetailRepository.save(stockAttrNoticeDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stockAttrNoticeDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StockAttrNoticeDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockAttrNoticeDetailDto stockAttrNoticeDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("属性调整通知id", stockAttrNoticeDetail.getStockAttrNotice().getId());
            map.put("交易序列号", stockAttrNoticeDetail.getSeq());
            map.put("单据id", stockAttrNoticeDetail.getDocId());
            map.put("单据编号", stockAttrNoticeDetail.getDocCode());
            map.put("产品id", stockAttrNoticeDetail.getProductId());
            map.put("商家id", stockAttrNoticeDetail.getMerchantId());
            map.put("交易类型", stockAttrNoticeDetail.getTransactionType());
            map.put("单据类型", stockAttrNoticeDetail.getDocType());
            map.put("交易数量", stockAttrNoticeDetail.getTransactionQty());
            map.put("生产日期", stockAttrNoticeDetail.getProductionTime());
            map.put("目标仓库id", stockAttrNoticeDetail.getToWarehouseId());
            map.put("是否坏品", stockAttrNoticeDetail.getIsDamaged());
            map.put("坏品类型", stockAttrNoticeDetail.getDamagedType());
            map.put("委托方id", stockAttrNoticeDetail.getConsignorId());
            map.put("客户批次号", stockAttrNoticeDetail.getCustomerBatchNo());
            map.put("首次入库时间", stockAttrNoticeDetail.getSourceAsnInWarehouseTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<StockAttrNoticeDetailDto> getAllSingularSeq(Long stockAttrId) {
        return stockAttrNoticeDetailMapper.toDto(stockAttrNoticeDetailRepository.getAllSingularSeq(stockAttrId));
    }

    @Override
    public void addDetailByPreProject(StockAttrNoticeDetail resources) {
        resources.setDocType("100");
        resources.setSeq(System.currentTimeMillis()+"");
        resources.setDocCode(resources.getSeq());
        BaseSku baseSku= baseSkuService.queryByGoodsNo(resources.getGoodsNo());
        if (baseSku!=null){
            //仓库货号
            resources.setProductId(baseSku.getGoodsCode());
            if (baseSku.getLifecycle()==null||baseSku.getLifecycle()<=0)
                throw new BadRequestException("没有维护保质期,无法推算生产日期");
            resources.setProductionTime(new Timestamp(resources.getExpireTime().getTime()-baseSku.getLifecycle()*24L*3600L*1000L));
        }else
            throw new BadRequestException("商品基本信息找不到货号"+resources.getGoodsNo());
        resources.setToWarehouseId("3302461510");
        WmsInstock wmsInstock=wmsInstockService.queryByPoNo(resources.getPoNo());
        if (wmsInstock==null)
            throw new BadRequestException("根据PO单号"+resources.getPoNo()+"找不到入库单");
        StockInTolly stockInTolly=stockInTollyService.queryByInId(wmsInstock.getId());
        if (stockInTolly==null)
            throw new BadRequestException("根据PO单号"+resources.getPoNo()+"找不到入库理货单");
        if (stockInTolly.getPutawayTime()==null) {
            throw new BadRequestException("根据PO单号" + resources.getPoNo() + "找到的入库理货单没有上架时间");
        }
        resources.setSourceAsnInWarehouseTime(DateUtils.parseDate(stockInTolly.getPutawayTime()));
        resources.setMerchantId(wmsInstock.getMerchantId());
        if (resources.getPreProject()==0){
            //正转残
            resources.setTransactionType("84");
            resources.setIsDamaged("0");
        }else if (resources.getPreProject()==1){
            //残转正
            resources.setTransactionType("85");
            resources.setIsDamaged("1");
        }
        resources=stockAttrNoticeDetailRepository.save(resources);
        StockAttrNoticeDetail copyDetail=new StockAttrNoticeDetail();
        copyDetail.copy(resources);
        copyDetail.setId(null);
        if (resources.getPreProject()==0){
            //正转残
            copyDetail.setTransactionType("81");
            copyDetail.setIsDamaged("1");
        }else if (resources.getPreProject()==1){
            //残转正
            copyDetail.setTransactionType("80");
            copyDetail.setIsDamaged("1");
        }
        stockAttrNoticeDetailRepository.save(copyDetail);
    }
}
