package dev.nhoxtam151.admin.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dev.nhoxtam151.admin.aop.annotations.LimitToken;
import dev.nhoxtam151.admin.models.SirvBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class SirvImageUtils implements TokenImageUtils {
    private final SirvBody sirvBody;
    private final Logger log = LoggerFactory.getLogger(SirvImageUtils.class);

    public SirvImageUtils(SirvBody sirvBody) {
        this.sirvBody = sirvBody;
    }

    public String getToken() {
        return sirvBody.getToken();
    }

    public void uploadImage(String url, byte[] image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            HttpResponse<String> response = Unirest.post(url)
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer " + getToken())
                    .body(image)
                    .asString();
        } catch (UnirestException e) {
            log.error("uploadImage: {}", e.getMessage());
        }
    }

    public InputStream retrieveImage(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        try {
            headers.set("Authorization", "Bearer " + getToken());
            HttpResponse<String> response = Unirest.get(path)
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer " + getToken())
                    .asString();
            log.info("Sirv token: {}", getToken());
            return response.getRawBody();
        } catch (UnirestException e) {
            log.error("retrieveImage: {}", e.getMessage());
            return null;
        }
    }

}
