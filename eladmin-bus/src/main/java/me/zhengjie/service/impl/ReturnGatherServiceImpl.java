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
import cn.hutool.json.JSONObject;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.*;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ReturnGatherRepository;
import me.zhengjie.service.mapstruct.ReturnGatherMapper;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBReturnGatherStatusEnum;
import me.zhengjie.utils.enums.CBReturnOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2022-04-06
**/
@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReturnGatherServiceImpl implements ReturnGatherService {

    private final ReturnGatherRepository returnGatherRepository;
    private final ReturnGatherMapper returnGatherMapper;

    @Autowired
    private OrderReturnService orderReturnService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private ReturnGatherDetailService returnGatherDetailService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public Map<String,Object> queryAll(ReturnGatherQueryCriteria criteria, Pageable pageable){
        Page<ReturnGather> page = returnGatherRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(returnGatherMapper::toDto));
    }

    @Override
    public List<ReturnGatherDto> queryAll(ReturnGatherQueryCriteria criteria){
        return returnGatherMapper.toDto(returnGatherRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ReturnGatherDto findById(Long id) {
        ReturnGather returnGather = returnGatherRepository.findById(id).orElseGet(ReturnGather::new);
        ValidationUtil.isNull(returnGather.getId(),"ReturnGather","id",id);
        return returnGatherMapper.toDto(returnGather);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnGatherDto create(ReturnGather resources) {
        return returnGatherMapper.toDto(returnGatherRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ReturnGather resources) {
        ReturnGather returnGather = returnGatherRepository.findById(resources.getId()).orElseGet(ReturnGather::new);
        ValidationUtil.isNull( returnGather.getId(),"ReturnGather","id",resources.getId());
        returnGather.copy(resources);
        returnGatherRepository.save(returnGather);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            returnGatherRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ReturnGatherDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReturnGatherDto returnGather : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("提总单号", returnGather.getGatherNo());
            map.put("WMS单号", returnGather.getWmsNo());
            map.put("状态", CBReturnGatherStatusEnum.getDesc(returnGather.getStatus()));
            map.put("客户名称", returnGather.getCustomersName());
            map.put("店铺名称", returnGather.getShopName());
            map.put("总单数", returnGather.getOrderNum());
            map.put("SKU数量", returnGather.getSkuNum());
            map.put("总数量", returnGather.getTotalNum());
            map.put("对比退货单数", returnGather.getReturnTotalOrder());
            map.put("对比退货件数", returnGather.getReturnTotalNum());
            map.put("创建人", returnGather.getCreateBy());
            map.put("创建时间", returnGather.getCreateTime());
            map.put("预处理完成时间", returnGather.getPreHandleTime());
            map.put("关单时间", returnGather.getCloseTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void doExportDetails(List<ReturnGatherDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReturnGatherDto returnGather : all) {
            List<ReturnGatherDetail> detailList = returnGatherDetailService.queryByGatherId(returnGather.getId());
            for(ReturnGatherDetail detail : detailList) {
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("提总单号", returnGather.getGatherNo());
                map.put("WMS单号", returnGather.getWmsNo());
                map.put("状态", CBReturnGatherStatusEnum.getDesc(returnGather.getStatus()));
                map.put("客户名称", returnGather.getCustomersName());
                map.put("店铺名称", returnGather.getShopName());
                map.put("SKU数量", returnGather.getSkuNum());
                map.put("总数量", returnGather.getTotalNum());
                map.put("创建人", returnGather.getCreateBy());
                map.put("创建时间", returnGather.getCreateTime());
                map.put("预处理完成时间", returnGather.getPreHandleTime());
                map.put("关单时间", returnGather.getCloseTime());

                map.put("货号", detail.getGoodsNo());
                map.put("名称", detail.getGoodsName());
                map.put("条码", detail.getBarCode());
                map.put("数量", detail.getQty());
                list.add(map);
            }

        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void doExportReturnOrder(List<ReturnGatherDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReturnGatherDto returnGather : all) {
            String returnIds = returnGather.getReturnIds();
            returnIds = StringUtils.strip(returnIds, "[]");
            String[] split = returnIds.split(",");
            for (int i = 0; i < split.length; i++) {
                OrderReturn orderReturn = orderReturnService.queryById(Long.valueOf(StringUtils.trim(split[i])));
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("提总单号", returnGather.getGatherNo());
                map.put("WMS单号", returnGather.getWmsNo());
                map.put("状态", CBReturnGatherStatusEnum.getDesc(returnGather.getStatus()));
                map.put("客户名称", returnGather.getCustomersName());
                map.put("店铺名称", returnGather.getShopName());
                map.put("SKU数量", returnGather.getSkuNum());
                map.put("总数量", returnGather.getTotalNum());
                map.put("创建人", returnGather.getCreateBy());
                map.put("创建时间", returnGather.getCreateTime());
                map.put("预处理完成时间", returnGather.getPreHandleTime());
                map.put("关单时间", returnGather.getCloseTime());
                map.put("退货单号", orderReturn.getOrderNo());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    // 生成提总单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void createGather(Long shopId) {
        String redisKey = "GATHER_CREATE";
        String gatherCreate = String.valueOf(redisUtils.get(redisKey));
        if (StringUtils.isNotBlank(gatherCreate)
                && !"null".equals(gatherCreate)) {
            throw new BadRequestException("有人在汇单，不要重复汇单");
        }
        // 保存redis缓存
        redisUtils.set(redisKey, "1");
        // 按店铺查询退货单清关完成回传的单子
        // 生成时只允许有一个任务在跑
        try{
            if (shopId == null)
                throw new BadRequestException("请选择一个要汇总的店铺");
            ReturnGather returnGather = new ReturnGather();
            ShopInfoDto shopInfoDto = shopInfoService.queryById(shopId);
            CustomerInfoDto customerInfoDto = customerInfoService.queryById(shopInfoDto.getCustId());
            returnGather.setShopId(shopInfoDto.getId());
            returnGather.setShopName(shopInfoDto.getName());
            returnGather.setCustomersId(customerInfoDto.getId());
            returnGather.setCustomersName(customerInfoDto.getCustName());
            returnGather.setStatus(CBReturnGatherStatusEnum.STATUS_300.getCode());
            returnGather.setGatherNo(genOrderNo());
            returnGather.setCreateBy(SecurityUtils.getCurrentUsername());
            returnGather.setCreateTime(new Timestamp(System.currentTimeMillis()));
            create(returnGather);

            // 查询的同时保存明细
            orderReturnService.queryGatherByShop(shopId, returnGather.getId(), returnGather.getGatherNo());

            List<ReturnGatherDetail> detailList = returnGatherDetailService.queryByGatherId(returnGather.getId());
            if (CollectionUtils.isEmpty(detailList))
                throw new BadRequestException("该店铺无可汇单数据");
            HashSet<Long> returnIds = new HashSet<>();// 退货单ID明细
            BigDecimal skuNum = BigDecimal.ZERO;
            BigDecimal totalNum = BigDecimal.ZERO;
            for (ReturnGatherDetail returnGatherDetail : detailList) {
                String ids = returnGatherDetail.getReturnIds();
                String[] split = ids.split(",");
                for (int i = 0; i < split.length; i++) {
                    returnIds.add(Long.valueOf(split[i]));
                }
                ReturnGatherDetail detail = new ReturnGatherDetail();
                Integer qty = returnGatherDetail.getQty();
                skuNum = skuNum.add(BigDecimal.ONE);
                totalNum = totalNum.add(new BigDecimal(qty));
            }
            returnGather.setSkuNum(skuNum.intValue());
            returnGather.setTotalNum(totalNum.intValue());
            returnGather.setReturnIds(returnIds.toString());
            returnGather.setOrderNum(returnIds.size());

            update(returnGather);
            for (Long returnId : returnIds) {
                OrderReturn orderReturn = orderReturnService.queryById(returnId);
                orderReturn.setIsWave("1");// 波次产生
                orderReturn.setGatherNo(returnGather.getGatherNo());
                orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_350.getCode());
                orderReturnService.update(orderReturn);
            }
            redisUtils.del(redisKey);
        }catch (Exception e) {
            redisUtils.del(redisKey);
            throw e;
        }

    }

    @Override
    public void compare(Long id) {
        ReturnGather returnGather = queryByIdWithDetails(id);
        String returnIds = returnGather.getReturnIds();
        returnIds = StringUtils.strip(returnIds, "[]");
        String[] split = returnIds.split(",");
        BigDecimal returnTotalNum = BigDecimal.ZERO;
        BigDecimal returnTotalOrder = BigDecimal.ZERO;
        for (int i = 0; i < split.length; i++) {
            OrderReturn orderReturn = orderReturnService.queryByIdWithDetails(Long.valueOf(StringUtils.trim(split[i])));
            returnTotalOrder = returnTotalOrder.add(new BigDecimal(1));
            List<OrderReturnDetails> itemList = orderReturn.getItemList();
            for (OrderReturnDetails details : itemList) {
                returnTotalNum = returnTotalNum.add(new BigDecimal(details.getQty()));
            }
        }
        returnGather.setReturnTotalOrder(returnTotalOrder.intValue());
        returnGather.setReturnTotalNum(returnTotalNum.intValue());
        update(returnGather);
    }

    // 推送WMS
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pushToWms(Long id) throws UnsupportedEncodingException {
        ReturnGather returnGather = queryByIdWithDetails(id);
        if (returnGather.getStatus().intValue() != CBReturnGatherStatusEnum.STATUS_300.getCode().intValue())
            throw new BadRequestException("此状态不能推送");
        wmsSupport.pushReturnGather(returnGather);
        returnGather.setStatus(CBReturnGatherStatusEnum.STATUS_315.getCode());
        returnGather.setPreHandleTime(new Timestamp(System.currentTimeMillis()));
        update(returnGather);
        String returnIds = returnGather.getReturnIds();
        returnIds = StringUtils.strip(returnIds, "[]");
        String[] split = returnIds.split(",");
        for (int i = 0; i < split.length; i++) {
            OrderReturn orderReturn = orderReturnService.queryById(Long.valueOf(StringUtils.trim(split[i])));
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_355.getCode());
            orderReturnService.update(orderReturn);
        }
    }

    // 刷新WMS状态
    @Override
    public void updateWMsStatus(Long id) {
        try {
            ReturnGather returnGather = queryById(id);
            JSONObject wmsOrder = wmsSupport.queryAsn(returnGather.getGatherNo());
            if (wmsOrder == null)
                throw new BadRequestException("获取WMS状态失败:" + returnGather.getGatherNo());
            if (StringUtil.isEmpty(returnGather.getWmsNo())) {
                returnGather.setWmsNo(wmsOrder.getJSONObject("header").getStr("asnno"));
                update(returnGather);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关单
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void closeOrder(Long id) {
        ReturnGather returnGather = queryById(id);
        if (returnGather.getStatus().intValue() != CBReturnGatherStatusEnum.STATUS_315.getCode().intValue())
            throw new BadRequestException("此状态不能关单");
        returnGather.setStatus(CBReturnGatherStatusEnum.STATUS_320.getCode());
        returnGather.setCloseTime(new Timestamp(System.currentTimeMillis()));
        update(returnGather);
        String returnIds = returnGather.getReturnIds();
        returnIds = StringUtils.strip(returnIds, "[]");
        String[] split = returnIds.split(",");
        for (int i = 0; i < split.length; i++) {
            orderReturnService.closeOrder(Long.valueOf(StringUtils.trim(split[i])));
        }
    }



    @Override
    public ReturnGather queryById(Long id) {
        return returnGatherRepository.findById(id).orElseGet(ReturnGather::new);
    }

    // 查询待汇波
    @Override
    public List<Map<String, Object>> queryWaitGather() {
        return orderReturnService.queryWaitGather();
    }


    private synchronized String genOrderNo() {
        int tryCount = 0;
        String qz = "THTZ";
        String time = String.valueOf(System.currentTimeMillis());
        String result = qz + time;
        Random random = new Random();
        for (int i=0;i<4; i++) {
            result += random.nextInt(10);
        }
        ReturnGather order = queryByGatherNo(result);
        if (order != null && tryCount < 4) {
            genOrderNo();
            tryCount++;
        }
        return result;
    }

    @Override
    public ReturnGather queryByGatherNo(String gatherNo) {
        return returnGatherRepository.findByGatherNo(gatherNo);
    }

    @Override
    public ReturnGather queryByIdWithDetails(Long id) {
        ReturnGather returnGather = returnGatherRepository.findById(id).orElseGet(ReturnGather::new);
        List<ReturnGatherDetail> detailList = returnGatherDetailService.queryByGatherId(id);
        returnGather.setItemList(detailList);
        return returnGather;
    }


}