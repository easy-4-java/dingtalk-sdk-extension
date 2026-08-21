package io.github.easy4j.dingtalk.service;

import com.taobao.api.ApiException;
import io.github.easy4j.dingtalk.storage.DingTalkConfigStorage;

/**
 * Strategy interface for obtaining DingTalk access tokens.
 * <p>Implementations may cache tokens, refresh them automatically,
 * or delegate to external token management services.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DefaultDingTalkAccessTokenProvider
 * @see DingTalkTemplate#getAccessToken(String, String)
 * @see <a href="https://open.dingtalk.com/document/isvapp-server/obtain-the-access_token-of-an-enterprise-s-internal-applications">Access token documentation</a>
 */
public interface DingTalkAccessTokenProvider {

    /**
     * Retrieves the enterprise internal application access token.
     *
     * @param corpId  the corporate ID
     * @param appKey  the application key
     * @return the access token
     * @throws ApiException if the API request fails
     */
    String getAccessToken(String corpId, String appKey) throws ApiException;

    /**
     * Retrieves the SNS access token for an open application.
     *
     * @param corpId  the corporate ID
     * @param appId   the application ID
     * @return the SNS access token
     * @throws ApiException if the API request fails
     */
    String getSnsAccessToken(String corpId, String appId) throws ApiException;

    /**
     * @return the storage used for caching tokens and jsapi tickets;
     *         may be {@code null} for implementations that do not manage
     *         local caching. The default implementation returns {@code null}.
     */
    default DingTalkConfigStorage getStorage() {
        return null;
    }
}
