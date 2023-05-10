package com.ContactManager.controller;

import com.ContactManager.dao.UserRepository;
import com.ContactManager.entities.User;
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

    @Autowired
    private UserRepository userRepository;
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
        String message = ""
                + "<div style='border:1px solid #e2e2e2; padding:20px'>"
                + "<h1>"
                + "OTP is "
                + "<b>"+otp
                + "</n>"
                + "</h1> "
                + "</div>";;
        String to = email;

        boolean flag = this.emailService.sendEmail(subject, message, to);
        if(flag){
            session.setAttribute("otp", otp);
            session.setAttribute("email", email);
            return "verify_otp";
        }else{
            session.setAttribute("message", "Pls check your email Id !!!");
            return "forgot_email_form";
        }
    }

//    Verify OTP controller
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp,HttpSession session)
    {
        int myOtp=(int)session.getAttribute("otp");

        System.out.println("User OTP "+otp);
        System.out.println("Our OTP "+myOtp);

        String email=(String)session.getAttribute("email");
        if(myOtp==otp)
        {
            //password change form
            User user = this.userRepository.getUserByUserName(email);

            if(user==null)
            {
                //send error message
                session.setAttribute("message", "User does not exits with this email !!");

                return "forgot_email_form";
            }else
            {
                //send change password form
                return "password_change_form";

            }
        }else
        {
            session.setAttribute("message", "You have entered wrong otp !!");
            return "verify_otp";
        }
    }


}
