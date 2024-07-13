package com.example.ecommercewebsite.aop;

import com.example.ecommercewebsite.exception.AuthenticationException;
import com.example.ecommercewebsite.redis.TokenRedisService;
import com.example.ecommercewebsite.utils.JWTUtils;
import com.example.ecommercewebsite.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class SecuredAspect {

    private final TokenRedisService tokenRedisService;

    @Before(value = "@annotation(com.example.ecommercewebsite.aop.Secured)")
    public void before(JoinPoint joinPoint){
        try {
            String role = getCurrentRole(joinPoint);

            String tokenRedis = tokenRedisService.get(JWTUtils.getUsername()).trim();

            String tokenHeaders = JWTUtils.getCurrentToken();

            List<String> rolesCurrent = JWTUtils.getRoles(tokenHeaders);

            if (StringUtils.isNullOrEmpty(role)){
                checkEqualsToken(tokenRedis, tokenHeaders);
            } else {
                switch (role) {
                    case "ROLE_ADMIN", "ROLE_USER": {
                        checkRole(rolesCurrent, role);
                        checkEqualsToken(tokenRedis, tokenHeaders);
                        break;
                    }
                    case "ALL": {
                        checkEqualsToken(tokenRedis, tokenHeaders);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        } catch (Exception e){
            if (e instanceof AuthenticationException){
                throw e;
            }
            else {
                throw new AuthenticationException();
            }
        }
    }

    private String getCurrentRole(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Secured secured = method.getAnnotation(Secured.class);
        return secured.role().getValue();
    }

    private static void checkRole(List<String> roles, String role){
        if(!roles.contains(role)){
            throw new AuthenticationException("You do not have access to this functionality", 403);
        }
    }

    private static void checkEqualsToken(String token, String tokenCurrent){
        if(StringUtils.isNullOrEmpty(token) || StringUtils.isNullOrEmpty(tokenCurrent)){
            throw new AuthenticationException();
        }

        if(!StringUtils.equals(token, tokenCurrent)){
            throw new AuthenticationException();
        }
    }
}
