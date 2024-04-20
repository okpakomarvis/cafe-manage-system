package com.cafems.cafemanagementsystem.serviceImpl;

import com.cafems.cafemanagementsystem.dao.UserDao;
import com.cafems.cafemanagementsystem.enums.Role;
import com.cafems.cafemanagementsystem.jwt.JwtFilter;
import com.cafems.cafemanagementsystem.jwt.JwtUtil;
import com.cafems.cafemanagementsystem.jwt.UserSeviceJWT;
import com.cafems.cafemanagementsystem.pojo.User;
import com.cafems.cafemanagementsystem.service.UserService;
import com.cafems.cafemanagementsystem.util.CafeUtils;
import com.cafems.cafemanagementsystem.util.EmailUtil;
import com.cafems.cafemanagementsystem.wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.cafems.cafemanagementsystem.constant.CafeConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserDao userDao;


    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtFilter jwtFilter;

    private final UserSeviceJWT userSeviceJWT;

    private final JwtUtil jwtUtil;

    private  final EmailUtil emailutil;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("inside signup {}", requestMap);
        try {
            if(validateSignUpMap(requestMap)){
                User user = userDao.findByEmailId(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
                }else {
                    return CafeUtils.getResponseEntity("Email Already Exits",HttpStatus.BAD_REQUEST);
                }
            }else {
                return CafeUtils.getResponseEntity(INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if(auth.isAuthenticated()){
                User user = userDao.findByEmailId(requestMap.get("email"));
                if(user.getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<>("{\"token\":\""+jwtUtil.generateToken(user.getEmail(), user.getRole().name())+"\"}", HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("{\"message\":\" Wait for admin Approval\"}",HttpStatus.BAD_REQUEST);
                }
            }

        }catch (BadCredentialsException ex){
                log.error("error caught {}",ex.getMessage());
        } catch (Exception ex){
            log.error("{}",ex);
        }
        return new ResponseEntity<>("{\"message\":\" Bad Credentials\"}",HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                Optional<User> user =userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(user.isPresent()){
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    //sendMailToAllAdmin(requestMap.get("status"), user.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("Status updated Successfully", HttpStatus.OK);
                }else{
                    return CafeUtils.getResponseEntity("User id doesn't exits", HttpStatus.NOT_FOUND);
                }
            }else{
                return CafeUtils.getResponseEntity(UNAUTHORIZE, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status!=null && status.equalsIgnoreCase("true")){
            emailutil.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approved","USER:- "+user+"\n is approved by \nADMIN:- "+jwtFilter.getCurrentUser()+"",allAdmin );
        }else{
            emailutil.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account disabled","USER:- "+user+"\n is disabled by \nADMIN:- "+jwtFilter.getCurrentUser()+"",allAdmin );
        }
    }


    private boolean validateSignUpMap(Map<String, String> requestMap){
       if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")){
           return true;
       }else {
           return false;
       }
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setStatus("false");
        user.setRole(Role.USER);

        return user;
    }
    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if(!userObj.equals(null)){
                if(passwordEncoder.matches(requestMap.get("oldPassword"), userObj.getPassword())){
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDao.save(userObj);
                    return CafeUtils.getResponseEntity("Password updated successfully", HttpStatus.OK);
                }
                /*
                if(userObj.getPassword().equals(requestMap.get("oldPassword"))){
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDao.save(userObj);

                }*/
                return CafeUtils.getResponseEntity("Incorrect old password", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return CafeUtils.getResponseEntity(SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())){
                emailutil.forgetMail(user.getEmail(), "Credential dent from Cafe Management System, user.getPassword", user.getPassword());
                return CafeUtils.getResponseEntity("check your mail for credential", HttpStatus.OK);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
