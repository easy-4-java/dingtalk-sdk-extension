# dingtalk-sdk-extension

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-21-orange)](https://github.com/easy-4-java/dingtalk-sdk-extension) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](https://www.apache.org/licenses/LICENSE-2.0.txt)

> Extension SDK for the DingTalk (钉钉) Open Platform: template-based operations for
> robots, JSAPI, user, SNS login, SSO and account management, built on the official
> `alibaba-dingtalk-service-sdk`.

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`dingtalk-sdk-extension` is a Java extension layer on top of the official DingTalk
Open Platform SDK (`com.aliyun:alibaba-dingtalk-service-sdk`). It provides a
template-based, multi-app programming model for server-side DingTalk integration:

- **`DingTalkTemplate`** — central facade exposing typed operation groups:
  `opsForRobot()`, `opsForJsapi()`, `opsForUser()`, `opsForSns()`, `opsForSso()`,
  `opsForAccount()`, plus access-token helpers and robot signature computation.
- **Robot messages** — typed message beans (`TextMessage`, `MarkdownMessage`,
  `LinkMessage`, `ActionCardMessage`, `FeedCardMessage`) sent through
  `DingTalkRobotOperations` with signing support (`getSign`).
- **Multi-app configuration** — `DingTalkProperties` (prefix `dingtalk`) models
  corp apps, personal mini-apps, suites, login apps and robots as lists; providers
  resolve credentials per `corpId` / `appKey`.

What it is **not**:

- Not a replacement for the official DingTalk SDK — it extends it; the official
  artifacts (`com.dingtalk.api.*`, `com.taobao.api.*`) are used underneath.
- Not a Spring Boot starter — `DingTalkProperties` is a plain POJO you bind
  yourself (`@ConfigurationProperties` works because the class is a simple bean).

Typical scenarios:

| Scenario | What you use |
| :--- | :--- |
| Send a signed text / markdown message to a group robot | `template.opsForRobot().sendTextMessage(...)` / `sendMessage(corpId, robotId, BaseMessage)` |
| Sign a group-robot request | `template.getSign(secret, timestamp)` |
| JSAPI ticket + signature for H5 pages | `template.opsForJsapi().getTicket(...)` / `createSignature(url, agentId, accessToken)` |
| Get user info by OAuth code | `template.opsForUser().getUserinfoByCode(code, accessToken)` |
| SNS scan-login flow | `template.opsForSns().getUserinfoByTmpCode(...)` |
| Access tokens for many apps | `template.getAccessToken(corpId, appKey)` / `getSnsAccessToken(corpId, appId)` |

## 2. Features & Status

| Capability | Status | Notes |
| :--- | :--- | :--- |
| `DingTalkTemplate` facade | Stable | `opsForRobot` / `opsForJsapi` / `opsForUser` / `opsForSns` / `opsForSso` / `opsForAccount`, token helpers, `getSign` |
| Robot message operations | Stable | `sendMessage` (typed bean or `OapiRobotSendRequest`), `sendTextMessage` variants, `getUserMobile` |
| Typed robot message beans | Stable | `TextMessage`, `MarkdownMessage`, `LinkMessage`, `ActionCardMessage`, `FeedCardMessage`, `MessageType` |
| JSAPI operations | Stable | `getTicket(TicketType, accessToken)`, `createSignature(url, agentId, accessToken)` |
| User / SNS / SSO / account operations | Stable | `getUserinfoByCode`, `getUserinfoByTmpCode`, `getSnsToken`, `getPersistentCode` |
| Multi-app configuration model | Stable | `DingTalkProperties` (prefix `dingtalk`) + `DingTalkCorpAppProperties` / `DingTalkPersonalMiniAppProperties` / `DingTalkSuiteProperties` / `DingTalkLoginProperties` / `DingTalkRobotProperties` |
| Credential providers | Stable | `DingTalkConfigProvider` / `DefaultDingTalkConfigProvider`, `DingTalkAccessTokenProvider` / `DefaultDingTalkAccessTokenProvider` |

## 3. Requirements & Compatibility

| Requirement | Version / Notes |
| :--- | :--- |
| JDK | 21+ |
| Maven | 3.0+ (enforced; Maven Wrapper `./mvnw` included) |
| DingTalk Open Platform SDK | `com.aliyun:alibaba-dingtalk-service-sdk` (managed by this pom) |
| DingTalk credentials | App Key/Secret (or robot access token + secret) from the DingTalk developer console |

Version lines:

| Branch | JDK | Version |
| :--- | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

## 4. Architecture & Modules

```text
+------------------+   +------------------------------------------+
| Application      |   | dingtalk-sdk-extension                  |
| (corpId/appKey/  |-->|  DingTalkTemplate (facade)              |
|  credentials)    |   |    opsFor* : Robot / Jsapi / User /     |
|                  |   |              Sns / Sso / Account        |
|                  |   |    providers: Config / AccessToken      |
|                  |   |    beans    : Text / Markdown / Link /  |
+------------------+   |              ActionCard / FeedCard      |
                       +-------------------+----------------------+
                                           |
                                           v
                     +-------------------------------------------+
                     | Official DingTalk SDK (com.dingtalk.api)  |
                     +-------------------+----------------------+
                                           |
                                           v
                     +-------------------------------------------+
                     | oapi.dingtalk.com (robot webhook / REST)  |
                     +-------------------------------------------+
```

Single-module Maven project (`packaging: jar`). No child modules.

| Artifact | Responsibility |
| :--- | :--- |
| `io.github.easy4j:dingtalk-sdk-extension` | Template facade, operations, message beans, providers, properties |

Key packages:

| Package | Content |
| :--- | :--- |
| `io.github.easy4j.dingtalk.config` / `config.impl` | `DingTalkConfig`, 5 typed configs (CorpApp/PersonalMiniApp/Suite/Login/Robot), `DingTalkConfigProvider`, `DefaultDingTalkConfigProvider` |
| `io.github.easy4j.dingtalk.service` / `service.impl` | `DingTalkService` (5 `opsFor*`), `AbstractDingTalkService`, `DefaultDingTalkService`, 6 typed services (Account/Sns/Sso/User/Robot/Jsapi), `DingTalkAccessTokenProvider` |
| `io.github.easy4j.dingtalk.registry` | `DingTalkServiceRegistry`, `DefaultDingTalkServiceRegistry` |
| `io.github.easy4j.dingtalk.storage` | `DingTalkConfigStorage`, `InMemoryDingTalkConfigStorage` |
| `io.github.easy4j.dingtalk.model.message` | `BaseMessage` + `TextMessage` / `MarkdownMessage` / `LinkMessage` / `ActionCardMessage` / `FeedCardMessage`, `MessageType` |
| `io.github.easy4j.dingtalk.model.jsapi` | `JsapiTicketSignature`, `TicketType` |
| `io.github.easy4j.dingtalk.error` | `ErrorCode`, `DingTalkApiException` |
| `io.github.easy4j.dingtalk.internal` | `JsapiSignatureGenerator`, `NonceGenerator` |

## 5. Installation

The project is **not yet published to Maven Central**. Snapshots/releases are
distributed through the Aliyun Maven repository and GitHub Releases.

Maven:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>dingtalk-sdk-extension</artifactId>
    <version>3.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.easy4j:dingtalk-sdk-extension:3.0.x.x.20260630-SNAPSHOT'
```

The official `alibaba-dingtalk-service-sdk` is resolved as a regular dependency.

## 6. Quick Start

Send a signed text message to a group robot:

```java
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import java.util.Collections;

import io.github.easy4j.dingtalk.config.DingTalkConfig;
import io.github.easy4j.dingtalk.config.DingTalkRobotConfig;
import io.github.easy4j.dingtalk.config.impl.DefaultDingTalkConfigProvider;
import io.github.easy4j.dingtalk.service.DingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.service.DingTalkTemplate;
import io.github.easy4j.dingtalk.service.impl.DefaultDingTalkAccessTokenProvider;

// 1. configure one robot via DingTalkConfig
DingTalkRobotConfig robot = new DingTalkRobotConfig();
robot.setRobotId("ops-alert");
robot.setAccessToken("your-robot-access-token");
robot.setSecretToken("SEC...");                    // for signed requests

DingTalkConfig cfg = new DingTalkConfig();
cfg.setCorpId("your-corp-id");
cfg.setRobots(Collections.singletonList(robot));

// 2. wire providers + service (use deprecated DingTalkTemplate for migration only)
DefaultDingTalkConfigProvider configProvider = new DefaultDingTalkConfigProvider(cfg);
DingTalkAccessTokenProvider tokenProvider = new DefaultDingTalkAccessTokenProvider(configProvider);
DingTalkTemplate template = new DingTalkTemplate(configProvider, tokenProvider);

// 3. send
OapiRobotSendResponse resp = template.opsForRobot()
        .sendTextMessage("your-corp-id", "ops-alert", "Hello from Java");
System.out.println(resp.getErrcode() + " / " + resp.getErrmsg());
```

Expected result: the robot posts the text message to the group; `errcode == 0`
means success (the webhook URL is built from the configured access token and a
timestamp-based HMAC-SHA256 signature).

## 7. Configuration

`DingTalkConfig` is the SDK-native configuration POJO. It carries five nested typed lists
(`corpApps`, `apps`, `suites`, `logins`, `robots`) plus the enterprise-wide `corpId` and
`corpSecret`. Pass the instance to `DefaultDingTalkConfigProvider` or implement
`DingTalkConfigProvider` to load values from any source.

| DingTalkConfig field | Type | Description |
| :--- | :--- | :--- |
| `corpId` | String | Enterprise corpId |
| `corpSecret` | String | Enterprise secret |
| `corpApps` | List\<DingTalkCorpAppConfig\> | In-house mini-program / H5 apps |
| `apps` | List\<DingTalkPersonalMiniAppConfig\> | Third-party personal mini-apps |
| `suites` | List\<DingTalkSuiteConfig\> | Third-party enterprise suites |
| `logins` | List\<DingTalkLoginConfig\> | Scan-login (mobile) apps |
| `robots` | List\<DingTalkRobotConfig\> | Group robots (`robotId`, `accessToken`, `secretToken`) |

Example (plain Java):

```java
DingTalkConfig cfg = new DingTalkConfig();
cfg.setCorpId("your-corp-id");
DingTalkRobotConfig robot = new DingTalkRobotConfig();
robot.setRobotId("ops-alert");
robot.setAccessToken("your-robot-access-token");
robot.setSecretToken("SEC...");
cfg.setRobots(Collections.singletonList(robot));
```

## 8. Core Usage / API

### 8.1 Typed robot messages

```java
import io.github.easy4j.dingtalk.model.message.MarkdownMessage;

MarkdownMessage msg = new MarkdownMessage("Deploy Notice", "**release v1.2.0** finished", true);
OapiRobotSendResponse resp = template.opsForRobot().sendMessage("corp-1", "ops-alert", msg);
```

### 8.2 JSAPI ticket and signature (H5 integration)

```java
OapiGetJsapiTicketResponse ticket = template.opsForJsapi().getTicket(TicketType.JSAPI, accessToken);
JsapiTicketSignature signature = template.opsForJsapi().createSignature(pageUrl, agentId, accessToken);
```

### 8.3 Token helpers

```java
String corpToken = template.getAccessToken(corpId, appKey);    // in-house app
String snsToken  = template.getSnsAccessToken(corpId, appId);  // SNS login app
String sign      = template.getSign(robotSecret, System.currentTimeMillis());
```

## 9. Testing & Build

```bash
./mvnw clean verify
```

- The build is configured with the JaCoCo Maven plugin (report + `check` goal with a
  90% line-coverage rule bound to the `verify` phase; `haltOnFailure=false`).
- **Assumption**: the 1.0.x branch currently checks in no test sources under
  `src/test`; coverage thresholds are therefore enforced only when tests exist.
- No CI workflow files are present under `.github/` in this worktree.

## 10. Versioning & Branches

| Branch | JDK | Version | Notes |
| :--- | :--- | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` | Current branch, JDK 8 baseline, maintained |
| `feature/2.0.x` | 17 | `2.0.x.*` | JDK 17 line |
| `feature/3.0.x` | 21 | `3.0.x.*` | JDK 21 line |

Maintenance policy: the `1.0.x` line receives bug fixes and compatibility updates
for the JDK 8 baseline. New features targeting newer JDKs land on the `2.0.x` /
`3.0.x` lines. Releases are published to the Aliyun Maven repository and as
GitHub Releases; the project is not yet published to Maven Central.

## 11. Contributing & License

Contributions are welcome — please open issues or pull requests on GitHub.

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).
