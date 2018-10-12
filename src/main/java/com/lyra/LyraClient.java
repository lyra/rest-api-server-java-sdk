package com.lyra;

import com.google.gson.Gson;
import com.lyra.config.LyraClientConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class LyraClient {

    private static String defaultUsername;
    private static String defaultPassword;
    private static String defaultProxyHost;
    private static String defaultProxyPort;
    private static String defaultEndpointURL;
    private static String defaultConnectionTimeout;
    private static String defaultRequestTimeout;

    //Static initialization of default properties
    static {
        Properties defaultConfiguration = readDefaultConfiguration();

        defaultUsername = defaultConfiguration.getProperty("username");
        defaultPassword = defaultConfiguration.getProperty("password");
        defaultProxyHost = defaultConfiguration.getProperty("proxyHost");
        defaultProxyPort = defaultConfiguration.getProperty("proxyPort");
        defaultEndpointURL = defaultConfiguration.getProperty("endpointURL");
        defaultConnectionTimeout = defaultConfiguration.getProperty("connectionTimeout");
        defaultRequestTimeout = defaultConfiguration.getProperty("requestTimeout");
    }

    private static Map<String, String> getFinalConfiguration(LyraClientConfiguration requestConfiguration) {
        if (requestConfiguration == null) {
            requestConfiguration = LyraClientConfiguration.builder().build();
        }

        Map<String, String> finalConfiguration = new HashMap<>();
        finalConfiguration.put("username", requestConfiguration.getUsername() != null ? requestConfiguration.getUsername() : defaultUsername);
        finalConfiguration.put("password", requestConfiguration.getPassword() != null ? requestConfiguration.getPassword() : defaultPassword);
        finalConfiguration.put("proxyHost", requestConfiguration.getProxyHost() != null ? requestConfiguration.getProxyHost() : defaultProxyHost);
        finalConfiguration.put("proxyPort", requestConfiguration.getProxyPort() != null ? requestConfiguration.getProxyPort() : defaultProxyPort);
        finalConfiguration.put("endpointURL", requestConfiguration.getEndpointUrl() != null ? requestConfiguration.getEndpointUrl() : defaultEndpointURL);
        finalConfiguration.put("connectionTimeout", requestConfiguration.getConnectionTimeout() != null ? requestConfiguration.getConnectionTimeout() : defaultConnectionTimeout);
        finalConfiguration.put("requestTimeout", requestConfiguration.getRequestTimeout() != null ? requestConfiguration.getRequestTimeout() : defaultRequestTimeout);

        return finalConfiguration;
    }

    private static Properties readDefaultConfiguration() {
        Properties configurationProperties = new Properties();
        try (InputStream input = LyraClient.class.getClassLoader().getResourceAsStream("lyra-client-configuration.properties")) {
            configurationProperties.load(input);
        } catch (IOException ioe) {
            throw new LyraClientException("Could not read default configuration");
        }

        return configurationProperties;
    }

    private static String generateUrl(String resource, Map<String, String> configuration) {
        String endpoint = configuration.get("endpointURL");

        return endpoint /*+ "/api-payment/" */ + "/" + resource + "/";
    }

    private static HttpURLConnection createConnection(String resource, Map<String, String> configuration) throws IOException {
        URL urlToConnect = new URL(generateUrl(resource, configuration));

        //Set proxy if necessary
        String proxyServer = configuration.get("proxyHost");
        String proxyPort = configuration.get("proxyPort");

        Proxy proxy = null;
        if ((proxyServer != null && !proxyServer.isEmpty()) && (proxyPort != null && !proxyPort.isEmpty())) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer, Integer.parseInt(proxyPort)));
        }

        HttpURLConnection connection = (proxy != null) ? (HttpURLConnection) urlToConnect.openConnection(proxy)
                : (HttpURLConnection) urlToConnect.openConnection();

        //add request headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mobile Client SDK ");
        connection.setRequestProperty("Content-type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Basic " + generateAuthorizationToken(configuration));

        connection.setConnectTimeout(Integer.valueOf(configuration.get("connectionTimeout")));
        connection.setReadTimeout(Integer.valueOf(configuration.get("requestTimeout")));

        return connection;
    }

    private static void sendRequestPayload(HttpURLConnection connection, String payload) throws IOException {
        // Send post request
        connection.setDoOutput(true);
        try (OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream())) {
            wr.write(payload);
            wr.flush();
        }
    }

    private static String generateAuthorizationToken(Map<String, String> configuration) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(
                new String(configuration.get("username") + ":" + configuration.get("password")).getBytes("UTF-8"));
    }


    public static String preparePayment(Map<String, Object> parameters) throws LyraClientException {
        return preparePayment(parameters, LyraClientConfiguration.builder().build());
    }

    public static String preparePayment(Map<String, Object> parameters, LyraClientConfiguration requestConfiguration) throws LyraClientException {
        String response = null;
        Map<String, String> configuration = getFinalConfiguration(requestConfiguration);

        try {
            HttpURLConnection connection = createConnection("createPayment", configuration);

            sendRequestPayload(connection, new Gson().toJson(parameters));

            int responseCode = connection.getResponseCode();
            log.info("Response Code: " + responseCode);

            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = buffer.lines().collect(Collectors.joining("\n"));
            }
            log.info("Response: " + response);
        } catch (IOException ioe) {
            throw new LyraClientException("Exception calling payment platform server", ioe);
        }

        return response;
    }
}
