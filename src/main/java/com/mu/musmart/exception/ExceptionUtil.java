package com.mu.musmart.exception;

import com.yonyou.beisendemo.vo.constants.StatusEnum;

public class ExceptionUtil {

    public static ForumException of(StatusEnum statusEnum , Object... args){
        return new ForumException(statusEnum , args);
    }
}
