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

import me.zhengjie.domain.DyCangzuFee;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.DouyinService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DyCangzuFeeRepository;
import me.zhengjie.service.DyCangzuFeeService;
import me.zhengjie.service.dto.DyCangzuFeeDto;
import me.zhengjie.service.dto.DyCangzuFeeQueryCriteria;
import me.zhengjie.service.mapstruct.DyCangzuFeeMapper;
import me.zhengjie.utils.constant.MsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
* @date 2023-02-07
**/
@Service
@RequiredArgsConstructor
public class DyCangzuFeeServiceImpl implements DyCangzuFeeService {

    private final DyCangzuFeeRepository dyCangzuFeeRepository;
    private final DyCangzuFeeMapper dyCangzuFeeMapper;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private CBOrderProducer producer;

    @Override
    public Map<String,Object> queryAll(DyCangzuFeeQueryCriteria criteria, Pageable pageable){
        Page<DyCangzuFee> page = dyCangzuFeeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dyCangzuFeeMapper::toDto));
    }

    @Override
    public List<DyCangzuFeeDto> queryAll(DyCangzuFeeQueryCriteria criteria){
        return dyCangzuFeeMapper.toDto(dyCangzuFeeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DyCangzuFeeDto findById(Long id) {
        DyCangzuFee dyCangzuFee = dyCangzuFeeRepository.findById(id).orElseGet(DyCangzuFee::new);
        ValidationUtil.isNull(dyCangzuFee.getId(),"DyCangzuFee","id",id);
        return dyCangzuFeeMapper.toDto(dyCangzuFee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DyCangzuFeeDto create(DyCangzuFee resources) {
        return dyCangzuFeeMapper.toDto(dyCangzuFeeRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DyCangzuFee resources) {
        DyCangzuFee dyCangzuFee = dyCangzuFeeRepository.findById(resources.getId()).orElseGet(DyCangzuFee::new);
        ValidationUtil.isNull( dyCangzuFee.getId(),"DyCangzuFee","id",resources.getId());
        dyCangzuFee.copy(resources);
        dyCangzuFeeRepository.save(dyCangzuFee);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dyCangzuFeeRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DyCangzuFeeDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DyCangzuFeeDto dyCangzuFee : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("仓租单号", dyCangzuFee.getWsStoreNo());
            map.put("仓库编码", dyCangzuFee.getWarehouseCode());
            map.put("计费日期", dyCangzuFee.getFeeDate());
            map.put("店铺id", dyCangzuFee.getShopId());
            map.put("货主id", dyCangzuFee.getOwnerId());
            map.put("货主类型", dyCangzuFee.getOwnerType());
            map.put("创建时间", dyCangzuFee.getCreateTime());
            map.put("是否推送", dyCangzuFee.getIsPush());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void pushFeeOrder(Long id) {
        DyCangzuFee dyCangzuFee = dyCangzuFeeMapper.toEntity(findById(id));
        douyinService.createWarehouseFeeOrderPush(dyCangzuFee);
        dyCangzuFee.setIsPush("1");
        update(dyCangzuFee);
    }

    @Override
    public void pushFeeOrderByIds(Long[] ids) {
        /*StringBuilder builder = new StringBuilder();
        for (Long id : ids) {
            try {
                pushFeeOrder(id);
            }catch (Exception e){
                e.printStackTrace();
                builder.append(e.getMessage()).append("\n");
            }
        }
        if (StringUtil.isNotBlank(builder)) {
            System.out.println(builder);
            throw new BadRequestException(builder.toString());
        }*/
        for (Long id : ids) {
            producer.send(MsgType.DY_PUSH_CANGZU_FEE,id+"",id+"");
        }
    }

    @Override
    public DyCangzuFee queryByWsStoreNo(String wsStoreNo) {
        return dyCangzuFeeRepository.findByWsStoreNo(wsStoreNo);
    }


}