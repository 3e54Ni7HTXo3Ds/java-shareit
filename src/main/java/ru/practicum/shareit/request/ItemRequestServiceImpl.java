package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
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
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;


    @Override
    public Collection<ItemRequestResponseDto> findAll(Long userId) {
        List<ItemRequestResponseDto> list =
                ItemRequestMapper.mapToItemRequestResponseDto(itemRequestRepository.findAll());
        if (list.size() > 0) {
            for (ItemRequestResponseDto item : list) {
                item.setItems(ItemMapper.mapToItemResponseDto(itemRepository.findItemByRequestId(item.getId())));
            }
        }

        return list;
    }

    @Override
    public Collection<ItemRequestResponseDto> findAllPageble(Long userId, Integer from, Integer size)
            throws IncorrectParameterException {

        if (from == null || size == null) {
            List<ItemRequestResponseDto> list =
                    ItemRequestMapper.mapToItemRequestResponseDto(itemRequestRepository.findAll());

            return list;
        }
        if (from < 0 || size <= 0) {
            log.error("Неверные параметры : {} , {} ", from, size);
            throw new IncorrectParameterException("Неверные параметры ");
        }
        PageRequest pageRequest = PageRequest.of(from, size);
        List<ItemRequestResponseDto> list =
                ItemRequestMapper.mapToItemRequestResponseDto(
                        itemRequestRepository.findByRequestorIdNot(userId, pageRequest));
        if (list.size() > 0) {
            for (ItemRequestResponseDto item : list) {
                item.setItems(ItemMapper.mapToItemResponseDto(itemRepository.findItemByRequestId(item.getId())));
            }}
        return list;
    }


    @Override
    public ItemRequestResponseDto findByIdDto(Long itemRequestId, Long userId)
            throws NotFoundParameterException {
        if (itemRequestId > 0) {
            Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemRequestId);
            if (itemRequest.isPresent()) {
                ItemRequestResponseDto itemRequestResponseDto =
                        ItemRequestMapper.toItemRequestResponseDto(itemRequest.get());
                return itemRequestResponseDto;
            }
        } else log.error("Некорректный ID: {} ", itemRequestId);
        throw new NotFoundParameterException("Некорректный ID");
    }


    @Override
    public ItemRequestResponseDto create(Long userId, ItemRequestDto itemRequestDto)
            throws IncorrectParameterException {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(new User(userId, null, null));
        if (itemRequest.getDescription() == null ||
                itemRequest.getDescription().isBlank()) {
            log.error("Неверные параметры описания: {} ", itemRequest);
            throw new IncorrectParameterException("Неверные параметры описания");
        }
        return ItemRequestMapper.toItemRequestResponseDto(itemRequestRepository.save(itemRequest));
    }


}
