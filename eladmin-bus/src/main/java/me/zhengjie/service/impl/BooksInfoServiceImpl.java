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

import me.zhengjie.domain.BooksInfo;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.BooksInfoRepository;
import me.zhengjie.service.BooksInfoService;
import me.zhengjie.service.dto.BooksInfoDto;
import me.zhengjie.service.dto.BooksInfoQueryCriteria;
import me.zhengjie.service.mapstruct.BooksInfoMapper;
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
* @author BooksInfo
* @date 2021-07-21
**/
@Service
@RequiredArgsConstructor
public class BooksInfoServiceImpl implements BooksInfoService {

    private final BooksInfoRepository booksInfoRepository;
    private final BooksInfoMapper booksInfoMapper;

    @Override
    public Map<String,Object> queryAll(BooksInfoQueryCriteria criteria, Pageable pageable){
        Page<BooksInfo> page = booksInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(booksInfoMapper::toDto));
    }

    @Override
    public List<BooksInfoDto> queryAll(BooksInfoQueryCriteria criteria){
        return booksInfoMapper.toDto(booksInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }


    @Override
    @Transactional
    public BooksInfoDto findById(Long id) {
        BooksInfo booksInfo = booksInfoRepository.findById(id).orElseGet(BooksInfo::new);
        ValidationUtil.isNull(booksInfo.getId(),"BooksInfo","id",id);
        return booksInfoMapper.toDto(booksInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BooksInfoDto create(BooksInfo resources) {
        return booksInfoMapper.toDto(booksInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BooksInfo resources) {
        BooksInfo booksInfo = booksInfoRepository.findById(resources.getId()).orElseGet(BooksInfo::new);
        ValidationUtil.isNull( booksInfo.getId(),"BooksInfo","id",resources.getId());
        booksInfo.copy(resources);
        booksInfoRepository.save(booksInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            booksInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<BooksInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BooksInfoDto booksInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("账册编号", booksInfo.getBooksNo());
            map.put("说明", booksInfo.getRemark());
            map.put("创建人", booksInfo.getCreateBy());
            map.put("创建时间", booksInfo.getCreateTime());
            map.put("更新人", booksInfo.getUpdateBy());
            map.put("更新时间", booksInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}