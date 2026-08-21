package io.github.easy4j.dingtalk.config.impl;

import io.github.easy4j.dingtalk.config.*; import io.github.easy4j.dingtalk.config.impl.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link DingTalkConfigProvider} backed by a {@link DingTalkConfig} instance.
 * <p>On {@link #init()}, application keys and secrets from every configured sub-config list
 * are indexed into a {@link ConcurrentHashMap} for fast lookup by appKey/appId.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkConfigProvider
 * @see DingTalkConfig
 */
public class DefaultDingTalkConfigProvider implements DingTalkConfigProvider {

    private final DingTalkConfig dingTalkConfig;
    private final Map<String, String> appKeySecret = new ConcurrentHashMap<>();

    /**
     * Constructs a provider with the given root configuration object.
     *
     * @param dingTalkConfig  the DingTalk configuration object; must not be {@code null}
     */
    public DefaultDingTalkConfigProvider(DingTalkConfig dingTalkConfig) {
        this.dingTalkConfig = dingTalkConfig;
        init();
    }

    /**
     * Initializes the internal app-key-to-secret mapping from all configured lists.
     * <p>Must be called once after construction and before using lookup methods. Safe to re-call (idempotent).</p>
     */
    public void init() {
        if (!CollectionUtils.isEmpty(this.dingTalkConfig.getCorpApps())) {
            for (DingTalkCorpAppConfig cfg : this.dingTalkConfig.getCorpApps()) {
                String key = cfg == null ? null : cfg.getAppKey();
                String secret = cfg == null ? null : cfg.getAppSecret();
                if (StringUtils.isNotEmpty(key)) {
                    appKeySecret.put(key, StringUtils.defaultIfBlank(secret, ""));
                }
            }
        }
        if (!CollectionUtils.isEmpty(this.dingTalkConfig.getApps())) {
            for (DingTalkPersonalMiniAppConfig cfg : this.dingTalkConfig.getApps()) {
                String id = cfg == null ? null : cfg.getAppId();
                String secret = cfg == null ? null : cfg.getAppSecret();
                if (StringUtils.isNotEmpty(id)) {
                    appKeySecret.put(id, StringUtils.defaultIfBlank(secret, ""));
                }
            }
        }
        if (!CollectionUtils.isEmpty(this.dingTalkConfig.getSuites())) {
            for (DingTalkSuiteConfig cfg : this.dingTalkConfig.getSuites()) {
                String id = cfg == null ? null : cfg.getAppId();
                String secret = cfg == null ? null : cfg.getSuiteSecret();
                if (StringUtils.isNotEmpty(id)) {
                    appKeySecret.put(id, StringUtils.defaultIfBlank(secret, ""));
                }
            }
        }
        if (!CollectionUtils.isEmpty(this.dingTalkConfig.getLogins())) {
            for (DingTalkLoginConfig cfg : this.dingTalkConfig.getLogins()) {
                String id = cfg == null ? null : cfg.getAppId();
                String secret = cfg == null ? null : cfg.getAppSecret();
                if (StringUtils.isNotEmpty(id)) {
                    appKeySecret.put(id, StringUtils.defaultIfBlank(secret, ""));
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public DingTalkConfig getDingTalkConfig(String corpId) {
        return dingTalkConfig;
    }

    /** {@inheritDoc} */
    @Override
    public DingTalkCorpAppConfig getDingTalkCorpAppConfig(String corpId, String agentId) {
        if (CollectionUtils.isEmpty(dingTalkConfig.getCorpApps())) {
            return null;
        }
        Optional<DingTalkCorpAppConfig> optional = dingTalkConfig.getCorpApps().stream()
                .filter(item -> StringUtils.equals(item.getAgentId(), agentId))
                .findFirst();
        return optional.orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public DingTalkPersonalMiniAppConfig getDingTalkPersonalMiniAppConfig(String corpId, String appId) {
        if (CollectionUtils.isEmpty(dingTalkConfig.getApps())) {
            return null;
        }
        Optional<DingTalkPersonalMiniAppConfig> optional = dingTalkConfig.getApps().stream()
                .filter(item -> StringUtils.equals(item.getAppId(), appId))
                .findFirst();
        return optional.orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public DingTalkSuiteConfig getDingTalkSuiteConfig(String corpId, String suiteId) {
        if (CollectionUtils.isEmpty(dingTalkConfig.getSuites())) {
            return null;
        }
        Optional<DingTalkSuiteConfig> optional = dingTalkConfig.getSuites().stream()
                .filter(item -> StringUtils.equals(item.getSuiteId(), suiteId)
                             || StringUtils.equals(item.getSuiteKey(), suiteId))
                .findFirst();
        return optional.orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public DingTalkLoginConfig getDingTalkLoginConfig(String corpId, String appId) {
        if (CollectionUtils.isEmpty(dingTalkConfig.getLogins())) {
            return null;
        }
        Optional<DingTalkLoginConfig> optional = dingTalkConfig.getLogins().stream()
                .filter(item -> StringUtils.equals(item.getAppId(), appId))
                .findFirst();
        return optional.orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public DingTalkRobotConfig getDingTalkRobotConfig(String corpId, String robotId) {
        if (CollectionUtils.isEmpty(dingTalkConfig.getRobots())) {
            return null;
        }
        Optional<DingTalkRobotConfig> optional = dingTalkConfig.getRobots().stream()
                .filter(item -> StringUtils.equals(item.getRobotId(), robotId)
                             || StringUtils.equals(item.getAccessToken(), robotId))
                .findFirst();
        return optional.orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasAppKey(String appKey) {
        return appKeySecret.containsKey(appKey);
    }

    /** {@inheritDoc} */
    @Override
    public String getCorpId(String appKey) {
        return dingTalkConfig.getCorpId();
    }

    /** {@inheritDoc} */
    @Override
    public String getCorpSecret(String corpId) {
        return dingTalkConfig.getCorpSecret();
    }

    /** {@inheritDoc} */
    @Override
    public String getAppSecret(String corpId, String appKey) {
        return appKeySecret.get(appKey);
    }

}
