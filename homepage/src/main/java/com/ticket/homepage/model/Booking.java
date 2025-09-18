package com.ticket.homepage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private Long userId;
    private Long concertId;
    private List<String> seatNumbers;
    private int totalPrice;
    private BookingStatus status;
    private LocalDateTime bookingDate;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String ticketNumber;
    
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
    
    public Booking(Long id, Long userId, Long concertId, List<String> seatNumbers, 
                   int totalPrice, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.concertId = concertId;
        this.seatNumbers = seatNumbers;
        this.totalPrice = totalPrice;
        this.status = BookingStatus.PENDING;
        this.bookingDate = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
        this.ticketNumber = generateTicketNumber();
    }
    
    private String generateTicketNumber() {
        return "TK" + System.currentTimeMillis();
    }
}
