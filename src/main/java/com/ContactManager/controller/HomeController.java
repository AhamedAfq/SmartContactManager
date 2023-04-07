package com.ContactManager.controller;

import com.ContactManager.dao.UserRepository;
import com.ContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController  {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        User user = new User();
        user.setName("AhamedAfq");
        user.setEmail("ashfakahamed142@gmail.com");
        userRepository.save(user);
        return "Its Working...";
    }
}
