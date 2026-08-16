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
 * Represents a button in a DingTalk ActionCard message.
 * <p>Each button has a display title and an action URL that is
 * opened when the user clicks the button.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see ActionCardMessage
 */
public class ActionCardButton {

    /**
     * Display title of the button.
     */
    private String title;

    /**
     * URL to navigate to when the button is clicked.
     */
    private String actionURL;

    /**
     * Constructs an empty action card button.
     */
    public ActionCardButton() {
    }

    /**
     * Constructs an action card button with the given title and URL.
     *
     * @param title      the button display title
     * @param actionURL  the URL to open on click
     */
    public ActionCardButton(String title, String actionURL) {
        this.title = title;
        this.actionURL = actionURL;
    }

    /**
     * Creates a default "Read More" button with the specified URL.
     *
     * @param actionURL  the URL to open when the button is clicked
     * @return a new {@link ActionCardButton} with title "阅读全文"
     */
    public static ActionCardButton defaultReadButton(String actionURL) {
        ActionCardButton button = new ActionCardButton();
        button.setTitle("阅读全文");
        button.setActionURL(actionURL);
        return button;
    }

    /**
     * Returns the button display title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the button display title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the action URL.
     *
     * @return the action URL
     */
    public String getActionURL() {
        return actionURL;
    }

    /**
     * Sets the action URL.
     *
     * @param actionURL the URL to set
     */
    public void setActionURL(String actionURL) {
        this.actionURL = actionURL;
    }
}
