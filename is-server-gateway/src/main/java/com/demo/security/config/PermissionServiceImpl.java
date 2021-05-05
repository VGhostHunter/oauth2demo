package com.demo.security.config;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class PermissionServiceImpl implements PermissionService {

    /**
     * 将权限信息缓存起来 查询缓存中的权限列表
     * @param request 当前的request
     * @param authentication 包含了用户的信息
     * @return
     */
    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
//        System.out.println(request.getRequestURI());
//        System.out.println(ReflectionToStringBuilder.toString(authentication));

        // 没有传token
        if(authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessTokenRequiredException(null);
        }

//        return RandomUtils.nextInt() % 2 == 0;
        return true;
    }
}
