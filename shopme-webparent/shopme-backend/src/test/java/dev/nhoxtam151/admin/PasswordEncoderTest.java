package dev.nhoxtam151.admin;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordEncoderTest {
    @Test
    public void test() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        System.out.println(encoder.matches(password, encodedPassword));
        boolean isEqual = encoder.matches(password, encodedPassword);
        assertEquals(isEqual, true);
    }
}
