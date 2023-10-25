package ru.antisessa.util.car;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarErrorResponse {
    private String message;
    private long timestamp;
}
