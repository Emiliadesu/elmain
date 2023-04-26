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
import me.zhengjie.domain.DyOrderPush;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.DouyinService;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DyOrderPushRepository;
import me.zhengjie.service.DyOrderPushService;
import me.zhengjie.service.dto.DyOrderPushDto;
import me.zhengjie.service.dto.DyOrderPushQueryCriteria;
import me.zhengjie.service.mapstruct.DyOrderPushMapper;
import me.zhengjie.utils.constant.MsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
* @date 2021-09-30
**/
@Service
@RequiredArgsConstructor
public class DyOrderPushServiceImpl implements DyOrderPushService {

    private final DyOrderPushRepository dyOrderPushRepository;
    private final DyOrderPushMapper dyOrderPushMapper;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private DouyinService douyinService;

    @Override
    public Map<String,Object> queryAll(DyOrderPushQueryCriteria criteria, Pageable pageable){
        Page<DyOrderPush> page = dyOrderPushRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dyOrderPushMapper::toDto));
    }

    @Override
    public List<DyOrderPushDto> queryAll(DyOrderPushQueryCriteria criteria){
        return dyOrderPushMapper.toDto(dyOrderPushRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DyOrderPushDto findById(Long id) {
        DyOrderPush dyOrderPush = dyOrderPushRepository.findById(id).orElseGet(DyOrderPush::new);
        ValidationUtil.isNull(dyOrderPush.getId(),"DyOrderPush","id",id);
        return dyOrderPushMapper.toDto(dyOrderPush);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DyOrderPushDto create(DyOrderPush resources) {
        return dyOrderPushMapper.toDto(dyOrderPushRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DyOrderPush resources) {
        DyOrderPush dyOrderPush = dyOrderPushRepository.findById(resources.getId()).orElseGet(DyOrderPush::new);
        ValidationUtil.isNull( dyOrderPush.getId(),"DyOrderPush","id",resources.getId());
        dyOrderPush.copy(resources);
        dyOrderPushRepository.save(dyOrderPush);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dyOrderPushRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DyOrderPushDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DyOrderPushDto dyOrderPush : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", dyOrderPush.getOrderNo());
            map.put("抖音店铺id", dyOrderPush.getPlatformShopId());
            map.put("店铺id", dyOrderPush.getShopId());
            map.put("是否成功", dyOrderPush.getIsSuccess());
            map.put("推送时间", dyOrderPush.getCreateTime());
            map.put("最近推送时间", dyOrderPush.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public DyOrderPush queryByOrderNo(String orderNo) {
        DyOrderPush orderPush=new DyOrderPush();
        orderPush.setOrderNo(orderNo);
        List<DyOrderPush>list=dyOrderPushRepository.findAll(Example.of(orderPush));
        return CollectionUtil.isEmpty(list)?null:list.get(list.size()-1);
    }

    @Override
    public void rePullByOrderPush() {
        List<DyOrderPush>list=queryByNonSuccess();
        if (CollectionUtil.isEmpty(list))
            return;
        for (DyOrderPush dyOrderPush : list) {
            String json="{\"orderNo\":\""+dyOrderPush.getOrderNo()+"\",\"shopCode\":\""+dyOrderPush.getPlatformShopId()+"\"}";
            cbOrderProducer.send(MsgType.CB_ORDER_PULL_ORDERNO_DY,json,dyOrderPush.getOrderNo());
        }
    }

    @Override
    public List<DyOrderPush> queryByNonSuccess() {
        DyOrderPush orderPush=new DyOrderPush();
        orderPush.setIsSuccess("0");
        return dyOrderPushRepository.findAll(Example.of(orderPush));
    }
}
