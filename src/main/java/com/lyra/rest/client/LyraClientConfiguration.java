package com.lyra.rest.client;

import lombok.Builder;
import lombok.Getter;

/**
 * This bean class encapsulates client configuration data. <p>
 *
 * In order to facilitate object creation it implements a builder pattern.
 *
 * @author Lyra Network
 */
@Builder
@Getter
public class LyraClientConfiguration {
    public static final String CONFIGURATION_KEY_USERNAME = "username";
    public static final String CONFIGURATION_KEY_PASSWORD = "password";
    public static final String CONFIGURATION_KEY_PROXY_HOST = "proxyHost";
    public static final String CONFIGURATION_KEY_PROXY_PORT = "proxyPort";
    public static final String CONFIGURATION_KEY_CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String CONFIGURATION_KEY_REQUEST_TIMEOUT = "requestTimeout";

    private String username;
    private String password;
    private String proxyHost;
    private String proxyPort;
    private String connectionTimeout;
    private String requestTimeout;
}
