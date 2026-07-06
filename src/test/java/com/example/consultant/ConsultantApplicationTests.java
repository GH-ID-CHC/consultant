package com.example.consultant;

import com.example.consultant.pojo.Reservation;
import com.example.consultant.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class ConsultantApplicationTests {
    
    @Autowired
    private ReservationService reservationService;

    @Test
    void contextLoads() {
        reservationService.insert(new Reservation(null, "小王", "男", "13888888888", LocalDateTime.now(), "北京", 100));
    }

}
