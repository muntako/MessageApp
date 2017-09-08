package id.co.easysoft.muntako.messageapp.model;

/**
 * Created by ADMIN on 31-Aug-17.
 *
 */

public class Message {
    private int usersId;
    private String message;
    private long sentAt;
    private String name;
    private boolean isDelivered;
    private boolean hasBeenRead;

    public Message(int usersId, String message, long sentAt, String name) {
        this.usersId = usersId;
        this.message = message;
        this.sentAt = sentAt;
        this.name = name;
        this.isDelivered = false;
        this.hasBeenRead = false;
    }

    public int getUsersId() {
        return usersId;
    }

    public String getMessage() {
        return message;
    }

    public long getSentAt() {
        return sentAt;
    }

    public String getName() {
        return name;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public boolean isHasBeenRead() {
        return hasBeenRead;
    }

    public void setHasBeenRead(boolean hasBeenRead) {
        this.hasBeenRead = hasBeenRead;
    }
}
