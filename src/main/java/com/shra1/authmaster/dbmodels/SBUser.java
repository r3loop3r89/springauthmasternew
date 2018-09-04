package com.shra1.authmaster.dbmodels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SBUser {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   long sbuid;
   String name;
   String email;

   public long getSbuid() {
      return sbuid;
   }

   public void setSbuid(long sbuid) {
      this.sbuid = sbuid;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public SBUser() {
   }

   public SBUser(String name, String email) {
      this.name = name;
      this.email = email;
   }
}
