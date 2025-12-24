package com.simulation.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

public class RecordRequest {

    @Getter
    @Builder
    public static class RegisterDto {
        private String name;
        private float attempt1;
        private float attempt2;
        private float attempt3;
    }

}
