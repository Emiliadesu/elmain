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

import me.zhengjie.domain.CombSplit;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CombSplitRepository;
import me.zhengjie.service.CombSplitService;
import me.zhengjie.service.dto.CombSplitDto;
import me.zhengjie.service.dto.CombSplitQueryCriteria;
import me.zhengjie.service.mapstruct.CombSplitMapper;
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
* @date 2021-06-21
**/
@Service
@RequiredArgsConstructor
public class CombSplitServiceImpl implements CombSplitService {

    private final CombSplitRepository combSplitRepository;
    private final CombSplitMapper combSplitMapper;

    @Override
    public Map<String,Object> queryAll(CombSplitQueryCriteria criteria, Pageable pageable){
        Page<CombSplit> page = combSplitRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(combSplitMapper::toDto));
    }

    @Override
    public List<CombSplitDto> queryAll(CombSplitQueryCriteria criteria){
        return combSplitMapper.toDto(combSplitRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CombSplitDto findById(Long id) {
        CombSplit combSplit = combSplitRepository.findById(id).orElseGet(CombSplit::new);
        ValidationUtil.isNull(combSplit.getId(),"CombSplit","id",id);
        return combSplitMapper.toDto(combSplit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CombSplitDto create(CombSplit resources) {
        return combSplitMapper.toDto(combSplitRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CombSplit resources) {
        CombSplit combSplit = combSplitRepository.findById(resources.getId()).orElseGet(CombSplit::new);
        ValidationUtil.isNull( combSplit.getId(),"CombSplit","id",resources.getId());
        combSplit.copy(resources);
        combSplitRepository.save(combSplit);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            combSplitRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CombSplitDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CombSplitDto combSplit : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("组合包的id", combSplit.getCombId());
            map.put("组合包sku", combSplit.getCombSkuId());
            map.put("货号", combSplit.getSplitSkuId());
            map.put("品名", combSplit.getSplitSkuName());
            map.put("数量", combSplit.getQty());
            map.put("价格", combSplit.getPayment());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<CombSplit> queryByCombId(Long combId) {
        return combSplitRepository.findByCombId(combId);
    }
}
