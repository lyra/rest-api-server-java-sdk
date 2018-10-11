package com.lyra;

public class LyraRestClientException extends RuntimeException {

    public LyraRestClientException(String message) {
        super(message);
    }

    public LyraRestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
