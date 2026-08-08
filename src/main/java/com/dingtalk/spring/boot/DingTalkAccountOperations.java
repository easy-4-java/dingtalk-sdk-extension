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
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetUseridByUnionidRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetUseridByUnionidResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

/**
 * Operations for DingTalk enterprise internal application login-free access
 * and user management.
 * <p>Provides methods to retrieve user info by authorization code,
 * resolve user IDs from union IDs, and fetch detailed user profiles.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see DingTalkOperations
 * @see DingTalkTemplate#opsForAccount()
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/clotub">Enterprise internal application login-free</a>
 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/ege851">User management</a>
 */
@Slf4j
public class DingTalkAccountOperations extends DingTalkOperations {

	/**
	 * Constructs account operations with the given template.
	 *
	 * @param template the DingTalk template; must not be {@code null}
	 */
	public DingTalkAccountOperations(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * Retrieves user information by login-free authorization code.
	 * <p>The authorization code is obtained from the DingTalk client SDK
	 * and can only be used once.</p>
	 *
	 * @param code         the login-free authorization code
	 * @param accessToken  the access token for API calls
	 * @return the user info response
	 * @throws ApiException if the API request fails
	 * @see <a href="https://ding-doc.dingtalk.com/doc#/serverapi2/clotub">Login-free documentation</a>
	 */
	public OapiUserGetuserinfoResponse getUserinfoBycode( String code, String accessToken) throws ApiException {
		DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/user/getuserinfo");
		OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
		request.setCode(code);
		request.setHttpMethod(METHOD_GET);
		return client.execute(request, accessToken);
	}

	/**
	 * Retrieves the DingTalk user ID corresponding to a union ID.
	 *
	 * @param unionid      the union ID of the employee
	 * @param accessToken  the access token for API calls
	 * @return the user ID response
	 * @throws ApiException if the API request fails
	 * @see <a href="https://open-doc.dingtalk.com/microapp/serverapi2/ege851#-5">Get userid by unionid</a>
	 */
	public OapiUserGetUseridByUnionidResponse getUseridByUnionid( String unionid, String accessToken) throws ApiException {

		DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/user/getUseridByUnionid");
		OapiUserGetUseridByUnionidRequest request = new OapiUserGetUseridByUnionidRequest();
		request.setUnionid(unionid);
		request.setHttpMethod(METHOD_GET);

		return client.execute(request, accessToken);
	}

	/**
	 * Retrieves detailed user information (including mobile number, department ID, etc.)
	 * by DingTalk user ID.
	 *
	 * @param userid       the DingTalk user ID (staffId)
	 * @param accessToken  the access token for API calls
	 * @return the user detail response
	 * @throws ApiException if the API request fails
	 * @see <a href="https://open-doc.dingtalk.com/microapp/serverapi2/ege851">User management</a>
	 */
	public OapiUserGetResponse getUserByUserid( String userid, String accessToken) throws ApiException {

		DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/user/get");
		OapiUserGetRequest request = new OapiUserGetRequest();
		request.setUserid(userid);
		request.setHttpMethod(METHOD_GET);

		return client.execute(request, accessToken);
	}

}
