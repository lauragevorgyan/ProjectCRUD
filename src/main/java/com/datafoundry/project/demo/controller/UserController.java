package com.datafoundry.project.demo.controller;

import com.datafoundry.project.demo.model.ConfirmationToken;
import com.datafoundry.project.demo.repo.ConfirmationTokenRepository;
import com.datafoundry.project.demo.service.EmailSenderService;
import com.datafoundry.project.demo.service.TimeService;
import com.datafoundry.project.demo.service.UserService;
import com.datafoundry.project.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
/*
    -- Controller main part programm
 */
@Controller
public class UserController {  //-- Main controller
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    // -- link to pages

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        return "ind";
    }

    @GetMapping(value = "/login")
    public String login(Model model,
                        @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
        return "login";
    }

    @GetMapping(value = "/registr")
    public String registration(Model model,
                               @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
        return "registr";
    }

    // -- Add user

    @GetMapping(value = {"/add"})
    public String showAddUser(Model model) {
        User user = new User();
        model.addAttribute("add", true);
        model.addAttribute("user", user);

        return "index";
    }
    @PostMapping(value = "/add")
    public String addUser(Model model,
                          @ModelAttribute("user") User user) {
        try {
            // -- if user want registr with email admin@gmail.com

            if(user.getEmail().matches("admin@gmail.com")){
                System.out.println("We have admin !");
                model.addAttribute("error","We have admin !");
                return "notsuc";
            }

            // -- if password length <5

            if(user.getPassword().length()<5){
                model.addAttribute("error","Password must be more than 5 characters\n" +
                        "\n");
                return "notsuc";
            }
            if(userService.loadUserByUsername(user.getEmail()) == null){  // -- check DB has matches email or not
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // -- Crypting password (Spring Crypting)
                user.setPassword(passwordEncoder.encode(user.getPassword()));


                // -- Email verify part doesn't work becouse sql database
                /*
                ConfirmationToken confirmationToken = new ConfirmationToken(user);

                confirmationTokenRepository.save(confirmationToken);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setSubject("Complete Registration!");
                mailMessage.setFrom("gevorgyanlaura96@gmail.com");
                mailMessage.setText("To confirm your account, please click here : "
                        +"http://localhost:8082/confirm-account?token="+confirmationToken.getConfirmationToken());
                emailSenderService.sendEmail(mailMessage);*/

                //-- Save user to User DB
                userService.save(user); // Save to DB
                return "activated"; // if completed successfully
            }else{
                System.out.println("DB has matches user !");
                model.addAttribute("error","DB has matches user !");
                return "notsuc"; // if DB has matches email
            }
        } catch (Exception ex) {
            System.out.println("Add failed !");
            return "notsuc";
        }
    }

    // -- Check DB has User or Admin

    // -- Login Part

    @GetMapping(value = "/check")
    public String CheckUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "success";
    }

    @PostMapping(value = "/check")
    public String CheckUsers(Model model,
                          @ModelAttribute("user") User user) {
        List<User> users = userService.findAll();
        if(user.getEmail().equals("admin@gmail.com") && user.getPassword().equals("admin")){
            List<User> userList = userService.findAll();
            model.addAttribute("users", userList);
            return "AdminPage";
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            for(int i=0;i<users.size();i++){
                if((users.get(i).getEmail().equals( user.getEmail())) && (bCryptPasswordEncoder.matches(user.getPassword(),users.get(i).getPassword()))){

                    // -- Get time and Save to Table
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String currentTime = ""+dtf.format(now);
                    String userId = ""+users.get(i).getId().toString();
                    try {
                        timeService.save(userId,currentTime);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    // -- END

                    List<String> time = timeService.findAll(users.get(i).getId().toString());
                    model.addAttribute("times",time); // -- Move times to UserPage
                    return "UserPage";
                }
            }
            model.addAttribute("error","User exist !");
        return "notsuc";
    }

    // -- Read all Users

    @GetMapping(value = "/read")
    public String getUsers(Model model,
                           @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "AdminPage";
    }

    // -- Read using userId

    @GetMapping(value = "/read/{userId}")
    public String getUserById(Model model, @PathVariable long userId) {
        User user = null;
        try {
            user = userService.findById(userId);
            model.addAttribute("allowDelete", false);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("user", user);
        return "user";
    }

    // -- Delete User

    @GetMapping(value = {"/read/{userId}/delete"})
    public String showDeleteUser(
            Model model, @PathVariable long userId) {
        User user = null;
        try {
            user = userService.findById(userId);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("allowDelete", true);
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping(value = {"/read/{userId}/delete"})
    public String deleteUserById(
            Model model, @PathVariable long userId) {
        try {
            userService.deleteById(userId);
            return "/AdminPage";
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "success";
        }
    }

}
