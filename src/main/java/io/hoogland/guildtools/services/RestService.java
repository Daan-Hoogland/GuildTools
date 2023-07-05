package io.hoogland.guildtools.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.print.Pageable;

@Service
public class RestService {

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getPostsPlainJSON(String url) {
        return this.restTemplate.getForObject(url, String.class);
    }

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
}

