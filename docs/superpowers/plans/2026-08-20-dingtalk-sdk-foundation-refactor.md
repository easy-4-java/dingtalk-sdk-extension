# DingTalk SDK Foundation Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn `dingtalk-sdk-extension` into a buildable, framework-neutral DingTalk foundation SDK with a real `DingTalkService` facade, integrated token/ticket storage, consistent error semantics, and a thin Spring Boot starter that contains only Spring-specific binding and auto-configuration.

**Architecture:** Stabilize the existing 3.0.x API first under the current package so every change is testable and releasable. Then introduce the `io.github.easy4j.dingtalk` namespace and Service-oriented public API behind deprecated compatibility adapters in a separate major-version milestone. The starter consumes the SDK, maps Spring properties into SDK configuration, and creates one service graph; it never owns DingTalk domain models or API implementations.

**Tech Stack:** Java 21, Maven 4.0.0-rc-5, Alibaba DingTalk Service SDK 2.0.0, JUnit Jupiter 6.1.0, AssertJ/Spring `ApplicationContextRunner` in the starter, Lombok, SLF4J, CodeGraph.

**Spec:** `docs/superpowers/plans/2026-08-20-dingtalk-sdk-foundation-refactor.md#requirements-baseline`

## Global Constraints

- Do not use Git worktrees. This repository explicitly prohibits them, overriding the Superpowers worktree recommendation.
- Do not create or switch branches unless the user explicitly authorizes it.
- Preserve all existing dirty and untracked files in `dingtalk-sdk-extension`; never revert or overwrite unrelated user changes.
- Treat `dingtalk-sdk-extension` as a pure Java SDK: no Spring Framework or Spring Boot dependency, annotation, lifecycle interface, or `ApplicationContext` reference may enter it.
- Treat `dingtalk-spring-boot-starter` as integration glue: it may contain Spring properties, conditions, mapping, and storage-strategy auto-configuration, but no duplicated DingTalk domain implementation.
- Use JDK 21 and each repository's Maven 4 wrapper. Until the starter wrapper mode is repaired, invoke it as `bash ./mvnw`.
- Apply TDD to every behavior change: add a focused failing test, prove the intended failure, implement the minimum behavior, then run focused and regression tests.
- All Java null checks use `Objects.isNull` / `Objects.nonNull`; string checks use imported `StringUtils` or `StrKit` according to the project rules.
- Logs use SLF4J with Lombok `@Slf4j`; never log corp secrets, app secrets, robot secrets, access tokens, or signed webhook URLs.
- Public API compatibility changes require a deprecated adapter in 3.x and removal only in the next major SDK version.
- Do not mark a task complete from compilation alone; each task lists an observable test gate.
- After Java edits in the SDK, run `codegraph sync` and check `codegraph affected` for the touched production files.

---

## Requirements Baseline

### Required end state

1. `dingtalk-sdk-extension` builds from the current checkout and its full test suite executes.
2. `DingTalkService` has a concrete implementation; no constructor performs a runtime cast from `DingTalkTemplate` to `DingTalkService`.
3. All six operation groups share one service graph and one configuration/token/storage graph.
4. Robot message conversion supports text, markdown, link, action card, and feed card using the current message-model accessors.
5. All official DingTalk responses pass through a consistent logical error check; non-zero `errcode` never becomes `null` or an empty string.
6. Access tokens and JSAPI tickets use `DingTalkConfigStorage`, expiration buffering, and double-checked locking.
7. The unused SDK `property/` model is removed; Spring `Properties` types remain only in the starter.
8. The starter creates one facade bean, permits user overrides, and contains no duplicated SDK domain tests.
9. Cross-repository verification installs the current SDK artifact before testing the starter, so an older local snapshot cannot produce a false pass.
10. The next major version moves the SDK to `io.github.easy4j.dingtalk` and replaces the public `Template/Operations` vocabulary with `Service`, with one-major-version compatibility adapters.

### Explicit non-goals for this plan

- Do not add Redis, Jedis, or Redisson implementations before the in-memory storage is connected and proven.
- Do not introduce a second HTTP library; continue to delegate transport to `alibaba-dingtalk-service-sdk`.
- Do not create message routers, webhook event dispatch, or callback encryption abstractions in this change.
- Do not merge SNS and SSO behavior until compatibility aliases and endpoint-level tests exist.
- Do not publish or deploy artifacts as part of implementation verification.

## Repository Map

| Repository | Role | Current branch/state relevant to execution |
|---|---|---|
| `/Users/wandl/workspaces/workspace-github-easy-4-java/dingtalk-sdk-extension` | Pure SDK and plan source of truth | `feature/3.0.x`, ahead of origin, dirty migration in progress |
| `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter` | Spring Boot integration | `4.1.x`, ahead of origin, clean at plan creation |
| `/Users/wandl/workspaces/workspace-github/WxJava` | Read-only reference | detached HEAD; never modify during this implementation |

## Target File Structure

### 3.x stabilization structure

Keep the current package during Tasks 1-9 so compilation and behavior are repaired without combining a package-wide breaking change:

```text
com/dingtalk/spring/boot/
├── DingTalkTemplate.java                    # deprecated compatibility facade
├── DingTalkConfig.java
├── DingTalkConfigProvider.java
├── DefaultDingTalkConfigProvider.java
├── DingTalkAccessTokenProvider.java
├── DefaultDingTalkAccessTokenProvider.java
├── service/
│   ├── DingTalkService.java                 # public facade contract
│   ├── DefaultDingTalkService.java          # one concrete object graph
│   ├── DingTalkServiceRegistry.java
│   └── DefaultDingTalkServiceRegistry.java
├── error/
│   ├── DingTalkError.java
│   ├── DingTalkErrorCode.java
│   ├── DingTalkErrorException.java
│   ├── DingTalkRuntimeException.java
│   └── DingTalkResponseValidator.java
├── spi/storage/
│   ├── DingTalkConfigStorage.java
│   └── InMemoryDingTalkConfigStorage.java
├── bean/                                    # retained until namespace migration
└── utils/                                   # retained until namespace migration
```

### Next-major target structure

```text
io/github/easy4j/dingtalk/
├── service/
│   ├── DingTalkService.java
│   ├── DingTalkAuthService.java
│   ├── DingTalkUserService.java
│   ├── DingTalkJsapiService.java
│   ├── DingTalkRobotService.java
│   └── impl/
├── config/
├── storage/
│   └── impl/
├── model/
│   ├── message/
│   └── jsapi/
├── error/
├── registry/
└── internal/
```

---

## Milestone A: Restore a Buildable 3.x SDK

### Task 1: Lock the current failure and compatibility baseline

**Files:**
- Create: `src/test/java/com/dingtalk/spring/boot/CurrentMigrationBaselineTest.java`
- Create: `src/test/java/com/dingtalk/spring/boot/ApiCompatibilityTest.java`
- Read: `src/main/java/com/dingtalk/spring/boot/DingTalkTemplate.java`
- Read: `src/main/java/com/dingtalk/spring/boot/DingTalkRobotOperations.java`
- Read: `src/main/java/com/dingtalk/spring/boot/bean/ActionCardMessage.java`

**Interfaces:**
- Consumes: current 3.x public constructors and `opsFor*()` methods.
- Produces: executable compatibility assertions that later tasks must keep green.

- [ ] **Step 1: Capture the current build failure before editing**

Run:

```bash
cd /Users/wandl/workspaces/workspace-github-easy-4-java/dingtalk-sdk-extension
./mvnw -B --no-transfer-progress test
```

