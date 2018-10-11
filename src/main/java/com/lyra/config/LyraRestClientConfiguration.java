package com.lyra.config;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LyraRestClientConfiguration {

    private String username;
    private String password;
    private String publicKey;
    private String hashKey;
    private String proxyHost;
    private String proxyPort;
    private String endpoint;
    private String clientEndpoint;
/*
    private LyraRestClientConfiguration(String username, String password, String publicKey, String hashKey, String proxyHost,
                                        String proxyPort, String endpoint, String clientEndpoint) {
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
        this.hashKey = hashKey;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.endpoint = endpoint;
        this.clientEndpoint = clientEndpoint;
    }
    */
/*
    public static class Builder {
        private String username;
        private String password;
        private String publicKey;
        private String hashKey;
        private String proxyHost;
        private String proxyPort;
        private String endpoint;
        private String clientEndpoint;

        public Builder() {
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder withHashKey(String hashKey) {
            this.hashKey = hashKey;
            return this;
        }

        public Builder withProxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
            return this;
        }

        public Builder withProxyPort(String proxyPort) {
            this.proxyPort = proxyPort;
            return this;
        }

        public Builder withEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder clientEndpoint(String clientEndpoint) {
            this.clientEndpoint = clientEndpoint;
            return this;
        }

        public LyraRestClientConfiguration build() {
            return new LyraRestClientConfiguration(this.username, this.password,
                    this.publicKey, this.hashKey, this.proxyHost, this.proxyPort, this.endpoint, this.clientEndpoint);
        }
    }
    */
}
