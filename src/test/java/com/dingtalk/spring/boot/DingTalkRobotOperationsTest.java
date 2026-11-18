package com.dingtalk.spring.boot;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import com.dingtalk.spring.boot.bean.*;
import com.dingtalk.spring.boot.config.*;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkRobotOperationsTest {

    private DingTalkRobotOperations createOperations() {
        DingTalkRobotConfig robot = new DingTalkRobotConfig();
        robot.setRobotId("robot1");
        robot.setAccessToken("token1");
        robot.setSecretToken("SECsecret1");

        DingTalkConfig props = new DingTalkConfig();
        props.setCorpId("corp1");
        props.setRobots(Collections.singletonList(robot));

        DefaultDingTalkConfigProvider configProvider = new DefaultDingTalkConfigProvider(props);
        configProvider.init();

        DingTalkAccessTokenProvider tokenProvider = new DingTalkAccessTokenProvider() {
            @Override
            public String getAccessToken(String corpId, String appKey) { return "token"; }
            @Override
            public String getSnsAccessToken(String corpId, String appId) { return "sns_token"; }
        };

        DingTalkTemplate template = new DingTalkTemplate(configProvider, tokenProvider);
        return new DingTalkRobotOperations(template);
    }

    @Test
    void shouldBuildRequestForTextMessage() {
        DingTalkRobotOperations ops = createOperations();
        TextMessage msg = new TextMessage("hello");
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("text", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForTextMessageWithAtMobiles() {
        DingTalkRobotOperations ops = createOperations();
        TextMessage msg = new TextMessage("hello", new String[]{"13800138000"});
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("text", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForTextMessageWithAtAll() {
        DingTalkRobotOperations ops = createOperations();
        TextMessage msg = new TextMessage("hello", true);
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("text", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForLinkMessage() {
        DingTalkRobotOperations ops = createOperations();
        LinkMessage msg = new LinkMessage("title", "text", "http://url.com");
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("link", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForMarkdownMessage() {
        DingTalkRobotOperations ops = createOperations();
        MarkdownMessage msg = new MarkdownMessage("title", "# heading");
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("markdown", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForActionCardMessage() {
        DingTalkRobotOperations ops = createOperations();
        ActionCardMessage msg = new ActionCardMessage("title", "text");
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("actionCard", req.getMsgtype());
    }

    @Test
    void shouldBuildRequestForFeedCardMessage() {
        DingTalkRobotOperations ops = createOperations();
        FeedCardMessage msg = new FeedCardMessage();
        OapiRobotSendRequest req = ops.buidRequest(msg);
        assertNotNull(req);
        assertEquals("feedCard", req.getMsgtype());
    }

    @Test
    void shouldGetWebhook() {
        DingTalkRobotOperations ops = createOperations();
        String webhook = ops.getWebhook("corp1", "robot1", 1234567890L);
        assertNotNull(webhook);
        assertTrue(webhook.contains("/robot/send?access_token=token1"));
        assertTrue(webhook.contains("timestamp=1234567890"));
        assertTrue(webhook.contains("sign="));
    }

    @Test
    void sendsSignedTextMessageWithMentionsAndParsesWebhookResponse() throws Exception {
        AtomicReference<String> query = new AtomicReference<>();
        AtomicReference<JsonNode> body = new AtomicReference<>();
        ObjectMapper mapper = new ObjectMapper();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/robot/send", exchange -> {
            query.set(exchange.getRequestURI().getRawQuery());
            body.set(mapper.readTree(exchange.getRequestBody()));
            byte[] reply = "{\"errcode\":0,\"errmsg\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, reply.length);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(reply);
            }
        });
        server.start();
        try {
            String webhook = "http://127.0.0.1:" + server.getAddress().getPort()
                + "/robot/send?access_token=test-token";
            TextMessage message = new TextMessage("hello", new String[]{"13800138000"});
            OapiRobotSendResponse response = createOperations().sendMessageByUrl(
                webhook, "test-signing-secret", message);

            assertEquals(Long.valueOf(0), response.getErrcode());
            assertTrue(query.get().contains("access_token=test-token"));
            assertTrue(query.get().matches(".*&timestamp=[0-9]+.*"));
            assertTrue(query.get().matches(".*&sign=[^&]+.*"));
            assertEquals("text", body.get().path("msgtype").asText());
            assertEquals("hello", body.get().path("text").path("content").asText());
            assertEquals("13800138000", body.get().path("at").path("atMobiles").path(0).asText());
            assertFalse(body.get().path("at").path("isAtAll").asBoolean(true));
        } finally {
            server.stop(0);
        }
    }
}
