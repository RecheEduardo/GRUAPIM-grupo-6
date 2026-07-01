package com.gruapim.collaboration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Autentica a conexão STOMP a partir do JWT compartilhado. O cliente envia o
 * token no header nativo {@code Authorization: Bearer <jwt>} do frame CONNECT;
 * validado o token, o {@link UserPrincipal} fica associado à sessão WebSocket
 * e flui para os {@code @MessageMapping} via {@code @AuthenticationPrincipal}.
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String header = accessor.getFirstNativeHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtService.isTokenValid(token)) {
                    UserPrincipal principal = new UserPrincipal(
                            jwtService.extractUserId(token),
                            jwtService.extractEmail(token),
                            jwtService.extractName(token));
                    var authentication = new UsernamePasswordAuthenticationToken(
                            principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    accessor.setUser(authentication);
                }
            }
        }
        return message;
    }
}
