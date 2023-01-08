package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;
    private Item item3;

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
                "не Трансформатор1 кииборг",
                "земные теХнологии",
                false,
                user1,
                null
        );
        item3 = new Item(
                3L,
                "плазменный трансформатор",
                "марсианские изыскания",
                false,
                user1,
                null
        );

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }


    @Test
    void searchDesc() {
        var result = itemRepository.search("технологии");
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(true, String.valueOf((result.containsAll(List.of(item1, item2)))));
        assertFalse(false, String.valueOf((result.contains((item3)))));
    }

    @Test
    void searchName() {
        var result = itemRepository.search("трансформатор");
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(true, String.valueOf(result.containsAll(List.of(item2, item3))));
        assertFalse(false, String.valueOf((result.contains((item1)))));
    }

    @Test
    void searchNegative() {
        var result = itemRepository.search("киборг");
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
