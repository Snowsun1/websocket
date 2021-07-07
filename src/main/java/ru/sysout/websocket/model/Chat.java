package ru.sysout.websocket.model;

import java.util.Date;

public class Chat {
    public String user;
    public Boolean is_online;
    public String message;
    public Date date;

    public Chat(String user, Boolean is_online, String message, Date date) {
        this.is_online = is_online;
        this.message = message;
        this.user = user;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Chat{" +
            "user='" + user + '\'' +
            ", is_online=" + is_online +
            ", message='" + message + '\'' +
            ", date=" + date +
            '}';
    }
}
