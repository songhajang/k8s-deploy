package com.ticket.homepage.service;

import com.ticket.homepage.model.Concert;
import com.ticket.homepage.model.Seat;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConcertService {
    private final Map<Long, Concert> concerts = new HashMap<>();
    private Long nextId = 1L;
    
    @PostConstruct
    public void initData() {
        // 샘플 콘서트 데이터 생성
        createSampleConcerts();
    }
    
    private void createSampleConcerts() {
        // BTS 콘서트
        Concert btsConcert = new Concert(
            nextId++, 
            "BTS WORLD TOUR 'PROOF'", 
            "BTS", 
            "올림픽체조경기장", 
            "BTS의 새로운 월드투어가 시작됩니다!",
            "/images/bts.png",
            LocalDateTime.now().plusDays(30),
            LocalDateTime.now().plusDays(7),
            1000,
            150000,
            "K-POP"
        );
        btsConcert.setSeats(generateSeats(1000, 150000));
        concerts.put(btsConcert.getId(), btsConcert);
        
        // 아이유 콘서트
        Concert iuConcert = new Concert(
            nextId++, 
            "IU CONCERT 'The Golden Hour'", 
            "아이유", 
            "잠실실내체육관", 
            "아이유의 황금시간 콘서트",
            "/images/iu.png",
            LocalDateTime.now().plusDays(45),
            LocalDateTime.now().plusDays(14),
            800,
            120000,
            "K-POP"
        );
        iuConcert.setSeats(generateSeats(800, 120000));
        concerts.put(iuConcert.getId(), iuConcert);
        
        // 블랙핑크 콘서트
        Concert bpConcert = new Concert(
            nextId++, 
            "BLACKPINK WORLD TOUR", 
            "BLACKPINK", 
            "고척스카이돔", 
            "BLACKPINK의 월드투어",
            "/images/blackpink.png",
            LocalDateTime.now().plusDays(60),
            LocalDateTime.now().plusDays(21),
            1200,
            180000,
            "K-POP"
        );
        bpConcert.setSeats(generateSeats(1200, 180000));
        concerts.put(bpConcert.getId(), bpConcert);
        
        // 에픽하이 콘서트
        Concert epikHighConcert = new Concert(
            nextId++, 
            "EPIK HIGH CONCERT", 
            "에픽하이", 
            "올림픽공원 KSPO DOME", 
            "에픽하이의 힙합 콘서트",
            "/images/epikhigh.png",
            LocalDateTime.now().plusDays(20),
            LocalDateTime.now().plusDays(3),
            600,
            80000,
            "HIP-HOP"
        );
        epikHighConcert.setSeats(generateSeats(600, 80000));
        concerts.put(epikHighConcert.getId(), epikHighConcert);
        
        // 볼빨간사춘기 콘서트
        Concert bol4Concert = new Concert(
            nextId++, 
            "볼빨간사춘기 CONCERT", 
            "볼빨간사춘기", 
            "세종문화회관 대극장", 
            "볼빨간사춘기의 따뜻한 음악",
            "/images/bol4.png",
            LocalDateTime.now().plusDays(15),
            LocalDateTime.now().plusDays(1),
            400,
            70000,
            "BALLAD"
        );
        bol4Concert.setSeats(generateSeats(400, 70000));
        concerts.put(bol4Concert.getId(), bol4Concert);
    }
    
    private List<Seat> generateSeats(int totalSeats, int basePrice) {
        List<Seat> seats = new ArrayList<>();
        String[] sections = {"VIP", "R", "S", "A", "B"};
        int[] sectionPrices = {basePrice * 2, (int)(basePrice * 1.5), basePrice, (int)(basePrice * 0.8), (int)(basePrice * 0.6)};
        int seatsPerSection = totalSeats / sections.length;
        
        for (int s = 0; s < sections.length; s++) {
            String section = sections[s];
            int price = sectionPrices[s];
            int rows = (int) Math.ceil(Math.sqrt(seatsPerSection));
            int seatsPerRow = seatsPerSection / rows;
            
            for (int row = 1; row <= rows; row++) {
                for (int num = 1; num <= seatsPerRow; num++) {
                    String seatNumber = section + "-" + String.format("%02d", row) + "-" + String.format("%02d", num);
                    seats.add(new Seat(seatNumber, section, row, num, price, section));
                }
            }
        }
        
        return seats;
    }
    
    public List<Concert> getAllConcerts() {
        return new ArrayList<>(concerts.values());
    }
    
    public List<Concert> getActiveConcerts() {
        return concerts.values().stream()
                .filter(Concert::isActive)
                .collect(Collectors.toList());
    }
    
    public Optional<Concert> getConcertById(Long id) {
        return Optional.ofNullable(concerts.get(id));
    }
    
    public List<Concert> searchConcerts(String keyword) {
        return concerts.values().stream()
                .filter(concert -> concert.isActive() && 
                        (concert.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                         concert.getArtist().toLowerCase().contains(keyword.toLowerCase()) ||
                         concert.getCategory().toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }
    
    public List<Concert> getConcertsByCategory(String category) {
        return concerts.values().stream()
                .filter(concert -> concert.isActive() && concert.getCategory().equals(category))
                .collect(Collectors.toList());
    }
    
    public List<String> getCategories() {
        return concerts.values().stream()
                .map(Concert::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
    
    public boolean bookSeats(Long concertId, List<String> seatNumbers) {
        Optional<Concert> concertOpt = getConcertById(concertId);
        if (concertOpt.isEmpty()) {
            return false;
        }
        
        Concert concert = concertOpt.get();
        List<Seat> seats = concert.getSeats();
        
        // 좌석 예약 가능 여부 확인
        for (String seatNumber : seatNumbers) {
            boolean seatFound = false;
            for (Seat seat : seats) {
                if (seat.getSeatNumber().equals(seatNumber)) {
                    if (!seat.isAvailable()) {
                        return false; // 이미 예약된 좌석
                    }
                    seatFound = true;
                    break;
                }
            }
            if (!seatFound) {
                return false; // 존재하지 않는 좌석
            }
        }
        
        // 좌석 예약 처리
        for (String seatNumber : seatNumbers) {
            for (Seat seat : seats) {
                if (seat.getSeatNumber().equals(seatNumber)) {
                    seat.setAvailable(false);
                    break;
                }
            }
        }
        
        // 사용 가능한 좌석 수 업데이트
        concert.setAvailableSeats((int) seats.stream().filter(Seat::isAvailable).count());
        
        return true;
    }
    
    public void cancelSeats(Long concertId, List<String> seatNumbers) {
        Optional<Concert> concertOpt = getConcertById(concertId);
        if (concertOpt.isEmpty()) {
            return;
        }
        
        Concert concert = concertOpt.get();
        List<Seat> seats = concert.getSeats();
        
        for (String seatNumber : seatNumbers) {
            for (Seat seat : seats) {
                if (seat.getSeatNumber().equals(seatNumber)) {
                    seat.setAvailable(true);
                    break;
                }
            }
        }
        
        // 사용 가능한 좌석 수 업데이트
        concert.setAvailableSeats((int) seats.stream().filter(Seat::isAvailable).count());
    }
}
