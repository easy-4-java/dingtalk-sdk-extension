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
 * Unchecked variant of {@link DingTalkApiException}.
 * <p>Prefer this exception type for low-level, operation-wide failures where
 * bubbling a checked exception through every layer would be unnecessarily
 * invasive; for strongly typed contract surfaces use {@link DingTalkApiException}.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkError
 * @see DingTalkApiException
 */
@Getter
public class DingTalkRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Structured DingTalk error (may be a synthetic instance if the failure
     * did not originate from the DingTalk HTTP layer).
     */
    private final DingTalkError error;

    public DingTalkRuntimeException(DingTalkError error) {
        super(defaultMessage(error));
        this.error = error;
    }

    public DingTalkRuntimeException(DingTalkError error, Throwable cause) {
        super(defaultMessage(error), cause);
        this.error = error;
    }

    public DingTalkRuntimeException(String message) {
        super(message);
        this.error = DingTalkError.builder().errorMsg(message).build();
    }

    public DingTalkRuntimeException(String message, Throwable cause) {
        super(message, cause);
        this.error = DingTalkError.builder().errorMsg(message).build();
    }

    private static String defaultMessage(DingTalkError error) {
        if (error == null) {
            return "(empty dingtalk runtime error)";
        }
        ErrorCode known = error.getErrorCodeEnum();
        if (known != null) {
            return "DingTalk runtime failure: " + known;
        }
        return "DingTalk runtime failure: errcode=" + error.getErrorCode() + ", errmsg=" + error.getErrorMsg();
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
