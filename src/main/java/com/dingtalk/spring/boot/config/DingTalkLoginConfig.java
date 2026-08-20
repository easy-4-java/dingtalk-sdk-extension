package com.dingtalk.spring.boot.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration for a mobile-login application (QR code / mobile-free login).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see com.dingtalk.spring.boot.DingTalkConfigProvider#getDingTalkLoginConfig(String, String)
 */
@Getter
@Setter
@ToString
public class DingTalkLoginConfig {

    private String appId;

    private String appSecret;

}
