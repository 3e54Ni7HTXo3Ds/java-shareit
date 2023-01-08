package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;


    @Override
    public List<ItemRequestResponseDto> findAll(Long userId) {

        List<ItemRequestResponseDto> list = ItemRequestMapper.mapToItemRequestResponseDto(
                itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId));

        return getItemRequestResponseDtos(list);
    }

    private List<ItemRequestResponseDto> getItemRequestResponseDtos(List<ItemRequestResponseDto> list) {
        List<Long> requestIds = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        List<Item> requestsItems = new ArrayList<>();

        if (list.size() > 0) {
            for (ItemRequestResponseDto itemRequestResponseDto : list) {
                requestIds.add(itemRequestResponseDto.getId());
            }
            items = itemRepository.findByRequestIdIn(requestIds);
        }

        if (items.size() > 0) {
            for (ItemRequestResponseDto itemRequestResponseDto : list) { //берем весь список реквестов
                for (Item item : items) {                                  // берем весь список вещей полученный из базы
                    if (Objects.equals(item.getRequestId(), itemRequestResponseDto.getId())) {
                        requestsItems.add(item); // если в списке вещей присутствуют вещи с таким  RequestId -
                        // добавляем в отдельный список
                    }
                }
                itemRequestResponseDto.setItems(ItemMapper.mapToItemResponseDto(requestsItems)); //устанавливаем к
                // запросу список вещей которые добавили другие пользователи
                requestsItems.clear();
            }
        }
        return list;
    }

    @Override
    public List<ItemRequestResponseDto> findAllPageble(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);

        List<ItemRequestResponseDto> list = ItemRequestMapper.mapToItemRequestResponseDto(
                itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageRequest));

        return getItemRequestResponseDtos(list);
    }


    @Override
    public ItemRequestResponseDto findById(Long itemRequestId, Long userId)
            throws NotFoundParameterException {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                new NotFoundParameterException("Нет такого реквеста"));
        ItemRequestResponseDto itemRequestResponseDto =
                ItemRequestMapper.toItemRequestResponseDto(itemRequest);
        itemRequestResponseDto.setItems(
                ItemMapper.mapToItemResponseDto(itemRepository.findItemByRequestId(itemRequestId)));
        return itemRequestResponseDto;
    }


    @Override
    public ItemRequestResponseDto create(Long userId, ItemRequestDto itemRequestDto)
            throws IncorrectParameterException {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(new User(userId, null, null));
        return ItemRequestMapper.toItemRequestResponseDto(itemRequestRepository.save(itemRequest));
    }
}
