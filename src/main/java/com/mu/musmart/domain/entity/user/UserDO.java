package com.mu.musmart.domain.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mu.musmart.domain.entity.base.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class UserDO extends BaseDo {



}
