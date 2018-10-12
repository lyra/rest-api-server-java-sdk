package com.lyra;

import com.lyra.config.LyraClientConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.Properties;

public class LyraClientUnitTests {

    @Test
    public void testReadConfiguration() throws Exception {
        LyraClient client = new LyraClient();

        Properties configurationProperties =
                Whitebox.invokeMethod(client, "readDefaultConfiguration");

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
}
