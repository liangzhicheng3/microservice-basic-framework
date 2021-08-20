package com.liangzhicheng.modules.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.utils.SysBeanUtil;
import com.liangzhicheng.common.utils.SysConfigUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.entity.TestProductEntity;
import com.liangzhicheng.modules.entity.dto.TestProductDTO;
import com.liangzhicheng.modules.entity.vo.TestProductVO;
import com.liangzhicheng.modules.service.ITestProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description 【客户端】商品控制器
 * @author liangzhicheng
 * @since 2021-07-30
 */
@RefreshScope //刷新配置文件域注解,nacos配置列表中配置文件修改后动态刷新
@Api(value = "ProductClientController", tags = {"【客户端】商品相关控制器"})
@RestController
@RequestMapping(value = "/client")
public class ProductClientController extends BaseController {

    @Resource
    private ITestProductService testProductService;

    @Value("${appConfig.name}")
    private String name;
    @Value("${env}")
    private String env;

    @ApiOperation(value = "保存商品")
    @PostMapping(value = "/saveProduct")
    @ApiOperationSupport(ignoreParameters = {"productDTO.pageNo", "productDTO.pageSize"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "success", response = TestProductVO.class)})
    public ResponseResult saveProduct(@RequestBody TestProductDTO productDTO){
        return buildSuccessInfo(testProductService.saveProduct(productDTO));
    }

    @ApiOperation(value = "删除商品")
    @PostMapping(value = "/deleteProduct")
    @ApiOperationSupport(ignoreParameters = {"productDTO.name",
            "productDTO.price", "productDTO.stock",
            "productDTO.pageNo", "productDTO.pageSize"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "success", response = TestProductVO.class)})
    public ResponseResult deleteProduct(@RequestBody TestProductDTO productDTO){
        testProductService.deleteProduct(productDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "商品列表")
    @PostMapping(value = "/listProduct")
    @ApiOperationSupport(ignoreParameters = {"productDTO.id"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "success", response = TestProductVO.class)})
    public ResponseResult listProduct(@RequestBody TestProductDTO productDTO){
        return buildSuccessInfo(testProductService.listProduct(productDTO));
    }

    @ApiOperation(value = "获取商品")
//    @GetMapping(value = "/product/{id}")
    @PostMapping(value = "/getProduct")
    @ApiOperationSupport(ignoreParameters = {"productDTO.name",
            "productDTO.price", "productDTO.stock",
            "productDTO.pageNo", "productDTO.pageSize"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "success", response = TestProductVO.class)})
    public ResponseResult getProduct(/*@PathVariable("id") String id*/@RequestBody TestProductDTO productDTO) {
        String productId = productDTO.getId();
        SysToolUtil.info("接下来要进行{" + productId + "}号商品信息的查询");
        TestProductEntity product = testProductService.getProduct(productId);
        SysToolUtil.info("商品信息查询成功,内容为:" + JSONObject.toJSONString(product));
        SysToolUtil.info("动态获取配置类中参数:{" + name + "}");
        SysToolUtil.info("获取环境参数:{" + env + "}");
        SysToolUtil.info("获取配置文件参数:{" + SysConfigUtil.getValue("spring.application.name") + "}");
        return buildSuccessInfo(SysBeanUtil.copyEntity(product, TestProductVO.class));
    }

}
