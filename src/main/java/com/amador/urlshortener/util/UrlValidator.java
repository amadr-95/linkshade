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
            log.debug("Checking url: '{}'", formUrl);
            URL url = new URI(formUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if(responseCode >= 200 && responseCode < 400) {
                log.debug("URL '{}' valid with status code: {}", url, responseCode);
                return true;
            } else {
                log.error("URL '{}' not reachable with status code: {}", url, responseCode);
                return false;
            }
        } catch (Exception ex) {
            log.error("Url '{}' not valid", formUrl, ex);
            return false;
        }
    }
}
