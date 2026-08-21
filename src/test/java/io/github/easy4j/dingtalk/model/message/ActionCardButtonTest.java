package io.github.easy4j.dingtalk.model.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionCardButtonTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        ActionCardButton button = new ActionCardButton();
        assertNull(button.getTitle());
        assertNull(button.getActionURL());
    }

    @Test
    void shouldCreateWithTitleAndUrl() {
        ActionCardButton button = new ActionCardButton("Read", "http://example.com");
        assertEquals("Read", button.getTitle());
        assertEquals("http://example.com", button.getActionURL());
    }

    @Test
    void shouldCreateDefaultReadButton() {
        ActionCardButton button = ActionCardButton.defaultReadButton("http://example.com");
        assertEquals("阅读全文", button.getTitle());
        assertEquals("http://example.com", button.getActionURL());
    }

    @Test
    void shouldSetAndGetTitle() {
        ActionCardButton button = new ActionCardButton();
        button.setTitle("new title");
        assertEquals("new title", button.getTitle());
    }

    @Test
    void shouldSetAndGetActionURL() {
        ActionCardButton button = new ActionCardButton();
        button.setActionURL("http://new.com");
        assertEquals("http://new.com", button.getActionURL());
    }
}
