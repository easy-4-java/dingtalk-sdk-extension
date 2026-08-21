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
package io.github.easy4j.dingtalk.model.message;

/**
 * A plain text message for DingTalk robot webhooks.
 * <p>Supports mentioning specific group members by mobile number or
 * mentioning all members via the {@code atAll} flag.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see BaseMessage
 * @see MessageType#text
 */
@SuppressWarnings("serial")
public class TextMessage extends BaseMessage {

	/**
	 * The actual text content of the message.
	 */
	private String content;

	/**
	 * Mobile phone numbers of group members to mention.
	 */
	private String[] atMobiles;

	/**
	 * Whether to mention all group members.
	 */
	private boolean atAll;

	/**
	 * Constructs an empty text message.
	 */
	public TextMessage() {
		super(MessageType.text);
	}

	/**
	 * Constructs a text message with the given content.
	 *
	 * @param content the text content; must not be {@code null}
	 */
	public TextMessage(String content) {
		super(MessageType.text);
		this.content = content;
	}

	/**
	 * Constructs a text message that mentions specific members by mobile number.
	 *
	 * @param content    the text content; must not be {@code null}
	 * @param atMobiles  mobile phone numbers of members to mention
	 */
	public TextMessage(String content, String[] atMobiles) {
		super(MessageType.text);
		this.content = content;
		this.atMobiles = atMobiles;
	}

	/**
	 * Constructs a text message with an at-all flag.
	 *
	 * @param content  the text content; must not be {@code null}
	 * @param atAll    {@code true} to mention all group members
	 */
	public TextMessage(String content, boolean atAll) {
		super(MessageType.text);
		this.content = content;
		this.atAll = atAll;
	}

	/**
	 * Returns the text content.
	 *
	 * @return the text content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the text content.
	 *
	 * @param content the text content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Returns the mobile phone numbers of members to mention.
	 *
	 * @return array of mobile phone numbers, or {@code null}
	 */
	public String[] getAtMobiles() {
		return atMobiles;
	}

	/**
	 * Sets the mobile phone numbers of members to mention.
	 *
	 * @param atMobiles array of mobile phone numbers
	 */
	public void setAtMobiles(String[] atMobiles) {
		this.atMobiles = atMobiles;
	}

	/**
	 * Returns whether all group members should be mentioned.
	 *
	 * @return {@code true} if all members are mentioned
	 */
	public boolean isAtAll() {
		return atAll;
	}

	/**
	 * Sets whether all group members should be mentioned.
	 *
	 * @param atAll {@code true} to mention all members
	 */
	public void setAtAll(boolean atAll) {
		this.atAll = atAll;
	}

}
