package io.github.easy4j.dingtalk.service;

import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import io.github.easy4j.dingtalk.error.DingTalkApiException;
import io.github.easy4j.dingtalk.internal.DingTalkClientFactory;
import io.github.easy4j.dingtalk.service.DingTalkService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * Operations for DingTalk user information retrieval.
 * <p>Supports login-free access for enterprise internal applications,
 * third-party enterprise applications, and application management backends.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AbstractDingTalkService
 * @see DingTalkTemplate#opsForUser()
 * @see <a href="https://open.dingtalk.com/document/orgapp-server/enterprise-internal-application-logon-free">Enterprise internal application login-free</a>
 */
@Slf4j
public class DingTalkUserService extends AbstractDingTalkService {

	private final DingTalkClientFactory clientFactory;

	public DingTalkUserService(DingTalkService service) {
		this(service, DingTalkClientFactory.defaultFactory());
	}

	DingTalkUserService(DingTalkService service, DingTalkClientFactory clientFactory) {
		super(service);
		this.clientFactory = java.util.Objects.requireNonNull(clientFactory, "clientFactory");
	}

	@Deprecated
	public DingTalkUserService(DingTalkTemplate template) {
		this((DingTalkService) template);
	}

	/**
	 * @deprecated use {@link #getUserInfoByCode(String, String)}
	 */
	@Deprecated
	public OapiUserGetuserinfoResponse getUserinfoByCode(
			String code, String accessToken)
			throws ApiException, DingTalkApiException {
		return getUserInfoByCode(code, accessToken);
	}

	public OapiUserGetuserinfoResponse getUserInfoByCode(
			String code, String accessToken)
			throws ApiException, DingTalkApiException {
		DingTalkClient client = clientFactory.create(PREFIX + "/user/getuserinfo");
		OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
		request.setCode(code);
		request.setHttpMethod(METHOD_GET);
		OapiUserGetuserinfoResponse response = client.execute(request, accessToken);
		return executeChecked(response, response.getErrcode(), response.getErrmsg());
	}

}
