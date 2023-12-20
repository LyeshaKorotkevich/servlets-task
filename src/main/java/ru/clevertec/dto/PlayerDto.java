package ru.clevertec.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import ru.clevertec.validator.annotation.TShirtNumber;

import java.time.LocalDate;

/**
 * {@link ru.clevertec.entity.Player}
 */
public record PlayerDto(

        @Pattern(regexp = "^[A-Za-zА-Яа-я]+$", message = "Имя должно содержать только буквы")
        @NotBlank
        String name,

        @Pattern(regexp = "^[A-Za-zА-Яа-я-]+$", message = "Фамилия должно содержать только буквы или -")
        @NotBlank
        String surname,

        @NotNull
        @Past
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dateBirth,

        @TShirtNumber
        int number) {
}