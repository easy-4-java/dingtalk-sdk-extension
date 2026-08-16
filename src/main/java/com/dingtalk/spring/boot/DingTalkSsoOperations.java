package com.dingtalk.spring.boot;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetPersistentCodeRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.response.OapiSnsGetPersistentCodeResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

/**
 * Operations for DingTalk application management backend login-free access.
 * <p>Provides methods to retrieve user information and persistent codes
 * using the SNS API for third-party application login flows.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkOperations
 * @see DingTalkTemplate#opsForSso()
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/xswxhg">Application management backend login-free</a>
 */
@Slf4j
public class DingTalkSsoOperations extends DingTalkOperations {

	/**
	 * Constructs SSO operations with the given template.
	 *
	 * @param template the DingTalk template; must not be {@code null}
	 */
	public DingTalkSsoOperations(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * Retrieves user information by temporary authorization code for third-party application QR code login.
	 * <p>The temporary authorization code can only be used once.</p>
	 *
	 * @param tmp_auth_code  the temporary authorization code from redirect
	 * @param accessKey      the application appId
	 * @param accessSecret   the application secret
	 * @return the user info response
	 * @throws ApiException if the API request fails
	 * @see <a href="https://open-doc.dingtalk.com/microapp/serverapi2/kymkv6">QR code login documentation</a>
	 */
	public OapiSnsGetuserinfoBycodeResponse getUserinfoByTmpCode( String tmp_auth_code, String accessKey, String accessSecret) throws ApiException {
		DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/sns/getuserinfo_bycode");
		OapiSnsGetuserinfoBycodeRequest request = new OapiSnsGetuserinfoBycodeRequest();
		request.setTmpAuthCode(tmp_auth_code);
		return client.execute(request, accessKey, accessSecret);
	}

	/**
	 * Retrieves the persistent authorization code for the user.
	 *
	 * @param tmp_auth_code  the temporary authorization code from redirect
	 * @param accessToken    the open application access token
	 * @return the response body as a JSON string
	 * @throws ApiException if the API request fails
	 */
	public String getPersistentCode(String tmp_auth_code, String accessToken) throws ApiException  {
		DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/sns/get_persistent_code");
		OapiSnsGetPersistentCodeRequest request = new OapiSnsGetPersistentCodeRequest();
		request.setTmpAuthCode(tmp_auth_code);
		OapiSnsGetPersistentCodeResponse response = client.execute(request, accessToken);
		return response.getBody();
	}

}
