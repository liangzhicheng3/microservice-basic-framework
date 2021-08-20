package com.liangzhicheng.modules.feign;

import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.modules.entity.dto.TestProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description 商品服务feign接口远程调用类
 * @author liangzhicheng
 * @since 2021-07-30
 */
@FeignClient(name = "product-service")
public interface ITestProductFeignApi {

    /**
     * @description 获取商品
     * @param productDTO
     * @return ResponseResult
     */
    @PostMapping(value = "/client/getProduct")
    ResponseResult getProduct(@RequestBody TestProductDTO productDTO);

}
