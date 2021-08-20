package com.liangzhicheng.modules.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Maps;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.utils.SysTokenUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.common.utils.SysWeChatUtil;
import com.liangzhicheng.modules.entity.dto.TestLoginPhoneDTO;
import com.liangzhicheng.modules.entity.dto.TestLoginWeChatDTO;
import com.liangzhicheng.modules.entity.dto.TestVcodeDTO;
import com.liangzhicheng.modules.entity.vo.TestUserVO;
import com.liangzhicheng.modules.service.ITestUserService;
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
import java.util.*;

/**
 * @description 【客户端】登录控制器
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Api(value = "LoginClientController", tags = {"【客户端】登录相关控制器"})
@RestController
@RequestMapping(value = "/client")
public class LoginClientController extends BaseController {

    @Resource
    private ITestUserService testUserService;

    @ApiOperation(value = "获取短信验证码，不需要token")
    @PostMapping(value = "/sendSMS")
    @ApiOperationSupport(ignoreParameters = {"userLoginDTO.id", "userLoginDTO.username"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = Boolean.class)})
    public ResponseResult sendSMS(@RequestBody TestVcodeDTO userLoginDTO){
        testUserService.sendSMS(userLoginDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "APP手机号码登录，不需要token")
    @PostMapping(value = "/loginPhone")
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = String.class)})
    public ResponseResult loginPhone(@RequestBody TestLoginPhoneDTO loginDTO){
        return buildSuccessInfo(testUserService.loginPhone(loginDTO));
    }

    @ApiOperation(value = "APP授权登录，不需要token")
    @PostMapping(value = "/loginCodeAPP")
    @ApiOperationSupport(ignoreParameters = {"loginDTO.encryptedData", "loginDTO.iv"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = String.class)})
    public ResponseResult loginCodeAPP(@RequestBody TestLoginWeChatDTO loginDTO){
        return buildSuccessInfo(testUserService.loginCodeAPP(loginDTO));
    }

    @ApiOperation(value = "小程序授权登录，不需要token")
    @PostMapping(value = "/loginMINI")
    @ApiOperationSupport(ignoreParameters = {"loginDTO.appType", "loginDTO.deviceNo"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = TestUserVO.class)})
    public ResponseResult loginMINI(@RequestBody TestLoginWeChatDTO loginDTO){
        return buildSuccessInfo(testUserService.loginMINI(loginDTO));
    }

    @ApiOperation(value = "APP退出登录")
    @PostMapping(value = "/logOutAPP")
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = String.class)})
    public ResponseResult logOutAPP(HttpServletRequest request){
        testUserService.logOutAPP(request);
        return buildSuccessInfo(null);
    }

}
