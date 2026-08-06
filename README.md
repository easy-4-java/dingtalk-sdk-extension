# dingtalk-sdk-extension

[![Java](https://img.shields.io/badge/Java-21-orange)] [![License](https://img.shields.io/badge/license-Apache%202.0-green)](https://www.apache.org/licenses/LICENSE-2.0.txt)

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
| `com.dingtalk.spring.boot` | `DingTalkTemplate`, `DingTalkProperties`, `*Operations`, providers, `JsapiTicketSignature` |
| `com.dingtalk.spring.boot.bean` | `BaseMessage` + `TextMessage` / `MarkdownMessage` / `LinkMessage` / `ActionCardMessage` / `FeedCardMessage`, `MessageType` |
| `com.dingtalk.spring.boot.property` | `DingTalkCorpAppProperties`, `DingTalkPersonalMiniAppProperties`, `DingTalkSuiteProperties`, `DingTalkLoginProperties`, `DingTalkRobotProperties` |

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
import com.dingtalk.spring.boot.*;
import com.dingtalk.spring.boot.property.DingTalkRobotProperties;
import com.taobao.api.ApiException;

import java.util.Collections;

// 1. configure one robot
DingTalkRobotProperties robot = new DingTalkRobotProperties();
robot.setRobotId("ops-alert");
robot.setAccessToken("your-robot-access-token");
robot.setSecretToken("SEC...");                    // for signed requests

DingTalkProperties props = new DingTalkProperties();
props.setCorpId("your-corp-id");
props.setRobots(Collections.singletonList(robot));

// 2. wire providers + template
DingTalkConfigProvider configProvider = new DefaultDingTalkConfigProvider(props);
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

`DingTalkProperties` defines the property prefix `dingtalk` (constant
`DingTalkProperties.PREFIX`). It is a plain POJO — bind it in Spring Boot with
`@ConfigurationProperties(prefix = "dingtalk")`:

| Property (prefix `dingtalk`) | Type | Description |
| :--- | :--- | :--- |
| `corp-id` | String | Enterprise corpId |
| `corp-secret` | String | Enterprise secret |
| `corp-apps` | List\<DingTalkCorpAppProperties\> | In-house mini-program / H5 apps |
| `apps` | List\<DingTalkPersonalMiniAppProperties\> | Third-party personal mini-apps |
| `suites` | List\<DingTalkSuiteProperties\> | Third-party enterprise suites |
| `logins` | List\<DingTalkLoginProperties\> | Scan-login (mobile) apps |
| `robots` | List\<DingTalkRobotProperties\> | Group robots (`robotId`, `accessToken`, `secretToken`) |

Example (`application.yml`):

```yaml
dingtalk:
  corp-id: your-corp-id
  robots:
    - robot-id: ops-alert
      access-token: your-robot-access-token
      secret-token: SEC...
```

## 8. Core Usage / API

### 8.1 Typed robot messages

```java
import com.dingtalk.spring.boot.bean.MarkdownMessage;

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
