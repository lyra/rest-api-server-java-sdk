package com.lyra;

import com.lyra.config.LyraClientConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@PrepareForTest(LyraClient.class)
@RunWith(PowerMockRunner.class)
public class LyraClientUnitTests {
    private static final Integer TEST_AMOUNT = 100;
    private static final Integer TEST_CURRENCY = 100;

    private static final Integer HTTP_OK = 200;
    private static final Integer HTTP_ERROR = 500;

    private static final String RESPONSE_STATUS_OK = "SUCCESS";
    private static final String RESPONSE_STATUS_ERROR = "ERROR";
    private static final String TEST_VERSION = "4.2.0";
    private static final String TEST_FORM_TOKEN = "02NWE0MmU1ZDItZTNkMS00ODQ3LTkyMTAtZTJjZDA2NzQ0YWVlew0KCSJhb";
    private static final String TEST_ERROR_CODE = "PSP_100";

    @Test(expected = LyraClientException.class)
    public void testPreparePaymentWrongConfig() {
        //Empty config
        Map<String, Object> parameters = new HashMap<>();
        LyraClient.preparePayment(parameters);
    }

    @Test(expected = LyraClientException.class)
    public void testPreparePaymentBadReturnCode() throws Exception {
        mockPreparePayment(HTTP_ERROR);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", TEST_AMOUNT);
        parameters.put("currency", TEST_CURRENCY);
        LyraClient.preparePayment(parameters);
    }

    @Test
    public void testPreparePayment() throws Exception {
        mockPreparePayment(HTTP_OK);
        PowerMockito.doReturn(String.format("{\"status\":\"%s\",\"answer\":{\"formToken\":\"%s\"}}"
                ,RESPONSE_STATUS_OK, TEST_FORM_TOKEN))
                .when(LyraClient.class, "readResponseContent", Mockito.any());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", TEST_AMOUNT);
        parameters.put("currency", TEST_CURRENCY);
        LyraClientResponse response = LyraClient.preparePayment(parameters);

        Assert.assertEquals(RESPONSE_STATUS_OK, response.getStatus());
    }

    @Test
    public void testClientResponseOK() {
        String completeResponseOK = String.format("{\"status\":\"%s\",\"applicationVersion\":\"%s\",\"answer\":{\"formToken\":\"%s\"}}"
                , RESPONSE_STATUS_OK, TEST_VERSION, TEST_FORM_TOKEN);
        LyraClientResponse responseOK = LyraClientResponse.fromResponseMessage(completeResponseOK);
        Assert.assertEquals(RESPONSE_STATUS_OK, responseOK.getStatus());
        Assert.assertEquals(TEST_VERSION, responseOK.getVersion());
        Assert.assertEquals(TEST_FORM_TOKEN, responseOK.getFormToken());
    }

    @Test
    public void testClientResponseKO() {
        String completeResponseKO = String.format("{\"status\":\"%s\",\"applicationVersion\":\"%s\",\"answer\":{\"errorCode\":\"%s\"}}"
                , RESPONSE_STATUS_ERROR, TEST_VERSION, TEST_ERROR_CODE);
        LyraClientResponse responseOK = LyraClientResponse.fromResponseMessage(completeResponseKO);
        Assert.assertEquals(RESPONSE_STATUS_ERROR, responseOK.getStatus());
        Assert.assertEquals(TEST_VERSION, responseOK.getVersion());
        Assert.assertNull(responseOK.getFormToken());
        Assert.assertEquals(TEST_ERROR_CODE, responseOK.getErrorCode());
    }

    @Test(expected = LyraClientException.class)
    public void testClientResponseNoAnswer() {
        String completeResponseException = String.format("{\"status\":\"%s\",\"applicationVersion\":\"%s\"}"
                , RESPONSE_STATUS_OK, TEST_VERSION);
        LyraClientResponse.fromResponseMessage(completeResponseException);
    }

    @Test(expected = LyraClientException.class)
    public void testClientResponseInvalidJSON() {
        String completeResponseException = String.format("{\"status\":\"%s\",\"applicationVersion\":\"%s\",\"answer\":{\"formToken\":\"%s\"}"
                , RESPONSE_STATUS_OK, TEST_VERSION, TEST_FORM_TOKEN);
        LyraClientResponse.fromResponseMessage(completeResponseException);
    }

    @Test
    public void testReadConfiguration() throws Exception {
        Properties configurationProperties =
                Whitebox.invokeMethod(LyraClient.class, "readDefaultConfiguration");

        Assert.assertNotNull("Could not read configuration. It is null", configurationProperties);
        Assert.assertFalse("Could not read configuration. It is empty", configurationProperties.isEmpty());

    }

    @Test
    public void testConfigurationBuilder() {
        LyraClientConfiguration configuration = LyraClientConfiguration.builder()
                .username("testBuilderUsername")
                .password("testBuilderPassword")
                .endpointUrl("testEndpointUrl")
                .proxyHost("testBuilderProxyHost")
                .proxyPort("testBuilderProxyPort")
                .connectionTimeout("testConnectionTimeout")
                .requestTimeout("testRequestTimeout")
                .build();

        Assert.assertEquals("testBuilderUsername", configuration.getUsername());
        Assert.assertEquals("testBuilderPassword", configuration.getPassword());
        Assert.assertEquals("testEndpointUrl", configuration.getEndpointUrl());
        Assert.assertEquals("testBuilderProxyHost", configuration.getProxyHost());
        Assert.assertEquals("testBuilderProxyPort", configuration.getProxyPort());
        Assert.assertEquals("testConnectionTimeout", configuration.getConnectionTimeout());
        Assert.assertEquals("testRequestTimeout", configuration.getRequestTimeout());
    }

