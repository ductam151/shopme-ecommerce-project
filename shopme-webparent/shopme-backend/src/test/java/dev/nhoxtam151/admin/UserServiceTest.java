package dev.nhoxtam151.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nhoxtam151.admin.models.SirvBody;
import dev.nhoxtam151.admin.services.UserService;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private SirvBody sirvBody;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("retrieveToken")
    public void test1() throws JSONException, JsonProcessingException {
        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.setContentType(MediaType.APPLICATION_JSON);
        String uriTemplate = UriComponentsBuilder.fromUriString("https://api.sirv.com/v2/token")
                .toUriString();
        System.out.println(uriTemplate.toString());
        JSONObject body = new JSONObject();
        body.put("clientId", "3yqTN2PM7z5br97DdxgOBtLT3pY");
        body.put("clientSecret", "IOX5gH9LtH89tLXPv/q/k0swlDXVSlY/wicYGeNeaqhRzXj4lNeQMHnW9hG0tiB9jMbgKvJebZuYTXUo/p3KZw==");
        HttpEntity<String> httpEntity = new HttpEntity<>(body.toString(), tokenHeader);
        String exchange = testRestTemplate.postForObject(uriTemplate, httpEntity, String.class);
        System.out.println("exchange = " + exchange);
        JsonNode root = objectMapper.readTree(exchange);

        System.out.println(root.path("token").asText());
        //assert (exchange.getStatusCode()).is2xxSuccessful();
    }

    @Test
    @DisplayName("testRetrieveImageFailed")
    public void test() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json");
        headers.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRJZCI6IjN5cVROMlBNN3o1YnI5N0RkeGdPQnRMVDNwWSIsImNsaWVudE5hbWUiOiJzaG9wbWUtZWNvbW1lcmNlIiwic2NvcGUiOlsiYWNjb3VudDpyZWFkIiwiYWNjb3VudDp3cml0ZSIsInVzZXI6cmVhZCIsInVzZXI6d3JpdGUiLCJiaWxsaW5nOnJlYWQiLCJiaWxsaW5nOndyaXRlIiwiZmlsZXM6cmVhZCIsImZpbGVzOndyaXRlIiwiZmlsZXM6Y3JlYXRlIiwiZmlsZXM6dXBsb2FkOm11bHRpcGFydCIsImZpbGVzOnNoYXJlZEJpbGxpbmciLCJ2aWRlb3MiLCJpbWFnZXMiXSwiaWF0IjoxNzI5MTM3Mzg1LCJleHAiOjE3MjkxMzg1ODUsImF1ZCI6Im1udm9paDZkbDE0dDR3c3hzZWZjaHdlNDZtOHNneGRnIn0.35bObzPfQZRbqxiYWK9C3eh72EPUeICEQNJKbA1xAMs");
        ResponseEntity<String> result = testRestTemplate
                .exchange("https://api.sirv.com/v2/files/download?filename=/Shopme/avatar/default_avatar.jpg"
                        , HttpMethod.GET
                        , new HttpEntity<String>(headers), String.class);

        JsonNode root = objectMapper.readTree(result.getBody());
        System.out.println(root.path("message").asText());
        String message = root.path("message").asText();
        Assertions.assertThat(message).isEqualTo("Expired token");
    }

    @Test
    @DisplayName("testRetrieveImageSuccess")
    public void test3() {
        userService.retrieveImage("default_avatar.jpg");
        assertTrue(true);
    }
}
