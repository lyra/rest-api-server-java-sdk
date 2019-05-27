package com.lyra.rest.client;

import com.lyra.rest.client.LyraClient;
import com.lyra.rest.client.LyraClientResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LyraClientIntegrationTest {
    private static final String TEST_USERNAME = "91335531";
    private static final String TEST_PWD = "testpassword_8Z3eXc7GCXb51kskD3sKCMjnJCyURmUneRwwp8KW8nyu0";
    private static final String TEST_DOMAIN = "http://192.168.216.27:5556";

    private static final String RESPONSE_STATUS_SUCCESS = "SUCCESS";
    private static final String RESPONSE_STATUS_ERROR = "ERROR";

    @SuppressWarnings("unchecked")
	@Test
    public void Should_ReturnOk_When_CallPreparePayment() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", 100);
        parameters.put("currency", 978);
        parameters.put("domain", TEST_DOMAIN);

        String result = LyraClient.post(LyraClientResource.CREATE_PAYMENT.toString(), parameters, prepareConfiguration());

        Map<?, ?> jsonResult = LyraClient.GSON.fromJson(result, Map.class);

        Assert.assertEquals(RESPONSE_STATUS_SUCCESS, jsonResult.get("status"));
        Assert.assertNotNull(jsonResult.get("version"));
        Assert.assertNotNull(jsonResult.get("answer"));
        Map<String, String> answer = (Map<String, String>)jsonResult.get("answer");
        Assert.assertNotNull(answer.get("formToken"));
        Assert.assertNull(answer.get("errorCode"));
        Assert.assertNull(answer.get("errorDetails"));
    }

    @SuppressWarnings("unchecked")
	@Test
    public void Should_ReturnError_When_CallPreparePaymentBadOption() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", 123);
        parameters.put("currency", 978);
        parameters.put("domain", TEST_DOMAIN);

        String result = LyraClient.post(LyraClientResource.CREATE_PAYMENT.toString(), parameters, prepareConfiguration());

        Map<?, ?> jsonResult = LyraClient.GSON.fromJson(result, Map.class);

        Assert.assertEquals(RESPONSE_STATUS_ERROR, jsonResult.get("status"));
        Assert.assertNotNull(jsonResult.get("version"));
        Assert.assertNotNull(jsonResult.get("answer"));
        Map<String, String> answer = (Map<String, String>)jsonResult.get("answer");
        Assert.assertNull(answer.get("formToken"));
        Assert.assertNotNull(answer.get("errorCode"));
        Assert.assertNotNull(answer.get("errorMessage"));
    }

    private LyraClientConfiguration prepareConfiguration() {
        return LyraClientConfiguration.builder()
                .username(TEST_USERNAME)
                .password(TEST_PWD)
                .build();
    }
}
