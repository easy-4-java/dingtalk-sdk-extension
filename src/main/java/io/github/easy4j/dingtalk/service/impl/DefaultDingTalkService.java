package io.github.easy4j.dingtalk.service.impl;

import io.github.easy4j.dingtalk.service.DingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.service.DingTalkAccountService;
import io.github.easy4j.dingtalk.config.DingTalkConfigProvider;
import io.github.easy4j.dingtalk.service.DingTalkJsapiService;
import io.github.easy4j.dingtalk.service.DingTalkRobotService;
import io.github.easy4j.dingtalk.service.DingTalkSnsService;
import io.github.easy4j.dingtalk.service.DingTalkSsoService;
import io.github.easy4j.dingtalk.service.DingTalkUserService;
import io.github.easy4j.dingtalk.service.DingTalkService;
import io.github.easy4j.dingtalk.storage.DingTalkConfigStorage;
import com.taobao.api.ApiException;

import java.net.URLEncoder;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultDingTalkService implements DingTalkService {

    protected final DingTalkConfigProvider configProvider;
    protected final DingTalkAccessTokenProvider accessTokenProvider;

    protected final DingTalkAccountService accountOps;
    protected final DingTalkSnsService snsOps;
    protected final DingTalkSsoService ssoOps;
    protected final DingTalkJsapiService jsapiOps;
    protected final DingTalkRobotService robotOps;
    protected final DingTalkUserService userOps;

    public DefaultDingTalkService(
            DingTalkConfigProvider configProvider,
            DingTalkAccessTokenProvider accessTokenProvider) {
        this.configProvider = configProvider;
        this.accessTokenProvider = accessTokenProvider;
        this.accountOps = new DingTalkAccountService(this);
        this.snsOps = new DingTalkSnsService(this);
        this.ssoOps = new DingTalkSsoService(this);
        this.jsapiOps = new DingTalkJsapiService(this);
        this.robotOps = new DingTalkRobotService(this);
        this.userOps = new DingTalkUserService(this);
    }

    @Override
    public DingTalkAccountService opsForAccount() {
        return accountOps;
    }

    @Override
    public DingTalkSnsService opsForSns() {
        return snsOps;
    }

    @Override
    public DingTalkSsoService opsForSso() {
        return ssoOps;
    }

    @Override
    public DingTalkJsapiService opsForJsapi() {
        return jsapiOps;
    }

    @Override
    public DingTalkRobotService opsForRobot() {
        return robotOps;
    }

    @Override
    public DingTalkUserService opsForUser() {
        return userOps;
    }

    @Override
    public String getCorpId(String appKey) {
        return configProvider.getCorpId(appKey);
    }

    @Override
    public String getCorpSecret(String corpId) {
        return configProvider.getCorpSecret(corpId);
    }

    @Override
    public String getAppSecret(String corpId, String appKey) {
        return configProvider.getAppSecret(corpId, appKey);
    }

    @Override
    public boolean hasAppKey(String appKey) {
        return configProvider.hasAppKey(appKey);
    }

    @Override
    public String getAccessToken(String corpId, String appKey) throws ApiException {
        return accessTokenProvider.getAccessToken(corpId, appKey);
    }

    @Override
    public String getSnsAccessToken(String corpId, String appId) throws ApiException {
        return accessTokenProvider.getSnsAccessToken(corpId, appId);
    }

    @Override
    public String getSign(String secret, Long timestamp) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.getEncoder().encode(signData)), "UTF-8");
            log.debug("Signature computed: sign = {}", sign);
            return sign;
        } catch (Exception e) {
            log.error("Failed to compute signature: errMsg = {}", e);
            return null;
        }
    }

    @Override
    public DingTalkConfigProvider getDingTalkConfigProvider() {
        return configProvider;
    }

    @Override
    public DingTalkAccessTokenProvider getDingTalkAccessTokenProvider() {
        return accessTokenProvider;
    }

    @Override
    public DingTalkConfigStorage getConfigStorage() {
        return accessTokenProvider == null ? null : accessTokenProvider.getStorage();
    }
}
