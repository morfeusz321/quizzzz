package server.game;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

public class FakeSimpMessagingTemplate extends SimpMessagingTemplate {

    private List<Pair<String, Object>> sentPayloads;

    public FakeSimpMessagingTemplate() {

        super((message, timeout) -> true);

        this.sentPayloads = new ArrayList<>();

    }

    @Override
    public void convertAndSend(String destination, Object payload) {

        sentPayloads.add(Pair.of(destination, payload));

    }

    public List<Pair<String, Object>> getSentPayloads() {

        return sentPayloads;

    }

    public Pair<String, Object> getMostRecentSentPayload() {

        return sentPayloads.get(sentPayloads.size() - 1);

    }

}
