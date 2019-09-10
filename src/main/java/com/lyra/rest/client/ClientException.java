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
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int responseCode;
    private String responseMessage;

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, int responseCode, String responseMessage) {
        super(message);
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

