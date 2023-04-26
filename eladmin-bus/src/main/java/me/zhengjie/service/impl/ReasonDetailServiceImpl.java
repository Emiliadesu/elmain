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

import me.zhengjie.domain.ReasonDetail;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ReasonDetailRepository;
import me.zhengjie.service.ReasonDetailService;
import me.zhengjie.service.dto.ReasonDetailDto;
import me.zhengjie.service.dto.ReasonDetailQueryCriteria;
import me.zhengjie.service.mapstruct.ReasonDetailMapper;
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
* @date 2021-03-23
**/
@Service
@RequiredArgsConstructor
public class ReasonDetailServiceImpl implements ReasonDetailService {

    private final ReasonDetailRepository reasonDetailRepository;
    private final ReasonDetailMapper reasonDetailMapper;

    @Override
    public Map<String,Object> queryAll(ReasonDetailQueryCriteria criteria, Pageable pageable){
        Page<ReasonDetail> page = reasonDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(reasonDetailMapper::toDto));
    }

    @Override
    public List<ReasonDetailDto> queryAll(ReasonDetailQueryCriteria criteria){
        return reasonDetailMapper.toDto(reasonDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ReasonDetailDto findById(Long id) {
        ReasonDetail reasonDetail = reasonDetailRepository.findById(id).orElseGet(ReasonDetail::new);
        ValidationUtil.isNull(reasonDetail.getId(),"ReasonDetail","id",id);
        return reasonDetailMapper.toDto(reasonDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReasonDetailDto create(ReasonDetail resources) {
        return reasonDetailMapper.toDto(reasonDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ReasonDetail resources) {
        ReasonDetail reasonDetail = reasonDetailRepository.findById(resources.getId()).orElseGet(ReasonDetail::new);
        ValidationUtil.isNull( reasonDetail.getId(),"ReasonDetail","id",resources.getId());
        reasonDetail.copy(resources);
        reasonDetailRepository.save(reasonDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            reasonDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ReasonDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReasonDetailDto reasonDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("入库理货id", reasonDetail.getStockInTollyItem().getId());
            map.put("差异原因代码 00 QS问题 01 包装问题 02 标签信息不完善 03 产品日期问题 04 合格证问题 05 无卫检 06 效期问题 07 错货 08 质量问题 09 其他问题 100 供应商问题 200 缺货 300 整单未送", reasonDetail.getReasonCode());
            map.put("差异原因描述", reasonDetail.getReasonDescr());
            map.put("差异数量", reasonDetail.getNum());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
