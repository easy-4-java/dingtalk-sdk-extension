package io.github.easy4j.dingtalk.registry;

import io.github.easy4j.dingtalk.service.DingTalkService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default {@link DingTalkServiceRegistry} backed by a {@link ConcurrentHashMap}.
 * <p>Snapshots are produced using {@link Map#copyOf(Map)}, guaranteeing
 * immutability for callers without leaking the live registry.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Slf4j
public class DefaultDingTalkServiceRegistry implements DingTalkServiceRegistry {

    private final ConcurrentHashMap<String, DingTalkService> services = new ConcurrentHashMap<>();

    @Override
    public Optional<DingTalkService> register(String tenantId, DingTalkService service) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(service, "service");
        if (tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId must be non-blank");
        }
        DingTalkService prior = services.put(tenantId, service);
        if (log.isDebugEnabled()) {
            log.debug("DingTalkServiceRegistry registered tenant={} replacedPrevious={}",
                    tenantId, Boolean.valueOf(prior != null));
        }
        return Optional.ofNullable(prior);
    }

    @Override
    public Optional<DingTalkService> find(String tenantId) {
        if (tenantId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(services.get(tenantId));
    }

    @Override
    public DingTalkService require(String tenantId) {
        DingTalkService svc = tenantId == null ? null : services.get(tenantId);
        if (svc == null) {
            throw new IllegalStateException("No DingTalkService registered for tenantId=" + tenantId);
        }
        return svc;
    }

    @Override
    public Optional<DingTalkService> remove(String tenantId) {
        if (tenantId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(services.remove(tenantId));
    }

    @Override
    public Map<String, DingTalkService> snapshot() {
        return Map.copyOf(services);
    }
}
