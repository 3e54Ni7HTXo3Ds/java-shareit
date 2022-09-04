package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class BookingMapper implements Converter<Booking, BookingDto> {

    @Override
    public BookingDto convert(Booking booking) {
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


        String startDate = DateTimeFormatter
                .ofPattern("yyyy.MM.dd hh:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(booking.getStart());

        String endDate = DateTimeFormatter
                .ofPattern("yyyy.MM.dd hh:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(booking.getEnd());

        return new BookingResponseDto(
                booking.getId(),
                startDate,
                endDate,
                booking.getItemId(),
                booking.getBooker(),
                booking.getStatus(),
                "fdf"
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
}
