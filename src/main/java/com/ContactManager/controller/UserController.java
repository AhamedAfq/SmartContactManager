package com.ContactManager.controller;

import com.ContactManager.dao.ContactRepository;
import com.ContactManager.dao.UserRepository;
import com.ContactManager.entities.Contact;
import com.ContactManager.entities.User;
import com.ContactManager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController  {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository ;

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
                                 Principal principal,
                                 HttpSession session){

        try {

            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            if(file.isEmpty()){
//                File empty message
                System.out.println("Sry file is empty ...");
                contact.setImage("contact.svg");
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
//            Message success
            session.setAttribute("message", new Message("Your contact is added !!! Add more ...", "success"));

        }catch (Exception exception){
//            Message success
            session.setAttribute("message", new Message("Something went wrong !!! Try again ...", "danger"));
            System.out.println("Error: "+ exception.getMessage());
        }
        return "normal/add_contact_form";
    }

    @GetMapping("/show_contacts/{page}")
//    Per page = 5[n]
//    Current page  = 0 (starts form 0)
    public String showContacts(@PathVariable("page") Integer page ,Model model, Principal principal){
        model.addAttribute("title", "Show user contacts");

        int recordsPerPage = 5;
//        To get the userId
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

//        Easy way of fetching the list of contacts. But I need to implement the pagination later on hence using repository
//        List<Contact> contact = user.getContact();

        Pageable pageable = PageRequest.of(page, recordsPerPage);

        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    @GetMapping("{cId}/contact")
    public String showUserContactDetail(@PathVariable("cId") Integer cId, Model model){
        model.addAttribute("title", "User contact");
        System.out.println("Contact Id: "+cId);

        Optional<Contact> optionalContact = contactRepository.findById(cId);
        Contact contact = optionalContact.get();

        model.addAttribute("contact", contact);
        return "normal/contact_detail";
    }

}
