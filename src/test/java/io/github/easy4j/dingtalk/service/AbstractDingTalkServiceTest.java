package io.github.easy4j.dingtalk.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractDingTalkServiceTest {

    @Test
    void shouldHaveCorrectPrefix() {
        assertEquals("https://oapi.dingtalk.com", AbstractDingTalkService.PREFIX);
    }

    @Test
    void shouldHaveCorrectMethodGet() {
        assertEquals("GET", AbstractDingTalkService.METHOD_GET);
    }

    @Test
    void shouldHaveCorrectJsonContentType() {
        assertEquals("application/json", AbstractDingTalkService.APPLICATION_JSON_VALUE);
    }

    @Test
    void shouldHaveCorrectJsonUtf8ContentType() {
        assertEquals("application/json;charset=UTF-8", AbstractDingTalkService.APPLICATION_JSON_UTF8_VALUE);
    }

    @Test
    void shouldHaveCorrectDelimiter() {
        assertEquals("&", AbstractDingTalkService.DELIMITER);
    }

    @Test
    void shouldHaveCorrectSeparator() {
        assertEquals("=", AbstractDingTalkService.SEPARATOR);
    }
}
