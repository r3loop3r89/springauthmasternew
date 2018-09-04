package com.shra1.authmaster.controller;

import com.shra1.authmaster.dbmodels.*;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Rest {

   Logger logger = Logger.getLogger(Rest.class);

   @Autowired
   UserRepository userRepository;

   @Autowired
   SBUserRepository sbUserRepository;

   @Autowired
   SBMessageRepository sbMessageRepository;

   //<editor-fold desc="No Auth APIs">
   @RequestMapping("/addUser")
   public void addUser(@RequestBody User user) {
      logger.info(user);
      BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
      String cryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
      user.setPassword(cryptedPassword);
      userRepository.save(user);
   }

   @RequestMapping(value = "/showAllUsers", method = RequestMethod.GET)
   public List<User> showAllUsers() {
      return userRepository.findAll();
   }

   @GetMapping("/showByName/{name}")
   public List<User> showUserByName(@PathVariable("name") String name) {
      return userRepository.findByNameDistinct(name);
   }

   @RequestMapping("/addSBUser")
   public void addSBUser(@RequestBody SBUser sbUser) {
      sbUserRepository.save(sbUser);
   }

   @RequestMapping("/loginSBUser/{email}")
   public SBUser loginSBUser(@PathVariable("email") String email) {
      logger.info("email : " + email);
      SBUser sbUser = sbUserRepository.findByEmail(email);
      logger.info(sbUser);
      return sbUser;
   }

   @RequestMapping("/getAllSBUsers")
   public List<SBUser> getAllSBUsers() {
      return sbUserRepository.findAll();
   }

   @RequestMapping("/addSBMessage")
   public void addSBMessage(@RequestBody SBMessage sbMessage) {
      sbMessageRepository.save(sbMessage);
   }

   @RequestMapping("/getAllSBMessages")
   public List<SBMessage> getAllSBMessages() {
      return sbMessageRepository.findAll();
   }
   //</editor-fold>


   //<editor-fold desc="Auth APIs">
   @RequestMapping("/auth/addUser")
   public void addUserAuth(@RequestBody User user) {
      logger.info(user);
      BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
      String cryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
      user.setPassword(cryptedPassword);
      userRepository.save(user);
   }


   @RequestMapping(value = "/auth/showAllUsers", method = RequestMethod.GET)
   public List<User> showAllUsersAuth() {
      return userRepository.findAll();
   }
   //</editor-fold>

}
