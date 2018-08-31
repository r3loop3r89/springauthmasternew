package com.shra1.authmaster.dbmodels;

import javax.persistence.*;

@Entity
public class User {
   @Id()
   @GeneratedValue(strategy = GenerationType.AUTO)
   long id;
   String name;
   String username;
   String password;
   String role;

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getRole() {
      return role;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public User(String name, String username, String password, String role) {
      this.name = name;
      this.username = username;
      this.password = password;
      this.role = role;
   }

   public User() {
   }
}
