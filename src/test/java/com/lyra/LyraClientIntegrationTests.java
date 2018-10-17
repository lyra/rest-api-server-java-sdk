package com.lyra;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LyraClientIntegrationTests {

    @Test
    public void testPreparePaymentOK() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", 100);
        parameters.put("currency", 978);

        LyraClientResponse result = LyraClient.preparePayment(parameters);

        Assert.assertEquals("SUCCESS", result.getStatus());
        Assert.assertNotNull(result.getFormToken());
        Assert.assertNotNull(result.getVersion());
        Assert.assertNull(result.getErrorCode());
        Assert.assertNull(result.getErrorDetails());
    }

    @Test
    public void testPreparePaymentBadOption() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", 123);
        parameters.put("currency", 978);

        LyraClientResponse result = LyraClient.preparePayment(parameters);

        Assert.assertEquals("ERROR", result.getStatus());
        Assert.assertNotNull(result.getVersion());
        Assert.assertNotNull(result.getErrorCode());
        Assert.assertEquals("PSP_100", result.getErrorCode());
        Assert.assertNotNull(result.getErrorDetails());
        Assert.assertNull(result.getFormToken());

    }
}
