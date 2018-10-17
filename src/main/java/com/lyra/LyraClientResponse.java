package com.lyra;

import com.google.gson.JsonSyntaxException;
import lombok.Getter;

import java.util.Map;

/**
 * Class that encapsulates all response data<p/>
 *
 * This inmutable class can only be instantiated using the method {@link #fromResponseMessage(String)} that
 * build the data using the message retrieved from Payment Platform.
 *
 * @author Lyra Network
 */
@Getter
public class LyraClientResponse {
    private String status;
    private String version;

    //Field returned in case of success
    private String formToken;

    //Fields returned in case of error
    private String errorCode;
    private String errorDetails;

    //Cannot be instantiated directly
    private LyraClientResponse() {
    }

    /**
     * Creates an inmmutable {@link LyraClientResponse} object using the response message retrieved from
     * Payment Platform
     *
     * @param responseMessage JSON string containig all data returned by Payment Platform
     * @return à {@link LyraClientResponse} object
     */
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

    /**
     * Converts object state into JSON
     *
     * @return
     */
    public String toJson() {
        return LyraClient.GSON.toJson(this);
    }

    /**
     * String representation of this object. Same result as calling {@link #toJson()} method
     *
     * @return String containing the JSON representation of the object
     */
    @Override
    public String toString() {
        return toJson();
    }

    //Helper method that builds the complete error data
    private static String generateErrorData(Map<String, String> answer, String error, String extendedErrorData) {
        String errorMessage = answer.get(error);
        if (answer.get(extendedErrorData) != null) {
            errorMessage += " - " + answer.get(extendedErrorData);
        }
        return errorMessage;
    }
}