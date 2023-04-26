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

import me.zhengjie.domain.LogisticsInfo;
import me.zhengjie.domain.LogisticsUnarrivePlace;
import me.zhengjie.domain.PackageInfo;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PackageInfoRepository;
import me.zhengjie.service.PackageInfoService;
import me.zhengjie.service.dto.PackageInfoDto;
import me.zhengjie.service.dto.PackageInfoQueryCriteria;
import me.zhengjie.service.mapstruct.PackageInfoMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author leningzhou
* @date 2022-01-26
**/
@CacheConfig(cacheNames = "packageInfo")
@Service
@RequiredArgsConstructor
public class PackageInfoServiceImpl implements PackageInfoService {

    private final PackageInfoRepository packageInfoRepository;
    private final PackageInfoMapper packageInfoMapper;
    private final RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(PackageInfoQueryCriteria criteria, Pageable pageable){
        Page<PackageInfo> page = packageInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(packageInfoMapper::toDto));
    }

    @Override
    public List<PackageInfoDto> queryAll(PackageInfoQueryCriteria criteria){
        return packageInfoMapper.toDto(packageInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PackageInfoDto findById(Long id) {
        PackageInfo packageInfo = packageInfoRepository.findById(id).orElseGet(PackageInfo::new);
        ValidationUtil.isNull(packageInfo.getId(),"PackageInfo","id",id);
        return packageInfoMapper.toDto(packageInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PackageInfoDto create(PackageInfo resources) {
        return packageInfoMapper.toDto(packageInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PackageInfo resources) {
        PackageInfo packageInfo = packageInfoRepository.findById(resources.getId()).orElseGet(PackageInfo::new);
        ValidationUtil.isNull( packageInfo.getId(),"PackageInfo","id",resources.getId());
        packageInfo.copy(resources);
        redisUtils.del("packageInfo::packageCode:" + packageInfo.getPackageCode());
        packageInfoRepository.save(packageInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            PackageInfo packageInfo = packageInfoRepository.findById(id).orElseGet(PackageInfo::new);
            redisUtils.del("packageInfo::packageCode:" + packageInfo.getPackageCode());
            packageInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PackageInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PackageInfoDto packageInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("包材编码", packageInfo.getPackageCode());
            map.put("包材名称", packageInfo.getPackageName());
            map.put("包材类型", StringUtils.equals("BC", packageInfo.getPackageType())?"包材":"耗材");
            map.put("长", packageInfo.getPackLength());
            map.put("宽", packageInfo.getPackWidth());
            map.put("高", packageInfo.getPackHeight());
            map.put("重量", packageInfo.getWeight());
            map.put("是否增值", StringUtils.equals("1", packageInfo.getAddValue())?"是":"f否");
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<PackageInfo> packageInfos = new ArrayList<>();
        for (Map<String, Object> map : list) {
            int sNo = Integer.valueOf(map.get("序号").toString());
            String packageCode = map.get("包材编码") != null ?map.get("包材编码").toString() : null;
            String packageName = map.get("包材名称") != null ?map.get("包材名称").toString() : null;
            String packageType = map.get("包材类型") != null ?map.get("包材类型").toString() : null;
            String platformCode = map.get("平台") != null ?map.get("平台").toString() : null;
            String packLength = map.get("长") != null ?map.get("长").toString() : null;
            String packWidth = map.get("宽") != null ?map.get("宽").toString() : null;
            String packHeight = map.get("高") != null ?map.get("高").toString() : null;
            String weight = map.get("重量") != null ?map.get("重量").toString() : null;

            if (StringUtils.isEmpty(packageCode))
                throw new BadRequestException("第" + sNo + "行，包材编码不能为空");
            if (StringUtils.isEmpty(packageName))
                throw new BadRequestException("第" + sNo + "行，包材名称不能为空");
            if (StringUtils.isEmpty(platformCode))
                throw new BadRequestException("第" + sNo + "行，平台不能为空");
            if (StringUtils.isEmpty(packageType))
                throw new BadRequestException("第" + sNo + "行，packageType不能为空");
            if (!StringUtils.equals("HC", packageType) && !StringUtils.equals("BC", packageType))
                throw new BadRequestException("第" + sNo + "行，packageType必须为HC或者BC");
            if (StringUtils.isEmpty(packLength) && StringUtils.equals("BC", packageType))
                throw new BadRequestException("第" + sNo + "行，包材长必填");
            if (StringUtils.isEmpty(packWidth) && StringUtils.equals("BC", packageType))
                throw new BadRequestException("第" + sNo + "行，包材宽必填");
            if (StringUtils.isEmpty(packHeight) && StringUtils.equals("BC", packageType))
                throw new BadRequestException("第" + sNo + "行，包材高必填");
            if (StringUtils.isEmpty(weight) && StringUtils.equals("BC", packageType))
                throw new BadRequestException("第" + sNo + "行，包材重量必填");

            PackageInfo exist =  queryByPackageCode(packageCode);
            if (exist != null)
                throw new BadRequestException("第" + sNo + "行，包材编码已存在");

            PackageInfo packageInfo = new PackageInfo();
            packageInfo.setPackageCode(packageCode);
            packageInfo.setPackageName(packageName);
            packageInfo.setPackageType(packageType);
            packageInfo.setPlatformCode(platformCode);
            if (StringUtils.isNotEmpty(packLength))
                packageInfo.setPackLength(new BigDecimal(packLength));
            if (StringUtils.isNotEmpty(packWidth))
                packageInfo.setPackWidth(new BigDecimal(packWidth));
            if (StringUtils.isNotEmpty(packHeight))
                packageInfo.setPackHeight(new BigDecimal(packHeight));
            if (StringUtils.isNotEmpty(weight))
                packageInfo.setWeight(new BigDecimal(weight));
            packageInfos.add(packageInfo);
        }
        packageInfoRepository.saveAll(packageInfos);
    }

    @Cacheable(key = "'packageCode:' + #p0")
    @Override
    public PackageInfo queryByPackageCode(String packageCode) {
        return packageInfoRepository.findByPackageCode(packageCode);
    }

    @Override
    public List<PackageInfo> queryMaterialHc() {
        PackageInfoQueryCriteria criteria = new PackageInfoQueryCriteria();
        criteria.setPackageType("HC");
        List<PackageInfo> all = packageInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return all;
    }
}