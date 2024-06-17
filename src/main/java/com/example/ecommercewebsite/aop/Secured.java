package com.example.ecommercewebsite.aop;

import com.example.ecommercewebsite.enums.RoleEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Secured {
    RoleEnum role() default RoleEnum.ALL;
}
