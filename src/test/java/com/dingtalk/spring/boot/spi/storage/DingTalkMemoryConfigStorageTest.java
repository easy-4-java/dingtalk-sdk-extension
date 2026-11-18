package com.dingtalk.spring.boot.spi.storage;

import com.dingtalk.spring.boot.TicketType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkMemoryConfigStorageTest {

    @Test
    void accessTokensAreIsolatedByKeyAndCanBeExplicitlyExpired() {
        DingTalkMemoryConfigStorage storage = new DingTalkMemoryConfigStorage();
        storage.setExpireBufferSeconds(0);

        assertNull(storage.getAccessToken("first"));
        assertTrue(storage.isAccessTokenExpired("first"));

        storage.updateAccessToken("first", "token-1", 60);
        storage.updateAccessToken("second", "token-2", 60);
        assertEquals("token-1", storage.getAccessToken("first"));
        assertFalse(storage.isAccessTokenExpired("first"));

        storage.expireAccessToken("first");
        assertNull(storage.getAccessToken("first"));
        assertTrue(storage.isAccessTokenExpired("first"));
        assertEquals("token-2", storage.getAccessToken("second"));
        assertFalse(storage.isAccessTokenExpired("second"));
    }

    @Test
    void ticketsAreIsolatedByKey() {
        DingTalkMemoryConfigStorage storage = new DingTalkMemoryConfigStorage();
        storage.setExpireBufferSeconds(0);

        assertNull(storage.getJsapiTicket("corp", TicketType.JSAPI));
        assertTrue(storage.isJsapiTicketExpired("corp", TicketType.JSAPI));
        storage.updateJsapiTicket("corp", TicketType.JSAPI, "ticket-1", 60);
        storage.updateJsapiTicket("other", TicketType.JSAPI, "ticket-2", 60);

        assertEquals("ticket-1", storage.getJsapiTicket("corp", TicketType.JSAPI));
        assertFalse(storage.isJsapiTicketExpired("corp", TicketType.JSAPI));
        storage.expireJsapiTicket("corp", TicketType.JSAPI);
        assertNull(storage.getJsapiTicket("corp", TicketType.JSAPI));
        assertTrue(storage.isJsapiTicketExpired("corp", TicketType.JSAPI));
        assertEquals("ticket-2", storage.getJsapiTicket("other", TicketType.JSAPI));
    }

    @Test
    void rejectsNegativeExpiryBufferAndKeepsPreviousValue() {
        DingTalkMemoryConfigStorage storage = new DingTalkMemoryConfigStorage();
        storage.setExpireBufferSeconds(0);
        assertThrows(IllegalArgumentException.class, () -> storage.setExpireBufferSeconds(-1));
        storage.updateAccessToken("corp", "token", 2);
        assertFalse(storage.isAccessTokenExpired("corp"));
    }

    @Test
    void exposesStableLockForSameKey() {
        DingTalkMemoryConfigStorage storage = new DingTalkMemoryConfigStorage();
        assertSame(storage.getAccessTokenLock("corp"), storage.getAccessTokenLock("corp"));
        assertSame(storage.getJsapiTicketLock("corp"), storage.getJsapiTicketLock("corp"));
    }

    @Test
    void concurrentTicketWritesKeepBothKeys() throws Exception {
        DingTalkMemoryConfigStorage storage = new DingTalkMemoryConfigStorage();
        storage.setExpireBufferSeconds(0);
        CountDownLatch start = new CountDownLatch(1);
        Thread first = new Thread(() -> {
            await(start);
            storage.updateJsapiTicket("corp", TicketType.JSAPI, "ticket-1", 60);
        });
        Thread second = new Thread(() -> {
            await(start);
            storage.updateJsapiTicket("other", TicketType.JSAPI, "ticket-2", 60);
        });
        first.start();
        second.start();
        start.countDown();
        first.join(3000);
        second.join(3000);
        assertFalse(first.isAlive());
        assertFalse(second.isAlive());
        assertEquals("ticket-1", storage.getJsapiTicket("corp", TicketType.JSAPI));
        assertEquals("ticket-2", storage.getJsapiTicket("other", TicketType.JSAPI));
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(3, TimeUnit.SECONDS)) {
                throw new AssertionError("Timed out waiting for concurrent writers");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }
    }
}
