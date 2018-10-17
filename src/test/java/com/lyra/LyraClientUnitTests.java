package com.lyra;

import com.lyra.config.LyraClientConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LyraClientUnitTests {

    @Test
    public void testReadConfiguration() throws Exception {
        LyraClient client = new LyraClient();

        Properties configurationProperties =
                Whitebox.invokeMethod(LyraClient.class, "readDefaultConfiguration");

        Assert.assertNotNull("Could not read configuration. It is null", configurationProperties);
        Assert.assertFalse("Could not read configuration. It is empty", configurationProperties.isEmpty());

    }

    @Test
    public void testConfigurationBuilder() throws Exception {
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
    public void testParametersOk() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", 100);
        parameters.put("currency", 978);
        Assert.assertTrue("Sent parameters should be OK", invokeCheckParameters(parameters));
    }

    @Test
    public void testParametersKO() throws Exception {
        //No parameters sent
        Map<String, Object> parameters = new HashMap<>();
        Assert.assertFalse("Sent parameters should be OK", invokeCheckParameters(parameters));

        //Only amount
        parameters = new HashMap<>();
        parameters.put("amount", 100);
        Assert.assertFalse("Sent parameters should be OK", invokeCheckParameters(parameters));

        //Only currency
        parameters = new HashMap<>();
        parameters.put("currency", 100);
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

        String wrongUrl =  Whitebox.invokeMethod(LyraClient.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertNotEquals(wrongUrl, expected);

        resource = "testResource";
        wrongUrl =  Whitebox.invokeMethod(LyraClient.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertNotEquals(wrongUrl, expected);

        configuration.put("endpointUrl", "test");
        String rightUrl =  Whitebox.invokeMethod(LyraClient.class, "generateChargeUrl",
                resource, configuration);
        Assert.assertEquals(rightUrl, expected);
    }

    private boolean invokeCheckParameters(Map<String, Object> parameters) throws Exception {
        return Whitebox.invokeMethod(LyraClient.class, "checkParameters",
                parameters);
    }
}
