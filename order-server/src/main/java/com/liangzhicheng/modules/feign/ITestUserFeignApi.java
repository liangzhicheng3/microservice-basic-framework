package com.liangzhicheng.modules.feign;

import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.modules.entity.dto.TestUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description 用户服务feign接口远程调用类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@FeignClient(name = "user-service") //name名称要与订单服务的服务名称一致
public interface ITestUserFeignApi {

    /**
     * @description 获取用户
     * @param userDTO
     * @return ResponseResult
     */
    @PostMapping(value = "/client/getUser")
    ResponseResult getUser(@RequestBody TestUserDTO userDTO);

}
