package com.liangzhicheng.modules.controller.server;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.modules.entity.dto.SysUserDTO;
import com.liangzhicheng.modules.entity.vo.SysUserLoginVO;
import com.liangzhicheng.modules.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @description 【服务端】登录控制器
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Api(value = "LoginServerController", tags = {"【服务端】登录相关控制器"})
@RestController
@RequestMapping(value = "/server")
public class LoginServerController extends BaseController {

    @Resource
    private ISysUserService accountService;

    @ApiOperation(value = "登录")
    @PostMapping(value = "/login")
    @ApiOperationSupport(ignoreParameters = {"accountDTO.companyId",
            "accountDTO.deptId", "accountDTO.roleIds", "accountDTO.keyword",
            "accountDTO.id", "accountDTO.truename", "accountDTO.avatar",
            "accountDTO.isAdmin", "accountDTO.loginStatus", "accountDTO.newPassword",
            "accountDTO.pageNo", "accountDTO.pageSize"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = SysUserLoginVO.class)})
    public ResponseResult login(@RequestBody SysUserDTO accountDTO,
                                HttpServletRequest request){
        return buildSuccessInfo(accountService.login(accountDTO, request));
    }

    @ApiOperation(value = "退出登录")
    @PostMapping(value = "/logOut")
    public ResponseResult logOut(HttpServletRequest request){
        accountService.logOut(request);
        return buildSuccessInfo(null);
    }

}
