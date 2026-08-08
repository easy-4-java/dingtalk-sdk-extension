package com.dingtalk.spring.boot.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HideAvatarTypeTest {

    @Test
    void shouldHaveHideValue() {
        assertEquals("1", HideAvatarType.HIDE.getValue());
        assertEquals("隐藏", HideAvatarType.HIDE.getComment());
    }

    @Test
    void shouldHaveUnhideValue() {
        assertEquals("0", HideAvatarType.UNHIDE.getValue());
        assertEquals("不隐藏，正常显示", HideAvatarType.UNHIDE.getComment());
    }

    @Test
    void shouldHaveTwoValues() {
        assertEquals(2, HideAvatarType.values().length);
    }

    @Test
    void shouldResolveByName() {
        assertEquals(HideAvatarType.HIDE, HideAvatarType.valueOf("HIDE"));
        assertEquals(HideAvatarType.UNHIDE, HideAvatarType.valueOf("UNHIDE"));
    }
}
