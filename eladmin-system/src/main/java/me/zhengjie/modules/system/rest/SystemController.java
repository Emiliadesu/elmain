package me.zhengjie.modules.system.rest;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.ClearCompanyInfo;
import me.zhengjie.domain.CompanyInfo;
import me.zhengjie.domain.SupplierInfo;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.modules.system.service.dto.UserQueryCriteria;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.*;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @website https://el-admin.vip
 * @author luob
 * @date 2020-10-09
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "特殊业务系统")
@RequestMapping("/api/busSystem")
public class SystemController {
    private final ClearCompanyInfoService clearCompanyInfoService;
    private final ShopInfoService shopInfoService;
    private final CustomerInfoService customerInfoService;
    private final SupplierInfoService supplierInfoService;
    private final LocalStorageService localStorageService;
    private final PlatformService platformService;
    @Autowired
    private RuoYuChenService ruoYuChenService;

    @GetMapping("/getCurUserClearCompAndComp")
    @Log("查询当前用户绑定的清关抬头信息")
    @ApiOperation("查询当前用户绑定的清关抬头信息")
    public ResponseEntity<Object> getCurUserClearComp(){
//        UserDto user=userService.findByIdDto(SecurityUtils.getCurrentUserId());
//        Map<String,Object>map=new HashMap<>();
//        // 无论如何都要获取所有业务信息
//        List<ClearCompanyInfoDto>dictClCompList=clearCompanyInfoService.queryAll(new ClearCompanyInfoQueryCriteria());
//        map.put("dictClComp",dictClCompList);
//        if (StringUtil.isEmpty(user.getClearCompId())&&user.getComp()!=null){
//            //如果绑定了商家但没有绑定业务组，则尝试获取该商家所属业务组
//            CompanyInfoQueryCriteria compCriteria=new CompanyInfoQueryCriteria();
//            compCriteria.setCompanyId(user.getComp().getCompanyId()+"");
//            List<CompanyInfoDto>compList=companyInfoService.queryAll(compCriteria);
//            List<ClearCompanyInfoDto>clearCompList=clearCompanyInfoService.queryByCompanyId(user.getComp().getCompanyId());
//            map.put("compList",compList);
//            map.put("clearCompList",clearCompList);
//            return new ResponseEntity<>(map, HttpStatus.OK);
//        }else if (StringUtil.isEmpty(user.getClearCompId())&&user.getComp()==null){
//            //如果既没绑定商家又没绑定商家业务，则查询全部
//            List<CompanyInfoDto>compList=companyInfoService.queryAll(new CompanyInfoQueryCriteria());
//            map.put("compList",compList);
//            return new ResponseEntity<>(map, HttpStatus.OK);
//        }
//        //上述条件都不满足，就查询用户绑定的商家业务id
//        String[]ids=user.getClearCompId().split(",");
//        List<String>idsList= new ArrayList<>();
//        for (String id : ids) {
//            if (StringUtil.isNotEmpty(id))
//                idsList.add(id);
//        }
//        List<ClearCompanyInfoDto>clearCompList=clearCompanyInfoService.queryInClearCompIds(idsList);
//        CompanyInfoQueryCriteria compCriteria=new CompanyInfoQueryCriteria();
//        map.put("clearCompList",clearCompList);
//        compCriteria.setCompanyId(user.getComp().getCompanyId()+"");
//        List<CompanyInfoDto>compList=companyInfoService.queryAll(compCriteria);
//        map.put("compList",compList);
        return null;
    }

    @GetMapping("/queryClearCompanyInfo")
    @Log("根据商家id查询绑定的业务组信息")
    @ApiOperation("查询当前用户绑定的清关抬头信息")
    public ResponseEntity<Object> queryClearCompanyInfo(ClearCompanyInfoQueryCriteria criteria){
        return new ResponseEntity<>(clearCompanyInfoService.queryAll(criteria),HttpStatus.OK);
    }

    @PostMapping("/queryAllZZShopInfo")
    @Log("查询所有店铺信息")
    @ApiOperation("查询当前用户绑定的清关抬头信息")
    public ResponseEntity<Object> queryAllZZShopInfo() {
        return new ResponseEntity<>(shopInfoService.queryAllZZShopInfo(), HttpStatus.OK);
    }

    @GetMapping("/queryCustomersByUser")
    @Log("根据当前用户查询客户")
    @ApiOperation("根据当前用户查询客户")
    public ResponseEntity<Object> queryCustomersByUser(CustomerInfoQueryCriteria criteria){
        return new ResponseEntity<>(customerInfoService.queryAll(criteria),HttpStatus.OK);
    }

    @GetMapping("/querySupplier")
    @Log("查询所有报关行")
    @ApiOperation("查询所有报关行")
    public ResponseEntity<Object> querySupplier(SupplierInfoQueryCriteria criteria){
        return new ResponseEntity<>(supplierInfoService.queryAll(criteria),HttpStatus.OK);
    }

    @GetMapping("/queryClrComps")
    @Log("查询所有业务抬头")
    @ApiOperation("查询所有业务抬头")
    public ResponseEntity<Object> queryClrComps(ClearCompanyInfoQueryCriteria criteria){
        return new ResponseEntity<>(clearCompanyInfoService.queryAll(criteria),HttpStatus.OK);
    }

    @ApiOperation("上传文件")
    @RequestMapping(value = "upload")
    @Log("文件上传")
    public ResponseEntity<Object> upload(@RequestParam String name, @RequestParam("file") MultipartFile file ,String opt,Long id){
        if (StringUtil.equals("ruoYuChen",opt)){
            ruoYuChenService.upLoadFile(file,id);
            return new ResponseEntity<>(localStorageService.create(name, file),HttpStatus.CREATED);
        }else
            return new ResponseEntity<>(localStorageService.create(name, file),HttpStatus.CREATED);
    }

    @GetMapping("/queryPlatform")
    @Log("查询所有电商平台")
    @ApiOperation("查询所有电商平台")
    public ResponseEntity<Object> queryPlatform(PlatformQueryCriteria criteria){
        return new ResponseEntity<>(platformService.queryAll(criteria),HttpStatus.OK);
    }

    @GetMapping("/queryAllCustom")
    @Log("查询客户基本信息")
    @ApiOperation("查询客户基本信息")
    public ResponseEntity<Object> queryAllCustom(CustomerInfoQueryCriteria criteria){
        return new ResponseEntity<>(customerInfoService.queryAll(criteria),HttpStatus.OK);
    }
}
