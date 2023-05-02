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
                contact.setImage("contact.png");
                System.out.println("Default image is set ...");
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
    public String showUserContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal){
        System.out.println("Contact Id: "+cId);

        Optional<Contact> optionalContact = contactRepository.findById(cId);
        Contact contact = optionalContact.get();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if(user.getId() == contact.getUsers().getId()){
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getFirstName());
        }
//       Msg is handled in UI
//        else{
//            throw new RuntimeException("This user doesn't have the access to view this contact");
//        }
        return "normal/contact_detail";
    }

//    Delete contact handler
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId,
                                Model model,
                                HttpSession session,
                                Principal principal){

        Contact contact =this.contactRepository.findById(cId).get();

        User user = this.userRepository.getUserByUserName(principal.getName());

        user.getContact().remove(contact);

        this.userRepository.save(user);

//        This is because, I have configured "cascade = CascadeType.ALL" in user entity.
//        This method didn't delete the contacts from contacts table
//        Part of the solution was around user class . Added orphanRemoval and overriding equals func
//        contact.setUsers(null);
//        this.contactRepository.delete(contact);

/* TODO: Hit and trail validation*/

        System.out.println("Contact: "+ contact.getContactId());
        session.setAttribute("message", new Message("Contact deleted successfully...", "success"));

/* TODO: Remove the image of the deleted user*/

        return "redirect:/user/show_contacts/0";
    }

//    Open update form handler
    @PostMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cId, Model model){
        model.addAttribute("title", "Update Contact");

        Contact contact = this.contactRepository.findById(cId).get();
        model.addAttribute("contact",contact);
        return "normal/update_form";
    }

//    Update contact handler
    @RequestMapping(value="/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,
                                @RequestParam("profileImage") MultipartFile multipartFile,
                                Model model,
                                HttpSession session,
                                Principal principal){


        try{

            Contact oldContactDetail = this.contactRepository.findById(contact.getContactId()).get();
//            Image
            if(!multipartFile.isEmpty()){
//          Delete old photo
                File deleteFile = new ClassPathResource("/static/img").getFile();
                File file1 = new File(deleteFile, oldContactDetail.getImage());
                file1.delete();

//          Add a new photo

                File saveFile = new ClassPathResource("/static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
                Files.copy(multipartFile.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(multipartFile.getOriginalFilename());

            }else {
                contact.setImage(oldContactDetail.getImage());
            }

            User user = this.userRepository.getUserByUserName(principal.getName());
            contact.setUsers(user);
            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Your contact is updated ...", "success"));

        }catch(Exception exception){

        }

        System.out.println("Contact Name: "+ contact.getFirstName());
        System.out.println("Contact Id: "+ contact.getContactId());

        return "redirect:/user/"+contact.getContactId()+"/contact";
    }

}
