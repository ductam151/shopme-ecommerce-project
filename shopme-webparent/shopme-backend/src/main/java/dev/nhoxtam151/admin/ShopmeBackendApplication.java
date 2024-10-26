package dev.nhoxtam151.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan({"dev.nhoxtam151.shopmecommon.models", "dev.nhoxtam151.admin.models"})
@EnableTransactionManagement
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class ShopmeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopmeBackendApplication.class, args);
    }

}
