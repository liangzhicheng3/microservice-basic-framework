package com.liangzhicheng.modules.feign;

import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.modules.entity.dto.TestOrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description 订单服务feign接口远程调用类
 * @author liangzhicheng
 * @since 2021-08-14
 */
@FeignClient(name = "order-service")
public interface ITestOrderFeignApi {

    /**
     * @description 获取订单
     * @param orderDTO
     * @return ResponseResult
     */
    @PostMapping(value = "/client/getOrder")
    ResponseResult getOrder(@RequestBody TestOrderDTO orderDTO);

}
