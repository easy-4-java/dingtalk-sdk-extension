package com.dingtalk.spring.boot.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration properties for third-party personal mini applications.
 * <p>Each personal application is assigned a unique appId and appSecret
 * used to obtain user-authorized access tokens.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see com.dingtalk.spring.boot.DingTalkConfigProvider#getDingTalkPersonalMiniAppProperties(String, String)
 */
@Getter
@Setter
@ToString
public class DingTalkPersonalMiniAppProperties {

	/**
	 * Unique application ID for the personal mini app.
	 */
	private String appId;

	/**
	 * Application secret used to obtain user-authorized access tokens.
	 */
	private String appSecret;

}
