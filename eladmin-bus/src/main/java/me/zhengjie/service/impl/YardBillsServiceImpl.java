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

import me.zhengjie.domain.YardBills;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.YardBillsRepository;
import me.zhengjie.service.YardBillsService;
import me.zhengjie.service.dto.YardBillsDto;
import me.zhengjie.service.dto.YardBillsQueryCriteria;
import me.zhengjie.service.mapstruct.YardBillsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
* @date 2021-03-16
**/
@Service
@RequiredArgsConstructor
public class YardBillsServiceImpl implements YardBillsService {

    private final YardBillsRepository YardBillsRepository;
    private final YardBillsMapper YardBillsMapper;

    @Override
    public Map<String,Object> queryAll(YardBillsQueryCriteria criteria, Pageable pageable){
        Page<YardBills> page = YardBillsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(YardBillsMapper::toDto));
    }

    @Override
    public List<YardBillsDto> queryAll(YardBillsQueryCriteria criteria){
        return YardBillsMapper.toDto(YardBillsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public YardBillsDto findById(Long id) {
        YardBills YardBills = YardBillsRepository.findById(id).orElseGet(YardBills::new);
        ValidationUtil.isNull(YardBills.getId(),"YardBills","id",id);
        return YardBillsMapper.toDto(YardBills);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YardBillsDto create(YardBills resources) {
        BigDecimal total=new BigDecimal(resources.getDropBox()).add(new BigDecimal(resources.getDemurrage())).add(new BigDecimal(resources.getRefund())).add(new BigDecimal(resources.getAddCost()));
        resources.setTotal(total.toString());
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return YardBillsMapper.toDto(YardBillsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(YardBills resources) {
        YardBills YardBills = YardBillsRepository.findById(resources.getId()).orElseGet(YardBills::new);
        ValidationUtil.isNull( YardBills.getId(),"YardBills","id",resources.getId());
        YardBills.copy(resources);
        YardBillsRepository.save(YardBills);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            YardBillsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<YardBillsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YardBillsDto YardBills : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("箱号", YardBills.getBoxNum());
            map.put("船名航次", YardBills.getFlight());
            map.put("客户", YardBills.getCustomer());
            map.put("联系电话", YardBills.getTelPhone());
            map.put("进场日期", YardBills.getInDate());
            map.put("出场日期", YardBills.getOutDate());
            map.put("落箱费", YardBills.getDropBox());
            map.put("滞箱费", YardBills.getDemurrage());
            map.put("还箱费", YardBills.getRefund());
            map.put("附加费", YardBills.getAddCost());
            map.put("费用总计", YardBills.getTotal());
            map.put("联系场地", YardBills.getSendAddress());
            map.put("场地联系电话", YardBills.getSendTel());
            map.put("凭据添加时间", YardBills.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
