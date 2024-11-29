package dev.nhoxtam151.admin.controllers;

import dev.nhoxtam151.admin.models.LoginUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @ModelAttribute
    public LoginUserDTO userDTO() {
        return new LoginUserDTO();
    }

    @GetMapping
    public String showLoginPage(Authentication authentication) {
//        if (authentication.getPrincipal() != null) {
//            return "redirect:/";
//        }
        return "login";
    }


    @PostMapping
    public String processLogin(LoginUserDTO loginUserDTO) {
        logger.info("User with email " + loginUserDTO.getEmail() + " has logged in.");
        return "redirect:/users";
    }

}
