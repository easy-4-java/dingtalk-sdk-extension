/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.dingtalk.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import io.github.easy4j.dingtalk.model.message.ActionCardButton;
import io.github.easy4j.dingtalk.model.message.ActionCardMessage;
import io.github.easy4j.dingtalk.model.message.BaseMessage;
import io.github.easy4j.dingtalk.model.message.ButtonOrientationType;
import io.github.easy4j.dingtalk.model.message.FeedCardMessage;
import io.github.easy4j.dingtalk.model.message.FeedCardMessageItem;
import io.github.easy4j.dingtalk.model.message.HideAvatarType;
import io.github.easy4j.dingtalk.model.message.LinkMessage;
import io.github.easy4j.dingtalk.model.message.MarkdownMessage;
import io.github.easy4j.dingtalk.model.message.TextMessage;
import io.github.easy4j.dingtalk.config.DingTalkRobotConfig;
import io.github.easy4j.dingtalk.error.DingTalkApiException;
import io.github.easy4j.dingtalk.service.DingTalkService;
import com.taobao.api.ApiException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Operations for sending messages through DingTalk robot webhooks.
 * <p>Supports text, link, markdown, action card, and feed card message types.
 * Every robot-send call validates the DingTalk {@code errcode} via
 * {@link #executeChecked(com.taobao.api.BaseTaobaoResponse)} and raises
 * {@link DingTalkApiException} on failure — callers no longer need to
 * manually check {@link OapiRobotSendResponse#isSuccess()}.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AbstractDingTalkService
 * @see service.DingTalkService#opsForRobot()
 */
public class DingTalkRobotService extends AbstractDingTalkService {

	/**
	 * Constructs robot operations bound to the supplied service.
	 *
	 * @param service aggregated DingTalk service; must not be {@code null}
	 */
	public DingTalkRobotService(DingTalkService service) {
		super(service);
	}

	/**
	 * Legacy bridge accepting {@link DingTalkTemplate}; retained for binary
	 * compatibility with third-party code that instantiates operations directly.
	 *
	 * @param template DingTalk template holder; must not be {@code null}
	 * @deprecated prefer the {@link service.DingTalkService}-based constructor
	 */
	@Deprecated
	public DingTalkRobotService(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * @return the robot configuration for the requested corporate/robot pair,
	 *         never {@code null}
	 * @throws IllegalStateException when the robot is not registered
	 */
	public DingTalkRobotConfig getDingTalkRobotConfig(String corpId, String robotId) {
		DingTalkRobotConfig cfg = service.getDingTalkConfigProvider().getDingTalkRobotConfig(corpId, robotId);
		if (cfg == null) {
			throw new IllegalStateException("No DingTalkRobotConfig registered for corpId=" + corpId + ", robotId=" + robotId);
		}
		return cfg;
	}

	protected String getWebhook(String corpId, String robotId, Long timestamp) {
		DingTalkRobotConfig config = getDingTalkRobotConfig(corpId, robotId);
		StringBuilder serverUrl = new StringBuilder(PREFIX).append("/robot/send?access_token=").append(config.getAccessToken());
		String sign = service.getSign(config.getSecretToken(), timestamp);
		if (sign != null) {
			serverUrl.append("&timestamp=").append(timestamp).append("&sign=").append(sign);
		}
		return serverUrl.toString();
	}

    public String getUserMobile(String access_token, String userid, String lang) throws DingTalkApiException {
        try {
            DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/topapi/v2/user/get");
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(userid);
            req.setLanguage(lang);
            OapiV2UserGetResponse raw = client.execute(req, access_token);
            OapiV2UserGetResponse rsp = executeChecked(raw, raw.getErrcode(), raw.getErrmsg());
            return rsp.getResult() == null ? null : rsp.getResult().getMobile();
        } catch (ApiException ex) {
            throw wrap("Failed to get user mobile userid=" + userid, ex);
        }
    }

	/**
	 * Builds an {@link OapiRobotSendRequest} from any supported {@link BaseMessage}
	 * implementation.  The five existing branches (actionCard, feedCard, link,
	 * markdown, text) are fully populated — no longer no-op stubs.
	 *
	 * @param message the message to convert
	 * @return the fully-populated robot send request
	 */
    public OapiRobotSendRequest buildRequest(BaseMessage message) {
		OapiRobotSendRequest request = new OapiRobotSendRequest();
		request.setMsgtype(message.getMsgtype().name());

		switch (message.getMsgtype()) {
			case actionCard: {
				ActionCardMessage msg = (ActionCardMessage) message;
				OapiRobotSendRequest.Actioncard card = new OapiRobotSendRequest.Actioncard();
				card.setTitle(msg.getTitle());
				card.setText(msg.getText());
				if (msg.getHideAvatar() != null) {
					card.setHideAvatar(String.valueOf(msg.getHideAvatar().getValue()));
				}
				if (msg.getBtnOrientation() != null) {
					card.setBtnOrientation(String.valueOf(msg.getBtnOrientation().getValue()));
				}
				List<ActionCardButton> btns = msg.getButtons();
				if (msg.isButtonView() && btns != null && btns.size() == 1) {
					ActionCardButton single = btns.get(0);
					card.setSingleTitle(single.getTitle());
					card.setSingleURL(single.getActionURL());
				} else if (btns != null && !btns.isEmpty()) {
					List<OapiRobotSendRequest.Btns> out = new ArrayList<>(btns.size());
					for (ActionCardButton b : btns) {
						OapiRobotSendRequest.Btns x = new OapiRobotSendRequest.Btns();
						x.setTitle(b.getTitle());
						x.setActionURL(b.getActionURL());
						out.add(x);
					}
					card.setBtns(out);
				}
				request.setActionCard(card);
			} break;
			case feedCard: {
				FeedCardMessage msg = (FeedCardMessage) message;
				OapiRobotSendRequest.Feedcard fc = new OapiRobotSendRequest.Feedcard();
				List<OapiRobotSendRequest.Links> links = new ArrayList<>();
				List<FeedCardMessageItem> items = msg.getFeedCardItems() == null ? new ArrayList<>() : msg.getFeedCardItems();
				for (FeedCardMessageItem item : items) {
					OapiRobotSendRequest.Links l = new OapiRobotSendRequest.Links();
					l.setTitle(item.getTitle());
					l.setPicURL(item.getPicURL());
					l.setMessageURL(item.getMessageURL());
					links.add(l);
				}
				fc.setLinks(links);
				request.setFeedCard(fc);
			} break;
			case link: {
				LinkMessage msg = (LinkMessage) message;
				OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
				link.setTitle(msg.getTitle());
				link.setText(msg.getText());
				link.setPicUrl(msg.getPicUrl());
				link.setMessageUrl(msg.getMessageUrl());
				request.setLink(link);
			} break;
			case markdown: {
				MarkdownMessage msg = (MarkdownMessage) message;
				OapiRobotSendRequest.Markdown md = new OapiRobotSendRequest.Markdown();
				md.setTitle(msg.getTitle());
				md.setText(msg.getText());
				request.setMarkdown(md);
				OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
				if (ArrayUtils.isNotEmpty(msg.getAtMobiles())) {
					at.setAtMobiles(Arrays.asList(msg.getAtMobiles()));
				}
				at.setIsAtAll(Boolean.valueOf(msg.getIsAtAll()));
				request.setAt(at);
			} break;
			case text: {
				TextMessage msg = (TextMessage) message;
				OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
				text.setContent(msg.getContent());
				request.setText(text);
				OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
				if (ArrayUtils.isNotEmpty(msg.getAtMobiles())) {
					at.setAtMobiles(Arrays.asList(msg.getAtMobiles()));
				}
				at.setIsAtAll(Boolean.valueOf(msg.isAtAll()));
				request.setAt(at);
			} break;
		}

		return request;
    }

	/**
	 * Legacy spelling; preserved for binary compatibility with prior releases.
	 *
	 * @param message the message to convert
	 * @return the fully-populated robot send request
	 * @deprecated typo preserved for compat; use {@link #buildRequest(BaseMessage)}
	 */
	@Deprecated
	public OapiRobotSendRequest buidRequest(BaseMessage message) {
		return buildRequest(message);
	}

	/* ---------- Core send primitives ---------- */

    public OapiRobotSendResponse sendMessage(String corpId, String robotId, BaseMessage message) throws ApiException, DingTalkApiException {
		return sendMessage(corpId, robotId, buildRequest(message));
	}

    public OapiRobotSendResponse sendMessage(String corpId, String robotId, OapiRobotSendRequest request) throws ApiException, DingTalkApiException {
		Long timestamp = Long.valueOf(System.currentTimeMillis());
		DingTalkClient client = new DefaultDingTalkClient(getWebhook(corpId, robotId, timestamp));
		request.setTimestamp(timestamp);
		OapiRobotSendResponse resp = client.execute(request);
		return executeChecked(resp, resp.getErrcode(), resp.getErrmsg());
	}

    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, TextMessage message) throws ApiException, DingTalkApiException {
		return sendMessage(corpId, robotId, message);
    }

    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, String content) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new TextMessage(content));
    }

    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, String content, String[] atMobiles) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new TextMessage(content, atMobiles));
    }

    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, String content, boolean isAtAll) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new TextMessage(content, isAtAll));
    }

    public OapiRobotSendResponse sendLinkMessage(String corpId, String robotId, LinkMessage message) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, message);
    }

    public OapiRobotSendResponse sendLinkMessage(String corpId, String robotId, String title, String text, String messageUrl) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new LinkMessage(title, text, messageUrl));
    }

    public OapiRobotSendResponse sendLinkMessage(String corpId, String robotId, String title, String text, String messageUrl, String picUrl) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new LinkMessage(title, text, messageUrl, picUrl));
    }

    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, MarkdownMessage message) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, message);
    }

    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, String title, String text) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new MarkdownMessage(title, text));
    }

    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, String title, String text, String[] atMobiles) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new MarkdownMessage(title, text, atMobiles));
    }

    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, String title, String text, boolean isAtAll) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new MarkdownMessage(title, text, isAtAll));
    }

    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, ActionCardMessage message) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, message);
    }

    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new ActionCardMessage(title, text));
    }

    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text, HideAvatarType hideAvatar) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new ActionCardMessage(title, text, hideAvatar));
    }

    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text, ActionCardButton button) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new ActionCardMessage(title, text, button));
    }

    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text, HideAvatarType hideAvatar, ActionCardButton button) throws ApiException, DingTalkApiException {
		ActionCardMessage msg = new ActionCardMessage(title, text, hideAvatar, button);
		if (button != null && StringUtils.isNotBlank(button.getTitle()) && StringUtils.isNotBlank(button.getActionURL())) {
			msg.setBtnOrientation(ButtonOrientationType.HORIZONTAL);
		}
        return sendMessage(corpId, robotId, msg);
    }

    public OapiRobotSendResponse sendFeedCardMessage(String corpId, String robotId, FeedCardMessage feedCardMessage) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, feedCardMessage);
    }

    public OapiRobotSendResponse sendFeedCardMessage(String corpId, String robotId, List<FeedCardMessageItem> feedCardItems) throws ApiException, DingTalkApiException {
        return sendMessage(corpId, robotId, new FeedCardMessage(feedCardItems));
    }

    public OapiRobotSendResponse sendMessageByUrl(String webhook, String secret, BaseMessage message) throws ApiException, DingTalkApiException {
		return sendMessageByUrl(webhook, secret, buildRequest(message));
	}

    public OapiRobotSendResponse sendMessageByUrl(String webhook, String secret, OapiRobotSendRequest request) throws ApiException, DingTalkApiException {
		Long timestamp = Long.valueOf(System.currentTimeMillis());
		String sign = service.getSign(secret, timestamp);
		StringBuilder serverUrl = new StringBuilder(webhook);
		if (sign != null) {
			serverUrl.append("&timestamp=").append(timestamp).append("&sign=").append(sign);
		}
		DingTalkClient client = new DefaultDingTalkClient(serverUrl.toString());
		request.setTimestamp(timestamp);
		OapiRobotSendResponse resp = client.execute(request);
		return executeChecked(resp, resp.getErrcode(), resp.getErrmsg());
	}
}
