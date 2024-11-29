package dev.nhoxtam151.admin.config;

import dev.nhoxtam151.admin.models.UserDecorator;
import dev.nhoxtam151.admin.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.stereotype.Component;


@Component
public class MyAuthenticationSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final Logger logger = LoggerFactory.getLogger(MyAuthenticationSuccessHandler.class);

    public MyAuthenticationSuccessHandler(UserService userService, UserDetailsService userDetailsService, ProjectInfoAutoConfiguration projectInfoAutoConfiguration) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        if (event.getSource() instanceof OAuth2LoginAuthenticationToken token) {
            if (token.getClientRegistration().getRegistrationId().equals("google")) {
                try {
                    userDetailsService.loadUserByUsername(token.getPrincipal().getAttribute("email"));
                } catch (UsernameNotFoundException e) {
                    userService.saveOAuth2User(token, "google");
                }
                logger.info("User logged in via Google: " + token.getPrincipal().getAttribute("email"));
            }
        } else {
            UserDecorator userDetails = (UserDecorator)event.getAuthentication().getPrincipal();
            logger.info("User logged in: " + userDetails.getFullName() + " authorities: " + userDetails.getAuthorities());
        }
    }
}
