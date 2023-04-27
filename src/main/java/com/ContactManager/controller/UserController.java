package com.ContactManager.controller;

import com.ContactManager.dao.UserRepository;
import com.ContactManager.entities.Contact;
import com.ContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileImage") MultipartFile file,
                                 Principal principal){

        try {

            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            if(file.isEmpty()){
//                File empty message
                System.out.println("Sry file is empty ...");
            }else{
//                File not empty then save the user to contacts
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("/static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
            }

//      Bidirectional Mapping (XX---IMPORTANT---XX)
            contact.setUsers(user);
            user.getContact().add(contact);

            this.userRepository.save(user);
            System.out.println("DATA : " + contact);
            System.out.println("Contact added to DB");

        }catch (Exception exception){
            System.out.println("Error: "+ exception.getMessage());
        }
        return "normal/add_contact_form";
    }
}
