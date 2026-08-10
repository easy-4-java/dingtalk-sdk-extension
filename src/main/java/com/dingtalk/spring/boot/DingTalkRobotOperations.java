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
package com.dingtalk.spring.boot;

import java.util.Arrays;
import java.util.List;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.spring.boot.bean.ActionCardButton;
import com.dingtalk.spring.boot.bean.ActionCardMessage;
import com.dingtalk.spring.boot.bean.BaseMessage;
import com.dingtalk.spring.boot.bean.FeedCardMessage;
import com.dingtalk.spring.boot.bean.FeedCardMessageItem;
import com.dingtalk.spring.boot.bean.HideAvatarType;
import com.dingtalk.spring.boot.bean.LinkMessage;
import com.dingtalk.spring.boot.bean.MarkdownMessage;
import com.dingtalk.spring.boot.bean.TextMessage;
import com.dingtalk.spring.boot.property.DingTalkRobotProperties;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Operations for sending messages through DingTalk robot webhooks.
 * <p>Supports text, link, markdown, action card, and feed card message types.
 * Messages can be sent by corporate ID + robot ID (using configured webhook)
 * or by an explicit webhook URL.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DingTalkOperations
 * @see DingTalkTemplate#opsForRobot()
 * @see TextMessage
 * @see LinkMessage
 * @see MarkdownMessage
 * @see ActionCardMessage
 * @see FeedCardMessage
 */
public class DingTalkRobotOperations extends DingTalkOperations {