Expected: compilation fails with the 13 known errors in `DingTalkOperations` and `DingTalkRobotOperations`. Save the exact command output in the task notes; do not treat this as a test-red proof because test compilation has not started.

- [ ] **Step 2: Add facade construction and identity assertions**

Add tests equivalent to:

```java
@Test
void templateConstructionMustNotThrowClassCastException() {
    DingTalkTemplate template = TestFixtures.template();
    assertNotNull(template.opsForRobot());
    assertSame(template.opsForRobot(), template.opsForRobot());
}

@Test
void publicCompatibilityMethodsRemainAvailable() throws Exception {
    assertNotNull(DingTalkTemplate.class.getMethod("opsForAccount"));
    assertNotNull(DingTalkRobotOperations.class.getMethod("buidRequest", BaseMessage.class));
    assertNotNull(DingTalkRobotOperations.class.getMethod("buildRequest", BaseMessage.class));
}
```

- [ ] **Step 3: Add a single shared fixture instead of duplicating configuration setup**

Create package-private helpers inside `CurrentMigrationBaselineTest` or an existing test fixture:

```java
static DingTalkConfig config() {
    DingTalkConfig config = new DingTalkConfig();
    config.setCorpId("corp-1");
    config.setCorpSecret("corp-secret");
    return config;
}
```

- [ ] **Step 4: Do not commit while main compilation is broken**

This task's test code is a baseline input for Tasks 2-4. Commit it together with the first task that restores compilation, so no commit intentionally leaves the branch less buildable than before.

### Task 2: Replace the invalid generic response checker

**Files:**
- Create: `src/main/java/com/dingtalk/spring/boot/error/DingTalkResponseValidator.java`
- Create: `src/test/java/com/dingtalk/spring/boot/error/DingTalkResponseValidatorTest.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkOperations.java:73`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkRobotOperations.java:105,243,330`

**Interfaces:**
- Consumes: `TaobaoResponse`, generated response `getErrcode()` / `getErrmsg()` values.
- Produces: `static <T extends TaobaoResponse> T requireSuccess(T response, Long errcode, String errmsg) throws DingTalkErrorException`.

- [ ] **Step 1: Write failing validator tests**

Cover all four observable branches:

```java
@Test
void returnsSameResponseForZeroErrcode() throws Exception {
    OapiRobotSendResponse response = new OapiRobotSendResponse();
    response.setErrcode(0L);
    response.setErrmsg("ok");
    assertSame(response, DingTalkResponseValidator.requireSuccess(
            response, response.getErrcode(), response.getErrmsg()));
}

@Test
void throwsStructuredExceptionForNonZeroErrcode() {
    OapiRobotSendResponse response = new OapiRobotSendResponse();
    response.setErrcode(40014L);
    response.setErrmsg("invalid access token");
    DingTalkErrorException exception = assertThrows(
            DingTalkErrorException.class,
            () -> DingTalkResponseValidator.requireSuccess(
                    response, response.getErrcode(), response.getErrmsg()));
    assertEquals(40014, exception.getErrorCode());
    assertEquals(response.getBody(), exception.getError().getBody());
}
```

Also test `null` response and `isSuccess() == false` with `getErrorCode()` / `getSubCode()` fallback.

- [ ] **Step 2: Run the focused test and verify it cannot compile**

Run:

```bash
./mvnw -B --no-transfer-progress -Dtest=DingTalkResponseValidatorTest test
```

Expected: FAIL because `DingTalkResponseValidator` does not exist, after the pre-existing main-source errors are addressed in the same red-green cycle.

- [ ] **Step 3: Implement the validator without reflection or JSON parsing**

The implementation must use generated response accessors supplied by callers and base response fields only as transport fallback:

```java
public static <T extends TaobaoResponse> T requireSuccess(
        T response, Long errcode, String errmsg) throws DingTalkErrorException {
    if (Objects.isNull(response)) {
        throw new DingTalkErrorException("DingTalk API returned null response");
    }
    long resolvedCode = Objects.nonNull(errcode) ? errcode.longValue()
            : parseCode(StringUtils.defaultIfBlank(response.getSubCode(), response.getErrorCode()));
    if (!response.isSuccess() || resolvedCode != 0L) {
        DingTalkError error = DingTalkError.builder()
                .errorCode(Integer.valueOf(Math.toIntExact(resolvedCode)))
                .errorMsg(StringUtils.defaultIfBlank(errmsg,
                        StringUtils.defaultIfBlank(response.getSubMsg(), response.getMsg())))
                .body(response.getBody())
                .build();
        throw new DingTalkErrorException(error);
    }
    return response;
}
```

`parseCode` returns `-1L` for blank or non-numeric transport codes; it must not throw `NumberFormatException`.

- [ ] **Step 4: Replace the broken method in `DingTalkOperations`**

Use the exact protected API:

```java
protected final <T extends TaobaoResponse> T executeChecked(
        T response, Long errcode, String errmsg) throws DingTalkErrorException {
    return DingTalkResponseValidator.requireSuccess(response, errcode, errmsg);
}
```

Remove the invalid calls to `TaobaoResponse#getErrcode`, `getErrmsg`, and the invalid `ApiException(int, String)` constructor.

- [ ] **Step 5: Update the three Robot call sites**

Each call must retain its concrete generated response type:

```java
OapiRobotSendResponse response = client.execute(request);
return executeChecked(response, response.getErrcode(), response.getErrmsg());
```

For `OapiV2UserGetResponse`, pass its concrete `getErrcode()` and `getErrmsg()` accessors.

- [ ] **Step 6: Run focused and compile gates**

```bash
./mvnw -B --no-transfer-progress -DskipTests compile
./mvnw -B --no-transfer-progress -Dtest=DingTalkResponseValidatorTest test
```

Expected: the three response-checker compilation errors are gone; remaining failures are limited to the ActionCard mismatch until Task 3.

### Task 3: Reconcile ActionCard message and request conversion

**Files:**
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkRobotOperations.java:126-170`
- Modify: `src/test/java/com/dingtalk/spring/boot/DingTalkRobotOperationsTest.java`
- Test: `src/test/java/com/dingtalk/spring/boot/bean/ActionCardMessageTest.java`

**Interfaces:**
- Consumes: `ActionCardMessage#getText`, `getButtons`, `getHideAvatar`, `getBtnOrientation`, `isButtonView`.
- Produces: a valid `OapiRobotSendRequest.Actioncard` for single-jump and multi-button cards.

- [ ] **Step 1: Write failing request-shape tests**

Add these two cases:

```java
@Test
void buildsMultiButtonActionCardFromCurrentModel() {
    ActionCardMessage message = new ActionCardMessage("title", "**body**");
    message.addButton(new ActionCardButton("Approve", "https://example.test/approve"));
    message.addButton(new ActionCardButton("Reject", "https://example.test/reject"));
    OapiRobotSendRequest request = operations().buildRequest(message);
    assertEquals("actionCard", request.getMsgtype());
    assertEquals("**body**", request.getActionCard().getMarkdown());
    assertEquals(2, request.getActionCard().getBtns().size());
}

@Test
void buildsSingleJumpActionCardWhenButtonViewEnabled() {
    ActionCardMessage message = new ActionCardMessage("title", "body");
    message.addButton(new ActionCardButton("Open", "https://example.test/open"));
    message.setButtonView(true);
    OapiRobotSendRequest request = operations().buildRequest(message);
    assertEquals("Open", request.getActionCard().getSingleTitle());
    assertEquals("https://example.test/open", request.getActionCard().getSingleURL());
    assertTrue(Objects.isNull(request.getActionCard().getBtns()));
}
```

