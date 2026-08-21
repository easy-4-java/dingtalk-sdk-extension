package io.github.easy4j.dingtalk.model.jsapi;

import io.github.easy4j.dingtalk.model.jsapi.TicketType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketTypeTest {

    @Test
    void shouldHaveJsapiType() {
        assertEquals(TicketType.JSAPI, TicketType.valueOf("JSAPI"));
    }

    @Test
    void shouldHaveOneValue() {
        assertEquals(1, TicketType.values().length);
    }

    @Test
    void shouldHaveJsapiCode() {
        assertEquals("jsapi", TicketType.JSAPI.getCode());
    }
}
