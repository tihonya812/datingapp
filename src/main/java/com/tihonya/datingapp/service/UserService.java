package com.tihonya.datingapp.service;

import com.tihonya.datingapp.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();

    public UserService() {
        // Добавляем несколько пользователей при инициализации
        users.add(new User(1L, "Alice", "alice@example.com"));
        users.add(new User(2L, "Bob", "bob@example.com"));
        users.add(new User(3L, "Charlie", "charlie@example.com"));
        users.add(new User(4L, "Ann", "ann@example.com"));
        users.add(new User(5L, "Ann", "ann2@example.com"));
    }

    // Найти пользователя по ID
    public Optional<User> getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    // Найти всех пользователей с данным именем
    public List<User> getUsersByName(String name) {
        return users.stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }
}