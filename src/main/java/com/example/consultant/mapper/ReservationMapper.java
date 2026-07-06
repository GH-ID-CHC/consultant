package com.example.consultant.mapper;

import com.example.consultant.pojo.Reservation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReservationMapper {
    @Insert("INSERT INTO reservation (name, gender, phone, communication_time, province, estimated_score) VALUES (#{name},#{gender},#{phone},#{communicationTime},#{province},#{estimatedScore});")
    int addReservation(Reservation reservation);

    @Select("SELECT * FROM `reservation` where phone = #{phone}")
    Reservation findByPhone(String phone);
}

