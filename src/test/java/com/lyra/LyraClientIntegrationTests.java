package com.lyra;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LyraClientIntegrationTests {

    @Test
    public void testProcessPayment() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", 100);

        String result = LyraClient.preparePayment(parameters);
    }
}
