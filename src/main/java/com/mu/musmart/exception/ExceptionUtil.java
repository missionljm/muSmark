package com.mu.musmart.exception;


import com.mu.musmart.enums.common.StatusEnum;

public class ExceptionUtil {

    public static ForumException of(StatusEnum statusEnum , Object... args){
        return new ForumException(statusEnum , args);
    }
}
