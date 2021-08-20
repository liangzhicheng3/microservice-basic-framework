package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 角色权限VO类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Data
@ApiModel(value="SysRolePermVO")
public class SysRolePermVO {

    @ApiModelProperty("角色id")
    private String roleId;
    @ApiModelProperty("权限id")
    private String permId;
    @ApiModelProperty("权限名称")
    private String permName;
    @ApiModelProperty("表达式")
    private String expression;

}
