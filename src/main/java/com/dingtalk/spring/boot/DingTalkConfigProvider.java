package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.config.*;

/**
 * Strategy interface for providing DingTalk configuration.
 * <p>Implementations supply configuration for corporate apps, personal mini apps,
 * suites, login apps, and robots keyed by corporate ID and application identifiers.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DefaultDingTalkConfigProvider
 * @see DingTalkConfig
 */
public interface DingTalkConfigProvider {

    /**
     * Returns the root DingTalk configuration for the given corporate ID.
     *
     * @param corpId  the corporate ID
     * @return the root DingTalk config object
     */
    DingTalkConfig getDingTalkConfig(String corpId);

    /**
     * Returns the enterprise-internal app config for the given agent ID.
     *
     * @param corpId   the corporate ID
     * @param agentId  the application agent ID
     * @return the corp-app config, or {@code null} if not found
     */
    DingTalkCorpAppConfig getDingTalkCorpAppConfig(String corpId, String agentId);

    /**
     * Returns the personal-miniapp config for the given application ID.
     *
     * @param corpId  the corporate ID
     * @param appId   the application ID
     * @return the personal-miniapp config, or {@code null} if not found
     */
    DingTalkPersonalMiniAppConfig getDingTalkPersonalMiniAppConfig(String corpId, String appId);

    /**
     * Returns the ISV-suite config for the given suite ID.
     *
     * @param corpId   the corporate ID
     * @param suiteId  the suite ID
     * @return the suite config, or {@code null} if not found
     */
    DingTalkSuiteConfig getDingTalkSuiteConfig(String corpId, String suiteId);

    /**
     * Returns the mobile-login app config for the given application ID.
     *
     * @param corpId  the corporate ID
     * @param appId   the application ID
     * @return the login config, or {@code null} if not found
     */
    DingTalkLoginConfig getDingTalkLoginConfig(String corpId, String appId);

    /**
     * Returns the robot config for the given robot ID.
     *
     * @param corpId   the corporate ID
     * @param robotId  the robot ID
     * @return the robot config, or {@code null} if not found
     */
    DingTalkRobotConfig getDingTalkRobotConfig(String corpId, String robotId);

    /**
     * Checks whether the given application key is registered.
     *
     * @param appKey  the application key
     * @return {@code true} if the key is known
     */
    boolean hasAppKey(String appKey);

    /**
     * Returns the corporate ID for the given application key.
     *
     * @param appKey  the application key or ID
     * @return the corporate ID
     */
    String getCorpId(String appKey);

    /**
     * Returns the corporate secret for the given corporate ID.
     *
     * @param corpId  the corporate ID
     * @return the corporate secret
     */
    String getCorpSecret(String corpId);

    /**
     * Returns the application secret for the given corporate ID and application key.
     *
     * @param corpId  the corporate ID
     * @param appKey  the application key
     * @return the application secret
     */
    String getAppSecret(String corpId, String appKey);

}
