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

    @BeforeEach
    void setUp() {
        User user1 = new User(1L, "John", "john.doe@mail.com");
        User user2 = new User(2L, "Sam", "1@1.com");
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
        booking2 = new Booking(2L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(30), item2,
                user2, Booking.Status.REJECTED);
        booking3 = new Booking(3L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(30), item3,
                user2, Booking.Status.APPROVED);

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
    }

    @Test
    void findWaitingByOwner() {
        var result = bookingRepository.findWaitingByOwner(2L);
        assertNotNull(result);
        assertEquals((booking1).getId(), result.get(0).getId());
        assertEquals(List.of(booking1).get(0).getBooker().getId(), result.get(0).getBooker().getId());
    }


}
