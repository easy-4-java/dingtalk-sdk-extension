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
package io.github.easy4j.dingtalk.model.jsapi;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the signature data required for DingTalk JSAPI calls.
 * <p>This bean holds the agent ID, target URL, nonce string, timestamp,
 * corporate ID, and the computed signature used to authenticate JSAPI requests.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see io.github.easy4j.dingtalk.service.DingTalkJsapiService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsapiTicketSignature implements Serializable {

	private static final long serialVersionUID = 7442708886169868689L;
	
	/**
	 * 应用的标识
	 */
	private String agentId;
	/**
	 * 当前网页的URL，不包含#及其后面部分
	 */
	private String url;
	/**
	 * 随机串，自己定义
	 */
	private String nonceStr;
	/**
	 * 时间戳：当前时间，但是前端和服务端进行校验时候的值要一致
	 */
	private long timestamp;
	/**
	 * 企业ID，在开发者后台中企业视图下开发者账号设置里面可以看到
	 */
	private String corpId;
	/**
	 * 签名信息
	 */
	private String signature;

}
