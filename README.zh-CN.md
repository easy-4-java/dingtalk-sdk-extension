# dingtalk-sdk-extension

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-21-orange)](https://github.com/easy-4-java/dingtalk-sdk-extension) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](https://www.apache.org/licenses/LICENSE-2.0.txt)

> 钉钉开放平台的 Java 扩展 SDK：基于官方 `alibaba-dingtalk-service-sdk`，提供
> 模板化的机器人、JSAPI、用户、SNS 登录、SSO 与账号管理操作。

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 功能与状态](#2-功能与状态)
- [3. 环境要求与兼容性](#3-环境要求与兼容性)
- [4. 架构与模块](#4-架构与模块)
- [5. 安装](#5-安装)
- [6. 快速开始](#6-快速开始)
- [7. 配置](#7-配置)
- [8. 核心用法 / API](#8-核心用法--api)
- [9. 测试与构建](#9-测试与构建)
- [10. 版本与分支](#10-版本与分支)
- [11. 贡献与许可](#11-贡献与许可)

## 1. 项目概述

`dingtalk-sdk-extension` 是在钉钉官方开放平台 SDK（`com.aliyun:alibaba-dingtalk-service-sdk`）
之上的 Java 扩展层，为服务端钉钉集成提供基于模板、支持多应用编程模型：

- **`DingTalkTemplate`** — 核心门面，暴露类型化操作组：`opsForRobot()`、
  `opsForJsapi()`、`opsForUser()`、`opsForSns()`、`opsForSso()`、
  `opsForAccount()`，以及 access_token 辅助与机器人签名计算。
- **机器人消息** — 类型化消息 bean（`TextMessage`、`MarkdownMessage`、
  `LinkMessage`、`ActionCardMessage`、`FeedCardMessage`），通过
  `DingTalkRobotOperations` 发送，支持加签（`getSign`）。
- **多应用配置** — `DingTalkProperties`（前缀 `dingtalk`）把企业内部应用、个人
  小程序、套件、登录应用与机器人建模为列表；Provider 按 `corpId` / `appKey`
  解析凭据。

它不是：

- 官方钉钉 SDK 的替代品——它是扩展层；底层仍使用官方构件
  （`com.dingtalk.api.*`、`com.taobao.api.*`）。
- Spring Boot starter——`DingTalkProperties` 是纯 POJO，由你自行绑定
  （类本身是简单 bean，可直接配合 `@ConfigurationProperties`）。

典型场景：

| 场景 | 使用内容 |
| :--- | :--- |
| 向群机器人发送加签的文本 / markdown 消息 | `template.opsForRobot().sendTextMessage(...)` / `sendMessage(corpId, robotId, BaseMessage)` |
| 群机器人请求签名 | `template.getSign(secret, timestamp)` |
| H5 页面 JSAPI ticket 与签名 | `template.opsForJsapi().getTicket(...)` / `createSignature(url, agentId, accessToken)` |
| 通过 OAuth code 获取用户信息 | `template.opsForUser().getUserinfoByCode(code, accessToken)` |
| SNS 扫码登录流程 | `template.opsForSns().getUserinfoByTmpCode(...)` |
| 多应用的 access_token | `template.getAccessToken(corpId, appKey)` / `getSnsAccessToken(corpId, appId)` |

## 2. 功能与状态

| 能力 | 状态 | 说明 |
| :--- | :--- | :--- |
| `DingTalkTemplate` 门面 | 稳定 | `opsForRobot` / `opsForJsapi` / `opsForUser` / `opsForSns` / `opsForSso` / `opsForAccount`、token 辅助、`getSign` |
| 机器人消息操作 | 稳定 | `sendMessage`（类型化 bean 或 `OapiRobotSendRequest`）、`sendTextMessage` 变体、`getUserMobile` |
| 类型化机器人消息 bean | 稳定 | `TextMessage`、`MarkdownMessage`、`LinkMessage`、`ActionCardMessage`、`FeedCardMessage`、`MessageType` |
| JSAPI 操作 | 稳定 | `getTicket(TicketType, accessToken)`、`createSignature(url, agentId, accessToken)` |
| 用户 / SNS / SSO / 账号操作 | 稳定 | `getUserinfoByCode`、`getUserinfoByTmpCode`、`getSnsToken`、`getPersistentCode` |
| 多应用配置模型 | 稳定 | `DingTalkProperties`（前缀 `dingtalk`）+ `DingTalkCorpAppProperties` / `DingTalkPersonalMiniAppProperties` / `DingTalkSuiteProperties` / `DingTalkLoginProperties` / `DingTalkRobotProperties` |
| 凭据 Provider | 稳定 | `DingTalkConfigProvider` / `DefaultDingTalkConfigProvider`、`DingTalkAccessTokenProvider` / `DefaultDingTalkAccessTokenProvider` |

## 3. 环境要求与兼容性

| 要求 | 版本 / 说明 |
| :--- | :--- |
| JDK | 21+ |
| Maven | 3.0+（enforcer 强制；项目内置 Maven Wrapper `./mvnw`） |
| 钉钉开放平台 SDK | `com.aliyun:alibaba-dingtalk-service-sdk`（由本 pom 管理） |
| 钉钉凭据 | 钉钉开发者后台的应用 Key/Secret（或机器人 access token 与加签 secret） |

版本线：

| 分支 | JDK | 版本 |
| :--- | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

## 4. 架构与模块

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

单模块 Maven 工程（`packaging: jar`），无子模块。

| 构件 | 职责 |
| :--- | :--- |
| `io.github.easy4j:dingtalk-sdk-extension` | 模板门面、操作类、消息 bean、Provider、属性模型 |

关键包：

| 包 | 内容 |
| :--- | :--- |
| `com.dingtalk.spring.boot` | `DingTalkTemplate`、`DingTalkProperties`、`*Operations`、Provider、`JsapiTicketSignature` |
| `com.dingtalk.spring.boot.bean` | `BaseMessage` + `TextMessage` / `MarkdownMessage` / `LinkMessage` / `ActionCardMessage` / `FeedCardMessage`、`MessageType` |
| `com.dingtalk.spring.boot.property` | `DingTalkCorpAppProperties`、`DingTalkPersonalMiniAppProperties`、`DingTalkSuiteProperties`、`DingTalkLoginProperties`、`DingTalkRobotProperties` |

## 5. 安装

项目**尚未发布到 Maven Central**。快照 / 发布版本通过阿里云 Maven 仓库与 GitHub
Releases 分发。

Maven：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>dingtalk-sdk-extension</artifactId>
    <version>3.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.easy4j:dingtalk-sdk-extension:3.0.x.x.20260630-SNAPSHOT'
```

官方 `alibaba-dingtalk-service-sdk` 会作为常规依赖被解析。

## 6. 快速开始

向群机器人发送加签文本消息：

```java
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.dingtalk.spring.boot.*;
import com.dingtalk.spring.boot.property.DingTalkRobotProperties;
import com.taobao.api.ApiException;

import java.util.Collections;

// 1. 配置一个机器人
DingTalkRobotProperties robot = new DingTalkRobotProperties();
robot.setRobotId("ops-alert");
robot.setAccessToken("your-robot-access-token");
robot.setSecretToken("SEC...");                    // 加签请求使用

DingTalkProperties props = new DingTalkProperties();
props.setCorpId("your-corp-id");
props.setRobots(Collections.singletonList(robot));

// 2. 装配 Provider 与模板
DingTalkConfigProvider configProvider = new DefaultDingTalkConfigProvider(props);
DingTalkAccessTokenProvider tokenProvider = new DefaultDingTalkAccessTokenProvider(configProvider);
DingTalkTemplate template = new DingTalkTemplate(configProvider, tokenProvider);

// 3. 发送
OapiRobotSendResponse resp = template.opsForRobot()
        .sendTextMessage("your-corp-id", "ops-alert", "Hello from Java");
System.out.println(resp.getErrcode() + " / " + resp.getErrmsg());
```

预期结果：机器人把文本消息推送到群聊；`errcode == 0` 表示成功（webhook URL 由
配置的 access token 与基于时间戳的 HMAC-SHA256 签名拼接而成）。

## 7. 配置

`DingTalkProperties` 定义了属性前缀 `dingtalk`（常量 `DingTalkProperties.PREFIX`）。
它是纯 POJO——Spring Boot 中通过 `@ConfigurationProperties(prefix = "dingtalk")`
绑定：

| 属性（前缀 `dingtalk`） | 类型 | 说明 |
| :--- | :--- | :--- |
| `corp-id` | String | 企业 corpId |
| `corp-secret` | String | 企业密钥 |
| `corp-apps` | List\<DingTalkCorpAppProperties\> | 企业内部小程序 / H5 应用 |
| `apps` | List\<DingTalkPersonalMiniAppProperties\> | 第三方个人小程序 |
| `suites` | List\<DingTalkSuiteProperties\> | 第三方企业套件 |
| `logins` | List\<DingTalkLoginProperties\> | 扫码登录（移动接入）应用 |
| `robots` | List\<DingTalkRobotProperties\> | 群机器人（`robotId`、`accessToken`、`secretToken`） |

示例（`application.yml`）：

```yaml
dingtalk:
  corp-id: your-corp-id
  robots:
    - robot-id: ops-alert
      access-token: your-robot-access-token
      secret-token: SEC...
```

## 8. 核心用法 / API

### 8.1 类型化机器人消息

```java
import com.dingtalk.spring.boot.bean.MarkdownMessage;

MarkdownMessage msg = new MarkdownMessage("Deploy Notice", "**release v1.2.0** finished", true);
OapiRobotSendResponse resp = template.opsForRobot().sendMessage("corp-1", "ops-alert", msg);
```

### 8.2 JSAPI ticket 与签名（H5 集成）

```java
OapiGetJsapiTicketResponse ticket = template.opsForJsapi().getTicket(TicketType.JSAPI, accessToken);
JsapiTicketSignature signature = template.opsForJsapi().createSignature(pageUrl, agentId, accessToken);
```

### 8.3 Token 辅助

```java
String corpToken = template.getAccessToken(corpId, appKey);    // 企业内部应用
String snsToken  = template.getSnsAccessToken(corpId, appId);  // SNS 登录应用
String sign      = template.getSign(robotSecret, System.currentTimeMillis());
```

## 9. 测试与构建

```bash
./mvnw clean verify
```

- 构建配置了 JaCoCo Maven 插件（报告 + 绑定在 `verify` 阶段的 `check` 目标，
  行覆盖率规则为 90%；`haltOnFailure=false`）。
- **假设**：1.0.x 分支当前 `src/test` 下未提交测试源码；覆盖率门禁仅在存在测试时生效。
- 本 worktree 的 `.github/` 下无 CI 工作流文件。

## 10. 版本与分支

| 分支 | JDK | 版本 | 说明 |
| :--- | :--- | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` | 当前分支，JDK 8 基线，维护中 |
| `feature/2.0.x` | 17 | `2.0.x.*` | JDK 17 版本线 |
| `feature/3.0.x` | 21 | `3.0.x.*` | JDK 21 版本线 |

维护策略：`1.0.x` 版本线接收针对 JDK 8 基线的缺陷修复与兼容性更新；面向新 JDK 的
新特性在 `2.0.x` / `3.0.x` 版本线开发。发布物通过阿里云 Maven 仓库与 GitHub
Releases 分发；项目尚未发布到 Maven Central。

## 11. 贡献与许可

欢迎通过 GitHub Issue 或 Pull Request 参与贡献。

本项目基于 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) 许可。
