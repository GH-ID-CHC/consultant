package com.example.consultant.service.impl;

import com.example.consultant.mapper.ReservationMapper;
import com.example.consultant.pojo.Reservation;
import com.example.consultant.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {
    
    @Autowired
    private ReservationMapper mapper;

    @Override
    public void insert(Reservation reservation) {
        mapper.addReservation(reservation);
    }

    @Override
    public Reservation findByPhone(String phone) {
        return mapper.findByPhone(phone);
    }
}
