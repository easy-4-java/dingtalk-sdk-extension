package com.dingtalk.spring.boot;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetPersistentCodeRequest;
import com.dingtalk.api.request.OapiSnsGetSnsTokenRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoRequest;
import com.dingtalk.api.response.OapiSnsGetPersistentCodeResponse;
import com.dingtalk.api.response.OapiSnsGetSnsTokenResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * Operations for DingTalk third-party SNS login flows.
 * <p>Supports QR code login, in-app login, and password-based login
 * for third-party websites using the DingTalk SNS API.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkOperations
 * @see DingTalkTemplate#opsForSns()
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/kymkv6">QR code login</a>
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/etaarr">In-app login</a>
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/hmxp3f">Password login</a>
 */
@Slf4j
public class DingTalkSnsOperations extends DingTalkOperations {

	/**
	 * Constructs SNS operations with the given template.
	 *
	 * @param template the DingTalk template; must not be {@code null}
	 */
	public DingTalkSnsOperations(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * Retrieves user information by temporary authorization code (QR code login).
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

	/**
	 * Retrieves the SNS token using the open ID and persistent code.
	 *
	 * @param openId          the user's open ID
	 * @param persistentCode  the persistent authorization code
	 * @param accessToken     the open application access token
	 * @return the SNS token string
	 * @throws ApiException if the API request fails
	 */
	public String getSnsToken(String openId, String persistentCode, String accessToken) throws ApiException {
		DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/sns/get_sns_token");
		OapiSnsGetSnsTokenRequest request = new OapiSnsGetSnsTokenRequest();
		request.setOpenid(openId);
		request.setPersistentCode(persistentCode);
		OapiSnsGetSnsTokenResponse response = client.execute(request, accessToken);
		return response.getSnsToken();
	}

	/**
	 * Retrieves user personal information using the SNS token.
	 *
	 * @param snsToken  the SNS token obtained from {@link #getSnsToken}
	 * @return the response body as a JSON string
	 * @throws ApiException if the API request fails
	 */
	public String getUserinfo(String snsToken) throws ApiException{
		DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/sns/getuserinfo");
		OapiSnsGetuserinfoRequest request = new OapiSnsGetuserinfoRequest();
		request.setSnsToken(snsToken);
		request.setHttpMethod(METHOD_GET);
		OapiSnsGetuserinfoResponse response = client.execute(request);
		return response.getBody();
	}

}
