package com.ticket.homepage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class SessionController {
    
    @PostMapping("/session/set-seats")
    public String setSelectedSeats(@RequestParam String selectedSeats,
                                  @RequestParam Long concertId,
                                  HttpSession session) {
        session.setAttribute("selectedSeats", selectedSeats);
        session.setAttribute("concertId", concertId.toString());
        return "redirect:/login";
    }
}
