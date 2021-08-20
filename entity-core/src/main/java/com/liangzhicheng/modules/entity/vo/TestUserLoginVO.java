package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 用户登录VO类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Data
@ApiModel(value="TestUserLoginVO")
public class TestUserLoginVO {

    @ApiModelProperty("用户id")
    private String id;
    @ApiModelProperty("账号名称")
    private String accountName;

}
