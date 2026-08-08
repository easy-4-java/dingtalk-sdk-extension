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


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * A FeedCard message for DingTalk robot webhooks.
 * <p>FeedCard messages contain a list of {@link FeedCardMessageItem} entries,
 * each representing a clickable card with a title, URL, and cover image.
 * The maximum number of items is 10.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see BaseMessage
 * @see FeedCardMessageItem
 * @see MessageType#feedCard
 */
@SuppressWarnings("serial")
public class FeedCardMessage extends BaseMessage {

    /**
     * Maximum number of feed card items allowed.
     */
    private static final int MAX_BUTTON_COUNT = 10;

    /**
     * Minimum number of feed card items.
     */
    private static final int MIN_BUTTON_COUNT = 1;

    /**
     * List of feed card items.
     */
    private List<FeedCardMessageItem> feedCardItems = new ArrayList<>();

    /**
     * Constructs an empty FeedCard message.
     */
    public FeedCardMessage() {
        super(MessageType.feedCard);
    }

    /**
     * Constructs a FeedCard message with the given items.
     * <p>The provided list must be an {@link ArrayList} instance and
     * must not exceed {@value #MAX_BUTTON_COUNT} items.</p>
     *
     * @param feedCardItems the list of feed card items
     * @throws IllegalArgumentException if the list is not an {@link ArrayList} or exceeds the maximum size
     */
    public FeedCardMessage(List<FeedCardMessageItem> feedCardItems) {
        super(MessageType.feedCard);
        if (!(feedCardItems instanceof ArrayList)) {
            throw new IllegalArgumentException("feedCardItems must bu ArrayList");
        }
        if (feedCardItems.size() > MAX_BUTTON_COUNT) {
            throw new IllegalArgumentException("the number of buttons is not advise bigger than " + MAX_BUTTON_COUNT);
        }
        this.feedCardItems = feedCardItems;
    }

    /**
     * Returns the list of feed card items.
     *
     * @return the feed card items list, never {@code null}
     */
    public List<FeedCardMessageItem> getFeedCardItems() {
        return feedCardItems;
    }

    /**
     * Adds a feed card item to this message.
     *
     * @param item the item to add; must have non-empty title, messageURL, and picURL
     * @throws IllegalArgumentException if the item is invalid or the maximum item count is exceeded
     */
    public void addFeedCardItem(FeedCardMessageItem item) {
        if (item == null || StringUtils.isEmpty(item.getMessageURL()) ||
                StringUtils.isEmpty(item.getPicURL()) || StringUtils.isEmpty(item.getTitle())) {
            throw new IllegalArgumentException("please check the necessary parameters of item!");
        }
        if (feedCardItems == null || feedCardItems.size() >= MAX_BUTTON_COUNT) {
            throw new IllegalArgumentException("the number of buttons is not advise bigger than " + MAX_BUTTON_COUNT);
        }
        feedCardItems.add(item);
    }
}
