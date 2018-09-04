package com.shra1.authmaster.dbmodels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SBMessage {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   long sbmid;
   String message;
   String sentByUserEmail;
   long sendOnEpoch;

   public long getSbmid() {
      return sbmid;
   }

   public void setSbmid(long sbmid) {
      this.sbmid = sbmid;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getSentByUserEmail() {
      return sentByUserEmail;
   }

   public void setSentByUserEmail(String sentByUserEmail) {
      this.sentByUserEmail = sentByUserEmail;
   }

   public long getSendOnEpoch() {
      return sendOnEpoch;
   }

   public void setSendOnEpoch(long sendOnEpoch) {
      this.sendOnEpoch = sendOnEpoch;
   }

   public SBMessage(String message, String sentByUserEmail, long sendOnEpoch) {
      this.message = message;
      this.sentByUserEmail = sentByUserEmail;
      this.sendOnEpoch = sendOnEpoch;
   }

   public SBMessage() {
   }
}
