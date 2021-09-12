package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 权限实体类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_perm")
public class SysPermEntity extends Model<SysPermEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 权限id(主键)
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 表达式
     */
    private String expression;

    /**
     * 删除标记-平台：0否，1是
     */
    private String delFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
