package io.github.easy4j.dingtalk.service;

import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetPersistentCodeRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.response.OapiSnsGetPersistentCodeResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import io.github.easy4j.dingtalk.error.DingTalkApiException;
import io.github.easy4j.dingtalk.internal.DingTalkClientFactory;
import io.github.easy4j.dingtalk.service.DingTalkService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * Operations for DingTalk application management backend login-free access.
 * <p>All Open API responses are {@link #executeChecked(com.taobao.api.TaobaoResponse, Long, String)}
 * validated before being returned to the caller.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AbstractDingTalkService
 * @see DingTalkTemplate#opsForSso()
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/xswxhg">Application management backend login-free</a>
 */
@Slf4j
public class DingTalkSsoService extends AbstractDingTalkService {

	private final DingTalkClientFactory clientFactory;

	public DingTalkSsoService(DingTalkService service) {
		this(service, DingTalkClientFactory.defaultFactory());
	}

	DingTalkSsoService(DingTalkService service, DingTalkClientFactory clientFactory) {
		super(service);
		this.clientFactory = java.util.Objects.requireNonNull(clientFactory, "clientFactory");
	}

	@Deprecated
	public DingTalkSsoService(DingTalkTemplate template) {
		this((DingTalkService) template);
	}

	public OapiSnsGetuserinfoBycodeResponse getUserinfoByTmpCode(
			String tmpAuthCode, String accessKey, String accessSecret)
			throws ApiException, DingTalkApiException {
		DingTalkClient client = clientFactory.create(PREFIX + "/sns/getuserinfo_bycode");
		OapiSnsGetuserinfoBycodeRequest request = new OapiSnsGetuserinfoBycodeRequest();
		request.setTmpAuthCode(tmpAuthCode);
		OapiSnsGetuserinfoBycodeResponse response =
				client.execute(request, accessKey, accessSecret);
		return executeChecked(response, response.getErrcode(), response.getErrmsg());
	}

	public String getPersistentCode(String tmpAuthCode, String accessToken)
			throws ApiException, DingTalkApiException {
		DingTalkClient client = clientFactory.create(PREFIX + "/sns/get_persistent_code");
		OapiSnsGetPersistentCodeRequest request = new OapiSnsGetPersistentCodeRequest();
		request.setTmpAuthCode(tmpAuthCode);
		OapiSnsGetPersistentCodeResponse response = client.execute(request, accessToken);
		executeChecked(response, response.getErrcode(), response.getErrmsg());
		return response.getBody();
	}

}
