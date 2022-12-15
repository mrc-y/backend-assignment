package com.katanox.api.integrationtest.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderGenerationHelper {

    private static final String APPLICATION_JSON = "application/json;charset=utf-8";

    public static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        return headers;
    }
}
