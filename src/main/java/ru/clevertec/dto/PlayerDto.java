package ru.clevertec.dto;

import java.time.LocalDate;

/**
 * {@link ru.clevertec.entity.Player}
 */
public record PlayerDto(

        String name,

        String surname,

        LocalDate dateBirth,

        int number) {
}

