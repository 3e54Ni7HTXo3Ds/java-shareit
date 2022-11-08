package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.user.UserRepository;
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
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Collection<ItemResponseDto> findAll(Long userId) {
        List<ItemResponseDto> list =
                ItemMapper.mapToItemResponseDto(itemRepository.findItemByOwnerIdOrderByIdAsc(userId));
        for (ItemResponseDto i : list) {
            i = addLastNextBooking(i.getId(), userId, i);
            list.set(list.indexOf(i), i);
        }
        return list;
    }

    @Override
    public ItemResponseDto findById(Long itemId, Long userId)
            throws NotFoundParameterException {
        if (itemId > 0) {
            Optional<Item> item = itemRepository.findById(itemId);
            if (item.isPresent()) {
                ItemResponseDto itemResponseDto =
                        addLastNextBooking(itemId, userId, ItemMapper.toItemResponseDto(item.get()));
                List<Comment> comments = commentRepository.findByItem(itemRepository.findById(itemId).get());
                List<CommentResponseDto> commentResponseDtos = CommentMapper.mapToCommentResponseDto(comments);
                itemResponseDto.setCommentResponseDto((commentResponseDtos));
                return itemResponseDto;
            }
        } else log.error("Некорректный ID: {} ", itemId);
        throw new NotFoundParameterException("Некорректный ID");
    }

    ItemResponseDto addLastNextBooking(Long itemId, Long userId, ItemResponseDto itemResponseDto) {
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
    public ItemResponseDto create(Long userId, ItemDto itemDto) throws IncorrectParameterException {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(new User(userId, null, null));
        if (item.getAvailable() == null
                || item.getName() == null
                || item.getDescription() == null
                || item.getName().isBlank()
                || item.getDescription().isBlank()) {
            log.error("Неверные параметры вещи: {} ", item);
            throw new IncorrectParameterException("Неверные параметры вещи");
        }
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto update(Long itemId, Long userId, ItemDto itemDto) throws
            NotFoundParameterException, IncorrectParameterException, UpdateException {
        if (!itemRepository.existsById(itemId)) {
            log.error("Вещь не найдена: {} ", itemId);
            throw new UpdateException("Вещь не найдена");
        }
        Item itemNew = ItemMapper.toItem(itemDto);
        Item item = itemRepository.findById(itemId).get();
        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new NotFoundParameterException("Изменять может только создатель");
        }
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
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Override
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemResponseDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text.toLowerCase()).stream()
                .filter(r -> Objects.equals(r.getAvailable(), true))
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto createComment(Long userId, Long itemId, CommentDto commentDto)
            throws IncorrectParameterException {
        if (commentDto.getText().isBlank()) {
            log.error("Неверный комментарий: {} ", commentDto);
            throw new IncorrectParameterException("Неверный комментарий");
        }
        if (!itemRepository.existsById(itemId)) {
            log.error("Неверные параметры вещи: {} ", commentDto);
            throw new IncorrectParameterException("Неверные параметры вещи");
        }
        Item item = itemRepository.findById(itemId).get();
        User user = userRepository.findById(userId).get();
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
