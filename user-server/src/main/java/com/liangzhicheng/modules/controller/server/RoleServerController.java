package com.liangzhicheng.modules.controller.server;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.liangzhicheng.common.basic.BaseController;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.modules.entity.dto.SysRoleDTO;
import com.liangzhicheng.modules.entity.vo.SysRoleDescVO;
import com.liangzhicheng.modules.entity.vo.SysRoleVO;
import com.liangzhicheng.modules.service.ISysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description 【服务端】角色相关控制器
 * @author liangzhicheng
 * @since 2021-07-03
 */
@Api(value="Server-RoleServerController", tags={"【服务端】角色相关控制器"})
@RestController
@RequestMapping("/server/roleServerController")
public class RoleServerController extends BaseController {

    @Resource
    private ISysRoleService roleService;

    @ApiOperation(value = "保存角色")
    @PostMapping(value = "/saveRole")
    @ApiOperationSupport(ignoreParameters = {"roleDTO.keyword",
            "roleDTO.createDate", "roleDTO.pageNo", "roleDTO.pageSize"})
    public ResponseResult saveRole(@RequestBody SysRoleDTO roleDTO){
        roleService.saveRole(roleDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "删除角色")
    @PostMapping(value = "/deleteRole")
    @ApiOperationSupport(ignoreParameters = {"roleDTO.keyword",
            "roleDTO.createDate", "roleDTO.description", "roleDTO.menuIds",
            "roleDTO.permIds", "roleDTO.pageNo", "roleDTO.pageSize"})
    public ResponseResult deleteRole(@RequestBody SysRoleDTO roleDTO){
        roleService.deleteRole(roleDTO);
        return buildSuccessInfo(null);
    }

    @ApiOperation(value = "角色管理")
    @PostMapping(value = "/listRole")
    @ApiOperationSupport(ignoreParameters = {"roleDTO.id", "roleDTO.name",
            "roleDTO.description", "roleDTO.menuIds", "roleDTO.permIds"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE, message = "成功",
            response = SysRoleVO.class)})
    public ResponseResult listRole(@RequestBody SysRoleDTO roleDTO,
                                   Pageable pageable){
        return buildSuccessInfo(roleService.listRole(roleDTO, pageable));
    }

    @ApiOperation(value = "获取角色")
    @PostMapping(value = "/getRole")
    @ApiOperationSupport(ignoreParameters = {"roleDTO.keyword", "roleDTO.createDate",
            "roleDTO.name", "roleDTO.description", "roleDTO.menuIds",
            "roleDTO.permIds", "roleDTO.pageNo", "roleDTO.pageSize"})
    @ApiResponses({@ApiResponse(code = ApiConstant.BASE_SUCCESS_CODE, message = "成功",
            response = SysRoleDescVO.class)})
    public ResponseResult getRole(@RequestBody SysRoleDTO roleDTO){
        return buildSuccessInfo(roleService.getRole(roleDTO));
    }

}
