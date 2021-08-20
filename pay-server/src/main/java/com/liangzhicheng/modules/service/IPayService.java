package com.liangzhicheng.modules.service;

import com.liangzhicheng.modules.entity.dto.TestOrderDTO;
import com.liangzhicheng.modules.entity.vo.TestOrderVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.SortedMap;

/**
 * @description 支付服务接口类
 * @author liangzhicheng
 * @since 2021-08-14
 */
public interface IPayService {

    /**
     * @description 测试调用获取订单
     * @param orderDTO
     * @return TestOrderVO
     */
    TestOrderVO testInvokeGetOrder(TestOrderDTO orderDTO);

    /**
     * @description 微信小程序支付
     * @param orderDTO
     * @param request
     * @return SortedMap<String, Object>
     */
    SortedMap<String, Object> weChatMiniPay(TestOrderDTO orderDTO, HttpServletRequest request);

    /**
     * @description 微信APP支付
     * @param orderDTO
     * @param request
     * @return SortedMap<String, Object>
     */
    SortedMap<String, Object> weChatAppPay(TestOrderDTO orderDTO, HttpServletRequest request);

    /**
     * @description 支付宝APP支付
     * @param orderDTO
     * @return String
     */
    String alipayApp(TestOrderDTO orderDTO);

    /**
     * @description 微信支付退款
     * @param orderDTO
     */
    void weChatRefund(TestOrderDTO orderDTO);

    /**
     * @description 支付宝支付退款
     * @param orderDTO
     */
    void alipayRefund(TestOrderDTO orderDTO);

    /**
     * @description WeChat小程序支付异步回调
     * @param request
     * @param response
     */
    void weChatMiniPayNotify(HttpServletRequest request, HttpServletResponse response);

    /**
     * @description WeChat APP支付异步回调
     * @param request
     * @param response
     */
    void weChatAppPayNotify(HttpServletRequest request, HttpServletResponse response);

    /**
     * @description Alipay APP支付异步回调
     * @param request
     * @param response
     */
    void alipayAppPayNotify(HttpServletRequest request, HttpServletResponse response);

}
