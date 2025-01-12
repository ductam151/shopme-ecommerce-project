package dev.nhoxtam151.admin.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dev.nhoxtam151.admin.aop.annotations.LimitToken;
import dev.nhoxtam151.admin.exceptions.UserNotFoundException;
import dev.nhoxtam151.admin.models.SirvBody;
import dev.nhoxtam151.admin.models.SirvUrl;
import dev.nhoxtam151.admin.models.User;
import dev.nhoxtam151.admin.repositories.UserRepository;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final RestTemplate restTemplate;
    private final SirvBody sirvBody;
    private final SirvUrl sirvUrl;
    private final ObjectMapper objectMapper;
    private final UserExporter userCSVExporter;
    private final UserExporter userExcelExporter;
    private ApplicationContext applicationContext;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RestTemplate restTemplate, SirvBody sirvBody, SirvUrl sirvUrl, ObjectMapper objectMapper, @Qualifier("userCSVExporter") UserExporter userCSVExporter, @Qualifier("userExcelExporter") UserExporter userExcelExporter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.sirvBody = sirvBody;
        this.sirvUrl = sirvUrl;
        this.objectMapper = objectMapper;
        this.userCSVExporter = userCSVExporter;
        this.userExcelExporter = userExcelExporter;
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
            user.setPhoto(user.getId() + "_avatar");
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

    @LimitToken
    public String retrieveToken() {
        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.setContentType(MediaType.APPLICATION_JSON);
        String uriTemplate = UriComponentsBuilder.fromUriString(sirvUrl.getUrl().get("token"))
                .toUriString();
        HttpEntity<SirvBody> httpEntity = new HttpEntity<>(sirvBody, tokenHeader);
        String exchange = restTemplate.postForObject(uriTemplate, httpEntity, String.class);
        JsonNode root = null;
        try {
            root = objectMapper.readTree(exchange);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return root.path("token").asText();
    }

    public InputStream retrieveImage(String imageName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        UserService userService = applicationContext.getBean(UserService.class);
        String token = userService.retrieveToken();
        try {
            headers.set("Authorization", "Bearer " + token);
            HttpResponse<String> response = Unirest.get(sirvUrl.getUrl().get("get") + imageName)
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer " + token)
                    .asString();
            return response.getRawBody();
        } catch (UnirestException e) {
            log.error("UserService.retrieveImage: {}", e.getMessage());
            return null;
        }
    }

    public void uploadImage(String imageName, byte[] image) {
        String token = applicationContext.getBean(UserService.class).retrieveToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            HttpResponse<String> response = Unirest.post(sirvUrl.getUrl().get("post") + imageName)
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer " + token)
                    .body(image)
                    .asString();
        } catch (UnirestException e) {
            log.error("UserService.uploadImage: {}", e.getMessage());
        }
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
        List<User> users = userRepository.findAll(keyword, null).getContent();
        String fileName = createFileNameForUserExporter(keyword);
        userCSVExporter.export(fileName, users, response);
    }

    public void exportToExcel(String keyword, HttpServletResponse response) throws IOException {
        List<User> users = userRepository.findAll(keyword, null).getContent();
        String fileName = createFileNameForUserExporter(keyword);
        userExcelExporter.export(fileName, users, response);
    }

    public String createFileNameForUserExporter(String keyword) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        String fileName = "users_";
        fileName += !keyword.equals("") ? "keyword=" + keyword + "_" : "";
        fileName += timestamp;
        return fileName;
    }

}
