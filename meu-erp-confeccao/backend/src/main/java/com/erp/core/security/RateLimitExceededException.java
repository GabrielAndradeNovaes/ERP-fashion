package com.erp.core.security;

import org.springframework.security.core.AuthenticationException;

public class RateLimitExceededException extends AuthenticationException {
    public RateLimitExceededException(String msg) {
        super(msg);
    }
}
