package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 用户VO类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Data
@ApiModel(value="TestUserVO")
public class TestUserVO {

    @ApiModelProperty("用户id")
    private String id;
    @ApiModelProperty("用户名称")
    private String username;
    @ApiModelProperty("手机号码")
    private String phone;
    @ApiModelProperty("国家id")
    private String countryId;
    @ApiModelProperty("国家名称")
    private String countryName;
    @ApiModelProperty("省份id")
    private String provinceId;
    @ApiModelProperty("省份名称")
    private String provinceName;
    @ApiModelProperty("城市id")
    private String cityId;
    @ApiModelProperty("城市名称")
    private String cityName;

    @ApiModelProperty("登录tokenAPP")
    private String tokenAPP;
    @ApiModelProperty("登录tokenMINI")
    private String tokenMINI;

}
