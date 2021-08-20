package com.liangzhicheng.modules.controller.server;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.utils.CacheUtil;
import com.liangzhicheng.modules.entity.dto.SysMenuDTO;
import com.liangzhicheng.modules.entity.vo.SysMenuDescVO;
import com.liangzhicheng.modules.entity.vo.SysMenuVO;
import com.liangzhicheng.modules.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description 【服务端】菜单相关控制器
 * @author liangzhicheng
 * @since 2021-08-13
 */
@Api(value="Server-MenuServerController", tags={"【服务端】菜单相关控制器"})
@RestController
@RequestMapping("/server/menuServerController")
public class MenuServerController extends BaseController {

    @Resource
    private ISysMenuService menuService;

    @ApiOperation(value = "新增菜单")
    @PostMapping(value = "/insertMenu")
    @ApiOperationSupport(ignoreParameters = {"menuDTO.id"})
    public ResponseResult insertMenu(@RequestBody SysMenuDTO menuDTO){
        menuService.insertMenu(menuDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "删除菜单")
    @PostMapping(value = "/deleteMenu")
    @ApiOperationSupport(ignoreParameters = {"menuDTO.type",
            "menuDTO.parentId", "menuDTO.name", "menuDTO.icon",
            "menuDTO.component", "menuDTO.routerPath", "menuDTO.redirect",
            "menuDTO.isHide", "menuDTO.rank"})
    public ResponseResult deleteMenu(@RequestBody SysMenuDTO menuDTO){
        menuService.deleteMenu(menuDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "更新菜单")
    @PostMapping(value = "/updateMenu")
    @ApiOperationSupport(ignoreParameters = {"menuDTO.type", "menuDTO.parentId"})
    public ResponseResult updateMenu(@RequestBody SysMenuDTO menuDTO){
        menuService.updateMenu(menuDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "菜单列表")
    @PostMapping(value = "/listMenu")
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE, message = "成功",
            response = SysMenuVO.class)})
    public ResponseResult listMenu(){
        return buildSuccessInfo(CacheUtil.listPermMenu());
    }

    @ApiOperation(value = "获取菜单")
    @PostMapping(value = "/getMenu")
    @ApiOperationSupport(ignoreParameters = {"menuDTO.type", "menuDTO.parentId",
            "menuDTO.name", "menuDTO.icon", "menuDTO.component",
            "menuDTO.routerPath", "menuDTO.redirect", "menuDTO.isHide", "menuDTO.rank"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE, message = "成功",
            response = SysMenuDescVO.class)})
    public ResponseResult getMenu(@RequestBody SysMenuDTO menuDTO){
        return buildSuccessInfo(menuService.getMenu(menuDTO));
    }

}
