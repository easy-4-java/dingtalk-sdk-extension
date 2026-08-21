package io.github.easy4j.dingtalk.internal;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiSnsGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiSnsGettokenResponse;
import io.github.easy4j.dingtalk.config.DingTalkConfigProvider;
import io.github.easy4j.dingtalk.error.DingTalkApiException;
import io.github.easy4j.dingtalk.error.DingTalkResponseValidator;
import io.github.easy4j.dingtalk.error.DingTalkRuntimeException;
import io.github.easy4j.dingtalk.storage.DingTalkConfigStorage;
import com.taobao.api.ApiException;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * Internal DingTalk token refresh helper.
 * <p>Implements double-checked locking over a {@link DingTalkConfigStorage} so
 * concurrent callers for the same cache key never trigger a DingTalk API
 * round-trip more than once per expiry window.</p>
 *
 * <p>Token cache keys are composed as {@code corpId + ':' + appKey} (for
 * enterprise internal app tokens) or {@code corpId + ':' + appId + ":sns"}
 * (for SNS open-app tokens).</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public final class DingTalkTokenClient {

    private static final String DINGTALK_SERVICE = "https://oapi.dingtalk.com";
    private static final String METHOD_GET = "GET";

    private final DingTalkConfigProvider configProvider;
    private final DingTalkConfigStorage storage;

    public DingTalkTokenClient(DingTalkConfigProvider configProvider,
                               DingTalkConfigStorage storage) {
        this.configProvider = Objects.requireNonNull(configProvider, "configProvider");
        this.storage = Objects.requireNonNull(storage, "storage");
    }

    /**
     * Returns a (possibly cached) enterprise internal-app access token.
     * <p>If the token is not cached or has expired, performs one refresh under
     * a striped lock, then re-checks expiry to implement double-checked locking.</p>
     *
     * @param corpId corporate identifier
     * @param appKey application key
     * @return non-null access token
     * @throws ApiException   when the DingTalk API round-trip fails
     * @throws RuntimeException if the API responds with a non-zero errcode
     */
    public String getAccessToken(String corpId, String appKey) throws ApiException {
        String key = cacheKey(corpId, appKey);
        if (!storage.isAccessTokenExpired(key)) {
            return storage.getAccessToken(key);
        }
        Lock lock = storage.getAccessTokenLock(key);
        lock.lock();
        try {
            if (!storage.isAccessTokenExpired(key)) {
                return storage.getAccessToken(key);
            }
            String appSecret = configProvider.getAppSecret(corpId, appKey);

            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(appKey);
            request.setAppsecret(appSecret);
            request.setHttpMethod(METHOD_GET);

            DingTalkClient client = new DefaultDingTalkClient(DINGTALK_SERVICE + "/gettoken");
            OapiGettokenResponse response = client.execute(request);
            try {
                DingTalkResponseValidator.requireSuccess(response, response.getErrcode(), response.getErrmsg());
            } catch (DingTalkApiException ex) {
                throw new DingTalkRuntimeException(ex.getMessage(), ex);
            }

            String token = response.getAccessToken();
            Number rawExpire = response.getExpiresIn();
            long expiresIn = rawExpire == null ? 7200L : rawExpire.longValue();
            storage.updateAccessToken(key, token, expiresIn);
            return token;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a (possibly cached) SNS open-application access token.
     *
     * @param corpId corporate identifier
     * @param appId  open application ID
     * @return non-null SNS access token
     * @throws ApiException   when the DingTalk API round-trip fails
     * @throws RuntimeException if the API responds with a non-zero errcode
     */
    public String getSnsAccessToken(String corpId, String appId) throws ApiException {
        String key = cacheKey(corpId, appId) + ":sns";
        if (!storage.isAccessTokenExpired(key)) {
            return storage.getAccessToken(key);
        }
        Lock lock = storage.getAccessTokenLock(key);
        lock.lock();
        try {
            if (!storage.isAccessTokenExpired(key)) {
                return storage.getAccessToken(key);
            }
            String appSecret = configProvider.getAppSecret(corpId, appId);

            OapiSnsGettokenRequest request = new OapiSnsGettokenRequest();
            request.setAppid(appId);
            request.setAppsecret(appSecret);
            request.setHttpMethod(METHOD_GET);

            DingTalkClient client = new DefaultDingTalkClient(DINGTALK_SERVICE + "/sns/gettoken");
            OapiSnsGettokenResponse response = client.execute(request);
            try {
                DingTalkResponseValidator.requireSuccess(response, response.getErrcode(), response.getErrmsg());
            } catch (DingTalkApiException ex) {
                throw new DingTalkRuntimeException(ex.getMessage(), ex);
            }

            String token = response.getAccessToken();
            storage.updateAccessToken(key, token, 7200L);
            return token;
        } finally {
            lock.unlock();
        }
    }

    private static String cacheKey(String corpId, String app) {
        return (corpId == null ? "" : corpId) + ':' + (app == null ? "" : app);
    }
}
