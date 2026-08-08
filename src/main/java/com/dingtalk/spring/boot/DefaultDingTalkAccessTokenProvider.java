package com.dingtalk.spring.boot;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiSnsGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiSnsGettokenResponse;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.StringUtils;

/**
 * Default implementation of {@link DingTalkAccessTokenProvider} that calls
 * the DingTalk Open API directly to obtain access tokens.
 * <p>Application secrets are resolved via the {@link DingTalkConfigProvider}.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see DingTalkAccessTokenProvider
 * @see DingTalkConfigProvider
 */
public class DefaultDingTalkAccessTokenProvider implements DingTalkAccessTokenProvider {

    private final String DINGTALK_SERVICE = "https://oapi.dingtalk.com";
    private final String METHOD_GET = "GET";
    private final DingTalkConfigProvider dingTalkConfigProvider;

    /**
     * Constructs a provider with the given configuration provider.
     *
     * @param dingTalkConfigProvider  the configuration provider for resolving app secrets; must not be {@code null}
     */
    public DefaultDingTalkAccessTokenProvider(DingTalkConfigProvider dingTalkConfigProvider) {
        this.dingTalkConfigProvider = dingTalkConfigProvider;
    }

    /** {@inheritDoc} */
    @Override
    public String getAccessToken(String corpId, String appKey) throws ApiException {

        OapiGettokenRequest request = new OapiGettokenRequest();

        String appSecret = dingTalkConfigProvider.getAppSecret(corpId, appKey);

        request.setAppkey(appKey);
        request.setAppsecret(appSecret);
        request.setHttpMethod(METHOD_GET);

        DingTalkClient client = new DefaultDingTalkClient(DINGTALK_SERVICE + "/gettoken");
        OapiGettokenResponse response = client.execute(request);

        if (response.isSuccess()) {
            return response.getAccessToken();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getSnsAccessToken(String corpId, String appId) throws ApiException {

        String appSecret = dingTalkConfigProvider.getAppSecret(corpId, appId);

        OapiSnsGettokenRequest request = new OapiSnsGettokenRequest();

        request.setAppid(appId);
        request.setAppsecret(appSecret);
        request.setHttpMethod(METHOD_GET);

        DingTalkClient client = new DefaultDingTalkClient(DINGTALK_SERVICE + "/sns/gettoken");

        OapiSnsGettokenResponse response = client.execute(request);

        if (response.isSuccess()) {
            return response.getAccessToken();
        }
        return StringUtils.EMPTY;

    }

}
