package com.ContactManager.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;
    public boolean sendEmail(String subject, String message, String to) {

        boolean flag = false;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("secularcomerade@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setText(message);
        mailMessage.setSubject(subject);

        javaMailSender.send(mailMessage);
        flag = true;
        System.out.println("Mail Sent Successfully");
        return flag;
    }
}
