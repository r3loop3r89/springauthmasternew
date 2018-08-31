package com.shra1.authmaster.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class MyResponseWriterUtil {
   public static void write(HttpServletResponse response, Object message) {
      PrintWriter writer = null;
      ObjectMapper objectMapper = new ObjectMapper();
      try {
         writer = response.getWriter();
         objectMapper.writeValue(writer, message);
         response.flushBuffer();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         writer.close();
      }
   }
}
