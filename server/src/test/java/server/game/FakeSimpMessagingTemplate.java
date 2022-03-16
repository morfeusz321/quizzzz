package server.game;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * The FakeSimpMessagingTemplate can be injected instead of a SimpMessagingTemplate
 * for testing purposes
 */
public class FakeSimpMessagingTemplate extends SimpMessagingTemplate {

    private List<Pair<String, Object>> sentPayloads;

    /**
     * Creates a new FakeSimpMessagingTemplate
     */
    public FakeSimpMessagingTemplate() {

        super((message, timeout) -> true);

        this.sentPayloads = new ArrayList<>();

    }

    /**
     * Saves the combination of destination and payload to a list containing
     * all "sent" messages by this FakeSimpMessagingTemplate
     * @param destination the destination to save
     * @param payload the payload to save
     */
    @Override
    public void convertAndSend(String destination, Object payload) {

        sentPayloads.add(Pair.of(destination, payload));

    }

    /**
     * Returns the list of all "sent" messages by this FakeSimpMessagingTemplate
     * @return the list of all "sent" messages
     */
    public List<Pair<String, Object>> getSentPayloads() {

        return sentPayloads;

    }

    /**
     * Returns the most recent "sent" message by this FakeSimpMessagingTemplate
     * @return the most recent "sent" message
     */
    public Pair<String, Object> getMostRecentSentPayload() {

        return sentPayloads.get(sentPayloads.size() - 1);

    }

}
