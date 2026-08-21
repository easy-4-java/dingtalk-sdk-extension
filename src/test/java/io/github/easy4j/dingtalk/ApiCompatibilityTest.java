package io.github.easy4j.dingtalk;

import io.github.easy4j.dingtalk.model.message.BaseMessage;
import io.github.easy4j.dingtalk.service.DingTalkTemplate;
import io.github.easy4j.dingtalk.service.DingTalkRobotService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ApiCompatibilityTest {

    @Test
    void publicCompatibilityMethodsRemainAvailable() throws Exception {
        assertNotNull(DingTalkTemplate.class.getMethod("opsForAccount"));
        assertNotNull(DingTalkTemplate.class.getMethod("opsForSns"));
        assertNotNull(DingTalkTemplate.class.getMethod("opsForSso"));
        assertNotNull(DingTalkTemplate.class.getMethod("opsForJsapi"));
        assertNotNull(DingTalkTemplate.class.getMethod("opsForRobot"));
        assertNotNull(DingTalkTemplate.class.getMethod("opsForUser"));
    }

    @Test
    void robotOperationsRetainsBothBuildRequestSpellings() throws Exception {
        Method legacy = DingTalkRobotService.class.getMethod("buidRequest", BaseMessage.class);
        Method current = DingTalkRobotService.class.getMethod("buildRequest", BaseMessage.class);
        assertNotNull(legacy);
        assertNotNull(current);
    }
}
