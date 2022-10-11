package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerOrderByStartDesc(User booker,
                                               OffsetBasedPageRequest pageRequest); //all

    List<Booking> findByBookerAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime now); //future

    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, Booking.Status status);

    List<Booking> findByItemIdOrderByStartAsc(Long itemId);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User booker, LocalDateTime now,
                                                                            LocalDateTime now1);

    List<Booking> findByBookerAndEndIsBeforeOrderByStartDesc(User booker, LocalDateTime now);

    @Query("select b from Booking b " +
            "join Item i on b.item.id=i.id " +
            "where i.owner.id =  ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwner(Long userId, OffsetBasedPageRequest pageRequest);

    @Query(" select b from Booking b " +
            "join Item i on b.item.id=i.id " +
            "where i.owner.id =  ?1 and b.status='WAITING'" +
            "order by b.start desc ")
    List<Booking> findWaitingByOwner(Long userId);

    @Query(" select b from Booking b " +
            "join Item i on b.item.id=i.id " +
            "where i.owner.id =  ?1 and b.status='REJECTED'" +
            "order by b.start desc ")
    List<Booking> findRejectedByOwner(Long userId);

    @Query(" select b from Booking b " +
            "join Item i on b.item.id=i.id " +
            "where i.owner.id =  ?1 and b.start>?2 and b.status<>'REJECTED'" +
            "order by b.start desc ")
    List<Booking> findFutureByOwner(Long userId, LocalDateTime now);

    @Query(" select b from Booking b " +
            "join Item i on b.item.id=i.id " +
            "where i.owner.id =  ?1 and b.start<?2 and b.status<>'REJECTED'" +
            "order by b.start desc ")
    List<Booking> findPastByOwner(Long userId, LocalDateTime now);

    @Query(" select b from Booking b " +
            "join Item i on b.item.id=i.id " +
            "where i.owner.id =  ?1 and b.start<?2 and b.end>?2 " +
            "order by b.start desc ")
    List<Booking> findCurrentByOwner(Long userId, LocalDateTime now);
}
