package com.ticket.homepage.service;

import com.ticket.homepage.model.Booking;
import com.ticket.homepage.model.Concert;
import com.ticket.homepage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private Long nextId = 1L;
    
    @Autowired
    private ConcertService concertService;
    
    @Autowired
    private UserService userService;
    
    public Booking createBooking(Long userId, Long concertId, List<String> seatNumbers, String paymentMethod) {
        // 사용자 확인
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        
        // 콘서트 확인
        Optional<Concert> concertOpt = concertService.getConcertById(concertId);
        if (concertOpt.isEmpty()) {
            throw new IllegalArgumentException("콘서트를 찾을 수 없습니다.");
        }
        
        Concert concert = concertOpt.get();
        
        // 좌석 가격 계산
        int totalPrice = calculateTotalPrice(concert, seatNumbers);
        
        // 좌석 예약
        boolean bookingSuccess = concertService.bookSeats(concertId, seatNumbers);
        if (!bookingSuccess) {
            throw new IllegalArgumentException("좌석 예약에 실패했습니다. 이미 예약된 좌석이거나 존재하지 않는 좌석입니다.");
        }
        
        // 예약 생성
        Booking booking = new Booking(nextId++, userId, concertId, seatNumbers, totalPrice, paymentMethod);
        bookings.put(booking.getId(), booking);
        
        // 사용자 예약 목록에 추가
        User user = userOpt.get();
        user.getBookings().add(booking);
        
        return booking;
    }
    
    private int calculateTotalPrice(Concert concert, List<String> seatNumbers) {
        int totalPrice = 0;
        for (String seatNumber : seatNumbers) {
            for (com.ticket.homepage.model.Seat seat : concert.getSeats()) {
                if (seat.getSeatNumber().equals(seatNumber)) {
                    totalPrice += seat.getPrice();
                    break;
                }
            }
        }
        return totalPrice;
    }
    
    public boolean confirmPayment(Long bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null && booking.getStatus() == Booking.BookingStatus.PENDING) {
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            booking.setPaymentDate(java.time.LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    public boolean cancelBooking(Long bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null && booking.getStatus() != Booking.BookingStatus.CANCELLED) {
            // 좌석 취소
            concertService.cancelSeats(booking.getConcertId(), booking.getSeatNumbers());
            
            // 예약 상태 변경
            booking.setStatus(Booking.BookingStatus.CANCELLED);
            return true;
        }
        return false;
    }
    
    public Optional<Booking> getBookingById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }
    
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookings.values().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .sorted(Comparator.comparing(Booking::getBookingDate).reversed())
                .collect(Collectors.toList());
    }
    
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }
    
    public List<Booking> getBookingsByStatus(Booking.BookingStatus status) {
        return bookings.values().stream()
                .filter(booking -> booking.getStatus() == status)
                .collect(Collectors.toList());
    }
}
