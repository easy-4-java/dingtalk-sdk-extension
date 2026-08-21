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

import com.taobao.api.ApiException;
import io.github.easy4j.dingtalk.service.DingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.service.DingTalkAccountService;
import io.github.easy4j.dingtalk.config.DingTalkConfigProvider;
import io.github.easy4j.dingtalk.service.DingTalkJsapiService;
import io.github.easy4j.dingtalk.service.DingTalkRobotService;
import io.github.easy4j.dingtalk.service.DingTalkSnsService;
import io.github.easy4j.dingtalk.service.DingTalkSsoService;
import io.github.easy4j.dingtalk.service.DingTalkUserService;
import io.github.easy4j.dingtalk.storage.DingTalkConfigStorage;

/**
 * Aggregated service facade that composes the six DingTalk operation
 * domains (account, SNS, SSO, JSAPI, robot, and user) into a single
 * contract.
 * <p>This mirrors the composite-service pattern used by the WxJava SDK
 * family: {@code WxService} unifies every {@code *Service} sub-service,
 * and every sub-operation carries a back-reference to the enclosing
 * service instance so cross-domain calls remain cheap.</p>
 *
 * <h3>Backreference contract</h3>
 * Every concrete {@code DingTalk*Operations} subclass accepts a
 * {@code DingTalkService} in its constructor and defers all token,
 * signature, and configuration lookups to the service.  This keeps
 * the operations stateless and testable in isolation.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public interface DingTalkService {

    DingTalkAccountService opsForAccount();

    DingTalkSnsService opsForSns();

    DingTalkSsoService opsForSso();

    DingTalkJsapiService opsForJsapi();

    DingTalkRobotService opsForRobot();

    DingTalkUserService opsForUser();

    String getCorpId(String appKey);

    String getCorpSecret(String corpId);

    String getAppSecret(String corpId, String appKey);

    boolean hasAppKey(String appKey);

    String getAccessToken(String corpId, String appKey) throws ApiException;

    String getSnsAccessToken(String corpId, String appId) throws ApiException;

    String getSign(String secret, Long timestamp);

    DingTalkConfigProvider getDingTalkConfigProvider();

    DingTalkAccessTokenProvider getDingTalkAccessTokenProvider();

    DingTalkConfigStorage getConfigStorage();
}
