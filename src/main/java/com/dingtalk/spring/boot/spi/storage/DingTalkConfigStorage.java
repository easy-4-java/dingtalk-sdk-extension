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
package com.dingtalk.spring.boot.spi.storage;

import com.dingtalk.spring.boot.TicketType;

/**
 * Token &amp; ticket storage SPI for DingTalk SDK.
 * <p>Implementations are expected to be thread-safe and should expire entries
 * slightly before the DingTalk reported TTL to eliminate window-edge race
 * conditions. The storage abstraction intentionally models tokens and
 * jsapi-tickets with opaque strings (appKey/corpId/appId combinations are
 * encoded into the caller-supplied cache {@code key}).</p>
 *
 * <h3>Typical call flow</h3>
 * <ol>
 *     <li>Caller checks {@code isAccessTokenExpired(key)}.</li>
 *     <li>When still fresh, {@code getAccessToken(key)} returns the cached value.</li>
 *     <li>When expired, the caller performs a DingTalk API round-trip and feeds
 *         the fresh token back via {@code updateAccessToken(key, token, expiresIn)}.</li>
 * </ol>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkMemoryConfigStorage
 */
public interface DingTalkConfigStorage {

    /**
     * Default safety margin (seconds) subtracted from DingTalk's reported TTL.
     * Prevents window-edge races where a token that expires "now" is still handed out.
     */
    long DEFAULT_EXPIRE_BUFFER_SECONDS = 300L;

    /**
     * Reads a cached access token.
     *
     * @param key cache key (e.g. {@code corpId + ':' + appKey})
     * @return the cached token, or {@code null} if absent
     */
    String getAccessToken(String key);

    /**
     * @param key cache key
     * @return {@code true} if the entry is missing or expired
     */
    boolean isAccessTokenExpired(String key);

    /**
     * Writes (or replaces) a cached access token entry.
     *
     * @param key            cache key
     * @param accessToken    the freshly obtained token
     * @param expiresInSeconds DingTalk reported TTL in seconds (usually 7200)
     */
    void updateAccessToken(String key, String accessToken, long expiresInSeconds);

    /**
     * Reads a cached jsapi ticket of the requested type.
     *
     * @param key  cache key
     * @param type jsapi ticket flavour
     * @return the ticket, or {@code null} if absent
     */
    String getJsapiTicket(String key, TicketType type);

    /**
     * @param key  cache key
     * @param type jsapi ticket flavour
     * @return {@code true} if the entry is missing or expired
     */
    boolean isJsapiTicketExpired(String key, TicketType type);

    /**
     * Writes (or replaces) a cached jsapi ticket entry.
     *
     * @param key              cache key
     * @param type             jsapi ticket flavour
     * @param jsapiTicket      the freshly obtained ticket
     * @param expiresInSeconds DingTalk reported TTL in seconds
     */
    void updateJsapiTicket(String key, TicketType type, String jsapiTicket, long expiresInSeconds);

    /**
     * Expires the access token for a key immediately (for example after receiving
     * {@code 40014} from DingTalk indicating the token is stale).
     *
     * @param key cache key
     */
    void expireAccessToken(String key);

    /**
     * Expires the jsapi ticket for a key/type pair immediately.
     *
     * @param key  cache key
     * @param type ticket flavour
     */
    void expireJsapiTicket(String key, TicketType type);
}
