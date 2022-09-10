package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findByBookerOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerAndStatusOrderByStartDesc(Long bookerId, Booking.Status status);

    List<Booking> findByItemIdOrderByStartAsc(Long itemId);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now,
                                                                            LocalDateTime now1);

    List<Booking> findByBookerAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);


    @Query(" select b from Booking b " +
            "join Item i on b.itemId=i.id " +
            "where i.owner =  ?1 and b.status<>'REJECTED'" +
            "order by b.start desc ")
    List<Booking> findAllByOwner(Long userId);

    @Query(" select b from Booking b " +
            "join Item i on b.itemId=i.id " +
            "where i.owner =  ?1 and b.status='WAITING'" +
            "order by b.start desc ")
    List<Booking> findWaitingByOwner(Long userId);

    @Query(" select b from Booking b " +
            "join Item i on b.itemId=i.id " +
            "where i.owner =  ?1 and b.status='REJECTED'" +
            "order by b.start desc ")
    List<Booking> findRejectedByOwner(Long userId);

    @Query(" select b from Booking b " +
            "join Item i on b.itemId=i.id " +
            "where i.owner =  ?1 and b.start>?2 and b.status<>'REJECTED'" +
            "order by b.start desc ")
    List<Booking> findFutureByOwner(Long userId, LocalDateTime now);

    @Query(" select b from Booking b " +
            "join Item i on b.itemId=i.id " +
            "where i.owner =  ?1 and b.start<?2 and b.status<>'REJECTED'" +
            "order by b.start desc ")
    List<Booking> findPastByOwner(Long userId, LocalDateTime now);

    @Query(" select b from Booking b " +
            "join Item i on b.itemId=i.id " +
            "where i.owner =  ?1 and b.start<?2 and b.end>?2 " +
            "order by b.start desc ")
    List<Booking> findCurrentByOwner(Long userId, LocalDateTime now);
}
