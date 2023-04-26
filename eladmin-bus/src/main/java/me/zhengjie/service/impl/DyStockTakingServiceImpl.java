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
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DyStockTakingRepository;
import me.zhengjie.service.dto.DyStockTakingDto;
import me.zhengjie.service.dto.DyStockTakingQueryCriteria;
import me.zhengjie.service.mapstruct.DyStockTakingMapper;
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
* @date 2021-09-26
**/
@Service
@RequiredArgsConstructor
public class DyStockTakingServiceImpl implements DyStockTakingService {

    private final DyStockTakingRepository dyStockTakingRepository;
    private final DyStockTakingMapper dyStockTakingMapper;

    @Autowired
    private DyStockTakingDetailService dyStockTakingDetailService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private InboundOrderService inboundOrderService;

    @Autowired
    private ReturnGatherService returnGatherService;

    @Override
    public Map<String,Object> queryAll(DyStockTakingQueryCriteria criteria, Pageable pageable){
        Page<DyStockTaking> page = dyStockTakingRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dyStockTakingMapper::toDto));
    }

    @Override
    public List<DyStockTakingDto> queryAll(DyStockTakingQueryCriteria criteria){
        return dyStockTakingMapper.toDto(dyStockTakingRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DyStockTakingDto findById(Long id) {
        DyStockTaking dyStockTaking = dyStockTakingRepository.findById(id).orElseGet(DyStockTaking::new);
        ValidationUtil.isNull(dyStockTaking.getId(),"DyStockTaking","id",id);
        return dyStockTakingMapper.toDto(dyStockTaking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DyStockTakingDto create(DyStockTaking resources) {
        resources.setOccurrenceTime(System.currentTimeMillis()/1000);
        resources.setCreateUserId(SecurityUtils.getCurrentUserId());
        resources.setIsSuccess("0");
        DyStockTaking dyStockTaking=dyStockTakingRepository.save(resources);
        String warehoursNo=null;
        resources.setStatus("0");
        resources.setIdempotentNo("STOCK"+DateUtils.format(new Date(),"yyyyMMddHHmmssSSS"));
        if (CollectionUtil.isNotEmpty(resources.getItemList())){
            for (DyStockTakingDetail dyStockTakingDetail : resources.getItemList()) {
                BaseSku baseSku=baseSkuService.queryByGoodsNo(dyStockTakingDetail.getGoodsNo());
                if (warehoursNo==null){
                    warehoursNo=baseSku.getWarehouseCode();
                }else {
                    if (!StringUtil.equals(warehoursNo,baseSku.getWarehouseCode()))
                        throw new BadRequestException("货号："+dyStockTakingDetail.getGoodsNo()+"与其他货号的仓编码不统一");
                }
                if (StringUtil.isBlank(dyStockTakingDetail.getEvidence())){
                    dyStockTakingDetail.setEvidence("[]");
                }
                dyStockTakingDetail.setTakingId(dyStockTaking.getId());
                dyStockTakingDetailService.create(dyStockTakingDetail);
            }
        }
        return dyStockTakingMapper.toDto(dyStockTaking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DyStockTaking resources) {
        DyStockTaking dyStockTaking = dyStockTakingRepository.findById(resources.getId()).orElseGet(DyStockTaking::new);
        ValidationUtil.isNull( dyStockTaking.getId(),"DyStockTaking","id",resources.getId());
        if (CollectionUtil.isNotEmpty(resources.getItemList())){
            for (DyStockTakingDetail detail : resources.getItemList()) {
                if (detail.getId()!=null)
                    dyStockTakingDetailService.update(detail);
                else {
                    detail.setTakingId(dyStockTaking.getId());
                    dyStockTakingDetailService.create(detail);
                }
            }
        }
        dyStockTaking.copy(resources);
        dyStockTakingRepository.save(dyStockTaking);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dyStockTakingRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DyStockTakingDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DyStockTakingDto dyStockTaking : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("店铺id", dyStockTaking.getShopId());
            map.put("盘点类型", dyStockTaking.getTakingType());
            map.put("盘点完成时间", dyStockTaking.getOccurrenceTime());
            map.put("备注", dyStockTaking.getRemark());
            map.put("创建人", dyStockTaking.getCreateUserId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void push() {
        DyStockTaking wrapper=new DyStockTaking();
        wrapper.setIsSuccess("0");
        List<DyStockTaking>list=dyStockTakingRepository.findAll(Example.of(wrapper));
        if (CollectionUtil.isEmpty(list))
            return;
        for (DyStockTaking dyStockTaking : list) {
            List<DyStockTakingDetail>detailList=dyStockTakingDetailService.queryByTalkingId(dyStockTaking.getId());
            dyStockTaking.setItemList(detailList);
            try {
                douyinService.pushStockTaking(dyStockTaking);
                update(dyStockTaking);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Map<String, Object>> uploadSku(List<Map<String, Object>> maps,Long shopId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            int sNo = Integer.valueOf(map.get("序号").toString());
            String goodsNo = map.get("货号*") != null ? map.get("货号*").toString() : null;
            String qualityGrade = map.getOrDefault("质量等级*",null) != null ? map.get("质量等级*").toString() : null;
            String reasonCode = map.getOrDefault("盘点原因*",null) != null ? map.get("盘点原因*").toString() : null;
            String quantity = map.get("数量*") != null ? map.get("数量*").toString() : null;
            if (StringUtil.isBlank(goodsNo))
                throw new BadRequestException("第" + sNo + "行，货号不能为空");
            if (StringUtil.isBlank(quantity))
                throw new BadRequestException("第" + sNo + "行，数量不能为空");
            if (StringUtil.isBlank(qualityGrade))
                throw new BadRequestException("第" + sNo + "行，质量等级不能为空");
            if (StringUtil.isBlank(reasonCode))
                throw new BadRequestException("第" + sNo + "行，盘点原因不能为空");
            BaseSku baseSku=baseSkuService.queryByGoodsNoAndShop(goodsNo,shopId);
            if (baseSku==null)
                throw new BadRequestException("第" + sNo + "行，货号不存在");
            if (StringUtil.isBlank(baseSku.getOuterGoodsNo()))
                throw new BadRequestException("第" + sNo + "行，没有抖音货号");
            Map<String, Object> resultItem = new HashMap<>();
            resultItem.put("goodsNo", goodsNo);
            resultItem.put("quantity", quantity);
            resultItem.put("reasonCode", StringUtil.equals("退货盘点",reasonCode)?"2":"1");
            resultItem.put("qualityGrade", StringUtil.equals("良品",qualityGrade)?"1":"2");
            result.add(resultItem);
        }
        return result;
    }

    @Override
    public DyStockTaking queryByIdempotentNo(String idempotentNo) {
        return dyStockTakingRepository.findByIdempotentNo(idempotentNo);
    }

    @Override
    public Map<String, Object> trackInboundOrderNo(String wmsBatch, String location) {
        JSONArray trsLogs = wmsSupport.trackDocNoByLotNumAndLocation(wmsBatch,location);
        if (CollectionUtils.isEmpty(trsLogs))
            throw new BadRequestException("找不到对应的入库单");
        Set<String> docNos = new TreeSet<>();
        for (int i = 0; i < trsLogs.size(); i++) {
            JSONObject trsLog = trsLogs.getJSONObject(i);
            docNos.add(trsLog.getStr("docno"));
        }
        StringBuilder outNo = new StringBuilder();
        for (String docNo : docNos) {
            if (docNo.indexOf("RK")==0){
                InboundOrder inboundOrder = inboundOrderService.queryByOrderNo(docNo);
                if (StringUtil.isNotBlank(inboundOrder.getOutNo())){
                    outNo.append(inboundOrder.getOutNo()).append(",");
                }
            }else if (docNo.indexOf("THTZ")==0){

            }

        }
        Map<String,Object>map=new HashMap<>();
        outNo.replace(outNo.length()-1,outNo.length(),"");
        map.put("outNos",outNo);
        return map;
    }

    @Override
    public void pushProcess(Long id) throws Exception {
        DyStockTaking dyStockTaking = queryById(id);
        dyStockTaking.setItemList(dyStockTakingDetailService.queryByTalkingId(dyStockTaking.getId()));
        if (StringUtil.equals(dyStockTaking.getStatus(),"0")){
            //必须是已创建状态才能推送审核
            douyinService.pushStockTaking(dyStockTaking);
            dyStockTaking.setStatus("1");
            update(dyStockTaking);
        }else
            throw new BadRequestException("非创建状态不能推送盘点单审核");
    }

    @Override
    public DyStockTaking queryById(Long id) {
        DyStockTaking dyStockTaking = dyStockTakingRepository.findById(id).orElseGet(DyStockTaking::new);
        ValidationUtil.isNull(dyStockTaking.getId(),"DyStockTaking","id",id);
        return dyStockTaking;
    }

    @Override
    public void pushSuccess(Long id) throws Exception {
        DyStockTaking dyStockTaking = queryById(id);
        dyStockTaking.setItemList(dyStockTakingDetailService.queryByTalkingId(dyStockTaking.getId()));
        if (StringUtil.equals(dyStockTaking.getStatus(),"2")){
            //必须是审核通过状态才能推送完成盘点
            douyinService.pushStockTaking(dyStockTaking);
            dyStockTaking.setStatus("5");
            update(dyStockTaking);
        }else
            throw new BadRequestException("非审核通过状态不能推送盘点单完成");
    }

    @Override
    public void pushCancel(Long id) throws Exception {
        DyStockTaking dyStockTaking = queryById(id);
        dyStockTaking.setItemList(dyStockTakingDetailService.queryByTalkingId(id));
        dyStockTaking.setStatus("8");
        douyinService.pushStockTaking(dyStockTaking);
        dyStockTaking.setIsSuccess("0");
        update(dyStockTaking);
    }
}
