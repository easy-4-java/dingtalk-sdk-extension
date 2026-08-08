package com.dingtalk.spring.boot.bean;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeedCardMessageTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        FeedCardMessage msg = new FeedCardMessage();
        assertEquals(MessageType.feedCard, msg.getMsgtype());
        assertNotNull(msg.getFeedCardItems());
        assertTrue(msg.getFeedCardItems().isEmpty());
    }

    @Test
    void shouldCreateWithArrayList() {
        ArrayList<FeedCardMessageItem> items = new ArrayList<>();
        items.add(new FeedCardMessageItem("t", "http://u.com", "http://p.com"));
        FeedCardMessage msg = new FeedCardMessage(items);
        assertEquals(1, msg.getFeedCardItems().size());
    }

    @Test
    void shouldThrowWhenListNotArrayList() {
        List<FeedCardMessageItem> items = List.of(
                new FeedCardMessageItem("t", "http://u.com", "http://p.com"));
        assertThrows(IllegalArgumentException.class, () -> new FeedCardMessage(items));
    }

    @Test
    void shouldThrowWhenExceedingMaxItems() {
        ArrayList<FeedCardMessageItem> items = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            items.add(new FeedCardMessageItem("t" + i, "http://u" + i + ".com", "http://p" + i + ".com"));
        }
        assertThrows(IllegalArgumentException.class, () -> new FeedCardMessage(items));
    }

    @Test
    void shouldAddFeedCardItem() {
        FeedCardMessage msg = new FeedCardMessage();
        msg.addFeedCardItem(new FeedCardMessageItem("t", "http://u.com", "http://p.com"));
        assertEquals(1, msg.getFeedCardItems().size());
    }

    @Test
    void shouldThrowWhenAddingNullItem() {
        FeedCardMessage msg = new FeedCardMessage();
        assertThrows(IllegalArgumentException.class, () -> msg.addFeedCardItem(null));
    }

    @Test
    void shouldThrowWhenAddingItemWithEmptyTitle() {
        FeedCardMessage msg = new FeedCardMessage();
        assertThrows(IllegalArgumentException.class,
                () -> msg.addFeedCardItem(new FeedCardMessageItem("", "http://u.com", "http://p.com")));
    }

    @Test
    void shouldThrowWhenAddingItemWithEmptyUrl() {
        FeedCardMessage msg = new FeedCardMessage();
        assertThrows(IllegalArgumentException.class,
                () -> msg.addFeedCardItem(new FeedCardMessageItem("t", "", "http://p.com")));
    }

    @Test
    void shouldThrowWhenAddingItemWithEmptyPic() {
        FeedCardMessage msg = new FeedCardMessage();
        assertThrows(IllegalArgumentException.class,
                () -> msg.addFeedCardItem(new FeedCardMessageItem("t", "http://u.com", "")));
    }

    @Test
    void shouldThrowWhenExceedingMaxItemsOnAdd() {
        FeedCardMessage msg = new FeedCardMessage();
        for (int i = 0; i < 10; i++) {
            msg.addFeedCardItem(new FeedCardMessageItem("t" + i, "http://u" + i + ".com", "http://p" + i + ".com"));
        }
        assertThrows(IllegalArgumentException.class,
                () -> msg.addFeedCardItem(new FeedCardMessageItem("extra", "http://e.com", "http://ep.com")));
    }
}
