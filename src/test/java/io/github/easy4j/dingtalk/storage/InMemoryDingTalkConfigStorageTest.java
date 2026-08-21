package io.github.easy4j.dingtalk.storage;

import io.github.easy4j.dingtalk.model.jsapi.TicketType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Lock;

import static io.github.easy4j.dingtalk.storage.DingTalkConfigStorage.DEFAULT_EXPIRE_BUFFER_SECONDS;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryDingTalkConfigStorageTest {

    @Test
    void returnsSameStripedLockForSameKey() {
        DingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        assertSame(storage.getAccessTokenLock("corp:app"),
                   storage.getAccessTokenLock("corp:app"));
    }

    @Test
    void differentKeysMayReturnDifferentLocks() {
        InMemoryDingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        Lock a = storage.getAccessTokenLock("key-one");
        Lock b = storage.getAccessTokenLock("key-two");
        assertNotNull(a);
        assertNotNull(b);
    }

    @Test
    void expiresTokenBeforeRemoteTtl() {
        InMemoryDingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        storage.setExpireBufferSeconds(300L);
        storage.updateAccessToken("corp:app", "token", 301L);
        assertFalse(storage.isAccessTokenExpired("corp:app"));
    }

    @Test
    void respectsExpireBufferForToken() {
        InMemoryDingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        long ttl = 60L;
        storage.setExpireBufferSeconds(ttl * 2L);
        storage.updateAccessToken("corp:app", "token", ttl);
        assertTrue(storage.isAccessTokenExpired("corp:app"));
    }

    @Test
    void whenBufferSmallerThanTtlTokenStillValid() {
        InMemoryDingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        storage.setExpireBufferSeconds(0L);
        storage.updateAccessToken("corp:app", "token", 7200L);
        assertFalse(storage.isAccessTokenExpired("corp:app"));
        assertEquals("token", storage.getAccessToken("corp:app"));
    }

    @Test
    void jsapiTicketLockIsStable() {
        DingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        assertSame(storage.getJsapiTicketLock("corp:app"),
                   storage.getJsapiTicketLock("corp:app"));
    }

    @Test
    void jsapiTicketExpiryRespectsBuffer() {
        InMemoryDingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        storage.setExpireBufferSeconds(60L);
        storage.updateJsapiTicket("corp:app", TicketType.JSAPI, "ticket", 7200L);
        assertEquals("ticket", storage.getJsapiTicket("corp:app", TicketType.JSAPI));
        assertFalse(storage.isJsapiTicketExpired("corp:app", TicketType.JSAPI));
    }

    @Test
    void expireAccessTokenForcesExpiry() {
        DingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        storage.updateAccessToken("k", "t", 7200L);
        storage.expireAccessToken("k");
        assertTrue(storage.isAccessTokenExpired("k"));
        assertNull(storage.getAccessToken("k"));
    }

    @Test
    void missingTokenReportedAsExpired() {
        DingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        assertTrue(storage.isAccessTokenExpired("absent"));
    }

    @Test
    void missingJsapiTicketReportedAsExpired() {
        DingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
        assertTrue(storage.isJsapiTicketExpired("absent", TicketType.JSAPI));
    }
}
