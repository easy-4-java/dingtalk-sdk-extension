package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.property.*;

/**
 * Strategy interface for providing DingTalk configuration.
 * <p>Implementations supply configuration for corporate apps, personal mini apps,
 * suites, login apps, and robots based on corporate ID and application identifiers.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see DefaultDingTalkConfigProvider
 * @see DingTalkProperties
 */
public interface DingTalkConfigProvider {

    /**
     * Returns the DingTalk properties for the given corporate ID.
     *
     * @param corpId  the corporate ID
     * @return the DingTalk properties
     */
    DingTalkProperties getDingTalkProperties(String corpId);

    /**
     * Returns the corporate application properties for the given agent ID.
     *
     * @param corpId   the corporate ID
     * @param agentId  the application agent ID
     * @return the corporate app properties, or {@code null} if not found
     */
    DingTalkCorpAppProperties getDingTalkCorpAppProperties(String corpId, String agentId);

    /**
     * Returns the personal mini app properties for the given application ID.
     *
     * @param corpId  the corporate ID
     * @param appId   the application ID
     * @return the personal mini app properties, or {@code null} if not found
     */
    DingTalkPersonalMiniAppProperties getDingTalkPersonalMiniAppProperties(String corpId, String appId);

    /**
     * Returns the suite properties for the given suite ID.
     *
     * @param corpId   the corporate ID
     * @param suiteId  the suite ID
     * @return the suite properties, or {@code null} if not found
     */
    DingTalkSuiteProperties getDingTalkSuiteProperties(String corpId, String suiteId);

    /**
     * Returns the login properties for the given application ID.
     *
     * @param corpId  the corporate ID
     * @param appId   the application ID
     * @return the login properties, or {@code null} if not found
     */
    DingTalkLoginProperties getDingTalkLoginProperties(String corpId, String appId);

    /**
     * Returns the robot properties for the given robot ID.
     *
     * @param corpId   the corporate ID
     * @param robotId  the robot ID
     * @return the robot properties, or {@code null} if not found
     */
    DingTalkRobotProperties getDingTalkRobotProperties(String corpId, String robotId);

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
