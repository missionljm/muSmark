package com.mu.musmart.domain.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mu.musmart.domain.entity.base.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class UserDO extends BaseDo {

    private static final long serialVersionUID = 1L;

    /**
     * 第三方用户ID
     */
    @TableField("third_account_id")
    private String thirdAccountId;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 登录方式: 0-微信登录，1-账号密码登录
     */
    @TableField("login_type")
    private Integer loginType;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Integer deleted;



}
