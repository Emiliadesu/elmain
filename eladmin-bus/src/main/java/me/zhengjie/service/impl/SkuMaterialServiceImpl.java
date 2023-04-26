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

import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.PackageInfo;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.domain.SkuMaterial;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.PackageInfoService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.SkuMaterialRepository;
import me.zhengjie.service.SkuMaterialService;
import me.zhengjie.service.dto.SkuMaterialDto;
import me.zhengjie.service.dto.SkuMaterialQueryCriteria;
import me.zhengjie.service.mapstruct.SkuMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
* @author luob
* @date 2022-05-20
**/
@Service
@RequiredArgsConstructor
public class SkuMaterialServiceImpl implements SkuMaterialService {

    private final SkuMaterialRepository skuMaterialRepository;
    private final SkuMaterialMapper skuMaterialMapper;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private PackageInfoService packageInfoService;

    @Override
    public Map<String,Object> queryAll(SkuMaterialQueryCriteria criteria, Pageable pageable){
        Page<SkuMaterial> page = skuMaterialRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(skuMaterialMapper::toDto));
    }

    @Override
    public List<SkuMaterialDto> queryAll(SkuMaterialQueryCriteria criteria){
        return skuMaterialMapper.toDto(skuMaterialRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public SkuMaterialDto findById(Long id) {
        SkuMaterial skuMaterial = skuMaterialRepository.findById(id).orElseGet(SkuMaterial::new);
        ValidationUtil.isNull(skuMaterial.getId(),"SkuMaterial","id",id);
        return skuMaterialMapper.toDto(skuMaterial);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SkuMaterialDto create(SkuMaterial resources) {
        return skuMaterialMapper.toDto(skuMaterialRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SkuMaterial resources) {
        SkuMaterial skuMaterial = skuMaterialRepository.findById(resources.getId()).orElseGet(SkuMaterial::new);
        ValidationUtil.isNull( skuMaterial.getId(),"SkuMaterial","id",resources.getId());
        skuMaterial.copy(resources);
        skuMaterialRepository.save(skuMaterial);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            skuMaterialRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SkuMaterialDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SkuMaterialDto skuMaterial : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户ID", skuMaterial.getCustomersId());
            map.put("店铺ID", skuMaterial.getShopId());
            map.put("商品ID", skuMaterial.getGoodsId());
            map.put("耗材ID", skuMaterial.getMaterialId());
            map.put("货号", skuMaterial.getGoodsNo());
            map.put("商品编码", skuMaterial.getGoodsCode());
            map.put("商品名称", skuMaterial.getGoodsName());
            map.put("商品条码", skuMaterial.getBarCode());
            map.put("耗材编码", skuMaterial.getMaterialCode());
            map.put("耗材名称", skuMaterial.getMaterialName());
            map.put("绑定数量", skuMaterial.getQty());
            map.put("创建人", skuMaterial.getCreateBy());
            map.put("创建时间", skuMaterial.getCreateTime());
            map.put("修改人", skuMaterial.getUpdateBy());
            map.put("修改时间", skuMaterial.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<SkuMaterial> skuMaterials = new ArrayList<>();
        for (Map<String, Object> map : list) {
            int sNo = Integer.valueOf(map.get("序号").toString());
            String shopCode = map.get("店铺CODE") != null ?map.get("店铺CODE").toString() : null;
            String goodsNo = map.get("海关货号") != null ?map.get("海关货号").toString() : null;
            String materialCode = map.get("包材编码") != null ?map.get("包材编码").toString() : null;
            String qty = map.get("数量") != null ?map.get("数量").toString() : null;

            if (StringUtils.isEmpty(shopCode))
                throw new BadRequestException("第" + sNo + "行，店铺CODE不能为空");
            if (StringUtils.isEmpty(goodsNo))
                throw new BadRequestException("第" + sNo + "行，海关货号不能为空");
            if (StringUtils.isEmpty(materialCode))
                throw new BadRequestException("第" + sNo + "行，包材编码不能为空");
            if (StringUtils.isEmpty(qty))
                throw new BadRequestException("第" + sNo + "行，数量不能为空");
            ShopInfo shopInfo = shopInfoService.queryByShopCode(shopCode);
            if (shopInfo == null)
                throw new BadRequestException("第" + sNo + "行，店铺CODE不存在");
            BaseSku baseSku = baseSkuService.queryByGoodsNo(goodsNo);
            if (shopInfo == null)
                throw new BadRequestException("第" + sNo + "行，货号不存在");
            if (shopInfo.getId().intValue() != baseSku.getShopId())
                throw new BadRequestException("第" + sNo + "行，货号不属于此店铺");
            PackageInfo packageInfo = packageInfoService.queryByPackageCode(materialCode);
            if (packageInfo == null || !StringUtils.equals("HC", packageInfo.getPackageType()))
                throw new BadRequestException("第" + sNo + "行，无此耗材");
            // 同个货号只能绑定一个相同的包材编码
            SkuMaterial exist = queryByGoodsNoAndMaterialCode(goodsNo, materialCode);
            if (exist != null)
                throw new BadRequestException("第" + sNo + "行，此货号已绑定相同耗材");
            SkuMaterial skuMaterial = new SkuMaterial();
            skuMaterial.setCustomersId(shopInfo.getCustId());
            skuMaterial.setShopId(shopInfo.getId());
            skuMaterial.setGoodsId(baseSku.getId());
            skuMaterial.setGoodsCode(baseSku.getGoodsCode());
            skuMaterial.setGoodsNo(baseSku.getGoodsNo());
            skuMaterial.setGoodsName(baseSku.getGoodsName());
            skuMaterial.setBarCode(baseSku.getBarCode());
            skuMaterial.setMaterialId(packageInfo.getId());
            skuMaterial.setMaterialCode(packageInfo.getPackageCode());
            skuMaterial.setMaterialName(packageInfo.getPackageName());
            skuMaterial.setQty(Integer.valueOf(qty));
            skuMaterials.add(skuMaterial);
        }
        skuMaterialRepository.saveAll(skuMaterials);
    }

    @Override
    public SkuMaterial queryByGoodsNoAndMaterialCode(String goodsNo, String materialCode) {
        return skuMaterialRepository.findByGoodsNoAndMaterialCode(goodsNo, materialCode);
    }

    @Override
    public List<SkuMaterial> queryByGoodsNo(String goodsNo) {
        return skuMaterialRepository.findByGoodsNo(goodsNo);
    }


}