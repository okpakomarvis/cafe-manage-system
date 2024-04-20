package com.cafems.cafemanagementsystem.jwt;

import com.cafems.cafemanagementsystem.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceJWTImpl implements UserSeviceJWT {
    private final UserDao userDao;

    @Override
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserDetails  user= userDao.findByEmailId(username);
                if(Objects.isNull(user)){
                    throw new UsernameNotFoundException("user not found");
                }else{
                    return user;
                }

            }
        };
    }
}
