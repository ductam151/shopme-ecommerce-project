package dev.nhoxtam151.admin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nhoxtam151.admin.exceptions.UserNotFoundException;
import dev.nhoxtam151.admin.models.SirvUrl;
import dev.nhoxtam151.admin.repositories.RoleRepository;
import dev.nhoxtam151.admin.repositories.UserRepository;
import dev.nhoxtam151.admin.utils.SirvImageUtils;
import dev.nhoxtam151.admin.utils.TokenImageUtils;
import dev.nhoxtam151.shopmecommon.models.Role;
import dev.nhoxtam151.shopmecommon.models.User;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserService implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenImageUtils imageUtils;
    private final SirvUrl sirvUrl;
    private ApplicationContext applicationContext;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RestTemplate restTemplate,
                       SirvUrl sirvUrl,
                       ObjectMapper objectMapper, RoleRepository roleRepository, @Qualifier("sirvImageUtils") TokenImageUtils imageUtils, SirvImageUtils sirvImageUtils, SirvUrl sirvUrl1) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.imageUtils = imageUtils;
        this.sirvUrl = sirvUrl1;
    }

    public Page<User> listFirstPage() {
        Page<User> pageUser = userRepository.findAll(PageRequest.of(0, 4));
        return pageUser;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Not found user with id " + id));
    }

    public User save(User user, MultipartFile fileImage) throws IOException {
        if (user.getId() == null && existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (!fileImage.isEmpty() && user.getPhoto() == null) {
            user.setPhoto(user.getFullName() + "_avatar");
            uploadImage(user.getPhoto(), fileImage.getBytes());
        }
        if (user.getId() == null) { //create new user case
            String encode = passwordEncoder.encode(user.getPassword());
            user.setPassword(encode);
            user.setPhoto("default_avatar.jpg");
        } else if (user.getId() != null && userRepository.countById(user.getId()) > 0) { //updating user
            User userById = userRepository.findById(user.getId())
                    .orElseThrow(() -> new UserNotFoundException("Not found user with id " + user.getId()));
            if (user.equals(userById)) {
                return userById;
            }
            if (user.getPassword().isEmpty()) { //user don't want to change current password
                user.setPassword(userById.getPassword());
            } else { //update new password
                String encode = passwordEncoder.encode(user.getPassword());
                user.setPassword(encode);
            }
            if (user.getPhoto() == null) {
                user.setPhoto(userById.getPhoto());
            }
        }
        return userRepository.save(user);
    }

    public User saveOAuth2User(OAuth2LoginAuthenticationToken loginUser, String type) {
        if (type.equals("google")) {
            OAuth2User principal = loginUser.getPrincipal();
            String firstName = (String) principal.getAttributes().get("given_name");
            String lastName = (String) principal.getAttributes().get("family_name");
            String email = (String) principal.getAttributes().get("email");
            String picture = (String) principal.getAttributes().get("picture");
            User user = new User(email, firstName, lastName, picture, List.of(roleRepository.findByName(Role.RoleType.USER).orElseThrow(() -> new RuntimeException("Can not find role with id " + 5l))));
            userRepository.save(user);
        }
        return null;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteById(Long id) {
        Long userCount = userRepository.countById(id);
        if (userCount == null || userCount == 0) {
            throw new UserNotFoundException("Could not delete user with id " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void updateUserStatus(Long id, boolean enabled) {
        Long userDb = userRepository.countById(id);
        if (userDb == null || userDb == 0) {
            throw new UserNotFoundException("Could not update user with id " + id);
        }
        userRepository.updateEnabledStatus(id, enabled);
    }

    public InputStream retrieveImage(String imageName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return retrieveImageSirv(imageName);
    }

    public InputStream retrieveImageSirv(String imageName) {
        String url = sirvUrl.getUrl().get("get.user") + imageName;
        return imageUtils.retrieveImage(url);
    }

    public void uploadImage(String imageName, byte[] image) {
        uploadImageSirv(imageName, image);
    }

    public void uploadImageSirv(String imageName, byte[] image) {
        String url = sirvUrl.getUrl().get("post.user") + imageName;
        imageUtils.uploadImage(url, image);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAll(int page, int pageSize) {
        return userRepository.findAll(PageRequest.of(page - 1, pageSize));
    }

    public Page<User> findAllAndSorting(int page, int pageSize, String sortField, String sortDirection) {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("desc") ? sort.descending() : sort.ascending();
        return userRepository.findAll(PageRequest.of(page - 1, pageSize, sort));
    }

    public Page<User> findAllByKeywordAndSorting(String keyword, int page, int pageSize, String sortField, String sortDirection) {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("desc") ? sort.descending() : sort.ascending();
        return userRepository.findAll(keyword, PageRequest.of(page - 1, pageSize, sort));
    }

    public void exportToCSV(String keyword, HttpServletResponse response) throws IOException {
        AbstractUserExporter userCSVExporter = applicationContext.getBean("userCSVExporter", UserCSVExporter.class);
        List<User> users = userRepository.findAll(keyword, null).getContent();
        String fileName = createFileNameForUserExporter(keyword);
        userCSVExporter.export(fileName, users, response);
    }

    public void exportToExcel(String keyword, HttpServletResponse response) throws IOException {
        AbstractUserExporter userExcelExporter = applicationContext.getBean("userExcelExporter", UserExcelExporter.class);
        List<User> users = userRepository.findAll(keyword, null).getContent();
        String fileName = createFileNameForUserExporter(keyword);
        userExcelExporter.export(fileName, users, response);
    }

    public void exportToPdf(HttpServletResponse response) throws IOException {
        String fileName = createFileNameForUserExporter(null);
        AbstractUserExporter userPdfExporter = applicationContext.getBean("userPdfExporter", UserPdfExporter.class);
        List<User> users = userRepository.findAll(Sort.by("firstName").ascending());
        userPdfExporter.export(fileName, users, response);
    }


    public String createFileNameForUserExporter(String keyword) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        String fileName = "users_";
        fileName += keyword != null ? "keyword=" + keyword + "_" : "";
        fileName += timestamp;
        return fileName;
    }

}
