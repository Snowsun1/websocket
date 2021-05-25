package ru.sysout.websocket.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.sysout.websocket.model.ChatRoom;
import ru.sysout.websocket.model.Message;

import java.util.ArrayList;
import java.util.List;

public interface MessagesRepository extends CrudRepository<Message, Long> {

  Message findBySenderAndRecipient(String sender, String recipient);

  ArrayList<Message> findAllBySender(String sender);

  ArrayList<Message> findAllByRecipient(String recipient);

  @Query(value = "select u from Message u where u.sender = ?1 or u.recipient = ?1 order by u.date asc")
  List<Message> findAllMessagesWithUser(String user);

  @Query(value = "select u from Message u where u.sender = ?1 or u.recipient = ?1 order by u.date asc")
    // TODO: Перепиши потом
  List<Message> findAllMessageToUser(String user);

  @Query(value = "select u from Message u where (u.sender = ?1 or u.recipient = ?2) or (u.sender = ?2 or u.recipient = ?1) order by u.date asc")
  List<Message> findAllMessageBetweenUsers(String user1, String user2);
}
