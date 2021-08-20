package com.liangzhicheng.modules.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.exception.CustomizeException;
import com.liangzhicheng.common.utils.SysBeanUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.dao.ITestOrderDao;
import com.liangzhicheng.modules.entity.TestOrderEntity;
import com.liangzhicheng.modules.entity.TestProductEntity;
import com.liangzhicheng.modules.entity.TestUserEntity;
import com.liangzhicheng.modules.entity.dto.TestOrderDTO;
import com.liangzhicheng.modules.entity.dto.TestProductDTO;
import com.liangzhicheng.modules.entity.dto.TestUserDTO;
import com.liangzhicheng.modules.entity.vo.TestOrderVO;
import com.liangzhicheng.modules.feign.ITestProductFeignApi;
import com.liangzhicheng.modules.feign.ITestUserFeignApi;
import com.liangzhicheng.modules.service.ITestOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description 订单服务实现类
 * @author liangzhicheng
 * @since 2021-07-30
 */
@Service
public class TestOrderServiceImpl extends ServiceImpl<ITestOrderDao, TestOrderEntity> implements ITestOrderService {

    @Resource
    private ITestUserFeignApi testUserFeignApi;
    @Resource
    private ITestProductFeignApi testProductFeignApi;

    /**
     * @description 保存订单
     * @param orderDTO
     * @return TestOrderVO
     */
    @Override
    public TestOrderVO saveOrder(TestOrderDTO orderDTO) {
        String productId = orderDTO.getProductId();
        String userId = orderDTO.getUserId();
        SysToolUtil.info("接收到{" + productId + "}号商品的下单请求,接下来调用商品微服务查询此商品信息");
        //集成Feign组件,远程调用用户微服务,实现负载均衡查询用户信息(nacos)
        SysToolUtil.info("接收到{" + userId + "}号用户的下单请求,接下来调用用户微服务查询此用户信息");
        TestUserEntity user = SysToolUtil.getObjectEntity(
                testUserFeignApi.getUser(new TestUserDTO(userId)), TestUserEntity.class);
        if(SysToolUtil.isNull(user)){
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "获取远程调用用户信息不存在");
        }
        SysToolUtil.info("查询到{" + userId + "}号用户的信息,内容是:" + JSONObject.toJSONString(user));
        TestProductEntity product = SysToolUtil.getObjectEntity(
                testProductFeignApi.getProduct(new TestProductDTO(productId)), TestProductEntity.class);
        if(SysToolUtil.isNull(product)){
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "获取远程调用商品信息不存在");
        }
        SysToolUtil.info("查询到{" + productId + "}号商品的信息,内容是:" + JSONObject.toJSONString(product));
        //创建订单并保存
        TestOrderEntity order = new TestOrderEntity(SysToolUtil.random(), userId,
                user.getUsername(), productId, product.getName(), product.getPrice(), 1);
        baseMapper.insert(order);
        SysToolUtil.info("创建订单成功,订单信息为:" + JSONObject.toJSONString(order));
        return SysBeanUtil.copyEntity(order, TestOrderVO.class);
    }

    /**
     * @description 获取订单
     * @param orderDTO
     * @return TestOrderVO
     */
    @Override
    public TestOrderVO getOrder(TestOrderDTO orderDTO) {
        String orderId = orderDTO.getId();
        SysToolUtil.info("接收到{" + orderId + "}号订单的请求,接下来调用订单微服务查询此订单信息");
        TestOrderVO orderVO = SysBeanUtil.copyEntity(baseMapper.selectById(orderDTO.getId()), TestOrderVO.class);
        SysToolUtil.info("查询到{" + orderId + "}号订单的信息,内容是:" + JSONObject.toJSONString(orderVO));
        return orderVO;
    }

}
