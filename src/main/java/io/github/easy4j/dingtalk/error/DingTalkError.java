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
package io.github.easy4j.dingtalk.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Standardised representation of a DingTalk Open API error response.
 * <p>This class wraps the three fields that every DingTalk JSON reply carries
 * ({@code errcode}, {@code errmsg}, and an optional raw {@code body}) and
 * optionally resolves the numeric code to a {@link ErrorCode}.</p>
 *
 * <h3>Typical lifecycle</h3>
 * <ol>
 *     <li>An HTTP call to DingTalk completes.</li>
 *     <li>The JSON body is parsed into {@code errcode}/{@code errmsg}.</li>
 *     <li>A {@link DingTalkError} is constructed; when {@code errcode != 0}
 *         callers will typically wrap it inside a {@link DingTalkApiException}
 *         (checked) or {@link DingTalkRuntimeException} (unchecked).</li>
 * </ol>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see ErrorCode
 * @see DingTalkApiException
 * @see DingTalkRuntimeException
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DingTalkError implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Numeric DingTalk error code ({@code errcode}).
     * {@code 0} conventionally means "success".
     */
    private Integer errorCode;

    /**
     * Human readable error message from DingTalk ({@code errmsg}).
     */
    private String errorMsg;

    /**
     * Optional raw body of the response, useful for post-mortem debugging
     * when the JSON structure contains additional unmodelled fields.
     */
    private String body;

    /**
     * Resolves {@link #errorCode} to a known enumeration entry, or
     * {@code null} when the code is not yet listed in {@link ErrorCode}.
     *
     * @return the enum entry, or {@code null}
     */
    public ErrorCode getErrorCodeEnum() {
        if (errorCode == null) {
            return null;
        }
        return ErrorCode.fromCode(errorCode);
    }

    /**
     * @return {@code true} when {@code errorCode != null && errorCode == 0}
     */
    public boolean isSuccess() {
        return errorCode != null && errorCode.intValue() == 0;
    }

    /**
     * Shortcut factory for ad-hoc success replies.
     *
     * @return a {@link #isSuccess()} == true instance
     */
    public static DingTalkError success() {
        return DingTalkError.builder()
                .errorCode(0)
                .errorMsg("ok")
                .build();
    }

    /**
     * Shortcut factory for wrapping a numeric code + message without JSON body.
     *
     * @param code    DingTalk {@code errcode}
     * @param message DingTalk {@code errmsg}
     * @return new error
     */
    public static DingTalkError of(int code, String message) {
        return DingTalkError.builder()
                .errorCode(code)
                .errorMsg(message)
                .build();
    }
}
