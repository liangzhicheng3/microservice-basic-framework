package com.liangzhicheng.modules.controller;

import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.modules.service.IProviderService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description 供应者控制器
 * @author liangzhicheng
 * @since 2021-08-16
 */
@Api(value="ProviderController", tags = {"供应者相关控制器"})
@RestController
public class ProviderController extends BaseController {

    @Resource
    private IProviderService providerService;

    @GetMapping(value = "/send")
    public ResponseResult send(){
        providerService.send();
        return buildSuccessInfo("发送成功 ...");
    }

}
