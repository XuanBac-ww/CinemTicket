package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
}
