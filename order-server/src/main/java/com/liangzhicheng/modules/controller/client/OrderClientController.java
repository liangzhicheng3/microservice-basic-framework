package com.liangzhicheng.modules.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.utils.SysBeanUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.entity.TestOrderEntity;
import com.liangzhicheng.modules.entity.dto.TestOrderDTO;
import com.liangzhicheng.modules.entity.vo.TestOrderVO;
import com.liangzhicheng.modules.service.ITestOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description 【客户端】订单控制器
 * @author liangzhicheng
 * @since 2021-07-30
 */
@Api(value = "OrderClientController", tags = {"【客户端】订单相关控制器"})
@RestController
@RequestMapping(value = "/client")
public class OrderClientController extends BaseController {

    @Resource
    private ITestOrderService testOrderService;

    @ApiOperation(value = "保存订单")
    @PostMapping(value = "/saveOrder")
    @ApiOperationSupport(ignoreParameters = {"orderDTO.id"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "success", response = TestOrderVO.class)})
    public ResponseResult saveOrder(@RequestBody TestOrderDTO orderDTO) {
        return buildSuccessInfo(testOrderService.saveOrder(orderDTO));
    }

    @ApiOperation(value = "获取订单")
    @PostMapping(value = "/getOrder")
    @ApiOperationSupport(ignoreParameters = {"orderDTO.userId", "orderDTO.productId"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "success", response = TestOrderVO.class)})
    public ResponseResult getOrder(@RequestBody TestOrderDTO orderDTO) {
        return buildSuccessInfo(testOrderService.getOrder(orderDTO));
    }

}
