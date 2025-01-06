package com.zerobase.stock_dividend.exception.impl;

import com.zerobase.stock_dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AlreadyExistCompany extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }


    @Override
    public String getMessage() {
        return "이미 추가된 회사입니다.";
    }
}
