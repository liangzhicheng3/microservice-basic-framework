package com.liangzhicheng.modules.controller.server;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.modules.entity.dto.SysDeptDTO;
import com.liangzhicheng.modules.entity.vo.SysDeptDescVO;
import com.liangzhicheng.modules.entity.vo.SysDeptVO;
import com.liangzhicheng.modules.service.ISysDeptService;
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
 * @description 【服务端】部门相关控制器
 * @author liangzhicheng
 * @since 2021-08-13
 */
@Api(value="Server-DepartmentServerController", tags={"【服务端】部门相关控制器"})
@RestController
@RequestMapping("/server/departmentServerController")
public class DepartmentServerController extends BaseController {

    @Resource
    private ISysDeptService deptService;

    @ApiOperation(value = "保存部门")
    @PostMapping(value = "/saveDept")
    @ApiOperationSupport(ignoreParameters = {"deptDTO.keyword",
            "deptDTO.dateStart", "deptDTO.dateEnd",
            "deptDTO.pageNo", "deptDTO.pageSize"})
    public ResponseResult saveDept(@RequestBody SysDeptDTO deptDTO){
        deptService.saveDept(deptDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "删除部门")
    @PostMapping(value = "/deleteDept")
    @ApiOperationSupport(ignoreParameters = {"deptDTO.keyword",
            "deptDTO.dateStart", "deptDTO.dateEnd", "deptDTO.name",
            "deptDTO.companyId", "deptDTO.companyName", "deptDTO.description",
            "deptDTO.pageNo", "deptDTO.pageSize"})
    public ResponseResult deleteDept(@RequestBody SysDeptDTO deptDTO){
        deptService.deleteDept(deptDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "部门列表")
    @PostMapping(value = "/listDept")
    @ApiOperationSupport(ignoreParameters = {"deptDTO.id", "deptDTO.name",
            "deptDTO.companyName", "deptDTO.description"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE, message = "成功",
            response = SysDeptVO.class)})
    public ResponseResult listDept(@RequestBody SysDeptDTO deptDTO){
        return buildSuccessInfo(deptService.listDept(deptDTO));
    }

    @ApiOperation(value = "获取部门")
    @PostMapping(value = "/getDept")
    @ApiOperationSupport(ignoreParameters = {"deptDTO.keyword", "deptDTO.dateStart",
            "deptDTO.dateEnd", "deptDTO.name", "deptDTO.companyId", "deptDTO.companyName",
            "deptDTO.description", "deptDTO.pageNo", "deptDTO.pageSize"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE, message = "成功",
            response = SysDeptDescVO.class)})
    public ResponseResult getDept(@RequestBody SysDeptDTO deptDTO){
        return buildSuccessInfo(deptService.getDept(deptDTO));
    }

}
