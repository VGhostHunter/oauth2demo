package com.demo.security.server.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class OAuth2OuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 用来存储 token的 默认的实现就是内存中的实现
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(tokenConverter());
//        return new RedisTokenStore(redisConnectionFactory);
//        return new JdbcTokenStore(dataSource);
    }

    /**
     * TokenKeyEndpoint 需要此tokenConverter
     * @return
     */
    @Bean
    public JwtAccessTokenConverter tokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("demo.key"), "123456".toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("demo"));
        return converter;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                //当使用refresh token的时候是没有密码的 需要获取用户信息只能去查
                .userDetailsService(userDetailsService)
                //告诉服务器 用这个tokenStore来存储token
                .tokenStore(tokenStore())
                .tokenEnhancer(tokenConverter())
                //authenticationManager 配置了userDetailsService 可以支持oauth的4中认证
                .authenticationManager(authenticationManager);
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }

    /**
     * 这里的配置放在数据库
     * @param clients
     * @throws Exception
     */
    /*@Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("orderApp")
                .secret(passwordEncoder.encode("123456"))
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600)
                .resourceIds("order-service")
                .authorizedGrantTypes("password")
                .and()
                .withClient("orderService")
                .secret(passwordEncoder.encode("123456"))
                .scopes("read")
                .accessTokenValiditySeconds(3600)
                .resourceIds("order-service")
                .authorizedGrantTypes("password");
    }*/

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //客户端信息自己去数据库里面找
        clients.jdbc(dataSource);
    }

    /**
     * 配置谁能找我验证 token
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //只有经过认证的服务才能拿到token key 也就是sign key 拿到sign key去验签名
        security.tokenKeyAccess("isAuthenticated()")
                //必须经过身份认证才可以来验 token
                .checkTokenAccess("isAuthenticated()");
    }
}
