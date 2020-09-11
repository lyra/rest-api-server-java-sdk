package com.lyra.rest.client;

import org.junit.Assert;
import org.junit.Test;

public class ClientCryptUtilTest {
    static String REAL_ANSWER = "{\"shopId\":\"33148340\",\"orderCycle\":\"CLOSED\",\"orderStatus\":\"PAID\",\"serverDate\":\"2018-10-04T06:02:27+00:00\",\"orderDetails\":{\"orderTotalAmount\":990,\"orderCurrency\":\"EUR\",\"mode\":\"TEST\",\"orderId\":\"myOrderId-253795\",\"_type\":\"V4\\/OrderDetails\"},\"customer\":{\"billingDetails\":{\"address\":null,\"category\":null,\"cellPhoneNumber\":null,\"city\":null,\"country\":null,\"district\":null,\"firstName\":null,\"identityCode\":null,\"language\":\"EN\",\"lastName\":null,\"phoneNumber\":null,\"state\":null,\"streetNumber\":null,\"title\":null,\"zipCode\":null,\"_type\":\"V4\\/Customer\\/BillingDetails\"},\"email\":\"sample@example.com\",\"reference\":null,\"shippingDetails\":{\"address\":null,\"address2\":null,\"category\":null,\"city\":null,\"country\":null,\"deliveryCompanyName\":null,\"district\":null,\"firstName\":null,\"identityCode\":null,\"lastName\":null,\"legalName\":null,\"phoneNumber\":null,\"shippingMethod\":null,\"shippingSpeed\":null,\"state\":null,\"streetNumber\":null,\"zipCode\":null,\"_type\":\"V4\\/Customer\\/ShippingDetails\"},\"extraDetails\":{\"browserAccept\":null,\"fingerPrintId\":null,\"ipAddress\":\"192.168.216.124\",\"browserUserAgent\":\"Mozilla\\/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/69.0.3497.100 Safari\\/537.36\",\"_type\":\"V4\\/Customer\\/ExtraDetails\"},\"shoppingCart\":{\"insuranceAmount\":null,\"shippingAmount\":null,\"taxAmount\":null,\"cartItemInfo\":null,\"_type\":\"V4\\/Customer\\/ShoppingCart\"},\"_type\":\"V4\\/Customer\\/Customer\"},\"transactions\":[{\"shopId\":\"33148340\",\"uuid\":\"eb9de25234ab49cb848eef844c0f06c4\",\"amount\":990,\"currency\":\"EUR\",\"paymentMethodType\":\"CARD\",\"paymentMethodToken\":null,\"status\":\"PAID\",\"detailedStatus\":\"AUTHORISED\",\"operationType\":\"DEBIT\",\"effectiveStrongAuthentication\":\"DISABLED\",\"creationDate\":\"2018-10-04T06:02:25+00:00\",\"errorCode\":null,\"errorMessage\":null,\"detailedErrorCode\":null,\"detailedErrorMessage\":null,\"metadata\":null,\"transactionDetails\":{\"liabilityShift\":\"NO\",\"effectiveAmount\":990,\"effectiveCurrency\":\"EUR\",\"creationContext\":\"CHARGE\",\"cardDetails\":{\"paymentSource\":\"EC\",\"manualValidation\":\"NO\",\"expectedCaptureDate\":\"2018-10-04T06:02:20+00:00\",\"effectiveBrand\":\"CB\",\"pan\":\"497010XXXXXX0055\",\"expiryMonth\":11,\"expiryYear\":2021,\"country\":\"FR\",\"emisorCode\":null,\"effectiveProductCode\":\"G1\",\"legacyTransId\":\"904674\",\"legacyTransDate\":\"2018-10-04T06:01:49+00:00\",\"paymentMethodSource\":\"NEW\",\"authorizationResponse\":{\"amount\":990,\"currency\":\"EUR\",\"authorizationDate\":\"2018-10-04T06:02:25+00:00\",\"authorizationNumber\":\"3fe7a3\",\"authorizationResult\":\"0\",\"authorizationMode\":\"FULL\",\"_type\":\"V4\\/PaymentMethod\\/Details\\/Cards\\/CardAuthorizationResponse\"},\"captureResponse\":{\"refundAmount\":null,\"captureDate\":null,\"captureFileNumber\":null,\"refundCurrency\":null,\"_type\":\"V4\\/PaymentMethod\\/Details\\/Cards\\/CardCaptureResponse\"},\"threeDSResponse\":{\"authenticationResultData\":{\"transactionCondition\":\"COND_3D_ERROR\",\"enrolled\":\"UNKNOWN\",\"status\":\"UNKNOWN\",\"eci\":null,\"xid\":null,\"cavvAlgorithm\":null,\"cavv\":null,\"signValid\":null,\"brand\":\"VISA\",\"_type\":\"V4\\/PaymentMethod\\/Details\\/Cards\\/CardAuthenticationResponse\"},\"_type\":\"V4\\/PaymentMethod\\/Details\\/Cards\\/ThreeDSResponse\"},\"installmentNumber\":null,\"markAuthorizationResponse\":{\"amount\":null,\"currency\":null,\"authorizationDate\":null,\"authorizationNumber\":null,\"authorizationResult\":null,\"_type\":\"V4\\/PaymentMethod\\/Details\\/Cards\\/MarkAuthorizationResponse\"},\"_type\":\"V4\\/PaymentMethod\\/Details\\/CardDetails\"},\"parentTransactionUuid\":null,\"mid\":\"1549425\",\"sequenceNumber\":1,\"_type\":\"V4\\/TransactionDetails\"},\"_type\":\"V4\\/PaymentTransaction\"}],\"_type\":\"V4\\/Payment\"}";
    static String REAL_SIGNATURE = "f7a0bc10032567b2b8a82fca256639d76c897ee18ca28adfd0e3ccd08d753cd2";
    static String HASH_KEY = "ktM7bSeTJpclvpm4eEE9N0LIyoxUvsQ9AAYbQI1xQx7Qh";
    static String EMPTY_ANSWER_SIGNATURE = "a95c2b13d50d57858ff38e7abd76c39d644fd5d1cfdcc360e4c61f2fc48d4a5e";

