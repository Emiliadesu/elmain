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
import me.zhengjie.service.*;
import me.zhengjie.support.zhuozhi.ZhuozhiSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.AddValueOrderRepository;
import me.zhengjie.service.dto.AddValueOrderDto;
import me.zhengjie.service.dto.AddValueOrderQueryCriteria;
import me.zhengjie.service.mapstruct.AddValueOrderMapper;
import me.zhengjie.utils.enums.AddValueOrderStatusEnum;
import me.zhengjie.utils.enums.AddValueOrderTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author AddValueOrder
* @date 2021-08-05
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class AddValueOrderServiceImpl implements AddValueOrderService {

    private final AddValueOrderRepository addValueOrderRepository;
    private final AddValueOrderMapper addValueOrderMapper;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private AddValueInfoService addValueInfoService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private AddValueOrderDetailsService addValueOrderDetailsService;

    @Autowired
    private ZhuozhiSupport zhuozhiSupport;

    @Override
    public Map<String,Object> queryAll(AddValueOrderQueryCriteria criteria, Pageable pageable){
        Page<AddValueOrder> page = addValueOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(addValueOrderMapper::toDto));
    }

    @Override
    public List<AddValueOrderDto> queryAll(AddValueOrderQueryCriteria criteria){
        return addValueOrderMapper.toDto(addValueOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public AddValueOrderDto findById(Long id) {
        AddValueOrder addValueOrder = addValueOrderRepository.findById(id).orElseGet(AddValueOrder::new);
        ValidationUtil.isNull(addValueOrder.getId(),"AddValueOrder","id",id);
        return addValueOrderMapper.toDto(addValueOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddValueOrderDto create(AddValueOrder resources) {
        return addValueOrderMapper.toDto(addValueOrderRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AddValueOrder resources) {
        AddValueOrder addValueOrder = addValueOrderRepository.findById(resources.getId()).orElseGet(AddValueOrder::new);
        ValidationUtil.isNull( addValueOrder.getId(),"AddValueOrder","id",resources.getId());
        addValueOrder.copy(resources);
        addValueOrderRepository.save(addValueOrder);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            addValueOrderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<AddValueOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AddValueOrderDto addValueOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户", addValueOrder.getCustomersId());
            map.put("店铺", addValueOrder.getShopId());
//            map.put("状态", addValueOrder.getStatus());
            map.put("外部单号", addValueOrder.getOutOrderNo());
            map.put("仓区", addValueOrder.getWarehouse());
//            map.put("增值服务单号", addValueOrder.getVasNo());
            map.put("类型", addValueOrder.getType());
            map.put("关联单号", addValueOrder.getRefNo());
            map.put("备注", addValueOrder.getRemark());
            map.put("创建人", addValueOrder.getCreateBy());
            map.put("创建时间", addValueOrder.getCreateTime());
            map.put("更新人", addValueOrder.getUpdateBy());
            map.put("更新时间", addValueOrder.getUpdateTime());
//            map.put("合同费目编码", addValueOrder.getContractsinvtcode());
//            map.put("合同费目名称", addValueOrder.getWorkProcedure());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /*
    增值接口实现
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AddValueOrder addValue(String decData, Long customerId) {
        log.info("收到增值单据下发：{}", decData);
        JSONObject data = JSONObject.parseObject(decData);
        String merchantId = data.getString("merchantId");
        if (StringUtils.isEmpty(merchantId))
            throw new BadRequestException("merchantId必填");
        ShopInfo shopInfo = shopInfoService.queryByShopCode(merchantId);
        if (shopInfo == null)
            throw new BadRequestException("merchantId未配置，请联系富立技术");

        String vasNo = data.getString("vasNo");
        AddValueOrder exist = queryByOutOrderNo(vasNo);
        if (exist != null)
            throw new BadRequestException("vasNo已存在");
        AddValueOrder addValueOrder = new AddValueOrder();
        Integer type = data.getInteger("type");
        String refNo = data.getString("refNo");
        Integer refNoType = data.getInteger("refNoType");
        String notes = data.getString("notes");
        if (StringUtils.isEmpty(vasNo))
            throw new BadRequestException("vasNo必填");
        if (type == null)
            throw new BadRequestException("type必填");
        if (StringUtils.isNotEmpty(refNo) && refNoType == null)
            throw new BadRequestException("refNo存在时refNoType必填");
        addValueOrder.setOrderNo(genOrderNo());
        addValueOrder.setOutOrderNo(vasNo);
        addValueOrder.setShopId(shopInfo.getId());
        addValueOrder.setCustomersId(customerId);
        addValueOrder.setRefNo(refNo);
        addValueOrder.setRemark(notes);
        switch (type) {
            case 5:
                addValueOrder.setType(1);// 贴标
                break;
            case 6:
                addValueOrder.setType(9);// 普通加工
                break;
            case 7:
                addValueOrder.setType(5);// 残转良
                break;
            case 8:
                addValueOrder.setType(8);// 来货加工
                break;
            default:
                addValueOrder.setType(type);
                break;
        }
        JSONArray vasCodes = data.getJSONArray("vasCodes");
        if (vasCodes == null)
            throw new BadRequestException("vasCodes必填");
        for (int i = 0; i < vasCodes.size(); i++) {
            String code = vasCodes.getJSONObject(i).getString("contractsinvtcode");
            if (StringUtils.isEmpty(code))
                throw new BadRequestException("contractsinvtcode必填");
            AddValueInfo addValueInfo = addValueInfoService.queryByAddCode(code);
            if (addValueInfo == null)
                throw new BadRequestException("增值项编码不存在：" + code);
            addValueOrder.setAddCode(addValueInfo.getAddCode());
            addValueOrder.setAddName(addValueInfo.getAddName());
        }
        addValueOrder.setStatus(AddValueOrderStatusEnum.STATUS_900.getCode());
        AddValueOrderDto save = create(addValueOrder);
        JSONArray vasDetailLists = data.getJSONArray("vasDetailLists");
        if (vasDetailLists != null) {
            List<AddValueOrderDetails> saveDetails = new ArrayList<>();
            for (int i = 0; i < vasDetailLists.size(); i++) {
                String skuNo = vasDetailLists.getJSONObject(i).getString("skuNo");
                String customBatchNo = vasDetailLists.getJSONObject(i).getString("customBatchNo");
                Integer qty = vasDetailLists.getJSONObject(i).getInteger("qty");
                if (type == 5 || type == 6 || type == 7) {
                    if (StringUtils.isEmpty(skuNo))
                        throw new BadRequestException("skuNo必填");
                    if (qty == null)
                        throw new BadRequestException("qty必填");
                }
                BaseSku baseSku = baseSkuService.queryByOutGoodsNo(skuNo);
                if (baseSku == null)
                    throw new BadRequestException("skuNo不存在：" + skuNo);
                AddValueOrderDetails orderDetails = new AddValueOrderDetails();
                orderDetails.setOrderId(save.getId());
                orderDetails.setGoodsId(baseSku.getId());
                orderDetails.setGoodsCode(baseSku.getGoodsCode());
                orderDetails.setGoodsNo(baseSku.getGoodsNo());
                orderDetails.setGoodsName(baseSku.getGoodsName());
                orderDetails.setBarCode(baseSku.getBarCode());
                orderDetails.setQty(qty);
                orderDetails.setDefault01(customBatchNo);
                saveDetails.add(orderDetails);
            }
            addValueOrderDetailsService.createBatch(saveDetails);
        }
        return addValueOrder;
    }

    @Override
    public AddValueOrder queryByOutOrderNo(String outOrderNo) {
        return addValueOrderRepository.findByOutOrderNo(outOrderNo);
    }

    @Override
    public AddValueOrder queryByIdWithDetails(Long id) {
        AddValueOrder addValueOrder = addValueOrderRepository.findById(id).orElseGet(AddValueOrder::new);
        List<AddValueOrderDetails> list = addValueOrderDetailsService.queryByOrderId(addValueOrder.getId());
        addValueOrder.setDetails(list);
        return addValueOrder;
    }

    @Override
    public void finish(Long id) {
        AddValueOrder addValueOrder = queryByIdWithDetails(id);
        zhuozhiSupport.noticeAddValue(addValueOrder);
    }

    @Override
    public AddValueOrder queryByType(Integer type) {
        return addValueOrderRepository.findByType(type);
    }

    private synchronized String genOrderNo() {
        int tryCount = 0;
        String qz = "ZZ";
        String time = DateUtils.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        String result = qz + time;
        Random random = new Random();
        for (int i=0;i<4; i++) {
            result += random.nextInt(10);
        }
        AddValueOrder order = queryByOrderNo(result);
        if (order != null && tryCount < 4) {
            genOrderNo();
            tryCount++;
        }
        return result;
    }

    @Override
    public AddValueOrder queryByOrderNo(String orderNo) {
        return addValueOrderRepository.findByOrderNo(orderNo);
    }
}