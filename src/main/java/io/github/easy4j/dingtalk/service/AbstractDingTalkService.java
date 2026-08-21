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

import io.github.easy4j.dingtalk.error.DingTalkError;
import io.github.easy4j.dingtalk.error.DingTalkApiException;
import io.github.easy4j.dingtalk.error.DingTalkResponseValidator;
import io.github.easy4j.dingtalk.service.DingTalkService;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for all DingTalk operation classes.
 * <p>Every concrete {@code DingTalk*Operations} subclass holds a back-reference
 * to the aggregated {@link DingTalkService} that owns it; token, configuration,
 * and cross-domain lookups are all routed through the service instance.  This
 * mirrors the WxJava pattern ({@code WxService} composes {@code *ServiceImpl}
 * sub-services) and keeps the operations layer stateless.</p>
 *
 * <p>All callers that invoke a DingTalk Open API response should use
 * {@link #executeChecked(com.taobao.api.TaobaoResponse)} after the raw
 * {@code client.execute(...)} call to convert non-zero {@code errcode}
 * replies into {@link DingTalkApiException} (checked) — failures otherwise
 * silently return an object that looks successful until {@code getXxx()} is
 * called.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkService
 * @see DingTalkAccountService
 * @see DingTalkSnsService
 * @see DingTalkSsoService
 * @see DingTalkJsapiService
 * @see DingTalkRobotService
 * @see DingTalkUserService
 */
@Slf4j
public abstract class AbstractDingTalkService {

	public static final String PREFIX = "https://oapi.dingtalk.com";
	public static final String METHOD_GET = "GET";
	public static final String APPLICATION_JSON_VALUE = "application/json";
	public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
	public static final String DELIMITER = "&";
	public static final String SEPARATOR = "=";

	protected final DingTalkService service;

	public AbstractDingTalkService(DingTalkService service) {
		this.service = service;
	}

	@Deprecated
	public AbstractDingTalkService(DingTalkTemplate template) {
		this((DingTalkService) template);
	}

	protected final <T extends TaobaoResponse> T executeChecked(
			T response, Long errcode, String errmsg) throws DingTalkApiException {
		return DingTalkResponseValidator.requireSuccess(response, errcode, errmsg);
	}

	protected DingTalkApiException wrap(String msg, Throwable cause) {
		DingTalkError err = DingTalkError.builder()
				.errorCode(Integer.valueOf(-1))
				.errorMsg(msg)
				.build();
		return new DingTalkApiException(err, cause);
	}
}
