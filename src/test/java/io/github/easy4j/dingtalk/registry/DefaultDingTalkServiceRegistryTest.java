package io.github.easy4j.dingtalk.registry;

import io.github.easy4j.dingtalk.service.DingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.service.DingTalkService;
import io.github.easy4j.dingtalk.service.DingTalkTemplate;
import io.github.easy4j.dingtalk.config.DingTalkConfig;
import io.github.easy4j.dingtalk.config.impl.DefaultDingTalkConfigProvider;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDingTalkServiceRegistryTest {

    private DingTalkService service(String name) {
        DingTalkConfig props = new DingTalkConfig();
        props.setCorpId(name);
        DefaultDingTalkConfigProvider cfg = new DefaultDingTalkConfigProvider(props);
        DingTalkAccessTokenProvider tok = new DingTalkAccessTokenProvider() {
            @Override public String getAccessToken(String a, String b) { return "tok_" + name; }
            @Override public String getSnsAccessToken(String a, String b) { return "snstok_" + name; }
        };
        return new DingTalkTemplate(cfg, tok);
    }

    @Test
    void registerReturnsReplacedService() {
        DingTalkServiceRegistry reg = new DefaultDingTalkServiceRegistry();
        DingTalkService s1 = service("corp1");
        DingTalkService s2 = service("corp1-b");
        assertTrue(reg.register("corp1", s1).isEmpty());
        Optional<DingTalkService> prior = reg.register("corp1", s2);
        assertTrue(prior.isPresent());
        assertSame(s1, prior.get());
        assertSame(s2, reg.require("corp1"));
    }

    @Test
    void requireRaisesForUnknownTenant() {
        DingTalkServiceRegistry reg = new DefaultDingTalkServiceRegistry();
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> reg.require("nope"));
        assertTrue(ex.getMessage().contains("tenantId=nope"));
    }

    @Test
    void nullsRejected() {
        DingTalkServiceRegistry reg = new DefaultDingTalkServiceRegistry();
        DingTalkService svc = service("x");
        assertThrows(NullPointerException.class, () -> reg.register(null, svc));
        assertThrows(NullPointerException.class, () -> reg.register("corp", null));
        assertThrows(IllegalArgumentException.class, () -> reg.register("   ", svc));
    }

    @Test
    void removeAndFind() {
        DingTalkServiceRegistry reg = new DefaultDingTalkServiceRegistry();
        DingTalkService svc = service("corp2");
        reg.register("corp2", svc);
        assertTrue(reg.find("corp2").isPresent());
        assertSame(svc, reg.find("corp2").get());
        Optional<DingTalkService> removed = reg.remove("corp2");
        assertTrue(removed.isPresent());
        assertSame(svc, removed.get());
        assertTrue(reg.find("corp2").isEmpty());
        assertTrue(reg.remove("corp2").isEmpty());
        assertTrue(reg.find(null).isEmpty());
        assertTrue(reg.remove(null).isEmpty());
    }

    @Test
    void snapshotIsImmutable() {
        DingTalkServiceRegistry reg = new DefaultDingTalkServiceRegistry();
        DingTalkService s1 = service("corp-a");
        DingTalkService s2 = service("corp-b");
        reg.register("corp-a", s1);
        reg.register("corp-b", s2);
        Map<String, DingTalkService> snap = reg.snapshot();
        assertEquals(2, snap.size());
        assertSame(s1, snap.get("corp-a"));
        assertSame(s2, snap.get("corp-b"));
        assertThrows(UnsupportedOperationException.class,
                () -> snap.put("corp-c", service("corp-c")));
        reg.register("corp-c", service("corp-c"));
        assertEquals(2, snap.size(), "snapshot must be frozen copy");
    }
}
