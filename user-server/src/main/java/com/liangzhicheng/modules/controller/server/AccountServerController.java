package com.liangzhicheng.modules.controller.server;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.config.mvc.interceptor.annotation.LoginValidate;
import com.liangzhicheng.config.mvc.interceptor.annotation.PermissionsValidate;
import com.liangzhicheng.modules.entity.dto.SysUserDTO;
import com.liangzhicheng.modules.entity.vo.SysPersonInfoVO;
import com.liangzhicheng.modules.entity.vo.SysUserDescVO;
import com.liangzhicheng.modules.entity.vo.SysUserVO;
import com.liangzhicheng.modules.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @description 【服务端】账号控制器
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Api(value = "AccountServerController", tags = {"【服务端】账号相关控制器"})
@RestController
@RequestMapping(value = "/server")
public class AccountServerController extends BaseController {

    @Resource
    private ISysUserService accountService;

    @ApiOperation(value = "保存账号")
    @PostMapping(value = "/saveAccount")
    @ApiOperationSupport(ignoreParameters = {"userDTO.keyword",
            "userDTO.password", "userDTO.avatar", "userDTO.isAdmin",
            "userDTO.newPassword", "userDTO.pageNo", "userDTO.pageSize"})
    public ResponseResult saveAccount(@RequestBody SysUserDTO userDTO){
        accountService.saveAccount(userDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "删除账号")
    @PostMapping(value = "/deleteAccount")
    @ApiOperationSupport(ignoreParameters = {"userDTO.companyId", "userDTO.deptId",
            "userDTO.roleIds", "userDTO.keyword", "userDTO.accountName",
            "userDTO.truename", "userDTO.password", "userDTO.avatar",
            "userDTO.isAdmin", "userDTO.loginStatus", "userDTO.newPassword",
            "userDTO.pageNo", "userDTO.pageSize"})
    public ResponseResult deleteAccount(@RequestBody SysUserDTO userDTO){
        accountService.deleteAccount(userDTO);
        return buildSuccessInfo(null);
    }

    /**
     * @description 更新当前登录用户头像
     * @param userDTO
     * @return WebResult
     */
    @ApiOperation(value = "更新头像")
    @PostMapping(value = "/updateAvatar")
    @ApiOperationSupport(ignoreParameters = {"userDTO.companyId", "userDTO.deptId",
            "userDTO.roleIds", "userDTO.keyword", "userDTO.id", "userDTO.accountName",
            "userDTO.truename", "userDTO.password", "userDTO.isAdmin", "userDTO.loginStatus",
            "userDTO.newPassword", "userDTO.pageNo", "userDTO.pageSize"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE, message = "成功",
            response = SysPersonInfoVO.class)})
    public ResponseResult updateAvatar(@RequestBody SysUserDTO userDTO, HttpServletRequest request){
        return buildSuccessInfo(accountService.updateAvatar(userDTO, request));
    }

    /**
     * @description 更新当前登录用户密码
     * @param userDTO
     * @return WebResult
     */
    @ApiOperation(value = "更新密码")
    @PostMapping(value = "/updatePassword")
    @ApiOperationSupport(ignoreParameters = {"userDTO.companyId", "userDTO.deptId",
            "userDTO.roleIds", "userDTO.keyword", "userDTO.id",
            "userDTO.accountName", "userDTO.truename", "userDTO.avatar",
            "userDTO.isAdmin", "userDTO.loginStatus",
            "userDTO.pageNo", "userDTO.pageSize"})
    public ResponseResult updatePassword(@RequestBody SysUserDTO userDTO, HttpServletRequest request){
        accountService.updatePassword(userDTO, request);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "重置密码")
    @PostMapping(value = "/resetPassword")
    @ApiOperationSupport(ignoreParameters = {"userDTO.companyId", "userDTO.deptId",
            "userDTO.roleIds", "userDTO.keyword", "userDTO.accountName",
            "userDTO.truename", "userDTO.password", "userDTO.avatar",
            "userDTO.isAdmin", "userDTO.loginStatus", "userDTO.newPassword",
            "userDTO.pageNo", "userDTO.pageSize"})
    public ResponseResult resetPassword(@RequestBody SysUserDTO userDTO){
        accountService.resetPassword(userDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "账号管理")
    @PostMapping(value = "/listAccount")
    @ApiOperationSupport(ignoreParameters = {"userDTO.id",
            "userDTO.accountName", "userDTO.truename", "userDTO.password",
            "userDTO.avatar", "userDTO.isAdmin", "userDTO.newPassword"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE, message = "成功",
            response = SysUserVO.class)})
    public ResponseResult listAccount(@RequestBody SysUserDTO userDTO){
        return buildSuccessInfo(accountService.listAccount(userDTO));
    }

    @ApiOperation(value = "获取账号")
    @PostMapping(value = "/getAccount")
    @ApiOperationSupport(ignoreParameters = {"userDTO.companyId", "userDTO.deptId",
            "userDTO.roleIds", "userDTO.keyword", "userDTO.accountName", "userDTO.truename",
            "userDTO.password", "userDTO.avatar", "userDTO.isAdmin", "userDTO.loginStatus",
            "userDTO.newPassword", "userDTO.pageNo", "userDTO.pageSize"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE, message = "成功",
            response = SysUserDescVO.class)})
    public ResponseResult getAccount(@RequestBody SysUserDTO userDTO){
        return buildSuccessInfo(accountService.getAccount(userDTO));
    }

}
