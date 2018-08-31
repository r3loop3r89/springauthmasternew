package com.shra1.authmaster.security.dto;

public class GenericResponse {
   String message;

   public GenericResponse(String message) {
      this.message = message;
   }

   public GenericResponse() {
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
