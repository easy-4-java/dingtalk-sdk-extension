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
package io.github.easy4j.dingtalk.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import io.github.easy4j.dingtalk.model.jsapi.JsapiTicketSignature;
import io.github.easy4j.dingtalk.model.jsapi.TicketType;
import io.github.easy4j.dingtalk.error.DingTalkApiException;
import io.github.easy4j.dingtalk.error.DingTalkResponseValidator;
import io.github.easy4j.dingtalk.error.DingTalkRuntimeException;
import io.github.easy4j.dingtalk.service.DingTalkService;
import io.github.easy4j.dingtalk.storage.DingTalkConfigStorage;
import io.github.easy4j.dingtalk.internal.JsapiSignatureGenerator;
import io.github.easy4j.dingtalk.internal.NonceGenerator;
import com.taobao.api.ApiException;

import java.util.concurrent.locks.Lock;

/**
 * Operations for DingTalk JSAPI ticket retrieval and signature creation.
 * <p>Tickets are cached and refreshed with double-checked locking through the
 * service-level {@link DingTalkConfigStorage}.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AbstractDingTalkService
 * @see DingTalkTemplate#opsForJsapi()
 * @see <a href="https://ding-doc.dingtalk.com/doc#/dev/uwa7vs">JSAPI signature documentation</a>
 */
public class DingTalkJsapiService extends AbstractDingTalkService {

	DefaultDingTalkClient client = new DefaultDingTalkClient(PREFIX + "/get_jsapi_ticket");

	public DingTalkJsapiService(DingTalkService service) {
		super(service);
	}

	@Deprecated
	public DingTalkJsapiService(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * Retrieves a JSAPI ticket of the requested type, returning a cached value
	 * when still valid. Performs a single refresh under a striped lock when the
	 * stored ticket has expired.
	 *
	 * @param type    the ticket type (JSAPI / etc.)
	 * @param corpId  the corporate ID used to build the storage key
	 * @param appKey  the app key used to build the storage key and obtain an access token
	 * @return the ticket string
	 * @throws ApiException if the DingTalk API round-trip fails
	 */
	public String getJsapiTicket(TicketType type, String corpId, String appKey) throws ApiException {
		String key = cacheKey(corpId, appKey);
		DingTalkConfigStorage storage = service.getConfigStorage();
		if (storage != null && !storage.isJsapiTicketExpired(key, type)) {
			return storage.getJsapiTicket(key, type);
		}
		String accessToken = service.getAccessToken(corpId, appKey);
		if (storage != null) {
			Lock lock = storage.getJsapiTicketLock(key);
			lock.lock();
			try {
				if (!storage.isJsapiTicketExpired(key, type)) {
					return storage.getJsapiTicket(key, type);
				}
				return fetchAndStoreTicket(type, corpId, appKey, accessToken, key, storage);
			} finally {
				lock.unlock();
			}
		}
		OapiGetJsapiTicketResponse response = fetchResponse(accessToken);
		validate(response);
		return response.getTicket();
	}

	/**
	 * Retrieves a raw JSAPI ticket response via the DingTalk Open API.
	 * The response is validated; non-zero errcodes become a runtime exception.
	 *
	 * @param type         the ticket type
	 * @param accessToken  the access token for API calls
	 * @return the JSAPI ticket response
	 * @throws ApiException if the API request fails
	 */
	public OapiGetJsapiTicketResponse getTicket(TicketType type, String accessToken) throws ApiException {
		OapiGetJsapiTicketResponse response = fetchResponse(accessToken);
		validate(response);
		return response;
	}

	/**
	 * Creates a JSAPI signature for the given URL and agent.
	 * <p>The signature is computed using the cached JSAPI ticket, a random nonce,
	 * and the current timestamp.</p>
	 *
	 * @param url          the current page URL (without hash fragment)
	 * @param corpId       the corporate ID
	 * @param agentId      the application agent ID
	 * @param appKey       the application key used to retrieve token and ticket
	 * @return the computed {@link JsapiTicketSignature}
	 * @throws ApiException if the API request fails
	 */
	public JsapiTicketSignature createSignature(String url, String corpId, String agentId, String appKey)
			throws ApiException {

		long timestamp = System.currentTimeMillis() / 1000;
		String randomStr = NonceGenerator.getRandomStr();
		String jsapiTicket = getJsapiTicket(TicketType.JSAPI, corpId, appKey);
		String signature = JsapiSignatureGenerator.sign(jsapiTicket, randomStr, timestamp, url);
		JsapiTicketSignature jsapiSignature = new JsapiTicketSignature();
		jsapiSignature.setAgentId(agentId);
		jsapiSignature.setTimestamp(timestamp);
		jsapiSignature.setNonceStr(randomStr);
		jsapiSignature.setUrl(url);
		jsapiSignature.setSignature(signature);
		return jsapiSignature;

	}

	/**
	 * @deprecated Use {@link #createSignature(String, String, String, String)} which
	 *             manages tickets and access tokens via the service graph instead.
	 */
	@Deprecated
	public JsapiTicketSignature createSignature(String url, String agentId, String accessToken)
			throws ApiException {

		long timestamp = System.currentTimeMillis() / 1000;
		String randomStr = NonceGenerator.getRandomStr();
		OapiGetJsapiTicketResponse jsapiTicket = getTicket(TicketType.JSAPI, accessToken);
		String signature = JsapiSignatureGenerator.sign(jsapiTicket.getTicket(), randomStr, timestamp, url);
		JsapiTicketSignature jsapiSignature = new JsapiTicketSignature();
		jsapiSignature.setAgentId(agentId);
		jsapiSignature.setTimestamp(timestamp);
		jsapiSignature.setNonceStr(randomStr);
		jsapiSignature.setUrl(url);
		jsapiSignature.setSignature(signature);
		return jsapiSignature;

	}

	private String fetchAndStoreTicket(TicketType type, String corpId, String appKey,
									   String accessToken, String key,
									   DingTalkConfigStorage storage) throws ApiException {
		OapiGetJsapiTicketResponse response = fetchResponse(accessToken);
		validate(response);
		String ticket = response.getTicket();
		Number expiresIn = response.getExpiresIn();
		long expires = expiresIn == null ? 7200L : expiresIn.longValue();
		storage.updateJsapiTicket(key, type, ticket, expires);
		return ticket;
	}

	private OapiGetJsapiTicketResponse fetchResponse(String accessToken) throws ApiException {
		OapiGetJsapiTicketRequest req = new OapiGetJsapiTicketRequest();
		req.setTopHttpMethod(METHOD_GET);
		return client.execute(req, accessToken);
	}

	private void validate(OapiGetJsapiTicketResponse response) {
		try {
			Long code = response.getErrcode();
			String msg = response.getErrmsg();
			DingTalkResponseValidator.requireSuccess(response, code, msg);
		} catch (DingTalkApiException ex) {
			throw new DingTalkRuntimeException(ex.getMessage(), ex);
		}
	}

	private static String cacheKey(String corpId, String appKey) {
		return (corpId == null ? "" : corpId) + ':' + (appKey == null ? "" : appKey);
	}
}
