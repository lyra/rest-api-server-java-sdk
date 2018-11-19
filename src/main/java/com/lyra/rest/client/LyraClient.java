package com.lyra.rest.client;

import com.google.gson.Gson;

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

/**
 * This client component allows to interact with the Rest API of the Payment Platform.<p/>
 * <p>
 * In order to configure this component, it is necessary to set the properties in a file called
 * lyra-client-configuration.properties. This file must exist in the classpath.<p/>
 * <p>
 * It is possible, anyway, to set an specific configuration using the {@link LyraClientConfiguration} object
 * and its builder.<p/>
 * <p>
 * In case of error, a {@link LyraClientException} non-checked exception will be thrown, containing error details and the full stack.
 *
 * @author Lyra Network
 */
public class LyraClient {

    protected static final String REST_API_VERSION = "V4";
    protected static final Gson GSON = new Gson();
    protected static final String ENCODING = "UTF-8";
    private static final String CONFIGURATION_FILE_NAME = "lyra-client-configuration";

    private static final int HTTP_RESPONSE_OK = 200;

    private static String defaultUsername;
    private static String defaultPassword;
    private static String defaultProxyHost;
    private static String defaultProxyPort;
    private static String defaultEndpointUrl;
    private static String defaultConnectionTimeout;
    private static String defaultRequestTimeout;

    //Static initialization of default properties
    static {
        Properties defaultConfiguration = readDefaultConfiguration();

        defaultUsername = defaultConfiguration.getProperty(LyraClientConfiguration.CONFIGURATION_KEY_USERNAME);
        defaultPassword = defaultConfiguration.getProperty(LyraClientConfiguration.CONFIGURATION_KEY_PASSWORD);
        defaultProxyHost = defaultConfiguration.getProperty(LyraClientConfiguration.CONFIGURATION_KEY_PROXY_HOST);
        defaultProxyPort = defaultConfiguration.getProperty(LyraClientConfiguration.CONFIGURATION_KEY_PROXY_PORT);
        defaultEndpointUrl = defaultConfiguration.getProperty(LyraClientConfiguration.CONFIGURATION_KEY_ENDPOINT_URL);
        defaultConnectionTimeout = defaultConfiguration.getProperty(LyraClientConfiguration.CONFIGURATION_KEY_CONNECTION_TIMEOUT);
        defaultRequestTimeout = defaultConfiguration.getProperty(LyraClientConfiguration.CONFIGURATION_KEY_REQUEST_TIMEOUT);
    }

    /**
     * Calls the payment platform using the REST API.
     *
     * @param targetResource the resource to target. Use {@link LyraClientResource} enum to help defining this parameter
     * @param parameters     Map that contains the parameters of the payment
     * @return {@link String} that contains the full response from API
     * @throws LyraClientException exception if error processing the request
     */
    public static String post(String targetResource, Map<String, Object> parameters) {
        return post(targetResource, parameters, LyraClientConfiguration.builder().build());
    }

    /**
     * Calls the payment platform using the REST API
     *
     * @param targetResource       the resource to target. You can use {@link LyraClientResource} enum to help defining this parameter
     * @param parameters           Map that contains the parameters of the payment
     * @param requestConfiguration Configuration object that overrides the default configuration for this request
     * @return {@link String} that contains the full response from API
     * @throws LyraClientException exception if error processing the request
     */
    public static String post(String targetResource, Map<String, Object> parameters, LyraClientConfiguration requestConfiguration) {
        String responseMessage = null;
        Map<String, String> configuration = getFinalConfiguration(requestConfiguration);

        try {
            //Call Payment Platform
            HttpURLConnection connection = createConnection(targetResource, configuration);
            sendRequestPayload(connection, GSON.toJson(parameters));

            int responseCode = connection.getResponseCode();

            //There will always be a 200-OK response, even if there is an error.
            if (responseCode == HTTP_RESPONSE_OK) {
                responseMessage = readResponseContent(connection);
            } else {
                //Generic server error case (404, 500, etc).
                throw new LyraClientException("HTTP call to Payment Platform was not successful.", responseCode,
                        readResponseContent(connection));
            }
        } catch (IOException ioe) {
            throw new LyraClientException("Exception calling payment platform server", ioe);
        }

        return responseMessage;
    }

