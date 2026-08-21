package io.github.easy4j.dingtalk.internal;

import com.taobao.api.ApiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsapiSignatureGeneratorTest {

    @Test
    void shouldComputeSignature() throws ApiException {
        String signature = JsapiSignatureGenerator.sign("test_ticket", "test_nonce", 1234567890L, "http://example.com");
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
        // SHA-1 produces a 40-character hex string
        assertEquals(40, signature.length());
        assertTrue(signature.matches("[0-9a-f]+"));
    }

    @Test
    void shouldProduceDifferentSignaturesForDifferentInputs() throws ApiException {
        String sig1 = JsapiSignatureGenerator.sign("ticket1", "nonce1", 100L, "http://a.com");
        String sig2 = JsapiSignatureGenerator.sign("ticket2", "nonce2", 200L, "http://b.com");
        assertNotEquals(sig1, sig2);
    }

    @Test
    void shouldProduceSameSignatureForSameInputs() throws ApiException {
        String sig1 = JsapiSignatureGenerator.sign("ticket", "nonce", 100L, "http://a.com");
        String sig2 = JsapiSignatureGenerator.sign("ticket", "nonce", 100L, "http://a.com");
        assertEquals(sig1, sig2);
    }
}
