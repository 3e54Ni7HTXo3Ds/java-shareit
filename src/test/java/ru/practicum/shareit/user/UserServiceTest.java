package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.error.exceptions.AuthException;
import ru.practicum.shareit.error.exceptions.CreatingException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userServiceImpl;
    private User user;
    private User user2;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userServiceImpl = new UserServiceImpl(userRepository);
        user = new User(1L, "John", "1@1.com");
        user2 = new User(2L, "user2", "2@2.com");
        userDto = UserMapper.toUserDto(user);

        when(userRepository.save(any())).then(invocation -> invocation.getArgument(0));

    }


    @Test
    void findById() throws NotFoundParameterException {
        //Assign
        Long idBelowZero = -1L;
        when(userRepository.findById(any())).thenReturn(Optional.of(user));


        //Act
        UserDto result = userServiceImpl.findById(user.getId());
        Optional<User> optionalUser = userRepository.findById(any());
        final NotFoundParameterException exception = assertThrows(NotFoundParameterException.class,
                () -> userServiceImpl.findById(idBelowZero));
        //Assert
        assertNotNull(result);
        assertTrue(true, String.valueOf(optionalUser.isPresent()));
        assertEquals(user.getId(), result.getId());
        assertEquals("Некорректный ID", exception.getMessage());

    }

    @Test
    void findAll() {
        //Assign
        when(userRepository.findAll()).thenReturn(List.of(user));
        //Act

        var result = userServiceImpl.findAll();

        //Assert
        assertNotNull(result);
        assertEquals(userDto.getId(), result.get(0).getId());
        assertEquals(userDto, result.get(0));
    }

    @Test
    void create() throws CreatingException, IncorrectParameterException {
        //Assign
        UserDto emailnull = new UserDto(1L, null, null);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        //Act

        var result = userServiceImpl.create(userDto);
        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> userServiceImpl.create(emailnull));

        //Assert
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals("Email не валидный", exception.getMessage());
    }


    @Test
    void update() throws CreatingException, IncorrectParameterException, NotFoundParameterException {

        //Assign
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        //Act

        var result = userServiceImpl.update(user.getId(), UserMapper.toUserDto(user2));

        //Assert
        assertNotNull(result);
        assertEquals(user2.getName(), result.getName());
        assertEquals(user2.getEmail(), result.getEmail());
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void delete() {
        //Assign
        Long idBelowZero = -1L;
        when(userRepository.existsById(any())).thenReturn(true);

        //Act
        userServiceImpl.delete(user.getId());
        userServiceImpl.delete(idBelowZero);

        //Assert
        verify(userRepository, Mockito.times(1)).deleteById(user.getId());
        verify(userRepository, Mockito.never()).deleteById(idBelowZero);
    }

    @Test
    void uniqueEmail() {

        //Assign
        when(userRepository.findAll()).thenReturn(List.of(user));
        //Act
        Boolean flag = userServiceImpl.uniqueEmail(user2);
        //Assert
        assertEquals(flag, true);
        verify(userRepository, Mockito.times(1)).findAll();

    }

    @Test
    void userExists() {

        //Assign

        when(userRepository.findAll()).thenReturn(List.of(user));
        //Act
        Boolean flag = userServiceImpl.userExists(user.getId());
        //Assert
        assertEquals(flag, true);
        verify(userRepository, Mockito.times(1)).findAll();

    }

    @Test
    void auth() throws AuthException {
        //Assign

        when(userRepository.findAll()).thenReturn(List.of(user));
        //Act
        userServiceImpl.auth(user.getId());

        final AuthException exception = assertThrows(AuthException.class,
                () -> userServiceImpl.auth(user2.getId()));
        //Assert

        verify(userRepository, Mockito.times(2)).findAll();

        assertEquals("Такого пользователя нет", exception.getMessage());

    }


}
