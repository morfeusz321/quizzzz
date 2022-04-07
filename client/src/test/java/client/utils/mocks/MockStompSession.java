package client.utils.mocks;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * A StompSession for testing purposes
 */
public class MockStompSession implements StompSession {

    public Consumer<String> mockReader;
    public String sessionID;
    public String destination;
    public boolean isConnected = false;

    /**
     * Creates a new mock stomp session for testing purposes with the provided consumer
     * @param mockReader the consumer this mock stomp session will send updates to
     */
    public MockStompSession(Consumer<String> mockReader) {

        this.mockReader = mockReader;

    }

    @Override
    public String getSessionId() {
        return sessionID;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Sets this testing session's isConnected status
     * @param isConnected the isConnected status to set
     */
    public void setIsConnected(boolean isConnected) {

        this.isConnected = isConnected;

    }

    @Override
    public void setAutoReceipt(boolean enabled) {

    }

    @Override
    public Receiptable send(String destination, Object payload) {

        mockReader.accept(destination + ";" + payload.toString());

        return null;

    }

    @Override
    public Receiptable send(StompHeaders headers, Object payload) {
        return null;
    }

    @Override
    public Subscription subscribe(String destination, StompFrameHandler handler) {
        this.sessionID = UUID.randomUUID().toString();
        this.destination = destination;
        this.isConnected = true;
        mockReader.accept(destination);
        return null;
    }

    @Override
    public Subscription subscribe(StompHeaders headers, StompFrameHandler handler) {
        this.sessionID = UUID.randomUUID().toString();
        this.destination = destination;
        this.isConnected = true;
        return null;
    }

    @Override
    public Receiptable acknowledge(String messageId, boolean consumed) {
        return null;
    }

    @Override
    public Receiptable acknowledge(StompHeaders headers, boolean consumed) {
        return null;
    }

    @Override
    public void disconnect() {
        this.sessionID = null;
        this.destination = null;
        this.isConnected = false;
        mockReader.accept("Disconnected");
    }

    @Override
    public void disconnect(StompHeaders headers) {
        this.sessionID = null;
        this.destination = null;
        this.isConnected = false;
    }

}
