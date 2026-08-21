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
 * Represents a single item within a DingTalk FeedCard message.
 * <p>Each item has a title, a target URL, and a cover image URL.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see FeedCardMessage
 */
public class FeedCardMessageItem {

    /**
     * Item title.
     */
    private String title;

    /**
     * URL to navigate to when the item is clicked.
     */
    private String messageURL;

    /**
     * URL of the cover image.
     */
    private String picURL;

    /**
     * Constructs an empty FeedCard message item.
     */
    public FeedCardMessageItem() {
    }

    /**
     * Constructs a FeedCard message item with all fields.
     *
     * @param title       the item title
     * @param messageURL  the target URL
     * @param picURL      the cover image URL
     */
    public FeedCardMessageItem(String title, String messageURL, String picURL) {
        this.title = title;
        this.messageURL = messageURL;
        this.picURL = picURL;
    }

    /**
     * Returns the item title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the item title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the target URL.
     *
     * @return the message URL
     */
    public String getMessageURL() {
        return messageURL;
    }

    /**
     * Sets the target URL.
     *
     * @param messageURL the message URL to set
     */
    public void setMessageURL(String messageURL) {
        this.messageURL = messageURL;
    }

    /**
     * Returns the cover image URL.
     *
     * @return the picture URL
     */
    public String getPicURL() {
        return picURL;
    }

    /**
     * Sets the cover image URL.
     *
     * @param picURL the picture URL to set
     */
    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }
}
