package ru.practicum.shareit.item;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper implements Converter<Item, ItemDto> {

    @Override
    public ItemDto convert(Item item) {
        return toItemDto(item);
    }

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
}