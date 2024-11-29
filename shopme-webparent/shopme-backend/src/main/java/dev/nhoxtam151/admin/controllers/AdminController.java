package dev.nhoxtam151.admin.controllers;

import dev.nhoxtam151.admin.models.UserDecorator;
import dev.nhoxtam151.admin.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class AdminController {
    private Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final UserUtils userUtils;

    public AdminController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @ModelAttribute(name = "username")
    public String username(Authentication authentication) {
        return userUtils.getUsername() == null ?  "" : userUtils.getUsername();
    }

    @GetMapping
    public String viewHomePage(Model model) {
        logger.info("Username: {}", model.getAttribute("username"));
        return "index";
    }
}
