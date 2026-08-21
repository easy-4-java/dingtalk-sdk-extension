package io.github.easy4j.dingtalk.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NonceGeneratorTest {

    @Test
    void shouldReturn16CharacterString() {
        String result = NonceGenerator.getRandomStr();
        assertNotNull(result);
        assertEquals(16, result.length());
    }

    @Test
    void shouldReturnAlphanumericString() {
        String result = NonceGenerator.getRandomStr();
        assertTrue(result.matches("[a-zA-Z0-9]+"));
    }

    @Test
    void shouldReturnDifferentStringsOnMultipleCalls() {
        String first = NonceGenerator.getRandomStr();
        String second = NonceGenerator.getRandomStr();
        // Extremely unlikely to be equal with 62^16 possibilities
        assertNotEquals(first, second);
    }
}