    @Test
    public void testParametersOK() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", TEST_AMOUNT);
        parameters.put("currency", TEST_CURRENCY);
        Assert.assertTrue("Sent parameters should be OK", invokeCheckParameters(parameters));
    }

    @Test
    public void testParametersKO() throws Exception {
        //No parameters sent
        Map<String, Object> parameters = new HashMap<>();
        Assert.assertFalse("Sent parameters should be OK", invokeCheckParameters(parameters));

        //Only amount
        parameters = new HashMap<>();
        parameters.put("amount", TEST_AMOUNT);
        Assert.assertFalse("Sent parameters should be OK", invokeCheckParameters(parameters));

        //Only currency
        parameters = new HashMap<>();
        parameters.put("currency", TEST_CURRENCY);
        Assert.assertFalse("Sent parameters should be OK", invokeCheckParameters(parameters));
    }

    @Test
    public void testParameters() throws Exception {
        String expectedUsername = "testUsername";
        String expectedPassword = "testPassword";
        String expectedProxyHost = "testProxyHost";
        String expectedProxyPort = "testProxyPort";
        String expectedEndpointUrl = "testEndpointUrl";
        String expectedConnectionTimeout = "testConnectionTimeout";
        String expectedRequestTimeout = "testRequestTimeout";
        Properties defaultConfigurationProperties =
                Whitebox.invokeMethod(LyraClient.class, "readDefaultConfiguration");
        LyraClientConfiguration.LyraClientConfigurationBuilder configurationBuilder = LyraClientConfiguration.builder();

        Map<String, String> finalConfiguration = Whitebox.invokeMethod(
                LyraClient.class, "getFinalConfiguration", configurationBuilder.build());
        Assert.assertNotEquals(expectedUsername, finalConfiguration.get("username"));
        Assert.assertNotEquals(expectedPassword, finalConfiguration.get("password"));
        Assert.assertNotEquals(expectedProxyHost, finalConfiguration.get("proxyHost"));
        Assert.assertNotEquals(expectedProxyPort, finalConfiguration.get("proxyPort"));
        Assert.assertNotEquals(expectedEndpointUrl, finalConfiguration.get("endpointUrl"));
        Assert.assertNotEquals(expectedConnectionTimeout, finalConfiguration.get("connectionTimeout"));
        Assert.assertNotEquals(expectedRequestTimeout, finalConfiguration.get("requestTimeout"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("username"), finalConfiguration.get("username"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("password"), finalConfiguration.get("password"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("proxyHost"), finalConfiguration.get("proxyHost"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("proxyPort"), finalConfiguration.get("proxyPort"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("endpointUrl"), finalConfiguration.get("endpointUrl"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("connectionTimeout"), finalConfiguration.get("connectionTimeout"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("requestTimeout"), finalConfiguration.get("requestTimeout"));

        LyraClientConfiguration rightConfiguration = configurationBuilder
                .username(expectedUsername)
                .password(expectedPassword)
                .proxyHost(expectedProxyHost)
                .proxyPort(expectedProxyPort)
                .endpointUrl(expectedEndpointUrl)
                .connectionTimeout(expectedConnectionTimeout)
                .requestTimeout(expectedRequestTimeout)
                .build();
        finalConfiguration = Whitebox.invokeMethod(
                LyraClient.class, "getFinalConfiguration", rightConfiguration);
        Assert.assertEquals(expectedUsername, finalConfiguration.get("username"));
        Assert.assertEquals(expectedPassword, finalConfiguration.get("password"));
        Assert.assertEquals(expectedProxyHost, finalConfiguration.get("proxyHost"));
        Assert.assertEquals(expectedProxyPort, finalConfiguration.get("proxyPort"));
        Assert.assertEquals(expectedEndpointUrl, finalConfiguration.get("endpointUrl"));
        Assert.assertEquals(expectedConnectionTimeout, finalConfiguration.get("connectionTimeout"));
        Assert.assertEquals(expectedRequestTimeout, finalConfiguration.get("requestTimeout"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("username"), finalConfiguration.get("username"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("password"), finalConfiguration.get("password"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("proxyHost"), finalConfiguration.get("proxyHost"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("proxyPort"), finalConfiguration.get("proxyPort"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("endpointUrl"), finalConfiguration.get("endpointUrl"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("connectionTimeout"), finalConfiguration.get("connectionTimeout"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("requestTimeout"), finalConfiguration.get("requestTimeout"));
    }

    @Test
    public void testGenerateChargeUrl() throws Exception {
        String expected = "test/api-payment/" + LyraClient.SDK_VERSION + "/Charge/testResource";
        Map<String, String> configuration = new HashMap<>();
        String resource = "";

        String wrongUrl = Whitebox.invokeMethod(LyraClient.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertNotEquals(wrongUrl, expected);

        resource = "testResource";
        wrongUrl = Whitebox.invokeMethod(LyraClient.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertNotEquals(wrongUrl, expected);

        configuration.put("endpointUrl", "test");
        String rightUrl = Whitebox.invokeMethod(LyraClient.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertEquals(rightUrl, expected);
    }

    private void mockPreparePayment(int responseCode) throws Exception {
        PowerMockito.spy(LyraClient.class);
        HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
        PowerMockito.when(mockHttpURLConnection.getResponseCode()).thenReturn(responseCode);
        PowerMockito.doReturn(mockHttpURLConnection)
                .when(LyraClient.class, "createConnection", Mockito.any(), Mockito.any());
        PowerMockito.doNothing()
                .when(LyraClient.class, "sendRequestPayload", Mockito.any(), Mockito.any());
    }

    private boolean invokeCheckParameters(Map<String, Object> parameters) throws Exception {
        return Whitebox.invokeMethod(LyraClient.class, "checkParameters",
                parameters);
    }
}
