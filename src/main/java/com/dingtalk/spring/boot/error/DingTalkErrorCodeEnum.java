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
package com.dingtalk.spring.boot.error;

import lombok.Getter;

/**
 * Common DingTalk Open API error codes.
 * <p>Only the most frequently encountered codes are enumerated here;
 * the full list is maintained by DingTalk and may grow over time.
 * Consumers should treat unknown codes gracefully.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
public enum DingTalkErrorCodeEnum {

    SYSTEM_BUSY(-1, "系统繁忙", "System busy, please retry later."),
    SUCCESS(0, "请求成功", "Success."),
    ACCESS_TOKEN_ILLEGAL(40001, "不合法的 access_token", "Invalid access token."),
    ACCESS_TOKEN_MISSING(41001, "缺少 access_token 参数", "Missing access_token parameter."),
    ACCESS_TOKEN_EXPIRED(42001, "access_token 已过期", "Access token has expired."),
    ACCESS_TOKEN_INVALID(40014, "不合法的 access_token 或已失效", "Invalid or stale access token."),
    REQUEST_FREQUENT(42007, "用户请求过于频繁", "Request frequency limit exceeded."),
    PERMISSION_DENIED(50001, "权限不足", "Permission denied for the requested scope."),
    ROBOT_TOKEN_INVALID(33001, "机器人 Webhook Token 无效", "Invalid robot webhook access token."),
    ROBOT_SECRET_MISMATCH(310000, "机器人签名校验失败", "Robot signature mismatch, check the signing secret."),
    SECRET_NOT_BOUND(90001, "Secret 未绑定套件", "Secret is not bound to the current suite."),
    CORP_ID_INVALID(300001, "不合法的 CorpId", "Invalid CorpId."),
    PARAM_INVALID(300002, "参数无效", "Invalid parameter(s)."),
    USER_NOT_FOUND(300003, "用户不存在", "Specified user does not exist.");

    /**
     * DingTalk API numeric error code (errcode field).
     */
    private final int code;

    /**
     * Chinese description from DingTalk's official documentation.
     */
    private final String description;

    /**
     * English description intended for developers logging on international systems.
     */
    private final String developerHint;

    DingTalkErrorCodeEnum(int code, String description, String developerHint) {
        this.code = code;
        this.description = description;
        this.developerHint = developerHint;
    }

    /**
     * Looks up an enum by numeric code.
     *
     * @param code the DingTalk numeric error code
     * @return the matching enum, or {@code null} when the code is not enumerated
     */
    public static DingTalkErrorCodeEnum fromCode(int code) {
        for (DingTalkErrorCodeEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }

    /**
     * @return text form {@code [code] description (hint)}
     */
    @Override
    public String toString() {
        return "[" + code + "] " + description + " (" + developerHint + ")";
    }
}
