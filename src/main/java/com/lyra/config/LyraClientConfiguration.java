package com.lyra.config;

import lombok.Builder;
import lombok.Getter;

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
