package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.model.User;
import com.tihonya.datingapp.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET-эндпоинт с Query Parameters (поиск пользователей по имени)
    @GetMapping("/search")  // <-- Это маршрут "/users/search"
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        List<User> users = userService.getUsersByName(name);
        return users.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(users);
    }

    // GET-эндпоинт с Path Parameters (получение пользователя по ID)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
