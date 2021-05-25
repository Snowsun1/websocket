package ru.sysout.websocket.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import ru.sysout.websocket.model.Chat;
import ru.sysout.websocket.model.ChatRoom;
import ru.sysout.websocket.model.Message;
import ru.sysout.websocket.model.User;
import ru.sysout.websocket.repository.MessagesRepository;
import ru.sysout.websocket.repository.UsersRepository;

import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ChatController {

    private final MessagesRepository messagesRepository;
    private final UsersRepository usersRepository;
    private final SimpUserRegistry simpUserRegistry;

    @Autowired
    private SimpMessagingTemplate messageSender;

    public ChatController(MessagesRepository messagesRepository, UsersRepository usersRepository,SimpUserRegistry simpUserRegistry) {
        this.messagesRepository = messagesRepository;
        this.usersRepository = usersRepository;
        this.simpUserRegistry=simpUserRegistry;
    }

    @MessageMapping("/chat.sendMessage")
    public Message sendMessage(@Payload Message message) { // получаем сообщение записываем в базу отправляем на фронт
        System.out.println(message);
        if (message.getMessage() != null
                && message.getSender() != null
                && message.getRecipient() != null) {
            messagesRepository.save(message);
        }
        else {
            Message m = message;
            return m;
        }
        return message;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/newUserEvent")
    public String addUser(@Payload User user, // сделать вызов на фронте
                           SimpMessageHeaderAccessor headerAccessor) {
        if(headerAccessor==null) return "";
        Optional.of(headerAccessor.getSessionAttributes()).ifPresent(
            header->header.put("username", user.getLogin())
        );
        if (usersRepository.findByLogin(user.getLogin()) == null) {
            usersRepository.save(user);
            return user.getLogin();
        } else return user.getLogin();
    }

    @MessageMapping("/chat.getChatMessages")
    @SendTo("/topic/chat.getChatMessages")
    public List<Message> getChatMessages(@Payload ChatRoom chatRoom,
                                SimpMessageHeaderAccessor headerAccessor) {
        if(chatRoom.getFirstPerson()==null|| chatRoom.getSecondPerson()==null) return  List.of();
        return messagesRepository.findAllMessageBetweenUsers(chatRoom.getFirstPerson(),chatRoom.getSecondPerson());
    }

    @MessageMapping("/chat.getChats")
    @SendTo("/topic/chat.getUserChats")
    public List<Chat> getChats(@Payload User user,
                               SimpMessageHeaderAccessor headerAccessor) {
        List<Message> list = messagesRepository.findAllMessageToUser(user.getLogin());
        List<Chat> ret = new ArrayList<>();
        Set<String> set = new HashSet<>();
        set.add(user.getLogin());
        for (Message mes : list) {
            if (!set.contains(mes.getSender())) {
                User tmp_user = usersRepository.findByLogin(mes.getSender());
                if(tmp_user != null) {
                    ret.add(new Chat(
                            mes.getSender(),
                            tmp_user.getOnline(),
                            mes.getMessage(),
                            mes.getDate()
                    ));
                    set.add(mes.getSender());
                }
            }
            if (!set.contains(mes.getRecipient())) {
                User tmp_user = usersRepository.findByLogin(mes.getRecipient());
                if(tmp_user != null) {
                    ret.add(new Chat(
                            mes.getRecipient(),
                            tmp_user.getOnline(),
                            mes.getMessage(),
                            mes.getDate()
                    ));
                    set.add(mes.getRecipient());
                }
            }
        }
        usersRepository.findAll().forEach(u->{
            if(!set.contains(u.getLogin()) && !u.getLogin().equals(user.getLogin())){
                ret.add(new Chat(
                    u.getLogin(),
                    u.getOnline(),
                    "",
                    null
                ));
            }
        });
        System.out.println(user);
        System.out.println(ret);
        return ret;
    }

}
