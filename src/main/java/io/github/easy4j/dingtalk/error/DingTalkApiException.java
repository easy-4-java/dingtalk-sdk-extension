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

import lombok.Getter;

/**
 * Checked exception raised when a DingTalk Open API call returns an error payload
 * ({@code errcode != 0}) and the callee must be handled explicitly by callers.
 * <p>For unchecked scenarios prefer {@link DingTalkRuntimeException}.</p>
 *
 * <h3>Usage pattern</h3>
 * <pre>{@code
 *  try {
 *      template.opsForRobot().sendMessage(...);
 *  } catch (DingTalkApiException ex) {
 *      DingTalkError err = ex.getError(); // structured info
 *      logger.warn("dingtalk failed errcode={}", err.getErrorCode(), ex);
 *  }
 * }</pre>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkError
 * @see DingTalkRuntimeException
 */
@Getter
public class DingTalkApiException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Structured error returned by DingTalk. Never {@code null} after construction.
     */
    private final DingTalkError error;

    public DingTalkApiException(DingTalkError error) {
        super(buildMessage(error));
        this.error = error;
    }

    public DingTalkApiException(DingTalkError error, Throwable cause) {
        super(buildMessage(error), cause);
        this.error = error;
    }

    /**
     * Convenience constructor without a structured {@link DingTalkError}; only for
     * call sites that do not yet have the parsed body.
     *
     * @param message human readable text
     */
    public DingTalkApiException(String message) {
        super(message);
        this.error = DingTalkError.builder().errorMsg(message).build();
    }

    private static String buildMessage(DingTalkError error) {
        if (error == null) {
            return "(empty dingtalk error)";
        }
        ErrorCode known = error.getErrorCodeEnum();
        StringBuilder sb = new StringBuilder(80);
        sb.append("DingTalk API error: errcode=").append(error.getErrorCode());
        if (known != null) {
            sb.append(" [").append(known.name()).append(']');
        }
        sb.append(", errmsg=").append(error.getErrorMsg());
        if (known != null && known.getDeveloperHint() != null &&
                !known.getDeveloperHint().equalsIgnoreCase(error.getErrorMsg())) {
            sb.append(" (").append(known.getDeveloperHint()).append(')');
        }
        return sb.toString();
    }

    /**
     * Numeric error code shortcut.
     *
     * @return the code or {@code -1} if absent
     */
    public int getErrorCode() {
        return error == null || error.getErrorCode() == null ? -1 : error.getErrorCode();
    }
}
