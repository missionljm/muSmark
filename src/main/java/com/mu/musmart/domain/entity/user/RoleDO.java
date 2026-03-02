package com.mu.musmart.domain.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mu.musmart.domain.entity.base.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 角色表实体类
 */
@TableName("base_role")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleDO extends BaseDo {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色编码
     */
    @TableField("code")
    private String code;

    /**
     * 角色描述
     */
    @TableField("versom")
    private String versom;


}