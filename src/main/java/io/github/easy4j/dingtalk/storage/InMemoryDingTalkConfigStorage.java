/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.dingtalk.storage;

import io.github.easy4j.dingtalk.model.jsapi.TicketType;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-heap, thread-safe, double-checked-locking implementation of
 * {@link DingTalkConfigStorage}.
 *
 * <p>Token entries are held in two {@link ConcurrentHashMap}s.  A stripe of
 * {@link ReentrantLock}s (default size = 32) is used to serialise refills per
 * cache key, while reads remain entirely lock-free thanks to
 * {@code volatile} expire timestamps.  This mirrors the pattern used by
 * the WxJava family of SDKs and keeps the zero-refresh contention path cheap.</p>
 *
 * <h3>Double-checked locking (DCL) contract</h3>
 * Although the {@code updateXxx} methods are unconditional writes, the
 * <em>caller</em> is expected to perform DCL:
 * <pre>{@code
 *   if (storage.isAccessTokenExpired(key)) {
 *       synchronized (stripeLock(key)) {
 *           if (storage.isAccessTokenExpired(key)) {   // re-check!
 *               Token t = dingTalkApi.refresh();
 *               storage.updateAccessToken(key, t.token, t.expiresIn);
 *           }
 *       }
 *   }
 *   return storage.getAccessToken(key);
 * }</pre>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkConfigStorage
 */
@Slf4j
public class InMemoryDingTalkConfigStorage implements DingTalkConfigStorage {

    private static final int STRIPE_COUNT = 32;

    private final Map<String, TokenEntry> accessTokenMap = new ConcurrentHashMap<>();
    private final Map<String, Map<TicketType, TokenEntry>> jsapiTicketMap = new ConcurrentHashMap<>();
    private final Lock[] accessTokenLocks = new Lock[STRIPE_COUNT];
    private final Lock[] jsapiTicketLocks = new Lock[STRIPE_COUNT];
    private long expireBufferSeconds = DEFAULT_EXPIRE_BUFFER_SECONDS;

    public InMemoryDingTalkConfigStorage() {
        for (int i = 0; i < STRIPE_COUNT; i++) {
            accessTokenLocks[i] = new ReentrantLock();
            jsapiTicketLocks[i] = new ReentrantLock();
        }
    }

    /**
     * Allows callers to tune the safety margin subtracted from DingTalk's
     * reported TTL. The default is {@link #DEFAULT_EXPIRE_BUFFER_SECONDS}.
     *
     * @param bufferSeconds buffer &gt;= 0
     */
    public void setExpireBufferSeconds(long bufferSeconds) {
        if (bufferSeconds < 0) {
            throw new IllegalArgumentException("expireBufferSeconds must be >= 0");
        }
        this.expireBufferSeconds = bufferSeconds;
    }

    @Override
    public String getAccessToken(String key) {
        TokenEntry e = accessTokenMap.get(key);
        return e == null ? null : e.token;
    }

    @Override
    public boolean isAccessTokenExpired(String key) {
        TokenEntry e = accessTokenMap.get(key);
        return e == null || System.currentTimeMillis() >= e.expiresTime;
    }

    @Override
    public void updateAccessToken(String key, String accessToken, long expiresInSeconds) {
        long effectiveTtl = expiresInSeconds - expireBufferSeconds;
        long expiresTime;
        if (effectiveTtl <= 0L) {
            expiresTime = System.currentTimeMillis() - 1000L;
        } else {
            expiresTime = System.currentTimeMillis() + effectiveTtl * 1000L;
        }
        TokenEntry entry = new TokenEntry(accessToken, expiresTime);
        accessTokenMap.put(key, entry);
        log.debug("DingTalk access_token updated key={} expiresIn={}s buffer={}s effectiveExpiry={}ms",
                key, expiresInSeconds, expireBufferSeconds, expiresTime);
    }

    @Override
    public String getJsapiTicket(String key, TicketType type) {
        Map<TicketType, TokenEntry> inner = jsapiTicketMap.get(key);
        if (inner == null) {
            return null;
        }
        TokenEntry e = inner.get(type);
        return e == null ? null : e.token;
    }

    @Override
    public boolean isJsapiTicketExpired(String key, TicketType type) {
        Map<TicketType, TokenEntry> inner = jsapiTicketMap.get(key);
        if (inner == null) {
            return true;
        }
        TokenEntry e = inner.get(type);
        return e == null || System.currentTimeMillis() >= e.expiresTime;
    }

    @Override
    public void updateJsapiTicket(String key, TicketType type, String jsapiTicket, long expiresInSeconds) {
        long effectiveTtl = expiresInSeconds - expireBufferSeconds;
        long expiresTime;
        if (effectiveTtl <= 0L) {
            expiresTime = System.currentTimeMillis() - 1000L;
        } else {
            expiresTime = System.currentTimeMillis() + effectiveTtl * 1000L;
        }
        jsapiTicketMap.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                      .put(type, new TokenEntry(jsapiTicket, expiresTime));
        log.debug("DingTalk jsapi_ticket updated key={} type={} expiresIn={}s buffer={}s effectiveExpiry={}ms",
                key, type, expiresInSeconds, expireBufferSeconds, expiresTime);
    }

    @Override
    public void expireAccessToken(String key) {
        TokenEntry e = accessTokenMap.remove(key);
        if (e != null && log.isDebugEnabled()) {
            log.debug("DingTalk access_token forcibly expired key={}", key);
        }
    }

    @Override
    public void expireJsapiTicket(String key, TicketType type) {
        Map<TicketType, TokenEntry> inner = jsapiTicketMap.get(key);
        if (inner != null) {
            TokenEntry e = inner.remove(type);
            if (e != null && log.isDebugEnabled()) {
                log.debug("DingTalk jsapi_ticket forcibly expired key={} type={}", key, type);
            }
        }
    }

    /**
     * Returns the per-key stripe lock used by callers to serialise access_token
     * refills. This is the "L" of the DCL contract described in the class javadoc.
     *
     * @param key cache key
     * @return stripe-scoped {@link ReentrantLock}
     */
    public Lock getAccessTokenLock(String key) {
        return accessTokenLocks[stripeIndexForKey(key)];
    }

    /**
     * Returns the per-key stripe lock used by callers to serialise jsapi ticket
     * refills.
     *
     * @param key cache key
     * @return stripe-scoped {@link Lock}
     */
    public Lock getJsapiTicketLock(String key) {
        return jsapiTicketLocks[stripeIndexForKey(key)];
    }

    private static int stripeIndexForKey(String key) {
        return (key.hashCode() & 0x7fffffff) % STRIPE_COUNT;
    }

    /**
     * Immutable token bucket; {@code expiresTime} is {@code volatile} so the
     * write performed in {@code updateAccessToken} happens-before a subsequent
     * lock-free read in {@code isAccessTokenExpired}.
     */
    private static final class TokenEntry {
        final String token;
        volatile long expiresTime;

        TokenEntry(String token, long expiresTime) {
            this.token = token;
            this.expiresTime = expiresTime;
        }
    }
}
