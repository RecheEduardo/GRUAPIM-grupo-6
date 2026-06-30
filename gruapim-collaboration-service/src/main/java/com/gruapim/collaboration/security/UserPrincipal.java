package com.gruapim.collaboration.security;

import java.util.UUID;

public record UserPrincipal(UUID id, String email, String name) {}
