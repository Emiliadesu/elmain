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
import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.DoLot;
import me.zhengjie.domain.StockOutTolly;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.fuliPre.ActAllocationDetails;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DoLotRepository;
import me.zhengjie.service.DoLotService;
import me.zhengjie.service.dto.DoLotDto;
import me.zhengjie.service.dto.DoLotQueryCriteria;
import me.zhengjie.service.mapstruct.DoLotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
* @date 2021-03-28
**/
@Service
@RequiredArgsConstructor
public class DoLotServiceImpl implements DoLotService {

    private final DoLotRepository doLotRepository;
    private final DoLotMapper doLotMapper;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private BaseSkuService baseSkuService;

    @Override
    public Map<String,Object> queryAll(DoLotQueryCriteria criteria, Pageable pageable){
        Page<DoLot> page = doLotRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(doLotMapper::toDto));
    }

    @Override
    public List<DoLotDto> queryAll(DoLotQueryCriteria criteria){
        return doLotMapper.toDto(doLotRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DoLotDto findById(Long id) {
        DoLot doLot = doLotRepository.findById(id).orElseGet(DoLot::new);
        ValidationUtil.isNull(doLot.getId(),"DoLot","id",id);
        return doLotMapper.toDto(doLot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DoLotDto create(DoLot resources) {
        return doLotMapper.toDto(doLotRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DoLot resources) {
        DoLot doLot = doLotRepository.findById(resources.getId()).orElseGet(DoLot::new);
        ValidationUtil.isNull( doLot.getId(),"DoLot","id",resources.getId());
        doLot.copy(resources);
        doLotRepository.save(doLot);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            doLotRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DoLotDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DoLotDto doLot : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("库位", doLot.getAreaNo());
            map.put("wms批次号", doLot.getBatchNo());
            map.put("委托方id", doLot.getConsignorId());
            map.put("订单明细id", doLot.getDocItemId());
            map.put("有效期截止", doLot.getExpireTime());
            map.put("资金方id", doLot.getFundProviderId());
            map.put("是否坏品", doLot.getIsDamaged());
            map.put("原始批次号", doLot.getLotNo());
            map.put("商家id", doLot.getMerchantId());
            map.put("进仓单号", doLot.getPoCode());
            map.put("产品id", doLot.getProductId());
            map.put("生产日期", doLot.getProductionTime());
            map.put("店铺id", doLot.getShopId());
            map.put("数量", doLot.getTransactionQty());
            map.put("仓库id", doLot.getWarehouseId());
            map.put("入库时间", doLot.getWarehouseTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void genDoLot(String soNo,Long outTallyId,String merchantId,String warehouseId) {
        DoLot lot=new DoLot();
        StockOutTolly stockOutTolly=new StockOutTolly();
        stockOutTolly.setId(outTallyId);
        lot.setStockOutTolly(stockOutTolly);
        List<DoLot>lotList=doLotRepository.findAll(Example.of(lot));
        if (CollectionUtil.isNotEmpty(lotList)){
            throw new BadRequestException("有剩余理货批次信息，请先删除");
        }
        List<ActAllocationDetails>actDetails=wmsSupport.getActAllocationBySoNo(soNo);
        if (CollectionUtil.isEmpty(actDetails))
            throw new BadRequestException(soNo+"找不到出库单分配明细");
        for (ActAllocationDetails actDetail : actDetails) {
            DoLot doLot=new DoLot();
            doLot.setStockOutTolly(stockOutTolly);
            doLot.setConsignorId(merchantId);
            doLot.setWarehouseId(warehouseId);
            doLot.setFundProviderId(merchantId);
            doLot.setMerchantId(merchantId);
            doLot.setDocItemId(actDetail.getOrderlineno()+"");
            doLot.setBatchNo(actDetail.getLotnum());
            doLot.setAreaNo(actDetail.getLocation());
            BaseSku baseSku=baseSkuService.queryByGoodsNo(actDetail.getSku());
            if (StringUtil.isNotBlank(actDetail.getLotAtt02())){
                doLot.setExpireTime(new Timestamp(DateUtils.parseDate(actDetail.getLotAtt02()).getTime()));
                doLot.setProductionTime(new Timestamp(doLot.getExpireTime().getTime()-baseSku.getLifecycle()*24*3600*1000));
            }
            doLot.setIsDamaged(StringUtil.equals("良品",actDetail.getLotAtt08())?"0":"1");
            doLot.setLotNo(actDetail.getLotnum());
            doLot.setPoCode(actDetail.getLotAtt04());
            doLot.setProductId(baseSku.getGoodsCode());
            doLot.setTransactionQty(actDetail.getQty().intValue());
            doLot.setWarehouseTime(new Timestamp(DateUtils.parseDate(actDetail.getLotAtt03()).getTime()));
            doLot.setCustomerBatchNo(actDetail.getLotAtt09());
            create(doLot);
        }
    }
}
