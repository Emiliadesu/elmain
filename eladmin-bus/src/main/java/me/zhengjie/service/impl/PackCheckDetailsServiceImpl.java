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

import me.zhengjie.domain.PackCheckDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PackCheckDetailsRepository;
import me.zhengjie.service.PackCheckDetailsService;
import me.zhengjie.service.dto.PackCheckDetailsDto;
import me.zhengjie.service.dto.PackCheckDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.PackCheckDetailsMapper;
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
* @author luob
* @date 2021-07-22
**/
@Service
@RequiredArgsConstructor
public class PackCheckDetailsServiceImpl implements PackCheckDetailsService {

    private final PackCheckDetailsRepository packCheckDetailsRepository;
    private final PackCheckDetailsMapper packCheckDetailsMapper;

    @Override
    public Map<String,Object> queryAll(PackCheckDetailsQueryCriteria criteria, Pageable pageable){
        Page<PackCheckDetails> page = packCheckDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(packCheckDetailsMapper::toDto));
    }

    @Override
    public List<PackCheckDetailsDto> queryAll(PackCheckDetailsQueryCriteria criteria){
        return packCheckDetailsMapper.toDto(packCheckDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PackCheckDetailsDto findById(Long id) {
        PackCheckDetails packCheckDetails = packCheckDetailsRepository.findById(id).orElseGet(PackCheckDetails::new);
        ValidationUtil.isNull(packCheckDetails.getId(),"PackCheckDetails","id",id);
        return packCheckDetailsMapper.toDto(packCheckDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PackCheckDetailsDto create(PackCheckDetails resources) {
        return packCheckDetailsMapper.toDto(packCheckDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PackCheckDetails resources) {
        PackCheckDetails packCheckDetails = packCheckDetailsRepository.findById(resources.getId()).orElseGet(PackCheckDetails::new);
        ValidationUtil.isNull( packCheckDetails.getId(),"PackCheckDetails","id",resources.getId());
        packCheckDetails.copy(resources);
        packCheckDetailsRepository.save(packCheckDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            packCheckDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PackCheckDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PackCheckDetailsDto packCheckDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("主单ID", packCheckDetails.getCheckId());
            map.put("商品ID", packCheckDetails.getGoodsId());
            map.put("条码", packCheckDetails.getBarCode());
            map.put("预期数量", packCheckDetails.getExpectQty());
            map.put("当前数量", packCheckDetails.getCurrentQty());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public PackCheckDetails queryByCheckIdAndBarCode(Long checkId, String barCode) {
        return packCheckDetailsRepository.findByCheckIdAndBarCode(checkId, barCode);
    }

    @Override
    public void creates(List<PackCheckDetails> saveDetails) {
        packCheckDetailsRepository.saveAll(saveDetails);
    }

    @Override
    public List<PackCheckDetails> queryByCheckId(Long checkId) {
        return packCheckDetailsRepository.findByCheckId(checkId);
    }
}