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

import me.zhengjie.domain.BooksGoodsInfo;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.BooksGoodsInfoRepository;
import me.zhengjie.service.BooksGoodsInfoService;
import me.zhengjie.service.dto.BooksGoodsInfoDto;
import me.zhengjie.service.dto.BooksGoodsInfoQueryCriteria;
import me.zhengjie.service.mapstruct.BooksGoodsInfoMapper;
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
* @author 王淼
* @date 2020-10-13
**/
@Service
@RequiredArgsConstructor
public class BooksGoodsInfoServiceImpl implements BooksGoodsInfoService {

    private final BooksGoodsInfoRepository booksGoodsInfoRepository;
    private final BooksGoodsInfoMapper booksGoodsInfoMapper;

    @Override
    public Map<String,Object> queryAll(BooksGoodsInfoQueryCriteria criteria, Pageable pageable){
        Page<BooksGoodsInfo> page = booksGoodsInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(booksGoodsInfoMapper::toDto));
    }

    @Override
    public List<BooksGoodsInfoDto> queryAll(BooksGoodsInfoQueryCriteria criteria){
        return booksGoodsInfoMapper.toDto(booksGoodsInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public BooksGoodsInfoDto findById(Long id) {
        BooksGoodsInfo booksGoodsInfo = booksGoodsInfoRepository.findById(id).orElseGet(BooksGoodsInfo::new);
        ValidationUtil.isNull(booksGoodsInfo.getId(),"BooksGoodsInfo","id",id);
        return booksGoodsInfoMapper.toDto(booksGoodsInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BooksGoodsInfoDto create(BooksGoodsInfo resources) {
        return booksGoodsInfoMapper.toDto(booksGoodsInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BooksGoodsInfo resources) {
        BooksGoodsInfo booksGoodsInfo = booksGoodsInfoRepository.findById(resources.getId()).orElseGet(BooksGoodsInfo::new);
        ValidationUtil.isNull( booksGoodsInfo.getId(),"BooksGoodsInfo","id",resources.getId());
        booksGoodsInfo.copy(resources);
        booksGoodsInfoRepository.save(booksGoodsInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            booksGoodsInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<BooksGoodsInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BooksGoodsInfoDto booksGoodsInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("账册编号", booksGoodsInfo.getBooksNo());
            map.put("货号", booksGoodsInfo.getGoodsNo());
            map.put("备案序号", booksGoodsInfo.getSeqNo());
            map.put("料号", booksGoodsInfo.getPartNo());
            map.put("HS编码", booksGoodsInfo.getHsCode());
            map.put("货品名称", booksGoodsInfo.getGoodsName());
            map.put("商品规格型号", booksGoodsInfo.getSpec());
            map.put("申报计量单位", booksGoodsInfo.getDeclareUnit());
            map.put("法定计量单位", booksGoodsInfo.getMeaUnitOne());
            map.put("法定第二计量单位", booksGoodsInfo.getMeaUnitTwo());
            map.put("法定数量", booksGoodsInfo.getMeaNumOne());
            map.put("法定第二数量", booksGoodsInfo.getMeaNumTwo());
            map.put("币制", booksGoodsInfo.getCurrencySystem());
            map.put("征免方式", booksGoodsInfo.getTaxWay());
            map.put("最终目的国", booksGoodsInfo.getDestinationCountry());
            map.put("原产国", booksGoodsInfo.getMakeCountry());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}