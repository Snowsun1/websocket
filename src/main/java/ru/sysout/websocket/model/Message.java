package ru.sysout.websocket.model;

import java.util.Objects;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private Date date;
    private String sender;
    private String recipient;

    protected Message() {};

    public Message(String message, Date date, String sender, String recipient) {
        this.message = message;
        this.date = date;
        this.sender = sender;
        this.recipient = recipient;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message1 = (Message) o;
        return Objects.equals(getId(), message1.getId()) && Objects
            .equals(getMessage(), message1.getMessage()) && Objects
            .equals(getDate(), message1.getDate()) && Objects
            .equals(getSender(), message1.getSender()) && Objects
            .equals(getRecipient(), message1.getRecipient());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMessage(), getDate(), getSender(), getRecipient());
    }

    @Override
    public String toString() {
        return "Message{" +
            "id=" + id +
            ", message='" + message + '\'' +
            ", date=" + date +
            ", sender='" + sender + '\'' +
            ", recipient='" + recipient + '\'' +
            '}';
    }
}
