package io.github.easy4j.dingtalk.model.message;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionCardMessageTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        ActionCardMessage msg = new ActionCardMessage();
        assertEquals(MessageType.actionCard, msg.getMsgtype());
        assertNull(msg.getTitle());
        assertNull(msg.getText());
        assertEquals(HideAvatarType.UNHIDE, msg.getHideAvatar());
        assertEquals(ButtonOrientationType.HORIZONTAL, msg.getBtnOrientation());
        assertFalse(msg.isButtonView());
        assertNotNull(msg.getButtons());
        assertTrue(msg.getButtons().isEmpty());
    }

    @Test
    void shouldCreateWithTitleAndText() {
        ActionCardMessage msg = new ActionCardMessage("title", "text");
        assertEquals("title", msg.getTitle());
        assertEquals("text", msg.getText());
    }

    @Test
    void shouldCreateWithHideAvatar() {
        ActionCardMessage msg = new ActionCardMessage("title", "text", HideAvatarType.HIDE);
        assertEquals(HideAvatarType.HIDE, msg.getHideAvatar());
    }

    @Test
    void shouldCreateWithButton() {
        ActionCardButton button = new ActionCardButton("btn", "http://url.com");
        ActionCardMessage msg = new ActionCardMessage("title", "text", button);
        assertEquals(1, msg.getButtons().size());
        assertEquals("btn", msg.getButtons().get(0).getTitle());
    }

    @Test
    void shouldCreateWithHideAvatarAndButton() {
        ActionCardButton button = new ActionCardButton("btn", "http://url.com");
        ActionCardMessage msg = new ActionCardMessage("title", "text", HideAvatarType.HIDE, button);
        assertEquals(HideAvatarType.HIDE, msg.getHideAvatar());
        assertEquals(1, msg.getButtons().size());
    }

    @Test
    void shouldAddButton() {
        ActionCardMessage msg = new ActionCardMessage();
        msg.addButton(new ActionCardButton("b1", "http://1.com"));
        assertEquals(1, msg.getButtons().size());
    }

    @Test
    void shouldThrowWhenAddingNullButton() {
        ActionCardMessage msg = new ActionCardMessage();
        assertThrows(IllegalArgumentException.class, () -> msg.addButton(null));
    }

    @Test
    void shouldThrowWhenExceedingMaxButtons() {
        ActionCardMessage msg = new ActionCardMessage();
        for (int i = 0; i < 5; i++) {
            msg.addButton(new ActionCardButton("b" + i, "http://" + i + ".com"));
        }
        assertThrows(IllegalArgumentException.class,
                () -> msg.addButton(new ActionCardButton("extra", "http://extra.com")));
    }

    @Test
    void shouldSetAndGetTitle() {
        ActionCardMessage msg = new ActionCardMessage();
        msg.setTitle("t");
        assertEquals("t", msg.getTitle());
    }

    @Test
    void shouldSetAndGetText() {
        ActionCardMessage msg = new ActionCardMessage();
        msg.setText("body");
        assertEquals("body", msg.getText());
    }

    @Test
    void shouldSetAndGetHideAvatar() {
        ActionCardMessage msg = new ActionCardMessage();
        msg.setHideAvatar(HideAvatarType.HIDE);
        assertEquals(HideAvatarType.HIDE, msg.getHideAvatar());
    }

    @Test
    void shouldSetAndGetBtnOrientation() {
        ActionCardMessage msg = new ActionCardMessage();
        msg.setBtnOrientation(ButtonOrientationType.VERTICAL);
        assertEquals(ButtonOrientationType.VERTICAL, msg.getBtnOrientation());
    }

    @Test
    void shouldSetButtonView() {
        ActionCardMessage msg = new ActionCardMessage();
        msg.setButtonView(true);
        assertTrue(msg.isButtonView());
    }
}
