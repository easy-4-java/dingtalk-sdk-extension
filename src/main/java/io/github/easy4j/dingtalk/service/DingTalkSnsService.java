package io.github.easy4j.dingtalk.service;

import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetPersistentCodeRequest;
import com.dingtalk.api.request.OapiSnsGetSnsTokenRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoRequest;
import com.dingtalk.api.response.OapiSnsGetPersistentCodeResponse;
import com.dingtalk.api.response.OapiSnsGetSnsTokenResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoResponse;
import io.github.easy4j.dingtalk.error.DingTalkApiException;
import io.github.easy4j.dingtalk.error.DingTalkResponseValidator;
import io.github.easy4j.dingtalk.error.DingTalkRuntimeException;
import io.github.easy4j.dingtalk.internal.DingTalkClientFactory;
import io.github.easy4j.dingtalk.service.DingTalkService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * Operations for DingTalk third-party SNS login flows.
 * <p>All Open API responses are {@linkplain DingTalkResponseValidator validated}
 * before their typed or raw body fields are returned to the caller.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AbstractDingTalkService
 * @see DingTalkTemplate#opsForSns()
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/kymkv6">QR code login</a>
 */
@Slf4j
public class DingTalkSnsService extends AbstractDingTalkService {

	private final DingTalkClientFactory clientFactory;

	public DingTalkSnsService(DingTalkService service) {
		this(service, DingTalkClientFactory.defaultFactory());
	}

	DingTalkSnsService(DingTalkService service, DingTalkClientFactory clientFactory) {
		super(service);
		this.clientFactory = java.util.Objects.requireNonNull(clientFactory, "clientFactory");
	}

	@Deprecated
	public DingTalkSnsService(DingTalkTemplate template) {
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

	public String getSnsToken(String openId, String persistentCode, String accessToken)
			throws ApiException, DingTalkApiException {
		DingTalkClient client = clientFactory.create(PREFIX + "/sns/get_sns_token");
		OapiSnsGetSnsTokenRequest request = new OapiSnsGetSnsTokenRequest();
		request.setOpenid(openId);
		request.setPersistentCode(persistentCode);
		OapiSnsGetSnsTokenResponse response = client.execute(request, accessToken);
		try {
			DingTalkResponseValidator.requireSuccess(response, response.getErrcode(), response.getErrmsg());
		} catch (DingTalkApiException ex) {
			throw new DingTalkRuntimeException(ex.getMessage(), ex);
		}
		return response.getSnsToken();
	}

	public String getUserinfo(String snsToken) throws ApiException, DingTalkApiException {
		DingTalkClient client = clientFactory.create(PREFIX + "/sns/getuserinfo");
		OapiSnsGetuserinfoRequest request = new OapiSnsGetuserinfoRequest();
		request.setSnsToken(snsToken);
		request.setHttpMethod(METHOD_GET);
		OapiSnsGetuserinfoResponse response = client.execute(request);
		executeChecked(response, response.getErrcode(), response.getErrmsg());
		return response.getBody();
	}

}
