package com.demo.security.config;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface PermissionService {

    /**
     * hasPermission
     * @param request 当前的request
     * @param authentication 包含了用户的信息
     * @return
     */
    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
