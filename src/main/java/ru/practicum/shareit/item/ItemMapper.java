package ru.practicum.shareit.item;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null);
    }

    public static ItemResponseDto toItemResponseDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequestId() != null ? item.getRequestId() : null,
                null,
                null,
                null);
    }

    public static List<ItemResponseDto> mapToItemResponseDto(Iterable<Item> items) {
        List<ItemResponseDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemResponseDto(item));
        }
        return dtos;
    }

}