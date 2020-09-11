package com.lyra.rest.client;

import lombok.Getter;

/**
 * Runtime exception launched in case of error calling the client.
 *
 * @author Lyra Network
 */
@Getter
public class ClientException extends RuntimeException {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
	
    private int responseCode;
    private String responseMessage;

    /**
     * Construct a ClientException from a message
     */
    public ClientException(String message) {
        super(message);
    }

    /**
     * Construct a ClientException from a message, a responseCode and a responseMessage
     */
    public ClientException(String message, int responseCode, String responseMessage) {
        super(message);
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    /**
     * Construct a ClientException from a message and a cause
     */
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

