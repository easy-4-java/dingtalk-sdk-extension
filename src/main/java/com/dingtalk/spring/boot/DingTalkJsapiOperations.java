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

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.dingtalk.spring.boot.bean.JsapiTicketSignature;
import com.dingtalk.spring.boot.utils.DingTalkUtils;
import com.dingtalk.spring.boot.utils.RandomUtils;
import com.taobao.api.ApiException;

/**
 * Operations for DingTalk JSAPI ticket retrieval and signature creation.
 * <p>Used to obtain JSAPI tickets and compute signatures required for
 * front-end DingTalk JSAPI calls.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkOperations
 * @see DingTalkTemplate#opsForJsapi()
 * @see <a href="https://ding-doc.dingtalk.com/doc#/dev/uwa7vs">JSAPI signature documentation</a>
 */
public class DingTalkJsapiOperations extends DingTalkOperations {

	DefaultDingTalkClient client = new DefaultDingTalkClient(PREFIX + "/get_jsapi_ticket");

	/**
	 * Constructs JSAPI operations with the given template.
	 *
	 * @param template the DingTalk template; must not be {@code null}
	 */
	public DingTalkJsapiOperations(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * Retrieves a JSAPI ticket (without forced refresh).
	 *
	 * @param type         the ticket type
	 * @param accessToken  the access token for API calls
	 * @return the JSAPI ticket response
	 * @throws ApiException if the API request fails
	 */
	public OapiGetJsapiTicketResponse getTicket(TicketType type, String accessToken) throws ApiException {
		OapiGetJsapiTicketRequest req = new OapiGetJsapiTicketRequest();
		req.setTopHttpMethod(METHOD_GET);
		return client.execute(req, accessToken);
	}

	/**
	 * Creates a JSAPI signature for the given URL and agent.
	 * <p>The signature is computed using the JSAPI ticket, a random nonce,
	 * and the current timestamp.</p>
	 *
	 * @param url          the current page URL (without hash fragment)
	 * @param agentId      the application agent ID
	 * @param accessToken  the access token for API calls
	 * @return the computed {@link JsapiTicketSignature}
	 * @throws ApiException if the API request fails
	 * @see DingTalkUtils#sign(String, String, long, String)
	 */
	public JsapiTicketSignature createSignature(String url, String agentId, String accessToken)
			throws ApiException {

		long timestamp = System.currentTimeMillis() / 1000;
		String randomStr = RandomUtils.getRandomStr();
		OapiGetJsapiTicketResponse jsapiTicket = getTicket(TicketType.JSAPI, accessToken);
		String signature = DingTalkUtils.sign(jsapiTicket.getTicket(), randomStr, timestamp, url);
		JsapiTicketSignature jsapiSignature = new JsapiTicketSignature();
		jsapiSignature.setAgentId(agentId);
		jsapiSignature.setTimestamp(timestamp);
		jsapiSignature.setNonceStr(randomStr);
		jsapiSignature.setUrl(url);
		jsapiSignature.setSignature(signature);
		return jsapiSignature;

	}

}
