package io.github.easy4j.dingtalk.internal;

import com.dingtalk.api.DingTalkClient;

/**
 * Test seam used by {@code DingTalk*Operations} classes so that callers can
 * intercept or fake outgoing DingTalk Open API invocations without needing to
 * spin up a real HTTP transport.
 * <p>The default production instance is {@code DefaultDingTalkClient::new}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see com.dingtalk.api.DefaultDingTalkClient
 */
@FunctionalInterface
public interface DingTalkClientFactory {

    /**
     * Creates a new DingTalk HTTP client bound to {@code endpoint}.
     *
     * @param endpoint absolute endpoint URL (never {@code null} or blank in practice)
     * @return a ready-to-use DingTalk client
     */
    DingTalkClient create(String endpoint);

    /**
     * @return the default production factory backed by {@code DefaultDingTalkClient::new}
     */
    static DingTalkClientFactory defaultFactory() {
        return com.dingtalk.api.DefaultDingTalkClient::new;
    }
}
