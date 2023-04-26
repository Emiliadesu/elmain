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

import cn.hutool.core.date.DatePattern;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.service.*;
import me.zhengjie.support.zhuozhi.ZhuozhiSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.HezhuInfoRepository;
import me.zhengjie.service.dto.HezhuInfoDto;
import me.zhengjie.service.dto.HezhuInfoQueryCriteria;
import me.zhengjie.service.mapstruct.HezhuInfoMapper;
import me.zhengjie.utils.enums.ClearBusTypeEnum;
import me.zhengjie.utils.enums.ClearInfoStatusEnum;
import me.zhengjie.utils.enums.ClearTransStatusEnum;
import me.zhengjie.utils.enums.HeZhuInfoStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
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
* @author luob
* @date 2021-08-26
**/
@Service
@RequiredArgsConstructor
public class HezhuInfoServiceImpl implements HezhuInfoService {

    private final HezhuInfoRepository hezhuInfoRepository;
    private final HezhuInfoMapper hezhuInfoMapper;

    @Autowired
    private HezhuDetailsService hezhuDetailsService;

    @Autowired
    private HezhuLogService hezhuLogService;

    @Autowired
    private ZhuozhiSupport zhuozhiSupport;

    @Autowired
    private InboundOrderService inboundOrderService;

    @Autowired
    private OutboundOrderService outboundOrderService;

    @Autowired
    private ClearInfoService clearInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createHeZhuByClear(ClearInfo clearInfo) {
        HezhuInfo exist = queryByClearNo(clearInfo.getClearNo());
        if (exist != null)
            throw new BadRequestException("核注单已生成："+ exist.getOrderNo());
        HezhuInfo hezhuInfo = new HezhuInfo();
        hezhuInfo.setCustomersId(clearInfo.getCustomersId());
        hezhuInfo.setShopId(clearInfo.getShopId());
        hezhuInfo.setClearCompanyId(clearInfo.getClearCompanyId());
        hezhuInfo.setTradeType(clearInfo.getTradeType());
        hezhuInfo.setClearId(clearInfo.getId());
        hezhuInfo.setClearNo(clearInfo.getClearNo());
        hezhuInfo.setOrderNo(genOrderNo());
        hezhuInfo.setStatus(HeZhuInfoStatusEnum.STATUS_800.getCode());
        hezhuInfo.setBusType(clearInfo.getBusType());
        hezhuInfo.setSkuNum(clearInfo.getSkuNum());
        hezhuInfo.setTotalNum(clearInfo.getTotalNum());
        hezhuInfo.setGrossWeight(clearInfo.getGroosWeight());
        hezhuInfo.setNetWeight(clearInfo.getNetWeight());
        hezhuInfo.setTotalPrice(clearInfo.getSumMoney());
        hezhuInfo.setInWareHose(clearInfo.getInWarehose());
        hezhuInfo.setEntryNo(clearInfo.getEntryNo());
        hezhuInfo.setDeclNo(clearInfo.getDeclNo());
        hezhuInfo.setRefQdCode(clearInfo.getRefQdCode());
        hezhuInfo.setBooksNo(clearInfo.getBooksNo());
        hezhuInfo.setRefBooksNo(clearInfo.getRefBooksNo());
        hezhuInfo.setTransWay(clearInfo.getTransWay());
        hezhuInfo.setInPort(clearInfo.getInPort());
        hezhuInfo.setShipCountry(clearInfo.getShipCountry());
        hezhuInfo.setOrderSource(clearInfo.getOrderSource());
        HezhuInfoDto save = create(hezhuInfo);
        List<ClearDetails> details = clearInfo.getDetails();
        if (CollectionUtils.isNotEmpty(details)) {
            List<HezhuDetails> saveDetails = new ArrayList<>();
            for (ClearDetails clearDetails : details) {
                HezhuDetails hezhuDetails = new HezhuDetails();
                hezhuDetails.setOrderId(save.getId());
                hezhuDetails.setOrderNo(save.getOrderNo());
                hezhuDetails.setSeqNo(clearDetails.getSeqNo());
                hezhuDetails.setGoodsId(clearDetails.getGoodsId());
                hezhuDetails.setGoodsCode(clearDetails.getGoodsCode());
                hezhuDetails.setGoodsName(clearDetails.getGoodsName());
                hezhuDetails.setGoodsNo(clearDetails.getGoodsNo());
                hezhuDetails.setOuterGoodsNo(clearDetails.getOuterGoodsNo());
                hezhuDetails.setHsCode(clearDetails.getHsCode());
                hezhuDetails.setRecordNo(clearDetails.getRecordNo());
                hezhuDetails.setNetWeight(clearDetails.getNetWeight());
                hezhuDetails.setGrossWeight(clearDetails.getGrossWeight());
                hezhuDetails.setLegalUnit(clearDetails.getLegalUnit());
                hezhuDetails.setLegalUnitCode(clearDetails.getLegalUnitCode());
                hezhuDetails.setLegalNum(clearDetails.getLegalNum());
                hezhuDetails.setSecondUnit(clearDetails.getSecondUnit());
                hezhuDetails.setSecondUnitCode(clearDetails.getSecondUnitCode());
                hezhuDetails.setSecondNum(clearDetails.getSecondNum());
                hezhuDetails.setQty(clearDetails.getQty());
                hezhuDetails.setUnit(clearDetails.getUnit());
                hezhuDetails.setPrice(clearDetails.getPrice());
                hezhuDetails.setTotalPrice(clearDetails.getTotalPrice());
                hezhuDetails.setProperty(clearDetails.getProperty());
                hezhuDetails.setCurrency(clearDetails.getCurrency());
                hezhuDetails.setMakeCountry(clearDetails.getMakeCountry());
                saveDetails.add(hezhuDetails);
            }
            hezhuDetailsService.createBatch(saveDetails);
        }

    }

