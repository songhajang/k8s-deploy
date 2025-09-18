package com.ticket.homepage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String name;
    private String phone;
    private LocalDateTime createdAt;
    private List<Booking> bookings;
    
    public User(Long id, String username, String email, String password, String name, String phone) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
        this.bookings = new ArrayList<>();
    }
}
