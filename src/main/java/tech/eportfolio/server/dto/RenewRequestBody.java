package tech.eportfolio.server.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RenewRequestBody {
    @NotBlank
    private String refreshToken;
}
