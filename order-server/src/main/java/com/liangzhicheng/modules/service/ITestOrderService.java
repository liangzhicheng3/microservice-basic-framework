package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.TestOrderEntity;
import com.liangzhicheng.modules.entity.dto.TestOrderDTO;
import com.liangzhicheng.modules.entity.vo.TestOrderVO;

/**
 * @description 订单服务接口类
 * @author liangzhicheng
 * @since 2021-07-30
 */
public interface ITestOrderService extends IService<TestOrderEntity> {

    /**
     * @description 保存订单
     * @param orderDTO
     * @return TestOrderVO
     */
    TestOrderVO saveOrder(TestOrderDTO orderDTO);

    /**
     * @description 获取订单
     * @param orderDTO
     * @return TestOrderVO
     */
    TestOrderVO getOrder(TestOrderDTO orderDTO);

}
