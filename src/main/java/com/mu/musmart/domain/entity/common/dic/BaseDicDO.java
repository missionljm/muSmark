package com.mu.musmart.domain.entity.common.dic;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mu.musmart.domain.entity.base.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 角色表实体类
 */
@TableName("base_dic")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDicDO extends BaseDo {

    /**
     * 字典文本
     */
    @TableField("name")
    private String name;

    /**
     * 字典编码
     */
    @TableField("code")
    private String code;

    /**
     * 字典类型
     */
    @TableField("type")
    private String type;

    /**
     * 字典文本（英文）
     */
    @TableField("e_name")
    private String eName;


}