    @Override
    public HezhuInfo queryByClearNo(String clearNo) {
        return hezhuInfoRepository.findByClearNo(clearNo);
    }


    @Override
    public Map<String,Object> queryAll(HezhuInfoQueryCriteria criteria, Pageable pageable){
        Page<HezhuInfo> page = hezhuInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(hezhuInfoMapper::toDto));
    }

    @Override
    public List<HezhuInfoDto> queryAll(HezhuInfoQueryCriteria criteria){
        return hezhuInfoMapper.toDto(hezhuInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public HezhuInfoDto findById(Long id) {
        HezhuInfo hezhuInfo = hezhuInfoRepository.findById(id).orElseGet(HezhuInfo::new);
        ValidationUtil.isNull(hezhuInfo.getId(),"HezhuInfo","id",id);
        return hezhuInfoMapper.toDto(hezhuInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HezhuInfoDto create(HezhuInfo resources) {
        if(hezhuInfoRepository.findByOrderNo(resources.getOrderNo()) != null){
            throw new EntityExistException(HezhuInfo.class,"order_no",resources.getOrderNo());
        }
        return hezhuInfoMapper.toDto(hezhuInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(HezhuInfo resources) {
        HezhuInfo hezhuInfo = hezhuInfoRepository.findById(resources.getId()).orElseGet(HezhuInfo::new);
        ValidationUtil.isNull( hezhuInfo.getId(),"HezhuInfo","id",resources.getId());
        HezhuInfo hezhuInfo1 = null;
        hezhuInfo1 = hezhuInfoRepository.findByOrderNo(resources.getOrderNo());
        if(hezhuInfo1 != null && !hezhuInfo1.getId().equals(hezhuInfo.getId())){
            throw new EntityExistException(HezhuInfo.class,"order_no",resources.getOrderNo());
        }
        hezhuInfo.copy(resources);
        hezhuInfoRepository.save(hezhuInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            hezhuInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<HezhuInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HezhuInfoDto hezhuInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("单据编号", hezhuInfo.getOrderNo());
            map.put("状态", hezhuInfo.getStatus());
            map.put("海关状态", hezhuInfo.getCustomsStatus());
            map.put("客户ID", hezhuInfo.getCustomersId());
            map.put("店铺ID", hezhuInfo.getShopId());
            map.put("清关抬头ID", hezhuInfo.getClearCompanyId());
            map.put("业务类型", hezhuInfo.getBusType());
            map.put("预估SKU数量", hezhuInfo.getSkuNum());
            map.put("预估件数", hezhuInfo.getTotalNum());
            map.put("毛重", hezhuInfo.getGrossWeight());
            map.put("净重", hezhuInfo.getNetWeight());
            map.put("入库仓", hezhuInfo.getInWareHose());
            map.put("报关单号", hezhuInfo.getEntryNo());
            map.put("报检单号", hezhuInfo.getDeclNo());
            map.put("监管方式", hezhuInfo.getRegulatoryWay());
            map.put("报关类型", hezhuInfo.getClearType());
            map.put("QD单号", hezhuInfo.getQdCode());
            map.put("关联单证编码", hezhuInfo.getRefQdCode());
            map.put("账册编号", hezhuInfo.getBooksNo());
            map.put("关联账册编号", hezhuInfo.getRefBooksNo());
            map.put("运输方式", hezhuInfo.getTransWay());
            map.put("进境关别", hezhuInfo.getInPort());
            map.put("启运国", hezhuInfo.getShipCountry());
            map.put("清关开始时间", hezhuInfo.getClearStartTime());
            map.put("清关完成时间", hezhuInfo.getClearEndTime());
            map.put("服务完成时间", hezhuInfo.getFinishTime());
            map.put("创建者", hezhuInfo.getCreateBy());
            map.put("创建时间", hezhuInfo.getCreateTime());
            map.put("更新者", hezhuInfo.getUpdateBy());
            map.put("更新时间", hezhuInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    private synchronized String genOrderNo() {
        int tryCount = 0;
        String qz = "HZ";
        String time = DateUtils.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        String result = qz + time;
        Random random = new Random();
        for (int i=0;i<4; i++) {
            result += random.nextInt(10);
        }
        HezhuInfo order = queryByOrderNo(result);
        if (order != null && tryCount < 4) {
            genOrderNo();
            tryCount++;
        }
        return result;
    }

    @Override
    public HezhuInfo queryByOrderNo(String orderNo) {
        return hezhuInfoRepository.findByOrderNo(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOptNode(HezhuLog log) {
        HezhuInfo hezhuInfo = queryById(log.getOrderId());
        if (hezhuInfo == null)
            throw new BadRequestException("单据不存在");
        if (StringUtils.equals(hezhuInfo.getStatus().toString(), HeZhuInfoStatusEnum.STATUS_830.getCode().toString()))
            throw new BadRequestException("服务完成不能再上传状态");
        if (StringUtils.equals(log.getOptNode(), HeZhuInfoStatusEnum.STATUS_800.getCode().toString()))
            throw new BadRequestException("不用上传创建状态");
        if (Integer.valueOf(log.getOptNode()) <= hezhuInfo.getStatus().intValue())
            throw new BadRequestException("请按状态顺序上传节点");
        if (StringUtils.equals(log.getOptNode(), HeZhuInfoStatusEnum.STATUS_815.getCode().toString())) {
            if (StringUtils.isEmpty(log.getMsg())) {
                throw new BadRequestException("清关开始请将QD单号填在备注中");
            }else {
                hezhuInfo.setQdCode(log.getMsg());
            }
        }
        hezhuInfo.setStatus(Integer.valueOf(log.getOptNode()));
        if (StringUtils.equals(log.getOptNode(), HeZhuInfoStatusEnum.STATUS_815.getCode().toString())) {
            hezhuInfo.setClearStartTime(log.getCreateTime());
        }
        if (StringUtils.equals(log.getOptNode(), HeZhuInfoStatusEnum.STATUS_825.getCode().toString())) {
            hezhuInfo.setClearEndTime(log.getCreateTime());
        }
        if (StringUtils.equals(log.getOptNode(), HeZhuInfoStatusEnum.STATUS_830.getCode().toString())) {
            hezhuInfo.setFinishTime(log.getCreateTime());
        }
        update(hezhuInfo);
        hezhuLogService.create(log);
        if (StringUtils.equals(hezhuInfo.getOrderSource(), "1")) {
            // 卓志单据
            zhuozhiSupport.noticeHeZhuStatus(hezhuInfo);
        }

    }

    @Override
    public HezhuInfo queryById(Long id) {
        return  hezhuInfoRepository.findById(id).orElseGet(HezhuInfo::new);
    }

    @Override
    public void spawnInOutBoundOrder(Long[] ids) {
        List<Long> idList = new ArrayList<>(Arrays.asList(ids));
        List<HezhuInfo>hezhuInfoList=hezhuInfoRepository.findAllById(idList);
        for (HezhuInfo hezhuInfo : hezhuInfoList) {
            List<HezhuDetails>detailList=hezhuDetailsService.queryByHezhuId(hezhuInfo.getId());
            ClearInfo clearInfo=clearInfoService.queryByClearNo(hezhuInfo.getClearNo());
            if (StringUtil.equals(hezhuInfo.getBusType(), ClearBusTypeEnum.TYPE_01.getCode())
                    ||StringUtil.equals(hezhuInfo.getBusType(), ClearBusTypeEnum.TYPE_02.getCode())
                    ||StringUtil.equals(hezhuInfo.getBusType(), ClearBusTypeEnum.TYPE_03.getCode())){
                        //入库
                InboundOrder inboundOrder=inboundOrderService.queryByOutNo(hezhuInfo.getClearNo());
                if (inboundOrder!=null)
                    continue;
                inboundOrder=new InboundOrder();
                inboundOrder.setCustomersId(hezhuInfo.getCustomersId());
                inboundOrder.setShopId(hezhuInfo.getShopId());
                inboundOrder.setOutNo(hezhuInfo.getClearNo());
                inboundOrder.setOrderType("0");
                inboundOrder.setExpectArriveTime(clearInfo.getExpectArrivalTime());
                inboundOrder.setTallyWay("0");
                List<InboundOrderDetails>details=new ArrayList<>();
                for (HezhuDetails hezhuDetails : detailList) {
                    InboundOrderDetails detail=new InboundOrderDetails();
                    detail.setGoodsNo(hezhuDetails.getGoodsNo());
                    detail.setExpectNum(Integer.valueOf(hezhuDetails.getQty()));
                    details.add(detail);
                }
                inboundOrder.setDetails(details);
                inboundOrderService.create(inboundOrder);
            }else {
                OutboundOrder outboundOrder=outboundOrderService.queryByOutNo(hezhuInfo.getClearNo());
                if (outboundOrder!=null)
                    continue;
                outboundOrder = new OutboundOrder();
                outboundOrder.setCustomersId(hezhuInfo.getCustomersId());
                outboundOrder.setShopId(hezhuInfo.getShopId());
                outboundOrder.setOutNo(hezhuInfo.getClearNo());
                outboundOrder.setOrderType("0");
                outboundOrder.setTallyWay("0");
                outboundOrder.setExpectDeliverTime(clearInfo.getExpectArrivalTime());
                List<OutboundOrderDetails>details=new ArrayList<>();
                for (HezhuDetails hezhuDetails : detailList) {
                    OutboundOrderDetails detail=new OutboundOrderDetails();
                    detail.setGoodsNo(hezhuDetails.getGoodsNo());
                    detail.setExpectNum(Integer.valueOf(hezhuDetails.getQty()));
                    details.add(detail);
                }
                outboundOrder.setDetails(details);
                outboundOrderService.create(outboundOrder);
            }
        }
    }
}
