package com.lyra;

import com.lyra.config.LyraRestClientConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.Properties;

public class RestSdkUnitTests {

    @Test
    public void testReadConfiguration() throws Exception {
        LyraRestClient client = new LyraRestClient();

        Properties configurationProperties =
                Whitebox.invokeMethod(client, "readDefaultConfiguration");

        Assert.assertNotNull("Could not read configuration. It is null", configurationProperties);
        Assert.assertFalse("Could not read configuration. It is empty", configurationProperties.isEmpty());

    }

    @Test
    public void testConfigurationBuilder() throws Exception {
        LyraRestClientConfiguration configuration = LyraRestClientConfiguration.builder()
                .username("testBuilderUsername")
                .password("testBuilderPassword")
                .publicKey("testBuilderPublicKey")
                .hashKey("testBuilderHashKey")
                .proxyHost("testBuilderProxyHost")
                .proxyPort("testBuilderProxyPort")
                .endpoint("testBuilderEndpoint")
                .clientEndpoint("testBuilderClientEndpoint")
                .build();

        Assert.assertEquals("testBuilderUsername", configuration.getUsername());
        Assert.assertEquals("testBuilderPassword", configuration.getPassword());
        Assert.assertEquals("testBuilderPublicKey", configuration.getPublicKey());
        Assert.assertEquals("testBuilderHashKey", configuration.getHashKey());
        Assert.assertEquals("testBuilderProxyHost", configuration.getProxyHost());
        Assert.assertEquals("testBuilderProxyPort", configuration.getProxyPort());
        Assert.assertEquals("testBuilderEndpoint", configuration.getEndpoint());
        Assert.assertEquals("testBuilderClientEndpoint", configuration.getClientEndpoint());
    }
}
