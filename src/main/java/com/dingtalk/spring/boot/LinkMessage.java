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
 * A link message for DingTalk robot webhooks.
 * <p>Contains a title, description text, target URL, and an optional
 * cover image URL.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see BaseMessage
 * @see MessageType#link
 */
@SuppressWarnings("serial")
public class LinkMessage extends BaseMessage {

    /**
     * Brief description of the link message.
     */
    private String text;

    /**
     * Title of the link message.
     */
    private String title;

    /**
     * URL of the cover image.
     */
    private String picUrl;

    /**
     * URL to navigate to when the message is clicked.
     */
    private String messageUrl;

    /**
     * Constructs an empty link message.
     */
    public LinkMessage() {
    	super(MessageType.link);
    }

    /**
     * Constructs a link message without a cover image.
     *
     * @param title       the message title
     * @param text        the message description
     * @param messageUrl  the target URL
     */
    public LinkMessage(String title, String text, String messageUrl) {
    	super(MessageType.link);
        this.text = text;
        this.title = title;
        this.messageUrl = messageUrl;
    }

    /**
     * Constructs a link message with a cover image.
     *
     * @param title       the message title
     * @param text        the message description
     * @param messageUrl  the target URL
     * @param picUrl      the cover image URL
     */
    public LinkMessage(String title, String text, String messageUrl, String picUrl) {
    	super(MessageType.link);
        this.text = text;
        this.title = title;
        this.picUrl = picUrl;
        this.messageUrl = messageUrl;
    }

    /**
     * Returns the message description.
     *
     * @return the message text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the message description.
     *
     * @param text the message text to set
     */
    public void setText(String text) {
        this.text = text;
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
     * Returns the cover image URL.
     *
     * @return the cover image URL, or {@code null}
     */
    public String getPicUrl() {
        return picUrl;
    }

    /**
     * Sets the cover image URL.
     *
     * @param picUrl the cover image URL
     */
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    /**
     * Returns the target URL.
     *
     * @return the target URL
     */
    public String getMessageUrl() {
        return messageUrl;
    }

    /**
     * Sets the target URL.
     *
     * @param messageUrl the target URL to set
     */
    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

}
