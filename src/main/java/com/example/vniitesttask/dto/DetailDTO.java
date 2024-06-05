package com.example.vniitesttask.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailDTO {

    @NotNull
    @Positive
    private Long masterDocNumber;

    @NotNull
    @NotEmpty
    @Size(min = 10, max = 255)
    private String detailName;

    @NotNull
    @Positive
    private Long costValue;
}
