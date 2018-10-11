package com.lyra;

import com.lyra.config.LyraRestClientConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LyraRestClient {

    private String username;
    private String password;
    private String publicKey;
    private String hashKey;
    private String proxyHost;
    private String proxyPort;
    private String endpoint;
    private String clientEndpoint;

    public LyraRestClient() {
        //Read Rest client default configuration
        Properties defaultConfiguration = readDefaultConfiguration();

        this.username = defaultConfiguration.getProperty("username");
        this.password =  defaultConfiguration.getProperty("password");
        this.publicKey = defaultConfiguration.getProperty("publicKey");
        this.hashKey = defaultConfiguration.getProperty("hashKey");
        this.proxyHost = defaultConfiguration.getProperty("proxyHost");
        this.proxyPort = defaultConfiguration.getProperty("proxyPort");
        this.endpoint = defaultConfiguration.getProperty("endpoint");
        this.clientEndpoint = defaultConfiguration.getProperty("clientEndpoint");
    }

    public LyraRestClient(LyraRestClientConfiguration configuration) {
        this();

        this.username = configuration.getUsername() != null ? configuration.getUsername() : this.username;
        this.password = configuration.getPassword() != null ? configuration.getPassword() : this.password;
        this.publicKey = configuration.getPublicKey() != null ? configuration.getPublicKey() : this.publicKey;
        this.hashKey = configuration.getHashKey() != null ? configuration.getHashKey() : this.hashKey;
        this.proxyHost = configuration.getProxyHost() != null ? configuration.getProxyHost() : this.proxyHost;
        this.proxyPort = configuration.getProxyPort() != null ? configuration.getProxyPort() : this.proxyPort;
        this.endpoint = configuration.getEndpoint() != null ? configuration.getEndpoint() : this.endpoint;
        this.clientEndpoint = configuration.getClientEndpoint() != null ? configuration.getClientEndpoint() : this.clientEndpoint;
    }

    private Properties readDefaultConfiguration() {
        Properties configurationProperties = new Properties();
        try (InputStream input = LyraRestClient.class.getClassLoader().getResourceAsStream("lyra-rest-configuration.properties")) {
            configurationProperties.load(input);
        } catch (IOException ioe) {
            throw new LyraRestClientException("Could not read default configuration");
        }

        return configurationProperties;
    }
}
