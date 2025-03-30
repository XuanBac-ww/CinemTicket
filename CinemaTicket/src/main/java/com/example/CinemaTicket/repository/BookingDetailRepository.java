package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail,Long> {
}
