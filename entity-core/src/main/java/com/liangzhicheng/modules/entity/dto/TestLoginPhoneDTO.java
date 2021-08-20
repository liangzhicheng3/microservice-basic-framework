package com.liangzhicheng.modules.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 【手机】登录相关数据传输对象
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Data
public class TestLoginPhoneDTO {

    @ApiModelProperty(value = "手机号码")
    private String phone;
    @ApiModelProperty(value = "短信验证码")
    private String vcode;
    @ApiModelProperty(value = "应用类型：1IOS，2ANDROID")
    private String appType;
    @ApiModelProperty(value = "设备号")
    private String deviceNo;

}
