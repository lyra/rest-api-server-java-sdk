package com.lyra.rest.client;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * <p>
 * This client component allows to interact with the Rest API of the Payment Platform.</p>
 * <p>L
 * In order to configure this component, it is necessary to set the properties in a file called
 * api-client-configuration-default.properties. This file must exist in the classpath.</p>
 * <p>
 * It is possible, anyway, to set an specific configuration using the {@link ClientConfiguration} object
 * and its builder.</p>
 * <p>
 * In case of error, a {@link ClientException} non-checked exception will be thrown, containing error details and the full stack.
 *
 * @author Lyra Network
 */
public class Client {

    protected static final String REST_API_VERSION = "V4";
    protected static final Gson GSON = new Gson();
    private static final String DEFAULT_CONFIGURATION_FILE_NAME = "api-client-configuration-default";
    private static final String APP_CONFIGURATION_FILE_NAME = "api-client-configuration";

    private static final int HTTP_RESPONSE_OK = 200;

    private static String defaultUsername;
    private static String defaultPassword;
    private static String defaultRestApiServerName;
    private static String defaultProxyHost;
    private static String defaultProxyPort;
    private static String defaultConnectionTimeout;
    private static String defaultRequestTimeout;
    private static String defaultHashKey;

    //Static initialization of default properties
    static {
        Properties defaultConfiguration = readDefaultConfiguration();

        defaultUsername = defaultConfiguration.getProperty(ClientConfiguration.CONFIGURATION_KEY_USERNAME);
        defaultPassword = defaultConfiguration.getProperty(ClientConfiguration.CONFIGURATION_KEY_PASSWORD);
        defaultRestApiServerName = defaultConfiguration.getProperty(ClientConfiguration.CONFIGURATION_KEY_REST_API_SERVER_NAME);
        defaultProxyHost = defaultConfiguration.getProperty(ClientConfiguration.CONFIGURATION_KEY_PROXY_HOST);
        defaultProxyPort = defaultConfiguration.getProperty(ClientConfiguration.CONFIGURATION_KEY_PROXY_PORT);
        defaultConnectionTimeout = defaultConfiguration.getProperty(ClientConfiguration.CONFIGURATION_KEY_CONNECTION_TIMEOUT);
        defaultRequestTimeout = defaultConfiguration.getProperty(ClientConfiguration.CONFIGURATION_KEY_REQUEST_TIMEOUT);
        defaultHashKey = defaultConfiguration.getProperty(ClientConfiguration.CONFIGURATION_KEY_HASH_KEY);
    }

    //Private constructor as all methods are static
    private Client() {
    }

    /**
     * Calls the payment platform using the REST API.
     *
     * @param targetResource the resource to target. Use {@link ClientResource} enum to help defining this parameter
     * @param parameters     Map that contains the parameters of the payment
     * @return {@link String} that contains the full response from API
     * @throws ClientException exception if error processing the request
     */
    public static String post(String targetResource, Map<String, Object> parameters) {
        return post(targetResource, parameters, ClientConfiguration.builder().build());
    }

