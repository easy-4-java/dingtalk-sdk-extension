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

/**
 * Abstract base class for all DingTalk operation classes.
 * <p>Provides common constants (API prefix, HTTP methods, content types,
 * delimiters) and holds a reference to the {@link DingTalkTemplate} that
 * manages configuration and access tokens.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see DingTalkTemplate
 * @see DingTalkAccountOperations
 * @see DingTalkSnsOperations
 * @see DingTalkSsoOperations
 * @see DingTalkJsapiOperations
 * @see DingTalkRobotOperations
 * @see DingTalkUserOperations
 */
public abstract class DingTalkOperations {

	/** DingTalk Open API base URL. */
	public static final String PREFIX = "https://oapi.dingtalk.com";

	/** HTTP GET method identifier. */
	public static final String METHOD_GET = "GET";

	/** JSON content type. */
	public static final String APPLICATION_JSON_VALUE = "application/json";

	/** JSON content type with UTF-8 charset. */
	public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

	/** URL parameter delimiter. */
	public static final String DELIMITER = "&";

	/** URL key-value separator. */
	public static final String SEPARATOR = "=";

	/** The template used for configuration and token management. */
	protected DingTalkTemplate template;

	/**
	 * Constructs operations with the given template.
	 *
	 * @param template the DingTalk template; must not be {@code null}
	 */
	public DingTalkOperations(DingTalkTemplate template) {
		this.template = template;
	}

}
