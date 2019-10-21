package com.lyra.rest.client;

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

@PrepareForTest(Client.class)
@RunWith(PowerMockRunner.class)
public class ClientUnitTest {
    private static final Integer TEST_AMOUNT = 100;
    private static final Integer TEST_CURRENCY = 100;

    private static final Integer HTTP_OK = 200;
    private static final Integer HTTP_ERROR = 500;

    private static final String RESPONSE_STATUS_SUCCESS = "SUCCESS";
    private static final String TEST_FORM_TOKEN = "02NWE0MmU1ZDItZTNkMS00ODQ3LTkyMTAtZTJjZDA2NzQ0YWVlew0KCSJhb";

    @Test(expected = ClientException.class)
    public void Should_ThrowClientException_When_CallPreparePaymentBadReturnCode() throws Exception {
        mockPreparePayment(HTTP_ERROR);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", TEST_AMOUNT);
        parameters.put("currency", TEST_CURRENCY);
        Client.post(ClientResource.CREATE_PAYMENT.toString(), parameters);
    }

    @Test
    public void Should_ReturnOk_When_CallPreparePaymentWithGoodParams() throws Exception {
        mockPreparePayment(HTTP_OK);
        PowerMockito.doReturn(String.format("{\"status\":\"%s\",\"answer\":{\"formToken\":\"%s\"}}"
                , RESPONSE_STATUS_SUCCESS, TEST_FORM_TOKEN))
                .when(Client.class, "readResponseContent", Mockito.any());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", TEST_AMOUNT);
        parameters.put("currency", TEST_CURRENCY);
        String response = Client.post(ClientResource.CREATE_PAYMENT.toString(), parameters);

        Map<?, ?> jsonResponse = Client.GSON.fromJson(response, Map.class);
        Assert.assertEquals(RESPONSE_STATUS_SUCCESS, jsonResponse.get("status"));
    }

    @Test
    public void Should_ReadCondigurationFromFile_When_CallReadConfiguration() throws Exception {
        Properties configurationProperties =
                Whitebox.invokeMethod(Client.class, "readDefaultConfiguration");

        Assert.assertNotNull("Could not read configuration. It is null", configurationProperties);
        Assert.assertFalse("Could not read configuration. It is empty", configurationProperties.isEmpty());

    }

    @Test
    public void Should_LoadConfiguration_When_UsingConfigurationBuilder() {
        ClientConfiguration configuration = ClientConfiguration.builder()
                .username("testBuilderUsername")
                .password("testBuilderPassword")
                .proxyHost("testBuilderProxyHost")
                .proxyPort("testBuilderProxyPort")
                .connectionTimeout("testConnectionTimeout")
                .requestTimeout("testRequestTimeout")
                .build();

        Assert.assertEquals("testBuilderUsername", configuration.getUsername());
        Assert.assertEquals("testBuilderPassword", configuration.getPassword());
        Assert.assertEquals("testBuilderProxyHost", configuration.getProxyHost());
        Assert.assertEquals("testBuilderProxyPort", configuration.getProxyPort());
        Assert.assertEquals("testConnectionTimeout", configuration.getConnectionTimeout());
        Assert.assertEquals("testRequestTimeout", configuration.getRequestTimeout());
    }

