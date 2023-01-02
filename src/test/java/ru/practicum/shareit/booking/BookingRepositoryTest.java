package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private Item item1;
    private Item item2;
    private Item item3;

    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;
    private Booking booking6;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "Sam", "1@1.com");
        item1 = new Item(
                1L,
                "Ионный двигатель",
                "Внеземные технологии",
                true,
                user2,
                null
        );
        item2 = new Item(
                2L,
                "не Тансформатор1 кииборг",
                "земные теХнологии",
                false,
                user1,
                null
        );
        item3 = new Item(
                2L,
                "плазменный трансформатор",
                "марсианские изыскания",
                false,
                user1,
                null
        );

        booking1 = new Booking(1L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(30), item1,
                user1, Booking.Status.WAITING);
        booking2 = new Booking(2L, LocalDateTime.now().minusMinutes(30), LocalDateTime.now().minusMinutes(10), item2,
                user2, Booking.Status.REJECTED);
        booking3 = new Booking(3L, LocalDateTime.now().minusMinutes(60), LocalDateTime.now().plusMinutes(30), item3,
                user2, Booking.Status.APPROVED);
        booking4 = new Booking(4L, LocalDateTime.now().minusMinutes(120), LocalDateTime.now().minusMinutes(10), item2,
                user2, Booking.Status.WAITING);

        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);
    }

    @Test
    void findAllByOwnerPageble() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 10);
        var result = bookingRepository.findAllByOwnerPageble(1L, pageRequest);
        assertNotNull(result);
        assertEquals(result.size(),3);
        assertEquals((booking2).getId(), result.get(0).getId());
        assertEquals((booking3).getId(), result.get(1).getId());
        assertEquals((booking4).getId(), result.get(2).getId());


    }
    @Test
    void findWaitingByOwner() {
        var result = bookingRepository.findWaitingByOwner(2L);
        assertNotNull(result);
        assertEquals((booking1).getId(), result.get(0).getId());
        assertEquals(List.of(booking1).get(0).getBooker().getId(), result.get(0).getBooker().getId());
    }
    @Test
    void findRejectedByOwner() {
        var result = bookingRepository.findRejectedByOwner(1L);
        assertNotNull(result);
        assertEquals((booking2).getId(), result.get(0).getId());
        assertEquals(List.of(booking2).get(0).getBooker().getId(), result.get(0).getBooker().getId());
    }
    @Test
    void findFutureByOwner() {
        var result = bookingRepository.findFutureByOwner(2L, LocalDateTime.now());
        assertNotNull(result);
        assertEquals((booking1).getId(), result.get(0).getId());
        assertEquals(List.of(booking1).get(0).getBooker().getId(), result.get(0).getBooker().getId());
    }
    @Test
    void findPastByOwner() {
        var result = bookingRepository.findPastByOwner(1L, LocalDateTime.now());
        assertNotNull(result);
        assertEquals((booking4).getId(), result.get(0).getId());
        assertEquals(List.of(booking4).get(0).getBooker().getId(), result.get(0).getBooker().getId());
    }
    @Test
    void findCurrentByOwner() {
        var result = bookingRepository.findCurrentByOwner(1L, LocalDateTime.now());
        assertNotNull(result);
        assertEquals((booking3).getId(), result.get(0).getId());
        assertEquals(List.of(booking3).get(0).getBooker().getId(), result.get(0).getBooker().getId());
    }


}
