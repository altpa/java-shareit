package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Slf4j
public class CheckDateValidator implements ConstraintValidator<StartBeforeOrEqualEndDateValid, BookingDto> {
    @Override
    public void initialize(StartBeforeOrEqualEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        log.debug("+isValid - start: {}, end = {}, bookingDto = {}", start, end, bookingDto);

        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}