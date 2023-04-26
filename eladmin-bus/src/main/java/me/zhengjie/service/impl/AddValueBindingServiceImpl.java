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

import com.alibaba.fastjson.JSONObject;
import me.zhengjie.domain.AddValueBinding;
import me.zhengjie.domain.AddValueOrder;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.AddValueBindingRepository;
import me.zhengjie.service.AddValueBindingService;
import me.zhengjie.service.dto.AddValueBindingDto;
import me.zhengjie.service.dto.AddValueBindingQueryCriteria;
import me.zhengjie.service.mapstruct.AddValueBindingMapper;
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
* @author AddValueBinding
* @date 2021-08-05
**/
@Service
@RequiredArgsConstructor
public class AddValueBindingServiceImpl implements AddValueBindingService {

    private final AddValueBindingRepository addValueBindingRepository;
    private final AddValueBindingMapper addValueBindingMapper;

    @Autowired
    private ShopInfoService shopInfoService;

    @Override
    public Map<String,Object> queryAll(AddValueBindingQueryCriteria criteria, Pageable pageable){
        Page<AddValueBinding> page = addValueBindingRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(addValueBindingMapper::toDto));
    }

    @Override
    public List<AddValueBindingDto> queryAll(AddValueBindingQueryCriteria criteria){
        return addValueBindingMapper.toDto(addValueBindingRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public AddValueBindingDto findById(Long id) {
        AddValueBinding addValueBinding = addValueBindingRepository.findById(id).orElseGet(AddValueBinding::new);
        ValidationUtil.isNull(addValueBinding.getId(),"AddValueBinding","id",id);
        return addValueBindingMapper.toDto(addValueBinding);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddValueBindingDto create(AddValueBinding resources) {
        return addValueBindingMapper.toDto(addValueBindingRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AddValueBinding resources) {
        AddValueBinding addValueBinding = addValueBindingRepository.findById(resources.getId()).orElseGet(AddValueBinding::new);
        ValidationUtil.isNull( addValueBinding.getId(),"AddValueBinding","id",resources.getId());
        addValueBinding.copy(resources);
        addValueBindingRepository.save(addValueBinding);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            addValueBindingRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<AddValueBindingDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AddValueBindingDto addValueBinding : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户", addValueBinding.getCustomerId());
            map.put("店铺", addValueBinding.getShopId());
            map.put("增项值ID", addValueBinding.getValueId());
            map.put("增值项编码", addValueBinding.getValueCode());
            map.put("价格", addValueBinding.getPrice());
            map.put("创建人", addValueBinding.getCreateBy());
            map.put("创建时间", addValueBinding.getCreateTime());
            map.put("更新人", addValueBinding.getUpdateBy());
            map.put("更新时间", addValueBinding.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /*
    增值接口实现
     */

    @Override
    public AddValueBinding addValue(String decData, Long customerId) {
        JSONObject data = JSONObject.parseObject(decData);
        AddValueBinding addValueBinding=new AddValueBinding();
        String shopId = data.getString("merchantId");
        if (StringUtils.isEmpty(shopId))
            throw new BadRequestException("merchantId必须有");
        ShopInfo shopInfo = shopInfoService.queryByShopCode(shopId);
        if (shopInfo == null)
            throw new BadRequestException("shopId不正确");
//        Long custId = data.getLong("customerId");
        addValueBinding.setShopId(shopInfo.getId());
//        Long custId = data.getLong("customerId");
        addValueBinding.setCustomerId(customerId);
//        String shopId = data.getString("shopId");
//        if (StringUtils.isEmpty(shopId))
//            throw new BadRequestException("shopId必须有");
//        addValueBinding.setShopId(shopId);
        String valueId = data.getString("valueId");
        if (StringUtils.isEmpty(valueId))
            throw new BadRequestException("valueId必须有");
        addValueBinding.setValueId(valueId);
        String valueCode = data.getString("addCode");
        if (StringUtils.isEmpty(valueCode))
            throw new BadRequestException("addCode必须有");
        addValueBinding.setValueCode(valueCode);
        BigDecimal price = data.getBigDecimal("price");
        addValueBinding.setPrice(price);
        create(addValueBinding);
        return addValueBinding;
    }
}