package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = { "db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemIntegrTest {

    private final ItemServiceImpl itemServiceImpl;

    private final EntityManager em;

    private User user;
    private ItemDto itemDto1;


    @BeforeEach
    void setUp() {
        user = new User(null, "John", "john.doe@mail.com");
        em.persist(user);
        Item item1 = new Item(
                1L,
                "Ионный трансформатор",
                "Внеземные технологии",
                true,
                user,
                null
        );
        itemDto1 = ItemMapper.toItemDto(item1);
    }

    @AfterEach
    void endUp() {
        em.createNativeQuery("truncate table items");
    }

    @Test
    void create() throws IncorrectParameterException {

        ItemResponseDto itemResponseDto = itemServiceImpl.create(user.getId(), itemDto1);

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id=:id", Item.class);
        Item item = query.setParameter("id", itemDto1.getId()).getSingleResult();

        assertNotNull(itemResponseDto);
        assertEquals(itemResponseDto.getId(), item.getId());
        assertEquals(itemResponseDto.getName(), item.getName());
        assertEquals(itemResponseDto.getDescription(), item.getDescription());
        assertEquals(itemResponseDto.getAvailable(), item.getAvailable());
        assertEquals(itemResponseDto.getOwnerId(), user.getId());
    }

}
