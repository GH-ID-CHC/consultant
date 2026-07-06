package com.example.consultant.tools;

import com.example.consultant.pojo.Reservation;
import com.example.consultant.service.ReservationService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationTool {
    
    @Autowired
    private ReservationService reservationService;

    /**
     * 添加预约
     *
     * @param name              名字
     * @param gender            性别
     * @param phone             电话
     * @param communicationTime 沟通时间
     * @param province          省
     * @param estimatedScore    考生预估分数
     */
    @Tool("添加志愿指导服务预约")
    public void addReservation(
            @P("考生姓名") String name,
            @P("考生性别") String gender,
            @P("考生电话") String phone,
            @P("沟通时间，格式：yyyy-MM-dd'T'HH:mm") LocalDateTime communicationTime,
            @P("考生所在的省份") String province,
            @P("考生预估分数") Integer estimatedScore
    ) {
        reservationService.insert(new Reservation(null, name, gender, phone, communicationTime, province, estimatedScore));
    }

    /**
     * 根据电话查询预约信息
     *
     * @param phone 电话
     * @return {@link Reservation }
     */
    @Tool("查询预约信息")
    public Reservation findReservation(@P("考生电话") String phone) {
        return reservationService.findByPhone(phone);
    }
}