    @Test
    public void testShouldValidateSignatureWhenProvidedSignatureIsCorrect() {
        String result = ClientCryptUtil.calculateHash(REAL_ANSWER, HASH_KEY, ClientCryptUtil.ALGORITHM_HMAC_SHA256);
        Assert.assertTrue("Signature not valid", REAL_SIGNATURE.equals(result));
    }

    @Test
    public void testShouldRefuseSignatureWhenProvidedSignatureIsInCorrect() {
        String result = ClientCryptUtil.calculateHash("CHANGE_" + REAL_ANSWER, HASH_KEY, ClientCryptUtil.ALGORITHM_HMAC_SHA256);
        Assert.assertFalse("Signature must be refused", REAL_SIGNATURE.equals(result));
    }

    @Test
    public void testShouldApplyDefaultAlgorithmWhenAlgorithmIsNotRecognized() {
        String result = ClientCryptUtil.calculateHash(REAL_ANSWER, HASH_KEY, "sha256");
        Assert.assertTrue("Should apply default algorithm", REAL_SIGNATURE.equals(result));
    }

    @Test(expected = ClientException.class)
    public void testShouldThrowExceptionWhenKeyIsEmpty() {
        String result = ClientCryptUtil.calculateHash(REAL_ANSWER, "", ClientCryptUtil.ALGORITHM_HMAC_SHA256);
        Assert.assertTrue("This code should not happen because exception must be thrown before", REAL_SIGNATURE ==  result);
    }

    @Test
    public void testShouldEncodeAnywayWhenAnswerIsEmpty() {
        String result = ClientCryptUtil.calculateHash("", HASH_KEY, ClientCryptUtil.ALGORITHM_HMAC_SHA256);
        Assert.assertTrue("Should encode anyway", EMPTY_ANSWER_SIGNATURE.equals(result));
    }

    @Test(expected = ClientException.class)
    public void testShouldThrowExceptionWhenAnswerIsNull() {
        String result = ClientCryptUtil.calculateHash(null, HASH_KEY, ClientCryptUtil.ALGORITHM_HMAC_SHA256);
        Assert.assertTrue("This code should not happen because exception must be thrown before", EMPTY_ANSWER_SIGNATURE == result);
    }

    @Test(expected = ClientException.class)
    public void testShouldThrowExceptionWhenKeyIsNull() {
        String result = ClientCryptUtil.calculateHash(REAL_ANSWER, null, ClientCryptUtil.ALGORITHM_HMAC_SHA256);
        Assert.assertTrue("This code should not happen because exception must be thrown before", EMPTY_ANSWER_SIGNATURE == result);
    }
}
