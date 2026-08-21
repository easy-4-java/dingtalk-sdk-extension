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

import java.io.Serializable;

/**
 * Abstract base class for all DingTalk message types.
 * <p>Every concrete message (text, link, markdown, action card, feed card)
 * extends this class and carries a {@link MessageType} discriminator.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see MessageType
 * @see TextMessage
 * @see LinkMessage
 * @see MarkdownMessage
 * @see ActionCardMessage
 * @see FeedCardMessage
 */
@SuppressWarnings("serial")
public abstract class BaseMessage implements Serializable {

    /**
     * The message type discriminator.
     */
    protected MessageType msgtype;

    /**
     * Constructs a new base message with the given type.
     *
     * @param msgtype the message type; must not be {@code null}
     */
	public BaseMessage(MessageType msgtype) {
		super();
		this.msgtype = msgtype;
	}

    /**
     * Returns the message type.
     *
     * @return the message type, never {@code null}
     */
	public MessageType getMsgtype() {
		return msgtype;
	}

    /**
     * Sets the message type.
     *
     * @param msgtype the message type to set; must not be {@code null}
     */
	public void setMsgtype(MessageType msgtype) {
		this.msgtype = msgtype;
	}

}
