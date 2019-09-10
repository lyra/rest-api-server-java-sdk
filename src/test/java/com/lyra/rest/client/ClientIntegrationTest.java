package com.lyra.rest.client;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@PrepareForTest(Client.class)
@RunWith(PowerMockRunner.class)
public class ClientIntegrationTest {
    private static final String TEST_USERNAME = "91335531";
    private static final String TEST_PWD = "testpassword_DEMOPRIVATEKEY23G4475zXZQ2UA5x7M";
    private static final String MOCKED_DOMAIN = "MOCKED_DOMAIN";
    private static final String TEST_DOMAIN = MOCKED_DOMAIN;

    private static final String BAD_CURRENCY = "BAD";

    private static final String RESPONSE_STATUS_SUCCESS = "SUCCESS";
    private static final String RESPONSE_STATUS_ERROR = "ERROR";

    private static final String MOCK_RESPONSE_OK = "{\"webService\":\"Charge/CreatePayment\",\"version\":\"V4\",\"applicationVersion\":\"4.10.0\",\"status\":\"SUCCESS\",\"answer\":{\"formToken\":\"fdtJCCzEwCSRm43PLaxgDpZw195eyJhbW91bnQiOjEwMCwiY3VycmVuY3kiOiJFVVIiLCJtb2RlIjoiVEVTVCIsInZlcnNpb24iOjMsInNob3BOYW1lIjoiTHlyYSBTTVMiLCJicmFuZFByaW9yaXR5IjpbIkJBTkNPTlRBQ1QiLCJDQiIsIkUtQ0FSVEVCTEVVRSIsIk1BU1RFUkNBUkQiLCJNQUVTVFJPIiwiVklTQSIsIlZJU0FfRUxFQ1RST04iXSwiY2F0ZWdvcmllcyI6eyJkZWJpdENyZWRpdENhcmRzIjp7ImFwcElkIjoiY2FyZHMiLCJwYXJhbSI6WyJNQUVTVFJPIiwiQU1FWCIsIkUtQ0FSVEVCTEVVRSIsIk1BU1RFUkNBUkQiLCJKQ0IiLCJWSVNBIiwiQkFOQ09OVEFDVCIsIlZJU0FfRUxFQ1RST04iLCJDQiIsIkRJTkVSUyJdfX0sImNhcmRzIjp7IkFNRVgiOnsiZmllbGRzIjp7InNlY3VyaXR5Q29kZSI6eyJtYXhMZW5ndGgiOjR9fSwiY29weUZyb20iOiJjYXJkcy5ERUZBVUxUIn0sIk1BRVNUUk8iOnsiZmllbGRzIjp7InNlY3VyaXR5Q29kZSI6eyJyZXF1aXJlZCI6ZmFsc2V9fSwiY29weUZyb20iOiJjYXJkcy5ERUZBVUxUIn0sIkUtQ0FSVEVCTEVVRSI6eyJjb3B5RnJvbSI6ImNhcmRzLkRFRkFVTFQifSwiTUFTVEVSQ0FSRCI6eyJjb3B5RnJvbSI6ImNhcmRzLkRFRkFVTFQifSwiSkNCIjp7ImZpZWxkcyI6eyJzZWN1cml0eUNvZGUiOnsicmVxdWlyZWQiOmZhbHNlLCJoaWRkZW4iOnRydWV9fSwiY29weUZyb20iOiJjYXJkcy5ERUZBVUxUIn0sIlZJU0EiOnsiY29weUZyb20iOiJjYXJkcy5ERUZBVUxUIn0sIkJBTkNPTlRBQ1QiOnsiZmllbGRzIjp7InNlY3VyaXR5Q29kZSI6eyJyZXF1aXJlZCI6ZmFsc2UsImhpZGRlbiI6dHJ1ZX19LCJjb3B5RnJvbSI6ImNhcmRzLkRFRkFVTFQifSwiVklTQV9FTEVDVFJPTiI6eyJmaWVsZHMiOnsic2VjdXJpdHlDb2RlIjp7InJlcXVpcmVkIjpmYWxzZX19LCJjb3B5RnJvbSI6ImNhcmRzLkRFRkFVTFQifSwiREVGQVVMVCI6eyJmaWVsZHMiOnsicGFuIjp7Im1pbkxlbmd0aCI6MTAsIm1heExlbmd0aCI6MTksInZhbGlkYXRvcnMiOlsiTlVNRVJJQyIsIkxVSE4iXSwicmVxdWlyZWQiOnRydWUsInNlbnNpdGl2ZSI6dHJ1ZSwiaGlkZGVuIjpmYWxzZSwiY2xlYXJPbkVycm9yIjpmYWxzZX0sImV4cGlyeURhdGUiOnsicmVxdWlyZWQiOnRydWUsInNlbnNpdGl2ZSI6dHJ1ZSwiaGlkZGVuIjpmYWxzZSwiY2xlYXJPbkVycm9yIjpmYWxzZX0sInNlY3VyaXR5Q29kZSI6eyJtaW5MZW5ndGgiOjMsIm1heExlbmd0aCI6MywidmFsaWRhdG9ycyI6WyJOVU1FUklDIl0sInJlcXVpcmVkIjp0cnVlLCJzZW5zaXRpdmUiOnRydWUsImhpZGRlbiI6ZmFsc2UsImNsZWFyT25FcnJvciI6dHJ1ZX19fSwiRElORVJTIjp7ImZpZWxkcyI6eyJzZWN1cml0eUNvZGUiOnsicmVxdWlyZWQiOmZhbHNlfX0sImNvcHlGcm9tIjoiY2FyZHMuREVGQVVMVCJ9LCJDQiI6eyJjb3B5RnJvbSI6ImNhcmRzLkRFRkFVTFQifX19ba02\",\"_type\":\"V4/Charge/PaymentForm\"},\"ticket\":null,\"serverDate\":\"2019-05-28T13:02:49+00:00\",\"applicationProvider\":\"PAYZEN\",\"metadata\":null,\"_type\":\"V4/WebService/Response\"}";
    private static final String MOCK_RESPONSE_KO = "{\"webService\":\"Charge/CreatePayment\",\"version\":\"V4\",\"applicationVersion\":\"4.10.0\",\"status\":\"ERROR\",\"answer\":{\"errorCode\":\"INT_010\",\"errorMessage\":\"invalid currency\",\"detailedErrorCode\":null,\"detailedErrorMessage\":\"Invalid input value [name=currency, value=TTT]\",\"ticket\":\"null\",\"_type\":\"V4/WebService/WebServiceError\"},\"ticket\":null,\"serverDate\":\"2019-05-28T13:04:21+00:00\",\"applicationProvider\":\"PAYZEN\",\"metadata\":null,\"_type\":\"V4/WebService/Response\"}";

