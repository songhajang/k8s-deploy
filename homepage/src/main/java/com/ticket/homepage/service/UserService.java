package com.ticket.homepage.service;

import com.ticket.homepage.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    private Long nextId = 1L;
    
    public User registerUser(String username, String email, String password, String name, String phone) {
        // 중복 확인
        if (usersByUsername.containsKey(username)) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }
        if (usersByEmail.containsKey(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        User user = new User(nextId++, username, email, password, name, phone);
        users.put(user.getId(), user);
        usersByUsername.put(username, user);
        usersByEmail.put(email, user);
        
        return user;
    }
    
    public Optional<User> loginUser(String username, String password) {
        User user = usersByUsername.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
    
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }
    
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    public boolean updateUser(Long userId, String name, String phone) {
        User user = users.get(userId);
        if (user != null) {
            user.setName(name);
            user.setPhone(phone);
            return true;
        }
        return false;
    }
    
    public boolean deleteUser(Long userId) {
        User user = users.get(userId);
        if (user != null) {
            users.remove(userId);
            usersByUsername.remove(user.getUsername());
            usersByEmail.remove(user.getEmail());
            return true;
        }
        return false;
    }
}
