package io.github.easy4j.dingtalk.service;

import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetUseridByUnionidRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetUseridByUnionidResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import io.github.easy4j.dingtalk.error.DingTalkApiException;
import io.github.easy4j.dingtalk.internal.DingTalkClientFactory;
import io.github.easy4j.dingtalk.service.DingTalkService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * Operations for DingTalk enterprise internal application login-free access
 * and user management.
 * <p>All Open API responses are {@link #executeChecked(com.taobao.api.TaobaoResponse, Long, String)}
 * validated before being returned.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AbstractDingTalkService
 * @see DingTalkTemplate#opsForAccount()
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/clotub">Enterprise internal application login-free</a>
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/ege851">User management</a>
 */
@Slf4j
public class DingTalkAccountService extends AbstractDingTalkService {

	private final DingTalkClientFactory clientFactory;

	public DingTalkAccountService(DingTalkService service) {
		this(service, DingTalkClientFactory.defaultFactory());
	}

	DingTalkAccountService(DingTalkService service, DingTalkClientFactory clientFactory) {
		super(service);
		this.clientFactory = java.util.Objects.requireNonNull(clientFactory, "clientFactory");
	}

	@Deprecated
	public DingTalkAccountService(DingTalkTemplate template) {
		this((DingTalkService) template);
	}

	/** @deprecated use {@link #getUserInfoByCode(String, String)} */
	@Deprecated
	public OapiUserGetuserinfoResponse getUserinfoBycode(
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

	/** @deprecated use {@link #getUserIdByUnionId(String, String)} */
	@Deprecated
	public OapiUserGetUseridByUnionidResponse getUseridByUnionid(
			String unionid, String accessToken)
			throws ApiException, DingTalkApiException {
		return getUserIdByUnionId(unionid, accessToken);
	}

	public OapiUserGetUseridByUnionidResponse getUserIdByUnionId(
			String unionId, String accessToken)
			throws ApiException, DingTalkApiException {
		DingTalkClient client = clientFactory.create(PREFIX + "/user/getUseridByUnionid");
		OapiUserGetUseridByUnionidRequest request = new OapiUserGetUseridByUnionidRequest();
		request.setUnionid(unionId);
		request.setHttpMethod(METHOD_GET);
		OapiUserGetUseridByUnionidResponse response = client.execute(request, accessToken);
		return executeChecked(response, response.getErrcode(), response.getErrmsg());
	}

	/** @deprecated use {@link #getUserByUserId(String, String)} */
	@Deprecated
	public OapiUserGetResponse getUserByUserid(
			String userid, String accessToken)
			throws ApiException, DingTalkApiException {
		return getUserByUserId(userid, accessToken);
	}

	public OapiUserGetResponse getUserByUserId(
			String userId, String accessToken)
			throws ApiException, DingTalkApiException {
		DingTalkClient client = clientFactory.create(PREFIX + "/user/get");
		OapiUserGetRequest request = new OapiUserGetRequest();
		request.setUserid(userId);
		request.setHttpMethod(METHOD_GET);
		OapiUserGetResponse response = client.execute(request, accessToken);
		return executeChecked(response, response.getErrcode(), response.getErrmsg());
	}

}
