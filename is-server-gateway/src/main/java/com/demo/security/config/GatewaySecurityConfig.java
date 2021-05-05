package com.demo.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableResourceServer
public class GatewaySecurityConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private GatewayWebSecurityExpressionHandler gatewayWebSecurityExpressionHandler;

    @Autowired
    private GatewayAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private GatewayAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("gateway")
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
            .expressionHandler(gatewayWebSecurityExpressionHandler);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            //限流的过滤器加在最前面
            .addFilterBefore(new GatewayRateLimitFilter(), SecurityContextPersistenceFilter.class)
            //审计日志的filter 加在认证之后 授权之前
            .addFilterBefore(new GatewayAuditLogFilter(), ExceptionTranslationFilter.class)
            .authorizeRequests()
            .antMatchers("/token/**").permitAll()
            .anyRequest().access("#permissionService.hasPermission(request, authentication)");
    }
}
