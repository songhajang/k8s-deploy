package com.ticket.homepage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Concert {
    private Long id;
    private String title;
    private String artist;
    private String venue;
    private String description;
    private String imageUrl;
    private LocalDateTime concertDate;
    private LocalDateTime ticketOpenDate;
    private int totalSeats;
    private int availableSeats;
    private int price;
    private String category;
    private List<Seat> seats;
    private boolean isActive;
    
    public Concert(Long id, String title, String artist, String venue, String description, 
                   String imageUrl, LocalDateTime concertDate, LocalDateTime ticketOpenDate, 
                   int totalSeats, int price, String category) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.venue = venue;
        this.description = description;
        this.imageUrl = imageUrl;
        this.concertDate = concertDate;
        this.ticketOpenDate = ticketOpenDate;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.price = price;
        this.category = category;
        this.isActive = true;
    }
}
