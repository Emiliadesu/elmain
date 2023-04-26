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

import me.zhengjie.domain.DouyinGoodsDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DouyinGoodsDetailsRepository;
import me.zhengjie.service.DouyinGoodsDetailsService;
import me.zhengjie.service.dto.DouyinGoodsDetailsDto;
import me.zhengjie.service.dto.DouyinGoodsDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.DouyinGoodsDetailsMapper;
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
* @author le
* @date 2021-09-28
**/
@Service
@RequiredArgsConstructor
public class DouyinGoodsDetailsServiceImpl implements DouyinGoodsDetailsService {

    private final DouyinGoodsDetailsRepository douyinGoodsDetailsRepository;
    private final DouyinGoodsDetailsMapper douyinGoodsDetailsMapper;

    @Override
    public Map<String,Object> queryAll(DouyinGoodsDetailsQueryCriteria criteria, Pageable pageable){
        Page<DouyinGoodsDetails> page = douyinGoodsDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(douyinGoodsDetailsMapper::toDto));
    }

    @Override
    public List<DouyinGoodsDetailsDto> queryAll(DouyinGoodsDetailsQueryCriteria criteria){
        return douyinGoodsDetailsMapper.toDto(douyinGoodsDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DouyinGoodsDetailsDto findById(Long id) {
        DouyinGoodsDetails douyinGoodsDetails = douyinGoodsDetailsRepository.findById(id).orElseGet(DouyinGoodsDetails::new);
        ValidationUtil.isNull(douyinGoodsDetails.getId(),"DouyinGoodsDetails","id",id);
        return douyinGoodsDetailsMapper.toDto(douyinGoodsDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DouyinGoodsDetailsDto create(DouyinGoodsDetails resources) {
        return douyinGoodsDetailsMapper.toDto(douyinGoodsDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DouyinGoodsDetails resources) {
        DouyinGoodsDetails douyinGoodsDetails = douyinGoodsDetailsRepository.findById(resources.getId()).orElseGet(DouyinGoodsDetails::new);
        ValidationUtil.isNull( douyinGoodsDetails.getId(),"DouyinGoodsDetails","id",resources.getId());
        douyinGoodsDetails.copy(resources);
        douyinGoodsDetailsRepository.save(douyinGoodsDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            douyinGoodsDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DouyinGoodsDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DouyinGoodsDetailsDto douyinGoodsDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("商品id", douyinGoodsDetails.getItemNo());
            map.put(" barCode",  douyinGoodsDetails.getBarCode());
            map.put("净重", douyinGoodsDetails.getItemName());
            map.put(" price",  douyinGoodsDetails.getPrice());
            map.put("数量", douyinGoodsDetails.getQty());
            map.put("货币类型", douyinGoodsDetails.getCurrency());
            map.put("毛重", douyinGoodsDetails.getWeight());
            map.put(" netWeightQty",  douyinGoodsDetails.getNetWeightQty());
            map.put("订单ID", douyinGoodsDetails.getMarkId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<DouyinGoodsDetails> queryByMailMarkId(Long mailMarkId) {
        DouyinGoodsDetails details=new DouyinGoodsDetails();
        details.setMarkId(mailMarkId);
        return douyinGoodsDetailsRepository.findAll(Example.of(details));
    }
}