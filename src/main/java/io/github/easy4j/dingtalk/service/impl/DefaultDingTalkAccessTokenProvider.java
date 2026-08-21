package io.github.easy4j.dingtalk.service.impl;

import io.github.easy4j.dingtalk.config.DingTalkConfigProvider;
import io.github.easy4j.dingtalk.internal.DingTalkTokenClient;
import io.github.easy4j.dingtalk.service.DingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.storage.DingTalkConfigStorage;
import io.github.easy4j.dingtalk.storage.InMemoryDingTalkConfigStorage;
import com.taobao.api.ApiException;

import java.util.Objects;

/**
 * Default implementation of {@link DingTalkAccessTokenProvider} that calls
 * the DingTalk Open API directly via {@link DingTalkTokenClient} and caches
 * tokens in a pluggable {@link DingTalkConfigStorage}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkAccessTokenProvider
 * @see DingTalkConfigProvider
 * @see DingTalkConfigStorage
 */
public class DefaultDingTalkAccessTokenProvider implements DingTalkAccessTokenProvider {

    private final DingTalkTokenClient tokenClient;
    private final DingTalkConfigStorage storage;

    public DefaultDingTalkAccessTokenProvider(DingTalkConfigProvider dingTalkConfigProvider) {
        this(dingTalkConfigProvider, new InMemoryDingTalkConfigStorage());
    }

    public DefaultDingTalkAccessTokenProvider(DingTalkConfigProvider dingTalkConfigProvider,
                                              DingTalkConfigStorage storage) {
        Objects.requireNonNull(dingTalkConfigProvider, "dingTalkConfigProvider");
        this.storage = Objects.requireNonNull(storage, "storage");
        this.tokenClient = new DingTalkTokenClient(dingTalkConfigProvider, this.storage);
    }

    /** {@inheritDoc} */
    @Override
    public String getAccessToken(String corpId, String appKey) throws ApiException {
        return tokenClient.getAccessToken(corpId, appKey);
    }

    /** {@inheritDoc} */
    @Override
    public String getSnsAccessToken(String corpId, String appId) throws ApiException {
        return tokenClient.getSnsAccessToken(corpId, appId);
    }

    /**
     * @return the underlying storage used for token caching; never {@code null}
     */
    public DingTalkConfigStorage getStorage() {
        return storage;
    }
}
