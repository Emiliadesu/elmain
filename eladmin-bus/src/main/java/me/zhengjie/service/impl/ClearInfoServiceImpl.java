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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.ExpandDto;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.support.zhuozhi.ZhuozhiSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ClearInfoRepository;
import me.zhengjie.service.dto.ClearInfoDto;
import me.zhengjie.service.dto.ClearInfoQueryCriteria;
import me.zhengjie.service.mapstruct.ClearInfoMapper;
import me.zhengjie.utils.enums.ClearBusTypeEnum;
import me.zhengjie.utils.enums.ClearInfoStatusEnum;
import me.zhengjie.utils.enums.ClearTransStatusEnum;
import me.zhengjie.utils.enums.HeZhuInfoStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-03-07
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class ClearInfoServiceImpl implements ClearInfoService {

    private final ClearInfoRepository clearInfoRepository;
    private final ClearInfoMapper clearInfoMapper;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private ClearDetailsService clearDetailsService;

    @Autowired
    private ClearOptLogService clearOptLogService;

    @Autowired
    private ClearContainerService clearContainerService;

    @Autowired
    private ZhuozhiSupport zhuozhiSupport;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private HezhuInfoService hezhuInfoService;

    @Autowired
    private TransInfoService transInfoService;

    @Override
    public Map<String,Object> queryAll(ClearInfoQueryCriteria criteria, Pageable pageable){
        Page<ClearInfo> page = clearInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        List<ClearInfo> content = page.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            for(ClearInfo clearInfo : content) {
                List<ExpandDto> expands = new ArrayList<>();
                HezhuInfo hezhuInfo = hezhuInfoService.queryByClearNo(clearInfo.getClearNo());
                if (hezhuInfo != null) {
                    ExpandDto expandDto = new ExpandDto();
                    expandDto.setOrderType("核注单");
                    expandDto.setOrderNo(hezhuInfo.getOrderNo());
                    expandDto.setStatus(HeZhuInfoStatusEnum.getDesc(hezhuInfo.getStatus()));
                    expands.add(expandDto);
                }
                TransInfo transInfo = transInfoService.queryByClearNo(clearInfo.getClearNo());
                if (transInfo != null) {
                    ExpandDto expandDto = new ExpandDto();
                    expandDto.setOrderType("运输单");
                    expandDto.setOrderNo(transInfo.getOrderNo());
                    expandDto.setStatus(ClearTransStatusEnum.getDesc(transInfo.getStatus()));
                    expands.add(expandDto);
                }
                clearInfo.setExpands(expands);
            }
        }
        return PageUtil.toPage(page);
    }

    @Override
    public List<ClearInfoDto> queryAll(ClearInfoQueryCriteria criteria){
        return clearInfoMapper.toDto(clearInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ClearInfoDto findById(Long id) {
        ClearInfo clearInfo = clearInfoRepository.findById(id).orElseGet(ClearInfo::new);
        ValidationUtil.isNull(clearInfo.getId(),"ClearInfo","id",id);
        return clearInfoMapper.toDto(clearInfo);
    }

    @Override
    @Transactional
    public ClearInfo createClearInfo(ClearInfo clearInfo) {
        if(StringUtils.isEmpty(clearInfo.getClearNo())) {
            clearInfo.setClearNo(genOrderNo());
        }
        clearInfo.setStatus(ClearInfoStatusEnum.STATUS_CREATE.getCode());// 创建状态
        if (StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_01.getCode())
                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_02.getCode())
                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_03.getCode())
                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_05.getCode())) {
            clearInfo.setRefOrderType("1");
        }else {
            clearInfo.setRefOrderType("2");
        }

        List<ClearDetails> details = clearInfo.getDetails();

        ShopInfoDto shopInfoDto = shopInfoService.queryById(clearInfo.getShopId());
        clearInfo.setClearCompanyId(shopInfoDto.getServiceId());

        clearInfo.setSkuNum(details.size());
        BigDecimal totalNum = BigDecimal.ZERO;// 总件数
        BigDecimal sumMoney = BigDecimal.ZERO;// 总金额
        BigDecimal totalGroosWeight = BigDecimal.ZERO;// 总毛重
        BigDecimal totalNetWeight = BigDecimal.ZERO;// 总净重
        if (CollectionUtils.isNotEmpty(details)) {
            for (int i = 0; i < details.size(); i++) {
                BaseSku baseSku = baseSkuService.queryByGoodsNo(details.get(i).getGoodsNo());
                details.get(i).setClearNo(clearInfo.getClearNo());
                details.get(i).setSeqNo(i+1);
                details.get(i).setGoodsId(baseSku.getId());
                details.get(i).setGoodsCode(baseSku.getGoodsCode());
                details.get(i).setHsCode(baseSku.getHsCode());
                details.get(i).setGoodsNo(baseSku.getGoodsNo());
                if (StringUtil.isBlank(details.get(i).getOuterGoodsNo()))
                    details.get(i).setOuterGoodsNo(baseSku.getGoodsNo());
                details.get(i).setRecordNo(baseSku.getRecordNo());
                BigDecimal qty = new BigDecimal(details.get(i).getQty());
                BigDecimal singleGrossWeight = new BigDecimal(details.get(i).getGrossWeight()).divide(qty , 5, BigDecimal.ROUND_HALF_UP);
                BigDecimal singleNetWeight = new BigDecimal(details.get(i).getNetWeight()).divide(qty , 5, BigDecimal.ROUND_HALF_UP);
                details.get(i).setNetWeight(String.valueOf(singleNetWeight));
                details.get(i).setGrossWeight(String.valueOf(singleGrossWeight));
                details.get(i).setLegalUnitCode(baseSku.getLegalUnitCode());
                BigDecimal totalLegalNum = baseSku.getLegalNum().multiply(qty);
                details.get(i).setLegalNum(String.valueOf(totalLegalNum));
                if (StringUtils.isNotEmpty(baseSku.getSecondUnit())) {
                    details.get(i).setSecondUnitCode(String.valueOf(baseSku.getSecondUnitCode()));
                    BigDecimal totalSecondNum = baseSku.getSecondNum().multiply(qty);
                    details.get(i).setSecondNum(String.valueOf(totalSecondNum));
                }
                details.get(i).setUnit(baseSku.getUnitCode());
                BigDecimal totalPrice = new BigDecimal(details.get(i).getPrice()).multiply(qty);
                details.get(i).setTotalPrice(String.valueOf(totalPrice));
                details.get(i).setProperty(baseSku.getProperty());
                details.get(i).setMakeCountry(baseSku.getMakeContry());

                clearInfo.setCurrency(details.get(i).getCurrency());
                totalNum = totalNum.add(qty);
                sumMoney = sumMoney.add(totalPrice);
                totalGroosWeight = totalGroosWeight.add(new BigDecimal(details.get(i).getGrossWeight()));
                totalNetWeight = totalNetWeight.add(new BigDecimal(details.get(i).getNetWeight()));
            }

            clearInfo.setTotalNum(totalNum.intValue());
            clearInfo.setSumMoney(sumMoney);
            clearInfo.setGroosWeight(totalGroosWeight);
            clearInfo.setNetWeight(totalNetWeight);
            ClearInfo save = clearInfoRepository.save(clearInfo);
            for (ClearDetails clearDetails : details) {
                clearDetails.setClearId(save.getId());
            }
            clearDetailsService.saveBatch(details);
        }else {
            clearInfoRepository.save(clearInfo);
        }
        ClearOptLog log = new ClearOptLog(
                clearInfo.getId(),
                clearInfo.getStatus(),
                new Timestamp(System.currentTimeMillis()),
                "",
                ""
        );
        clearOptLogService.create(log);

        // 如果是非报关的就直接生成核注单