- [ ] **Step 2: Run the focused test and verify failure**

```bash
./mvnw -B --no-transfer-progress -Dtest=DingTalkRobotOperationsTest test
```

Expected before implementation: main compilation fails on removed `ActionCardMessage` accessors.

- [ ] **Step 3: Implement conversion using current model accessors**

Replace the old-accessor branch with this behavior:

```java
card.setTitle(message.getTitle());
card.setMarkdown(message.getText());
card.setHideAvatar(String.valueOf(message.getHideAvatar().getValue()));
card.setBtnOrientation(String.valueOf(message.getBtnOrientation().getValue()));
List<ActionCardButton> buttons = message.getButtons();
if (message.isButtonView() && buttons.size() == 1) {
    ActionCardButton button = buttons.get(0);
    card.setSingleTitle(button.getTitle());
    card.setSingleURL(button.getActionURL());
} else {
    card.setBtns(toRequestButtons(buttons));
}
```

`toRequestButtons` returns an empty list for an empty input and never dereferences a null button. Do not invent ActionCard `atMobiles` behavior because the current model does not expose it.

- [ ] **Step 4: Run Robot and message-model tests**

```bash
./mvnw -B --no-transfer-progress \
  -Dtest=DingTalkRobotOperationsTest,ActionCardMessageTest test
```

Expected: PASS.

- [ ] **Step 5: Run the full SDK test suite**

```bash
./mvnw -B --no-transfer-progress test
```

Expected: main and test compilation complete; failures now describe runtime service construction if Task 4 is not yet implemented.

- [ ] **Step 6: Commit Tasks 1-3 as the first build-restoring unit**

```bash
git add src/main/java/com/dingtalk/spring/boot/DingTalkOperations.java \
        src/main/java/com/dingtalk/spring/boot/DingTalkRobotOperations.java \
        src/main/java/com/dingtalk/spring/boot/error/DingTalkResponseValidator.java \
        src/test/java/com/dingtalk/spring/boot
git commit -m "fix: restore dingtalk sdk compilation"
```

Before committing, inspect `git diff --cached` and remove unrelated pre-existing files from the index.

### Task 4: Build one concrete `DingTalkService` object graph

**Files:**
- Create: `src/main/java/com/dingtalk/spring/boot/service/DefaultDingTalkService.java`
- Create: `src/test/java/com/dingtalk/spring/boot/service/DefaultDingTalkServiceTest.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkTemplate.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkAccountOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkSnsOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkSsoOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkJsapiOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkRobotOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkUserOperations.java`

**Interfaces:**
- Consumes: `DingTalkConfigProvider`, `DingTalkAccessTokenProvider`.
- Produces: `DefaultDingTalkService implements DingTalkService`; deprecated `DingTalkTemplate extends DefaultDingTalkService`.

- [ ] **Step 1: Write service-graph tests**

```java
@Test
void createsExactlyOneInstancePerOperationDomain() {
    DingTalkService service = new DefaultDingTalkService(configProvider(), tokenProvider());
    assertSame(service.opsForAccount(), service.opsForAccount());
    assertSame(service.opsForRobot(), service.opsForRobot());
}

@Test
void legacyTemplateIsAServiceWithoutRuntimeCast() {
    DingTalkService service = new DingTalkTemplate(configProvider(), tokenProvider());
    assertNotNull(service.opsForUser());
}
```

- [ ] **Step 2: Run the tests and prove the missing implementation**

```bash
./mvnw -B --no-transfer-progress -Dtest=DefaultDingTalkServiceTest test
```

Expected: FAIL because `DefaultDingTalkService` does not exist or template construction throws `ClassCastException`.

- [ ] **Step 3: Move facade state into `DefaultDingTalkService`**

Use constructor initialization after dependencies are assigned:

```java
public class DefaultDingTalkService implements DingTalkService {
    private final DingTalkConfigProvider configProvider;
    private final DingTalkAccessTokenProvider tokenProvider;
    private final DingTalkAccountOperations accountOperations;
    private final DingTalkSnsOperations snsOperations;
    private final DingTalkSsoOperations ssoOperations;
    private final DingTalkJsapiOperations jsapiOperations;
    private final DingTalkRobotOperations robotOperations;
    private final DingTalkUserOperations userOperations;

    public DefaultDingTalkService(DingTalkConfigProvider configProvider,
                                  DingTalkAccessTokenProvider tokenProvider) {
        this.configProvider = Objects.requireNonNull(configProvider, "configProvider");
        this.tokenProvider = Objects.requireNonNull(tokenProvider, "tokenProvider");
        this.accountOperations = new DingTalkAccountOperations(this);
        this.snsOperations = new DingTalkSnsOperations(this);
        this.ssoOperations = new DingTalkSsoOperations(this);
        this.jsapiOperations = new DingTalkJsapiOperations(this);
        this.robotOperations = new DingTalkRobotOperations(this);
        this.userOperations = new DingTalkUserOperations(this);
    }
}
```

Move the existing delegation and signature behavior from `DingTalkTemplate` without changing observable output.

- [ ] **Step 4: Convert every operation constructor to `DingTalkService`**

The primary constructor in every operation class is:

```java
public DingTalkAccountOperations(DingTalkService service) {
    super(service);
}
```

Retain a deprecated compatibility constructor only while `DingTalkTemplate` remains:

```java
@Deprecated
public DingTalkAccountOperations(DingTalkTemplate template) {
    this((DingTalkService) template);
}
```

The cast is now safe because the compatibility template extends a concrete service implementation.

- [ ] **Step 5: Reduce `DingTalkTemplate` to a compatibility adapter**

```java
@Deprecated
public class DingTalkTemplate extends DefaultDingTalkService {
    public DingTalkTemplate(DingTalkConfigProvider configProvider,
                            DingTalkAccessTokenProvider tokenProvider) {
        super(configProvider, tokenProvider);
    }
}
```

- [ ] **Step 6: Run construction, operation, and full tests**

```bash
./mvnw -B --no-transfer-progress \
  -Dtest=DefaultDingTalkServiceTest,DingTalkTemplateTest,DingTalkRobotOperationsTest test
./mvnw -B --no-transfer-progress test
```

Expected: PASS; no `ClassCastException`.

- [ ] **Step 7: Commit the service graph**

```bash
git add src/main/java/com/dingtalk/spring/boot/service \
        src/main/java/com/dingtalk/spring/boot/DingTalkTemplate.java \
        src/main/java/com/dingtalk/spring/boot/DingTalk*Operations.java \
        src/test/java/com/dingtalk/spring/boot/service \
        src/test/java/com/dingtalk/spring/boot/DingTalkTemplateTest.java
git commit -m "refactor: introduce concrete dingtalk service facade"
```

### Task 5: Make configuration initialization an SDK responsibility

**Files:**
- Modify: `src/main/java/com/dingtalk/spring/boot/DefaultDingTalkConfigProvider.java`
- Modify: `src/test/java/com/dingtalk/spring/boot/DefaultDingTalkConfigProviderTest.java`
- Delete: `src/main/java/com/dingtalk/spring/boot/property/DingTalkCorpAppProperties.java`
- Delete: `src/main/java/com/dingtalk/spring/boot/property/DingTalkLoginProperties.java`
- Delete: `src/main/java/com/dingtalk/spring/boot/property/DingTalkPersonalMiniAppProperties.java`
- Delete: `src/main/java/com/dingtalk/spring/boot/property/DingTalkRobotProperties.java`
- Delete: `src/main/java/com/dingtalk/spring/boot/property/DingTalkSuiteProperties.java`

