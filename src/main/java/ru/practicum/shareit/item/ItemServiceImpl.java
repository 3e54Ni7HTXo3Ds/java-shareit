package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
@Slf4j
@AllArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ConversionService conversionService;

    @Override
    public Collection<ItemResponseDto> findAll(Long userId) {
        List<ItemResponseDto> list = itemRepository.findAll().stream()
                .filter(r -> Objects.equals(r.getOwner(), userId))
                .sorted(Comparator.comparing(Item::getId))
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
        for (ItemResponseDto i : list) {
            i = addLastNextBooking(i.getId(), userId, i);
            list.set(list.indexOf(i), i);
        }
        return list;
    }

    @Override
    public Item findById(Long itemId) throws NotFoundParameterException {
        if (itemId > 0) {
            if (itemRepository.findById(itemId).isPresent()) {
                return itemRepository.findById(itemId).get();
            }
        } else log.error("Некорректный ID: {} ", itemId);
        throw new NotFoundParameterException("Некорректный ID");
    }

    @Override
    public ItemResponseDto findByIdDto(Long itemId, Long userId)
            throws NotFoundParameterException {
        if (itemId > 0) {
            if (itemRepository.findById(itemId).isPresent()) {
                ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(itemRepository.findById(itemId).get());
                itemResponseDto = addLastNextBooking(itemId, userId, itemResponseDto);
                List<Comment> comments = commentRepository.findByItem(findById(itemId));
                List<CommentResponseDto> commentResponseDtos =
                        CommentMapper.mapToCommentResponseDto(comments);
                itemResponseDto.setCommentResponseDto((commentResponseDtos));
                return itemResponseDto;
            }
        } else log.error("Некорректный ID: {} ", itemId);
        throw new NotFoundParameterException("Некорректный ID");
    }

    private ItemResponseDto addLastNextBooking(Long itemId, Long userId, ItemResponseDto itemResponseDto) {
        if (Objects.equals(itemResponseDto.getOwnerId(), userId)) {
            List<Booking> list = bookingRepository.findByItemIdOrderByStartAsc(itemId);
            if (list != null && list.size() > 0) {
                itemResponseDto.setLastBooking(BookingMapper.toBookingDto(list.get(0)));
            }
            if (list != null && list.size() > 1 && list.get(1) != null) {
                itemResponseDto.setNextBooking(BookingMapper.toBookingDto(list.get(1)));
            }
        }
        return itemResponseDto;
    }

    @Override
    public Item create(Long userId, ItemDto itemDto) throws IncorrectParameterException {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        if (
                item.getAvailable() == null || item.getName() == null || item.getDescription() == null ||
                        item.getName().isBlank() ||
                        item.getDescription().isBlank()) {
            log.error("Неверные параметры вещи: {} ", item);
            throw new IncorrectParameterException("Неверные параметры вещи");
        }
        return itemRepository.save(item);
    }

    @Override
    public Item update(Long itemId, Long userId, ItemDto itemDto) throws
            NotFoundParameterException, IncorrectParameterException, UpdateException {
        if (!itemRepository.existsById(itemId)) {
            log.error("Вещь не найдена: {} ", itemId);
            throw new UpdateException("Вещь не найдена");
        }
        if (!Objects.equals(userId, findById(itemId).getOwner())) {
            throw new NotFoundParameterException("Изменять может только создатель");
        }
        Item itemNew = ItemMapper.toItem(itemDto);
        Item item = findById(itemId);
        if (itemNew.getDescription() != null) {
            item.setDescription(itemNew.getDescription());
        }
        if (itemNew.getName() != null) {
            item.setName(itemNew.getName());
        }
        if (itemNew.getAvailable() != null) {
            item.setAvailable(itemNew.getAvailable());
        }

        log.info("Обновлена вещь: {} ", item);
        return itemRepository.save(item);
    }

    @Override
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text.toLowerCase()).stream()
                .filter(r -> Objects.equals(r.getAvailable(), true))
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean itemExists(Long itemId) {
        return itemRepository.findAll().stream()
                .anyMatch(r -> Objects.equals(r.getId(), itemId));
    }

    @Override
    public CommentResponseDto create(Long userId, Long itemId, CommentDto commentDto)
            throws IncorrectParameterException, NotFoundParameterException {

        if (commentDto.getText().isBlank()) {
            log.error("Неверный комментарий: {} ", commentDto);
            throw new IncorrectParameterException("Неверный комментарий");
        }

        if (!itemRepository.existsById(itemId)) {
            log.error("Неверные параметры вещи: {} ", commentDto);
            throw new IncorrectParameterException("Неверные параметры вещи");
        }
        Item item = findById(itemId);
        User user = userService.findById(userId);
        Comment comment = CommentMapper.toComment(commentDto);
        List<Booking> listOfPastBookings =
                bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(user,
                        LocalDateTime.now());
        boolean ableToComment = false;
        for (Booking b : listOfPastBookings) {
            if (Objects.equals(b.getItem().getId(), itemId) && b.getStatus() != Booking.Status.REJECTED) {
                ableToComment = true;
                break;
            }
        }
        if (!ableToComment) {
            log.error("Данный пользователь не может комментировать эту вещь: {} ", userId);
            throw new IncorrectParameterException("Данный пользователь не может комментировать эту вещь");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.toCommentResponseDto(comment);
    }
}
