package com.liangzhicheng.modules.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.pay.alipay.utils.AlipayRefundUtil;
import com.liangzhicheng.common.pay.wechatpay.utils.MD5Util;
import com.liangzhicheng.common.pay.wechatpay.utils.SignUtil;
import com.liangzhicheng.common.pay.wechatpay.utils.WeChatRefundUtil;
import com.liangzhicheng.common.pay.wechatpay.utils.XmlUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.entity.dto.TestOrderDTO;
import com.liangzhicheng.modules.entity.vo.TestOrderVO;
import com.liangzhicheng.modules.feign.ITestOrderFeignApi;
import com.liangzhicheng.modules.service.IPayService;
import io.swagger.annotations.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @description 支付控制器
 * @author liangzhicheng
 * @since 2021-08-13
 */
@Api(value="PayClientController", tags = {"【客户端】支付相关控制器"})
@RestController
@RequestMapping("/client")
public class PayClientController extends BaseController {

    @Resource
    private IPayService payService;

    @ApiOperation(value = "测试调用获取订单")
    @PostMapping(value = "/testInvokeGetOrder")
    @ApiOperationSupport(ignoreParameters = {"orderDTO.userId", "orderDTO.productId"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "success", response = TestOrderVO.class)})
    public ResponseResult testInvokeGetOrder(@RequestBody TestOrderDTO orderDTO) {
        return buildSuccessInfo(payService.testInvokeGetOrder(orderDTO));
    }

    @ApiOperation(value = "微信小程序支付")
    @PostMapping(value = "/weChatMiniPay")
    @ApiOperationSupport(ignoreParameters = {"orderDTO.userId", "orderDTO.productId"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = String.class)})
    public ResponseResult weChatMiniPay(@RequestBody TestOrderDTO orderDTO,
                                        HttpServletRequest request){
        return buildSuccessInfo(payService.weChatMiniPay(orderDTO, request));
    }

    @ApiOperation(value = "微信APP支付")
    @PostMapping(value = "/weChatAppPay")
    @ApiOperationSupport(ignoreParameters = {"orderDTO.userId", "orderDTO.productId"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = String.class)})
    public ResponseResult weChatAppPay(@RequestBody TestOrderDTO orderDTO,
                                       HttpServletRequest request){
        return buildSuccessInfo(payService.weChatAppPay(orderDTO, request));
    }

    @ApiOperation(value = "支付宝APP支付")
    @PostMapping(value = "/alipayApp")
    @ApiOperationSupport(ignoreParameters = {"orderDTO.userId", "orderDTO.productId"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = String.class)})
    public ResponseResult alipayApp(@RequestBody TestOrderDTO orderDTO){
        return buildSuccessInfo(payService.alipayApp(orderDTO));
    }

    @ApiOperation(value = "微信支付退款")
    @PostMapping(value = "/weChatRefund")
    @ApiOperationSupport(ignoreParameters = {"orderDTO.userId", "orderDTO.productId"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = String.class)})
    public ResponseResult weChatRefund(@RequestBody TestOrderDTO orderDTO){
        payService.weChatRefund(orderDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "支付宝支付退款")
    @PostMapping(value = "/alipayRefund")
    @ApiOperationSupport(ignoreParameters = {"orderDTO.userId", "orderDTO.productId"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = String.class)})
    public ResponseResult alipayRefund(@RequestBody TestOrderDTO orderDTO){
        payService.alipayRefund(orderDTO);
        return buildSuccessInfo(null);
    }

    /**
     * @description WeChat小程序支付异步回调
     * @param request
     * @param response
     * @return String
     */
    @ApiIgnore
    @PostMapping(value = "/weChatMiniPayNotify")
    protected void weChatMiniPayNotify(HttpServletRequest request, HttpServletResponse response){
        payService.weChatMiniPayNotify(request, response);
    }

    /**
     * @description WeChat APP支付异步回调
     * @param request
     * @param response
     * @return String
     */
    @ApiIgnore
    @PostMapping(value = "/weChatAppPayNotify")
    protected void weChatAppPayNotify(HttpServletRequest request, HttpServletResponse response){
        payService.weChatAppPayNotify(request, response);
    }

    /**
     * @description Alipay APP支付异步回调
     * @param request
     * @param response
     * @return String
     */
    @ApiIgnore
    @PostMapping(value = "/alipayAppPayNotify")
    protected void alipayAppPayNotify(HttpServletRequest request, HttpServletResponse response){
        payService.alipayAppPayNotify(request, response);
    }

}
