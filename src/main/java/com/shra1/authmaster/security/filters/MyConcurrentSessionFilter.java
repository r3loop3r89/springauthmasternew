package com.shra1.authmaster.security.filters;

import com.shra1.authmaster.security.dto.GenericResponse;
import com.shra1.authmaster.security.utils.MyResponseWriterUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class MyConcurrentSessionFilter extends ConcurrentSessionFilter {
   public static final int HTTP_STATUS_AUTHENTICATION_TIMEOUT = 419;
   SessionRegistry sessionRegistry;
   String expiredUrl;
   RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
   LogoutHandler[] handlers = new LogoutHandler[]{new SecurityContextLogoutHandler()};

   public MyConcurrentSessionFilter(SessionRegistry sessionRegistry) {
      super(sessionRegistry);
      Assert.notNull(sessionRegistry, "Session Registry Required");
      this.sessionRegistry = sessionRegistry;
   }

   public MyConcurrentSessionFilter(SessionRegistry sessionRegistry, String expiredUrl) {
      super(sessionRegistry, expiredUrl);
      Assert.notNull(sessionRegistry, "Session Registry Required");
      Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl),
        expiredUrl + " isn't a valide redirect URL");
      this.sessionRegistry = sessionRegistry;
      this.expiredUrl = expiredUrl;
   }

   @Override
   public void afterPropertiesSet() {
      Assert.notNull(sessionRegistry, "Session Registry Required");
      Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl),
        expiredUrl + " isn't a valide redirect URL");
   }

   @Override
   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;

      HttpSession session = request.getSession();

      if (session != null) {
         SessionInformation info = sessionRegistry.getSessionInformation(session.getId());
         if (info != null) {
            if (info.isExpired()) {
               doLogout(request, response);
               String targetUrl = determineExpiredUrl(request, info);

               if (targetUrl != null) {
                  redirectStrategy.sendRedirect(request, response, targetUrl);
                  return;
               } else {
                  response.setStatus(HTTP_STATUS_AUTHENTICATION_TIMEOUT);
                  MyResponseWriterUtil.write(response, new GenericResponse("This session has been expired (possibly due to multiple concurrent logins being attempted by the same user)."));
               }
               return;
            } else {
               sessionRegistry.refreshLastRequest(info.getSessionId());
            }
         }
      }
      chain.doFilter(request, response);
   }

   protected String determineExpiredUrl(HttpServletRequest request, SessionInformation info) {
      return expiredUrl;
   }

   private void doLogout(HttpServletRequest request, HttpServletResponse response) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      for (LogoutHandler handler : handlers) {
         handler.logout(request, response, auth);
      }
   }


   public void setLogoutHandlers(LogoutHandler[] handlers) {
      Assert.notNull(handlers);
      this.handlers = handlers;
   }

   public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
      this.redirectStrategy = redirectStrategy;
   }
}
