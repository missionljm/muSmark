package com.mu.musmart.domain.entity.user;

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
public class UserRoleDO extends BaseDo {

    @TableField("user_id")
    private Long userId;

    @TableField("role_id")
    private Long roleId;

}
