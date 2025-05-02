package com.amador.urlshortener.exceptions;

public class UrlExpiredException extends UrlException {

    public UrlExpiredException(String message) {
        super(message);
    }
}