    @Test
    public void Should_OverrideDefautConfiguration_WithConfigurationBuilder() throws Exception {
        String expectedUsername = "testUsername";
        String expectedPassword = "testPassword";
        String expectedProxyHost = "testProxyHost";
        String expectedProxyPort = "testProxyPort";
        String expectedConnectionTimeout = "testConnectionTimeout";
        String expectedRequestTimeout = "testRequestTimeout";
        Properties defaultConfigurationProperties =
                Whitebox.invokeMethod(Client.class, "readDefaultConfiguration");
        ClientConfiguration.ClientConfigurationBuilder configurationBuilder = ClientConfiguration.builder();

        Map<String, String> finalConfiguration = Whitebox.invokeMethod(
                Client.class, "getFinalConfiguration", configurationBuilder.build());
        Assert.assertNotEquals(expectedUsername, finalConfiguration.get("username"));
        Assert.assertNotEquals(expectedPassword, finalConfiguration.get("password"));
        Assert.assertNotEquals(expectedProxyHost, finalConfiguration.get("proxyHost"));
        Assert.assertNotEquals(expectedProxyPort, finalConfiguration.get("proxyPort"));
        Assert.assertNotEquals(expectedConnectionTimeout, finalConfiguration.get("connectionTimeout"));
        Assert.assertNotEquals(expectedRequestTimeout, finalConfiguration.get("requestTimeout"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("username"), finalConfiguration.get("username"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("password"), finalConfiguration.get("password"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("proxyHost"), finalConfiguration.get("proxyHost"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("proxyPort"), finalConfiguration.get("proxyPort"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("connectionTimeout"), finalConfiguration.get("connectionTimeout"));
        Assert.assertEquals(defaultConfigurationProperties.getProperty("requestTimeout"), finalConfiguration.get("requestTimeout"));

        ClientConfiguration rightConfiguration = configurationBuilder
                .username(expectedUsername)
                .password(expectedPassword)
                .proxyHost(expectedProxyHost)
                .proxyPort(expectedProxyPort)
                .connectionTimeout(expectedConnectionTimeout)
                .requestTimeout(expectedRequestTimeout)
                .build();
        finalConfiguration = Whitebox.invokeMethod(
                Client.class, "getFinalConfiguration", rightConfiguration);
        Assert.assertEquals(expectedUsername, finalConfiguration.get("username"));
        Assert.assertEquals(expectedPassword, finalConfiguration.get("password"));
        Assert.assertEquals(expectedProxyHost, finalConfiguration.get("proxyHost"));
        Assert.assertEquals(expectedProxyPort, finalConfiguration.get("proxyPort"));
        Assert.assertEquals(expectedConnectionTimeout, finalConfiguration.get("connectionTimeout"));
        Assert.assertEquals(expectedRequestTimeout, finalConfiguration.get("requestTimeout"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("username"), finalConfiguration.get("username"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("password"), finalConfiguration.get("password"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("proxyHost"), finalConfiguration.get("proxyHost"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("proxyPort"), finalConfiguration.get("proxyPort"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("connectionTimeout"), finalConfiguration.get("connectionTimeout"));
        Assert.assertNotEquals(defaultConfigurationProperties.getProperty("requestTimeout"), finalConfiguration.get("requestTimeout"));
    }

    @Test
    public void Should_GenerateUrl_WithGenerateChargeUrlMethod() throws Exception {
        String expected = "test/api-payment/" + Client.REST_API_VERSION + "/Charge/testResource";
        Map<String, String> configuration = new HashMap<>();
        String resource = "";

        configuration.put(ClientConfiguration.CONFIGURATION_KEY_SERVER_NAME, "toto");
        String wrongUrl = Whitebox.invokeMethod(Client.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertNotEquals(expected, wrongUrl);

        resource = "Charge/testResource";
        wrongUrl = Whitebox.invokeMethod(Client.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertNotEquals(expected, wrongUrl);

        configuration.put(ClientConfiguration.CONFIGURATION_KEY_SERVER_NAME, "test");
        String rightUrl = Whitebox.invokeMethod(Client.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertEquals(expected, rightUrl);
    }

    @Test(expected = ClientException.class)
    public void Should_ThrowLyraException_When_AlgorithmIsNotSupported() throws Exception {
        Map<String, Object> answer = new HashMap<>();
        answer.put("kr-answer", "The quick brown fox jumps over the lazy dog");
        answer.put("kr-hash-algorithm", "sha1");
        answer.put("kr-hash", "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8");

        Client.verifyAnswer(answer);
    }

    private void mockPreparePayment(int responseCode) throws Exception {
        PowerMockito.spy(Client.class);
        HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
        PowerMockito.when(mockHttpURLConnection.getResponseCode()).thenReturn(responseCode);
        PowerMockito.doReturn(mockHttpURLConnection)
                .when(Client.class, "createConnection", Mockito.any(), Mockito.any());
        PowerMockito.doNothing()
                .when(Client.class, "sendRequestPayload", Mockito.any(), Mockito.any());
    }
}
