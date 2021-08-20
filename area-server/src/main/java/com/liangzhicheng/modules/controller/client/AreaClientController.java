package com.liangzhicheng.modules.controller.client;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.page.PageResult;
import com.liangzhicheng.modules.entity.TestUserEntity;
import com.liangzhicheng.modules.entity.dto.TestAreaDTO;
import com.liangzhicheng.modules.entity.vo.TestAreaNameVO;
import com.liangzhicheng.modules.service.ITestAreaNameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description 【客户端】地区相关控制器
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Api(value="Client-AreaClientController", tags={"【客户端】地区相关控制器"})
@RestController
@RequestMapping("/client")
public class AreaClientController extends BaseController {

    @Resource
    private ITestAreaNameService areaNameService;

    @ApiOperation(value = "地区列表")
    @PostMapping(value = "/listArea")
    @ApiOperationSupport(ignoreParameters = {"areaDTO.country",
            "areaDTO.province", "areaDTO.city"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = TestAreaNameVO.class)})
    public ResponseResult listArea(@RequestBody TestAreaDTO areaDTO){
        return buildSuccessInfo(areaNameService.listArea(areaDTO));
    }

    @ApiIgnore
    @ApiOperation(value = "获取地区")
    @PostMapping(value = "/getArea")
    @ApiOperationSupport(ignoreParameters = {"areaDTO.areaId",
            "areaDTO.areaLevel", "areaDTO.page", "areaDTO.pageSize"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE,
            message = "成功", response = TestAreaNameVO.class)})
    public List<Map<String, Object>> getArea(@RequestBody TestAreaDTO areaDTO){
        return areaNameService.getArea(areaDTO);
    }

}
