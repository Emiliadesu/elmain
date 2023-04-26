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

import me.zhengjie.domain.ClearCompanyInfo;
import me.zhengjie.domain.CustomerInfo;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.service.CustomerInfoService;
import me.zhengjie.service.PlatformService;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.PlatformDto;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ShopInfoRepository;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.service.dto.ShopInfoQueryCriteria;
import me.zhengjie.service.mapstruct.ShopInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author 王淼
* @date 2020-10-20
**/
@CacheConfig(cacheNames = "shop")
@Service
@RequiredArgsConstructor
public class ShopInfoServiceImpl implements ShopInfoService {

    private final ShopInfoRepository shopInfoRepository;
    private final ShopInfoMapper shopInfoMapper;
    private final RedisUtils redisUtils;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private UserCustomerService userCustomerService;

    @Override
    public Map<String,Object> queryAll(ShopInfoQueryCriteria criteria, Pageable pageable){
        Page<ShopInfo> page = shopInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(shopInfoMapper::toDto));
    }

    @Override
    public List<ShopInfoDto> queryAll(ShopInfoQueryCriteria criteria){
        return shopInfoMapper.toDto(shopInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ShopInfoDto findByIdDto(Long id) {
        ShopInfo shopInfo = shopInfoRepository.findById(id).orElseGet(ShopInfo::new);
        ValidationUtil.isNull(shopInfo.getId(),"ShopInfo","id",id);
        return shopInfoMapper.toDto(shopInfo);
    }

    @Override
    @Transactional
    public ShopInfo findById(Long id) {
        ShopInfo shopInfo = shopInfoRepository.findById(id).orElseGet(ShopInfo::new);
        //ValidationUtil  验证工具
        ValidationUtil.isNull(shopInfo.getId(),"ShopInfo","id",id);
        return shopInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopInfoDto create(ShopInfo resources) {
        if(shopInfoRepository.findByCode(resources.getCode()) != null){
            throw new EntityExistException(ShopInfo.class,"code",resources.getCode());
        }
        ShopInfo exit = queryByName(resources.getName());
        if (exit!=null)
            throw new BadRequestException("店铺名字已存在");
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setCreateUserId(SecurityUtils.getCurrentUserId());
        PlatformDto platformDto = platformService.findById(resources.getPlatformId());
        resources.setPlatformCode(platformDto.getPlafCode());
        redisUtils.del("shop::id:" + resources.getId());
        redisUtils.del("shop::code:" + resources.getCode());
        return shopInfoMapper.toDto(shopInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ShopInfo resources) {
        ShopInfo shopInfo = shopInfoRepository.findById(resources.getId()).orElseGet(ShopInfo::new);
        ValidationUtil.isNull( shopInfo.getId(),"ShopInfo","id",resources.getId());
        ShopInfo shopInfo1 = shopInfoRepository.findByCode(resources.getCode());
        if(shopInfo1 != null && !shopInfo1.getId().equals(shopInfo.getId())){
            throw new EntityExistException(ShopInfo.class,"code",resources.getCode());
        }
        shopInfo.copy(resources);
        PlatformDto platformDto = platformService.findById(resources.getPlatformId());
        shopInfo.setPlatformCode(platformDto.getPlafCode());
        shopInfoRepository.save(shopInfo);
        redisUtils.del("shop::id:" + shopInfo.getId());
        redisUtils.del("shop::code:" + shopInfo.getCode());
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            shopInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ShopInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ShopInfoDto shopInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户ID", shopInfo.getCustId());
            map.put("代码，唯一", shopInfo.getCode());
            map.put("名称", shopInfo.getName());
            map.put("电商平台", shopInfo.getPlatformId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void uploadInsert(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list)){
            throw new BadRequestException("导入数据为空");
        }
        List<ShopInfo>combOrderList=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            try {
                fillImportOrder(map, combOrderList);
            }catch (Exception e){
                e.printStackTrace();
                throw new BadRequestException(e.getMessage());
            }
        }
        try {
            insertImportDomOrders(combOrderList);
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public List<ShopInfo> queryByPlafCode(String code) {
        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setPlatformCode(code);
        return shopInfoRepository.findAll(Example.of(shopInfo));
    }

    @Override
    public List<ShopInfo> queryByPlafCodeByWithCusId(String code, List<Long> shopIds) {
        ShopInfoQueryCriteria criteria = new ShopInfoQueryCriteria();
        criteria.setPlatformCode(code);
        criteria.setId(shopIds);
        return shopInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    public List<ShopInfo> queryByCusId(Long cusId) {
        ShopInfoQueryCriteria criteria = new ShopInfoQueryCriteria();
        criteria.setCustId(cusId);
        return shopInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    public List<ShopInfo> queryZhuoZhi() {
        CustomerInfo customerInfo=customerInfoService.queryByName("广东卓志跨境电商供应链服务有限公司");
        ShopInfo shopInfo=new ShopInfo();
        shopInfo.setCustId(customerInfo.getId());
        return shopInfoRepository.findAll(Example.of(shopInfo));
    }

    @Override
    public List<ShopInfo> queryAllZZShopInfo() {
        CustomerInfo cust=customerInfoService.queryByName("广东卓志跨境电商供应链服务有限公司");
        if (cust==null)
            return new ArrayList<>();
        ShopInfo shopInfo=new ShopInfo();
        shopInfo.setCustId(cust.getId());
        return shopInfoRepository.findAll(Example.of(shopInfo));
    }

    @Override
    public List<ShopInfo> queryCurrentUserShop(Long customerId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(currentUserId);
        ShopInfoQueryCriteria criteria = new ShopInfoQueryCriteria();
        criteria.setId(shopIds);
        if (customerId != null) {
            criteria.setCustId(customerId);
        }
        List<ShopInfo> shopInfoList = shopInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
        return shopInfoList;
    }

    @Override
    public List<ShopInfo> queryByPlafCodeByWithCusIdAll(List<Long> shopIds,String platformCode) {
        ShopInfoQueryCriteria criteria = new ShopInfoQueryCriteria();
        criteria.setId(shopIds);
        criteria.setPlatformCode(platformCode);
        return shopInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    public List<ShopInfo> queryByCusIdAll(List<Long> shopIds) {
        ShopInfoQueryCriteria criteria = new ShopInfoQueryCriteria();
        criteria.setId(shopIds);
        return shopInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    public ShopInfo queryByServiceId(Long serviceId) {
        try {
            return shopInfoRepository.findByServiceId(serviceId);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public ShopInfo queryByKjgCode(String kjgCode) {
        return shopInfoRepository.findByKjgCode(kjgCode);
    }

    @Cacheable(key = "'code:' + #p0")
    @Override
    public ShopInfo queryByShopCode(String code) {
        return shopInfoRepository.findByCode(code);
    }

    @Override
    public ShopInfo queryByName(String shopName) {
        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setName(shopName);
        Example<ShopInfo> example = Example.of(shopInfo);
        Optional<ShopInfo> one = shopInfoRepository.findOne(example);
        return one.isPresent() ? one.get() : null;
    }

    @Cacheable(key = "'id:' + #p0")
    @Override
    public ShopInfoDto queryById(Long shopId) {
        return findByIdDto(shopId);
    }

    private void fillImportOrder(Map<String, Object> map, List<ShopInfo> shopInfos) {
        String shopName = (String) map.get("店铺名");
        String shopCode = (String) map.get("店铺code");
        String custId = map.get("客户")+"";
        String serviceType =  map.get("业务类型")+"";
        String serviceHeaderId = (String) map.get("业务抬头");
        String platformId = (String) map.get("电商平台");
        if (StringUtil.isEmpty(shopName)) {
            throw new BadRequestException("店铺名某行为空");
        }
        if (StringUtil.isEmpty(shopCode)) {
            throw new BadRequestException("店铺code某行为空");
        }
        if (StringUtil.isEmpty(custId)) {
            throw new BadRequestException("客户某行为空");
        }
        if (StringUtil.isEmpty(serviceType)) {
            throw new BadRequestException("业务类型某行为空");
        }
        if (StringUtil.isEmpty(platformId)) {
            throw new BadRequestException("电商平台某行为空");
        }
        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setName(StringUtil.removeEscape(shopName));
        shopInfo.setCode(StringUtil.removeEscape(shopCode));
        shopInfo.setCustId(Long.parseLong(StringUtil.removeEscape(custId)));
        shopInfo.setPlatformCode(StringUtil.removeEscape(platformId));
        shopInfo.setServiceType(StringUtil.removeEscape(serviceType));
        if (StringUtil.isNotEmpty(serviceHeaderId)){
            shopInfo.setServiceId(Long.parseLong(StringUtil.removeEscape(serviceHeaderId)));
        }
        shopInfo.setCreateUserId(SecurityUtils.getCurrentUserId());
        shopInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        shopInfos.add(shopInfo);
    }

    public void insertImportDomOrders(List<ShopInfo> shopInfoList) {
        for (int i = 0; i < shopInfoList.size(); i++) {
            ShopInfo shopInfo = shopInfoList.get(i);
            shopInfoRepository.save(shopInfo);
        }

    }
}
