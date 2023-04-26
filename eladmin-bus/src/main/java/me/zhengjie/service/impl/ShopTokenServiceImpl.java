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
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.Platform;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.MoGuJieService;
import me.zhengjie.service.PlatformService;
import me.zhengjie.service.dto.PlatformDto;
import me.zhengjie.support.EmptyResponse;
import me.zhengjie.support.douyin.CommonTokenRefreshRequest;
import me.zhengjie.support.douyin.CommonTokenResponse;
import me.zhengjie.support.douyin.DYCommonResponse;
import me.zhengjie.support.douyin.DYSupport;
import me.zhengjie.support.meituan.MeiTuanSupport;
import me.zhengjie.support.pdd.PDDSupport;
import me.zhengjie.support.pdd.PddAddShopTokenRequest;
import me.zhengjie.support.pdd.PddCommonResponse;
import me.zhengjie.support.youzan.YouZanSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ShopTokenRepository;
import me.zhengjie.service.ShopTokenService;
import me.zhengjie.service.dto.ShopTokenDto;
import me.zhengjie.service.dto.ShopTokenQueryCriteria;
import me.zhengjie.service.mapstruct.ShopTokenMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
@CacheConfig(cacheNames = "shopToken")
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopTokenServiceImpl implements ShopTokenService {

    private final ShopTokenRepository shopTokenRepository;
    private final ShopTokenMapper shopTokenMapper;
    private final RedisUtils redisUtils;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private YouZanSupport youZanSupport;

    @Autowired
    private PDDSupport pddSupport;

    @Autowired
    private DYSupport dySupport;

    @Autowired
    private MoGuJieService moGuJieService;

    @Autowired
    private MeiTuanSupport meiTuanSupport;

    @Override
    public Map<String,Object> queryAll(ShopTokenQueryCriteria criteria, Pageable pageable){
        Page<ShopToken> page = shopTokenRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(shopTokenMapper::toDto));
    }

    @Override
    public List<ShopTokenDto> queryAll(ShopTokenQueryCriteria criteria){
        return shopTokenMapper.toDto(shopTokenRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ShopTokenDto findById(Long id) {
        ShopToken shopToken = shopTokenRepository.findById(id).orElseGet(ShopToken::new);
        ValidationUtil.isNull(shopToken.getId(),"ShopToken","id",id);
        return shopTokenMapper.toDto(shopToken);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopTokenDto create(ShopToken resources) {
        PlatformDto platformDto=null;
        if (StringUtil.isNotBlank(resources.getPlatform())){
            platformDto=platformService.findById(Long.parseLong(resources.getPlatform()));
        }
        if (platformDto!=null&&StringUtil.isBlank(resources.getClientId())){
            resources.setClientId(platformDto.getAppId());
            resources.setClientSecret(platformDto.getAppSecret());
        }
        ShopToken shopToken=shopTokenRepository.save(resources);
        if (platformDto!=null&&StringUtil.equals(platformDto.getPlafCode(),"PDD")){
            //拼多多的授权数据
            pddSupport.addShopToken(shopToken);
        }
        return shopTokenMapper.toDto(shopToken);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ShopToken resources) {
        ShopToken shopToken = shopTokenRepository.findById(resources.getId()).orElseGet(ShopToken::new);
        ValidationUtil.isNull( shopToken.getId(),"ShopToken","id",resources.getId());
        shopToken.copy(resources);
        shopTokenRepository.save(shopToken);
        redisUtils.del("shopToken::platShopId:" + shopToken.getPlatformShopId());
        redisUtils.del("shopToken::shopId:" + shopToken.getShopId());
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            shopTokenRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ShopTokenDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ShopTokenDto shopToken : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("店铺名", shopToken.getShopName());
            map.put("店铺id(拼多多则是user_id)", shopToken.getShopId());
            map.put("应用id", shopToken.getClientId());
            map.put("应用secret", shopToken.getClientSecret());
            map.put("授权码", shopToken.getCode());
            map.put("令牌", shopToken.getAccessToken());
            map.put("刷新令牌", shopToken.getRefreshToken());
            map.put("授权码获取时间", shopToken.getCodeGetTime());
            map.put("令牌刷新时间", shopToken.getRefreshTime());
            map.put("token有效期", shopToken.getTokenTime());
            map.put("电商平台代码", shopToken.getPlatform());
            map.put(" pubKey",  shopToken.getPubKey());
            map.put(" priKey",  shopToken.getPriKey());
            map.put("是否允许拉单操作", shopToken.getPullOrderAble());
            map.put("是否推送至菜鸟", shopToken.getIsPushToCn());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Cacheable(key = "'shopId:' + #p0")
    @Override
    public ShopToken queryByShopId(Long shopId) {
        ShopTokenQueryCriteria criteria = new ShopTokenQueryCriteria();
        criteria.setShopId(shopId);
        List<ShopToken> list = shopTokenRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
        if (CollectionUtils.isNotEmpty(list))
            return list.get(0);
        else
            return null;
    }

    @Cacheable(key = "'platShopId:' + #p0")
    @Override
    public ShopToken queryByPaltShopId(String platShopId) {
        return shopTokenRepository.findByPlatformShopId(platShopId);
    }

    @Override
    public List<ShopToken> queryEnableTokenByPlatId(Long platId) {
        ShopToken shopToken=new ShopToken();
        shopToken.setPlatform(platId+"");
        shopToken.setPullOrderAble("1");
        return shopTokenRepository.findAll(Example.of(shopToken));
    }

    @Override
    public void testTokenOverdue(ShopToken shopToken) throws Exception {
        if (shopToken==null||StringUtil.isEmpty(shopToken.getAccessToken()))
            throw new BadRequestException("授权令牌为空");
        if (shopToken.getTokenTime()<(System.currentTimeMillis()/1000-3600)){
            //token在1小时候过期，应该去换取新的token
            switch (shopToken.getPlatform()){
                case "23":{
                    //有赞
                    youZanSupport.refreshToken(shopToken);
                    update(shopToken);
                    break;
                }
                case "19":{
                    //蘑菇街
                    moGuJieService.refreshToken(shopToken);
                    update(shopToken);
                    break;
                }
                case "16":{
                    //抖音
                    CommonTokenRefreshRequest request = new CommonTokenRefreshRequest();
                    request.setRefreshToken(shopToken.getRefreshToken());
                    dySupport.setApiParam(request);
                    DYCommonResponse<CommonTokenResponse> response = dySupport.request(CommonTokenResponse.class);
                    log.info("刷新抖音token:{}", response);
                    if (StringUtil.equals(response.getStatusCode(), "0")) {
                        CommonTokenResponse data = response.getData();
                        shopToken.setAccessToken(data.getAccessToken());
                        shopToken.setRefreshToken(data.getRefreshToken());
                        shopToken.setTokenTime(data.getExpireIn() + System.currentTimeMillis() / 1000);
                        Date time = new Date(System.currentTimeMillis() + 14L * 24L * 3600L * 1000L);
                        shopToken.setRefreshTime(new Timestamp(time.getTime()));
                        update(shopToken);
                    }else {
                        throw new BadRequestException("刷新token失败" + response.getMessage());
                    }
                }
                case "26":{
                    //美团
                    meiTuanSupport.refreshAccessToken(shopToken);
                    update(shopToken);
                }
            }
        }
    }

    @Override
    public ShopToken queryById(Long id) {
        return shopTokenRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String,Object> copyLink(Long id) {
        ShopToken shopToken=queryById(id);
        if (shopToken==null)
            throw new BadRequestException(id+":没有授权对象");
        if (StringUtil.isEmpty(shopToken.getPlatform()))
            throw new BadRequestException(id+":没有授权平台");
        Platform platform=platformService.queryById(Long.parseLong(shopToken.getPlatform()));
        if (platform==null)
            throw new BadRequestException("找不到授权平台");
        Map<String,Object> map=new HashMap<>();
        String link=null;
        switch (platform.getPlafCode()){
            case "PDD":
                link="https://mms.pinduoduo.com/open.html?response_type=code&client_id=" + shopToken.getClientId() + "&redirect_uri=http://erp.fl56.net:8000/api/pdd/callback&state=" + shopToken.getId();
                break;
            case "BD":
                link="http://api.open.beibei.com/outer/oauth/app.html?app_id=" + shopToken.getClientId() + "&params=" + shopToken.getId();
                break;
            case "MGJ":
                link="https://oauth.mogujie.com/authorize?response_type=code&app_key=" + shopToken.getClientId() + "&redirect_uri=http://pre.fl56.net/api/mgj/callback&state=" + shopToken.getId();
                break;
        }
        map.put("link",link);
        return map;
    }

    @Override
    public void toToken(Long id) {
        ShopToken shopToken=shopTokenRepository.findById(id).orElse(null);
        if (shopToken==null)
            throw new BadRequestException("无授权店铺");
        Platform platform=platformService.queryById(Long.valueOf(shopToken.getPlatform()));
        if (StringUtil.equals(platform.getPlafCode(),"MeiTuan")){
            meiTuanSupport.getCode(shopToken);
            meiTuanSupport.getAccessToken(shopToken);
            update(shopToken);
        }
    }


}
