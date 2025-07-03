package de.linkshade.util;

import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlValidator {

    private final ValidationContextBuilder contextBuilder;
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    public boolean isValid(ConstraintValidatorContext context, String formUrl) {
        log.debug("Checking url: '{}'", formUrl);
        // first layer (mandatory): syntax validation
        if (!isValidSyntax(formUrl)) {
            log.error("URL '{}' has syntax error", formUrl);
            contextBuilder.buildContext(context, "{validation.urlForm.invalidUrl}", "originalUrl");
            return false;
        }
        // second layer (optional): validate http response code (slow)
        return isReachable(context, formUrl);
        // return true
    }

    private boolean isValidSyntax(String url) {
        if (url == null || url.isBlank()) return false;
        return URL_PATTERN.matcher(url).matches();
    }

    private boolean isReachable(ConstraintValidatorContext context, String url) {
        try {
            int responseCode = getResponseCode(url);

            if (responseCode >= 200 && responseCode < 400) {
                log.debug("URL '{}' valid with status code: {}", url, responseCode);
                return true;
            } else if (responseCode == 401 || responseCode == 403) {
                log.warn("URL '{}' with status code: {} - may be valid", url, responseCode);
                return true;
            } else {
                log.error("URL '{}' not reachable with status code: {}", url, responseCode);
                contextBuilder.buildContext(context, "{validation.urlForm.invalidUrl}", "originalUrl");
                return false;
            }
        } catch (Exception ex) {
            log.error("Url '{}' not valid", url, ex);
            contextBuilder.buildContext(context, "{validation.urlForm.invalidUrl}", "originalUrl");
            return false;
        }
    }

    private static int getResponseCode(String url) throws Exception {
        URL urlObj = new URI(url).toURL();
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);
        //simulate real browser
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);

        return connection.getResponseCode();
    }
}
