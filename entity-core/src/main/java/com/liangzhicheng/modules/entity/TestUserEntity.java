package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description 用户实体类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_user")
public class TestUserEntity extends Model<TestUserEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 账号名称
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 国家id
     */
    private String countryId;

    /**
     * 国家名称
     */
    private String countryName;

    /**
     * 省份id
     */
    private String provinceId;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市id
     */
    private String cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 微信授权唯一凭证
     */
    private String openId;

    @Override
    public Serializable pkVal() {
        return null;
    }

}
