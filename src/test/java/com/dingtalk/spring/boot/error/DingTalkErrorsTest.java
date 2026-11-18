package com.dingtalk.spring.boot.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkErrorsTest {

    @Test
    void mapsKnownAndUnknownApiCodesWithoutLosingMessage() {
        DingTalkError known = DingTalkError.of(40001, "bad token");
        assertEquals(DingTalkErrorCodeEnum.ACCESS_TOKEN_ILLEGAL, known.getErrorCodeEnum());
        assertFalse(known.isSuccess());
        assertEquals(40001, DingTalkErrorCodeEnum.fromCode(40001).getCode());
        assertTrue(DingTalkErrorCodeEnum.ACCESS_TOKEN_ILLEGAL.toString().contains("40001"));

        DingTalkErrorException checked = new DingTalkErrorException(known);
        assertSame(known, checked.getError());
        assertEquals(40001, checked.getErrorCode());
        assertTrue(checked.getMessage().contains("ACCESS_TOKEN_ILLEGAL"));
        assertTrue(checked.getMessage().contains("bad token"));

        DingTalkError unknown = DingTalkError.of(999999, "new server error");
        assertNull(unknown.getErrorCodeEnum());
        assertNull(DingTalkErrorCodeEnum.fromCode(999999));
        assertTrue(new DingTalkErrorException(unknown).getMessage().contains("new server error"));
        assertTrue(new DingTalkRuntimeException(unknown).getMessage().contains("new server error"));
    }

    @Test
    void preservesCausesAndStructuredErrorAcrossExceptionKinds() {
        DingTalkError error = DingTalkError.of(42001, "expired");
        IllegalStateException cause = new IllegalStateException("transport");

        DingTalkErrorException checked = new DingTalkErrorException(error, cause);
        assertSame(cause, checked.getCause());
        assertSame(error, checked.getError());
        assertTrue(checked.getMessage().contains("ACCESS_TOKEN_EXPIRED"));

        DingTalkRuntimeException unchecked = new DingTalkRuntimeException(error, cause);
        assertSame(cause, unchecked.getCause());
        assertSame(error, unchecked.getError());
        assertEquals(42001, unchecked.getErrorCode());
        assertTrue(unchecked.getMessage().contains("42001"));
    }

    @Test
    void handlesMissingCodeAndPlainTextFailures() {
        DingTalkError success = DingTalkError.success();
        assertTrue(success.isSuccess());
        assertEquals(DingTalkErrorCodeEnum.SUCCESS, success.getErrorCodeEnum());

        DingTalkError missing = DingTalkError.builder().errorMsg("missing code").build();
        assertFalse(missing.isSuccess());
        assertNull(missing.getErrorCodeEnum());
        assertEquals(-1, new DingTalkErrorException(missing).getErrorCode());
        assertEquals(-1, new DingTalkRuntimeException(missing).getErrorCode());
        assertEquals(-1, new DingTalkErrorException((DingTalkError) null).getErrorCode());
        assertEquals(-1, new DingTalkRuntimeException((DingTalkError) null).getErrorCode());

        DingTalkErrorException checked = new DingTalkErrorException("plain failure");
        assertEquals("plain failure", checked.getMessage());
        assertEquals("plain failure", checked.getError().getErrorMsg());

        DingTalkRuntimeException unchecked = new DingTalkRuntimeException("plain failure", new IllegalStateException());
        assertEquals("plain failure", unchecked.getMessage());
        assertEquals("plain failure", unchecked.getError().getErrorMsg());
        assertEquals(-1, unchecked.getErrorCode());
    }
}
