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

import me.zhengjie.domain.BusStockConvertDetail;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.BusStockConvertDetailRepository;
import me.zhengjie.service.BusStockConvertDetailService;
import me.zhengjie.service.dto.BusStockConvertDetailDto;
import me.zhengjie.service.dto.BusStockConvertDetailQueryCriteria;
import me.zhengjie.service.mapstruct.BusStockConvertDetailMapper;
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
* @date 2021-04-18
**/
@Service
@RequiredArgsConstructor
public class BusStockConvertDetailServiceImpl implements BusStockConvertDetailService {

    private final BusStockConvertDetailRepository busStockConvertDetailRepository;
    private final BusStockConvertDetailMapper busStockConvertDetailMapper;

    @Override
    public Map<String,Object> queryAll(BusStockConvertDetailQueryCriteria criteria, Pageable pageable){
        Page<BusStockConvertDetail> page = busStockConvertDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(busStockConvertDetailMapper::toDto));
    }

    @Override
    public List<BusStockConvertDetailDto> queryAll(BusStockConvertDetailQueryCriteria criteria){
        return busStockConvertDetailMapper.toDto(busStockConvertDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public BusStockConvertDetailDto findById(Long id) {
        BusStockConvertDetail busStockConvertDetail = busStockConvertDetailRepository.findById(id).orElseGet(BusStockConvertDetail::new);
        ValidationUtil.isNull(busStockConvertDetail.getId(),"BusStockConvertDetail","id",id);
        return busStockConvertDetailMapper.toDto(busStockConvertDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusStockConvertDetailDto create(BusStockConvertDetail resources) {
        return busStockConvertDetailMapper.toDto(busStockConvertDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BusStockConvertDetail resources) {
        BusStockConvertDetail busStockConvertDetail = busStockConvertDetailRepository.findById(resources.getId()).orElseGet(BusStockConvertDetail::new);
        ValidationUtil.isNull( busStockConvertDetail.getId(),"BusStockConvertDetail","id",resources.getId());
        busStockConvertDetail.copy(resources);
        busStockConvertDetailRepository.save(busStockConvertDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            busStockConvertDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<BusStockConvertDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BusStockConvertDetailDto busStockConvertDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("转移主单id", busStockConvertDetail.getMainId());
            map.put("产品id", busStockConvertDetail.getProductId());
            map.put("wms批次号", busStockConvertDetail.getBatchNo());
            map.put("源商家Id(OP系统)", busStockConvertDetail.getFmMerchantId());
            map.put("目标商家Id(OP系统)", busStockConvertDetail.getToMerchantId());
            map.put("转移数量", busStockConvertDetail.getConvertQty());
            map.put("目标数量", busStockConvertDetail.getToQty());
            map.put("目标好坏品类型", busStockConvertDetail.getToIsDamaged());
            map.put("源虚拟货主Id(OP系统)", busStockConvertDetail.getFmVirtualMerchantId());
            map.put("目标虚拟货主Id(OP系统)", busStockConvertDetail.getToVirtualMerchantId());
            map.put("源客户批次号", busStockConvertDetail.getFmCustomerBatchNo());
            map.put("目标客户批次号", busStockConvertDetail.getToCustomerBatchNo());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<BusStockConvertDetail> queryByMainId(Long id) {
        BusStockConvertDetail wrapper=new BusStockConvertDetail();
        wrapper.setMainId(id);
        return busStockConvertDetailRepository.findAll(Example.of(wrapper));
    }
}