    /*
    This method calculates the configuration to use. It takes the default one and overrides it with the configuration
    passed as parameter
     */
    private static Map<String, String> getFinalConfiguration(LyraClientConfiguration requestConfiguration) {
        if (requestConfiguration == null) {
            requestConfiguration = LyraClientConfiguration.builder().build();
        }

        Map<String, String> finalConfiguration = new HashMap<>();
        finalConfiguration.put(LyraClientConfiguration.CONFIGURATION_KEY_USERNAME, requestConfiguration.getUsername() != null ? requestConfiguration.getUsername() : defaultUsername);
        finalConfiguration.put(LyraClientConfiguration.CONFIGURATION_KEY_PASSWORD, requestConfiguration.getPassword() != null ? requestConfiguration.getPassword() : defaultPassword);
        finalConfiguration.put(LyraClientConfiguration.CONFIGURATION_KEY_PROXY_HOST, requestConfiguration.getProxyHost() != null ? requestConfiguration.getProxyHost() : defaultProxyHost);
        finalConfiguration.put(LyraClientConfiguration.CONFIGURATION_KEY_PROXY_PORT, requestConfiguration.getProxyPort() != null ? requestConfiguration.getProxyPort() : defaultProxyPort);
        finalConfiguration.put(LyraClientConfiguration.CONFIGURATION_KEY_ENDPOINT_URL, requestConfiguration.getEndpointUrl() != null ? requestConfiguration.getEndpointUrl() : defaultEndpointUrl);
        finalConfiguration.put(LyraClientConfiguration.CONFIGURATION_KEY_CONNECTION_TIMEOUT, requestConfiguration.getConnectionTimeout() != null ? requestConfiguration.getConnectionTimeout() : defaultConnectionTimeout);
        finalConfiguration.put(LyraClientConfiguration.CONFIGURATION_KEY_REQUEST_TIMEOUT, requestConfiguration.getRequestTimeout() != null ? requestConfiguration.getRequestTimeout() : defaultRequestTimeout);

        return finalConfiguration;
    }

    /*
    Read the default configuration from configuration file that should exist in classpath
     */
    private static Properties readDefaultConfiguration() {
        Properties configurationProperties = new Properties();
        try (InputStream input = LyraClient.class.getClassLoader()
                .getResourceAsStream(CONFIGURATION_FILE_NAME + ".properties")) {
            configurationProperties.load(input);
        } catch (IOException ioe) {
            throw new LyraClientException("Could not read default configuration", ioe);
        }

        return configurationProperties;
    }

    /*
    Generates the Url to call Rest API
     */
    private static String generateChargeUrl(String resource, Map<String, String> configuration) {
        String endpoint = configuration.get(LyraClientConfiguration.CONFIGURATION_KEY_ENDPOINT_URL);

        return String.format("%s/api-payment/%s/%s", endpoint, REST_API_VERSION, resource);
    }

    /*
    Creates the connection used to make a JSON based REST call
     */
    private static HttpURLConnection createConnection(String resource, Map<String, String> configuration) throws IOException {
        URL urlToConnect = new URL(generateChargeUrl(resource, configuration));

        //Set proxy if necessary
        Proxy proxy = null;
        if (couldUseProxy(urlToConnect.getHost())) {
            String proxyServer = configuration.get(LyraClientConfiguration.CONFIGURATION_KEY_PROXY_HOST);
            String proxyPort = configuration.get(LyraClientConfiguration.CONFIGURATION_KEY_PROXY_PORT);

            if ((proxyServer != null && !proxyServer.isEmpty()) && (proxyPort != null && !proxyPort.isEmpty())) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer, Integer.parseInt(proxyPort)));
            }
        }

        //Create connection
        HttpURLConnection connection = (proxy != null) ? (HttpURLConnection) urlToConnect.openConnection(proxy)
                : (HttpURLConnection) urlToConnect.openConnection();

        //Add request headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mobile Client SDK " + REST_API_VERSION);
        connection.setRequestProperty("Content-type", "application/json; charset=" + ENCODING);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Basic " + generateAuthorizationToken(configuration));

        //Set timeouts if necessary
        String connectionTimeout = configuration.get(LyraClientConfiguration.CONFIGURATION_KEY_CONNECTION_TIMEOUT);
        String requestTimeout = configuration.get(LyraClientConfiguration.CONFIGURATION_KEY_REQUEST_TIMEOUT);

        if (connectionTimeout != null && !connectionTimeout.isEmpty()) {
            connection.setConnectTimeout(Integer.valueOf(connectionTimeout));
        }
        if (requestTimeout != null && !requestTimeout.isEmpty()) {
            connection.setReadTimeout(Integer.valueOf(requestTimeout));
        }

        return connection;
    }

    /*
    Send an HTTP request with the provided payload
     */
    private static void sendRequestPayload(HttpURLConnection connection, String payload) throws IOException {
        // Send post request
        connection.setDoOutput(true);
        try (OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream(), ENCODING)) {
            wr.write(payload);
            wr.flush();
        }
    }

    /*
    Read the content from an HTTP response
     */
    private static String readResponseContent(HttpURLConnection connection) throws IOException {
        String responseMessage;
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream(), ENCODING))) {
            responseMessage = buffer.lines().collect(Collectors.joining("\n"));
        }
        return responseMessage;
    }

    /*
    Generates the string to send in basic authorization
     */
    private static String generateAuthorizationToken(Map<String, String> configuration) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(
                (configuration.get(LyraClientConfiguration.CONFIGURATION_KEY_USERNAME) + ":" + configuration.get(LyraClientConfiguration.CONFIGURATION_KEY_PASSWORD)).getBytes(ENCODING));
    }

    private static boolean couldUseProxy(String proxyHost) {
        return !(proxyHost == null) && !proxyHost.equals("localhost") && !proxyHost.equals("127.0.0.1");
    }
}
