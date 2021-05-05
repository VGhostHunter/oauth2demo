package com.demo.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class GatewayAuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        //没传token 已经经过认证filter 认证为AnonymousAuthenticationToken
        if(authException instanceof AccessTokenRequiredException) {
            logger.info("update log to 401");
        } else {
            logger.info("add log 401");
        }

        request.setAttribute("logEnd", "yes");

        super.commence(request, response, authException);
    }
}
