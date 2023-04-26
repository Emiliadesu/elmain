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

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.DewuDeclarePush;
import me.zhengjie.domain.DewuDeclarePushItem;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.DewuDeclarePushItemService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DewuDeclarePushRepository;
import me.zhengjie.service.DewuDeclarePushService;
import me.zhengjie.service.dto.DewuDeclarePushDto;
import me.zhengjie.service.dto.DewuDeclarePushQueryCriteria;
import me.zhengjie.service.mapstruct.DewuDeclarePushMapper;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
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
* @author wangm
* @date 2023-03-21
**/
@Service
@RequiredArgsConstructor
public class DewuDeclarePushServiceImpl implements DewuDeclarePushService {

    private final DewuDeclarePushRepository dewuDeclarePushRepository;
    private final DewuDeclarePushMapper dewuDeclarePushMapper;

    @Autowired
    private DewuDeclarePushItemService dewuDeclarePushItemService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private ShopInfoService shopInfoService;

    @Override
    public Map<String,Object> queryAll(DewuDeclarePushQueryCriteria criteria, Pageable pageable){
        Page<DewuDeclarePush> page = dewuDeclarePushRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dewuDeclarePushMapper::toDto));
    }

    @Override
    public List<DewuDeclarePushDto> queryAll(DewuDeclarePushQueryCriteria criteria){
        return dewuDeclarePushMapper.toDto(dewuDeclarePushRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DewuDeclarePushDto findById(Long id) {
        DewuDeclarePush dewuDeclarePush = dewuDeclarePushRepository.findById(id).orElseGet(DewuDeclarePush::new);
        ValidationUtil.isNull(dewuDeclarePush.getId(),"DewuDeclarePush","id",id);
        return dewuDeclarePushMapper.toDto(dewuDeclarePush);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DewuDeclarePushDto create(DewuDeclarePush resources) {
        return dewuDeclarePushMapper.toDto(dewuDeclarePushRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DewuDeclarePush resources) {
        DewuDeclarePush dewuDeclarePush = dewuDeclarePushRepository.findById(resources.getId()).orElseGet(DewuDeclarePush::new);
        ValidationUtil.isNull( dewuDeclarePush.getId(),"DewuDeclarePush","id",resources.getId());
        dewuDeclarePush.copy(resources);
        dewuDeclarePushRepository.save(dewuDeclarePush);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dewuDeclarePushRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DewuDeclarePushDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DewuDeclarePushDto dewuDeclarePush : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", dewuDeclarePush.getOrderNo());
            map.put("申报模式，1-仓库申报，默认传1", dewuDeclarePush.getDeclareType());
            map.put("店铺代码", dewuDeclarePush.getShopCode());
            map.put("申报单号", dewuDeclarePush.getDeclareNo());
            map.put("电商平台代码", dewuDeclarePush.getPlatformCode());
            map.put("订单创建时间", dewuDeclarePush.getOrderCreateTime());
            map.put("买家账号", dewuDeclarePush.getBuyerAccount());
            map.put("订购人电话", dewuDeclarePush.getBuyerPhone());
            map.put("订购人身份证号码", dewuDeclarePush.getBuyerIdNum());
            map.put("优惠金额合计", dewuDeclarePush.getDisAmount());
            map.put("实付金额", dewuDeclarePush.getPayment());
            map.put("运费", dewuDeclarePush.getPostFee());
            map.put("是否预售，1-是 0-否", dewuDeclarePush.getPreSell());
            map.put("预计出库时间", dewuDeclarePush.getExpDeliverTime());
            map.put("税费", dewuDeclarePush.getTaxAmount());
            map.put("优惠金额(若无传0)", dewuDeclarePush.getDiscount());
            map.put("快递公司代码", dewuDeclarePush.getLogisticsCode());
            map.put("运单号", dewuDeclarePush.getLogisticsNo());
            map.put("收货人", dewuDeclarePush.getConsigneeName());
            map.put("收货电话", dewuDeclarePush.getConsigneeTel());
            map.put("省", dewuDeclarePush.getProvince());
            map.put("市", dewuDeclarePush.getCity());
            map.put("区", dewuDeclarePush.getDistrict());
            map.put("三段码", dewuDeclarePush.getAddMark());
            map.put("毛重（千克）", dewuDeclarePush.getGrossWeight());
            map.put("净重（千克）", dewuDeclarePush.getNetWeight());
            map.put("账册编号", dewuDeclarePush.getBooksNo());
            map.put("收货地址", dewuDeclarePush.getAddress());
            map.put("支付流水号", dewuDeclarePush.getPaymentNo());
            map.put("支付方式", dewuDeclarePush.getPayType());
            map.put("支付时间", dewuDeclarePush.getPayTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void createOrder(DewuDeclarePush dewuDeclarePush) {
        if (StringUtil.isBlank(dewuDeclarePush.getOrderNo()))
            throw new BadRequestException("orderNo为空");
        if (StringUtil.isBlank(dewuDeclarePush.getShopCode()))
            throw new BadRequestException("shopCode为空");
        if (StringUtil.isBlank(dewuDeclarePush.getDeclareNo()))
            throw new BadRequestException("declareNo为空");
        if (StringUtil.isBlank(dewuDeclarePush.getPlatformCode()))
            throw new BadRequestException("platformCode为空");
        if (dewuDeclarePush.getCreateTime()==null)
            throw new BadRequestException("createTime为空");
        if (StringUtil.isBlank(dewuDeclarePush.getBuyerName()))
            throw new BadRequestException("buyerName为空");
        if (StringUtil.isBlank(dewuDeclarePush.getBuyerPhone()))
            throw new BadRequestException("buyerPhone为空");
        if (StringUtil.isBlank(dewuDeclarePush.getBuyerIdNum()))
            throw new BadRequestException("buyerIdNum为空");
        if (dewuDeclarePush.getTotalAmount()==null)
            throw new BadRequestException("totalAmount为空");
        if (dewuDeclarePush.getPayment()==null)
            throw new BadRequestException("payment为空");
        if (dewuDeclarePush.getFee()==null)
            throw new BadRequestException("fee为空");
        if (dewuDeclarePush.getTaxAmount()==null)
            throw new BadRequestException("taxAmount为空");
        if (dewuDeclarePush.getDiscount()==null)
            throw new BadRequestException("discount为空");
        if (StringUtil.isBlank(dewuDeclarePush.getLogisticsCode()))
            throw new BadRequestException("logisticsCode为空");
        if (StringUtil.isBlank(dewuDeclarePush.getLogisticsNo()))
            throw new BadRequestException("logisticsNo为空");
        if (StringUtil.isBlank(dewuDeclarePush.getConsigneeName()))
            throw new BadRequestException("consigneeName为空");
        if (StringUtil.isBlank(dewuDeclarePush.getConsigneePhone()))
            throw new BadRequestException("consigneePhone为空");
        if (StringUtil.isBlank(dewuDeclarePush.getProvince()))
            throw new BadRequestException("province为空");
        if (StringUtil.isBlank(dewuDeclarePush.getCity()))
            throw new BadRequestException("city为空");
        if (StringUtil.isBlank(dewuDeclarePush.getDistrict()))
            throw new BadRequestException("district为空");
        if (StringUtil.isBlank(dewuDeclarePush.getAddMark()))
            throw new BadRequestException("addMark为空");
        if (dewuDeclarePush.getGrossWeight()==null)
            throw new BadRequestException("grossWeight为空");
        if (dewuDeclarePush.getNetWeight()==null)
            throw new BadRequestException("netWeight为空");
        if (StringUtil.isBlank(dewuDeclarePush.getBooksNo()))
            throw new BadRequestException("booksNo为空");
        if (StringUtil.isBlank(dewuDeclarePush.getAddress()))
            throw new BadRequestException("address为空");
        if (StringUtil.isBlank(dewuDeclarePush.getPaymentNo()))
            throw new BadRequestException("paymentNo为空");
        if (StringUtil.isBlank(dewuDeclarePush.getPayTime()))
            throw new BadRequestException("payTime为空");
        if (StringUtil.isBlank(dewuDeclarePush.getPayType()))
            throw new BadRequestException("payType为空");
        if (CollectionUtils.isEmpty(dewuDeclarePush.getSkuDetails()))
            throw new BadRequestException("skuDetails为空");
        if (StringUtil.isBlank(dewuDeclarePush.getDeclareType()))
            dewuDeclarePush.setDeclareType("1");
        if (StringUtil.isBlank(dewuDeclarePush.getPreSell()))
            dewuDeclarePush.setPreSell("0");

        ShopInfo shopInfo = shopInfoService.queryByShopCode(dewuDeclarePush.getShopCode());
        if (shopInfo==null)
            throw new BadRequestException("店铺编码"+dewuDeclarePush.getShopCode()+"不存在");
        for (int i =0;i < dewuDeclarePush.getSkuDetails().size();i++) {
            DewuDeclarePushItem skuDetail = dewuDeclarePush.getSkuDetails().get(i);
            if (StringUtil.isBlank(skuDetail.getGoodsNo()))
                throw new BadRequestException("第"+(i+1)+"个商品的goodsNo为空");
            if (StringUtil.isBlank(skuDetail.getGoodsName()))
                throw new BadRequestException("第"+(i+1)+"个商品的goodsName为空");
            if (skuDetail.getQty()==null)
                throw new BadRequestException("第"+(i+1)+"个商品的qty为空");
            if (skuDetail.getQty()<=0)
                throw new BadRequestException("第"+(i+1)+"个商品的qty小于等于0");
            if (skuDetail.getDeclarePrice()==null)
                throw new BadRequestException("第"+(i+1)+"个商品的declarePrice为空");
            if (BigDecimalUtils.le(skuDetail.getDeclarePrice(),BigDecimal.ZERO))
                throw new BadRequestException("第"+(i+1)+"个商品的declarePrice为小于等于0");
            if (skuDetail.getDeclareAmount()==null)
                throw new BadRequestException("第"+(i+1)+"个商品的declareAmount为空");
            if (BigDecimalUtils.le(skuDetail.getDeclareAmount(),BigDecimal.ZERO))
                throw new BadRequestException("第"+(i+1)+"个商品的declareAmount为小于等于0");
            if (StringUtil.isBlank(skuDetail.getBarCode()))
                throw new BadRequestException("第"+(i+1)+"个商品的barCode为空");
            if (StringUtil.isBlank(skuDetail.getUnit()))
                throw new BadRequestException("第"+(i+1)+"个商品的unit为空");
            if (skuDetail.getTax()==null)
                throw new BadRequestException("第"+(i+1)+"个商品的tax为空");
            if (BigDecimalUtils.le(skuDetail.getTax(),BigDecimal.ZERO))
                throw new BadRequestException("第"+(i+1)+"个商品的tax为小于等于0");
        }
        dewuDeclarePush.setStatus(CBOrderStatusEnum.STATUS_200.getCode()+"");
        create(dewuDeclarePush);
        for (DewuDeclarePushItem skuDetail : dewuDeclarePush.getSkuDetails()) {
            skuDetail.setOrderId(dewuDeclarePush.getId());
            skuDetail.setOrderNo(dewuDeclarePush.getOrderNo());
            dewuDeclarePushItemService.create(skuDetail);
        }
        cbOrderProducer.send(MsgType.DW_215,dewuDeclarePush.getId()+"",dewuDeclarePush.getOrderNo());
    }

    @Override
    public DewuDeclarePush queryById(Long id) {
        return dewuDeclarePushRepository.queryById(id);
    }

    @Override
    public void dewuConfirmOrder(Long id) throws Exception{
        dewuConfirmOrder(queryByIdWithDetail(id));
    }
    @Override
    public void dewuConfirmOrder(DewuDeclarePush dewuDeclarePush) throws Exception{
        CrossBorderOrder crossBorderOrder = dewuDeclarePush.toCrossBorderOrder();
        //...........................

        //...........................
        dewuDeclarePush.setStatus(CBOrderStatusEnum.STATUS_215.getCode()+"");
        update(dewuDeclarePush);
        cbOrderProducer.send(MsgType.DW_220,dewuDeclarePush.getId()+"",dewuDeclarePush.getOrderNo());
    }

    @Override
    public DewuDeclarePush queryByIdWithDetail(Long id){
        DewuDeclarePush dw = dewuDeclarePushRepository.queryById(id);
        if (dw==null)
            return null;
        List<DewuDeclarePushItem>items = dewuDeclarePushItemService.queryByOrderId(id);
        dw.setSkuDetails(items);
        return dw;
    }



    @Override
    public void dewuDeclare(Long id) throws Exception{
        dewuDeclare(queryByIdWithDetail(id));
    }
    @Override
    public void dewuDeclare(DewuDeclarePush dewuDeclarePush) throws Exception{
        CrossBorderOrder crossBorderOrder = dewuDeclarePush.toCrossBorderOrder();
        String declareNo = kjgSupport.declare(crossBorderOrder);
        dewuDeclarePush.setDeclaredNo(declareNo);
        dewuDeclarePush.setStatus(CBOrderStatusEnum.STATUS_220.getCode()+"");
        update(dewuDeclarePush);
        cbOrderProducer.send(MsgType.DW_225,dewuDeclarePush.getId()+"",dewuDeclarePush.getOrderNo());
    }

    @Override
    public void dewuRefreshDeclare(Long id) throws Exception {
        dewuRefreshDeclare(queryById(id));
    }

    @Override
    public void dewuRefreshDeclare(DewuDeclarePush dewuDeclarePush) throws Exception {
        CrossBorderOrder order = dewuDeclarePush.toCrossBorderOrder();
        String res = kjgSupport.refresh(order);
        JSONObject resJSON = JSONUtil.xmlToJson(res);
        JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", resHeader.getStr("Result"))) {
            JSONObject mft = resJSON.getJSONObject("Message").getJSONObject("Body").getJSONObject("Mft");
            String ManifestId = mft.getStr("ManifestId");
            dewuDeclarePush.setInvNo(ManifestId);// 保存总署清单编号
            boolean needCallMq = false;
            if ("99".equals(String.valueOf(mft.get("Status")))) {
                if (StringUtil.equals(dewuDeclarePush.getStatus(),CBOrderStatusEnum.STATUS_880.getCode()+""))
                    needCallMq = true;
                dewuDeclarePush.setDeclareStatus("99");// 申报单取消状态
            }
            if (mft.get("CheckFlg") instanceof Integer && 0 == mft.getInt("CheckFlg")) {
                // 预校验异常
                dewuDeclarePush.setDeclareMsg(mft.getStr("CheckMsg"));
            }

            cn.hutool.json.JSONArray MftInfos = mft.getJSONObject("MftInfos").getJSONArray("MftInfo");
            String Status;
            String Result = "预校验未通过";
            if (MftInfos == null) {
                Status = mft.getStr("Status");
            } else {
                Status = MftInfos.getJSONObject(0).getStr("Status");
                Result = MftInfos.getJSONObject(0).getStr("Result");
            }
            dewuDeclarePush.setDeclareStatus(Status);
            dewuDeclarePush.setDeclareMsg(Result);
            if ("22".equals(String.valueOf(Status))) {
                // 单证放行清关完成
                dewuDeclarePush.setStatus(CBOrderStatusEnum.STATUS_230.getCode()+"");
                dewuDeclarePush.setDeclareMsg("清关完成");
                needCallMq = true;
            }
            // 更新订单状态
            update(dewuDeclarePush);

            if (StringUtil.contains(order.getDeclareMsg(), "[Code:1371")
                    || StringUtil.contains(order.getDeclareMsg(), "[Code:1313")
                    || StringUtil.contains(order.getDeclareMsg(), "人工审核")) {

            }

            if (needCallMq){
                if (StringUtil.equals(dewuDeclarePush.getDeclareStatus(),"22")&&StringUtil.equals(dewuDeclarePush.getStatus(),CBOrderStatusEnum.STATUS_230.getCode()+"")){
                    // 发送清关完成回传消息
                    cbOrderProducer.send(
                            MsgType.DW_235,
                            String.valueOf(order.getId()),
                            order.getOrderNo()
                    );
                }else if (StringUtil.equals(dewuDeclarePush.getDeclareStatus(),"99")){
                    // 发送撤单成功回传消息
                    cbOrderProducer.send(
                            MsgType.DW_888,
                            String.valueOf(order.getId()),
                            order.getOrderNo()
                    );
                }
            }
        } else {
            throw new BadRequestException(resHeader.getStr("ResultMsg"));
        }
    }

    @Override
    public void dewuConfirmDeclareSucc(Long id) {
        dewuConfirmDeclareSucc(queryById(id));
    }

    @Override
    public void dewuConfirmDeclareSucc(DewuDeclarePush dewuDeclarePush) {
        //...........................

        //...........................
        dewuDeclarePush.setStatus(CBOrderStatusEnum.STATUS_235.getCode()+"");
        update(dewuDeclarePush);
    }

    @Override
    public void dewuConfirmDelDeclareSucc(Long id) {
        dewuConfirmDelDeclareSucc(queryById(id));
    }

    @Override
    public void dewuConfirmDelDeclareSucc(DewuDeclarePush dewuDeclarePush) {
        //...........................

        //...........................
        dewuDeclarePush.setStatus(CBOrderStatusEnum.STATUS_888.getCode()+"");
        update(dewuDeclarePush);
    }

    @Override
    public void cancelOrder(String orderNo) {
        DewuDeclarePush dewuDeclarePush = dewuDeclarePushRepository.queryByOrderNo(orderNo);
        if (dewuDeclarePush==null)
            throw new BadRequestException("单号"+orderNo+"不存在");
        if (StringUtil.isNotBlank(dewuDeclarePush.getDeclaredNo())){
            CrossBorderOrder order = dewuDeclarePush.toCrossBorderOrder();
            try {
                kjgSupport.cancelOrder(order);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException(e.getMessage());
            }
            dewuDeclarePush.setStatus(CBOrderStatusEnum.STATUS_880.getCode()+"");
        }else {
            dewuDeclarePush.setStatus(CBOrderStatusEnum.STATUS_888.getCode()+"");
        }
        update(dewuDeclarePush);
    }

    @Override
    public void refreshDeclareStatus() {
        List<String>status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_225.getCode()+"");
        status.add(CBOrderStatusEnum.STATUS_880.getCode()+"");
        List<DewuDeclarePush> list = dewuDeclarePushRepository.queryAllByStatusInAndDeclaredNoNotNull(status);
        if (CollectionUtils.isNotEmpty(list)){
            for (DewuDeclarePush order : list) {
                cbOrderProducer.send(
                        MsgType.DW_230,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
            }
        }
    }

    @Override
    public void dewuConfirmDeclareStart(Long id) throws Exception{
        dewuConfirmDeclareStart(queryByIdWithDetail(id));
    }

    @Override
    public void dewuConfirmDeclareStart(DewuDeclarePush dewuDeclarePush) throws Exception{
        //...........................

        //...........................
        dewuDeclarePush.setStatus(CBOrderStatusEnum.STATUS_225.getCode()+"");
        update(dewuDeclarePush);
    }
}