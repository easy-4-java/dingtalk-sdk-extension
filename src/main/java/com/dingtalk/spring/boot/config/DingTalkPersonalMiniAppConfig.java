package com.dingtalk.spring.boot.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration for third-party personal mini applications.
 * <p>Each personal application is assigned a unique appId and appSecret
 * used to obtain user-authorized access tokens.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see com.dingtalk.spring.boot.DingTalkConfigProvider#getDingTalkPersonalMiniAppConfig(String, String)
 */
@Getter
@Setter
@ToString
public class DingTalkPersonalMiniAppConfig {

    /**
     * Unique application ID for the personal mini app.
     */
    private String appId;

    /**
     * Application secret used to obtain user-authorized access tokens.
     */
    private String appSecret;

}
