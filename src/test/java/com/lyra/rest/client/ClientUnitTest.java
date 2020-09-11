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
    public void testShouldThrowClientExceptionWhenCallPreparePaymentBadReturnCode() throws Exception {
        mockPreparePayment(HTTP_ERROR);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", TEST_AMOUNT);
        parameters.put("currency", TEST_CURRENCY);
        Client.post(ClientResource.CREATE_PAYMENT.toString(), parameters);
    }

    @Test
    public void testShouldReturnOkWhenCallPreparePaymentWithGoodParams() throws Exception {
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
    public void testShouldReadCondigurationFromFileWhenCallReadConfiguration() throws Exception {
        Properties configurationProperties =
                Whitebox.invokeMethod(Client.class, "readDefaultConfiguration");

        Assert.assertNotNull("Could not read configuration. It is null", configurationProperties);
        Assert.assertFalse("Could not read configuration. It is empty", configurationProperties.isEmpty());

    }

    @Test
    public void testShouldLoadConfigurationWhenUsingConfigurationBuilder() {
        ClientConfiguration configuration = ClientConfiguration.builder()
                .username("testBuilderUsername")
                .password("testBuilderPassword")
                .proxyHost("testBuilderProxyHost")
                .proxyPort("testBuilderProxyPort")
                .connectionTimeout("testConnectionTimeout")
                .requestTimeout("testRequestTimeout")
                .build();

        Assert.assertTrue("configuration property is invalid", "testBuilderUsername" == configuration.getUsername());
        Assert.assertTrue("configuration property is invalid", "testBuilderPassword" == configuration.getPassword());
        Assert.assertTrue("configuration property is invalid", "testBuilderProxyHost" == configuration.getProxyHost());
        Assert.assertTrue("configuration property is invalid", "testBuilderProxyPort" == configuration.getProxyPort());
        Assert.assertTrue("configuration property is invalid", "testConnectionTimeout" == configuration.getConnectionTimeout());
        Assert.assertTrue("configuration property is invalid", "testRequestTimeout" == configuration.getRequestTimeout());
    }

    @Test
    public void testShouldOverrideDefautConfigurationWithConfigurationBuilder() throws Exception {
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
        Assert.assertFalse("property value not expected", expectedUsername == finalConfiguration.get("username"));
        Assert.assertFalse("property value not expected", expectedPassword == finalConfiguration.get("password"));
        Assert.assertFalse("property value not expected", expectedProxyHost == finalConfiguration.get("proxyHost"));
        Assert.assertFalse("property value not expected", expectedProxyPort == finalConfiguration.get("proxyPort"));
        Assert.assertFalse("property value not expected", expectedConnectionTimeout == finalConfiguration.get("connectionTimeout"));
        Assert.assertFalse("property value not expected", expectedRequestTimeout == finalConfiguration.get("requestTimeout"));
        Assert.assertTrue("property value not expected", defaultConfigurationProperties.getProperty("username").equals(finalConfiguration.get("username")));
        Assert.assertTrue("property value not expected", defaultConfigurationProperties.getProperty("password").equals(finalConfiguration.get("password")));
        Assert.assertTrue("property value not expected", defaultConfigurationProperties.getProperty("proxyHost").equals(finalConfiguration.get("proxyHost")));
        Assert.assertTrue("property value not expected", defaultConfigurationProperties.getProperty("proxyPort").equals(finalConfiguration.get("proxyPort")));
        Assert.assertTrue("property value not expected", defaultConfigurationProperties.getProperty("connectionTimeout").equals(finalConfiguration.get("connectionTimeout")));
        Assert.assertTrue("property value not expected", defaultConfigurationProperties.getProperty("requestTimeout").equals(finalConfiguration.get("requestTimeout")));

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
        Assert.assertTrue("property value not expected", expectedUsername == finalConfiguration.get("username"));
        Assert.assertTrue("property value not expected", expectedPassword == finalConfiguration.get("password"));
        Assert.assertTrue("property value not expected", expectedProxyHost == finalConfiguration.get("proxyHost"));
        Assert.assertTrue("property value not expected", expectedProxyPort == finalConfiguration.get("proxyPort"));
        Assert.assertTrue("property value not expected", expectedConnectionTimeout == finalConfiguration.get("connectionTimeout"));
        Assert.assertTrue("property value not expected", expectedRequestTimeout == finalConfiguration.get("requestTimeout"));
        Assert.assertFalse("property value not expected", defaultConfigurationProperties.getProperty("username") == finalConfiguration.get("username"));
        Assert.assertFalse("property value not expected", defaultConfigurationProperties.getProperty("password") == finalConfiguration.get("password"));
        Assert.assertFalse("property value not expected", defaultConfigurationProperties.getProperty("proxyHost") == finalConfiguration.get("proxyHost"));
        Assert.assertFalse("property value not expected", defaultConfigurationProperties.getProperty("proxyPort") == finalConfiguration.get("proxyPort"));
        Assert.assertFalse("property value not expected", defaultConfigurationProperties.getProperty("connectionTimeout") == finalConfiguration.get("connectionTimeout"));
        Assert.assertFalse("property value not expected", defaultConfigurationProperties.getProperty("requestTimeout") == finalConfiguration.get("requestTimeout"));
    }

    @Test
    public void testShouldGenerateUrlWithGenerateChargeUrlMethod() throws Exception {
        String expected = "test/api-payment/" + Client.REST_API_VERSION + "/Charge/testResource";
        Map<String, String> configuration = new HashMap<>();
        String resource = "";

        configuration.put(ClientConfiguration.CONFIGURATION_KEY_REST_API_SERVER_NAME, "toto");
        String wrongUrl = Whitebox.invokeMethod(Client.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertFalse("URL not expected", expected == wrongUrl);

        resource = "Charge/testResource";
        wrongUrl = Whitebox.invokeMethod(Client.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertFalse("URL not expected", expected == wrongUrl);

        configuration.put(ClientConfiguration.CONFIGURATION_KEY_REST_API_SERVER_NAME, "test");
        String rightUrl = Whitebox.invokeMethod(Client.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertTrue("URL not expected", expected.equals(rightUrl));
    }

    @Test(expected = ClientException.class)
    public void testShouldThrowClientExceptionWhenAlgorithmIsNotSupported() throws Exception {
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
