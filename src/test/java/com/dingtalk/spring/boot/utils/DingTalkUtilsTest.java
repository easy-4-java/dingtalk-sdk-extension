package com.dingtalk.spring.boot.utils;

import com.taobao.api.ApiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkUtilsTest {

    @Test
    void shouldComputeSignature() throws ApiException {
        String signature = DingTalkUtils.sign("test_ticket", "test_nonce", 1234567890L, "http://example.com");
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
        // SHA-1 produces a 40-character hex string
        assertEquals(40, signature.length());
        assertTrue(signature.matches("[0-9a-f]+"));
    }

    @Test
    void shouldProduceDifferentSignaturesForDifferentInputs() throws ApiException {
        String sig1 = DingTalkUtils.sign("ticket1", "nonce1", 100L, "http://a.com");
        String sig2 = DingTalkUtils.sign("ticket2", "nonce2", 200L, "http://b.com");
        assertNotEquals(sig1, sig2);
    }

    @Test
    void shouldProduceSameSignatureForSameInputs() throws ApiException {
        String sig1 = DingTalkUtils.sign("ticket", "nonce", 100L, "http://a.com");
        String sig2 = DingTalkUtils.sign("ticket", "nonce", 100L, "http://a.com");
        assertEquals(sig1, sig2);
    }
}
