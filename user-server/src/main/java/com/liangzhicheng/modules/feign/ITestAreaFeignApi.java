package com.liangzhicheng.modules.feign;

import com.liangzhicheng.modules.entity.dto.TestAreaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @description 地区服务feign接口远程调用类
 * @author liangzhicheng
 * @since 2021-08-12
 */
@FeignClient(name = "area-service")
public interface ITestAreaFeignApi {

    /**
     * @description 获取地区
     * @param areaDTO
     * @return List<Map<String, Object>>
     */
    @PostMapping(value = "/client/getArea")
    List<Map<String, Object>> getArea(@RequestBody TestAreaDTO areaDTO);

}