**Interfaces:**
- Consumes: `DingTalkConfig` and its `config/*Config` lists.
- Produces: immediately usable `DefaultDingTalkConfigProvider`; no Spring lifecycle callback required.

- [ ] **Step 1: Write a failing constructor-readiness test**

```java
@Test
void constructorIndexesApplicationSecretsWithoutManualInit() {
    DingTalkConfig config = TestFixtures.configWithCorpApp("app-key", "app-secret");
    DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(config);
    assertTrue(provider.hasAppKey("app-key"));
    assertEquals("app-secret", provider.getAppSecret("corp-1", "app-key"));
}
```

- [ ] **Step 2: Verify the current requirement to call `init()` causes failure**

```bash
./mvnw -B --no-transfer-progress -Dtest=DefaultDingTalkConfigProviderTest test
```

Expected: FAIL because the constructor does not index secrets.

- [ ] **Step 3: Initialize atomically in the constructor**

Replace the externally required lifecycle with a private rebuild:

```java
public DefaultDingTalkConfigProvider(DingTalkConfig config) {
    this.dingTalkConfig = Objects.requireNonNull(config, "config");
    rebuildIndex();
}

@Deprecated
public void init() {
    rebuildIndex();
}
```

`rebuildIndex()` builds a temporary `Map<String, String>`, validates duplicate keys with conflicting secrets, then replaces the live map contents. Repeated calls must remain idempotent.

- [ ] **Step 4: Add duplicate-key conflict coverage**

Two entries with the same app key and different secrets must throw `IllegalArgumentException` containing the key but neither secret. Same-key/same-secret input may collapse to one entry.

- [ ] **Step 5: Remove the unused SDK `property/` classes**

Before deleting, run:

```bash
codegraph callers DingTalkCorpAppProperties --limit 100
codegraph callers DingTalkRobotProperties --limit 100
```

Expected: no production callers. Delete only these SDK classes; retain Starter `DingTalkProperties`.

- [ ] **Step 6: Run provider and full tests**

```bash
./mvnw -B --no-transfer-progress -Dtest=DefaultDingTalkConfigProviderTest test
./mvnw -B --no-transfer-progress test
```

- [ ] **Step 7: Commit configuration lifecycle cleanup**

```bash
git add src/main/java/com/dingtalk/spring/boot/DefaultDingTalkConfigProvider.java \
        src/main/java/com/dingtalk/spring/boot/property \
        src/test/java/com/dingtalk/spring/boot/DefaultDingTalkConfigProviderTest.java
git commit -m "refactor: own config initialization in sdk"
```

---

## Milestone B: Connect Runtime Storage and Error Semantics

### Task 6: Promote the in-memory storage contract into the token provider

**Files:**
- Modify: `src/main/java/com/dingtalk/spring/boot/spi/storage/DingTalkConfigStorage.java`
- Rename: `src/main/java/com/dingtalk/spring/boot/spi/storage/DingTalkMemoryConfigStorage.java` → `InMemoryDingTalkConfigStorage.java`
- Create: `src/test/java/com/dingtalk/spring/boot/spi/storage/InMemoryDingTalkConfigStorageTest.java`
- Create: `src/main/java/com/dingtalk/spring/boot/internal/DingTalkTokenClient.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkAccessTokenProvider.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DefaultDingTalkAccessTokenProvider.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/service/DingTalkService.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/service/DefaultDingTalkService.java`
- Create: `src/test/java/com/dingtalk/spring/boot/DefaultDingTalkAccessTokenProviderTest.java`

**Interfaces:**
- Consumes: config provider app secrets and official token responses.
- Produces: keyed token cache with SPI-level locks and deterministic refresh behavior.

- [ ] **Step 1: Add lock methods to the storage interface**

```java
Lock getAccessTokenLock(String key);

Lock getJsapiTicketLock(String key);
```

Use `java.util.concurrent.locks.Lock` in the contract; do not expose `ReentrantLock`.

- [ ] **Step 2: Write storage expiry and lock-stability tests**

```java
@Test
void returnsSameStripedLockForSameKey() {
    DingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
    assertSame(storage.getAccessTokenLock("corp:app"),
               storage.getAccessTokenLock("corp:app"));
}

@Test
void expiresTokenBeforeRemoteTtl() {
    InMemoryDingTalkConfigStorage storage = new InMemoryDingTalkConfigStorage();
    storage.setExpireBufferSeconds(300L);
    storage.updateAccessToken("corp:app", "token", 301L);
    assertFalse(storage.isAccessTokenExpired("corp:app"));
}
```

Add these exact boundary assertions:

```java
assertThrows(NullPointerException.class, () -> storage.getAccessTokenLock(null));
storage.updateAccessToken("corp:app", "token", 7200L);
storage.expireAccessToken("corp:app");
assertTrue(storage.isAccessTokenExpired("corp:app"));
assertTrue(Objects.isNull(storage.getAccessToken("corp:app")));
```

- [ ] **Step 3: Rename the implementation consistently**

The final class declaration is:

```java
public class InMemoryDingTalkConfigStorage implements DingTalkConfigStorage
```

Retain this deprecated 3.x forwarding class unconditionally:

```java
@Deprecated
public class DingTalkMemoryConfigStorage extends InMemoryDingTalkConfigStorage {
}
```

It preserves source compatibility and is deleted in Task 12.

- [ ] **Step 4: Make logical token failures explicit in the public contracts**

Update both provider and service signatures:

```java
String getAccessToken(String corpId, String appKey)
        throws ApiException, DingTalkErrorException;

String getSnsAccessToken(String corpId, String appId)
        throws ApiException, DingTalkErrorException;
```

Update implementations, compatibility facade methods, tests, and Javadocs in the same task. Do not convert a non-zero DingTalk code into a generic `ApiException` or unchecked exception.

- [ ] **Step 5: Add storage-aware provider constructors**

```java
public DefaultDingTalkAccessTokenProvider(DingTalkConfigProvider configProvider) {
    this(configProvider, new InMemoryDingTalkConfigStorage());
}

public DefaultDingTalkAccessTokenProvider(DingTalkConfigProvider configProvider,
                                          DingTalkConfigStorage storage) {
    this.configProvider = Objects.requireNonNull(configProvider, "configProvider");
    this.storage = Objects.requireNonNull(storage, "storage");
}
```

- [ ] **Step 6: Write token-cache tests with an injectable request seam**

Create `src/main/java/com/dingtalk/spring/boot/internal/DingTalkTokenClient.java` as a public two-operation boundary used by the provider and its tests:

```java
public interface DingTalkTokenClient {
    OapiGettokenResponse getCorpToken(String appKey, String appSecret) throws ApiException;
    OapiSnsGettokenResponse getSnsToken(String appId, String appSecret) throws ApiException;
}
```

Test that two sequential calls make one remote request, forced expiry causes a second request, and two concurrent expired calls cause one refresh.

- [ ] **Step 7: Implement double-checked locking**

```java
String key = "corp:" + corpId + ':' + appKey;
if (!storage.isAccessTokenExpired(key)) {
    return storage.getAccessToken(key);
}
Lock lock = storage.getAccessTokenLock(key);
lock.lock();
try {
    if (storage.isAccessTokenExpired(key)) {
        OapiGettokenResponse response = tokenClient.getCorpToken(appKey, appSecret);
        DingTalkResponseValidator.requireSuccess(
                response, response.getErrcode(), response.getErrmsg());
        Long expiresIn = response.getExpiresIn();
        if (Objects.isNull(expiresIn)) {
            throw new DingTalkErrorException("DingTalk token response omitted expires_in");
        }
        storage.updateAccessToken(key, response.getAccessToken(), expiresIn.longValue());
    }
    return storage.getAccessToken(key);
} finally {
    lock.unlock();
}
```

