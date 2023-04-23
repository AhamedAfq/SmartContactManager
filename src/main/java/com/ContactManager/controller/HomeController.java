package com.ContactManager.controller;

import com.ContactManager.dao.UserRepository;
import com.ContactManager.entities.User;
import com.ContactManager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController  {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

//    @GetMapping("/test")
//    @ResponseBody
//    public String test(){
//        User user = new User();
//        user.setName("AhamedAfq");
//        user.setEmail("ashfakahamed142@gmail.com");
//        userRepository.save(user);
//        return "Its Working...";
//    }

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }
    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }

//    I have to add method = {RequestMethod.GET, RequestMethod.POST} to the Request if /signin is my processing url also.
    @RequestMapping(value = "/signin", method = {RequestMethod.GET, RequestMethod.POST})
    public String customLogin(Model model){
        model.addAttribute("title", "Login Page");
        return "login";
    }

//    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
//    public String customLoginProcessingUrl(Model model){
//        model.addAttribute("title", "Login Page");
//        return "user_dashboard";
//    }

    // Handler for registering new users
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(value = "agreement",
            defaultValue = "false") boolean agreement,
            Model model,
            HttpSession session){

        try {

            if(!agreement){
                System.out.println("You have not agreed the terms and conditions");
                throw new Exception("You have not agreed the terms and conditions");
            }

            if(bindingResult.hasErrors()){
                System.out.println("Error: "+ bindingResult.toString());
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setStatus(true);
            user.setImageUrl("default.png ");
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            System.out.println("User: "+ user);
            System.out.println("Agreement: "+ agreement);

            User result = this.userRepository.save(user);

            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Registration is successful !!!", "alert-success"));
            return "signup";

        }catch (Exception exception){

            exception.printStackTrace();

            //To show the error to the User
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !!! " + exception.getMessage(), "alert-danger"));
            return "signup";
        }
    }
}
