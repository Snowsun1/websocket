package ru.sysout.websocket.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sysout.websocket.model.User;

public interface UsersRepository extends CrudRepository<User, Long> {
    User findByLogin (String login);
    User findById (long id);
}