Use the prefix `sns:` for SNS token keys. Never return `null` or `StringUtils.EMPTY` after a logical API error.

- [ ] **Step 8: Run concurrency and provider tests**

```bash
./mvnw -B --no-transfer-progress \
  -Dtest=InMemoryDingTalkConfigStorageTest,DefaultDingTalkAccessTokenProviderTest test
```

Expected: PASS with deterministic request counts.

- [ ] **Step 9: Commit the connected storage**

```bash
git add src/main/java/com/dingtalk/spring/boot/spi/storage \
        src/main/java/com/dingtalk/spring/boot/DingTalkAccessTokenProvider.java \
        src/main/java/com/dingtalk/spring/boot/DefaultDingTalkAccessTokenProvider.java \
        src/main/java/com/dingtalk/spring/boot/service/DingTalkService.java \
        src/main/java/com/dingtalk/spring/boot/service/DefaultDingTalkService.java \
        src/test/java/com/dingtalk/spring/boot
git commit -m "feat: cache dingtalk access tokens through storage"
```

### Task 7: Cache JSAPI tickets and make `TicketType` observable

**Files:**
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkJsapiOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/service/DingTalkService.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/service/DefaultDingTalkService.java`
- Modify: `src/test/java/com/dingtalk/spring/boot/DingTalkJsapiOperationsTest.java`

**Interfaces:**
- Consumes: `DingTalkConfigStorage`, `TicketType`, access token.
- Produces: `String getJsapiTicket(String cacheKey, TicketType type, String accessToken, boolean forceRefresh)`.

- [ ] **Step 1: Add failing cache-hit and force-refresh tests**

Use a fake ticket client with an `AtomicInteger` request count. Assert:

```java
assertEquals(firstTicket, operations.getJsapiTicket(key, TicketType.JSAPI, token, false));
assertEquals(firstTicket, operations.getJsapiTicket(key, TicketType.JSAPI, token, false));
assertEquals(1, requestCount.get());
operations.getJsapiTicket(key, TicketType.JSAPI, token, true);
assertEquals(2, requestCount.get());
```

- [ ] **Step 2: Expose storage from the service contract**

```java
DingTalkConfigStorage getConfigStorage();
```

The service contract exposes the shared storage, and the preferred constructor accepts that storage directly.

Implement the constructors explicitly:

```java
public DefaultDingTalkService(DingTalkConfigProvider configProvider,
                              DingTalkConfigStorage storage) {
    this(configProvider,
            new DefaultDingTalkAccessTokenProvider(configProvider, storage),
            storage);
}

public DefaultDingTalkService(DingTalkConfigProvider configProvider,
                              DingTalkAccessTokenProvider tokenProvider,
                              DingTalkConfigStorage storage) {
    this.configProvider = Objects.requireNonNull(configProvider, "configProvider");
    this.tokenProvider = Objects.requireNonNull(tokenProvider, "tokenProvider");
    this.storage = Objects.requireNonNull(storage, "storage");
    initializeOperations();
}

@Deprecated
public DefaultDingTalkService(DingTalkConfigProvider configProvider,
                              DingTalkAccessTokenProvider tokenProvider) {
    this(configProvider, tokenProvider, new InMemoryDingTalkConfigStorage());
}
```

The preferred two-argument `(configProvider, storage)` path shares one storage between the default token provider and JSAPI operations. The deprecated custom-provider constructor preserves 3.x behavior; a custom provider remains responsible for its own token state.

- [ ] **Step 3: Implement ticket DCL using `TicketType` as part of the storage key**

Use `cacheKey + ':' + type.name()` and the storage JSAPI lock. On `forceRefresh`, call `expireJsapiTicket` before the first expiry check.

- [ ] **Step 4: Keep the old raw response method as a deprecated compatibility method**

```java
@Deprecated
public OapiGetJsapiTicketResponse getTicket(TicketType type, String accessToken)
        throws ApiException, DingTalkErrorException {
    return requestTicket(type, accessToken);
}
```

The old method must now validate the response and must not silently ignore `type`; if the official request has no type field, document that the type partitions local cache semantics.

- [ ] **Step 5: Run JSAPI, storage, and full tests**

```bash
./mvnw -B --no-transfer-progress \
  -Dtest=DingTalkJsapiOperationsTest,InMemoryDingTalkConfigStorageTest test
./mvnw -B --no-transfer-progress test
```

- [ ] **Step 6: Commit ticket storage integration**

```bash
git add src/main/java/com/dingtalk/spring/boot/DingTalkJsapiOperations.java \
        src/main/java/com/dingtalk/spring/boot/service \
        src/test/java/com/dingtalk/spring/boot/DingTalkJsapiOperationsTest.java
git commit -m "feat: cache dingtalk jsapi tickets"
```

### Task 8: Apply logical response validation to every operation domain

**Files:**
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkAccountOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkSnsOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkSsoOperations.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/DingTalkUserOperations.java`
- Modify: `src/test/java/com/dingtalk/spring/boot/DingTalkAccountOperationsTest.java`
- Modify: `src/test/java/com/dingtalk/spring/boot/DingTalkSnsOperationsTest.java`
- Modify: `src/test/java/com/dingtalk/spring/boot/DingTalkSsoOperationsTest.java`
- Modify: `src/test/java/com/dingtalk/spring/boot/DingTalkUserOperationsTest.java`

**Interfaces:**
- Consumes: `DingTalkResponseValidator`.
- Produces: validated typed responses and corrected camel-case aliases.

- [ ] **Step 1: Introduce a client factory test seam**

Create `src/main/java/com/dingtalk/spring/boot/internal/DingTalkClientFactory.java`:

```java
@FunctionalInterface
public interface DingTalkClientFactory {
    DingTalkClient create(String endpoint);
}
```

The default service supplies `DefaultDingTalkClient::new`. Operation tests inject fake clients through a package-private constructor; public constructors remain unchanged.

- [ ] **Step 2: Write one logical-error test per operation domain**

For Account, SNS, SSO, and User, fake a generated response with `errcode=40014` and assert `DingTalkErrorException` with code 40014. Do not accept an HTTP-success/raw-body-only assertion.

- [ ] **Step 3: Correct the User endpoint and add an endpoint assertion**

Expected endpoint:

```text
https://oapi.dingtalk.com/user/getuserinfo
```

The test factory records the endpoint passed by `DingTalkUserOperations#getUserInfoByCode` and asserts the slash is present.

- [ ] **Step 4: Add corrected Java naming aliases**

Add:

```java
public OapiUserGetuserinfoResponse getUserInfoByCode(String code, String accessToken)
        throws ApiException, DingTalkErrorException;
public OapiUserGetUseridByUnionidResponse getUserIdByUnionId(String unionId, String accessToken)
        throws ApiException, DingTalkErrorException;
public OapiUserGetResponse getUserByUserId(String userId, String accessToken)
        throws ApiException, DingTalkErrorException;
```

Keep `getUserinfoBycode`, `getUseridByUnionid`, and `getUserByUserid` deprecated and delegate to the corrected methods.

- [ ] **Step 5: Validate before returning extracted strings or bodies**

Patterns such as this are prohibited:

```java
return response.getBody();
```

They become:

```java
executeChecked(response, response.getErrcode(), response.getErrmsg());
return response.getBody();
```

