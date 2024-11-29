package dev.nhoxtam151.admin.utils;

import dev.nhoxtam151.admin.models.UserDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (isPrincipalTypeOfUserDetails(principal)) {
            UserDecorator user = (UserDecorator) principal;
            return user.getFullName();
        } else if (principal instanceof OAuth2User user) {
            return user.getAttributes().get("name").toString();
        }
        return null;
    }

    public boolean isPrincipalTypeOfUserDetails(Object principal) {
        return principal instanceof UserDetails;
    }
}
