package com.gruapim.controller;

import com.gruapim.dto.request.ChangePasswordRequest;
import com.gruapim.dto.request.UpdateProfileRequest;
import com.gruapim.dto.response.UserResponse;
import com.gruapim.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Perfil do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Retorna dados do usuário autenticado")
    public UserResponse getMe(@AuthenticationPrincipal UserDetails principal) {
        return userService.getByEmail(principal.getUsername());
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza o perfil do usuário autenticado")
    public UserResponse updateMe(@AuthenticationPrincipal UserDetails principal,
                                 @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(principal.getUsername(), request);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Altera a senha do usuário autenticado")
    public void changePassword(@AuthenticationPrincipal UserDetails principal,
                               @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getUsername(), request);
    }
}
