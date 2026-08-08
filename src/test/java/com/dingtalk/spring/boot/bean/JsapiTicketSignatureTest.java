package com.dingtalk.spring.boot.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsapiTicketSignatureTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        JsapiTicketSignature sig = new JsapiTicketSignature();
        assertNull(sig.getAgentId());
        assertNull(sig.getUrl());
        assertNull(sig.getNonceStr());
        assertEquals(0L, sig.getTimestamp());
        assertNull(sig.getCorpId());
        assertNull(sig.getSignature());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        JsapiTicketSignature sig = new JsapiTicketSignature("agent1", "http://url.com", "nonce", 12345L, "corp1", "sig1");
        assertEquals("agent1", sig.getAgentId());
        assertEquals("http://url.com", sig.getUrl());
        assertEquals("nonce", sig.getNonceStr());
        assertEquals(12345L, sig.getTimestamp());
        assertEquals("corp1", sig.getCorpId());
        assertEquals("sig1", sig.getSignature());
    }

    @Test
    void shouldBuildWithBuilder() {
        JsapiTicketSignature sig = JsapiTicketSignature.builder()
                .agentId("a1")
                .url("http://u.com")
                .nonceStr("n1")
                .timestamp(999L)
                .corpId("c1")
                .signature("s1")
                .build();
        assertEquals("a1", sig.getAgentId());
        assertEquals("http://u.com", sig.getUrl());
        assertEquals("n1", sig.getNonceStr());
        assertEquals(999L, sig.getTimestamp());
        assertEquals("c1", sig.getCorpId());
        assertEquals("s1", sig.getSignature());
    }

    @Test
    void shouldSetAndGetAgentId() {
        JsapiTicketSignature sig = new JsapiTicketSignature();
        sig.setAgentId("agent");
        assertEquals("agent", sig.getAgentId());
    }

    @Test
    void shouldSetAndGetUrl() {
        JsapiTicketSignature sig = new JsapiTicketSignature();
        sig.setUrl("http://url.com");
        assertEquals("http://url.com", sig.getUrl());
    }

    @Test
    void shouldSetAndGetNonceStr() {
        JsapiTicketSignature sig = new JsapiTicketSignature();
        sig.setNonceStr("nonce");
        assertEquals("nonce", sig.getNonceStr());
    }

    @Test
    void shouldSetAndGetTimestamp() {
        JsapiTicketSignature sig = new JsapiTicketSignature();
        sig.setTimestamp(12345L);
        assertEquals(12345L, sig.getTimestamp());
    }

    @Test
    void shouldSetAndGetCorpId() {
        JsapiTicketSignature sig = new JsapiTicketSignature();
        sig.setCorpId("corp");
        assertEquals("corp", sig.getCorpId());
    }

    @Test
    void shouldSetAndGetSignature() {
        JsapiTicketSignature sig = new JsapiTicketSignature();
        sig.setSignature("sig");
        assertEquals("sig", sig.getSignature());
    }

    @Test
    void shouldImplementEquals() {
        JsapiTicketSignature sig1 = JsapiTicketSignature.builder().agentId("a").build();
        JsapiTicketSignature sig2 = JsapiTicketSignature.builder().agentId("a").build();
        assertEquals(sig1, sig2);
    }

    @Test
    void shouldImplementHashCode() {
        JsapiTicketSignature sig1 = JsapiTicketSignature.builder().agentId("a").build();
        JsapiTicketSignature sig2 = JsapiTicketSignature.builder().agentId("a").build();
        assertEquals(sig1.hashCode(), sig2.hashCode());
    }

    @Test
    void shouldImplementToString() {
        JsapiTicketSignature sig = JsapiTicketSignature.builder().agentId("a").build();
        String str = sig.toString();
        assertNotNull(str);
        assertTrue(str.contains("agentId"));
    }
}
