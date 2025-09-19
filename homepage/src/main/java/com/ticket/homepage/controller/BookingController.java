package com.ticket.homepage.controller;

import com.ticket.homepage.model.Booking;
import com.ticket.homepage.model.Concert;
import com.ticket.homepage.model.User;
import com.ticket.homepage.service.BookingService;
import com.ticket.homepage.service.ConcertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ConcertService concertService;
    
    @GetMapping("/booking/{concertId}")
    public String bookingPage(@PathVariable Long concertId, 
                             HttpSession session, 
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        // 로그인 체크
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        
        // 콘서트 정보 가져오기
        Optional<Concert> concertOpt = concertService.getConcertById(concertId);
        if (concertOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "콘서트를 찾을 수 없습니다.");
            return "redirect:/concerts";
        }
        
        // 세션에서 선택된 좌석 정보 가져오기
        String selectedSeatsStr = (String) session.getAttribute("selectedSeats");
        if (selectedSeatsStr == null || selectedSeatsStr.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "좌석을 선택해주세요.");
            return "redirect:/concert/" + concertId;
        }
        
        List<String> selectedSeats = Arrays.asList(selectedSeatsStr.split(","));
        
        model.addAttribute("concert", concertOpt.get());
        model.addAttribute("selectedSeats", selectedSeats);
        model.addAttribute("user", user);
        
        return "booking/booking-form";
    }
    
    @PostMapping("/booking/{concertId}")
    public String processBooking(@PathVariable Long concertId,
                                @RequestParam String paymentMethod,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        // 로그인 체크
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        
        // 세션에서 선택된 좌석 정보 가져오기
        String selectedSeatsStr = (String) session.getAttribute("selectedSeats");
        if (selectedSeatsStr == null || selectedSeatsStr.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "좌석을 선택해주세요.");
            return "redirect:/concert/" + concertId;
        }
        
        List<String> selectedSeats = Arrays.asList(selectedSeatsStr.split(","));
        
        try {
            // 예매 생성
            Booking booking = bookingService.createBooking(user.getId(), concertId, selectedSeats, paymentMethod);
            
            // 결제 확인 (시뮬레이션)
            bookingService.confirmPayment(booking.getId());
            
            // 세션에서 선택된 좌석 정보 제거
            session.removeAttribute("selectedSeats");
            session.removeAttribute("concertId");
            
            redirectAttributes.addFlashAttribute("success", "예매가 완료되었습니다!");
            return "redirect:/booking/success/" + booking.getId();
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/booking/" + concertId;
        }
    }
    
    @GetMapping("/booking/success/{bookingId}")
    public String bookingSuccess(@PathVariable Long bookingId,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        // 로그인 체크
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        
        Optional<Booking> bookingOpt = bookingService.getBookingById(bookingId);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "예매 정보를 찾을 수 없습니다.");
            return "redirect:/";
        }
        
        Booking booking = bookingOpt.get();
        
        // 본인의 예매인지 확인
        if (!booking.getUserId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/";
        }
        
        Optional<Concert> concertOpt = concertService.getConcertById(booking.getConcertId());
        if (concertOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "콘서트 정보를 찾을 수 없습니다.");
            return "redirect:/";
        }
        
        model.addAttribute("booking", booking);
        model.addAttribute("concert", concertOpt.get());
        model.addAttribute("user", user);
        
        return "booking/booking-success";
    }
    
    @GetMapping("/my-bookings")
    public String myBookings(HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        
        // 로그인 체크
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        
        List<Booking> bookings = bookingService.getBookingsByUserId(user.getId());
        model.addAttribute("bookings", bookings);
        model.addAttribute("user", user);
        
        return "booking/my-bookings";
    }
    
    @PostMapping("/booking/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        // 로그인 체크
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        
        Optional<Booking> bookingOpt = bookingService.getBookingById(bookingId);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "예매 정보를 찾을 수 없습니다.");
            return "redirect:/my-bookings";
        }
        
        Booking booking = bookingOpt.get();
        
        // 본인의 예매인지 확인
        if (!booking.getUserId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/my-bookings";
        }
        
        // 예매 취소
        boolean success = bookingService.cancelBooking(bookingId);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "예매가 취소되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "예매 취소에 실패했습니다.");
        }
        
        return "redirect:/my-bookings";
    }
}
