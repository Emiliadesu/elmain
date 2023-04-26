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

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.rest.model.Flux.DocOrderHeader;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.PddPrintData;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.pdd.*;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PddCloudPrintDataRepository;
import me.zhengjie.service.dto.PddCloudPrintDataDto;
import me.zhengjie.service.dto.PddCloudPrintDataQueryCriteria;
import me.zhengjie.service.mapstruct.PddCloudPrintDataMapper;
import me.zhengjie.utils.constant.MsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author wangmiao
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2022-07-19
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class PddCloudPrintDataServiceImpl implements PddCloudPrintDataService {

    private final PddCloudPrintDataRepository pddCloudPrintDataRepository;
    private final PddCloudPrintDataMapper pddCloudPrintDataMapper;

    @Value("${pdd.pddUserId}")
    private String pddUserId;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Autowired
    private PDDSupport pddSupport;

    @Autowired
    private PddOrderService pddOrderService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Override
    public Map<String, Object> queryAll(PddCloudPrintDataQueryCriteria criteria, Pageable pageable) {
        Page<PddCloudPrintData> page = pddCloudPrintDataRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(pddCloudPrintDataMapper::toDto));
    }

    @Override
    public List<PddCloudPrintDataDto> queryAll(PddCloudPrintDataQueryCriteria criteria) {
        return pddCloudPrintDataMapper.toDto(pddCloudPrintDataRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public PddCloudPrintDataDto findById(Long id) {
        PddCloudPrintData pddCloudPrintData = pddCloudPrintDataRepository.findById(id).orElseGet(PddCloudPrintData::new);
        ValidationUtil.isNull(pddCloudPrintData.getId(), "PddCloudPrintData", "id", id);
        return pddCloudPrintDataMapper.toDto(pddCloudPrintData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PddCloudPrintDataDto create(PddCloudPrintData resources) {
        return pddCloudPrintDataMapper.toDto(pddCloudPrintDataRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PddCloudPrintData resources) {
        PddCloudPrintData pddCloudPrintData = pddCloudPrintDataRepository.findById(resources.getId()).orElseGet(PddCloudPrintData::new);
        ValidationUtil.isNull(pddCloudPrintData.getId(), "PddCloudPrintData", "id", resources.getId());
        pddCloudPrintData.copy(resources);
        pddCloudPrintDataRepository.save(pddCloudPrintData);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            pddCloudPrintDataRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PddCloudPrintDataDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PddCloudPrintDataDto pddCloudPrintData : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("拼多多订单号", pddCloudPrintData.getOrderNo());
            map.put("拼多多XP单号", pddCloudPrintData.getCrossBorderOrderNo());
            map.put("电子面单云打印数据", pddCloudPrintData.getPrintData());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void saveOrUpdate(PddCloudPrintData pddCloudPrintData) {
        if (pddCloudPrintData.getCrossBorderOrderNo().indexOf("~") == 0) {
            //未解密XP单号
            PddEncryptRequest request = new PddEncryptRequest();
            request.setOrderSn(pddCloudPrintData.getOrderNo());
            request.setShopId(pddCloudPrintData.getShopCode());
            request.setEncrypt(pddCloudPrintData.getCrossBorderOrderNo());
            PddCommonResponse<PddEncryptResponse> response = null;
            try {
                response = pddSupport.request(PddEncryptResponse.class,request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null && response.isSuccess()) {
                PddEncryptResponse data = response.getData();
                pddCloudPrintData.setCrossBorderOrderNo(data.getInnerTransactionId());
            }
        }
        PddCloudPrintData data = pddCloudPrintDataRepository.findByOrderNo(pddCloudPrintData.getOrderNo());
        if (data != null) {
            pddCloudPrintData.setId(data.getId());
            update(pddCloudPrintData);
        } else
            create(pddCloudPrintData);
    }

    @Override
    public void saveOrUpdateByMq(String body) {
        saveOrUpdate(new cn.hutool.json.JSONObject(body).toBean(PddCloudPrintData.class));
    }

    @Override
    public JSON print(List<String> orderNos) {
        List<PddCloudPrintData> printDataList = new ArrayList<>();
        for (String orderNo : orderNos) {
            CrossBorderOrder order = crossBorderOrderService.queryByOrderNoWithDetails(orderNo);
            if (StringUtil.isBlank(order.getSoNo()) || StringUtil.isBlank(order.getWaveNo())) {
                crossBorderOrderService.refreshWmsStatus(order.getId() + "");
            }
            if (order.getStatus() == 888) {
                throw new BadRequestException("订单号" + orderNo + "已取消");
            }
            if (!StringUtil.equals(order.getPlatformCode(), "PDD"))
                throw new BadRequestException("订单号" + orderNo + "不是拼多多的");
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null)
                throw new BadRequestException("订单" + orderNo + "没有授权令牌");
            try {
                int status=crossBorderOrderService.getPlatformStatus(order);
                if (status==16){
                    throw new BadRequestException("售后处理中:"+orderNo);
                }else if (status==17){
                    throw new BadRequestException("退款中:"+orderNo);
                }else if (status==21){
                    throw new BadRequestException("退款完成:"+orderNo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ShopInfo shopInfo = shopInfoService.findById(order.getShopId());
            PddCloudPrintData printData = pddCloudPrintDataRepository.findByOrderNo(orderNo);
            //判断是否有打印数据，没有则调用获取面单接口
            if (printData == null) {
                List<DocOrderHeader> headerList = wmsSupport.queryOrderByWaveNo(order.getWaveNo());//补打面单时需要得到这个波次号的所有订单，得出这个订单在这个波次的索引得到篮号
                if (CollectionUtils.isEmpty(headerList))
                    throw new BadRequestException("订单号" + orderNo + "的波次号查不到任何订单");
                String basketNum = null;
                for (DocOrderHeader header: headerList) {
                    if (StringUtil.equals(header.getSoreference5(), order.getLogisticsNo())) {
                        basketNum = header.getSeqno() + "/" + headerList.size();
                        break;
                    }
                }
                PddPullOrderByOrderSnRequest request = new PddPullOrderByOrderSnRequest();
                request.setOrderSns(new String[]{orderNo});
                request.setShopCode(shopToken.getPlatformShopId());
                PddCommonResponse<PddOrder> resp;
                try {
                    resp = pddSupport.request(PddOrder.class,request);
                    if (!resp.isSuccess())
                        throw new BadRequestException(resp.getMsg());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("订单号" + orderNo + "获取打印数据错误:" + e.getMessage());
                }
                if (resp.getDataArray().size()==0){
                    cn.hutool.json.JSONObject obj=new cn.hutool.json.JSONObject(resp);
                    throw new BadRequestException("订单号"+orderNo+obj.getJSONObject("data").getJSONObject("errOrderSns").getStr(orderNo));
                }
                try {
                    printData = pddOrderService.getMailNo(resp.getDataArray().get(0));
                    printData.setSoNo(order.getSoNo());
                    printData.setWaveNo(order.getWaveNo());
                    printData.setBasketNum(basketNum);
                    printData.setSender(shopInfo.getName());
                    printData.setSenderPhone(shopInfo.getContactPhone());
                    printData.setCity(order.getCity());
                    List<CrossBorderOrderDetails> detailsList = crossBorderOrderDetailsService.queryByOrderId(order.getId());
                    int total = 0;
                    StringBuilder detailBuild = new StringBuilder();
                    for (CrossBorderOrderDetails details : detailsList) {
                        total += Integer.parseInt(details.getQty());
                        if (detailBuild.length() != 0) {
                            detailBuild.append("，");
                        }
                        detailBuild.append(String.format("%s %s件",
                                details.getBarCode(),
                                details.getQty()));
                    }
                    printData.setDetail(detailBuild.toString());
                    if (StringUtil.isBlank(order.getSkuNum())) {
                        printData.setTotal(total + "");
                        printData.setSkuTotal(detailsList.size() + "");
                    } else {
                        printData.setTotal(order.getTotalNum());
                        printData.setSkuTotal(order.getSkuNum());

                    }
                    printData.setShopCode(order.getPlatformShopId());
                    printDataList.add(printData);
                    cbOrderProducer.send(
                            MsgType.CB_ORDER_PDD_SAVE_PRINTDATA,
                            new cn.hutool.json.JSONObject(printData).toString(),
                            order.getOrderNo()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("订单号打印面单发生错误:" + e.getMessage());
                }
            } else {
                if (StringUtil.isBlank(printData.getWaveNo())||StringUtil.isBlank(printData.getSkuTotal())) {
                    List<DocOrderHeader> headerList = wmsSupport.queryOrderByWaveNo(order.getWaveNo());//补打面单时需要得到这个波次号的所有订单，得出这个订单在这个波次的索引得到篮号
                    if (CollectionUtils.isEmpty(headerList))
                        throw new BadRequestException("订单号" + orderNo + "的波次号查不到任何订单");
                    String basketNum = null;
                    //计算篮号
                    for (DocOrderHeader header:headerList) {
                        if (StringUtil.equals(header.getSoreference5(), order.getLogisticsNo())) {
                            basketNum = header.getSeqno() + "/" + headerList.size();
                            break;
                        }
                    }
                    if (StringUtil.isBlank(order.getSkuNum()) || StringUtil.isBlank(printData.getDetail())) {
                        List<CrossBorderOrderDetails> detailsList = order.getItemList();
                        int total = 0;
                        StringBuilder detailBuild = new StringBuilder();
                        for (CrossBorderOrderDetails details : detailsList) {
                            total += Integer.parseInt(details.getQty());
                            if (detailBuild.length() != 0) {
                                detailBuild.append("，");
                            }
                            //装箱清单-出库明细
                            detailBuild.append(String.format("%s %s件",
                                    details.getBarCode(),
                                    details.getQty()));
                        }
                        //包裹明细
                        if (StringUtil.isBlank(order.getSkuNum())) {
                            printData.setTotal(total + "");
                            printData.setSkuTotal(detailsList.size() + "");
                        }
                        if (StringUtil.isBlank(printData.getDetail())) {
                            printData.setDetail(detailBuild.toString());
                        }
                    } else {
                        printData.setTotal(order.getTotalNum());
                        printData.setSkuTotal(order.getSkuNum());
                    }
                    printData.setSoNo(order.getSoNo());
                    printData.setWaveNo(order.getWaveNo());
                    printData.setBasketNum(basketNum);
                    printData.setSender(shopInfo.getName());
                    printData.setSenderPhone(shopInfo.getContactPhone());
                    printData.setCity(order.getCity());
                    cbOrderProducer.send(
                            MsgType.CB_ORDER_PDD_SAVE_PRINTDATA,
                            new cn.hutool.json.JSONObject(printData).toString(),
                            order.getOrderNo()
                    );
                }
                printDataList.add(printData);
            }
        }
        for (PddCloudPrintData printData : printDataList) {
            PddCloudPrintLog printLog = new PddCloudPrintLog();
            printLog.setMailNo(printData.getMailNo());
            printLog.setPrintTime(DateUtils.now());
            printLog.setPrintOperator(SecurityUtils.getCurrentUsername());
            cbOrderProducer.send(
                    MsgType.CB_ORDER_PDD_PRINT_LOG_SAVE,
                    JSONObject.toJSONString(printLog),
                    printLog.getMailNo()
            );
        }
        String mainUrl = null;
        String customUrl = null;
        Config config = configService.queryByK("PDD_CLOUD_PRINT_MODEL_URL");//云打印主面单模板url
        if (config != null)
            mainUrl = config.getV();
        config = configService.queryByK("PDD_CLOUD_PRINT_CUSTOM_MODEL_URL");//云打印自定义数据模板url;
        if (config != null)
            customUrl = config.getV();
        return JSONObject.parseObject(JSONObject.toJSONString(new PddPrintData(printDataList, pddUserId, mainUrl, customUrl)));
    }

    @Override
    public JSON printByWaveNo(String[] waveNos) {
        if (ArrayUtil.isEmpty(waveNos))
            throw new BadRequestException("波次号为空");
        List<PddCloudPrintData> allList = new ArrayList<>();
        StringBuilder errRecord = new StringBuilder();
        for (String waveNo : waveNos) {
            List<DocOrderHeader> orderHeaderList = wmsSupport.queryOrderByWaveNo(waveNo);
            if (CollectionUtils.isEmpty(orderHeaderList)) {
                errRecord.append("根据波次号").append(waveNo).append("查询到的订单为空\n");
                continue;
            }
            for (DocOrderHeader orderHeader:orderHeaderList) {
                PddCloudPrintData printData = pddCloudPrintDataRepository.findByCrossBorderOrderNo(orderHeader.getSoreference2());
                PddCloudPrintLog printLog = new PddCloudPrintLog();
                if (StringUtil.equals(orderHeader.getSostatus(), "90") || StringUtil.equals(orderHeader.getSostatus(), "99")) {
                    errRecord.append("富勒订单状态处于取消和完成的运单号:").append(orderHeader.getSoreference5()).append("\n");
                    continue;
                }
                if (printData == null) {
                    CrossBorderOrder order = crossBorderOrderService.queryByCrossBorderNo(orderHeader.getSoreference2());
                    if (order == null) {
                        //该波次号夹杂不属于erp系统的运单号
                        errRecord.append("不属于erp系统的运单号:").append(orderHeader.getSoreference5()).append("\n");
                        continue;
                    } else if (!StringUtil.equals(order.getPlatformCode(), "PDD")) {
                        //不属于拼多多的单号
                        errRecord.append("不属于拼多多的运单号:").append(orderHeader.getSoreference5()).append("\n");
                        continue;
                    } else if (StringUtil.isBlank(order.getPlatformShopId())) {
                        //没有授权令牌
                        errRecord.append("没有授权令牌的运单号:").append(orderHeader.getSoreference5()).append("\n");
                        continue;
                    } else {
                        try {
                            int status=crossBorderOrderService.getPlatformStatus(order);
                            if (status==16){
                                errRecord.append("售后处理中:").append(orderHeader.getSoreference5()).append("\n");
                                continue;
                            }else if (status==17){
                                errRecord.append("退款中:").append(orderHeader.getSoreference5()).append("\n");
                                continue;
                            }else if (status==21){
                                errRecord.append("退款完成:").append(orderHeader.getSoreference5()).append("\n");
                                continue;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        PddPullOrderByOrderSnRequest request = new PddPullOrderByOrderSnRequest();
                        request.setOrderSns(new String[]{order.getOrderNo()});
                        request.setShopCode(order.getPlatformShopId());
                        PddCommonResponse<PddOrder> resp;
                        try {
                            resp = pddSupport.request(PddOrder.class,request);
                            if (!resp.isSuccess())
                                throw new BadRequestException(resp.getMsg());
                        } catch (Exception e) {
                            e.printStackTrace();
                            errRecord.append("运单号").append(orderHeader.getSoreference5()).append("获取打印数据错误:").append(e.getMessage());
                            continue;
                        }
                        if (resp.getDataArray().size()==0){
                            cn.hutool.json.JSONObject obj=new cn.hutool.json.JSONObject(resp);
                            errRecord.append("运单号").append(orderHeader.getSoreference5()).append(obj.getJSONObject("data").getJSONObject("errOrderSns").getStr(order.getOrderNo()));
                            continue;
                        }
                        ShopInfo shopInfo = shopInfoService.findById(order.getShopId());
                        try {
                            PddCloudPrintData printDataNew = pddOrderService.getMailNo(resp.getDataArray().get(0));
                            printDataNew.setSoNo(orderHeader.getOrderno());
                            printDataNew.setWaveNo(orderHeader.getWaveno());
                            printDataNew.setBasketNum(orderHeader.getSeqno() + "/" + orderHeaderList.size());
                            printDataNew.setSender(shopInfo.getName());
                            printDataNew.setSenderPhone(shopInfo.getContactPhone());
                            printDataNew.setCity(order.getCity());
                            printDataNew.setShopCode(order.getPlatformShopId());
                            List<CrossBorderOrderDetails> detailsList = crossBorderOrderDetailsService.queryByOrderId(order.getId());
                            int total = 0;
                            StringBuilder detailBuild = new StringBuilder();
                            for (CrossBorderOrderDetails details : detailsList) {
                                total += Integer.parseInt(details.getQty());
                                if (detailBuild.length() != 0) {
                                    detailBuild.append("，");
                                }
                                detailBuild.append(String.format("%s %s件",
                                        details.getBarCode(),
                                        details.getQty()));
                            }
                            printDataNew.setDetail(detailBuild.toString());
                            if (StringUtil.isBlank(order.getSkuNum())) {
                                printDataNew.setTotal(total + "");
                                printDataNew.setSkuTotal(detailsList.size() + "");
                            } else {
                                printDataNew.setTotal(order.getTotalNum());
                                printDataNew.setSkuTotal(order.getSkuNum());

                            }
                            printLog.setMailNo(printDataNew.getMailNo());
                            printLog.setPrintOperator(SecurityUtils.getCurrentUsername());
                            printLog.setPrintTime(DateUtils.now());
                            allList.add(printDataNew);
                            cbOrderProducer.send(
                                    MsgType.CB_ORDER_PDD_SAVE_PRINTDATA,
                                    new cn.hutool.json.JSONObject(printDataNew).toString(),
                                    order.getOrderNo()
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                            errRecord.append("订单号打印面单发生错误:").append(e.getMessage()).append("\n");
                            continue;
                        }
                    }
                } else {
                    printData.setBasketNum(orderHeader.getSeqno() + "/" + orderHeaderList.size());
                    printData.setWaveNo(waveNo);
                    printData.setSoNo(orderHeader.getOrderno());
                    if (StringUtil.isBlank(printData.getSkuTotal())
                            || StringUtil.isBlank(printData.getDetail())
                            || StringUtil.isBlank(printData.getCity())) {
                        CrossBorderOrder order = crossBorderOrderService.queryByCrossBorderNoWithDetails(orderHeader.getSoreference2());
                        try {
                            int status=crossBorderOrderService.getPlatformStatus(order);
                            if (status==16){
                                errRecord.append("售后处理中:").append(orderHeader.getSoreference5()).append("\n");
                                continue;
                            }else if (status==17){
                                errRecord.append("退款中:").append(orderHeader.getSoreference5()).append("\n");
                                continue;
                            }else if (status==21){
                                errRecord.append("退款完成:").append(orderHeader.getSoreference5()).append("\n");
                                continue;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (StringUtil.isBlank(printData.getCity()))
                            printData.setCity(order.getCity());
                        if (StringUtil.isBlank(order.getSkuNum()) || StringUtil.isBlank(printData.getDetail())) {
                            List<CrossBorderOrderDetails> detailsList = order.getItemList();
                            int total = 0;
                            StringBuilder detailBuild = new StringBuilder();
                            for (CrossBorderOrderDetails details : detailsList) {
                                total += Integer.parseInt(details.getQty());
                                if (detailBuild.length() != 0) {
                                    detailBuild.append("，");
                                }
                                detailBuild.append(String.format("%s %s件",
                                        details.getBarCode(),
                                        details.getQty()));
                            }
                            if (StringUtil.isBlank(order.getSkuNum())) {
                                printData.setTotal(total + "");
                                printData.setSkuTotal(detailsList.size() + "");
                            }
                            if (StringUtil.isBlank(printData.getDetail())) {
                                printData.setDetail(detailBuild.toString());
                            }
                        } else {
                            printData.setTotal(order.getTotalNum());
                            printData.setSkuTotal(order.getSkuNum());
                        }
                        cbOrderProducer.send(
                                MsgType.CB_ORDER_PDD_SAVE_PRINTDATA,
                                new cn.hutool.json.JSONObject(printData).toString(),
                                order.getOrderNo()
                        );
                    }
                    allList.add(printData);
                    printLog.setMailNo(printData.getMailNo());
                    printLog.setPrintOperator(SecurityUtils.getCurrentUsername());
                    printLog.setPrintTime(DateUtils.now());
                }
                cbOrderProducer.send(
                        MsgType.CB_ORDER_PDD_PRINT_LOG_SAVE,
                        JSONObject.toJSONString(printLog),
                        printLog.getMailNo()
                );
            }
        }
        JSONObject result = new JSONObject();
        String mainUrl = null;
        String customUrl = null;
        Config config = configService.queryByK("PDD_CLOUD_PRINT_MODEL_URL");//云打印主面单模板url
        if (config != null)
            mainUrl = config.getV();
        config = configService.queryByK("PDD_CLOUD_PRINT_CUSTOM_MODEL_URL");//云打印自定义数据模板url;
        if (config != null)
            customUrl = config.getV();
        result.put("printData", JSONObject.parseObject(JSONObject.toJSONString(new PddPrintData(allList, pddUserId, mainUrl, customUrl))));
        result.put("errRecord", errRecord);
        return result;
    }

    @Override
    public PddCloudPrintData findByOrderNo(String orderNo) {
        return pddCloudPrintDataRepository.findByOrderNo(orderNo);
    }
}