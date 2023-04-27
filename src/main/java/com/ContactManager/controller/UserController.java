package com.ContactManager.controller;

import com.ContactManager.dao.UserRepository;
import com.ContactManager.entities.Contact;
import com.ContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController  {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute // Common set of code that has run before all the following handlers (or) method to add common data to teh response
    public void addCommonData(Model model, Principal principal){ // Principal return the unique attribute of the Entity
        String userName = principal.getName();
        System.out.println("USERNAME: " + userName);

        //      Get the user using userName
        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER: " + user);

        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashBoard(Model model, Principal principal){  // Principal return the unique attribute of the Entity
        model.addAttribute("title", "User dashboard");
        return "normal/user_dashboard";
    }

//    Open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){

        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, Principal principal){

        String name = principal.getName();
        User user = this.userRepository.getUserByUserName(name);

//      Bidirectional Mapping (XX---IMPORTANT---XX)
        contact.setUsers(user);
        user.getContact().add(contact);

        this.userRepository.save(user);
        System.out.println("DATA : " + contact);
        System.out.println("Contact added to DB");

        return "normal/add_contact_form";
    }
}
