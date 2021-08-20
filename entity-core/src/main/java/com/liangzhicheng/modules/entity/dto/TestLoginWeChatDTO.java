package com.liangzhicheng.modules.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 【微信】登录相关数据传输对象
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Data
public class TestLoginWeChatDTO {

    @ApiModelProperty(value = "微信code")
    private String code;
    @ApiModelProperty(value = "用户信息密文")
    private String encryptedData;
    @ApiModelProperty(value = "解密算法初始向量")
    private String iv;

    @ApiModelProperty(value = "应用类型：1IOS，2ANDROID")
    private String appType;
    @ApiModelProperty(value = "设备号")
    private String deviceNo;

}
