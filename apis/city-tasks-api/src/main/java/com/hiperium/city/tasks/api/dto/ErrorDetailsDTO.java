package com.hiperium.city.tasks.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetailsDTO {

    private ZonedDateTime errorDate;
    private String requestedPath;
    private String errorMessage;
    private String errorCode;
}