- [ ] **Step 6: Run operation-domain regression tests**

```bash
./mvnw -B --no-transfer-progress \
  -Dtest=DingTalkAccountOperationsTest,DingTalkSnsOperationsTest,\
DingTalkSsoOperationsTest,DingTalkUserOperationsTest test
./mvnw -B --no-transfer-progress test
```

- [ ] **Step 7: Commit unified operation validation**

```bash
git add src/main/java/com/dingtalk/spring/boot/internal \
        src/main/java/com/dingtalk/spring/boot/DingTalk*Operations.java \
        src/test/java/com/dingtalk/spring/boot/DingTalk*OperationsTest.java
git commit -m "fix: validate all dingtalk api responses"
```

### Task 9: Replace the untested multi-service holder with a registry contract

**Files:**
- Create: `src/main/java/com/dingtalk/spring/boot/service/DingTalkServiceRegistry.java`
- Create: `src/main/java/com/dingtalk/spring/boot/service/DefaultDingTalkServiceRegistry.java`
- Modify: `src/main/java/com/dingtalk/spring/boot/service/DingTalkMultiServicesHolder.java`
- Create: `src/test/java/com/dingtalk/spring/boot/service/DefaultDingTalkServiceRegistryTest.java`

**Interfaces:**
- Consumes: concrete `DingTalkService` instances.
- Produces: explicit tenant registration and fail-fast lookup.

- [ ] **Step 1: Define the registry contract**

```java
public interface DingTalkServiceRegistry {
    DingTalkService register(String tenantId, DingTalkService service);
    Optional<DingTalkService> find(String tenantId);
    DingTalkService require(String tenantId);
    Optional<DingTalkService> remove(String tenantId);
    Map<String, DingTalkService> snapshot();
}
```

- [ ] **Step 2: Write registration and immutability tests**

Test replacement return value, unknown-tenant exception text, null rejection, removal, and that `snapshot()` cannot be mutated.

- [ ] **Step 3: Implement with `ConcurrentHashMap`**

Use `Map.copyOf(services)` for snapshots and `Objects.requireNonNull` for both key and service. Do not expose the live map.

- [ ] **Step 4: Convert the old holder to a deprecated adapter**

`DingTalkMultiServicesHolder` delegates to `DefaultDingTalkServiceRegistry`; do not maintain a second map. Existing `opsFor*` shortcuts call `registry.require(tenantId)`.

- [ ] **Step 5: Run registry tests and CodeGraph impact checks**

```bash
./mvnw -B --no-transfer-progress -Dtest=DefaultDingTalkServiceRegistryTest test
codegraph sync
codegraph impact DingTalkMultiServicesHolder
```

- [ ] **Step 6: Commit the registry abstraction**

```bash
git add src/main/java/com/dingtalk/spring/boot/service \
        src/test/java/com/dingtalk/spring/boot/service
git commit -m "refactor: replace multi service holder with registry"
```

---

## Milestone C: Make the Spring Boot Starter a Thin, Truthful Adapter

### Task 10: Collapse the starter to one facade bean graph

**Files:**
- Modify: `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter/src/main/java/com/dingtalk/spring/boot/DingTalkAutoConfiguration.java`
- Create: `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter/src/main/java/com/dingtalk/spring/boot/DingTalkPropertiesMapper.java`
- Delete: `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter/src/main/java/com/dingtalk/spring/boot/SpringDingTalkTemplate.java`
- Modify: `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter/src/test/java/com/dingtalk/spring/boot/DingTalkAutoConfigurationTest.java`

**Interfaces:**
- Consumes: SDK `DingTalkConfig`, `DingTalkConfigStorage`, `DingTalkAccessTokenProvider`, `DingTalkService`.
- Produces: one overridable `DingTalkService` bean and one shared storage graph.

- [ ] **Step 1: Update context tests before auto-configuration**

Assert exactly one service/facade graph:

```java
assertThat(context).hasSingleBean(DingTalkService.class);
assertThat(context).hasSingleBean(DingTalkConfigStorage.class);
assertThat(context).doesNotHaveBean("springDingTalkTemplate");
```

Add a custom `DingTalkService` bean test proving `@ConditionalOnMissingBean` backoff.

- [ ] **Step 2: Prove the current graph is duplicated**

Run:

```bash
cd /Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter
bash ./mvnw -B --no-transfer-progress -DskipTests=false \
  -Dtest=DingTalkAutoConfigurationTest test
```

Expected: FAIL on the new single-service expectations while both `DingTalkTemplate` and `SpringDingTalkTemplate` are registered.

- [ ] **Step 3: Extract property mapping without moving it into the SDK**

Create package-private:

```java
final class DingTalkPropertiesMapper {
    private DingTalkPropertiesMapper() {
    }

    static DingTalkConfig toConfig(DingTalkProperties properties) {
        DingTalkConfig config = new DingTalkConfig();
        config.setCorpId(properties.getCorpId());
        config.setCorpSecret(properties.getCorpSecret());
        config.setCorpApps(mapCorpApps(properties.getCorpApps()));
        config.setApps(mapPersonalApps(properties.getApps()));
        config.setSuites(mapSuites(properties.getSuites()));
        config.setLogins(mapLogins(properties.getLogins()));
        config.setRobots(mapRobots(properties.getRobots()));
        return config;
    }
}
```

Move the five existing mapping loops unchanged in field semantics. Add `DingTalkPropertiesMapperTest` with all five list types and null-list behavior.

- [ ] **Step 4: Register one storage-aware service**

The bean chain is:

```java
@Bean
@ConditionalOnMissingBean
DingTalkConfig dingTalkConfig(DingTalkProperties properties) {
    return DingTalkPropertiesMapper.toConfig(properties);
}

@Bean
@ConditionalOnMissingBean
DingTalkConfigStorage dingTalkConfigStorage() {
    return new InMemoryDingTalkConfigStorage();
}

@Bean
@ConditionalOnMissingBean
DingTalkAccessTokenProvider dingTalkAccessTokenProvider(
        DingTalkConfigProvider configProvider,
        DingTalkConfigStorage storage) {
    return new DefaultDingTalkAccessTokenProvider(configProvider, storage);
}

@Bean
@ConditionalOnMissingBean
DingTalkService dingTalkService(DingTalkConfigProvider configProvider,
                                DingTalkAccessTokenProvider tokenProvider,
                                DingTalkConfigStorage storage) {
    return new DefaultDingTalkService(configProvider, tokenProvider, storage);
}
```

Remove the six individual Operations beans. Users access them through the service facade.

- [ ] **Step 5: Delete `SpringDingTalkTemplate`**

SDK configuration now initializes itself, so no Spring lifecycle wrapper is needed. Remove tests and documentation that claim the unused `ApplicationContext` is passed to `DingTalkUserIdProvider`.

- [ ] **Step 6: Run Starter focused and full tests**

```bash
bash ./mvnw -B --no-transfer-progress -DskipTests=false \
  -Dtest=DingTalkPropertiesMapperTest,DingTalkAutoConfigurationTest test
bash ./mvnw -B --no-transfer-progress test -DskipTests=false
```

- [ ] **Step 7: Commit the one-graph auto-configuration**

```bash
git add src/main/java/com/dingtalk/spring/boot \
        src/test/java/com/dingtalk/spring/boot
git commit -m "refactor: create one dingtalk service graph"
```

### Task 11: Remove residual domain duplication and repair Starter build hygiene

