package com.lyra.rest.client;

import com.lyra.rest.client.LyraClient;
import com.lyra.rest.client.LyraClientResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LyraClientIntegrationTests {

    private static final String RESPONSE_STATUS_SUCCESS = "SUCCESS";
    private static final String RESPONSE_STATUS_ERROR = "ERROR";

    @Test
    public void Should_ReturnOk_When_CallPreparePayment() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", 100);
        parameters.put("currency", 978);

        String result = LyraClient.post(LyraClientResource.CREATE_PAYMENT.getValue(), parameters);

        Map jsonResult = LyraClient.GSON.fromJson(result, Map.class);

        Assert.assertEquals(RESPONSE_STATUS_SUCCESS, jsonResult.get("status"));
        Assert.assertNotNull(jsonResult.get("version"));
        Assert.assertNotNull(jsonResult.get("answer"));
        Map<String, String> answer = (Map)jsonResult.get("answer");
        Assert.assertNotNull(answer.get("formToken"));
        Assert.assertNull(answer.get("errorCode"));
        Assert.assertNull(answer.get("errorDetails"));
    }

    @Test
    public void Should_ReturnError_When_CallPreparePaymentBadOption() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", 123);
        parameters.put("currency", 978);

        String result = LyraClient.post(LyraClientResource.CREATE_PAYMENT.getValue(), parameters);

        Map jsonResult = LyraClient.GSON.fromJson(result, Map.class);

        Assert.assertEquals(RESPONSE_STATUS_ERROR, jsonResult.get("status"));
        Assert.assertNotNull(jsonResult.get("version"));
        Assert.assertNotNull(jsonResult.get("answer"));
        Map<String, String> answer = (Map)jsonResult.get("answer");
        Assert.assertNull(answer.get("formToken"));
        Assert.assertNotNull(answer.get("errorCode"));
        Assert.assertNotNull(answer.get("errorMessage"));
    }
}
