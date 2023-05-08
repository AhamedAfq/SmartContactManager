package com.ContactManager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotPasswordController {

    Random random = new Random(1000);
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email){
        System.out.println("Email for triggering OTP: "+ email);
//       Generating an OTP of 4 digits

        int otp = random.nextInt(999999);
        System.out.println("OTP triggered: "+ otp);
        return "verify_otp";
    }
}
