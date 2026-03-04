package com.mu.musmart.domain.entity.user;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mu.musmart.domain.entity.base.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("user_info")
public class UserInfoDO extends BaseDo {

    @TableField("user_name")
    private String userName;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 照片
     */
    @TableField("photo")
    private String photo;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 公司
     */
    @TableField("company")
    private String company;

    /**
     * 个人简介
     */
    @TableField("profile")
    private String profile;

    /**
     * 扩展字段
     */
    @TableField("extend")
    private String extend;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

}
