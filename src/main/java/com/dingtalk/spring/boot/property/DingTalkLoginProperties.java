package com.dingtalk.spring.boot.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration properties for mobile access applications
 * (QR code login).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see com.dingtalk.spring.boot.DingTalkConfigProvider#getDingTalkLoginProperties(String, String)
 */
@Getter
@Setter
@ToString
public class DingTalkLoginProperties {

	/**
	 * Application ID for the QR code login flow.
	 */
	private String appId;

	/**
	 * Application secret for the QR code login flow.
	 */
	private String appSecret;

}
