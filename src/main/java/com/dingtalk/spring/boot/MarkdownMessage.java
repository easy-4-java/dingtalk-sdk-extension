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
package com.dingtalk.spring.boot.bean;

/**
 * A Markdown-formatted message for DingTalk robot webhooks.
 * <p>The message body supports standard Markdown syntax.
 * Members can be mentioned by mobile number or all at once.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see BaseMessage
 * @see MessageType#markdown
 */
@SuppressWarnings("serial")
public class MarkdownMessage extends BaseMessage {

	/**
     * Brief description of the message.
     */
    private String text;

    /**
     * Title of the message.
     */
    private String title;

    /**
     * Mobile phone numbers of group members to mention.
     */
    private String[] atMobiles;

    /**
     * Whether to mention all group members.
     */
    private boolean isAtAll;

    /**
     * Constructs an empty Markdown message.
     */
	public MarkdownMessage() {
		super(MessageType.markdown);
	}

	/**
	 * Constructs a Markdown message with title and text.
	 *
	 * @param title  the message title
	 * @param text   the Markdown-formatted body
	 */
	public MarkdownMessage(String title, String text) {
		super(MessageType.markdown);
		this.text = text;
		this.title = title;
	}

	/**
	 * Constructs a Markdown message that mentions specific members by mobile number.
	 *
	 * @param title      the message title
	 * @param text       the Markdown-formatted body
	 * @param atMobiles  mobile phone numbers of members to mention
	 */
	public MarkdownMessage(String title, String text, String[] atMobiles) {
		super(MessageType.markdown);
		this.text = text;
		this.title = title;
		this.atMobiles = atMobiles;
	}

	/**
	 * Constructs a Markdown message with an at-all flag.
	 *
	 * @param title    the message title
	 * @param text     the Markdown-formatted body
	 * @param isAtAll  {@code true} to mention all group members
	 */
	public MarkdownMessage(String title, String text, boolean isAtAll) {
		super(MessageType.markdown);
		this.text = text;
		this.title = title;
		this.isAtAll = isAtAll;
	}

	/**
	 * Returns the message title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the message title.
	 *
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the Markdown-formatted body.
	 *
	 * @return the message text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the Markdown-formatted body.
	 *
	 * @param text the message text to set
	 */
	public void setText(String text) {
		this.text = text;
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
	public boolean getIsAtAll() {
		return isAtAll;
	}

	/**
	 * Sets whether all group members should be mentioned.
	 *
	 * @param isAtAll {@code true} to mention all members
	 */
	public void setIsAtAll(boolean isAtAll) {
		this.isAtAll = isAtAll;
	}

}
