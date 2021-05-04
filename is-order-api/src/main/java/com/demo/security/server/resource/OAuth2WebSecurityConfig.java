package com.demo.security.server.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.*;

/**
 * 配置怎么去认证服务器验证令牌 获取用户信息
 * @author vghosthunter
 */
@EnableWebSecurity
public class OAuth2WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices tokenServices = new RemoteTokenServices();
        tokenServices.setClientId("orderServer");
        tokenServices.setClientSecret("123456");
        tokenServices.setCheckTokenEndpointUrl("http://localhost:9090/oauth/check_token");

        //将token转换成用户信息
        tokenServices.setAccessTokenConverter(accessTokenConverter());
        return tokenServices;
    }

    private AccessTokenConverter accessTokenConverter() {
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        DefaultUserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();

        //设置我们的userDetailService 如果不做设置 这里的userDetailService是null 就只能拿到username的信息
        userTokenConverter.setUserDetailsService(userDetailsService);
        accessTokenConverter.setUserTokenConverter(userTokenConverter);
        return accessTokenConverter;
    }

    /**
     * 验证用户相关的信息使用 authenticationManagerBean
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        OAuth2AuthenticationManager authenticationManager = new OAuth2AuthenticationManager();
        authenticationManager.setTokenServices(tokenServices());
        return authenticationManager;
    }
}
