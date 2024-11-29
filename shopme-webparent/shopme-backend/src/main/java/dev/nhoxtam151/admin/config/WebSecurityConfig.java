package dev.nhoxtam151.admin.config;

import dev.nhoxtam151.admin.services.MyUserDetailsService;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig implements ApplicationContextAware {
    private ApplicationContext applicationContext;


    //http://localhost:8080/login/oauth2/code/google
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(formConfig -> formConfig
                .loginPage("/login")
                .permitAll()
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
        );
        http.oauth2Login(oauth2 -> oauth2.loginPage("/login"));
        http.logout(LogoutConfigurer::permitAll);
        http.httpBasic(withDefaults());
        http.headers(header -> header.cacheControl(HeadersConfigurer.CacheControlConfig::disable));
        http.authorizeHttpRequests(request -> {
            request.requestMatchers("style.css").permitAll();
            request.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll(); //this line omitted 2 lines below
//            request.requestMatchers("/resources/**").permitAll();
//            request.requestMatchers("/webjars/**", "/images/**", "/js/**", "style.css", "/fontawesome/**").permitAll();
            request.requestMatchers("/users/**").hasAuthority("ADMIN");
            request.requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("ADMIN", "EDITOR");
            request.requestMatchers("/products/**").hasAnyAuthority("ADMIN", "EDITOR", "SALESPERSON", "SHIPPER");
            request.requestMatchers("/customers/**", "/shipping/**").hasAnyAuthority("ADMIN", "SALESPERSON");
            request.requestMatchers("/orders/**").hasAnyAuthority("ADMIN", "SALESPERSON", "SHIPPER");
            request.requestMatchers("/reports/**", "/articles/**").hasAnyAuthority("ADMIN", "SALESPERSON");
            request.anyRequest().authenticated();
        });
        http.authenticationProvider(authenticationProvider());
        http.rememberMe(remember -> {
            remember.key("abc123456").tokenValiditySeconds(7 * 24 * 60 * 60);
            //remember.alwaysRemember(true);
        });
        return http.build();
    }

    /*@Bean another way to config ignoring instead of permitAll
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity -> {
            webSecurity.ignoring().requestMatchers("/images/**", "/js/**", "/css/**", "/fonts/**", "/webjars/**");
        };
    }*/


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(applicationContext.getBean("userDetailsService", MyUserDetailsService.class));
        return authenticationProvider;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
