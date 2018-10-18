package com.lyra.rest.client;

/**
 * Runtime exception launched in case of error calling the client.
 *
 * @author Lyra Network
 */
public class LyraClientException extends RuntimeException {

    public LyraClientException(String message) {
        super(message);
    }

    public LyraClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
