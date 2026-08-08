package com.dingtalk.spring.boot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilsTest {

    @Test
    void shouldReturn16CharacterString() {
        String result = RandomUtils.getRandomStr();
        assertNotNull(result);
        assertEquals(16, result.length());
    }

    @Test
    void shouldReturnAlphanumericString() {
        String result = RandomUtils.getRandomStr();
        assertTrue(result.matches("[a-zA-Z0-9]+"));
    }

    @Test
    void shouldReturnDifferentStringsOnMultipleCalls() {
        String first = RandomUtils.getRandomStr();
        String second = RandomUtils.getRandomStr();
        // Extremely unlikely to be equal with 62^16 possibilities
        assertNotEquals(first, second);
    }
}
