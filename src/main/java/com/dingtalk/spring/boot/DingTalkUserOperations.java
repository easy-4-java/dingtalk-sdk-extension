package com.dingtalk.spring.boot;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * Operations for DingTalk user information retrieval.
 * <p>Supports login-free access for enterprise internal applications,
 * third-party enterprise applications, and application management backends.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkOperations
 * @see DingTalkTemplate#opsForUser()
 * @see <a href="https://open.dingtalk.com/document/orgapp-server/enterprise-internal-application-logon-free">Enterprise internal application login-free</a>
 * @see <a href="https://open.dingtalk.com/document/orgapp-server/third-party-enterprise-application-logon-free">Third-party enterprise application login-free</a>
 * @see <a href="https://open.dingtalk.com/document/orgapp-server/log-on-site-application-management-backend">Application management backend login-free</a>
 */
@Slf4j
public class DingTalkUserOperations extends DingTalkOperations {

	/**
	 * Constructs user operations with the given template.
	 *
	 * @param template the DingTalk template; must not be {@code null}
	 */
	public DingTalkUserOperations(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * Retrieves user information by authorization code.
	 *
	 * @param code         the login-free authorization code
	 * @param accessToken  the application access token
	 * @return the user info response
	 * @throws ApiException if the API request fails
	 */
	public OapiUserGetuserinfoResponse getUserinfoByCode(String code, String accessToken) throws ApiException {
		DingTalkClient client = new DefaultDingTalkClient(PREFIX + "user/getuserinfo");
		OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
		request.setCode(code);
		return client.execute(request, accessToken);
	}

}
