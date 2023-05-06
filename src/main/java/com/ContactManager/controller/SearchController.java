package com.ContactManager.controller;

import com.ContactManager.dao.ContactRepository;
import com.ContactManager.dao.UserRepository;
import com.ContactManager.entities.Contact;
import com.ContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal)
    {
        System.out.println(query);
        User user=this.userRepository.getUserByUserName(principal.getName());
        System.out.println("Logged in User Id: "+ user.getId());
        List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user.getId());
        System.out.println("Contact list: "+ contacts);
        return ResponseEntity.ok(contacts);
    }
}
