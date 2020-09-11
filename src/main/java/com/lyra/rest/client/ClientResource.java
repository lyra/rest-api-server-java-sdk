package com.lyra.rest.client;

/**
 * <p>
 * Enum that contains the different resources from Rest API that can be called</p>
 *
 * Note that the {@link #toString()} method is overridden and returns the exact value of REST resource
 *
 * @author Lyra Network
 */
public enum ClientResource {
    CREATE_PAYMENT("Charge/CreatePayment"),
    CREATE_TOKEN("Charge/CreateToken"),
    SDK_TEST("Charge/SDKTest");

    private String value;

    ClientResource(String value) {
           this.value = value;
    }

    /**
     * Returns the value of the ClientResource
     */
    @Override
    public String toString() {
        return value;
    }
}

