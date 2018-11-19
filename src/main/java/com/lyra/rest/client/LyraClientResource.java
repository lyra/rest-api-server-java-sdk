package com.lyra.rest.client;

/**
 * Enum that contains the different resources from Rest API that can be called<p/>
 *
 * Note that the {@link #toString()} method is overridden and returns the exact value of REST resource
 *
 * @author Lyra Network
 */
public enum LyraClientResource {
    CREATE_PAYMENT("Charge/CreatePayment"),
    SDK_TEST("Charge/SDKTest");

    private String value;

    LyraClientResource(String value) {
           this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
