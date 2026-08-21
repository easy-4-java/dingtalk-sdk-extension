package io.github.easy4j.dingtalk.service;

import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import io.github.easy4j.dingtalk.config.*; import io.github.easy4j.dingtalk.config.impl.*;
import io.github.easy4j.dingtalk.error.DingTalkApiException;
import io.github.easy4j.dingtalk.internal.DingTalkClientFactory;
import io.github.easy4j.dingtalk.service.DingTalkService;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoRequest;
import com.taobao.api.TaobaoResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkUserServiceTest {

    private DingTalkUserService createOperations() {
        DingTalkConfig props = new DingTalkConfig();
        props.setCorpId("corp1");
        DefaultDingTalkConfigProvider configProvider = new DefaultDingTalkConfigProvider(props);
        DingTalkAccessTokenProvider tokenProvider = new DingTalkAccessTokenProvider() {
            @Override
            public String getAccessToken(String corpId, String appKey) { return "token"; }
            @Override
            public String getSnsAccessToken(String corpId, String appId) { return "sns_token"; }
        };
        DingTalkTemplate template = new DingTalkTemplate(configProvider, tokenProvider);
        return new DingTalkUserService(template);
    }

    @Test
    void shouldCreateInstance() {
        DingTalkUserService ops = createOperations();
        assertNotNull(ops);
    }

    @Test
    void shouldRaiseErrorExceptionForLogicalFailure() {
        List<String> capturedEndpoints = new ArrayList<>();
        DingTalkClientFactory factory = endpoint -> {
            capturedEndpoints.add(endpoint);
            return new DingTalkClient() {
                @Override public <T extends TaobaoResponse> T execute(TaobaoRequest<T> request)
                        throws ApiException { return null; }
                @Override public <T extends TaobaoResponse> T execute(TaobaoRequest<T> request, String session)
                        throws ApiException {
                    OapiUserGetuserinfoResponse resp = new OapiUserGetuserinfoResponse();
                    resp.setErrcode(40014L);
                    resp.setErrmsg("token expired");
                    return (T) resp;
                }
                @Override public <T extends TaobaoResponse> T execute(TaobaoRequest<T> req, String s, String s2)
                        throws ApiException { return null; }
                @Override public <T extends TaobaoResponse> T execute(TaobaoRequest<T> req, String s, String s2, String s3)
                        throws ApiException { return null; }
                @Override public <T extends TaobaoResponse> T execute(TaobaoRequest<T> req, String s, String s2, String s3, String s4)
                        throws ApiException { return null; }
            };
        };
        DingTalkConfig props = new DingTalkConfig();
        DefaultDingTalkConfigProvider cfg = new DefaultDingTalkConfigProvider(props);
        DingTalkAccessTokenProvider tok = new DingTalkAccessTokenProvider() {
            @Override public String getAccessToken(String a, String b) { return "x"; }
            @Override public String getSnsAccessToken(String a, String b) { return "x"; }
        };
        DingTalkService svc = new DingTalkTemplate(cfg, tok);
        DingTalkUserService ops = new DingTalkUserService(svc, factory);
        DingTalkApiException ex = assertThrows(DingTalkApiException.class,
                () -> ops.getUserInfoByCode("c", "token"));
        assertEquals(Integer.valueOf(40014), ex.getError().getErrorCode());
        assertEquals(1, capturedEndpoints.size());
        assertEquals("https://oapi.dingtalk.com/user/getuserinfo", capturedEndpoints.get(0));
    }
}
