package com.dingtalk.spring.boot;

import java.util.List;

import com.dingtalk.spring.boot.config.DingTalkCorpAppConfig;
import com.dingtalk.spring.boot.config.DingTalkLoginConfig;
import com.dingtalk.spring.boot.config.DingTalkPersonalMiniAppConfig;
import com.dingtalk.spring.boot.config.DingTalkRobotConfig;
import com.dingtalk.spring.boot.config.DingTalkSuiteConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Root configuration object for DingTalk SDK operations.
 * <p>Contains the corporate ID, corporate secret, and lists of
 * configuration for every application type (enterprise internal apps,
 * personal mini apps, ISV suites, login apps, and robots).</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DefaultDingTalkConfigProvider
 */
@Getter
@Setter
@ToString
public class DingTalkConfig {

    /** Configuration property prefix — matches starter {@code @ConfigurationProperties}. */
    public static final String PREFIX = "dingtalk";

    /** Corporate ID (corpId). */
    private String corpId;

    /** Corporate secret. */
    private String corpSecret;

    /** Enterprise internal development: mini program and H5 configurations. */
    private List<DingTalkCorpAppConfig> corpApps;

    /** Third-party personal application: mini program configurations. */
    private List<DingTalkPersonalMiniAppConfig> apps;

    /** Third-party enterprise application: mini program and H5 configurations. */
    private List<DingTalkSuiteConfig> suites;

    /** Mobile access application: QR code login configurations. */
    private List<DingTalkLoginConfig> logins;

    /** DingTalk robot configurations. */
    private List<DingTalkRobotConfig> robots;

}
