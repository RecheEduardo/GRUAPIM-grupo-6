package com.gruapim.collaboration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Microsserviço de Colaboração e Comunicação do GRUAPIM.
 *
 * <p>Resultado da extração estratégica do Bounded Context "Colaboração e Comunicação"
 * (Fase 5 do Event Storming): chat em tempo real, central de notificações e atas de reunião.
 * Roda de forma independente do monólito {@code gruapim-backend}, com banco de dados próprio
 * ({@code gruapim_collab}) e deploy/escala dedicados.</p>
 */
@SpringBootApplication
@EnableJpaAuditing
public class CollaborationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollaborationServiceApplication.class, args);
    }
}
