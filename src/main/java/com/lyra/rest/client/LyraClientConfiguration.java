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
    private String username;
    private String password;
    private String proxyHost;
    private String proxyPort;
    private String endpointUrl;
    private String connectionTimeout;
    private String requestTimeout;
}
