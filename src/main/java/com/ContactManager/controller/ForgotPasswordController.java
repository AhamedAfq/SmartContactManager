package com.ContactManager.controller;

import com.ContactManager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotPasswordController {

    Random random = new Random(1000);

    @Autowired
    private EmailService emailService;
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email,
                          HttpSession session){
        System.out.println("Email for triggering OTP: "+ email);
//       Generating an OTP of 4 digits

        int otp = random.nextInt(999999);
        System.out.println("OTP triggered: "+ otp);
//TODO:  Code for send OTR to email
        String subject = "OTP from smart contact manager";
        String message = "OTP = "+otp+"";
        String to = email;

        boolean flag = this.emailService.sendEmail(subject, message, to);
        if(flag){
            session.setAttribute("otp", otp);
            return "verify_otp";
        }else{
            session.setAttribute("message", "Pls check your email Id !!!");
            return "change_password";
        }
    }
}
