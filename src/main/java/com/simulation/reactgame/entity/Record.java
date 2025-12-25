package com.simulation.reactgame.entity;

import com.simulation.global.BaseTimeEntity;
import com.simulation.global.error.CustomException;
import com.simulation.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private float attempt1;
    private float attempt2;
    private float attempt3;
    private float averageTime;

    @Builder
    public Record(String name, float attempt1, float attempt2, float attempt3) {
        validate(name, attempt1, attempt2, attempt3);

        this.name = name;
        this.attempt1 = attempt1;
        this.attempt2 = attempt2;
        this.attempt3 = attempt3;
        this.averageTime = calculateAverageTime();
    }

    private float calculateAverageTime() {
        return (attempt1 + attempt2 + attempt3) / 3;
    }

    private void validate(String name, float... attempts) {
        if (name == null || name.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        for (float attempt : attempts) {
            if (attempt <= 0) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
        }
    }
}
