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

import com.alibaba.fastjson.JSONObject;
import me.zhengjie.domain.*;
import me.zhengjie.service.DouyinGoodsDetailsService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.ShopTokenService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DouyinMailMarkRepository;
import me.zhengjie.service.DouyinMailMarkService;
import me.zhengjie.service.dto.DouyinMailMarkDto;
import me.zhengjie.service.dto.DouyinMailMarkQueryCriteria;
import me.zhengjie.service.mapstruct.DouyinMailMarkMapper;
import me.zhengjie.utils.constant.PlatformConstant;
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
* @author le
* @date 2021-09-28
**/
@Service
@RequiredArgsConstructor
public class DouyinMailMarkServiceImpl implements DouyinMailMarkService {

    private final DouyinMailMarkRepository douyinMailMarkRepository;
    private final DouyinMailMarkMapper douyinMailMarkMapper;

    @Autowired
    private DouyinGoodsDetailsService douyinGoodsDetailsService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Override
    public Map<String,Object> queryAll(DouyinMailMarkQueryCriteria criteria, Pageable pageable){
        Page<DouyinMailMark> page = douyinMailMarkRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(douyinMailMarkMapper::toDto));
    }

    @Override
    public List<DouyinMailMarkDto> queryAll(DouyinMailMarkQueryCriteria criteria){
        return douyinMailMarkMapper.toDto(douyinMailMarkRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DouyinMailMarkDto findById(Long id) {
        DouyinMailMark douyinMailMark = douyinMailMarkRepository.findById(id).orElseGet(DouyinMailMark::new);
        ValidationUtil.isNull(douyinMailMark.getId(),"DouyinMailMark","id",id);
        return douyinMailMarkMapper.toDto(douyinMailMark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DouyinMailMarkDto create(DouyinMailMark resources) {
        return douyinMailMarkMapper.toDto(douyinMailMarkRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DouyinMailMark resources) {
        DouyinMailMark douyinMailMark = douyinMailMarkRepository.findById(resources.getId()).orElseGet(DouyinMailMark::new);
        ValidationUtil.isNull( douyinMailMark.getId(),"DouyinMailMark","id",resources.getId());
        douyinMailMark.copy(resources);
        douyinMailMarkRepository.save(douyinMailMark);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            douyinMailMarkRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DouyinMailMarkDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DouyinMailMarkDto douyinMailMark : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", douyinMailMark.getOrderNo());
            map.put("客户ID", douyinMailMark.getCustomersId());
            map.put("店铺ID", douyinMailMark.getShopId());
            map.put("店铺名称", shopInfoService.queryById(shopTokenService.queryByPaltShopId(douyinMailMark.getShopId()).getShopId()).getName());
            map.put("运单号", douyinMailMark.getLogisticsNo());
            map.put("省", douyinMailMark.getProvince());
            map.put("市", douyinMailMark.getCity());
            map.put("区", douyinMailMark.getDistrict());
            map.put("大头笔", douyinMailMark.getAddMark());
            map.put("创建时间", douyinMailMark.getCreateTime());;
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public DouyinMailMark queryByOrderNo(String orderNo) {
        List<DouyinMailMark> douyinMailMarkList = douyinMailMarkRepository.findByOrderNo(orderNo);
        if (CollectionUtils.isNotEmpty(douyinMailMarkList))
            return douyinMailMarkList.get(0);
        else
            return null;
    }

    @Override
    public DouyinMailMark queryById(long id) {
        DouyinMailMark douyinMailMark = douyinMailMarkRepository.findById(id).orElseGet(DouyinMailMark::new);
        List<DouyinGoodsDetails> douyinGoodsDetails = douyinGoodsDetailsService.queryByMailMarkId(id);
        douyinMailMark.setDetailList(douyinGoodsDetails);
        return douyinMailMark;
    }

    @Override
    public List<DouyinMailMark> queryByNonSucc() {
        DouyinMailMark mailMark=new DouyinMailMark();
        mailMark.setIsSuccess("0");
        return douyinMailMarkRepository.findAll(Example.of(mailMark));
    }

    @Override
    public CrossBorderOrder toOrder(DouyinMailMark mailMark) {
        CrossBorderOrder order = new CrossBorderOrder();
        ShopToken shopToken = shopTokenService.queryByPaltShopId(mailMark.getShopId());
        order.setShopId(shopToken.getShopId());
        order.setOrderNo(mailMark.getOrderNo());
        order.setSupplierId(mailMark.getSupplierId() == null ? null : Long.valueOf(mailMark.getSupplierId()));
        order.setLogisticsNo(mailMark.getLogisticsNo());
        order.setFourPl(mailMark.getFourPl());
        order.setPlatformCode(PlatformConstant.DY);
        order.setCrossBorderNo(mailMark.getOrderNo());//跨境单号，用于订单申报、获取运单号、支付单申报，海关用支付单、运单的订单号与订单申报的订单号进行比对，如果不一致会单证审核不过
        String consignee = mailMark.getConsignee();
        order.setConsigneeName(consignee);
        String consigneeTelephone = mailMark.getConsigneeTelephone();
        order.setConsigneeTel(consigneeTelephone);
        String consigneeAddress = mailMark.getConsigneeAddress();
        JSONObject address=JSONObject.parseObject(consigneeAddress);
        String detailAddr=address.getString("detail");//详细地址
        String province = address.getJSONObject("province").getString("name");
        String city = address.getJSONObject("city").getString("name");
        String district = address.getJSONObject("town").getString("name");
        order.setConsigneeAddr(province+" "+city+" "+district+" "+detailAddr);
        order.setProvince(province);
        order.setCity(city);
        order.setDistrict(district);
        String buyerIdNumber = mailMark.getBuyerIdNumber();
        order.setBuyerIdNum(buyerIdNumber);
        String ebpCode =mailMark.getEbpCode();
        order.setEbpCode(ebpCode);
        String ebpName = mailMark.getEbpName();
        order.setEbpName(ebpName);

        List<DouyinGoodsDetails>detailsList=douyinGoodsDetailsService.queryByMailMarkId(mailMark.getId());
        BigDecimal weightSum=BigDecimal.ZERO;
        BigDecimal netWeightQtySum=BigDecimal.ZERO;
        BigDecimal payment=BigDecimal.ZERO;
        List<CrossBorderOrderDetails>itemList=new ArrayList<>();
        for (int i = 0; i < detailsList.size(); i++) {
            String weight = detailsList.get(i).getWeight();
            String netWeightQty = detailsList.get(i).getNetWeightQty();
            BigDecimal price = detailsList.get(i).getPrice().divide(new BigDecimal(100));
            weightSum=weightSum.add(StringUtil.isBlank(weight)?new BigDecimal("0.5"):new BigDecimal(weight));
            netWeightQtySum=netWeightQtySum.add(StringUtil.isBlank(netWeightQty)?new BigDecimal("0.5"):new BigDecimal(netWeightQty));
            payment=payment.add(price);
            CrossBorderOrderDetails detail=new CrossBorderOrderDetails();
            detail.setGoodsNo(detailsList.get(i).getBarCode());
            detail.setGoodsName(StringUtils.isNotBlank(detailsList.get(i).getRecordName()) ? detailsList.get(i).getRecordName() : detailsList.get(i).getItemName());
            detail.setPayment(detailsList.get(i).getPrice().toString());
            itemList.add(detail);
        }
        order.setItemList(itemList);
        order.setGrossWeight(weightSum+"");
        order.setNetWeight(netWeightQtySum+"");
        order.setPayment(payment+"");
        return order;
    }
}
