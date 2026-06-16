package com.gruapim.dto.request;

import com.gruapim.domain.enums.UserRole;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Nome obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String name,

        @NotBlank(message = "E-mail obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Senha obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        String password,

        UserRole role
) {}
