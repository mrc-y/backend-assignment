package com.katanox.api.booking.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Guest {
    @NotEmpty(message = "Guest name should be provided")
    private String name;
    @NotEmpty(message = "Guest surname should be provided")
    private String surname;
    private LocalDate birthdate;
}
