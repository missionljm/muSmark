package com.mu.musmart.exception;


import com.mu.musmart.domain.vo.Status;
import com.mu.musmart.enums.common.StatusEnum;
import lombok.Getter;

public class ForumAdviceException extends RuntimeException{

    @Getter
    private Status status;

    public ForumAdviceException(Status status){
        this.status = status;
    }

    public ForumAdviceException(int code , String msg){
        this.status = Status.newStatus(code, msg);
    }

    public ForumAdviceException(StatusEnum status, Object... args){
        this.status = Status.newStatus(status, args);
    }
}
