package com.amador.urlshortener.util;

import com.amador.urlshortener.util.annotations.ValidUrl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Slf4j
@Deprecated
public class UrlValidator implements ConstraintValidator<ValidUrl, String> {
    @Override
    public boolean isValid(String formUrl, ConstraintValidatorContext context) {
        try {
            log.debug("Checking url: '{}'", formUrl);
            int responseCode = getResponseCode(formUrl);
            if(responseCode >= 200 && responseCode < 400) {
                log.debug("URL '{}' valid with status code: {}", formUrl, responseCode);
                return true;
            } else {
                log.error("URL '{}' not reachable with status code: {}", formUrl, responseCode);
                return false;
            }
        } catch (Exception ex) {
            log.error("Url '{}' not valid", formUrl, ex);
            return false;
        }
    }

    private int getResponseCode(String url) throws Exception {
        URL urlObj = new URI(url).toURL();
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        //simulate real browser
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        return connection.getResponseCode();
    }
}
