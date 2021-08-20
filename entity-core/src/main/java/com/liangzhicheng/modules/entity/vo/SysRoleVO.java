package com.liangzhicheng.modules.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 角色VO类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Data
@ApiModel(value="SysRoleVO")
public class SysRoleVO {

    @ApiModelProperty("角色id")
    private String id;
    @ApiModelProperty("角色名称")
    private String name;
    @ApiModelProperty("职务描述")
    private String description;
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

}
