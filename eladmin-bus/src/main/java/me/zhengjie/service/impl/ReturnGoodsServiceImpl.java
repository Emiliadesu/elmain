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

import lombok.SneakyThrows;
import me.zhengjie.domain.ReturnGoods;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ReturnGoodsRepository;
import me.zhengjie.service.ReturnGoodsService;
import me.zhengjie.service.dto.ReturnGoodsDto;
import me.zhengjie.service.dto.ReturnGoodsQueryCriteria;
import me.zhengjie.service.mapstruct.ReturnGoodsMapper;
import net.bytebuddy.implementation.bytecode.Throw;
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
* @author lh
* @date 2021-01-04
**/
@Service
@RequiredArgsConstructor
public class ReturnGoodsServiceImpl implements ReturnGoodsService {

    private final ReturnGoodsRepository returnGoodsRepository;
    private final ReturnGoodsMapper returnGoodsMapper;

    @Override
    public Map<String,Object> queryAll(ReturnGoodsQueryCriteria criteria, Pageable pageable){
        Page<ReturnGoods> page = returnGoodsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(returnGoodsMapper::toDto));
    }

    @Override
    public List<ReturnGoodsDto> queryAll(ReturnGoodsQueryCriteria criteria){
        return returnGoodsMapper.toDto(returnGoodsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ReturnGoodsDto findById(Long id) {
        ReturnGoods returnGoods = returnGoodsRepository.findById(id).orElseGet(ReturnGoods::new);
        ValidationUtil.isNull(returnGoods.getId(),"ReturnGoods","id",id);
        return returnGoodsMapper.toDto(returnGoods);
    }

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnGoodsDto create(ReturnGoods resources)  {
        ReturnGoodsQueryCriteria rgqc=new ReturnGoodsQueryCriteria();
        rgqc.setTmsOrderCode(resources.getTmsOrderCode());
        Optional<ReturnGoods> one = returnGoodsRepository.findOne((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, rgqc, criteriaBuilder));
        if (one.isPresent()) {
            ReturnGoods rg = one.get();
            if (rg!=null && rg.getTmsCompany()!=null) {
                throw new EntityExistException(ReturnGoods.class,"运单号已存在",resources.getTmsOrderCode());
            }
        }
        //获取用户信息
        String username = SecurityUtils.getCurrentUsername();
        //获取系统时间
        Timestamp date = new Timestamp(System.currentTimeMillis());
        resources.setCreateUser(username);
        resources.setCreateTime(date);
        //新增 修改人时间与创建人一样
        resources.setModifyUser(username);
        resources.setModifyTime(date);
        resources.setStatus("0");
        return returnGoodsMapper.toDto(returnGoodsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ReturnGoods resources) {
        ReturnGoods returnGoods = returnGoodsRepository.findById(resources.getId()).orElseGet(ReturnGoods::new);
        ValidationUtil.isNull( returnGoods.getId(),"ReturnGoods","id",resources.getId());
        returnGoods.copy(resources);
        returnGoodsRepository.save(returnGoods);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            returnGoodsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ReturnGoodsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReturnGoodsDto returnGoods : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("运单号", returnGoods.getTmsOrderCode());
            map.put("物流公司", returnGoods.getTmsCompany());
            map.put("是否原单退回", returnGoods.getIsOrigin());
            map.put("原订单号", returnGoods.getOriginOrderNo());
            map.put("原订单类型", returnGoods.getOriginOrderType());
            map.put("电商", returnGoods.getOwner());
            map.put("店铺", returnGoods.getOwnerShop());
            map.put("处理方式", returnGoods.getProcessWay());
            map.put("备注", returnGoods.getRemark());
            map.put("货品去向", returnGoods.getGoodsGoWhere());
            map.put("寄回单号", returnGoods.getReturnOwnerOrderNo());
            map.put("寄回时间", returnGoods.getReturnOwnerTime());
            map.put("是否完成", returnGoods.getStatus());
            map.put("登记人", returnGoods.getCreateUser());
            map.put("登记时间", returnGoods.getCreateTime());
            map.put("最后修改者", returnGoods.getModifyUser());
            map.put("最后修改时间", returnGoods.getModifyTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
