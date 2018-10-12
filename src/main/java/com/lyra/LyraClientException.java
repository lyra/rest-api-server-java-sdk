package com.lyra;

public class LyraClientException extends RuntimeException {

    public LyraClientException(String message) {
        super(message);
    }

    public LyraClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
