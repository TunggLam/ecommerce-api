package com.example.ecommercewebsite.exception.handler;

import com.example.ecommercewebsite.constant.Constant;
import com.example.ecommercewebsite.exception.AuthenticationException;
import com.example.ecommercewebsite.exception.BusinessException;
import com.example.ecommercewebsite.exception.model.ExceptionModel;
import com.example.ecommercewebsite.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Objects;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends Throwable implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @Order()
    public ResponseEntity<ExceptionModel> handleALLException(Throwable ex){
        ExceptionModel exceptionDTO = new ExceptionModel();
        exceptionDTO.setMessage(Constant.EXCEPTION_MESSAGE_DEFAULT);
        exceptionDTO.setDescription(ex.getLocalizedMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(value = HttpStatus.BAD_GATEWAY)
    @ResponseBody
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<ExceptionModel> handAllException(BusinessException ex){
        String message = Objects.isNull(ex) || StringUtils.isNullOrEmpty(ex.getMessage()) ? Constant.EXCEPTION_MESSAGE_DEFAULT : ex.getMessage();
        String code = Objects.isNull(ex) || StringUtils.isNullOrEmpty(ex.getCode()) ? null : ex.getCode();
        Object details = Objects.isNull(ex) ? null : ex.getData();
        ExceptionModel exception = new ExceptionModel();
        exception.setMessage(message);
        exception.setCode(code);
        exception.setDetail(details);
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<ExceptionModel> handAllException(AuthenticationException ex){
        String message = Objects.isNull(ex) || StringUtils.isNullOrEmpty(ex.getMessage()) ? "Thông tin truy cập không hợp lệ, vui lòng đăng nhập lại." : ex.getMessage();
        HttpStatus httpStatus = Objects.isNull(ex) || Objects.isNull(ex.getStatusCode()) ? HttpStatus.UNAUTHORIZED : HttpStatus.valueOf(ex.getStatusCode());
        ExceptionModel exception = new ExceptionModel();
        exception.setMessage(message);
        return new ResponseEntity<>(exception, httpStatus);
    }
}
