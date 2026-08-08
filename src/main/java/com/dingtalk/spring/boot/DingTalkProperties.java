package com.dingtalk.spring.boot;

import java.util.List;

import com.dingtalk.spring.boot.property.DingTalkCorpAppProperties;
import com.dingtalk.spring.boot.property.DingTalkLoginProperties;
import com.dingtalk.spring.boot.property.DingTalkPersonalMiniAppProperties;
import com.dingtalk.spring.boot.property.DingTalkRobotProperties;
import com.dingtalk.spring.boot.property.DingTalkSuiteProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Root configuration properties for DingTalk integration.
 * <p>Contains the corporate ID, corporate secret, and lists of
 * configuration for various application types (corporate apps,
 * personal mini apps, suites, login apps, and robots).</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see DefaultDingTalkConfigProvider
 */
@Getter
@Setter
@ToString
public class DingTalkProperties {

	/** Configuration property prefix. */
	public static final String PREFIX = "dingtalk";

	/**
	 * Corporate ID (corpId).
	 */
	private String corpId;

	/**
	 * Corporate secret.
	 */
	private String corpSecret;

	/**
	 * Enterprise internal development: mini program and H5 configurations.
	 */
	private List<DingTalkCorpAppProperties> corpApps;

	/**
	 * Third-party personal application: mini program configurations.
	 */
	private List<DingTalkPersonalMiniAppProperties> apps;

	/**
	 * Third-party enterprise application: mini program and H5 configurations.
	 */
	private List<DingTalkSuiteProperties> suites;

	/**
	 * Mobile access application: QR code login configurations.
	 */
	private List<DingTalkLoginProperties> logins;

	/**
	 * DingTalk robot configurations.
	 */
	private List<DingTalkRobotProperties> robots;

}
