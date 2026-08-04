package com.prajan.cinehub.movie.service;

import com.prajan.cinehub.movie.entity.SupabaseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupabaseStorageService {

    private final SupabaseConfig config;
    private final RestTemplate restTemplate;


    public String uploadImage(MultipartFile file) {

        String fileName = UUID.randomUUID()
                + "-"
                + file.getOriginalFilename();

        String endpoint =
                config.getUrl()
                        + "/storage/v1/object/"
                        + config.getBucket()
                        + "/movies/"
                        + fileName;

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(config.getApiKey());

        headers.setContentType(
                MediaType.parseMediaType(file.getContentType())
        );

        try {

            HttpEntity<byte[]> request =
                    new HttpEntity<>(file.getBytes(), headers);

            restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    request,
                    String.class
            );

        } catch ( Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image to Supabase", e);
        }
        log.info("img uploaded");
        return config.getUrl()
                + "/storage/v1/object/public/"
                + config.getBucket()
                + "/movies/"
                + fileName;
    }


    public void deleteImage(String imageUrl) {

    }
}
