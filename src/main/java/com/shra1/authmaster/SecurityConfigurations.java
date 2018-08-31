package com.shra1.authmaster;

import com.shra1.authmaster.security.dto.LoginFailureResponse;
import com.shra1.authmaster.security.dto.LoginSuccessResponse;
import com.shra1.authmaster.security.filters.CustomUsernamePasswordAuthenticationFilter;
import com.shra1.authmaster.security.filters.MyConcurrentSessionFilter;
import com.shra1.authmaster.security.utils.MyResponseWriterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

   @Autowired
   SessionRegistry sessionRegistry;
   @Autowired
   CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy;
   @Autowired
   MyConcurrentSessionFilter myConcurrentSessionFilter;

   @Autowired
   @Qualifier("customUserDetailsService")
   private UserDetailsService userDetailsService;
   @Autowired
   private BCryptPasswordEncoder bCryptPasswordEncoder;

   @Override
   protected void configure(HttpSecurity http) throws Exception {
      //super.configure(http);
      http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/", "/login", "/logout")
        .permitAll()

        .and()
        .authorizeRequests()
        .antMatchers("/auth/**")
        .authenticated()

        .and()
        .sessionManagement()
        .sessionAuthenticationStrategy(compositeSessionAuthenticationStrategy);
      http.addFilter(myConcurrentSessionFilter);
   }

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
   }

   @Bean
   @Override
   public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
   }

   @Bean
   public CustomUsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
      CustomUsernamePasswordAuthenticationFilter authenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
      authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
      authenticationFilter.setAuthenticationManager(authenticationManagerBean());

      //SUCCESS HANDLER
      authenticationFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
         if (authentication.isAuthenticated()) {
            LoginSuccessResponse loginSuccessResponse = new LoginSuccessResponse();
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
               User securityUserDetails = (User) principal;
               loginSuccessResponse.setUsername(securityUserDetails.getUsername());
               try {
                  response.setContentType("application/json");
                  response.setStatus(HttpServletResponse.SC_OK);
                  MyResponseWriterUtil.write(response, loginSuccessResponse);
               } catch (AuthenticationServiceException e) {
                  request.getSession().invalidate();
                  response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                  e.printStackTrace();
               }
            }

         } else {
            throw new UnsupportedOperationException("Invalid Login");
         }
      });

      //FAILURE HANDLER
      authenticationFilter.setAuthenticationFailureHandler((request, response, exception) -> {

         LoginFailureResponse loginFailureResponse = new LoginFailureResponse();
         if (exception instanceof BadCredentialsException) {
            loginFailureResponse.setMessage(exception.getMessage());
         } else if (exception instanceof DisabledException) {
            loginFailureResponse.setMessage(exception.getMessage());
         } else if (exception instanceof AuthenticationServiceException) {
            loginFailureResponse.setMessage(exception.getMessage());
         }
         MyResponseWriterUtil.write(response, loginFailureResponse);

      });

      authenticationFilter.setSessionAuthenticationStrategy(compositeSessionAuthenticationStrategy);
      return authenticationFilter;
   }

   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
      BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
      return bCryptPasswordEncoder;
   }

   @Bean
   SessionRegistry sessionRegistry() {
      SessionRegistry sessionRegistry = new SessionRegistryImpl();
      return sessionRegistry;
   }

   @Bean
   MyConcurrentSessionFilter myConcurrentSessionFilter() {
      MyConcurrentSessionFilter myConcurrentSessionFilter
        = new MyConcurrentSessionFilter(sessionRegistry);
      return myConcurrentSessionFilter;
   }

   @Bean
   CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy() {
      List<SessionAuthenticationStrategy> delegateStrategies = new ArrayList<>();

      ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
      concurrentSessionControlAuthenticationStrategy.setMaximumSessions(1);
      delegateStrategies.add(concurrentSessionControlAuthenticationStrategy);
      delegateStrategies.add(new SessionFixationProtectionStrategy());
      delegateStrategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistry));

      CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy
        = new CompositeSessionAuthenticationStrategy(delegateStrategies);

      return compositeSessionAuthenticationStrategy;
   }
}