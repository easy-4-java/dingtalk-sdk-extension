package com.dingtalk.spring.boot.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ButtonOrientationTypeTest {

    @Test
    void shouldHaveHorizontalValue() {
        assertEquals("1", ButtonOrientationType.HORIZONTAL.getValue());
        assertEquals("水平布局", ButtonOrientationType.HORIZONTAL.getComment());
    }

    @Test
    void shouldHaveVerticalValue() {
        assertEquals("0", ButtonOrientationType.VERTICAL.getValue());
        assertEquals("垂直布局", ButtonOrientationType.VERTICAL.getComment());
    }

    @Test
    void shouldHaveTwoValues() {
        assertEquals(2, ButtonOrientationType.values().length);
    }

    @Test
    void shouldResolveByName() {
        assertEquals(ButtonOrientationType.HORIZONTAL, ButtonOrientationType.valueOf("HORIZONTAL"));
        assertEquals(ButtonOrientationType.VERTICAL, ButtonOrientationType.valueOf("VERTICAL"));
    }
}
