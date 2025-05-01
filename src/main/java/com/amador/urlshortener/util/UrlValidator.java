package com.amador.urlshortener.util;

import com.amador.urlshortener.util.annotations.ValidUrl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Slf4j
public class UrlValidator implements ConstraintValidator<ValidUrl, String> {
    @Override
    public boolean isValid(String formUrl, ConstraintValidatorContext context) {
        try {
            log.info("Checking url: {}", formUrl);
            URL url = new URI(formUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 400);
        } catch (Exception e) {
            log.error("Url {} not valid", formUrl, e);
            return false;
        }
    }
}
