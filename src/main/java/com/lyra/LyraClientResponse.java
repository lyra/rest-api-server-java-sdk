package com.lyra;

import com.google.gson.JsonSyntaxException;
import lombok.Getter;

import java.util.Map;

@Getter
public class LyraClientResponse {
    private String status;
    private String version;

    //Field returned in case of success
    private String formToken;

    //Fields returned in case of error
    private String errorCode;
    private String errorDetails;

    private LyraClientResponse() {
    }

    protected static LyraClientResponse fromResponseMessage(String responseMessage) {
        LyraClientResponse clientResponse = new LyraClientResponse();
        Map restApiResponseMessage;
        try {
            restApiResponseMessage = LyraClient.GSON.fromJson(responseMessage, Map.class);
        } catch (JsonSyntaxException jse) {
            throw new LyraClientException("Could not read CreatePayment message", jse);
        }

        if (restApiResponseMessage != null) {
            clientResponse.status = (String) restApiResponseMessage.get("status");
            clientResponse.version = (String) restApiResponseMessage.get("applicationVersion");

            Map<String, String> answer = (Map) restApiResponseMessage.get("answer");
            if (answer != null) {
                if ("SUCCESS".equals(clientResponse.status)) {
                    clientResponse.formToken = answer.get("formToken");
                } else {
                    clientResponse.errorCode =
                            generateErrorData(answer, "errorCode", "detailedErrorCode");
                }
                clientResponse.errorDetails =
                        generateErrorData(answer, "errorMessage", "detailedErrorMessage");
            } else {
                throw new LyraClientException("No answer data found in CreatePayment message");
            }
        } else {
            throw new LyraClientException("Could not read CreatePayment message");
        }

        return clientResponse;
    }

    public String toJson() {
        return LyraClient.GSON.toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }

    private static String generateErrorData(Map<String, String> answer, String error, String extendedErrorData) {
        String errorMessage = answer.get(error);
        if (answer.get(extendedErrorData) != null) {
            errorMessage += " - " + answer.get(extendedErrorData);
        }
        return errorMessage;
    }
}