    /**
     * Calls the payment platform using the REST API
     *
     * @param targetResource       the resource to target. You can use {@link ClientResource} enum to help defining this parameter
     * @param parameters           Map that contains the parameters of the payment
     * @param requestConfiguration Configuration object that overrides the default configuration for this request
     * @return {@link String} that contains the full response from API
     * @throws ClientException exception if error processing the request
     */
    public static String post(String targetResource, Map<String, Object> parameters, ClientConfiguration requestConfiguration) {
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
                throw new ClientException("HTTP call to Payment Platform was not successful.", responseCode,
                        readResponseContent(connection));
            }
        } catch (IOException ioe) {
            throw new ClientException("Exception calling payment platform server", ioe);
        }

        return responseMessage;
    }

    /**
     * Checks the integrity of the answer. To perform this verification it compares the provided hash with
     * the one generated with the selected algorithm
     *
     * @param paymentAnswer map that contains the answer data
     * @return true if the integrity of the answer is valid
     */
    public static boolean verifyAnswer(Map<String, Object> paymentAnswer) {
        return verifyAnswer(paymentAnswer, ClientConfiguration.builder().build());
    }

    /**
     * Checks the integrity of the answer. To perform this verification it compares the provided hash with
     * the one generated with the selected algorithm
     *
     * @param paymentAnswer        map that contains the answer data
     * @param requestConfiguration configuration object that overrides the default configuration for this request
     * @return true if the integrity of the answer is valid
     */
    public static boolean verifyAnswer(Map<String, Object> paymentAnswer, ClientConfiguration requestConfiguration) {
        String answer = (String)paymentAnswer.get("kr-answer");
        String hashAlgorithm = (String) paymentAnswer.get("kr-hash-algorithm");

        if (!ClientCryptUtil.isAlgorithmSupported(hashAlgorithm)) {
            throw new ClientException("Signature algorithm not supported. Make sure you are using the last version of this SDK");
        }

        //Get configuration in order to retrieve the key to use
        Map<String, String> configuration = getFinalConfiguration(requestConfiguration);

        //Check hash
        String answerHash = (String) paymentAnswer.get("kr-hash");
        return answerHash.equals(ClientCryptUtil.calculateHash(answer, configuration.get(ClientConfiguration.CONFIGURATION_KEY_HASH_KEY), hashAlgorithm));
    }

    /*
    This method calculates the configuration to use. It takes the default one and overrides it with the configuration
    passed as parameter
     */
    private static Map<String, String> getFinalConfiguration(ClientConfiguration requestConfiguration) {
        if (requestConfiguration == null) {
            requestConfiguration = ClientConfiguration.builder().build();
        }

        Map<String, String> finalConfiguration = new HashMap<>();
        finalConfiguration.put(ClientConfiguration.CONFIGURATION_KEY_USERNAME, requestConfiguration.getUsername() != null ? requestConfiguration.getUsername() : defaultUsername);
        finalConfiguration.put(ClientConfiguration.CONFIGURATION_KEY_PASSWORD, requestConfiguration.getPassword() != null ? requestConfiguration.getPassword() : defaultPassword);
        finalConfiguration.put(ClientConfiguration.CONFIGURATION_KEY_REST_API_SERVER_NAME, requestConfiguration.getRestApiServerName() != null ? requestConfiguration.getRestApiServerName() : defaultRestApiServerName);
        finalConfiguration.put(ClientConfiguration.CONFIGURATION_KEY_PROXY_HOST, requestConfiguration.getProxyHost() != null ? requestConfiguration.getProxyHost() : defaultProxyHost);
        finalConfiguration.put(ClientConfiguration.CONFIGURATION_KEY_PROXY_PORT, requestConfiguration.getProxyPort() != null ? requestConfiguration.getProxyPort() : defaultProxyPort);
        finalConfiguration.put(ClientConfiguration.CONFIGURATION_KEY_CONNECTION_TIMEOUT, requestConfiguration.getConnectionTimeout() != null ? requestConfiguration.getConnectionTimeout() : defaultConnectionTimeout);
        finalConfiguration.put(ClientConfiguration.CONFIGURATION_KEY_REQUEST_TIMEOUT, requestConfiguration.getRequestTimeout() != null ? requestConfiguration.getRequestTimeout() : defaultRequestTimeout);
        finalConfiguration.put(ClientConfiguration.CONFIGURATION_KEY_HASH_KEY, requestConfiguration.getHashKey() != null ? requestConfiguration.getHashKey() : defaultHashKey);

        return finalConfiguration;
    }

    /*
    Read the default configuration from configuration file that should exist in classpath
     */
    private static Properties readDefaultConfiguration() {
        //Read default parameters
        final Properties finalConfigurationProperties = readConfigurationFile(DEFAULT_CONFIGURATION_FILE_NAME);

        //Read application parameters if exists
        final Properties appConfigurationProperties = readConfigurationFile(APP_CONFIGURATION_FILE_NAME);

        //Override with configuration defined by application
        appConfigurationProperties.forEach((k, v) -> finalConfigurationProperties.setProperty((String) k, (String) v));

        return finalConfigurationProperties;
    }

    //Read configuration file using classloader
    private static Properties readConfigurationFile(String configurationFilename) {
        Properties props = new Properties();
        try (InputStream input = Client.class.getClassLoader()
                .getResourceAsStream(configurationFilename + ".properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException ioe) {
            throw new ClientException("Could not read application configuration", ioe);
        }

        return props;
    }

    /*
    Generates the Url to call Rest API
     */
    private static String generateChargeUrl(String resource, Map<String, String> configuration) {
        return String.format("%s/api-payment/%s/%s", configuration.get(ClientConfiguration.CONFIGURATION_KEY_REST_API_SERVER_NAME),
                REST_API_VERSION, resource);
    }

    private static URL getURLToConnect (String resource, Map<String, String> configuration) throws MalformedURLException {
        return new URL(generateChargeUrl(resource, configuration));
    }

    private static HttpURLConnection getConnection(URL url, Proxy proxy) throws IOException {
        return (proxy != null) ? (HttpURLConnection) url.openConnection(proxy)
                : (HttpURLConnection) url.openConnection();
    }

    /*
    Creates the connection used to make a JSON based REST call
     */
    private static HttpURLConnection createConnection(String resource, Map<String, String> configuration) throws IOException {
        URL urlToConnect = getURLToConnect(resource, configuration);

        //Set proxy if necessary
        Proxy proxy = null;
        if (couldUseProxy(urlToConnect.getHost())) {
            String proxyServer = configuration.get(ClientConfiguration.CONFIGURATION_KEY_PROXY_HOST);
            String proxyPort = configuration.get(ClientConfiguration.CONFIGURATION_KEY_PROXY_PORT);

            if ((proxyServer != null && !proxyServer.isEmpty()) && (proxyPort != null && !proxyPort.isEmpty())) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer, Integer.parseInt(proxyPort)));
            }
        }

        //Create connection
        HttpURLConnection connection = getConnection(urlToConnect, proxy);

        //Add request headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mobile Client SDK " + REST_API_VERSION);
        connection.setRequestProperty("Content-type", "application/json; charset=" + StandardCharsets.UTF_8);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Basic " + generateAuthorizationToken(configuration));

        //Set timeouts if necessary
        String connectionTimeout = configuration.get(ClientConfiguration.CONFIGURATION_KEY_CONNECTION_TIMEOUT);
        String requestTimeout = configuration.get(ClientConfiguration.CONFIGURATION_KEY_REQUEST_TIMEOUT);

        if (connectionTimeout != null && !connectionTimeout.isEmpty()) {
            connection.setConnectTimeout(Integer.parseInt(connectionTimeout));
        }
        if (requestTimeout != null && !requestTimeout.isEmpty()) {
            connection.setReadTimeout(Integer.parseInt(requestTimeout));
        }

        return connection;
    }

    /*
    Send an HTTP request with the provided payload
     */
    private static void sendRequestPayload(HttpURLConnection connection, String payload) throws IOException {
        // Send post request
        connection.setDoOutput(true);
        try (OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
            wr.write(payload);
            wr.flush();
        }
    }

    /*
    Read the content from an HTTP response
     */
    private static String readResponseContent(HttpURLConnection connection) throws IOException {
        String responseMessage = "";
        if (connection != null && connection.getInputStream() != null) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                responseMessage = buffer.lines().collect(Collectors.joining("\n"));
            }
        }
        return responseMessage;
    }

    /*
    Generates the string to send in basic authorization
     */
    private static String generateAuthorizationToken(Map<String, String> configuration) {
        return Base64.getEncoder().encodeToString(
                (configuration.get(ClientConfiguration.CONFIGURATION_KEY_USERNAME) + ":" + configuration.get(ClientConfiguration.CONFIGURATION_KEY_PASSWORD)).getBytes(StandardCharsets.UTF_8));
    }

    /*
    Determine ig the proxy can be used
    */
    private static boolean couldUseProxy(String proxyHost) {
        return proxyHost != null && !"localhost".equals(proxyHost) && !"127.0.0.1".equals(proxyHost);
    }

}
