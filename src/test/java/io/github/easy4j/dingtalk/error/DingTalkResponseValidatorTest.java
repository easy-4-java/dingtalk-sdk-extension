package io.github.easy4j.dingtalk.error;

import com.dingtalk.api.response.OapiRobotSendResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkResponseValidatorTest {

    @Test
    void returnsSameResponseForZeroErrcode() throws Exception {
        OapiRobotSendResponse response = new OapiRobotSendResponse();
        response.setErrcode(0L);
        response.setErrmsg("ok");
        OapiRobotSendResponse result = DingTalkResponseValidator.requireSuccess(
                response, response.getErrcode(), response.getErrmsg());
        assertSame(response, result);
    }

    @Test
    void throwsStructuredExceptionForNonZeroErrcode() {
        OapiRobotSendResponse response = new OapiRobotSendResponse();
        response.setErrcode(40014L);
        response.setErrmsg("invalid access token");
        response.setBody("{\"errcode\":40014}");
        DingTalkApiException exception = assertThrows(
                DingTalkApiException.class,
                () -> DingTalkResponseValidator.requireSuccess(
                        response, response.getErrcode(), response.getErrmsg()));
        assertEquals(40014, exception.getErrorCode());
        assertEquals("{\"errcode\":40014}", exception.getError().getBody());
    }

    @Test
    void throwsForNullResponse() {
        DingTalkApiException exception = assertThrows(
                DingTalkApiException.class,
                () -> DingTalkResponseValidator.requireSuccess(null, 0L, "ok"));
        assertTrue(exception.getMessage().contains("null response"));
    }

    @Test
    void fallsBackToTransportFieldsWhenErrcodeNull() {
        OapiRobotSendResponse response = new OapiRobotSendResponse();
        response.setErrorCode("40014");
        response.setMsg("transport-fallback");
        DingTalkApiException exception = assertThrows(
                DingTalkApiException.class,
                () -> DingTalkResponseValidator.requireSuccess(response, null, null));
        assertEquals(40014, exception.getErrorCode());
        assertEquals("transport-fallback", exception.getError().getErrorMsg());
    }

    @Test
    void parseCodeHandlesBlankAndNonNumeric() {
        assertEquals(-1L, DingTalkResponseValidator.parseCode(null));
        assertEquals(-1L, DingTalkResponseValidator.parseCode(""));
        assertEquals(-1L, DingTalkResponseValidator.parseCode("   "));
        assertEquals(-1L, DingTalkResponseValidator.parseCode("not-a-number"));
        assertEquals(42L, DingTalkResponseValidator.parseCode("42"));
        assertEquals(7L, DingTalkResponseValidator.parseCode("  7  "));
    }
}
