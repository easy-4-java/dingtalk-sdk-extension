package io.github.easy4j.dingtalk.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration for enterprise internal development applications
 * (mini programs and H5 apps).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see io.github.easy4j.dingtalk.config.DingTalkConfigProvider#getDingTalkCorpAppConfig(String, String)
 */
@Getter
@Setter
@ToString
public class DingTalkCorpAppConfig {

    /**
     * Application agent ID for enterprise internal development.
     */
    private String agentId;

    /**
     * Unique application key for enterprise internal development.
     */
    private String appKey;

    /**
     * Application secret for enterprise internal development.
     */
    private String appSecret;

}