**Files:**
- Delete: `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter/src/test/java/com/dingtalk/spring/boot/DingTalkUserIdProviderTest.java`
- Delete: `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter/src/test/java/com/dingtalk/spring/boot/TicketTypeTest.java`
- Modify: `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter/pom.xml`
- Modify mode: `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter/mvnw`
- Modify: Starter English and Chinese README files

**Interfaces:**
- Consumes: current SDK artifact.
- Produces: Spring-only test suite and executable Maven wrapper.

- [ ] **Step 1: Delete the two duplicated SDK-domain tests**

Before deleting, verify the SDK owns equivalent tests:

```bash
test -f /Users/wandl/workspaces/workspace-github-easy-4-java/dingtalk-sdk-extension/src/test/java/com/dingtalk/spring/boot/DingTalkUserIdProviderTest.java
test -f /Users/wandl/workspaces/workspace-github-easy-4-java/dingtalk-sdk-extension/src/test/java/com/dingtalk/spring/boot/TicketTypeTest.java
```

- [ ] **Step 2: Simplify Starter dependencies**

Retain compile dependencies only when directly used:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>dingtalk-sdk-extension</artifactId>
    <version>${dingtalk-sdk-extension.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

Keep `spring-boot-starter-test` test-scoped. Remove the redundant `spring-boot-starter` plus logging exclusion if no remaining main code uses it. Do not claim Lombok is supplied transitively by the SDK.

- [ ] **Step 3: Repair the wrapper executable mode**

```bash
chmod +x mvnw
git update-index --chmod=+x mvnw
./mvnw -version
```

Expected: direct execution succeeds with Maven 4.0.0-rc-5 and Java 21.

- [ ] **Step 4: Update Starter documentation**

README examples inject `DingTalkService`, show `dingtalk.*` property binding, and explicitly state that SDK domain types come from `dingtalk-sdk-extension`. Remove claims that all six operation objects are independent Spring beans.

- [ ] **Step 5: Run Starter verification**

```bash
./mvnw -B --no-transfer-progress test -DskipTests=false
```

Expected: only Starter binding/auto-configuration tests run; no SDK-domain duplicate tests remain.

- [ ] **Step 6: Commit Starter hygiene**

```bash
git add pom.xml mvnw README.md README.zh-CN.md src/test/java
git commit -m "chore: keep dingtalk starter spring-only"
```

---

## Milestone D: Next-Major Namespace and Public API Migration

### Task 12: Move the SDK out of the vendor/Spring namespace

**Files:**
- Create/move: all production types under `src/main/java/io/github/easy4j/dingtalk/`
- Create/move: matching tests under `src/test/java/io/github/easy4j/dingtalk/`
- Modify: SDK `pom.xml` Maven JAR manifest configuration
- Modify: both SDK README files

**Interfaces:**
- Consumes: behavior-stable 3.x SDK from Milestones A-B.
- Produces: SDK public namespace `io.github.easy4j.dingtalk` with no `.spring.boot` segment.

- [ ] **Step 1: Generate a package inventory before moving files**

```bash
rg -n '^package com\.dingtalk\.spring\.boot' src/main/java src/test/java
rg -n '^import com\.dingtalk\.spring\.boot' src/main/java src/test/java
```

Save counts in the task notes. Every matching production and test type must appear in the move map.

- [ ] **Step 2: Apply the exact package mapping**

| Old package | New package |
|---|---|
| `com.dingtalk.spring.boot.service` | `io.github.easy4j.dingtalk.service` |
| `com.dingtalk.spring.boot.config` | `io.github.easy4j.dingtalk.config` |
| `com.dingtalk.spring.boot.spi.storage` | `io.github.easy4j.dingtalk.storage` |
| `com.dingtalk.spring.boot.bean` message types | `io.github.easy4j.dingtalk.model.message` |
| `com.dingtalk.spring.boot.bean.JsapiTicketSignature` | `io.github.easy4j.dingtalk.model.jsapi` |
| `com.dingtalk.spring.boot.error` | `io.github.easy4j.dingtalk.error` |
| `com.dingtalk.spring.boot.internal` | `io.github.easy4j.dingtalk.internal` |
| `com.dingtalk.spring.boot.utils` | domain-specific classes under `internal` |

Use `apply_patch`-based file moves/edits during execution; do not perform unreviewed global text replacement.

- [ ] **Step 3: Rename public types while moving**

| Old type | New type |
|---|---|
| `DingTalkOperations` | `AbstractDingTalkService` |
| `DingTalkAccountOperations` | `DingTalkAccountService` |
| `DingTalkSnsOperations` | `DingTalkSnsService` |
| `DingTalkSsoOperations` | `DingTalkSsoService` |
| `DingTalkUserOperations` | `DingTalkUserService` |
| `DingTalkRobotOperations` | `DingTalkRobotService` |
| `DingTalkJsapiOperations` | `DingTalkJsapiService` |
| `DingTalkErrorCodeEnum` | `DingTalkErrorCode` |
| `DingTalkErrorException` | `DingTalkApiException` |
| `DingTalkMultiServicesHolder` | removed after registry migration |
| `DingTalkUtils` | `io.github.easy4j.dingtalk.internal.JsapiSignatureGenerator` |
| `RandomUtils` | `io.github.easy4j.dingtalk.internal.NonceGenerator` |

The compatibility decision is fixed for this plan: 3.x contains the deprecated adapters created in Tasks 4, 8, and 9; the 4.0 namespace migration removes those old-package adapters completely. No second compatibility artifact is produced.

Move the remaining root types with these exact destinations:

| Current type | 4.0 destination |
|---|---|
| `DingTalkConfig` | `io.github.easy4j.dingtalk.config.DingTalkConfig` |
| `DingTalkConfigProvider` | `io.github.easy4j.dingtalk.config.DingTalkConfigProvider` |
| `DefaultDingTalkConfigProvider` | `io.github.easy4j.dingtalk.config.impl.DefaultDingTalkConfigProvider` |
| `DingTalkAccessTokenProvider` | `io.github.easy4j.dingtalk.service.DingTalkAccessTokenProvider` |
| `DefaultDingTalkAccessTokenProvider` | `io.github.easy4j.dingtalk.service.impl.DefaultDingTalkAccessTokenProvider` |
| `DingTalkService` | `io.github.easy4j.dingtalk.service.DingTalkService` |
| `DefaultDingTalkService` | `io.github.easy4j.dingtalk.service.impl.DefaultDingTalkService` |
| `DingTalkServiceRegistry` | `io.github.easy4j.dingtalk.registry.DingTalkServiceRegistry` |
| `DefaultDingTalkServiceRegistry` | `io.github.easy4j.dingtalk.registry.DefaultDingTalkServiceRegistry` |

Set the JAR manifest entry in `pom.xml`:

```xml
<manifestEntries>
    <Automatic-Module-Name>io.github.easy4j.dingtalk</Automatic-Module-Name>
</manifestEntries>
```

- [ ] **Step 4: Run namespace guard tests**

Create `NamespaceArchitectureTest` that scans compiled SDK classes and fails when a production class package starts with `com.dingtalk.spring.boot` or references `org.springframework`.

```java
assertTrue(productionClasses.stream()
        .noneMatch(type -> type.getName().startsWith("com.dingtalk.spring.boot")));
assertTrue(productionClasses.stream()
        .flatMap(type -> Arrays.stream(type.getDeclaredMethods()))
        .noneMatch(method -> method.getReturnType().getName().startsWith("org.springframework")));
```

- [ ] **Step 5: Run full SDK verification and JAR inspection**

```bash
./mvnw -B --no-transfer-progress clean verify
if jar tf target/dingtalk-sdk-extension-*.jar \
    | rg 'com/dingtalk/spring/boot|org/springframework'; then
  echo "legacy or Spring-owned class leaked into SDK jar" >&2
  exit 1
fi
```

Expected: `verify` succeeds and the JAR search returns no SDK-owned old-package/Spring classes.

- [ ] **Step 6: Commit the major namespace migration**

```bash
git add pom.xml README.md README.zh-CN.md src/main src/test
git commit -m "refactor!: move dingtalk sdk to easy4j namespace"
```

### Task 13: Move the Starter into its own auto-configuration namespace

**Files:**
- Move: Starter `DingTalkAutoConfiguration` → `io.github.easy4j.dingtalk.spring.boot.autoconfigure`
- Move: Starter `DingTalkProperties` → `io.github.easy4j.dingtalk.spring.boot.properties`
- Move: Starter `DingTalkPropertiesMapper` → `io.github.easy4j.dingtalk.spring.boot.autoconfigure`
- Modify: `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Delete: `src/main/resources/META-INF/spring.factories`
- Delete: `src/main/resources/META-INF/spring-autoconfigure-metadata.properties`
- Modify: Starter tests and README files

**Interfaces:**
- Consumes: next-major SDK packages from Task 12.
- Produces: a clearly framework-owned package that never occupies the SDK root.

- [ ] **Step 1: Update package-level context tests**

```java
assertThat(context).hasSingleBean(io.github.easy4j.dingtalk.service.DingTalkService.class);
assertThat(context).hasSingleBean(
        io.github.easy4j.dingtalk.spring.boot.properties.DingTalkProperties.class);
```

- [ ] **Step 2: Move the three Starter classes and update imports**

Do not move `DingTalkProperties` into the SDK. Its nested property classes remain Spring configuration-binding DTOs.

- [ ] **Step 3: Update auto-configuration registration exactly**

`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` contains:

```text
io.github.easy4j.dingtalk.spring.boot.autoconfigure.DingTalkAutoConfiguration
```

Delete `META-INF/spring.factories` from this Boot 4.1.x branch. Keep `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` as its only auto-configuration registration mechanism.

- [ ] **Step 4: Run metadata and context verification**

```bash
./mvnw -B --no-transfer-progress clean test -DskipTests=false
test -f target/classes/META-INF/spring-configuration-metadata.json
rg -n 'io.github.easy4j.dingtalk.spring.boot.autoconfigure.DingTalkAutoConfiguration' \
  target/classes/META-INF
```

- [ ] **Step 5: Commit the Starter package migration**

```bash
git add README.md README.zh-CN.md src/main src/test
git commit -m "refactor!: move dingtalk starter to easy4j namespace"
```

---

## Milestone E: Cross-Repository Release Gates

### Task 14: Prove the SDK and Starter work as one current source pair

**Files:**
- Modify: SDK `.github/workflows/ci.yml`
- Create: SDK `scripts/verify-starter-integration.sh`
- Modify: `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter/.github/workflows/ci.yml`
- Modify: SDK and Starter README build sections

**Interfaces:**
- Consumes: current SDK source and current Starter source.
- Produces: a repeatable source-pair verification that cannot consume a stale local SDK snapshot.

- [ ] **Step 1: Create a source-pair verification script**

The script contains:

```bash
#!/usr/bin/env bash
set -euo pipefail

sdk_dir=$(cd "$(dirname "$0")/.." && pwd -P)
starter_dir=${DINGTALK_STARTER_DIR:?Set DINGTALK_STARTER_DIR to the starter checkout}
maven_repo=$(mktemp -d)
trap 'rm -rf "$maven_repo"' EXIT

cd "$sdk_dir"
./mvnw -B --no-transfer-progress -Dmaven.repo.local="$maven_repo" clean install

cd "$starter_dir"
bash ./mvnw -B --no-transfer-progress -Dmaven.repo.local="$maven_repo" \
  clean test -DskipTests=false
```

Do not hard-code a developer home path inside the committed script.

- [ ] **Step 2: Prove stale local artifacts cannot satisfy the build**

The script uses the same empty `mktemp -d` Maven repository for SDK installation and Starter resolution. Add this assertion immediately after the SDK install:

```bash
sdk_version=$(./mvnw -q -DforceStdout help:evaluate -Dexpression=project.version)
test -f "$maven_repo/io/github/easy4j/dingtalk-sdk-extension/$sdk_version/dingtalk-sdk-extension-$sdk_version.pom"
```

Because no artifact exists in the temporary repository before the SDK command, an older developer snapshot cannot make this gate pass.

- [ ] **Step 3: Use the Maven wrapper in SDK CI**

Replace system Maven invocation:

```yaml
- name: Build and verify SDK
  run: ./mvnw -B --no-transfer-progress clean verify
```

This is required because the POM uses model version 4.1.0 and system Maven 3 cannot read it.

- [ ] **Step 4: Run the complete local gate**

```bash
cd /Users/wandl/workspaces/workspace-github-easy-4-java/dingtalk-sdk-extension
DINGTALK_STARTER_DIR=/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot-starters/dingtalk-spring-boot-starter \
  ./scripts/verify-starter-integration.sh
```

Expected:

```text
SDK: BUILD SUCCESS, full test count reported
Starter: BUILD SUCCESS, Spring-only test count reported
```

- [ ] **Step 5: Run static and graph hygiene checks**

```bash
codegraph sync
codegraph status
git diff --check
rg -n 'com\.dingtalk\.spring\.boot|DingTalkProperties' src/main/java
```

Interpret the last command according to milestone: old-package hits are permitted only in explicit 3.x deprecated adapters and forbidden after Task 12.

- [ ] **Step 6: Commit the verification gate**

```bash
git add .github/workflows/ci.yml scripts/verify-starter-integration.sh README.md README.zh-CN.md
git commit -m "ci: verify dingtalk sdk and starter source pair"
```

## Final Acceptance Checklist

- [ ] SDK `./mvnw clean verify` succeeds on JDK 21/Maven 4 and reports executed tests.
- [ ] Starter `./mvnw clean test -DskipTests=false` succeeds against the SDK installed from the same source checkout.
- [ ] `DingTalkTemplate` construction no longer throws or relies on an unsafe cast.
- [ ] Exactly one `DingTalkService` graph exists per configured tenant.
- [ ] Token and JSAPI ticket cache concurrency tests prove one remote refresh per expired key.
- [ ] Every operation domain has a non-zero `errcode` test.
- [ ] Robot request-shape tests cover all five message types.
- [ ] SDK contains no production `property/*Properties` classes.
- [ ] Starter contains no SDK-domain implementation or duplicated SDK-domain tests.
- [ ] Secrets and tokens are absent from logs and assertion failure messages.
- [ ] CodeGraph is synchronized and `codegraph affected` tests have been executed.
- [ ] `git diff --check` returns no errors in both repositories.
- [ ] README examples compile against the final public API.
- [ ] Package migration occurs only at the approved major-version gate.

## Plan Self-Review Record

- Spec coverage: all ten baseline requirements map to Tasks 2-14; no requirement depends on an unassigned task.
- Placeholder scan: the plan contains no unresolved placeholder token, deferred implementation marker, or generic test instruction without an exact behavior and command.
- Type consistency: `DingTalkService`, `DefaultDingTalkService`, `DingTalkConfigStorage`, `InMemoryDingTalkConfigStorage`, and `DingTalkServiceRegistry` names are used consistently from production tasks through Starter and CI tasks.
- Scope boundary: Redis-family implementations and callback routing remain explicit non-goals; they do not block a production-quality in-memory SDK foundation.
