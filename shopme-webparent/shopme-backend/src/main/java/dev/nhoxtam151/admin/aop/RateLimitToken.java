package dev.nhoxtam151.admin.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
public class RateLimitToken {
    private final int EXPIRE_TOKEN_IN_SECONDS = 1200;
    private Instant now;
    private String token;
    private Logger logger = LoggerFactory.getLogger(RateLimitToken.class);

    @Around("@annotation(dev.nhoxtam151.admin.aop.annotations.LimitToken)")
    public String isTokenExpire(ProceedingJoinPoint proceedingJoinPoint) throws Throwable, JsonProcessingException {
        Instant current = Instant.now();
        logger.info("Method name: " + proceedingJoinPoint.getSignature().getName());
        if (now == null) {
            now = current;
            token = (String) proceedingJoinPoint.proceed();
        }
        if (!current.isBefore(now.plusSeconds(EXPIRE_TOKEN_IN_SECONDS))) {
            token = proceedingJoinPoint.proceed().toString();
            now = current;
        }
        logger.info("Token from RateLimitToken{}", token);
        return token;
    }
}
