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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.mq.CBOrderConsumer;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.dto.*;
import me.zhengjie.support.fuliPre.BaseSkuPackInfo;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.BaseSkuRepository;
import me.zhengjie.service.mapstruct.BaseSkuMapper;
import me.zhengjie.utils.constant.CustomsCodeConstant;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.constant.PlatformConstant;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBGoodsStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-04-15
**/
@CacheConfig(cacheNames = "baseSku")
@Service
@RequiredArgsConstructor
public class BaseSkuServiceImpl implements BaseSkuService {

    private final BaseSkuRepository baseSkuRepository;
    private final BaseSkuMapper baseSkuMapper;

    private final RedisUtils redisUtils;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private CustomsTariffService customsTariffService;

    @Autowired
    private SkuLogService skuLogService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private CustomsCodeService customsCodeService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Override
    public Map<String,Object> queryAll(BaseSkuQueryCriteria criteria, Pageable pageable){
        Page<BaseSku> page = baseSkuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(baseSkuMapper::toDto));
    }

    @Override
    public Map<String, Object> querySumStock(BaseSkuQueryCriteria criteria, Pageable pageable) {
        Page<BaseSku> page = baseSkuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        List<BaseSku> content = page.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            List<String> goodsNos = new ArrayList<>();
            List<StockDto> copyContent = new ArrayList<>();
            for (BaseSku baseSku : content) {
                goodsNos.add(baseSku.getGoodsNo());
                StockDto stockDto = BeanUtil.copyProperties(baseSku, StockDto.class);
                copyContent.add(stockDto);
            }
            JSONArray stockArray = wmsSupport.querySumStock(goodsNos);
            for (int i = 0; i < stockArray.size(); i++) {
                for (StockDto stockDto : copyContent) {
                    if (StringUtil.equals(stockDto.getGoodsNo(), stockArray.getJSONObject(i).getStr("sku"))) {
                        // 如果是抖音4PL，则直接显示0
                        if (StringUtils.equals(stockDto.getPlatformCode(), PlatformConstant.DY)
                            && StringUtils.equals(stockDto.getWarehouseCode(), "4PLFLBBC01")) {
                            stockDto.setQty(0);
                            stockDto.setAvaQty(0);
                            stockDto.setOccuQty(0);
                        }else {
                            stockDto.setQty(stockArray.getJSONObject(i).getInt("qty"));
                            stockDto.setAvaQty(stockArray.getJSONObject(i).getInt("avaQty"));
                            stockDto.setOccuQty(stockArray.getJSONObject(i).getInt("occuQty"));
                        }

                    }
                }
            }
            Page<StockDto> stockDtoPage = new PageImpl<>(copyContent, pageable, page.getTotalElements());
            return PageUtil.toPage(stockDtoPage);
        }
        return PageUtil.toPage(page);
    }

    @Override
    public List<StockDto> queryDetailStock(String goodsNo) {
        BaseSku baseSku = queryByGoodsNo(goodsNo);
        List<BaseSku> skus = new ArrayList<>();
        skus.add(baseSku);
        List<StockDto> stockDtos = skuToDetailStock(skus);
        return stockDtos;
    }

    @Override
    public List<BaseSku> queryByCustomerId(long customerId) {
        BaseSku baseSku=new BaseSku();
        baseSku.setCustomersId(customerId);
        return baseSkuRepository.findAll(Example.of(baseSku));
    }

    /*
    商品备案方法
     */
    @Override
    public BaseSku addSku(String decData, Long customersId) {
//        BaseSku baseSku = JSONObject.parseObject(decData, BaseSku.class);
//        JSONObject data = JSONObject.parseObject(decData);
//
//        String shopCode = data.getString("shopCode");
//        if (StringUtils.isEmpty(shopCode))
//            throw new BadRequestException("shopCode必填");
//        // 1.根据调用方传入的shopCode查询shopIfo
//        ShopInfo shopInfo = shopInfoService.queryByShopCode(shopCode);
//        if (shopInfo == null)
//            throw new BadRequestException("shopCode不正确");
//        String operateType = data.getString("operateType");
//        String barCode = data.getString("barCode");
//        BaseSku baseSku;
//        if(StringUtils.equals(operateType, "1")) {
//            // 根据条码和shopid查询baseSku
//            baseSku = queryByBarCodeAndShopId(barCode, shopInfo.getId());
//        }else {
//            //如果没有则新增baseSku()
//            baseSku = new BaseSku();
//        }
////        baseSku.setStatus(CBGoodsStatusEnum.STATUS_130.getCode());
//        baseSku.setCustomersId(customersId);
//        baseSku.setShopId(shopInfo.getId());
//        //之后依次添加商品备案信息,若需要填写则加if判断语句,不需要则默认传入
//        barCode = data.getString("barCode");
//        if (StringUtils.isEmpty(barCode))
//            throw new BadRequestException("barCode必填");
//        baseSku.setBarCode(barCode);
//        String snControl = data.getString("snControl");
//        if (StringUtils.isEmpty(snControl))
//            throw new BadRequestException("snControl必填");
//        baseSku.setSnControl(snControl);
//        String hsCode = data.getString("hsCode");
//        if (StringUtils.isEmpty(hsCode))
//            throw new BadRequestException("hsCode必填");
//        baseSku.setHsCode(hsCode);
//        String goodsNameC = data.getString("goodsNameC");
//        if (StringUtils.isEmpty(goodsNameC))
//            throw new BadRequestException("goodsNameC必填");
//        baseSku.setGoodsNameC(goodsNameC);
//        baseSku.setGoodsName(goodsNameC);
//        String goodsNameE = data.getString("goodsNameE");
//        baseSku.setGoodsNameE(goodsNameE);
//        String goodsCode = data.getString("goodsCode");
//        baseSku.setGoodsCode(goodsCode);
//        String makeContry = data.getString("makeContry");
//        if (StringUtils.isEmpty(makeContry))
//            throw new BadRequestException("makeContry必填");
//        baseSku.setMakeContry(makeContry);
//        String brand = data.getString("brand");
//        if (StringUtils.isEmpty(brand))
//            throw new BadRequestException("brand必填");
//        baseSku.setBrand(brand);
//        String unit = data.getString("unit");
//        if (StringUtils.isEmpty(unit))
//            throw new BadRequestException("unit必填");
//        baseSku.setUnit(unit);
//        String guse = data.getString("guse");
//        baseSku.setGuse(guse);
//        String gcomposition = data.getString("gcomposition");
//        baseSku.setGcomposition(gcomposition);
//        String gfunction = data.getString("gfunction");
//        baseSku.setGfunction(gfunction);
//        String remark = data.getString("remark");
//        baseSku.setRemark(remark);
//        String supplier = data.getString("supplier");
//        if (StringUtils.isEmpty(supplier))
//            throw new BadRequestException("supplier必填");
//        baseSku.setSupplier(supplier);
//        BigDecimal legalNum = data.getBigDecimal("legalNum");
//        baseSku.setLegalNum(legalNum);
//        BigDecimal secondNum = data.getBigDecimal("secondNum");
//        baseSku.setSecondNum(secondNum);
//        Integer lifecycle = data.getInteger("lifecycle");
//        baseSku.setLifecycle(lifecycle);
//        BigDecimal saleL = data.getBigDecimal("saleL");
//        baseSku.setSaleL(saleL);
//        BigDecimal saleW = data.getBigDecimal("saleW");
//        baseSku.setSaleW(saleW);
//        BigDecimal saleH = data.getBigDecimal("saleH");
//        baseSku.setSaleH(saleH);
//        BigDecimal packL = data.getBigDecimal("packL");
//        baseSku.setPackL(packL);
//        BigDecimal packW = data.getBigDecimal("packW");
//        baseSku.setPackW(packW);
//        BigDecimal packH = data.getBigDecimal("packH");
//        baseSku.setPackH(packH);
//        Integer packNum = data.getInteger("packNum");
//        baseSku.setPackNum(packNum);
//        BigDecimal packWeight = data.getBigDecimal("packWeight");
//        baseSku.setPackWeight(packWeight);
//        BigDecimal grossWeight = data.getBigDecimal("grossWeight");
//        baseSku.setGrossWeight(grossWeight);
//        BigDecimal netWeight = data.getBigDecimal("netWeight");
//        baseSku.setNetWeight(netWeight);
//        String goodsNo = data.getString("goodsNo");
//        if (StringUtil.isNotBlank(goodsNo)) {
//            // 当有导入时有货号，则改商品已经备案过，状态直接置为备案完成
//            baseSku.setStatus(CBGoodsStatusEnum.STATUS_130.getCode());
//        }else {
//            baseSku.setStatus(CBGoodsStatusEnum.STATUS_100.getCode());
//        }
//        baseSku.setIsNew("0");
//        baseSku.setRegisterType(shopInfo.getRegisterType());
//        //最后添加到baseSku库中
//        create(baseSku);
//        return baseSku;
        // 此方法暂时还未提供给商家使用，先注释
            return null;
    }

    @Override
    public BaseSku queryByBarCodeAndShopId(String barCode, Long shopId) {
        return baseSkuRepository.findByBarCodeAndShopId(barCode, shopId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void uploadSize(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<BaseSku> skus = new ArrayList<>();
        for (Map<String, Object> map : list) {
            int sNo = Integer.valueOf(map.get("序号").toString());
            String goodsNo = map.get("货号") != null ?map.get("货号").toString() : null;
            String saleL = map.get("长") != null ?map.get("长").toString() : null;
            String saleW = map.get("宽") != null ?map.get("宽").toString() : null;
            String saleH = map.get("高") != null ?map.get("高").toString() : null;
            String saleWeight = map.get("重量") != null ?map.get("重量").toString() : null;
            String packL = map.get("箱长") != null ?map.get("箱长").toString() : null;
            String packW = map.get("箱宽") != null ?map.get("箱宽").toString() : null;
            String packH = map.get("箱高") != null ?map.get("箱高").toString() : null;
            String packNum = map.get("箱规") != null ?map.get("箱规").toString() : null;
            String packWeight = map.get("箱重") != null ?map.get("箱重").toString() : null;
            String lifecycle = map.get("保质期天数") != null ?map.get("保质期天数").toString() : null;

            if (StringUtils.isEmpty(goodsNo))
                throw new BadRequestException("第" + sNo + "行，goodsNo不能为空");
            if (StringUtils.isEmpty(saleL))
                throw new BadRequestException("第" + sNo + "行，长不能为空");
            if (StringUtils.isEmpty(saleW))
                throw new BadRequestException("第" + sNo + "行，宽不能为空");
            if (StringUtils.isEmpty(saleH))
                throw new BadRequestException("第" + sNo + "行，高不能为空");
            if (StringUtils.isEmpty(saleWeight))
                throw new BadRequestException("第" + sNo + "行，重量不能为空");
            if (StringUtils.isEmpty(packL))
                throw new BadRequestException("第" + sNo + "行，箱长不能为空");
            if (StringUtils.isEmpty(packW))
                throw new BadRequestException("第" + sNo + "行，箱宽不能为空");
            if (StringUtils.isEmpty(packH))
                throw new BadRequestException("第" + sNo + "行，箱高不能为空");
            if (StringUtils.isEmpty(packNum))
                throw new BadRequestException("第" + sNo + "行，箱规不能为空");
            if (StringUtils.isEmpty(packWeight))
                throw new BadRequestException("第" + sNo + "行，箱重不能为空");
            BaseSku baseSku = queryByGoodsNo(goodsNo);
            if (baseSku == null)
                throw new BadRequestException("第" + sNo + "行，goodsNo不存在");
            baseSku.setSaleL(new BigDecimal(saleL));
            baseSku.setSaleW(new BigDecimal(saleW));
            baseSku.setSaleH(new BigDecimal(saleH));
            baseSku.setSaleWeight(new BigDecimal(saleWeight));
            baseSku.setPackL(new BigDecimal(packL));
            baseSku.setPackW(new BigDecimal(packW));
            baseSku.setPackH(new BigDecimal(packH));
            baseSku.setPackNum(Integer.valueOf(packNum));
            baseSku.setPackWeight(new BigDecimal(packWeight));
            baseSku.setIsNew("0");
            if (StringUtils.isNotBlank(lifecycle)) {
                baseSku.setLifecycle(Integer.valueOf(lifecycle));
            }
            skus.add(baseSku);
        }
        baseSkuRepository.saveAll(skus);
    }

    @Override
    public void pushWms(Long id) throws Exception {
        BaseSku baseSku = queryById(id);
        if (StringUtils.isEmpty(baseSku.getGoodsNo()))
            throw new BadRequestException("货号为空不能直接推送富勒"+ baseSku.getId());
        if (baseSku.getStatus().intValue() != CBGoodsStatusEnum.STATUS_115.getCode().intValue())
            throw new BadRequestException("非申报成功状态不能推送富勒"+ baseSku.getId());
        wmsSupport.pushSku(baseSku);
        baseSku.setStatus(CBGoodsStatusEnum.STATUS_130.getCode());
        update(baseSku);
        SkuLog log = new SkuLog(
                baseSku.getId(),
                String.valueOf(baseSku.getStatus()),
                "",
                "",
                "SYSTEM"
        );
        skuLogService.create(log);
    }

    @Override
    public void auditPass(Long id) throws Exception {
        BaseSku baseSku = queryById(id);
        if (baseSku.getStatus().intValue() != CBGoodsStatusEnum.STATUS_100.getCode().intValue())
            throw new BadRequestException("非创建状态不能审核" + baseSku.getId());
        if (StringUtils.isNotEmpty(baseSku.getGoodsNo()))
            throw new BadRequestException("已存在货号不能海关备案"+ baseSku.getId());
        // 海关备案
        if (StringUtils.isBlank(baseSku.getSupplier())) {
            BaseSku baseSku1 = queryByGoodsNo(baseSku.getGoodsCode());
            baseSku.setSupplier(baseSku1.getSupplier());
        }
        String goodsNo = kjgSupport.register(baseSku);
        baseSku.setGoodsNo(goodsNo);
        baseSku.setStatus(CBGoodsStatusEnum.STATUS_115.getCode());
        update(baseSku);

        SkuLog log = new SkuLog(
                baseSku.getId(),
                String.valueOf(baseSku.getStatus()),
                "",
                "",
                SecurityUtils.getCurrentUsername()
        );
        skuLogService.create(log);

        cbOrderProducer.send(
                MsgType.SKU_PUSH_WMS,
                String.valueOf(baseSku.getId()),
                baseSku.getGoodsNo()
        );
    }


    @Override
    public BaseSku queryById(Long id) {
        return baseSkuRepository.findById(id).orElseGet(BaseSku::new);
    }

    @Override
    public List<BaseSku> queryListByBarcode(String barCode) {
        BaseSkuQueryCriteria criteria = new BaseSkuQueryCriteria();
        criteria.setBarCode(barCode);
        return baseSkuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    public List<StockDto> queryDetailStockByLocation(String location) {
        List<StockDto> result = new ArrayList<>();
        JSONArray stockArray = wmsSupport.queryDetailsStockByLocation(location);
        for (int i = 0; i < stockArray.size(); i++) {
            String goodsNo = stockArray.getJSONObject(i).getStr("goodsNo");
            BaseSku baseSku = queryByGoodsNo(goodsNo);
            StockDto stockDto;
            if (baseSku != null) {
                stockDto = BeanUtil.copyProperties(baseSku, StockDto.class);
                CustomerInfoDto customerInfoDto = customerInfoService.queryById(stockDto.getCustomersId());
                if (customerInfoDto != null) {
                    stockDto.setCustomerName(customerInfoDto.getCustNickName());
                }
                ShopInfoDto shopInfoDto = shopInfoService.queryById(stockDto.getShopId());
                if (shopInfoDto != null) {
                    stockDto.setShopName(shopInfoDto.getName());
                    stockDto.setPlatformName(shopInfoDto.getPlatformCode());
                }
            }else {
                stockDto = new StockDto();
                stockDto.setGoodsNo(goodsNo);
            }
            stockDto.setLocationId(stockArray.getJSONObject(i).getStr("locationId"));
            stockDto.setQty(stockArray.getJSONObject(i).getInt("qty"));
            stockDto.setAvaQty(stockArray.getJSONObject(i).getInt("avaQty"));
            stockDto.setOccuQty(stockArray.getJSONObject(i).getInt("occuQty"));
            stockDto.setProdDate(stockArray.getJSONObject(i).getStr("prodDate"));
            stockDto.setExpireDate(stockArray.getJSONObject(i).getStr("expireDate"));
            stockDto.setInStockDate(stockArray.getJSONObject(i).getStr("inStockDate"));
            stockDto.setCustomerBatch(stockArray.getJSONObject(i).getStr("customerBatch"));
            stockDto.setAvaOrDef(stockArray.getJSONObject(i).getStr("avaOrDef"));
            result.add(stockDto);
        }
        return result;
    }

    private List<StockDto> skuToDetailStock(List<BaseSku> skus) {
        List<StockDto> copyContent = new ArrayList<>();
        List<String> goodsNos = new ArrayList<>();
        for (BaseSku baseSku : skus) {
            goodsNos.add(baseSku.getGoodsNo());
        }
        JSONArray stockArray = wmsSupport.queryDetailsStock(goodsNos);
        if (stockArray != null) {
            for (BaseSku baseSku : skus) {
                for (int i = 0; i < stockArray.size(); i++) {
                    if (StringUtil.equals(baseSku.getGoodsNo(), stockArray.getJSONObject(i).getStr("sku"))) {
                        StockDto stockDto = BeanUtil.copyProperties(baseSku, StockDto.class);
                        CustomerInfoDto customerInfoDto = customerInfoService.queryById(stockDto.getCustomersId());
                        if (customerInfoDto != null) {
                            stockDto.setCustomerName(customerInfoDto.getCustNickName());
                        }
                        ShopInfoDto shopInfoDto = shopInfoService.queryById(stockDto.getShopId());
                        if (shopInfoDto != null) {
                            stockDto.setShopName(shopInfoDto.getName());
                            stockDto.setPlatformName(shopInfoDto.getPlatformCode());
                        }
                        stockDto.setBarCode(baseSku.getBarCode());
                        stockDto.setGoodsNo(baseSku.getGoodsNo());
                        stockDto.setGoodsName(baseSku.getGoodsNameC());
                        stockDto.setLocationId(stockArray.getJSONObject(i).getStr("locationId"));
                        // 如果是抖音4PL，则直接显示0
                        if (StringUtils.equals(stockDto.getPlatformCode(), PlatformConstant.DY)
                                && StringUtils.equals(stockDto.getWarehouseCode(), "4PLFLBBC01")) {
                            stockDto.setQty(0);
                            stockDto.setAvaQty(0);
                            stockDto.setOccuQty(0);
                        }else {
                            stockDto.setQty(stockArray.getJSONObject(i).getInt("qty"));
                            stockDto.setAvaQty(stockArray.getJSONObject(i).getInt("avaQty"));
                            stockDto.setOccuQty(stockArray.getJSONObject(i).getInt("occuQty"));
                        }


                        stockDto.setProdDate(stockArray.getJSONObject(i).getStr("prodDate"));
                        stockDto.setExpireDate(stockArray.getJSONObject(i).getStr("expireDate"));
                        stockDto.setInStockDate(stockArray.getJSONObject(i).getStr("inStockDate"));
                        stockDto.setCustomerBatch(stockArray.getJSONObject(i).getStr("customerBatch"));
                        stockDto.setAvaOrDef(stockArray.getJSONObject(i).getStr("avaOrDef"));
                        stockDto.setWmsBatchNo(stockArray.getJSONObject(i).getStr("batchNo"));
                        copyContent.add(stockDto);
                    }
                }
            }
        }
        return copyContent;
    }

    @Override
    public List<BaseSkuDto> queryAll(BaseSkuQueryCriteria criteria){
        return baseSkuMapper.toDto(baseSkuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public List<StockDto> queryAllStock(BaseSkuQueryCriteria criteria) {
        List<StockDto> result = new ArrayList<>();
        List<BaseSku> all = baseSkuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            int size = all.size();// 当前集合的长度，因为请求库存一次最多查50条
            int reqStockSize = 50;
            // 分页获取
            int page = (new BigDecimal(size)).divide(BigDecimal.valueOf(reqStockSize), 0, BigDecimal.ROUND_UP).intValue();
            for (int i = 0; i < page; i++) {
                List<BaseSku> skus;
                if ((i+1) == page) {
                    skus = all.subList(i*reqStockSize, size);
                }else {
                    skus = all.subList(i*reqStockSize, ((i+1)*reqStockSize));
                }
                List<StockDto> stockDtos = skuToDetailStock(skus);
                result.addAll(stockDtos);
            }

        }
        return result;
    }

    @Override
    @Transactional
    public BaseSkuDto findById(Long id) {
        BaseSku baseSku = baseSkuRepository.findById(id).orElseGet(BaseSku::new);
        ValidationUtil.isNull(baseSku.getId(),"BaseSku","id",id);
        return baseSkuMapper.toDto(baseSku);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseSkuDto create(BaseSku resources) {
        return baseSkuMapper.toDto(baseSkuRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BaseSku resources) {
        BaseSku baseSku = baseSkuRepository.findById(resources.getId()).orElseGet(BaseSku::new);
        ValidationUtil.isNull( baseSku.getId(),"BaseSku","id",resources.getId());
        baseSku.copy(resources);
        redisUtils.del("baseSku::goodsNo:" + resources.getGoodsNo());
        baseSkuRepository.save(baseSku);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            BaseSku baseSku = baseSkuRepository.findById(id).orElseGet(BaseSku::new);
            redisUtils.del("baseSku::goodsNo:" + baseSku.getGoodsNo());
            baseSkuRepository.deleteById(id);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadGoodsNo(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<BaseSku> skus = new ArrayList<>();
        for (Map<String, Object> map : list) {
            BaseSku baseSku = checkOuterGoodsNo(map);
            skus.add(baseSku);
        }
        List<BaseSku> returnSkus = baseSkuRepository.saveAll(skus);
        List<SkuLog> logs = new ArrayList<>();
        for (BaseSku sku : returnSkus) {
            SkuLog log = new SkuLog(
                    sku.getId(),
                    String.valueOf(sku.getStatus()),
                    "",
                    "",
                    SecurityUtils.getCurrentUsername()
            );
            logs.add(log);
        }
        skuLogService.saveAll(logs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadCNGoodsNo(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<BaseSku> skus = new ArrayList<>();
        for (Map<String, Object> map : list) {
            BaseSku baseSku = checkCNGoodsNo(map);
            skus.add(baseSku);
        }
        List<BaseSku> returnSkus = baseSkuRepository.saveAll(skus);
        List<SkuLog> logs = new ArrayList<>();
        for (BaseSku sku : returnSkus) {
            SkuLog log = new SkuLog(
                    sku.getId(),
                    String.valueOf(sku.getStatus()),
                    "",
                    "",
                    SecurityUtils.getCurrentUsername()
            );
            logs.add(log);
        }
        skuLogService.saveAll(logs);
    }

    private BaseSku checkCNGoodsNo(Map<String, Object> map) {
        BaseSku sku = new BaseSku();
        int sNo = Integer.valueOf(map.get("序号*").toString());
        String cusName = map.get("客户名称*") != null ? map.get("客户名称*").toString() : null;
        String shopName = map.get("店铺名称*") != null ?map.get("店铺名称*").toString() : null;
        String goodsCode = map.get("商品编码") != null ?map.get("商品编码").toString() : null;
        String goodsNo = map.get("海关货号*") != null ?map.get("海关货号*").toString() : null;
        String outerGoodsNo = map.get("菜鸟货品id*") != null ?map.get("菜鸟货品id*").toString() : null;
        if (StringUtil.isBlank(outerGoodsNo))
            outerGoodsNo = map.get("电商sku") != null ?map.get("电商sku").toString() : null;
        String goodsNameC = map.get("商品中文名称*") != null ?map.get("商品中文名称*").toString() : null;
        String goodsNameE = map.get("商品英文名称") != null ?map.get("商品英文名称").toString() : null;
        String hsCode = map.get("HS编码*") != null ?map.get("HS编码*").toString() : null;
        String barCode = map.get("商品条码*") != null ?map.get("商品条码*").toString() : null;
        String netWeight = map.get("净重（千克)*") != null ?map.get("净重（千克)*").toString() : null;
        String property = map.get("规格型号*") != null ?map.get("规格型号*").toString() : null;
        String makeContry = map.get("原产地*") != null ?map.get("原产地*").toString() : null;
        String brand = map.get("品牌*") != null ?map.get("品牌*").toString() : null;
        String unit = map.get("常用单位*") != null ?map.get("常用单位*").toString() : null;
        String guse = map.get("用途") != null ?map.get("用途").toString() : null;
        String gcomposition = map.get("成分") != null ?map.get("成分").toString() : null;
        String gfunction = map.get("功能") != null ?map.get("功能").toString() : null;
        String supplier = map.get("供应商*") != null ?map.get("供应商*").toString() : null;
        String legalNum = map.get("法定数量*") != null ?map.get("法定数量*").toString() : null;
        String secondNum = map.get("第二数量") != null ?map.get("第二数量").toString() : null;
        String remark = map.get("商品备注") != null ?map.get("商品备注").toString() : null;
        String lifecycle = map.get("保质期天数") != null ?map.get("保质期天数").toString() : "0";
        if (StringUtil.isBlank(cusName))
            throw new BadRequestException("第" + sNo + "行，客户名称不能为空");
        if (StringUtil.isBlank(shopName))
            throw new BadRequestException("第" + sNo + "行，店铺名称不能为空");
        if (StringUtil.isBlank(goodsNameC))
            throw new BadRequestException("第" + sNo + "行，商品中文名称不能为空");
        if (StringUtil.isBlank(goodsNo))
            throw new BadRequestException("第" + sNo + "行，海关货号不能为空");
        if (StringUtil.isBlank(hsCode))
            throw new BadRequestException("第" + sNo + "行，HS编码不能为空");
        if (StringUtil.isBlank(barCode))
            throw new BadRequestException("第" + sNo + "行，条码不能为空");
        if (StringUtil.isBlank(netWeight))
            throw new BadRequestException("第" + sNo + "行，净重不能为空");
        if (StringUtil.isBlank(property))
            throw new BadRequestException("第" + sNo + "行，规格型号不能为空");
        if (StringUtil.isBlank(makeContry))
            throw new BadRequestException("第" + sNo + "行，原产地不能为空");
        if (StringUtil.isBlank(outerGoodsNo))
            throw new BadRequestException("第" + sNo + "行，菜鸟货品ID不能为空");
        if (StringUtil.isBlank(unit))
            throw new BadRequestException("第" + sNo + "行，常用单位不能为空");
        if (StringUtil.isBlank(legalNum))
            throw new BadRequestException("第" + sNo + "行，法定数量不能为空");
        if (StringUtils.isNotEmpty(goodsNo)) {
            goodsNo = goodsNo.replaceAll("\t", "");
        }
        goodsNameC = goodsNameC.replaceAll("\t", "");
        barCode = barCode.replaceAll("\t", "");
        hsCode = hsCode.replaceAll("\t", "");
        netWeight = netWeight.replaceAll("\t", "");
        property = property.replaceAll("\t", "");
        makeContry = makeContry.replaceAll("\t", "");
        if (StringUtil.isNotBlank(brand))
            brand = brand.replaceAll("\t", "");
        unit = unit.replaceAll("\t", "");
        if (supplier!=null)
            supplier = supplier.replaceAll("\t", "");
        legalNum = legalNum.replaceAll("\t", "");

        CustomerInfo customerInfo = customerInfoService.queryByName(cusName);
        if (customerInfo == null)
            throw new BadRequestException("第" + sNo + "行，客户名称不存在");

        ShopInfo shopInfo = shopInfoService.queryByName(shopName);
        if (shopInfo == null)
            throw new BadRequestException("第" + sNo + "行，店铺名称不存在");
        if (!StringUtils.equals("1", shopInfo.getPushTo()))
            throw new BadRequestException("第" + sNo + "行，非推单到菜鸟的店铺请不要上传菜鸟类型备案");
        if (shopInfo.getCustId().intValue() != customerInfo.getId())
            throw new BadRequestException("第" + sNo + "行，该店铺不属于该客户");
        sku.setShopId(shopInfo.getId());
        if (StringUtils.isNotBlank(outerGoodsNo)) {
            BaseSku existOutGoodsNo = queryByOutGoodsNo(outerGoodsNo);
            if (existOutGoodsNo != null)
                throw new BadRequestException("第" + sNo + "行，菜鸟货品id已存在");
        }
        if (StringUtil.isNotEmpty(goodsCode)) {
            BaseSku code = queryByCode(goodsCode);
            if (code != null)
                throw new BadRequestException("第" + sNo + "行，商品编码已存在，请勿重复导入");
        }
        if (StringUtil.isNotEmpty(goodsNo)) {
            BaseSku gNo = queryByGoodsNo(goodsNo);
            if (gNo != null)
                throw new BadRequestException("第" + sNo + "行，货号已存在，请勿重复导入");
        }
        // 法一单位、法二单位从维护好的数据中取
        CustomsTariff customsTariff = customsTariffService.queryByHs(hsCode);
        if (customsTariff == null)
            throw new BadRequestException("第" + sNo + "行，HS有误，或HS基本信息未维护");
        sku.setStatus(CBGoodsStatusEnum.STATUS_130.getCode());
        sku.setSnControl(BooleanEnum.FAIL.getCode());
        sku.setCustomersId(customerInfo.getId());
        if (StringUtil.isBlank(goodsCode)) {
            // 当商品编码为空时，自动生成一个唯一的商品编码
            goodsCode = genGoodsCode();
        }
        sku.setPlatformCode(shopInfo.getPlatformCode());
        sku.setGoodsCode(goodsCode);
        sku.setOuterGoodsNo(outerGoodsNo);
        sku.setBarCode(barCode);
        sku.setGoodsName(goodsNameC);
        sku.setGoodsNameC(goodsNameC);
        sku.setGoodsNameE(goodsNameE);
        sku.setGoodsNo(goodsNo);
        sku.setOuterGoodsNo(outerGoodsNo);
        sku.setHsCode(hsCode);
        sku.setNetWeight(new BigDecimal(netWeight));
        sku.setProperty(property);
        sku.setMakeContry(makeContry);
        CustomsCode makeContryCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.COUNTRY, makeContry);
        if (makeContryCode == null)
            throw new BadRequestException("第" + sNo + "行，原产地代码未维护：" + makeContry);
        sku.setMakeContryCode(makeContryCode.getCode());

        sku.setBrand(brand);
        sku.setUnit(unit);
        CustomsCode unitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, unit);
        if (unitCode == null)
            throw new BadRequestException("第" + sNo + "行，常用单位代码未维护：" + unit);
        sku.setUnitCode(unitCode.getCode());
        String firstUnit = customsTariff.getFirstUnit();
        CustomsCode firstUnitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, firstUnit);
        if (firstUnitCode == null)
            throw new BadRequestException("第" + sNo + "行，法一单位代码未维护：" + firstUnit);
        sku.setLegalUnit(firstUnit);
        sku.setLegalUnitCode(firstUnitCode.getCode());

        String secondUnit = customsTariff.getSecondUnit();
        if (StringUtils.isNotEmpty(secondUnit)) {
            CustomsCode secondUnitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, secondUnit);
            if (secondUnitCode == null)
                throw new BadRequestException("第" + sNo + "行，法二单位代码未维护：" + secondUnit);
            sku.setSecondUnit(secondUnit);
            sku.setSecondUnitCode(secondUnitCode.getCode());
        }

        sku.setGuse(guse);
        sku.setGcomposition(gcomposition);
        sku.setGfunction(gfunction);
        sku.setSupplier(supplier);
        sku.setLegalNum(new BigDecimal(legalNum));
        if (StringUtil.isNotBlank(secondNum)) {
            sku.setSecondNum(new BigDecimal(secondNum));
        }
        sku.setRemark(remark);
        sku.setLifecycle(Integer.valueOf(lifecycle));
        sku.setIsNew("1");
        sku.setIsGift("0");
        sku.setRegisterType(shopInfo.getRegisterType());
        return sku;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadGift(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<BaseSku> skus = new ArrayList<>();
        for (Map<String, Object> map : list) {
            BaseSku baseSku = checkGifts(map);
            skus.add(baseSku);
        }
        List<BaseSku> returnSkus = baseSkuRepository.saveAll(skus);
        List<SkuLog> logs = new ArrayList<>();
        for (BaseSku sku : returnSkus) {
            SkuLog log = new SkuLog(
                    sku.getId(),
                    String.valueOf(sku.getStatus()),
                    "",
                    "",
                    SecurityUtils.getCurrentUsername()
            );
            logs.add(log);
        }
        skuLogService.saveAll(logs);
    }

    //查询主品和赠品basesku
    @Override
    public Map<String,List<BaseSku>> querySkuAndGiftSku(Long shopId) {
        List<BaseSku> baseSku = queryByShopId(shopId);
        List<BaseSku> skus = new ArrayList<>();
        List<BaseSku> giftSkus = new ArrayList<>();
        Map<String,List<BaseSku>>map=new HashMap<>();
        for (BaseSku sku : baseSku) {
                if (sku.getIsGift() == null){
                    skus.add(sku);
                }else {
                    giftSkus.add(sku);
                }
        }
        map.put("sku",skus);
        map.put("gift",giftSkus);
        return map;
    }

    @Override
    public BaseSku queryByGoodsName(String goodsName) {
        return baseSkuRepository.findByGoodsName(goodsName);
    }

    @Override
    public List<BaseSku> queryByTypeAndBarCodeLike(String type, String barCode, Long shopId) {
        BaseSkuQueryCriteria criteria = new BaseSkuQueryCriteria();
        criteria.setIsGift(type);
        criteria.setGoodsNoLike(barCode);
        criteria.setShopIdEq(shopId);
        return baseSkuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }


    /**
     * 导入赠品
     * @param map
     * @return
     */
    private BaseSku checkGifts(Map<String, Object> map) {
        BaseSku sku = new BaseSku();
        int sNo = Integer.valueOf(map.get("序号").toString());
        String cusName = map.get("客户名称*") != null ? map.get("客户名称*").toString() : null;
        String shopName = map.get("店铺名称*") != null ?map.get("店铺名称*").toString() : null;
        String barCode = map.get("赠品条码*") != null ?map.get("赠品条码*").toString() : null;
        String goodsName = map.get("赠品名称*") != null ?map.get("赠品名称*").toString() : null;
        if (StringUtil.isBlank(barCode))
            throw new BadRequestException("第" + sNo + "行，赠品条码不能为空");
        if (StringUtil.isBlank(cusName))
            throw new BadRequestException("第" + sNo + "行，客户名称不能为空");
        if (StringUtil.isBlank(shopName))
            throw new BadRequestException("第" + sNo + "行，店铺名称不能为空");
        if (StringUtil.isBlank(goodsName))
            throw new BadRequestException("第" + sNo + "行，商品名称不能为空");
        barCode.replaceAll("\t", "");
        CustomerInfo customerInfo = customerInfoService.queryByName(cusName);
        if (customerInfo == null)
            throw new BadRequestException("第" + sNo + "行，客户名称不存在");
        ShopInfo shopInfo = shopInfoService.queryByName(shopName);
        if (shopInfo == null)
            throw new BadRequestException("第" + sNo + "行，店铺名称不存在");
        if (shopInfo.getCustId().intValue() != customerInfo.getId())
            throw new BadRequestException("第" + sNo + "行，该店铺不属于该客户");
        // 检测条码是否已存在
        BaseSku exist = queryByBarCodeAndShopId(barCode, shopInfo.getId());
        if (exist != null)
            throw new BadRequestException("第" + sNo + "行，该店铺已存在相同条码备案");
        sku.setGoodsCode(genGoodsCode());
        // 货号由店铺code+条码生成
        sku.setPlatformCode(shopInfo.getPlatformCode());
        sku.setGoodsNo(shopInfo.getCode() + barCode);
        sku.setBarCode(barCode);
        sku.setShopId(shopInfo.getId());
        sku.setCustomersId(customerInfo.getId());
        sku.setSnControl(BooleanEnum.FAIL.getCode());
        sku.setGoodsName(goodsName);
        sku.setGoodsNameC(goodsName);
        sku.setStatus(CBGoodsStatusEnum.STATUS_130.getCode());
        //新加导入的是赠品
        sku.setIsGift("1");
        return sku;
    }

    private BaseSku checkOuterGoodsNo(Map<String, Object> map) {
        BaseSku sku = new BaseSku();
        int sNo = Integer.valueOf(map.get("序号").toString());
        String goodsNo = map.get("海关货号") != null ?map.get("海关货号").toString() : null;
        String outerGoodsNo = map.get("外部货号") != null ?map.get("外部货号").toString() : null;
        if (StringUtil.isBlank(goodsNo))
            throw new BadRequestException("第" + sNo + "行，海关货号不能为空");
        if (StringUtil.isBlank(outerGoodsNo))
            throw new BadRequestException("第" + sNo + "行，外部货号不能为空");
        if (StringUtils.isNotEmpty(goodsNo)) {
            goodsNo = goodsNo.replaceAll("\t", "");
        }
        outerGoodsNo.replaceAll("\t", "");
        if (StringUtil.isNotEmpty(goodsNo)) {
             sku = queryByGoodsNo(goodsNo);
            if (sku != null)
                sku.setOuterGoodsNo(outerGoodsNo);
        }
        return sku;
    }

    /**
     * 导入抖音备案
     * @param list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadDYSku(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<BaseSku> skus = new ArrayList<>();
        for (Map<String, Object> map : list) {
            BaseSku baseSku = checkDYSku(map);
            skus.add(baseSku);
        }
        List<BaseSku> returnSkus = baseSkuRepository.saveAll(skus);
        List<SkuLog> logs = new ArrayList<>();
        for (BaseSku sku : returnSkus) {
            SkuLog log = new SkuLog(
                    sku.getId(),
                    String.valueOf(sku.getStatus()),
                    "",
                    "",
                    SecurityUtils.getCurrentUsername()
            );
            logs.add(log);
        }
        skuLogService.saveAll(logs);
    }

    private BaseSku checkDYSku(Map<String, Object> map) {
        String recordId = map.get("备案记录ID") != null ? map.get("备案记录ID").toString() : null;// 作为料号
        String goodsId = map.get("货品ID") != null ?map.get("货品ID").toString() : null;// 作为外部货号
        String goodsName = map.get("货品名称") != null ?map.get("货品名称").toString() : null;// 作为商品名
        String goodsCode = map.get("货品编码") != null ?map.get("货品编码").toString() : null;
        String barCode = map.get("条形码") != null ?map.get("条形码").toString() : null;
        String shopId = map.get("商家/货主ID") != null ?map.get("商家/货主ID").toString() : null;
        String brand = map.get("品牌名称") != null ?map.get("品牌名称").toString() : null;
        String gfunction = map.get("功能用途") != null ?map.get("功能用途").toString() : null;
        String warehouseName = map.get("仓库名称") != null ?map.get("仓库名称").toString() : null;
        String goodsNameC = map.get("备案品名") != null ?map.get("备案品名").toString() : null;// 作为海关备案名
        String property = map.get("SKU规格") != null ?map.get("SKU规格").toString() : null;
        String declareElement = map.get("规格型号") != null ?map.get("规格型号").toString() : null;// 申报要素
        String makeContryCode = map.get("原产国ID") != null ?map.get("原产国ID").toString() : null;// 这里是代码
        String unit = map.get("申报计量单位") != null ?map.get("申报计量单位").toString() : null;// 这里是名称
        String hsCode = map.get("HS海关编码") != null ?map.get("HS海关编码").toString() : null;
        String legalNum = map.get("第一法定数量") != null ?map.get("第一法定数量").toString() : null;
        String legalUnit = map.get("第一法定单位") != null ?map.get("第一法定单位").toString() : null;// 这里是名称
        String secondNum = map.get("第二法定数量") != null ?map.get("第二法定数量").toString() : null;
        String secondUnit = map.get("第二法定单位") != null ?map.get("第二法定单位").toString() : null;// 这里是名称
        String netWeight = map.get("商品净重") != null ?map.get("商品净重").toString() : null;
        String grossWeight = map.get("商品毛重") != null ?map.get("商品毛重").toString() : null;
        String gcomposition = map.get("成分") != null ?map.get("成分").toString() : null;
        String supplier = map.get("生产企业名称") != null ?map.get("生产企业名称").toString() : null;
        String lifecycle = map.get("保质期天数") != null ?map.get("保质期天数").toString() : null;

        if (StringUtil.isBlank(goodsId))
            throw new BadRequestException(recordId + "，货品ID不能为空");
        if (StringUtil.isBlank(goodsName))
            throw new BadRequestException(recordId + "，，货品名称不能为空");
        if (StringUtil.isBlank(goodsCode))
            throw new BadRequestException(recordId + "，货品编码不能为空");
        if (StringUtil.isBlank(shopId))
            throw new BadRequestException(recordId + "，商家ID不能为空");
        if (StringUtil.isBlank(hsCode))
            throw new BadRequestException(recordId + "，，HS编码不能为空");
        if (StringUtil.isBlank(netWeight))
            throw new BadRequestException(recordId + "，净重不能为空");
        if (StringUtil.isBlank(property))
            throw new BadRequestException(recordId + "，规格型号不能为空");
        if (StringUtil.isBlank(makeContryCode))
            throw new BadRequestException(recordId + "，原产地不能为空");
        if (StringUtil.isBlank(brand))
            throw new BadRequestException(recordId + "，品牌不能为空");
        if (StringUtil.isBlank(unit))
            throw new BadRequestException(recordId + "，常用单位不能为空");
//        if (StringUtil.isBlank(supplier))
//            throw new BadRequestException(recordId + "，生产企业名称不能为空");
        if (StringUtil.isBlank(legalNum))
            throw new BadRequestException(recordId + "，法定数量不能为空");
        if (StringUtil.isBlank(warehouseName))
            throw new BadRequestException(recordId + "，仓库名称不能为空");
        if (StringUtil.isBlank(lifecycle))
            throw new BadRequestException(recordId + "，保质期天数不能为空");
        String warehouseCode;
        if (StringUtils.equals("宁波保税1号仓", warehouseName)) {
            warehouseCode = "4PLFLBBC01";
        }else if (StringUtils.equals("宁波北仑富立保税仓", warehouseName)) {
            warehouseCode = "FLBBC01";
        }else {
            throw new BadRequestException(recordId + "，仓库名称不存在");
        }
        BaseSku exist1 = queryByOutGoodsNoAndWarehouseCode(goodsId, warehouseCode);
        if (exist1 != null)
            throw new BadRequestException(recordId + "，货品ID已存在");
        ShopToken shopToken = shopTokenService.queryByPaltShopId(shopId);
        if (shopToken == null)
            throw new BadRequestException(recordId + "，商家ID不存在，请联系技术");
        ShopInfoDto shopInfoDto = shopInfoService.queryById(shopToken.getShopId());
//        BaseSku exist3 = queryByGoodsCodeAndShopId(goodsCode, shopInfoDto.getId());
//        if (exist3 != null)
//            throw new BadRequestException(recordId + "，该店铺已存在相同的货品编码");
        if (!StringUtils.equals(PlatformConstant.DY, shopInfoDto.getPlatformCode()))
            throw new BadRequestException(recordId + "，非抖音商家不要在此备案");
        BaseSku sku = new BaseSku();

        sku.setPlatformCode(shopInfoDto.getPlatformCode());
        sku.setStatus(CBGoodsStatusEnum.STATUS_100.getCode());
        sku.setCustomersId(shopInfoDto.getCustId());
        sku.setShopId(shopInfoDto.getId());
        sku.setOuterGoodsNo(goodsId);
        sku.setGoodsName(goodsNameC);
        sku.setGoodsCode(goodsCode);
        sku.setBarCode(barCode);
        sku.setGoodsNameC(goodsNameC);
        sku.setHsCode(hsCode);
        sku.setNetWeight(new BigDecimal(netWeight));
        sku.setGrossWeight(new BigDecimal(grossWeight));
        sku.setProperty(property);
        sku.setBrand(brand);
        sku.setUnit(unit);
        sku.setDeclareElement(declareElement);
        sku.setWarehouseCode(warehouseCode);
        CustomsCode unitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, unit);
        if (unitCode == null)
            throw new BadRequestException(recordId + "，常用单位代码未维护：" + unit);
        sku.setUnitCode(unitCode.getCode());
        sku.setGuse(gfunction);
        sku.setGfunction(gfunction);
        sku.setGcomposition(gcomposition);
        sku.setSupplier(supplier);
        // 法一单位、法二单位从维护好的数据中取
        CustomsTariff customsTariff = customsTariffService.queryByHs(hsCode);
        if (customsTariff == null)
            throw new BadRequestException(recordId + "，HS有误，或HS基本信息未维护");
        sku.setSnControl(BooleanEnum.FAIL.getCode());

        sku.setMakeContryCode(makeContryCode);
        CustomsCode makeContryCustomsCode = customsCodeService.queryByTypeAndCode(CustomsCodeConstant.COUNTRY, makeContryCode);
        if (makeContryCode == null)
            throw new BadRequestException(recordId + "，原产地代码未维护：" + makeContryCode);
        sku.setMakeContry(makeContryCustomsCode.getDes());

        sku.setLegalUnit(legalUnit);
        CustomsCode firstUnitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, legalUnit);
        if (firstUnitCode == null)
            throw new BadRequestException(recordId + "，法一单位代码未维护：" + legalUnit);
        sku.setLegalUnitCode(firstUnitCode.getCode());

        if (StringUtils.isNotEmpty(secondUnit)) {
            CustomsCode secondUnitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, secondUnit);
            if (secondUnitCode == null)
                throw new BadRequestException(recordId + "，，法二单位代码未维护：" + secondUnit);
            sku.setSecondUnit(secondUnit);
            sku.setSecondUnitCode(secondUnitCode.getCode());
            sku.setSecondNum(new BigDecimal(secondNum));
        }
        sku.setLegalNum(new BigDecimal(legalNum));
        sku.setIsNew("1");
        sku.setIsGift("0");
        sku.setRegisterType(shopInfoDto.getRegisterType());
        if (StringUtils.isNotBlank(lifecycle)) {
            sku.setLifecycle(Integer.valueOf(lifecycle));
        }
        return sku;

    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadSku(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<BaseSku> skus = new ArrayList<>();
        for (Map<String, Object> map : list) {
            BaseSku baseSku = checkSku(map);
            skus.add(baseSku);
        }
        List<BaseSku> returnSkus = baseSkuRepository.saveAll(skus);
        List<SkuLog> logs = new ArrayList<>();
        for (BaseSku sku : returnSkus) {
            SkuLog log = new SkuLog(
                    sku.getId(),
                    String.valueOf(sku.getStatus()),
                    "",
                    "",
                    SecurityUtils.getCurrentUsername()
            );
            logs.add(log);
        }
        skuLogService.saveAll(logs);
    }

    private BaseSku checkSku(Map<String, Object> map) {
        BaseSku sku = new BaseSku();
        int sNo = Integer.valueOf(map.get("序号").toString());
        String cusName = map.get("客户名称*") != null ? map.get("客户名称*").toString() : null;
        String shopName = map.get("店铺名称*") != null ?map.get("店铺名称*").toString() : null;
        String goodsCode = map.get("商品编码") != null ?map.get("商品编码").toString() : null;
        String goodsNo = map.get("海关货号*") != null ?map.get("海关货号*").toString() : null;
        String outerGoodsNo = map.get("电商sku") != null ?map.get("电商sku").toString() : null;
        String goodsNameC = map.get("商品中文名称*") != null ?map.get("商品中文名称*").toString() : null;
        String goodsNameE = map.get("商品英文名称") != null ?map.get("商品英文名称").toString() : null;
        String hsCode = map.get("HS编码*") != null ?map.get("HS编码*").toString() : null;
        String barCode = map.get("商品条码") != null ?map.get("商品条码").toString() : null;
        String netWeight = map.get("净重（千克)*") != null ?map.get("净重（千克)*").toString() : null;
        String property = map.get("规格型号*") != null ?map.get("规格型号*").toString() : null;
        String makeContry = map.get("原产地*") != null ?map.get("原产地*").toString() : null;
        String brand = map.get("品牌*") != null ?map.get("品牌*").toString() : null;
        String unit = map.get("常用单位*") != null ?map.get("常用单位*").toString() : null;
        String guse = map.get("用途") != null ?map.get("用途").toString() : null;
        String gcomposition = map.get("成分") != null ?map.get("成分").toString() : null;
        String gfunction = map.get("功能") != null ?map.get("功能").toString() : null;
        String supplier = map.get("供应商*") != null ?map.get("供应商*").toString() : null;
        String legalNum = map.get("法定数量*") != null ?map.get("法定数量*").toString() : null;
        String secondNum = map.get("第二数量") != null ?map.get("第二数量").toString() : null;
        String remark = map.get("商品备注") != null ?map.get("商品备注").toString() : null;
        String lifecycle = map.get("保质期天数") != null ?map.get("保质期天数").toString() : "0";
        if (StringUtil.isBlank(cusName))
            throw new BadRequestException("第" + sNo + "行，客户名称不能为空");
        if (StringUtil.isBlank(shopName))
            throw new BadRequestException("第" + sNo + "行，店铺名称不能为空");
        if (StringUtil.isBlank(goodsNameC))
            throw new BadRequestException("第" + sNo + "行，商品中文名称不能为空");
//        if (StringUtil.isBlank(goodsNo))
//            throw new BadRequestException("第" + sNo + "行，海关货号不能为空");
        if (StringUtil.isBlank(barCode))
            throw new BadRequestException("第" + sNo + "行，商品条码不能为空");
        if (StringUtil.isBlank(hsCode))
            throw new BadRequestException("第" + sNo + "行，HS编码不能为空");
        if (StringUtil.isBlank(netWeight))
            throw new BadRequestException("第" + sNo + "行，净重不能为空");
        if (StringUtil.isBlank(property))
            throw new BadRequestException("第" + sNo + "行，规格型号不能为空");
        if (StringUtil.isBlank(makeContry))
            throw new BadRequestException("第" + sNo + "行，原产地不能为空");
        if (StringUtil.isBlank(brand))
            throw new BadRequestException("第" + sNo + "行，品牌不能为空");
        if (StringUtil.isBlank(unit))
            throw new BadRequestException("第" + sNo + "行，常用单位不能为空");
        if (StringUtil.isBlank(supplier))
            throw new BadRequestException("第" + sNo + "行，供应商不能为空");
        if (StringUtil.isBlank(legalNum))
            throw new BadRequestException("第" + sNo + "行，法定数量不能为空");
        if (StringUtils.isNotEmpty(goodsNo)) {
            goodsNo = goodsNo.replaceAll("\t", "");
        }
        goodsNameC = goodsNameC.replaceAll("\t", "");
        barCode = barCode.replaceAll("\t", "");
        hsCode = hsCode.replaceAll("\t", "");
        netWeight = netWeight.replaceAll("\t", "");
        property = property.replaceAll("\t", "");
        makeContry = makeContry.replaceAll("\t", "");
        brand = brand.replaceAll("\t", "");
        unit = unit.replaceAll("\t", "");
        supplier = supplier.replaceAll("\t", "");
        legalNum = legalNum.replaceAll("\t", "");

        CustomerInfo customerInfo = customerInfoService.queryByName(cusName);
        if (customerInfo == null)
            throw new BadRequestException("第" + sNo + "行，客户名称不存在");

        ShopInfo shopInfo = shopInfoService.queryByName(shopName);
        if (StringUtils.equals(PlatformConstant.DY, shopInfo.getPlatformCode()))
            throw new BadRequestException("第" + sNo + "行，抖音店铺请上传抖音类型备案");

        if (shopInfo == null)
            throw new BadRequestException("第" + sNo + "行，店铺名称不存在");
        if (shopInfo.getCustId().intValue() != customerInfo.getId())
            throw new BadRequestException("第" + sNo + "行，该店铺不属于该客户");
        sku.setShopId(shopInfo.getId());
        if (StringUtils.isNotBlank(outerGoodsNo)) {
            BaseSku existOutGoodsNo = queryByOutGoodsNo(outerGoodsNo);
            if (existOutGoodsNo != null)
                throw new BadRequestException("第" + sNo + "行，抖音商品电商sku已存在");
        }
        if (StringUtil.isNotEmpty(goodsCode)) {
            BaseSku code = queryByCode(goodsCode);
            if (code != null)
                throw new BadRequestException("第" + sNo + "行，商品编码已存在，请勿重复导入");
        }
        if (StringUtil.isNotEmpty(goodsNo)) {
            BaseSku gNo = queryByGoodsNo(goodsNo);
            if (gNo != null)
                throw new BadRequestException("第" + sNo + "行，货号已存在，请勿重复导入");
        }
        // 法一单位、法二单位从维护好的数据中取
        CustomsTariff customsTariff = customsTariffService.queryByHs(hsCode);
        if (customsTariff == null)
            throw new BadRequestException("第" + sNo + "行，HS有误，或HS基本信息未维护");
        if (StringUtil.isNotEmpty(goodsNo)) {
            // 当有导入时有货号，则改商品已经备案过，状态直接置为备案完成
            sku.setStatus(CBGoodsStatusEnum.STATUS_130.getCode());
        }else {
            sku.setStatus(CBGoodsStatusEnum.STATUS_100.getCode());
        }
        sku.setSnControl(BooleanEnum.FAIL.getCode());
        sku.setCustomersId(customerInfo.getId());
        if (StringUtil.isBlank(goodsCode)) {
            // 当商品编码为空时，自动生成一个唯一的商品编码
            goodsCode = genGoodsCode();
        }
        sku.setPlatformCode(shopInfo.getPlatformCode());
        sku.setGoodsCode(goodsCode);
        sku.setOuterGoodsNo(outerGoodsNo);
        sku.setBarCode(barCode);
        sku.setGoodsName(goodsNameC);
        sku.setGoodsNameC(goodsNameC);
        sku.setGoodsNameE(goodsNameE);
        sku.setGoodsNo(goodsNo);
        sku.setHsCode(hsCode);
        sku.setNetWeight(new BigDecimal(netWeight));
        sku.setProperty(property);
        sku.setMakeContry(makeContry);
        CustomsCode makeContryCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.COUNTRY, makeContry);
        if (makeContryCode == null)
            throw new BadRequestException("第" + sNo + "行，原产地代码未维护：" + makeContry);
        sku.setMakeContryCode(makeContryCode.getCode());

        sku.setBrand(brand);
        sku.setUnit(unit);
        CustomsCode unitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, unit);
        if (unitCode == null)
            throw new BadRequestException("第" + sNo + "行，常用单位代码未维护：" + unit);
        sku.setUnitCode(unitCode.getCode());
        String firstUnit = customsTariff.getFirstUnit();
        CustomsCode firstUnitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, firstUnit);
        if (firstUnitCode == null)
            throw new BadRequestException("第" + sNo + "行，法一单位代码未维护：" + firstUnit);
        sku.setLegalUnit(firstUnit);
        sku.setLegalUnitCode(firstUnitCode.getCode());

        String secondUnit = customsTariff.getSecondUnit();
        if (StringUtils.isNotEmpty(secondUnit)) {
            CustomsCode secondUnitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, secondUnit);
            if (secondUnitCode == null)
                throw new BadRequestException("第" + sNo + "行，法二单位代码未维护：" + secondUnit);
            sku.setSecondUnit(secondUnit);
            sku.setSecondUnitCode(secondUnitCode.getCode());
        }

        sku.setGuse(guse);
        sku.setGcomposition(gcomposition);
        sku.setGfunction(gfunction);
        sku.setSupplier(supplier);
        sku.setLegalNum(new BigDecimal(legalNum));
        if (StringUtil.isNotBlank(secondNum)) {
            sku.setSecondNum(new BigDecimal(secondNum));
        }
        sku.setRemark(remark);
        sku.setLifecycle(Integer.valueOf(lifecycle));
        sku.setIsNew("1");
        sku.setIsGift("0");
        sku.setRegisterType(shopInfo.getRegisterType());
        return sku;
    }

    // 生成商品编码
    private synchronized String genGoodsCode() {
        int tryCount = 0;
        Random random = new Random();
        String result="7";
        for (int i=0;i<10; i++) {
            result += random.nextInt(10);
        }
        // 生成的码查询一下数据库中是否存在，如果存在再执行三次
        BaseSku baseSku = queryByCode(result);
        if (baseSku != null && tryCount < 4) {
            genGoodsCode();
            tryCount++;
        }
        return result;
    }

    @Override
    public BaseSku queryByCode(String goodsCode) {
        BaseSku baseSku = new  BaseSku();
        baseSku.setGoodsCode(goodsCode);
        Example<BaseSku> example = Example.of(baseSku);
        Optional<BaseSku> one = baseSkuRepository.findOne(example);
        return one.isPresent() ? one.get() : null;
    }

    @Override
    public BaseSku queryByBarcode(String barCode) {
        BaseSku baseSku = new BaseSku();
        baseSku.setBarCode(barCode);
        return baseSkuRepository.findOne(Example.of(baseSku)).orElse(null);
    }

    public BaseSku queryByGoodsNoAndShop(String goodsNo, Long shopId) {
        return baseSkuRepository.findByGoodsNoAndShopId(goodsNo, shopId);
    }

    @Override
    public BaseSku getProductIdByBarcode(BaseSku baseSku,String shopCode) {
        /*ShopInfo shopInfo=shopInfoService.queryByShopCode(shopCode);
        baseSku.setShopId(shopInfo.getId());*/
        return baseSkuRepository.findOne(Example.of(baseSku)).orElse(new BaseSku());
    }


    @Override
    public BaseSku queryByOutGoodsNo(String outGoodsNo) {
        return baseSkuRepository.findByOuterGoodsNo(outGoodsNo);
    }

    @Override
    public BaseSku queryByOutGoodsNoAndWarehouseCode(String outGoodsNo, String warehouseNo) {
        return baseSkuRepository.findByOuterGoodsNoAndWarehouseCode(outGoodsNo, warehouseNo);
    }

    @Override
    public BaseSku queryByGoodsNoAndWarehouseCode(String goodsNo, String warehouseNo) {
        return baseSkuRepository.findByGoodsNoAndWarehouseCode(goodsNo, warehouseNo);
    }

    @Override
    public BaseSku queryByGoodsCodeAndWarehouseCode(String goodCode, String warehouseNo) {
        return baseSkuRepository.findByGoodsCodeAndWarehouseCode(goodCode, warehouseNo);
    }

    @Override
    public void updateSkuSize() {
        BaseSkuQueryCriteria criteria = new BaseSkuQueryCriteria();
        criteria.setPlatformCode("DY");
//        criteria.setWarehouseCode("4PLFLBBC01");
        List<BaseSku> all = baseSkuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (BaseSku baseSku : all) {
                syncSize(baseSku.getId());
            }
        }
    }

    @Override
    public List<String> queryByShopIdOnlyGoodsNo(Long shopId) {
        return baseSkuRepository.queryByShopIdOnlyGoodsNo(shopId);
    }

    @Override
    public void syncSize(Long id) {
        try {
            BaseSku baseSku = queryById(id);
            BaseSkuPackInfo baseSkuPackInfo = wmsSupport.getBaseSkuPackInfo(baseSku.getGoodsNo());
            if (baseSkuPackInfo != null) {
                String barcode = baseSkuPackInfo.getBarcode();
                BigDecimal length = baseSkuPackInfo.getLength();
                BigDecimal width = baseSkuPackInfo.getWidth();
                BigDecimal height = baseSkuPackInfo.getHeight();
                BigDecimal weight = baseSkuPackInfo.getWeight();
                BigDecimal packLength = baseSkuPackInfo.getPackLength();
                BigDecimal packWidth = baseSkuPackInfo.getPackWidth();
                BigDecimal packHeight = baseSkuPackInfo.getPackHeight();
                BigDecimal packWeight = baseSkuPackInfo.getPackWeight();
                Integer packNum = baseSkuPackInfo.getPackNum();
                if (StringUtils.isNotBlank(barcode)) {
                    baseSku.setBarCode(baseSkuPackInfo.getBarcode());
                }
                if (length.compareTo(BigDecimal.ZERO) > 0) {
                    baseSku.setSaleL(length);
                }
                if (width.compareTo(BigDecimal.ZERO) > 0) {
                    baseSku.setSaleW(width);
                }
                if (height.compareTo(BigDecimal.ZERO) > 0) {
                    baseSku.setSaleH(height);
                }
                if (weight.compareTo(BigDecimal.ZERO) > 0) {
                    baseSku.setSaleWeight(weight);
                }
                if (packLength.compareTo(BigDecimal.ZERO) > 0) {
                    baseSku.setPackL(packLength);
                }
                if (packWidth.compareTo(BigDecimal.ZERO) > 0) {
                    baseSku.setPackW(packWidth);
                }
                if (packHeight.compareTo(BigDecimal.ZERO) > 0) {
                    baseSku.setPackH(packHeight);
                }
                if (packWeight.compareTo(BigDecimal.ZERO) > 0) {
                    baseSku.setPackWeight(packWeight);
                }
                if (packNum != null && packNum != 0) {
                    baseSku.setPackNum(packNum);
                }
                update(baseSku);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Cacheable(key = "'goodsNo:' + #p0")
    @Override
    public BaseSku queryByGoodsNo(String goodsNo) {
        return baseSkuRepository.findByGoodsNo(goodsNo);
    }

    @Override
    public void download(List<BaseSkuDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        CustomerInfoDto customerInfoDto = null;
        ShopInfoDto shopInfoDto = null;
        for (BaseSkuDto baseSku : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            if (customerInfoDto==null|| !customerInfoDto.getId().equals(baseSku.getCustomersId()))
                customerInfoDto = customerInfoService.queryById(baseSku.getCustomersId());
            if (shopInfoDto == null || !shopInfoDto.getId().equals(baseSku.getShopId()))
                shopInfoDto = shopInfoService.queryById(baseSku.getShopId());
            map.put("客户", customerInfoDto.getCustNickName());
            map.put("店铺", shopInfoDto.getName());
            map.put("商品编码", baseSku.getGoodsCode());
            map.put("条形码", baseSku.getBarCode());
            map.put("商品名称", baseSku.getGoodsName());
            map.put("仓库编码", baseSku.getWarehouseCode());
            map.put("长", baseSku.getSaleL());
            map.put("宽", baseSku.getSaleW());
            map.put("高", baseSku.getSaleH());
            map.put("重量", baseSku.getSaleWeight());
            map.put("箱长", baseSku.getPackL());
            map.put("箱宽", baseSku.getPackW());
            map.put("箱高", baseSku.getPackH());
            map.put("箱重", baseSku.getPackWeight());
            map.put("箱规", baseSku.getPackNum());
            map.put("海关货号", baseSku.getGoodsNo());
            map.put("外部货号", baseSku.getOuterGoodsNo());
            map.put("HS编码", baseSku.getHsCode());
            map.put("海关备案名中文", baseSku.getGoodsNameC());
            map.put("海关备案名英文", baseSku.getGoodsNameE());
            map.put("净重", baseSku.getNetWeight());
            map.put("毛重", baseSku.getGrossWeight());
            map.put("法一单位", baseSku.getLegalUnit());
            map.put("法一单位代码", baseSku.getLegalUnitCode());
            map.put("法一数量", baseSku.getLegalNum());
            map.put("法二单位", baseSku.getSecondUnit());
            map.put("法二单位代码", baseSku.getSecondUnitCode());
            map.put("法二数量", baseSku.getSecondNum());
            map.put("供应商名称", baseSku.getSupplier());
            map.put("品牌", baseSku.getBrand());
            map.put("规格型号", baseSku.getProperty());
            map.put("原产地", baseSku.getMakeContry());
            map.put("用途", baseSku.getGuse());
            map.put("成分", baseSku.getGcomposition());
            map.put("功能", baseSku.getGfunction());
            map.put("申报单位", baseSku.getUnit());
            map.put("申报单位代码", baseSku.getUnitCode());
            map.put("商品备注", baseSku.getRemark());
            map.put("保质期天数", baseSku.getLifecycle());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloads(List<BaseSkuDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BaseSkuDto baseSku : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户", baseSku.getCustomersId());
            map.put("店铺", baseSku.getShopId());
            map.put("商品编码", baseSku.getGoodsCode());
            map.put("条形码", baseSku.getBarCode());
            map.put("商品名称", baseSku.getGoodsName());
            map.put("仓库编码", baseSku.getWarehouseCode());
            map.put("长", baseSku.getSaleL());
            map.put("宽", baseSku.getSaleW());
            map.put("高", baseSku.getSaleH());
            map.put("重量", baseSku.getSaleWeight());
            map.put("箱长", baseSku.getPackL());
            map.put("箱宽", baseSku.getPackW());
            map.put("箱高", baseSku.getPackH());
            map.put("箱重", baseSku.getPackWeight());
            map.put("箱规", baseSku.getPackNum());
            map.put("海关货号", baseSku.getGoodsNo());
            map.put("外部货号", baseSku.getOuterGoodsNo());
            map.put("HS编码", baseSku.getHsCode());
            map.put("海关备案名中文", baseSku.getGoodsNameC());
            map.put("海关备案名英文", baseSku.getGoodsNameE());
            map.put("净重", baseSku.getNetWeight());
            map.put("毛重", baseSku.getGrossWeight());
            map.put("法一单位", baseSku.getLegalUnit());
            map.put("法一单位代码", baseSku.getLegalUnitCode());
            map.put("法一数量", baseSku.getLegalNum());
            map.put("法二单位", baseSku.getSecondUnit());
            map.put("法二单位代码", baseSku.getSecondUnitCode());
            map.put("法二数量", baseSku.getSecondNum());
            map.put("供应商名称", baseSku.getSupplier());
            map.put("品牌", baseSku.getBrand());
            map.put("规格型号", baseSku.getProperty());
            map.put("原产地", baseSku.getMakeContry());
            map.put("用途", baseSku.getGuse());
            map.put("成分", baseSku.getGcomposition());
            map.put("功能", baseSku.getGfunction());
            map.put("申报单位", baseSku.getUnit());
            map.put("申报单位代码", baseSku.getUnitCode());
            map.put("商品备注", baseSku.getRemark());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadStock(List<StockDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockDto baseSku : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户", baseSku.getCustomerName());
            map.put("店铺", baseSku.getShopName());
            map.put("商品编码", baseSku.getGoodsCode());
            map.put("仓库编码", baseSku.getWarehouseCode());
            map.put("外部货号", baseSku.getOuterGoodsNo());
            map.put("海关货号", baseSku.getGoodsNo());
            map.put("条形码", baseSku.getBarCode());
            map.put("商品名称", baseSku.getGoodsName());
            map.put("库位", baseSku.getLocationId());
            map.put("总库存", baseSku.getQty());
            map.put("占用库存", baseSku.getOccuQty());
            map.put("可用库存", baseSku.getAvaQty());
            map.put("生产日期", baseSku.getProdDate());
            map.put("失效日期", baseSku.getExpireDate());
            map.put("入库日期", baseSku.getInStockDate());
            map.put("产品批次号", baseSku.getCustomerBatch());
            map.put("库存属性", baseSku.getAvaOrDef());
            map.put("库存批次",baseSku.getWmsBatchNo());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<BaseSku> queryByShopId(Long shopId) {
        BaseSku baseSku=new BaseSku();
        baseSku.setShopId(shopId);
        return baseSkuRepository.findAll(Example.of(baseSku));
    }

    @Override
    public BaseSku queryByGoodsCodeAndShopId(String goodsCode, Long shopId) {
        return baseSkuRepository.findByGoodsCodeAndShopId(goodsCode, shopId);
    }

    @Override
    public void uploadRYCSku(List<Map<String, Object>> maps) {
        if (CollectionUtils.isEmpty(maps))
            throw new BadRequestException("数据为空");
        List<BaseSku> skus = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            BaseSku baseSku = checkRycSku(map);
            skus.add(baseSku);
        }
        List<BaseSku> returnSkus = baseSkuRepository.saveAll(skus);
        List<SkuLog> logs = new ArrayList<>();
        for (BaseSku sku : returnSkus) {
            SkuLog log = new SkuLog(
                    sku.getId(),
                    String.valueOf(sku.getStatus()),
                    "",
                    "",
                    SecurityUtils.getCurrentUsername()
            );
            logs.add(log);
        }
        skuLogService.saveAll(logs);
    }

    @Override
    public void banOrUnbanSku(String ids, int opt) {

        if (ids==null||ids.length()<=0)
            throw new BadRequestException("id为空");
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            if (id==null)
                continue;
            BaseSku baseSku=queryById(Long.valueOf(id));
            if (baseSku==null)
                continue;
            if (opt==0 && baseSku.getStatus()!=888)
                baseSku.setStatus(888);
            else if(opt==1&&baseSku.getStatus()==888)
                baseSku.setStatus(130);
            else
                continue;
            update(baseSku);
        }
    }



    private BaseSku checkRycSku(Map<String, Object> map) {
        int sNo = Integer.valueOf(map.get("序号").toString());
        String cusName = map.get("客户名称*") != null ? map.get("客户名称*").toString() : null;
        String shopName = map.get("店铺名称*") != null ?map.get("店铺名称*").toString() : null;
        String goodsCode = map.get("商品编码") != null ?map.get("商品编码").toString() : null;
        String goodsNo = map.get("海关货号*") != null ?map.get("海关货号*").toString() : null;
        String outerGoodsNo = map.get("电商sku") != null ?map.get("电商sku").toString() : null;
        String goodsNameC = map.get("商品中文名称*") != null ?map.get("商品中文名称*").toString() : null;
        String goodsNameE = map.get("商品英文名称") != null ?map.get("商品英文名称").toString() : null;
        String hsCode = map.get("HS编码*") != null ?map.get("HS编码*").toString() : null;
        String barCode = map.get("商品条码") != null ?map.get("商品条码").toString() : null;
        String netWeight = map.get("净重（千克)*") != null ?map.get("净重（千克)*").toString() : null;
        String property = map.get("规格型号*") != null ?map.get("规格型号*").toString() : null;
        String makeContry = map.get("原产地*") != null ?map.get("原产地*").toString() : null;
        String brand = map.get("品牌*") != null ?map.get("品牌*").toString() : null;
        String unit = map.get("常用单位*") != null ?map.get("常用单位*").toString() : null;
        String guse = map.get("用途") != null ?map.get("用途").toString() : null;
        String gcomposition = map.get("成分") != null ?map.get("成分").toString() : null;
        String gfunction = map.get("功能") != null ?map.get("功能").toString() : null;
        String supplier = map.get("供应商*") != null ?map.get("供应商*").toString() : null;
        String legalNum = map.get("法定数量*") != null ?map.get("法定数量*").toString() : null;
        String secondNum = map.get("第二数量") != null ?map.get("第二数量").toString() : null;
        String remark = map.get("商品备注") != null ?map.get("商品备注").toString() : null;
        String lifecycle = map.get("保质期天数") != null ?map.get("保质期天数").toString() : "0";
        if (StringUtil.isBlank(cusName))
            throw new BadRequestException("第" + sNo + "行，客户名称不能为空");
        if (StringUtil.isBlank(goodsCode))
            throw new BadRequestException("第" + sNo + "，商品编码不能为空");
        if (StringUtil.isBlank(shopName))
            throw new BadRequestException("第" + sNo + "行，店铺名称不能为空");
        if (StringUtil.isBlank(goodsNameC))
            throw new BadRequestException("第" + sNo + "行，商品中文名称不能为空");
//        if (StringUtil.isBlank(goodsNo))
//            throw new BadRequestException("第" + sNo + "行，海关货号不能为空");
        if (StringUtil.isBlank(barCode))
            throw new BadRequestException("第" + sNo + "行，商品条码不能为空");
        if (StringUtil.isBlank(hsCode))
            throw new BadRequestException("第" + sNo + "行，HS编码不能为空");
        if (StringUtil.isBlank(netWeight))
            throw new BadRequestException("第" + sNo + "行，净重不能为空");
        if (StringUtil.isBlank(property))
            throw new BadRequestException("第" + sNo + "行，规格型号不能为空");
        if (StringUtil.isBlank(makeContry))
            throw new BadRequestException("第" + sNo + "行，原产地不能为空");
        if (StringUtil.isBlank(brand))
            throw new BadRequestException("第" + sNo + "行，品牌不能为空");
        if (StringUtil.isBlank(unit))
            throw new BadRequestException("第" + sNo + "行，常用单位不能为空");
        if (StringUtil.isBlank(supplier))
            throw new BadRequestException("第" + sNo + "行，供应商不能为空");
        if (StringUtil.isBlank(legalNum))
            throw new BadRequestException("第" + sNo + "行，法定数量不能为空");
        if (StringUtils.isNotEmpty(goodsNo)) {
            goodsNo = goodsNo.replaceAll("\t", "");
        }
        goodsNameC = goodsNameC.replaceAll("\t", "");
        barCode = barCode.replaceAll("\t", "");
        hsCode = hsCode.replaceAll("\t", "");
        netWeight = netWeight.replaceAll("\t", "");
        property = property.replaceAll("\t", "");
        makeContry = makeContry.replaceAll("\t", "");
        brand = brand.replaceAll("\t", "");
        unit = unit.replaceAll("\t", "");
        supplier = supplier.replaceAll("\t", "");
        legalNum = legalNum.replaceAll("\t", "");

        CustomerInfo customerInfo = customerInfoService.queryByName(cusName);
        if (customerInfo == null)
            throw new BadRequestException("第" + sNo + "行，客户名称不存在");
        ShopInfo shopInfo = shopInfoService.queryByName(shopName);
        BaseSku sku = queryByCode(goodsCode);
        if (sku == null)
            sku = new BaseSku();
        if (shopInfo == null)
            throw new BadRequestException("第" + sNo + "行，店铺名称不存在");
        if (StringUtils.equals(PlatformConstant.DY, shopInfo.getPlatformCode()))
            throw new BadRequestException("第" + sNo + "行，抖音店铺请上传抖音类型备案");
        if (shopInfo.getCustId().intValue() != customerInfo.getId())
            throw new BadRequestException("第" + sNo + "行，该店铺不属于该客户");
        sku.setShopId(shopInfo.getId());
        if (StringUtils.isNotBlank(outerGoodsNo)) {
            BaseSku existOutGoodsNo = queryByOutGoodsNo(outerGoodsNo);
            if (existOutGoodsNo != null)
                throw new BadRequestException("第" + sNo + "行，抖音商品电商sku已存在");
        }
        if (StringUtil.isNotEmpty(goodsNo)) {
            BaseSku gNo = queryByGoodsNo(goodsNo);
            if (gNo != null)
                throw new BadRequestException("第" + sNo + "行，货号已存在，请勿重复导入");
        }
        // 法一单位、法二单位从维护好的数据中取
        CustomsTariff customsTariff = customsTariffService.queryByHs(hsCode);
        if (customsTariff == null)
            throw new BadRequestException("第" + sNo + "行，HS有误，或HS基本信息未维护");
        if (StringUtil.isNotEmpty(goodsNo)) {
            // 当有导入时有货号，则改商品已经备案过，状态直接置为备案完成
            sku.setStatus(CBGoodsStatusEnum.STATUS_130.getCode());
        }else {
            sku.setStatus(CBGoodsStatusEnum.STATUS_100.getCode());
        }
        sku.setSnControl(BooleanEnum.FAIL.getCode());
        sku.setCustomersId(customerInfo.getId());
        sku.setPlatformCode(shopInfo.getPlatformCode());
        sku.setGoodsCode(goodsCode);
        sku.setOuterGoodsNo(outerGoodsNo);
        sku.setBarCode(barCode);
        sku.setGoodsName(goodsNameC);
        sku.setGoodsNameC(goodsNameC);
        sku.setGoodsNameE(goodsNameE);
        sku.setGoodsNo(goodsNo);
        sku.setHsCode(hsCode);
        sku.setNetWeight(new BigDecimal(netWeight));
        sku.setProperty(property);
        sku.setMakeContry(makeContry);
        CustomsCode makeContryCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.COUNTRY, makeContry);
        if (makeContryCode == null)
            throw new BadRequestException("第" + sNo + "行，原产地代码未维护：" + makeContry);
        sku.setMakeContryCode(makeContryCode.getCode());

        sku.setBrand(brand);
        sku.setUnit(unit);
        CustomsCode unitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, unit);
        if (unitCode == null)
            throw new BadRequestException("第" + sNo + "行，常用单位代码未维护：" + unit);
        sku.setUnitCode(unitCode.getCode());
        String firstUnit = customsTariff.getFirstUnit();
        CustomsCode firstUnitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, firstUnit);
        if (firstUnitCode == null)
            throw new BadRequestException("第" + sNo + "行，法一单位代码未维护：" + firstUnit);
        sku.setLegalUnit(firstUnit);
        sku.setLegalUnitCode(firstUnitCode.getCode());

        String secondUnit = customsTariff.getSecondUnit();
        if (StringUtils.isNotEmpty(secondUnit)) {
            CustomsCode secondUnitCode = customsCodeService.queryByTypeAndDes(CustomsCodeConstant.UNIT, secondUnit);
            if (secondUnitCode == null)
                throw new BadRequestException("第" + sNo + "行，法二单位代码未维护：" + secondUnit);
            sku.setSecondUnit(secondUnit);
            sku.setSecondUnitCode(secondUnitCode.getCode());
        }

        sku.setGuse(guse);
        sku.setGcomposition(gcomposition);
        sku.setGfunction(gfunction);
        sku.setSupplier(supplier);
        sku.setLegalNum(new BigDecimal(legalNum));
        if (StringUtil.isNotBlank(secondNum)) {
            sku.setSecondNum(new BigDecimal(secondNum));
        }
        sku.setRemark(remark);
        sku.setLifecycle(Integer.valueOf(lifecycle));
        sku.setIsNew("1");
        sku.setIsGift("0");
        sku.setRegisterType(shopInfo.getRegisterType());
        return sku;
    }
}
