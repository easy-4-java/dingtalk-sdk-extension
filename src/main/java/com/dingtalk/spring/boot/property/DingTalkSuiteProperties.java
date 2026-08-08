package com.dingtalk.spring.boot.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration properties for third-party enterprise applications
 * (suite-based mini programs and H5 apps).
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see com.dingtalk.spring.boot.DingTalkConfigProvider#getDingTalkSuiteProperties(String, String)
 */
@Getter
@Setter
@ToString
public class DingTalkSuiteProperties {

	/**
	 * Suite identifier for the third-party enterprise application.
	 */
	private String suiteId;

	/**
	 * Unique application ID.
	 */
	private String appId;

	/**
	 * Unique application key.
	 */
	private String suiteKey;

	/**
	 * Suite secret used for authentication.
	 */
	private String suiteSecret;

}
