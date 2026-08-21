package io.github.easy4j.dingtalk.model.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownMessageTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        MarkdownMessage msg = new MarkdownMessage();
        assertEquals(MessageType.markdown, msg.getMsgtype());
        assertNull(msg.getTitle());
        assertNull(msg.getText());
        assertNull(msg.getAtMobiles());
        assertFalse(msg.getIsAtAll());
    }

    @Test
    void shouldCreateWithTitleAndText() {
        MarkdownMessage msg = new MarkdownMessage("title", "# heading");
        assertEquals("title", msg.getTitle());
        assertEquals("# heading", msg.getText());
    }

    @Test
    void shouldCreateWithAtMobiles() {
        String[] mobiles = {"13800138000"};
        MarkdownMessage msg = new MarkdownMessage("title", "text", mobiles);
        assertArrayEquals(mobiles, msg.getAtMobiles());
    }

    @Test
    void shouldCreateWithAtAll() {
        MarkdownMessage msg = new MarkdownMessage("title", "text", true);
        assertTrue(msg.getIsAtAll());
    }

    @Test
    void shouldSetAndGetTitle() {
        MarkdownMessage msg = new MarkdownMessage();
        msg.setTitle("t");
        assertEquals("t", msg.getTitle());
    }

    @Test
    void shouldSetAndGetText() {
        MarkdownMessage msg = new MarkdownMessage();
        msg.setText("body");
        assertEquals("body", msg.getText());
    }

    @Test
    void shouldSetAndGetAtMobiles() {
        MarkdownMessage msg = new MarkdownMessage();
        String[] mobiles = {"123", "456"};
        msg.setAtMobiles(mobiles);
        assertArrayEquals(mobiles, msg.getAtMobiles());
    }

    @Test
    void shouldSetAndGetIsAtAll() {
        MarkdownMessage msg = new MarkdownMessage();
        msg.setIsAtAll(true);
        assertTrue(msg.getIsAtAll());
        msg.setIsAtAll(false);
        assertFalse(msg.getIsAtAll());
    }
}
