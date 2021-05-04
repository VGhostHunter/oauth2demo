package com.demo.security.server.resource;

import com.demo.security.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author vghosthunter
 */
@Component
public class UserDetailServiceImpl implements UserDetailsService {

    /**
     * 这里应该去查数据库
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = new User();
        user.setUsername(username);
        user.setId(1L);
        return user;
    }
}
