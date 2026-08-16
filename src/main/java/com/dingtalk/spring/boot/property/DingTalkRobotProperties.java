package com.dingtalk.spring.boot.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration properties for a DingTalk robot webhook.
 * <p>Contains the robot ID, access token for the webhook URL,
 * and an optional secret token for signature verification.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see com.dingtalk.spring.boot.DingTalkRobotOperations
 */
@Getter
@Setter
@ToString
public class DingTalkRobotProperties {

	/**
	 * DingTalk robot identifier.
	 */
	private String robotId;

	/**
	 * Access token from the robot webhook URL.
	 */
	private String accessToken;

	/**
	 * Secret token used for webhook signature verification (SEC-prefixed).
	 */
	private String secretToken;

}
