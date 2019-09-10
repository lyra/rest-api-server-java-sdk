package com.lyra.rest.client;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * This utility class allows to generate a hash the message returned by platform in order to validate its integrity.
 *
 * @author Lyra Network
 */
class ClientCryptUtil {

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    //Use always UTF-8
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    //Supported algorithms
    static final String ALGORITHM_HMAC_SHA256 = "sha256_hmac";
    static final String[] SUPPORTED_ALGORITHMS = {ALGORITHM_HMAC_SHA256};

    //Private constructor as all methods are static
    private ClientCryptUtil() {}


    /**
     * Returns true if the provided algorithm is supported by this version of SDK
     *
     * @param algorithm String that contains the algorithm
     * @return boolean true if the algorithm is supported
     */
    static boolean isAlgorithmSupported(String algorithm) {
        return Arrays.asList(SUPPORTED_ALGORITHMS).contains(algorithm);
    }

    /**
     * Calculate the hash of the specified string using the provided key and the HMAC SHA256 algorithm.
     *
     * @param src Source string to calculateHash.
     * @param key Key used to calculateHash.
     * @return Hash encoded in hexadecimal string.
     */
    static String calculateHash(String src, String key, String algorithm) {
        //Verify null entries
        if (src == null) {
            throw new ClientException("Provided message is null. It is impossible to generate the hash!");
        } else if (key == null) {
            throw new ClientException("Provided key is null. It is impossible to generate the hash without a key!");
        }

        //Proceed with hash generation
        try {
            src = src.replace("\\/", "/"); //Replace unwanted characters

            byte[] messageBytes = src.getBytes(DEFAULT_CHARSET_NAME);
            byte[] keyBytes = key.getBytes(DEFAULT_CHARSET_NAME);

            switch (algorithm) {
                case ALGORITHM_HMAC_SHA256:
                    //Add new cases here
                default:
                    return new String(encodeHex(getHashHmacSha256(keyBytes, messageBytes), DIGITS_LOWER));
            }

        } catch (Exception e) {
            throw new ClientException("Unexpected error generating message hash", e);
        }
    }

    /**
     * This method uses the JCE to provide the HMAC-SHA-256 algorithm. HMAC computes a Hashed Message Authentication Code and in
     * this case SHA256 is the hash algorithm used.
     *
     * @param keyBytes the bytes to use for the HMAC-SHA-256 key
     * @param text     the message or text to be authenticated.
     * @throws NoSuchAlgorithmException if no provider makes either HmacSHA1 or HMAC-SHA-256 digest algorithms available.
     * @throws InvalidKeyException      The secret provided was not a valid HMAC-SHA-256 key.
     */
    private static byte[] getHashHmacSha256(byte[] keyBytes, byte[] text) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException nsae) {
            hmacSha256 = Mac.getInstance("HMAC-SHA-256");
        }
        SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
        hmacSha256.init(macKey);

        return hmacSha256.doFinal(text);
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data     a byte[] to convert to Hex characters
     * @param toDigits the output alphabet
     * @return A char[] containing hexadecimal characters
     */
    private static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }
}

