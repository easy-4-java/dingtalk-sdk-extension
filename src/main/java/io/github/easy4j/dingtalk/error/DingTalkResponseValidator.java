package io.github.easy4j.dingtalk.error;

import com.taobao.api.TaobaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Slf4j
public final class DingTalkResponseValidator {

    private DingTalkResponseValidator() {
    }

    public static <T extends TaobaoResponse> T requireSuccess(
            T response, Long errcode, String errmsg) throws DingTalkApiException {
        if (Objects.isNull(response)) {
            throw new DingTalkApiException("DingTalk API returned null response");
        }
        long resolvedCode;
        if (Objects.nonNull(errcode)) {
            resolvedCode = errcode.longValue();
        } else {
            String transportCode = StringUtils.defaultIfBlank(
                    response.getSubCode(), response.getErrorCode());
            resolvedCode = parseCode(transportCode);
        }
        if (!response.isSuccess() || resolvedCode != 0L) {
            Integer codeBoxed;
            try {
                codeBoxed = Integer.valueOf(Math.toIntExact(resolvedCode));
            } catch (ArithmeticException ex) {
                codeBoxed = Integer.valueOf(-1);
            }
            String resolvedMsg = StringUtils.defaultIfBlank(errmsg,
                    StringUtils.defaultIfBlank(response.getSubMsg(), response.getMsg()));
            DingTalkError error = DingTalkError.builder()
                    .errorCode(codeBoxed)
                    .errorMsg(resolvedMsg)
                    .body(response.getBody())
                    .build();
            if (log.isDebugEnabled()) {
                log.debug("DingTalk API failed errcode={} errmsg={} requestId={}",
                        error.getErrorCode(), resolvedMsg, response.getRequestId());
            }
            throw new DingTalkApiException(error);
        }
        return response;
    }

    static long parseCode(String raw) {
        if (StringUtils.isBlank(raw)) {
            return -1L;
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }
}
