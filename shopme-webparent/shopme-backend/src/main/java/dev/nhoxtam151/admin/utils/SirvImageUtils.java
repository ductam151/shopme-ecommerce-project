package dev.nhoxtam151.admin.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class SirvImageUtils implements ImageUtils {
    private final Logger log = LoggerFactory.getLogger(SirvImageUtils.class);

    public void uploadImage(String url, String token, byte[] image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            HttpResponse<String> response = Unirest.post(url)
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer " + token)
                    .body(image)
                    .asString();
        } catch (UnirestException e) {
            log.error("uploadImage: {}", e.getMessage());
        }
    }

    public InputStream retrieveImage(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        try {
            headers.set("Authorization", "Bearer " + token);
            HttpResponse<String> response = Unirest.get(url)
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer " + token)
                    .asString();
            return response.getRawBody();
        } catch (UnirestException e) {
            log.error("retrieveImage: {}", e.getMessage());
            return null;
        }
    }

}
