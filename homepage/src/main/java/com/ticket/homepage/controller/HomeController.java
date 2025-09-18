package com.ticket.homepage.controller;

import com.ticket.homepage.model.Concert;
import com.ticket.homepage.service.ConcertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private ConcertService concertService;
    
    @GetMapping("/")
    public String home(Model model) {
        List<Concert> concerts = concertService.getActiveConcerts();
        List<String> categories = concertService.getCategories();
        
        model.addAttribute("concerts", concerts);
        model.addAttribute("categories", categories);
        model.addAttribute("featuredConcerts", concerts.stream().limit(6).toList());
        
        return "index";
    }
    
    @GetMapping("/concerts")
    public String concerts(@RequestParam(required = false) String category,
                          @RequestParam(required = false) String search,
                          Model model) {
        List<Concert> concerts;
        
        if (search != null && !search.trim().isEmpty()) {
            concerts = concertService.searchConcerts(search.trim());
        } else if (category != null && !category.isEmpty()) {
            concerts = concertService.getConcertsByCategory(category);
        } else {
            concerts = concertService.getActiveConcerts();
        }
        
        List<String> categories = concertService.getCategories();
        
        model.addAttribute("concerts", concerts);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchKeyword", search);
        
        return "concerts";
    }
    
    @GetMapping("/concert/{id}")
    public String concertDetail(@PathVariable Long id, Model model) {
        return concertService.getConcertById(id)
                .map(concert -> {
                    model.addAttribute("concert", concert);
                    return "concert-detail";
                })
                .orElse("redirect:/concerts");
    }
}
