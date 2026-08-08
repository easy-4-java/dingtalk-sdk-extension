package com.dingtalk.spring.boot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkOperationsTest {

    @Test
    void shouldHaveCorrectPrefix() {
        assertEquals("https://oapi.dingtalk.com", DingTalkOperations.PREFIX);
    }

    @Test
    void shouldHaveCorrectMethodGet() {
        assertEquals("GET", DingTalkOperations.METHOD_GET);
    }

    @Test
    void shouldHaveCorrectJsonContentType() {
        assertEquals("application/json", DingTalkOperations.APPLICATION_JSON_VALUE);
    }

    @Test
    void shouldHaveCorrectJsonUtf8ContentType() {
        assertEquals("application/json;charset=UTF-8", DingTalkOperations.APPLICATION_JSON_UTF8_VALUE);
    }

    @Test
    void shouldHaveCorrectDelimiter() {
        assertEquals("&", DingTalkOperations.DELIMITER);
    }

    @Test
    void shouldHaveCorrectSeparator() {
        assertEquals("=", DingTalkOperations.SEPARATOR);
    }
}