//        if (StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_02.getCode())
//                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_03.getCode())
//                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_04.getCode())
//                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_05.getCode())) {
//            hezhuInfoService.createHeZhuByClear(clearInfo);
//        }
        return clearInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createHezhu(Long id) {
        ClearInfo clearInfo = queryByIdWithDetails(id);
        hezhuInfoService.createHeZhuByClear(clearInfo);
        if (StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_02.getCode())
                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_03.getCode())
                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_04.getCode())
                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_05.getCode())) {
            clearInfo.setStatus(ClearInfoStatusEnum.STATUS_DONE.getCode());
            update(clearInfo);
        }
    }

    @Override
    public void createTrans(Long id) {
        ClearInfo clearInfo = queryByIdWithDetails(id);
        transInfoService.createTransByClear(clearInfo);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClearInfoDto create(ClearInfo resources) {
        if(clearInfoRepository.findByClearNo(resources.getClearNo()) != null){
            throw new EntityExistException(ClearInfo.class,"clear_no",resources.getClearNo());
        }
        resources.setOrderSource("0");
        return clearInfoMapper.toDto(createClearInfo(resources));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addClearInfo(String dec, Long customerId) {
        log.info("收到清关单据下发：{}", dec);
        JSONObject resOrder = JSONObject.parseObject(dec);
        String orderCode = resOrder.getString("orderCode");
        String contractNum = resOrder.getString("contractNum");
        Integer tradeType = resOrder.getInteger("tradeType");
        String inProcessBl = resOrder.getString("inProcessBl");
        String iePort = resOrder.getString("iePort");
        String switchedBl = resOrder.getString("switchedBl");
        String customsType = resOrder.getString("customsType");
        String transitEnterpriseCode = resOrder.getString("transitEnterpriseCode");
        String accountNumber = resOrder.getString("accountNumber");
        String rltAccountNumber = resOrder.getString("rltAccountNumber");
        String operateUnitCode = resOrder.getString("operateUnitCode");
        String trspMode = resOrder.getString("trspMode");
        String departureCountry = resOrder.getString("departureCountry");
        String tradeCountry = resOrder.getString("tradeCountry");
        String departurePort = resOrder.getString("departurePort");
        String linkedQdCode = resOrder.getString("linkedQdCode");
        String entryPort = resOrder.getString("entryPort");

        if (StringUtils.isBlank(operateUnitCode))
            throw new BadRequestException("operateUnitCode必填");
        // 通过operateUnitCode查询店铺
        ShopInfo shopInfo = shopInfoService.queryByShopCode(operateUnitCode);
        if (shopInfo == null)
            throw new BadRequestException("operateUnitCode未配置，请联系富立技术");
        if (StringUtils.isBlank(orderCode))
            throw new BadRequestException("orderCode必填");
        ClearInfo exist = queryByClearNo(orderCode);
        if (exist != null)
            throw new BadRequestException("orderCode已存在：" + orderCode);
        if (tradeType == null)
            throw new BadRequestException("tradeType必填");
        if (StringUtils.isBlank(customsType))
            throw new BadRequestException("customsType必填");
        if (StringUtils.isBlank(accountNumber))
            throw new BadRequestException("accountNumber必填");
        ClearInfo clearInfo = new ClearInfo();
        clearInfo.setCustomersId(customerId);
        clearInfo.setShopId(shopInfo.getId());
        clearInfo.setClearNo(orderCode);
        clearInfo.setContractNo(contractNum);
        clearInfo.setClearCompanyId(shopInfo.getServiceId());
        clearInfo.setInPort(entryPort);
        clearInfo.setTradeType(String.valueOf(tradeType));
        clearInfo.setOrderSource("1");
        clearInfo.setInPort(iePort);
        clearInfo.setInProcessBl(inProcessBl);
        clearInfo.setBillNo(inProcessBl);
        clearInfo.setSwitchedBl(switchedBl);
        clearInfo.setRefEnterpriseCode(transitEnterpriseCode);
        clearInfo.setBooksNo(accountNumber);
        clearInfo.setRefBooksNo(rltAccountNumber);
        clearInfo.setTransWay(trspMode);
        clearInfo.setShipCountry(departureCountry);
        clearInfo.setTradeCountry(tradeCountry);
        clearInfo.setShipPort(departurePort);
        clearInfo.setRefQdCode(linkedQdCode);
        clearInfo.setCreateBy("System");
        clearInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));

        JSONArray extRelateNo = resOrder.getJSONArray("extRelateNo");
        if (extRelateNo == null)
            throw new BadRequestException("extRelateNo必填");
        String refOrderType = "";
        String refOrderNo = "";
        for (int i = 0; i< extRelateNo.size(); i++) {
            String orderType = String.valueOf(extRelateNo.getJSONObject(i).getIntValue("orderType"));
            if (StringUtils.isNotEmpty(refOrderType)
                    && !StringUtils.contains(refOrderType, orderType)) {
                throw new BadRequestException("extRelateNo中存在不同单据类型");
            }
            refOrderType += orderType+ ",";
            String orderNo = extRelateNo.getJSONObject(i).getString("orderNo");
            if (StringUtils.contains(refOrderNo, orderNo)) {
                throw new BadRequestException("extRelateNo中存在相同单据编号");
            }
            refOrderNo += orderNo + ",";
            clearInfo.setRefOrderType(orderType);
        }
        clearInfo.setRefOrderNo(refOrderNo);

        switch (customsType) {
            case "30":// 一线进境
                clearInfo.setBusType(ClearBusTypeEnum.TYPE_01.getCode());
                break;
            case "40":// 区间流转
                if(StringUtils.equals(refOrderType, "1")) {
                    clearInfo.setBusType(ClearBusTypeEnum.TYPE_02.getCode());
                }else {
                    clearInfo.setBusType(ClearBusTypeEnum.TYPE_04.getCode());
                }
                break;
            case "190":// 区内流转
                if(StringUtils.equals(refOrderType, "1")) {
                    clearInfo.setBusType(ClearBusTypeEnum.TYPE_03.getCode());
                }else {
                    clearInfo.setBusType(ClearBusTypeEnum.TYPE_05.getCode());
                }
                break;
            case "180":// 转口
                clearInfo.setBusType(ClearBusTypeEnum.TYPE_10.getCode());
                break;
            case "171":// 销毁
                clearInfo.setBusType(ClearBusTypeEnum.TYPE_07.getCode());
                break;
            default:
                clearInfo.setBusType(customsType);
                break;
        }
        clearInfo.setStatus(ClearInfoStatusEnum.STATUS_CREATE.getCode());// 创建状态

        JSONArray goodsList = resOrder.getJSONArray("goodsList");
        if (goodsList == null)
            throw new BadRequestException("goodsList必填");

        clearInfo.setSkuNum(goodsList.size());
        BigDecimal totalNum = BigDecimal.ZERO;// 总件数
        BigDecimal sumMoney = BigDecimal.ZERO;// 总金额
        BigDecimal totalGroosWeight = BigDecimal.ZERO;// 总毛重
        BigDecimal totalNetWeight = BigDecimal.ZERO;// 总毛重

        List<ClearDetails> detailsSave = new ArrayList<>();
        for (int i = 0; i< goodsList.size(); i++) {
            Integer seqNo = goodsList.getJSONObject(i).getInteger("seqNo");
            String productCode = goodsList.getJSONObject(i).getString("productCode");
            String goodsName = goodsList.getJSONObject(i).getString("goodsName");
            BigDecimal grossWeight = goodsList.getJSONObject(i).getBigDecimal("grossWeight");
            BigDecimal netWeight = goodsList.getJSONObject(i).getBigDecimal("netWeight");
            BigDecimal quantitativeFirstQuantity = goodsList.getJSONObject(i).getBigDecimal("quantitativeFirstQuantity");
            BigDecimal quantitativeSecondQuantity = goodsList.getJSONObject(i).getBigDecimal("quantitativeSecondQuantity");
            BigDecimal qty = goodsList.getJSONObject(i).getBigDecimal("qty");
            String hsCode = goodsList.getJSONObject(i).getString("hsCode");
            BigDecimal price = goodsList.getJSONObject(i).getBigDecimal("price");
            String specification = goodsList.getJSONObject(i).getString("specification");
            String currency = goodsList.getJSONObject(i).getString("currency");
            BigDecimal totalPrice = goodsList.getJSONObject(i).getBigDecimal("totalPrice");
            String dealUnit = goodsList.getJSONObject(i).getString("dealUnit");
            String sourceCountry = goodsList.getJSONObject(i).getString("sourceCountry");
            String legalUnit = goodsList.getJSONObject(i).getString("legalUnit");
            String secondUnit = goodsList.getJSONObject(i).getString("secondUnit");
            if (StringUtils.isBlank(productCode))
                throw new BadRequestException("productCode必填");
            if (StringUtils.isBlank(goodsName))
                throw new BadRequestException("goodsName必填");
            if (grossWeight == null)
                throw new BadRequestException("grossWeight必填");
            if (netWeight == null)
                throw new BadRequestException("netWeight必填");
            if (quantitativeFirstQuantity == null)
                throw new BadRequestException("quantitativeFirstQuantity必填");
            if (qty == null)
                throw new BadRequestException("qty必填");
            if (StringUtils.isBlank(hsCode))
                throw new BadRequestException("hsCode必填");
            if (price == null)
                throw new BadRequestException("price必填");
            if (StringUtils.isBlank(currency))
                throw new BadRequestException("currency必填");
            if (totalPrice == null)
                throw new BadRequestException("totalPrice必填");
            if (StringUtils.isBlank(dealUnit))
                throw new BadRequestException("dealUnit必填");
            if (StringUtils.isBlank(sourceCountry))
                throw new BadRequestException("sourceCountry必填");
            if (StringUtils.isBlank(legalUnit))
                throw new BadRequestException("legalUnit必填");
            BaseSku baseSku = baseSkuService.queryByOutGoodsNo(productCode);
            if (baseSku == null)
                throw new BadRequestException("productCode不存在：" + productCode);
            totalNum = totalNum.add(qty);
            sumMoney = sumMoney.add(totalPrice);
            totalGroosWeight = totalGroosWeight.add(grossWeight);
            totalNetWeight = totalNetWeight.add(netWeight);
            clearInfo.setCurrency(currency);

            ClearDetails clearDetails = new ClearDetails();

            clearDetails.setClearNo(clearInfo.getClearNo());
            clearDetails.setSeqNo(seqNo);
            clearDetails.setGoodsId(baseSku.getId());
            clearDetails.setGoodsCode(baseSku.getGoodsCode());
            clearDetails.setGoodsName(goodsName);
            clearDetails.setHsCode(hsCode);
            clearDetails.setGoodsNo(baseSku.getGoodsNo());
            clearDetails.setOuterGoodsNo(productCode);
            clearDetails.setNetWeight(String.valueOf(netWeight));
            clearDetails.setGrossWeight(String.valueOf(grossWeight));
            clearDetails.setLegalUnitCode(legalUnit);
            clearDetails.setLegalNum(String.valueOf(quantitativeFirstQuantity));
            clearDetails.setSecondUnitCode(String.valueOf(secondUnit));
            clearDetails.setSecondNum(String.valueOf(quantitativeSecondQuantity));
            clearDetails.setQty(String.valueOf(qty));
            clearDetails.setUnit(dealUnit);
            clearDetails.setPrice(String.valueOf(price));
            clearDetails.setTotalPrice(String.valueOf(totalPrice));
            clearDetails.setProperty(specification);
            clearDetails.setCurrency(currency);
            clearDetails.setMakeCountry(sourceCountry);
            detailsSave.add(clearDetails);
        }
        clearInfo.setTotalNum(totalNum.intValue());
        clearInfo.setSumMoney(sumMoney);
        clearInfo.setGroosWeight(totalGroosWeight);
        clearInfo.setNetWeight(totalNetWeight);

        ClearInfo save = clearInfoRepository.save(clearInfo);
        for (ClearDetails clearDetails : detailsSave) {
            clearDetails.setClearId(save.getId());
        }
        clearDetailsService.saveBatch(detailsSave);

        ClearOptLog log = new ClearOptLog(
                clearInfo.getId(),
                clearInfo.getStatus(),
                new Timestamp(System.currentTimeMillis()),
                "",
                ""
        );
        clearOptLogService.create(log);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ClearInfo resources) {
        ClearInfo clearInfo = clearInfoRepository.findById(resources.getId()).orElseGet(ClearInfo::new);
        ValidationUtil.isNull( clearInfo.getId(),"ClearInfo","id",resources.getId());
        ClearInfo clearInfo1 = null;
        clearInfo1 = clearInfoRepository.findByClearNo(resources.getClearNo());
        if(clearInfo1 != null && !clearInfo1.getId().equals(clearInfo.getId())){
            throw new EntityExistException(ClearInfo.class,"clear_no",resources.getClearNo());
        }
        clearInfo.copy(resources);
        clearInfoRepository.save(clearInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            clearInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ClearInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ClearInfoDto clearInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("单据编号", clearInfo.getClearNo());
            map.put("客户ID", clearInfo.getCustomersId());
            map.put("店铺ID", clearInfo.getShopId());
            map.put("清关抬头_ID", clearInfo.getClearCompanyId());
            map.put("报关行ID", clearInfo.getSupplierId());
            map.put("业务类型", clearInfo.getBusType());
            map.put("申报模式", clearInfo.getDeclareMode());
            map.put("提单号", clearInfo.getBillNo());
            map.put("报关单号", clearInfo.getEntryNo());
            map.put("报检单号", clearInfo.getDeclNo());
            map.put("运输方式", clearInfo.getTransWay());
            map.put("入境口岸", clearInfo.getInPort());
            map.put("QD单号", clearInfo.getQdCode());
            map.put("预估SKU数量", clearInfo.getSkuNum());
            map.put("预估件数", clearInfo.getTotalNum());
            map.put("毛重", clearInfo.getGroosWeight());
            map.put("币种", clearInfo.getCurrency());
            map.put("主要产品", clearInfo.getPruduct());
            map.put("入库仓", clearInfo.getInWarehose());
            map.put("清关资料链接", clearInfo.getClearDataLink());
            map.put("概报放行单链接", clearInfo.getDraftDeclareDataLink());
            map.put("报关报检单链接", clearInfo.getEntryDataLink());
            map.put("备注", clearInfo.getRemark());
            map.put("总金额",clearInfo.getSumMoney());
            map.put("预估到港日期", clearInfo.getExpectArrivalTime());
            map.put("创建者", clearInfo.getCreateBy());
            map.put("创建时间", clearInfo.getCreateTime());
            map.put("更新者", clearInfo.getUpdateBy());
            map.put("更新时间", clearInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadDetails(Long id, HttpServletResponse response) throws IOException {
        ClearInfo clearInfo = queryByIdWithDetails(id);
        List<ClearDetails> details = clearInfo.getDetails();
        List<Map<String, Object>> listOrder = new ArrayList<>();
        Map<String,Object> mapOrder = new LinkedHashMap<>();
        mapOrder.put("单据编号", clearInfo.getClearNo());
        mapOrder.put("客户", customerInfoService.queryById(clearInfo.getCustomersId()).getCustNickName());
        mapOrder.put("店铺", shopInfoService.queryById(clearInfo.getShopId()).getName());
        mapOrder.put("业务类型", clearInfo.getBusType());
        mapOrder.put("申报模式", clearInfo.getDeclareMode());
        mapOrder.put("关联单据类型", clearInfo.getRefOrderType());
        mapOrder.put("关联单据号", clearInfo.getRefOrderNo());
        mapOrder.put("提单号", clearInfo.getBillNo());
        mapOrder.put("报关单号", clearInfo.getEntryNo());
        mapOrder.put("报检单号", clearInfo.getDeclNo());
        mapOrder.put("运输方式", clearInfo.getTransWay());
        mapOrder.put("入境口岸", clearInfo.getInPort());
        mapOrder.put("QD单号", clearInfo.getQdCode());
        mapOrder.put("毛重", clearInfo.getGroosWeight());
        mapOrder.put("关联账册编号", clearInfo.getRefBooksNo());
        mapOrder.put("关联单证编码", clearInfo.getRefQdCode());
        listOrder.add(mapOrder);
        List<Map<String, Object>> listDetails = new ArrayList<>();
        if (details != null) {
            for (ClearDetails detail : details) {
                Map<String,Object> mapDetails = new LinkedHashMap<>();
                mapDetails.put("货号", detail.getGoodsNo());
                mapDetails.put("HS编码", detail.getHsCode());
                mapDetails.put("名称", detail.getGoodsName());
                mapDetails.put("规格", detail.getProperty());
                mapDetails.put("申报计量单位", detail.getUnit());
                mapDetails.put("法定计量单位", detail.getLegalUnitCode());
                mapDetails.put("法定第二计量", detail.getSecondUnitCode());
                mapDetails.put("原产国", detail.getMakeCountry());
                mapDetails.put("申报单价", detail.getPrice());
                mapDetails.put("申报总价", detail.getTotalPrice());
                mapDetails.put("币制", detail.getCurrency());
                mapDetails.put("法定数量", detail.getLegalNum());
                mapDetails.put("第二法定数量", detail.getSecondNum());
                mapDetails.put("申报数量", detail.getQty());
                mapDetails.put("毛重", detail.getGrossWeight());
                mapDetails.put("净重", detail.getNetWeight());
                listDetails.add(mapDetails);
            }
        }
        FileUtil.downloadExcelDetails(listOrder, listDetails, response);
    }

    @Override
    public List<Map<String, Object>> uploadSku(List<Map<String, Object>> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            int sNo = Integer.valueOf(map.get("序号").toString());
            String goodsNo = map.get("货号") != null ? map.get("货号").toString() : null;
            String num = map.get("数量") != null ? map.get("数量").toString() : null;
            String currency = map.get("币制") != null ? map.get("币制").toString() : null;
            String price = map.get("单价") != null ? map.get("单价").toString() : null;
            String groosWeight = map.get("毛重(总)") != null ? map.get("毛重(总)").toString() : null;
            String netWeight = map.get("净重(总)") != null ? map.get("净重(总)").toString() : null;
            if (StringUtil.isBlank(goodsNo))
                throw new BadRequestException("第" + sNo + "行，货号不能为空");
            if (StringUtil.isBlank(num))
                throw new BadRequestException("第" + sNo + "行，数量不能为空");
            if (StringUtil.isBlank(currency))
                throw new BadRequestException("第" + sNo + "行，币制不能为空");
            if (StringUtil.isBlank(price))
                throw new BadRequestException("第" + sNo + "行，单价不能为空");
            if (StringUtil.isBlank(groosWeight))
                throw new BadRequestException("第" + sNo + "行，毛重不能为空");
            if (StringUtil.isBlank(netWeight))
                throw new BadRequestException("第" + sNo + "行，净重不能为空");
            Map<String, Object> resultItem = new HashMap<>();
            resultItem.put("goodsNo", goodsNo);
            resultItem.put("num", num);
            resultItem.put("currency", currency);
            resultItem.put("price", price);
            resultItem.put("groosWeight", groosWeight);
            resultItem.put("netWeight", netWeight);
            result.add(resultItem);
        }
        return result;
    }

    @Override
    public ClearInfo queryByClearNo(String clearNo) {
        return clearInfoRepository.findByClearNo(clearNo);
    }

    @Override
    public ClearInfo queryByIdWithDetails(Long id) {
        ClearInfo clearInfo = clearInfoRepository.findById(id).orElseGet(ClearInfo::new);
        List<ClearDetails> list = clearDetailsService.queryByClearId(clearInfo.getId());
        clearInfo.setDetails(list);
        List<ClearContainer> clearContainerList = clearContainerService.queryByClearId(clearInfo.getId());
        clearInfo.setClearContainerList(clearContainerList);
        return clearInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOptNode(ClearOptLog log) {
        ClearInfo clearInfo = queryByIdWithDetails(log.getClearId());
        if (clearInfo == null)
            throw new BadRequestException("清关数据不存在：" + log.getClearId());
        if (StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_02.getCode())
                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_03.getCode())
                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_04.getCode())
                || StringUtils.equals(clearInfo.getBusType(), ClearBusTypeEnum.TYPE_05.getCode())) {
            throw new BadRequestException("非报关单据无需上传报关节点");
        }
        if (StringUtils.equals(clearInfo.getStatus(), ClearInfoStatusEnum.STATUS_DONE.getCode()))
            throw new BadRequestException("服务完成不能再上传状态");
        if (StringUtils.equals(log.getOptNode(), ClearInfoStatusEnum.STATUS_CREATE.getCode()))
            throw new BadRequestException("不用上传创建状态");
        if (StringUtils.equals(log.getOptNode(), ClearInfoStatusEnum.STATUS_CLEAR_START.getCode())) {
            if (StringUtils.isEmpty(log.getRemark())) {
                throw new BadRequestException("清关开始请将报关单号填在备注中");
            }else {
                clearInfo.setEntryNo(log.getRemark());
            }
        }
        clearInfo.setStatus(log.getOptNode());
        update(clearInfo);
        clearOptLogService.create(log);
        if (StringUtils.equals(clearInfo.getOrderSource(), "1")) {
            // 卓志单据
            zhuozhiSupport.noticeClearStatus(clearInfo);
        }
    }

    private synchronized String genOrderNo() {
        int tryCount = 0;
        String qz = "CL";
        String time = DateUtils.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        String result = qz + time;
        Random random = new Random();
        for (int i=0;i<4; i++) {
            result += random.nextInt(10);
        }
        ClearInfo order = queryByClearNo(result);
        if (order != null && tryCount < 4) {
            genOrderNo();
            tryCount++;
        }
        return result;
    }
}
