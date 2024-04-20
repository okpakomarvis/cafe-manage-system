package com.cafems.cafemanagementsystem.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.List;

@Service
public class EmailUtil {
    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bethsteve90@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if(list !=null && list.size()>0) {
            message.setCc(getCcArray(list));
        }
        emailSender.send(message);
    }
    private String[] getCcArray(List<String> ccList){
        String[] cc = new String[ccList.size()];
        for(int i=0; i< cc.length; i++){
            cc[i] = ccList.get(i);
        }
        return cc;
    }

    public void forgetMail(String to, String subject, String password) throws MessagingException{
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("bethsteve90@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlMsg ="<p><b><b>your Login detail for cafe Management System</b><b>Email: </b>"+to+" <br> <b>Password: </b>"+password+" <br> <a href=\"http://localhost:4200/\">Click here to login </a></p>";
        message.setContent(htmlMsg, "text/html");
        emailSender.send(message);
    }
}
