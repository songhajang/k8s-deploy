package com.ticket.homepage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private String seatNumber;
    private String section;
    private int row;
    private int number;
    private boolean isAvailable;
    private int price;
    private String grade;
    
    public Seat(String seatNumber, String section, int row, int number, int price, String grade) {
        this.seatNumber = seatNumber;
        this.section = section;
        this.row = row;
        this.number = number;
        this.isAvailable = true;
        this.price = price;
        this.grade = grade;
    }
}
