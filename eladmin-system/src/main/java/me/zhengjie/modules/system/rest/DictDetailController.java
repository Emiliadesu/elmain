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
package me.zhengjie.modules.system.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.domain.Dict;
import me.zhengjie.modules.system.domain.DictDetail;
import me.zhengjie.modules.system.service.DictDetailService;
import me.zhengjie.modules.system.service.DictService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.*;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.*;
import me.zhengjie.utils.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@RestController
@RequiredArgsConstructor
@Api(tags = "系统：字典详情管理")
@RequestMapping("/api/dictDetail")
public class DictDetailController {

    private final DictDetailService dictDetailService;
    private final PlatformService platformService;
    private final ClearCompanyInfoService clearCompanyInfoService;
    private final CustomerInfoService customerInfoService;
    private final ShopInfoService shopInfoService;
    private final UserService userService;
    private final DictService dictService;
    private final LogisticsUnarrivePlaceService logisticsUnarrivePlaceService;
    private final LogisticsInfoService logisticsInfoService;
    private static final String ENTITY_NAME = "dictDetail";

    @Log("查询字典详情")
    @ApiOperation("查询字典详情")
    @GetMapping
    public ResponseEntity<Object> query(DictDetailQueryCriteria criteria,
                                         @PageableDefault(sort = {"dictSort"}, direction = Sort.Direction.ASC) Pageable pageable){
        if (StringUtils.equals("platformCodes",criteria.getDictName())){
            //平台
            Map<String, Object> map = getDictByPlatformCodesData(pageable);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
        if (StringUtils.equals("platforms",criteria.getDictName())){
            //平台
            Map<String, Object> map = getDictByPlatformData(pageable);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }if (StringUtils.equals("clearComp",criteria.getDictName())){
            //清关抬头
            Map<String, Object> map = getDictByOwnerData(pageable);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }if (StringUtils.equals("customer",criteria.getDictName())){
            //客户
            Map<String, Object> map = getDictByCustData(pageable);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }if (StringUtils.equals("shop",criteria.getDictName())){
            //店铺
            Map<String, Object> map = getDictByshopData(pageable);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }if (StringUtils.equals("dy_shop",criteria.getDictName())){
            //店铺
            Map<String, Object> map = getDictByDyshopData(pageable);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }if (StringUtils.equals("user",criteria.getDictName())){
            //用户
            Map<String, Object> map = getDictByUserData(pageable);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }if (StringUtils.equals("logisticsMsg",criteria.getDictName())) {
            //物流快递
            Map<String, Object> map = getDictBylogisticsMsgData(pageable);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        return new ResponseEntity<>(dictDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    private Map<String, Object> getDictBylogisticsMsgData(Pageable pageable) {
        Dict dict=dictService.queryByName("logisticsMsg");
        Pageable logisticsMsgPageable=new PageRequest(pageable.getPageNumber(),pageable.getPageSize());
        Map<String,Object>map= logisticsInfoService.queryAll(new LogisticsInfoQueryCriteria(),logisticsMsgPageable);
        List objList= (List) map.get("content");
        List<DictDetailDto>dictDetails=new ArrayList<>();
        int i=0;
        for (Object o : objList) {
            i++;
            if (o instanceof LogisticsInfoDto){
                LogisticsInfoDto logisticsInfo = (LogisticsInfoDto) o;
                DictDetailDto dictDetail=new DictDetailDto();
                dictDetail.setId(logisticsInfo.getId());
                dictDetail.setDictSort(i);
                DictSmallDto dictSmallDto=new DictSmallDto();
                dictSmallDto.setId(dict.getId());
                dictDetail.setLabel(logisticsInfo.getName());
                dictDetail.setValue(logisticsInfo.getId()+"");
                dictDetails.add(dictDetail);
            }
        }
        map.put("content",dictDetails);
        return map;
    }

    private Map<String, Object> getDictByPlatformCodesData(@PageableDefault(sort = {"dictSort"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Dict dict=dictService.queryByName("platformCodes");
        Pageable platPageable=new PageRequest(pageable.getPageNumber(),pageable.getPageSize());
        Map<String,Object>map=platformService.queryAll(new PlatformQueryCriteria(),platPageable);
        List objList= (List) map.get("content");
        List<DictDetailDto>dictDetails=new ArrayList<>();
        int i=0;
        for (Object o : objList) {
            i++;
            if (o instanceof PlatformDto){
                PlatformDto platform= (PlatformDto) o;
                DictDetailDto dictDetail=new DictDetailDto();
                dictDetail.setId(platform.getId());
                dictDetail.setDictSort(i);
                DictSmallDto dictSmallDto=new DictSmallDto();
                dictSmallDto.setId(dict.getId());
                dictDetail.setLabel(platform.getPlafName());
                dictDetail.setValue(platform.getPlafCode()+"");
                dictDetails.add(dictDetail);
            }
        }
        map.put("content",dictDetails);
        return map;
    }

    private Map<String,Object> getDictByUserData(Pageable pageable) {
        Dict dict=dictService.queryByName("user");
        Pageable userPageable=new PageRequest(pageable.getPageNumber(),pageable.getPageSize());
        Map<String,Object>map= (Map<String, Object>) userService.queryAll(new UserQueryCriteria(),userPageable);
        List objList= (List) map.get("content");
        List<DictDetailDto>dictDetails=new ArrayList<>();
        int i=0;
        for (Object o : objList) {
            i++;
            if (o instanceof UserDto){
                UserDto userDto= (UserDto) o;
                DictDetailDto dictDetail=new DictDetailDto();
                dictDetail.setId(userDto.getId());
                dictDetail.setDictSort(i);
                DictSmallDto dictSmallDto=new DictSmallDto();
                dictSmallDto.setId(dict.getId());
                dictDetail.setLabel(userDto.getNickName());
                dictDetail.setValue(userDto.getId()+"");
                dictDetails.add(dictDetail);
            }
        }
        map.put("content",dictDetails);
        return map;
    }

    private Map<String,Object> getDictByshopData(Pageable pageable) {
        Dict dict=dictService.queryByName("shop");
        Pageable shopPageable=new PageRequest(pageable.getPageNumber(),pageable.getPageSize());
        Map<String,Object>map=shopInfoService.queryAll(new ShopInfoQueryCriteria(),shopPageable);
        List objList= (List) map.get("content");
        List<DictDetailDto>dictDetails=new ArrayList<>();
        int i=0;
        for (Object o : objList) {
            i++;
            if (o instanceof ShopInfoDto){
                ShopInfoDto shopInfoDto= (ShopInfoDto) o;
                DictDetailDto dictDetail=new DictDetailDto();
                dictDetail.setId(shopInfoDto.getId());
                dictDetail.setDictSort(i);
                DictSmallDto dictSmallDto=new DictSmallDto();
                dictSmallDto.setId(dict.getId());
                dictDetail.setLabel(shopInfoDto.getName());
                dictDetail.setValue(shopInfoDto.getId()+"");
                dictDetails.add(dictDetail);
            }
        }
        map.put("content",dictDetails);
        return map;
    }

    private Map<String,Object> getDictByDyshopData(Pageable pageable) {
        Dict dict=dictService.queryByName("dy_shop");
        Pageable shopPageable=new PageRequest(pageable.getPageNumber(),pageable.getPageSize());
        ShopInfoQueryCriteria criteria = new ShopInfoQueryCriteria();
        criteria.setPlatformCode("DY");
        Map<String,Object>map=shopInfoService.queryAll(criteria,shopPageable);
        List objList= (List) map.get("content");
        List<DictDetailDto>dictDetails=new ArrayList<>();
        int i=0;
        for (Object o : objList) {
            i++;
            if (o instanceof ShopInfoDto){
                ShopInfoDto shopInfoDto= (ShopInfoDto) o;
                DictDetailDto dictDetail=new DictDetailDto();
                dictDetail.setId(shopInfoDto.getId());
                dictDetail.setDictSort(i);
                DictSmallDto dictSmallDto=new DictSmallDto();
                dictSmallDto.setId(dict.getId());
                dictDetail.setLabel(shopInfoDto.getName());
                dictDetail.setValue(shopInfoDto.getId()+"");
                dictDetails.add(dictDetail);
            }
        }
        map.put("content",dictDetails);
        return map;
    }

    private Map<String, Object> getDictByCustData(Pageable pageable) {
        Dict dict=dictService.queryByName("customer");
        Pageable customerPageable=new PageRequest(pageable.getPageNumber(),pageable.getPageSize());
        Map<String,Object>map=customerInfoService.queryAll(new CustomerInfoQueryCriteria(),customerPageable);
        List objList= (List) map.get("content");
        List<DictDetailDto>dictDetails=new ArrayList<>();
        int i=0;
        for (Object o : objList) {
            i++;
            if (o instanceof CustomerInfoDto){
                CustomerInfoDto customerInfoDto= (CustomerInfoDto) o;
                DictDetailDto dictDetail=new DictDetailDto();
                dictDetail.setId(customerInfoDto.getId());
                dictDetail.setDictSort(i);
                DictSmallDto dictSmallDto=new DictSmallDto();
                dictSmallDto.setId(dict.getId());
                dictDetail.setDict(dictSmallDto);
                dictDetail.setLabel(customerInfoDto.getCustNickName());
                dictDetail.setValue(customerInfoDto.getId().toString());
                dictDetails.add(dictDetail);
            }
        }
        map.put("content",dictDetails);
        return map;
    }

    private Map<String, Object> getDictByOwnerData(Pageable pageable) {
        Dict dict=dictService.queryByName("clearComp");
        Pageable clearCompPageable=new PageRequest(pageable.getPageNumber(),pageable.getPageSize());
        Map<String,Object>map=clearCompanyInfoService.queryAll(new ClearCompanyInfoQueryCriteria(),clearCompPageable);
        List objList= (List) map.get("content");
        List<DictDetailDto>dictDetails=new ArrayList<>();
        int i=0;
        for (Object o : objList) {
            i++;
            if (o instanceof ClearCompanyInfoDto){
                ClearCompanyInfoDto clearCompanyInfo= (ClearCompanyInfoDto) o;
                DictDetailDto dictDetail=new DictDetailDto();
                dictDetail.setId(clearCompanyInfo.getClearCompanyId());
                dictDetail.setDictSort(i);
                DictSmallDto dictSmallDto=new DictSmallDto();
                dictSmallDto.setId(dict.getId());
                dictDetail.setLabel(clearCompanyInfo.getClearCompanyName());
                dictDetail.setValue(clearCompanyInfo.getClearCompanyId()+"");
                dictDetails.add(dictDetail);
            }
        }
        map.put("content",dictDetails);
        return map;
    }

    private Map<String, Object> getDictByPlatformData(@PageableDefault(sort = {"dictSort"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Dict dict=dictService.queryByName("platforms");
        Pageable platPageable=new PageRequest(pageable.getPageNumber(),pageable.getPageSize());
        Map<String,Object>map=platformService.queryAll(new PlatformQueryCriteria(),platPageable);
        List objList= (List) map.get("content");
        List<DictDetailDto>dictDetails=new ArrayList<>();
        int i=0;
        for (Object o : objList) {
            i++;
            if (o instanceof PlatformDto){
                PlatformDto platform= (PlatformDto) o;
                DictDetailDto dictDetail=new DictDetailDto();
                dictDetail.setId(platform.getId());
                dictDetail.setDictSort(i);
                DictSmallDto dictSmallDto=new DictSmallDto();
                dictSmallDto.setId(dict.getId());
                dictDetail.setLabel(platform.getPlafName());
                dictDetail.setValue(platform.getId()+"");
                dictDetails.add(dictDetail);
            }
        }
        map.put("content",dictDetails);
        return map;
    }

    @Log("查询多个字典详情")
    @ApiOperation("查询多个字典详情")
    @GetMapping(value = "/map")
    public ResponseEntity<Object> getDictDetailMaps(@RequestParam String dictName){
        String[] names = dictName.split("[,，]");
        Map<String, List<DictDetailDto>> dictMap = new HashMap<>(16);
        for (String name : names) {
            dictMap.put(name, dictDetailService.getDictByName(name));
        }
        return new ResponseEntity<>(dictMap, HttpStatus.OK);
    }

    @Log("新增字典详情")
    @ApiOperation("新增字典详情")
    @PostMapping
    @PreAuthorize("@el.check('dict:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DictDetail resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        dictDetailService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改字典详情")
    @ApiOperation("修改字典详情")
    @PutMapping
    @PreAuthorize("@el.check('dict:edit')")
    public ResponseEntity<Object> update(@Validated(DictDetail.Update.class) @RequestBody DictDetail resources){
        dictDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除字典详情")
    @ApiOperation("删除字典详情")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@el.check('dict:del')")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        dictDetailService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