    @SuppressWarnings("unchecked")
	@Test
        public void Should_ReturnOk_When_CallPreparePayment() throws Exception {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("amount", 100);
            parameters.put("currency", "EUR");
            parameters.put("domain", TEST_DOMAIN);

            String result = getPreparePaymentResponse(parameters);

            Map<?, ?> jsonResult = Client.GSON.fromJson(result, Map.class);

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
    public void Should_ReturnError_When_CallPreparePaymentBadCurrency() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("amount", 100);
        parameters.put("currency", BAD_CURRENCY);
        parameters.put("domain", TEST_DOMAIN);

        String result = getPreparePaymentResponse(parameters);

        Map<?, ?> jsonResult = Client.GSON.fromJson(result, Map.class);

        Assert.assertEquals(RESPONSE_STATUS_ERROR, jsonResult.get("status"));
        Assert.assertNotNull(jsonResult.get("version"));
        Assert.assertNotNull(jsonResult.get("answer"));
        Map<String, String> answer = (Map<String, String>)jsonResult.get("answer");
        Assert.assertNull(answer.get("formToken"));
        Assert.assertNotNull(answer.get("errorCode"));
        Assert.assertNotNull(answer.get("errorMessage"));
    }

    private ClientConfiguration prepareConfiguration() {
        return ClientConfiguration.builder()
                .username(TEST_USERNAME)
                .password(TEST_PWD)
                .endpointDomain(TEST_DOMAIN)
                .build();
    }

    private String getPreparePaymentResponse(Map<String, Object> parameters) throws Exception {
        if (MOCKED_DOMAIN.equals(parameters.get("domain"))) { //Mock mode
            PowerMockito.spy(Client.class);
            URL mockedURL = PowerMockito.mock(URL.class);
            HttpURLConnection mockedUrlConnection = PowerMockito.mock(HttpURLConnection.class);
            PowerMockito.when(mockedUrlConnection.getResponseCode()).thenReturn(200);

            PowerMockito.doReturn(mockedURL)
                    .when(Client.class, "getURLToConnect", Mockito.any(), Mockito.any());
            PowerMockito.doReturn(mockedUrlConnection)
                    .when(Client.class, "getConnection", Mockito.any(), Mockito.any());
            PowerMockito.doNothing().when(Client.class, "sendRequestPayload", Mockito.any(), Mockito.any());
            if (BAD_CURRENCY.equals(parameters.get("currency"))) {
                PowerMockito.doReturn(MOCK_RESPONSE_KO)
                        .when(Client.class, "readResponseContent", Mockito.any());
            } else {
                PowerMockito.doReturn(MOCK_RESPONSE_OK)
                        .when(Client.class, "readResponseContent", Mockito.any());
            }
        }

        return Client.post(ClientResource.CREATE_PAYMENT.toString(), parameters, prepareConfiguration());
    }
}
