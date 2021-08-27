package com.liangzhicheng.modules.controller;

import com.liangzhicheng.modules.service.IProviderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description 供应者控制器
 * @author liangzhicheng
 * @since 2021-08-16
 */
@RestController
public class ProviderController {

    @Resource
    private IProviderService providerService;

    @GetMapping(value = "/send")
    public String send(){
        providerService.send();
        return "发送成功 ...";
    }

}
