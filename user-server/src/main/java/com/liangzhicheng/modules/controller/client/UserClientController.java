package com.liangzhicheng.modules.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.utils.SysBeanUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.entity.TestUserEntity;
import com.liangzhicheng.modules.entity.dto.TestUserDTO;
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

/**
 * @description 【客户端】用户控制器
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Api(value = "UserClientController", tags = {"【客户端】用户相关控制器"})
@RestController
@RequestMapping(value = "/client")
public class UserClientController extends BaseController {

    @Resource
    private ITestUserService testUserService;

    @ApiOperation(value = "获取用户")
    @PostMapping(value = "/getUser")
    @ApiOperationSupport(ignoreParameters = {"userDTO.username", "userDTO.phone"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "success", response = TestUserVO.class)})
    public ResponseResult getUser(@RequestBody TestUserDTO userDTO) {
        SysToolUtil.info("接下来要进行{" + userDTO.getId() + "}号用户信息的查询");
        TestUserEntity user = testUserService.getById(userDTO.getId());
        SysToolUtil.info("用户信息查询成功,内容为:" + JSONObject.toJSONString(user));
        return buildSuccessInfo(SysBeanUtil.copyEntity(user, TestUserVO.class));
    }

}
