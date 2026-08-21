package io.github.easy4j.dingtalk.service;

import com.dingtalk.api.request.OapiRobotSendRequest;
import io.github.easy4j.dingtalk.model.message.*; import io.github.easy4j.dingtalk.model.jsapi.*;
import io.github.easy4j.dingtalk.config.*; import io.github.easy4j.dingtalk.config.impl.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkRobotServiceTest {

    private DingTalkRobotService createOperations() {
        DingTalkRobotConfig robot = new DingTalkRobotConfig();
        robot.setRobotId("robot1");
        robot.setAccessToken("token1");
        robot.setSecretToken("SECsecret1");

        DingTalkConfig props = new DingTalkConfig();
        props.setCorpId("corp1");
        props.setRobots(List.of(robot));

        DefaultDingTalkConfigProvider configProvider = new DefaultDingTalkConfigProvider(props);
        configProvider.init();

        DingTalkAccessTokenProvider tokenProvider = new DingTalkAccessTokenProvider() {
            @Override
            public String getAccessToken(String corpId, String appKey) { return "token"; }
            @Override
            public String getSnsAccessToken(String corpId, String appId) { return "sns_token"; }
        };

        DingTalkTemplate template = new DingTalkTemplate(configProvider, tokenProvider);
        return new DingTalkRobotService(template);
    }

    @Test
    void shouldBuildRequestForTextMessage() {
        DingTalkRobotService ops = createOperations();
        TextMessage msg = new TextMessage("hello");
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("text", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForTextMessageWithAtMobiles() {
        DingTalkRobotService ops = createOperations();
        TextMessage msg = new TextMessage("hello", new String[]{"13800138000"});
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("text", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForTextMessageWithAtAll() {
        DingTalkRobotService ops = createOperations();
        TextMessage msg = new TextMessage("hello", true);
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("text", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForLinkMessage() {
        DingTalkRobotService ops = createOperations();
        LinkMessage msg = new LinkMessage("title", "text", "http://url.com");
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("link", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForMarkdownMessage() {
        DingTalkRobotService ops = createOperations();
        MarkdownMessage msg = new MarkdownMessage("title", "# heading");
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("markdown", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForActionCardMessage() {
        DingTalkRobotService ops = createOperations();
        ActionCardMessage msg = new ActionCardMessage("title", "text");
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("actionCard", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForFeedCardMessage() {
        DingTalkRobotService ops = createOperations();
        FeedCardMessage msg = new FeedCardMessage();
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("feedCard", req.getMsgtype());
    }

    @Test
    void shouldGetWebhook() {
        DingTalkRobotService ops = createOperations();
        String webhook = ops.getWebhook("corp1", "robot1", 1234567890L);
        assertNotNull(webhook);
        assertTrue(webhook.contains("/robot/send?access_token=token1"));
        assertTrue(webhook.contains("timestamp=1234567890"));
        assertTrue(webhook.contains("sign="));
    }
}
