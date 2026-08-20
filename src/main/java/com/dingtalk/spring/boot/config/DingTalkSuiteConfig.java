package com.dingtalk.spring.boot.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration for a third-party suite / ISV application.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see com.dingtalk.spring.boot.DingTalkConfigProvider#getDingTalkSuiteConfig(String, String)
 */
@Getter
@Setter
@ToString
public class DingTalkSuiteConfig {

    private String suiteId;

    private String appId;

    private String suiteKey;

    private String suiteSecret;

}
