package dev.nhoxtam151.admin.controllers;

import dev.nhoxtam151.admin.exceptions.UserNotFoundException;
import dev.nhoxtam151.shopmecommon.models.User;
import dev.nhoxtam151.admin.repositories.RoleRepository;
import dev.nhoxtam151.admin.services.UserService;
import dev.nhoxtam151.admin.utils.UserUtils;
import dev.nhoxtam151.shopmecommon.models.Role;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    private final int PAGE_SIZE = 4;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserUtils userUtils;
    private Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, RoleRepository roleRepository, UserUtils userUtils) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.userUtils = userUtils;
    }

    @ModelAttribute(name = "username")
    public String username(Authentication authentication) {
        return userUtils.getUsername() == null ? "" : userUtils.getUsername();
    }

    @ModelAttribute(name = "rolesList")
    public List<Role> roles() {
        return roleRepository.findAll();
    }


    @GetMapping
    public String showUsersPage(HttpServletResponse response, Model model) {
        response.setHeader("Pragma-directive", "no-cache");
        response.setHeader("Cache-directive", " no-cache");
        response.setHeader("Cache-control", " no-cache");
        response.setHeader("Pragma", " no-cache");
        response.setHeader("Expires", " 0");
        return listByPage(1, "id", "asc", null, model);
    }

    @PostMapping("/new")
    public String processSaveUser(@Valid User user, Errors errors, Model model, RedirectAttributes redirectAttributes, @RequestParam(name = "image") MultipartFile image) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Create new user");
            return "user_form";
        }
        try {

            userService.save(user, image);
        } catch (Exception e) {
            model.addAttribute("emailTakenError", e.getMessage());
            model.addAttribute("title", "Create New User");
            return "user_form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "The user has been saved successfully.");
        return "redirect:/users";
    }


    @GetMapping("/new")
    public String showCreateUserPage(@RequestParam(required = false) Long id, Model model, @ModelAttribute("user") User user) {
        if (id != null) {
            user = userService.findById(id);
        }
        model.addAttribute("title", "Create New User");
        model.addAttribute("user", user);
        return "user_form";
    }

    @GetMapping("/edit/{userId}")
    public String showEditUserPage(@PathVariable Long userId, Model model, RedirectAttributes redirectAttributes, HttpServletResponse response) {

        try {
            User user = userService.findById(userId);
            model.addAttribute("user", user);
            model.addAttribute("title", "Edit User (ID: " + user.getId() + ")");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            return "user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/delete/{userId}")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(userId);
            redirectAttributes.addFlashAttribute("successMessage"
                    , "The user ID " + userId + " has been deleted successfully.");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/{userId}/enabled/{status}")
    public String updateUserStatus(@PathVariable Long userId, @PathVariable boolean status,
                                   RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserStatus(userId, status);
            String enabled = status ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("successMessage",
                    "The user ID " + userId + " has been " + enabled);
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @ResponseBody
    @GetMapping(value = {"/images/{name}"}, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] showImagePage(@PathVariable String name, Model model) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage image = ImageIO.read(userService.retrieveImage(name));
        ImageIO.write(image, "jpg", os);
        return os.toByteArray();
    }

    @GetMapping("/images")
    public String showPublishImagePage(Model model) {
        return "image_form";
    }

    @PostMapping("/images")
    public String processImageUpload(@RequestParam("image") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename().replace(' ', '_');
        userService.uploadImage(fileName, file.getBytes());
        return "image_form";
    }

    @GetMapping("/page/{page}")
    public String listByPage(@PathVariable Integer page,
                             @RequestParam(value = "sortField", defaultValue = "id", required = false) String sortField,
                             @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             Model model) {
        if (page < 1) {
            return "redirect:/users/page/1";
        }
        Page<User> pageUser = sortField == null ? userService.findAll(page, PAGE_SIZE) : userService.findAllAndSorting(page, PAGE_SIZE, sortField, sortDir);
        pageUser = keyword == null ? pageUser : userService.findAllByKeywordAndSorting(keyword, page, PAGE_SIZE, sortField, sortDir);
        List<User> users = pageUser.getContent();
        long totalElements = pageUser.getTotalElements();
        long startingNumber = (page - 1) * PAGE_SIZE + 1;
        long endingNumber = startingNumber + PAGE_SIZE - 1;
        int totalPages = pageUser.getTotalPages();
        if (page > totalPages) {
            page = totalPages;
        }
        if (page > pageUser.getTotalPages() && totalElements > 0) {
            return "redirect:/users/page/" + pageUser.getTotalPages();
        }
        String reverseDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startingNumber", startingNumber);
        model.addAttribute("endingNumber", endingNumber);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseDir", reverseDir);
        model.addAttribute("keyword", keyword);
        log.info("Keyword: {}", keyword);
        log.info("Username: {}", model.getAttribute("username"));
        return "user_page";
    }

    @GetMapping("/export/csv")
    public void exportToCSV(@RequestParam(required = false, defaultValue = "") String keyword, HttpServletResponse response) throws IOException {
        userService.exportToCSV(keyword, response);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(@RequestParam(required = false, defaultValue = "") String keyword, HttpServletResponse response) throws IOException {
        userService.exportToExcel(keyword, response);
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(@RequestParam(required = false, defaultValue = "") String keyword, HttpServletResponse response) throws IOException {
        userService.exportToPdf(response);
    }

}
