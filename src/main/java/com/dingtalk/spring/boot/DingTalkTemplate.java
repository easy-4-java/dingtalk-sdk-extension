/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dingtalk.spring.boot;

import java.net.URLEncoder;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

/**
 * Central entry point for interacting with the DingTalk Open API.
 * <p>This template manages configuration, access tokens, and exposes
 * operation facades for account, SNS, SSO, JSAPI, robot, and user
 * operations.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see DingTalkConfigProvider
 * @see DingTalkAccessTokenProvider
 * @see DingTalkAccountOperations
 * @see DingTalkSnsOperations
 * @see DingTalkSsoOperations
 * @see DingTalkJsapiOperations
 * @see DingTalkRobotOperations
 * @see DingTalkUserOperations
 */
@Slf4j
public class DingTalkTemplate {

	private final DingTalkConfigProvider dingTalkConfigProvider;
	private final DingTalkAccessTokenProvider dingTalkAccessTokenProvider;

	private final DingTalkAccountOperations accountOps = new DingTalkAccountOperations(this);
	private final DingTalkSnsOperations snsOps = new DingTalkSnsOperations(this);
	private final DingTalkSsoOperations ssoOps = new DingTalkSsoOperations(this);
	private final DingTalkJsapiOperations jsapiOps = new DingTalkJsapiOperations(this);
	private final DingTalkRobotOperations robotOps = new DingTalkRobotOperations(this);
	private final DingTalkUserOperations userOps = new DingTalkUserOperations(this);

	/**
	 * Constructs a new DingTalk template with the given providers.
	 *
	 * @param dingTalkConfigProvider       the configuration provider; must not be {@code null}
	 * @param dingTalkAccessTokenProvider  the access token provider; must not be {@code null}
	 */
	public DingTalkTemplate(DingTalkConfigProvider dingTalkConfigProvider, DingTalkAccessTokenProvider dingTalkAccessTokenProvider) {
		this.dingTalkConfigProvider = dingTalkConfigProvider;
		this.dingTalkAccessTokenProvider = dingTalkAccessTokenProvider;
	}

	/**
	 * Checks whether the given application key is registered.
	 *
	 * @param appKey the application key or ID
	 * @return {@code true} if the app key is known
	 */
	public boolean hasAppKey(String appKey) {
		return dingTalkConfigProvider.hasAppKey(appKey);
	}

	/**
	 * Returns the corporate ID for the given application key.
	 *
	 * @param appKey the application key or ID
	 * @return the corporate ID
	 */
	public String getCorpId(String appKey){
		return dingTalkConfigProvider.getCorpId(appKey);
	}

	/**
	 * Returns the corporate secret for the given corporate ID.
	 *
	 * @param corpId the corporate ID
	 * @return the corporate secret
	 */
	public String getCorpSecret(String corpId){
		return dingTalkConfigProvider.getCorpSecret(corpId);
	}

	/**
	 * Returns the application secret for the given corporate ID and application key.
	 *
	 * @param corpId  the corporate ID
	 * @param appKey  the application key or ID
	 * @return the application secret
	 */
	public String getAppSecret(String corpId, String appKey) {
		return dingTalkConfigProvider.getAppSecret(corpId, appKey);
	}

	/**
	 * Retrieves the enterprise internal access token.
	 *
	 * @param corpId  the corporate ID
	 * @param appKey  the application key
	 * @return the access token
	 * @throws ApiException if the API request fails
	 * @see <a href="https://open-doc.dingtalk.com/microapp/serverapi2/eev437">DingTalk documentation</a>
	 */
	public String getAccessToken(String corpId, String appKey) throws ApiException {
		return dingTalkAccessTokenProvider.getAccessToken(corpId, appKey);
	}

	/**
	 * Retrieves the SNS access token for an open application.
	 *
	 * @param corpId  the corporate ID
	 * @param appId   the application ID
	 * @return the SNS access token
	 * @throws ApiException if the API request fails
	 */
	public String getSnsAccessToken(String corpId, String appId) throws ApiException {
		return dingTalkAccessTokenProvider.getSnsAccessToken(corpId, appId);
	}

	/**
	 * Computes an HMAC-SHA256 signature for the given secret and timestamp.
	 * <p>The signature is URL-encoded and Base64-encoded before being returned.</p>
	 *
	 * @param secret    the robot secret token (SEC-prefixed)
	 * @param timestamp the current timestamp in milliseconds
	 * @return the URL-encoded signature string, or {@code null} on error
	 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq/9e91d73c">DingTalk signature documentation</a>
	 */
	public String getSign(String secret, Long timestamp) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.getEncoder().encode(signData)), "UTF-8");
            log.debug("Signature computed: sign = {}", sign);
            return sign;
        } catch (Exception e) {
            log.error("Failed to compute signature: errMsg = {}", e);
            return null;
        }
    }

	/**
	 * Returns the account operations facade.
	 *
	 * @return the {@link DingTalkAccountOperations} instance
	 */
	public DingTalkAccountOperations opsForAccount() {
		return accountOps;
	}

	/**
	 * Returns the SNS operations facade.
	 *
	 * @return the {@link DingTalkSnsOperations} instance
	 */
	public DingTalkSnsOperations opsForSns() {
		return snsOps;
	}

	/**
	 * Returns the SSO operations facade.
	 *
	 * @return the {@link DingTalkSsoOperations} instance
	 */
	public DingTalkSsoOperations opsForSso() {
		return ssoOps;
	}

	/**
	 * Returns the JSAPI operations facade.
	 *
	 * @return the {@link DingTalkJsapiOperations} instance
	 */
	public DingTalkJsapiOperations opsForJsapi() {
		return jsapiOps;
	}

	/**
	 * Returns the robot operations facade.
	 *
	 * @return the {@link DingTalkRobotOperations} instance
	 */
	public DingTalkRobotOperations opsForRobot() {
		return robotOps;
	}

	/**
	 * Returns the user operations facade.
	 *
	 * @return the {@link DingTalkUserOperations} instance
	 */
	public DingTalkUserOperations opsForUser() {
		return userOps;
	}

	/**
	 * Returns the access token provider.
	 *
	 * @return the {@link DingTalkAccessTokenProvider} instance
	 */
	public DingTalkAccessTokenProvider getDingTalkAccessTokenProvider() {
		return dingTalkAccessTokenProvider;
	}

	/**
	 * Returns the configuration provider.
	 *
	 * @return the {@link DingTalkConfigProvider} instance
	 */
	public DingTalkConfigProvider getDingTalkConfigProvider() {
		return dingTalkConfigProvider;
	}

}
