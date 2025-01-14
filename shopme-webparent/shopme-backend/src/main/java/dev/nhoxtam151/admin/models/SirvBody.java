package dev.nhoxtam151.admin.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nhoxtam151.admin.aop.annotations.LimitToken;
import dev.nhoxtam151.admin.utils.SirvCredentials;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SirvBody {
    private final SirvUrl sirvUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SirvCredentials credentials;
    private String clientId;
    private String clientSecret;

    public SirvBody(SirvUrl sirvUrl, RestTemplate restTemplate, ObjectMapper objectMapper, SirvCredentials credentials) {
        this.sirvUrl = sirvUrl;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.credentials = credentials;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @LimitToken
    public String getToken() {
        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.setContentType(MediaType.APPLICATION_JSON);
        String uriTemplate = UriComponentsBuilder.fromUriString(sirvUrl.getUrl().get("token"))
                .toUriString();
        HttpEntity<SirvCredentials> httpEntity = new HttpEntity<>(credentials, tokenHeader);
        String exchange = restTemplate.postForObject(uriTemplate, httpEntity, String.class);
        JsonNode root = null;
        try {
            root = objectMapper.readTree(exchange);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return root.path("token").asText();
    }


}
