package com.demo.security.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GatewayAuditLogFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String user = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        logger.info("Add AuditLog for {}", user);

        filterChain.doFilter(request, response);

        //可能在accessDeniedHandler已经更新过了
        if(StringUtils.isBlank((String) request.getAttribute("logEnd"))) {
            logger.info("Update AuditLog for {}", user);
        }
    }
}
