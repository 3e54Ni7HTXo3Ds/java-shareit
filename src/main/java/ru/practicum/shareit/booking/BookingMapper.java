package ru.practicum.shareit.booking;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper implements Converter<Booking, BookingDto> {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    @Override
    public BookingDto convert(@NonNull Booking booking) {
        return toBookingDto(booking);
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        UserDto responseBooker = new UserDto();
        responseBooker.setId(booking.getBooker().getId());
        ItemDto responseItem = new ItemDto(booking.getItem().getId(), booking.getItem().getName());
        //  responseItem.setId();


        String startDate = dateTimeFormatter.format(booking.getStart());

        String endDate = dateTimeFormatter.format(booking.getEnd());

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
        Booking booking = new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                new Item(),
                new User(),
                bookingDto.getStatus()
        );
        booking.getItem().setId(bookingDto.getItemId());
        booking.getBooker().setId(bookingDto.getBookerId());

        return booking;
    }

    public static List<BookingResponseDto> mapToBookingResponseDto(Iterable<Booking> bookings) {
        List<BookingResponseDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingResponseDto(booking));
        }
        return dtos;
    }
}
