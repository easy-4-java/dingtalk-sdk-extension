package io.github.easy4j.dingtalk.registry;

import io.github.easy4j.dingtalk.service.DingTalkService;

import java.util.Map;
import java.util.Optional;

/**
 * Registry of tenant-scoped {@link DingTalkService} instances.
 * <p>Replaces the untested {@link DingTalkMultiServicesHolder} with an explicit,
 * fail-fast contract that callers can stub in tests.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DefaultDingTalkServiceRegistry
 */
public interface DingTalkServiceRegistry {

    /**
     * Registers (or replaces) a service under {@code tenantId}.
     *
     * @param tenantId tenant identifier; must be non-blank
     * @param service  the service instance; must not be {@code null}
     * @return the previously registered service, wrapped, or empty
     * @throws NullPointerException if either argument is {@code null}
     */
    Optional<DingTalkService> register(String tenantId, DingTalkService service);

    /**
     * @param tenantId tenant identifier
     * @return the service registered under {@code tenantId}, or empty
     */
    Optional<DingTalkService> find(String tenantId);

    /**
     * @param tenantId tenant identifier
     * @return the registered service; never {@code null}
     * @throws IllegalStateException when no service is registered for the tenant
     */
    DingTalkService require(String tenantId);

    /**
     * Removes the service registered under {@code tenantId}.
     *
     * @return the removed service, or empty
     */
    Optional<DingTalkService> remove(String tenantId);

    /**
     * @return an immutable snapshot of the current registrations; callers cannot
     *         mutate the live registry via this map
     */
    Map<String, DingTalkService> snapshot();
}
