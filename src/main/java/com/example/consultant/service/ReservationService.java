package com.example.consultant.service;

import com.example.consultant.pojo.Reservation;

public interface ReservationService {

    /**
     * 添加预约信息的方法
     *
     * @param reservation 信息
     */
    void insert(Reservation reservation);

    /**
     * 查询预约信息的方法
     *
     * @param phone 电话
     * @return {@link Reservation }
     */
    Reservation findByPhone(String phone);
}
