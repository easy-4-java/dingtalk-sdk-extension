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

import java.util.ArrayList;
import java.util.List;

/**
 * An ActionCard message for DingTalk robot webhooks.
 * <p>ActionCard messages support a title, Markdown-formatted body,
 * avatar visibility control, button orientation, and up to five
 * action buttons. The DingTalk SDK officially supports up to 5 buttons;
 * this limit is enforced by {@link #addButton(ActionCardButton)}.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see BaseMessage
 * @see ActionCardButton
 * @see MessageType#actionCard
 */
@SuppressWarnings("serial")
public class ActionCardMessage extends BaseMessage {

    /**
     * Maximum number of buttons recommended (DingTalk SDK guideline).
     */
    private static final int MAX_BUTTON_COUNT = 5;

    /**
     * Minimum number of buttons.
     */
    private static final int MIN_BUTTON_COUNT = 0;

    /**
     * Message title.
     */
    private String title;

    /**
     * Message body, supports Markdown syntax.
     */
    private String text;

    /**
     * Whether to hide the robot avatar when sending the message.
     */
    private HideAvatarType hideAvatar = HideAvatarType.UNHIDE;

    /**
     * Button layout orientation.
     */
    private ButtonOrientationType btnOrientation = ButtonOrientationType.HORIZONTAL;

    /**
     * Whether to use independent jump layout (only effective when there is exactly one button).
     */
    private boolean isButtonView;

    /**
     * List of action buttons (max 5 recommended).
     */
    private List<ActionCardButton> buttons = new ArrayList<>();

    /**
     * Constructs an empty ActionCard message.
     */
    public ActionCardMessage() {
    	super(MessageType.actionCard);
    }

    /**
     * Constructs an ActionCard message with title and text.
     *
     * @param title  the message title
     * @param text   the Markdown-formatted body
     */
    public ActionCardMessage(String title, String text) {
    	super(MessageType.actionCard);
        this.title = title;
        this.text = text;
    }

    /**
     * Constructs an ActionCard message with avatar visibility.
     *
     * @param title       the message title
     * @param text        the Markdown-formatted body
     * @param hideAvatar  avatar visibility setting
     */
    public ActionCardMessage(String title, String text, HideAvatarType hideAvatar) {
    	super(MessageType.actionCard);
        this.title = title;
        this.text = text;
        this.hideAvatar = hideAvatar;
    }

    /**
     * Constructs an ActionCard message with a single button.
     *
     * @param title   the message title
     * @param text    the Markdown-formatted body
     * @param button  the action button to add
     */
    public ActionCardMessage(String title, String text, ActionCardButton button) {
    	super(MessageType.actionCard);
        this.title = title;
        this.text = text;
        this.buttons.add(button);
    }

    /**
     * Constructs an ActionCard message with avatar visibility and a single button.
     *
     * @param title       the message title
     * @param text        the Markdown-formatted body
     * @param hideAvatar  avatar visibility setting
     * @param button      the action button to add
     */
    public ActionCardMessage(String title, String text, HideAvatarType hideAvatar, ActionCardButton button) {
    	super(MessageType.actionCard);
        this.title = title;
        this.text = text;
        this.hideAvatar = hideAvatar;
        this.buttons.add(button);
    }

    /**
     * Adds an action button to this card.
     *
     * @param button the button to add; must not be {@code null}
     * @throws IllegalArgumentException if {@code button} is {@code null} or the maximum button count is exceeded
     */
    public void addButton(ActionCardButton button) {
        if (button == null) {
            throw new IllegalArgumentException("not allow add empty button");
        }
        if (buttons == null || buttons.size() >= MAX_BUTTON_COUNT) {
            throw new IllegalArgumentException("the number of buttons is not advise bigger than " + MAX_BUTTON_COUNT);
        }
        buttons.add(button);
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
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the Markdown-formatted body.
     *
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the avatar visibility setting.
     *
     * @return the hide avatar type
     */
    public HideAvatarType getHideAvatar() {
        return hideAvatar;
    }

    /**
     * Sets the avatar visibility setting.
     *
     * @param hideAvatar the hide avatar type to set
     */
    public void setHideAvatar(HideAvatarType hideAvatar) {
        this.hideAvatar = hideAvatar;
    }

    /**
     * Returns the button orientation.
     *
     * @return the button orientation type
     */
    public ButtonOrientationType getBtnOrientation() {
        return btnOrientation;
    }

    /**
     * Sets the button orientation.
     *
     * @param btnOrientation the button orientation type to set
     */
    public void setBtnOrientation(ButtonOrientationType btnOrientation) {
        this.btnOrientation = btnOrientation;
    }

    /**
     * Returns the list of action buttons.
     *
     * @return the button list, never {@code null}
     */
    public List<ActionCardButton> getButtons() {
        return buttons;
    }

    /**
     * Returns whether independent jump layout is used.
     *
     * @return {@code true} if independent jump layout is enabled
     */
    public boolean isButtonView() {
        return isButtonView;
    }

    /**
     * Sets whether independent jump layout is used.
     *
     * @param buttonView {@code true} to enable independent jump layout
     */
    public void setButtonView(boolean buttonView) {
        isButtonView = buttonView;
    }
}
