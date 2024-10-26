package dev.nhoxtam151.admin.models;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "sirv")
public class SirvUrl {
    private Map<String, String> url;

    public Map<String, String> getUrl() {
        return url;
    }

    public void setUrl(Map<String, String> url) {
        this.url = url;
    }
}