	/**
	 * Constructs robot operations with the given template.
	 *
	 * @param template the DingTalk template; must not be {@code null}
	 */
	public DingTalkRobotOperations(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * Builds the webhook URL for the specified robot.
	 *
	 * @param corpId     the corporate ID
	 * @param robotId    the robot ID
	 * @param timestamp  the current timestamp in milliseconds
	 * @return the fully-qualified webhook URL with access token and signature
	 */
	protected String getWebhook(String corpId, String robotId, Long timestamp) {
		DingTalkRobotProperties poperties = template.getDingTalkConfigProvider().getDingTalkRobotProperties(corpId, robotId);
        StringBuilder serverUrl = new StringBuilder(PREFIX + "/robot/send?access_token=").append(poperties.getAccessToken());
        String sign =  template.getSign(poperties.getSecretToken(), timestamp);
        serverUrl.append("&timestamp=").append(timestamp).append("&sign=").append(sign);
        return serverUrl.toString();
    }

	/**
	 * Retrieves the mobile phone number of a DingTalk user.
	 *
	 * @param access_token  the access token for API calls
	 * @param userid        the DingTalk user ID
	 * @param lang          the language for the response (e.g., "zh_CN")
	 * @return the mobile phone number, or {@code null} on error
	 */
    public String getUserMobile(String access_token, String userid,  String lang) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/topapi/v2/user/get");
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(userid);
            req.setLanguage(lang);
            OapiV2UserGetResponse rsp = client.execute(req, access_token);
            System.out.println(rsp.getBody());
            return rsp.getResult().getMobile();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

	/**
	 * Builds an {@link OapiRobotSendRequest} from a {@link BaseMessage}.
	 *
	 * @param message the message to convert
	 * @return the robot send request
	 */
    OapiRobotSendRequest buidRequest(BaseMessage message){

		OapiRobotSendRequest request = new OapiRobotSendRequest();
		request.setMsgtype(message.getMsgtype().name());

		switch (message.getMsgtype()) {
			case actionCard:{

			};break;
			case feedCard:{

			};break;
			case link:{

			};break;
			case markdown:{

			};break;
			case text:{

				TextMessage msg = (TextMessage) message;

				OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
				text.setContent(msg.getContent());
				request.setText(text);
                OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
				if(ArrayUtils.isNotEmpty(msg.getAtMobiles())){
                    at.setAtMobiles(Arrays.asList(msg.getAtMobiles()));
                }
				at.setIsAtAll(msg.isAtAll());
				request.setAt(at);

			};break;
		}


		return request;
    }

	/**
	 * Sends a message through the robot identified by corporate ID and robot ID.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param message  the message to send
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendMessage(String corpId, String robotId, BaseMessage message) throws ApiException {
		return this.sendMessage(corpId, robotId, this.buidRequest(message));
	}

	/**
	 * Sends a pre-built request through the robot identified by corporate ID and robot ID.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param request  the pre-built send request
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendMessage(String corpId, String robotId, OapiRobotSendRequest request) throws ApiException {
  		Long timestamp = System.currentTimeMillis();
  		DingTalkClient client = new DefaultDingTalkClient(this.getWebhook(corpId, robotId, timestamp));
  		request.setTimestamp(timestamp);
  		return client.execute(request);
  	}

	/**
	 * Sends a text message object through the robot.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param message  the text message to send
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, TextMessage message) throws ApiException {
		return this.sendMessage(corpId, robotId, this.buidRequest(message));
    }

	/**
	 * Sends a plain text string through the robot.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param content  the text content
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, String content) throws ApiException {
        return this.sendMessage(corpId, robotId, new TextMessage(content));
    }

	/**
	 * Sends a text message mentioning specific members by mobile number.
	 *
	 * @param corpId    the corporate ID
	 * @param robotId   the robot ID
	 * @param content   the text content
	 * @param atMobiles mobile phone numbers of members to mention
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, String content, String[] atMobiles) throws ApiException {
        return this.sendMessage(corpId, robotId, new TextMessage(content, atMobiles));
    }

	/**
	 * Sends a text message with an at-all flag.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param content  the text content
	 * @param isAtAll  {@code true} to mention all group members
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, String content, boolean isAtAll) throws ApiException {
        return this.sendMessage(corpId, robotId, new TextMessage(content, isAtAll));
    }

	/**
	 * Sends a link message object through the robot.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param message  the link message to send
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendLinkMessage(String corpId, String robotId, LinkMessage message) throws ApiException {
        return this.sendMessage(corpId, robotId, message);
    }

	/**
	 * Sends a link message with title, text, and URL.
	 *
	 * @param corpId      the corporate ID
	 * @param robotId     the robot ID
	 * @param title       the link title
	 * @param text        the link description
	 * @param messageUrl  the target URL
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendLinkMessage(String corpId, String robotId, String title, String text, String messageUrl) throws ApiException {
        return this.sendMessage(corpId, robotId, new LinkMessage(title, text, messageUrl));
    }

	/**
	 * Sends a link message with title, text, URL, and cover image.
	 *
	 * @param corpId      the corporate ID
	 * @param robotId     the robot ID
	 * @param title       the link title
	 * @param text        the link description
	 * @param messageUrl  the target URL
	 * @param picUrl      the cover image URL
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendLinkMessage(String corpId, String robotId, String title, String text, String messageUrl, String picUrl) throws ApiException {
        return this.sendMessage(corpId, robotId, new LinkMessage(title, text, messageUrl, picUrl));
    }

	/**
	 * Sends a Markdown message object through the robot.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param message  the Markdown message to send
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, MarkdownMessage message) throws ApiException {
        return this.sendMessage(corpId, robotId, message);
    }

	/**
	 * Sends a Markdown message with title and text.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param title    the message title
	 * @param text     the Markdown-formatted body
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, String title, String text) throws ApiException {
        return this.sendMessage(corpId, robotId, new MarkdownMessage(title, text));
    }

	/**
	 * Sends a Markdown message mentioning specific members by mobile number.
	 *
	 * @param corpId    the corporate ID
	 * @param robotId   the robot ID
	 * @param title     the message title
	 * @param text      the Markdown-formatted body
	 * @param atMobiles mobile phone numbers of members to mention
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, String title, String text, String[] atMobiles) throws ApiException {
        return this.sendMessage(corpId, robotId, new MarkdownMessage(title, text, atMobiles));
    }

	/**
	 * Sends a Markdown message with an at-all flag.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param title    the message title
	 * @param text     the Markdown-formatted body
	 * @param isAtAll  {@code true} to mention all group members
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, String title, String text, boolean isAtAll) throws ApiException {
        return this.sendMessage(corpId, robotId, new MarkdownMessage(title, text, isAtAll));
    }

	/**
	 * Sends an ActionCard message object through the robot.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param message  the ActionCard message to send
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, ActionCardMessage message) throws ApiException {
        return this.sendMessage(corpId, robotId, message);
    }

	/**
	 * Sends an ActionCard message with title and text.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param title    the message title
	 * @param text     the Markdown-formatted body
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text) throws ApiException {
        return this.sendMessage(corpId, robotId, new ActionCardMessage(title, text));
    }

	/**
	 * Sends an ActionCard message with avatar visibility control.
	 *
	 * @param corpId      the corporate ID
	 * @param robotId     the robot ID
	 * @param title       the message title
	 * @param text        the Markdown-formatted body
	 * @param hideAvatar  avatar visibility setting
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text, HideAvatarType hideAvatar) throws ApiException {
        return this.sendMessage(corpId, robotId, new ActionCardMessage(title, text, hideAvatar));
    }

	/**
	 * Sends an ActionCard message with a single button.
	 *
	 * @param corpId   the corporate ID
	 * @param robotId  the robot ID
	 * @param title    the message title
	 * @param text     the Markdown-formatted body
	 * @param button   the action button
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text, ActionCardButton button) throws ApiException {
        return this.sendMessage(corpId, robotId, new ActionCardMessage(title, text, button));
    }

	/**
	 * Sends an ActionCard message with avatar visibility and a single button.
	 *
	 * @param corpId      the corporate ID
	 * @param robotId     the robot ID
	 * @param title       the message title
	 * @param text        the Markdown-formatted body
	 * @param hideAvatar  avatar visibility setting
	 * @param button      the action button
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text, HideAvatarType hideAvatar, ActionCardButton button) throws ApiException {
        return this.sendMessage(corpId, robotId, new ActionCardMessage(title, text, hideAvatar, button));
    }

	/**
	 * Sends a FeedCard message object through the robot.
	 *
	 * @param corpId          the corporate ID
	 * @param robotId         the robot ID
	 * @param feedCardMessage the FeedCard message to send
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendFeedCardMessage(String corpId, String robotId, FeedCardMessage feedCardMessage) throws ApiException {
        return this.sendMessage(corpId, robotId, feedCardMessage);
    }

	/**
	 * Sends a FeedCard message built from a list of items.
	 *
	 * @param corpId        the corporate ID
	 * @param robotId       the robot ID
	 * @param feedCardItems the list of feed card items
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendFeedCardMessage(String corpId, String robotId, List<FeedCardMessageItem> feedCardItems) throws ApiException {
        return this.sendMessage(corpId, robotId, new FeedCardMessage(feedCardItems));
    }

	/**
	 * Sends a message by explicit webhook URL.
	 *
	 * @param webhook  the full webhook URL (including access_token parameter)
	 * @param secret   the robot secret for signature computation
	 * @param message  the message to send
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendMessageByUrl(String webhook, String secret, BaseMessage message) throws ApiException {
		return this.sendMessageByUrl(webhook, secret, this.buidRequest(message));
	}

	/**
	 * Sends a pre-built request by explicit webhook URL.
	 *
	 * @param webhook  the full webhook URL
	 * @param secret   the robot secret for signature computation
	 * @param request  the pre-built send request
	 * @return the send response
	 * @throws ApiException if the API request fails
	 */
    public OapiRobotSendResponse sendMessageByUrl(String webhook, String secret, OapiRobotSendRequest request) throws ApiException {
		Long timestamp = System.currentTimeMillis();

        String sign =  template.getSign(secret, timestamp);
        StringBuilder serverUrl = new StringBuilder(webhook).append("&timestamp=").append(timestamp).append("&sign=").append(sign);

		DingTalkClient client = new DefaultDingTalkClient(serverUrl.toString());
		request.setTimestamp(timestamp);
		return client.execute(request);
	}

}
