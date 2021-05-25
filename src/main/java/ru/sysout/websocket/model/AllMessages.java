package ru.sysout.websocket.model;

public class AllMessages {
    private String fromUser;
    private String time;
    private String message;

    public String getFromUser() {
        return fromUser;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
