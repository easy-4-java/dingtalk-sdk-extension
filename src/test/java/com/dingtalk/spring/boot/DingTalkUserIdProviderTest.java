package com.dingtalk.spring.boot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkUserIdProviderTest {

    private final DingTalkUserIdProvider provider = new DingTalkUserIdProvider() {};

    @Test
    void shouldReturnAccountAsUserId() {
        assertEquals("user1", provider.getUserIdByDingTalkUser("corp1", "app1", "user1"));
    }

    @Test
    void shouldJoinUserIdsWithComma() {
        assertEquals("a,b,c", provider.getDingTalkUserByUserId("corp1", "app1", "a", "b", "c"));
    }

    @Test
    void shouldHandleSingleUserId() {
        assertEquals("single", provider.getDingTalkUserByUserId("corp1", "app1", "single"));
    }

    @Test
    void shouldHandleEmptyUserIds() {
        assertEquals("", provider.getDingTalkUserByUserId("corp1", "app1"));
    }
}
