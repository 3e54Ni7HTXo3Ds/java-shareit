package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findByBookerOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);


    }
