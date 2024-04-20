package com.cafems.cafemanagementsystem.jwt;

import com.cafems.cafemanagementsystem.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

/*@Slf4j
@Service
public class CustomerUsersDetailService implements UserDetailsService {
    @Autowired
    UserDao userDao;

    private com.cafems.cafemanagementsystem.pojo.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("inside loadUserByUsername {}",username);
        userDetail = userDao.findByEmailId(username);
        if(!Objects.isNull(userDetail)){
            return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        }else {
            throw new UsernameNotFoundException("User Not Found");
        }
    }
    public  UserDetails getUserDetail(){
        return userDetail;
    }
}

 */
