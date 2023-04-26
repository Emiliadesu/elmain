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

import me.zhengjie.domain.ByteDance;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ByteDanceRepository;
import me.zhengjie.service.ByteDanceService;
import me.zhengjie.service.dto.ByteDanceDto;
import me.zhengjie.service.dto.ByteDanceQueryCriteria;
import me.zhengjie.service.mapstruct.ByteDanceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
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
* @date 2021-03-21
**/
@Service
@RequiredArgsConstructor
public class ByteDanceServiceImpl implements ByteDanceService {

    private final ByteDanceRepository byteDanceRepository;
    private final ByteDanceMapper byteDanceMapper;

    @Override
    public Map<String,Object> queryAll(ByteDanceQueryCriteria criteria, Pageable pageable){
        Page<ByteDance> page = byteDanceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(byteDanceMapper::toDto));
    }

    @Override
    public List<ByteDanceDto> queryAll(ByteDanceQueryCriteria criteria){
        return byteDanceMapper.toDto(byteDanceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ByteDanceDto findById(Long id) {
        ByteDance byteDance = byteDanceRepository.findById(id).orElseGet(ByteDance::new);
        ValidationUtil.isNull(byteDance.getId(),"ByteDance","id",id);
        return byteDanceMapper.toDto(byteDance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ByteDanceDto create(ByteDance resources) {
        return byteDanceMapper.toDto(byteDanceRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ByteDance resources) {
        ByteDance byteDance = byteDanceRepository.findById(resources.getId()).orElseGet(ByteDance::new);
        ValidationUtil.isNull( byteDance.getId(),"ByteDance","id",resources.getId());
        byteDance.copy(resources);
        byteDanceRepository.save(byteDance);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            byteDanceRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ByteDanceDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ByteDanceDto byteDance : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单编号", byteDance.getOrderId());
            map.put("电商平台代码", byteDance.getEbpCode());
            map.put("电商平台名", byteDance.getEbpName());
            map.put("电商企业代码", byteDance.getEbcCode());
            map.put("电商企业名", byteDance.getEbcName());
            map.put("店铺在电商平台的id", byteDance.getShopId());
            map.put("店铺在电商平台的名称", byteDance.getShopName());
            map.put("进出口标识", byteDance.getIeFlag());
            map.put("通关模式", byteDance.getCustomsClearType());
            map.put("申报海关代码", byteDance.getCustomsCode());
            map.put("口岸海关代码", byteDance.getPortCode());
            map.put("商家仓库编码", byteDance.getWarehouseCode());
            map.put("商品实际成交价， 含非现金抵扣金额（商品不含税价*商品数量）单位是分，数据库要求元", byteDance.getGoodsValue());
            map.put("运杂费（含物流保费）免邮传0，单位是分，数据库要求元", byteDance.getFreight());
            map.put("非现金抵扣金额（不含支付满减）使用积分等非现金支付金额，无则填写 \"0\" 单位是分，数据库要求元", byteDance.getDiscount());
            map.put("代扣税款  企业预先代扣的税款金额，无则填写“0” 单位是分,数据库要求元", byteDance.getTaxTotal());
            map.put("实际支付金额（商品价格+运杂费+代扣税款- 非现金抵扣金额）单位是分,数据库要求元", byteDance.getActuralPaid());
            map.put("物流保费（物流保价费）  一般传0  单位是分,数据库要求元", byteDance.getInsuredFee());
            map.put("币制 限定为人民币，填写“142”", byteDance.getCurrency());
            map.put("订购人注册号", byteDance.getBuyerRegNo());
            map.put("订购人姓名", byteDance.getBuyerName());
            map.put("订购人电话", byteDance.getBuyerTelephone());
            map.put("订购人证件类型", byteDance.getBuyerIdType());
            map.put("订购人证件号码", byteDance.getBuyerIdNumber());
            map.put("收货人", byteDance.getConsignee());
            map.put("收货人电话", byteDance.getConsigneeTelephone());
            map.put("收货地址(JSON字符串)", byteDance.getConsigneeAddress());
            map.put("支付企业的海关注册登记编号", byteDance.getPayCode());
            map.put("支付企业在海关注册登记的企业名称", byteDance.getPayName());
            map.put("支付流水号", byteDance.getPayTransactionId());
            map.put("拉单时间", byteDance.getCreateTime());
            map.put(" isConfirm",  byteDance.getIsConfirm());
            map.put(" province",  byteDance.getProvince());
            map.put(" city",  byteDance.getCity());
            map.put(" town",  byteDance.getTown());
            map.put(" detail",  byteDance.getDetail());
            map.put(" payTime",  byteDance.getPayTime());
            map.put("清关状态回传标记，null：未开始,0开始，1结束,-1失败结尾", byteDance.getCleanStatus());
            map.put("订单状态,-1取消，0未发货,1打包,2已发货", byteDance.getOrderStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
