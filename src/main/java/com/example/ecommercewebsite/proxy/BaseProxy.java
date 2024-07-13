package com.example.ecommercewebsite.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.io.File;
import java.util.Map;
import java.util.function.Function;

@Component
public class BaseProxy {

    @Autowired
    private RestTemplate restTemplate;

    protected HttpHeaders initHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected HttpHeaders initHeaders(String clientId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", clientId);
        return headers;
    }

    protected <R> R post(String url, Function<Integer, HttpHeaders> headersFunction, Map<String, Object> payload, Class<R> rClass) {
        return post(url, headersFunction, payload, 0, rClass);
    }


    private <R> R post(String url, Function<Integer, HttpHeaders> headersFunction, Map<String, Object> payload, int i, Class<R> rClass){
        try {
            HttpHeaders headers = headersFunction.apply(i);

            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            for(Map.Entry<String, Object> entry : payload.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if(value instanceof  File file) {
                    map.add(key, new FileSystemResource(file));
                } else {
                    map.add(key, value);
                }
            }
            return this.post(url, headers, map, rClass);
        } catch (HttpClientErrorException e) {
            if(i < 3 && e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return this.post(url, headersFunction, payload, i+1, rClass);
            } else {
                throw e;
            }
        }
    }

    private <R> R post(String url, HttpHeaders headers, MultiValueMap<String, Object> map, Class<R> rClass) {
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<R> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, rClass);
        return responseEntity.getBody();
    }
}
