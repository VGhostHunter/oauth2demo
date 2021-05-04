package com.demo.security.filter;

import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.repository.DefaultRateLimiterErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyRateLimitErrorHandler extends DefaultRateLimiterErrorHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleError(String msg, Exception e) {
        //记录日志 是否被人攻击？
        logger.info("{}", msg);
        super.handleError(msg, e);
    }
}
