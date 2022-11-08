package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Item item3;


    @BeforeEach
    void setUp() {

        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "Sam", "1@1.com");
        item1 = new Item(
                1L,
                "Ионный трансформатор",
                "Внеземные технологии",
                true,
                user2,
                null
        );
        item2 = new Item(
                2L,
                "не трансформатор",
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
    }

    @Test
    void search() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        var result = itemRepository.search("технологии");

        assertNotNull(result);
        assertTrue(true, String.valueOf((result.size() == 2)));
        assertTrue(true, String.valueOf((result.containsAll(List.of(item1, item2)))));
        assertFalse(false, String.valueOf((result.contains((item3)))));
    }
}
