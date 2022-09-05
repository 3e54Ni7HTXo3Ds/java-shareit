package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.Column;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper implements Converter<Booking, BookingDto> {

  //  private final ItemRepository itemRepository;

    @Override
    public BookingDto convert(@NonNull Booking booking) {
        return toBookingDto(booking);
    }


    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItemId(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {

        UserDto responseBooker = new UserDto();
        responseBooker.setId(booking.getBooker());
        ItemDto responseItem = new ItemDto();
        responseItem.setId(booking.getItemId());
       // responseItem.setName(itemRepository.findById(booking.getItemId()).get().getName());

        String startDate = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(booking.getStart());

        String endDate = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(booking.getEnd());

        return new BookingResponseDto(
                booking.getId(),
                booking.getStatus(),
                responseBooker,
                responseItem,
                startDate,
                endDate
        );
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
                bookingDto.getBooker(),
                bookingDto.getStatus()
        );
    }

    public static List<BookingResponseDto> mapToBookingResponseDto(Iterable<Booking> bookings) {
        List<BookingResponseDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingResponseDto(booking));
        }
        return dtos;
    }
}
