package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.model.OutboxEvent;
import com.ecommerce.order_service.model.OutboxStatus;
import com.ecommerce.order_service.repository.OutboxRepository;
import com.ecommerce.order_service.service.OutboxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; // Corregido: suele ser com.fasterxml.jackson en lugar de tools.jackson
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveOrderPlacedEvent(OrderPlacedEvent event, boolean isprocessed) {
        try {
            // 1. Convertir el Record/Objeto a un String JSON
            String payload = objectMapper.writeValueAsString(event);

            // 2. Crear la entidad que definimos antes
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(event.orderNumber()) // Usamos el orderNumber de la imagen anterior
                    .type("ORDER_PLACED")
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .processed(isprocessed)
                    .processedAt(null)
                    .build();

            // 3. Guardar en Postgres
            outboxRepository.save(outboxEvent);

            log.info("💾 Evento Outbox guardado para la orden: {}", event.orderNumber());

        } catch (JsonProcessingException e) {
            log.error("❌ Error al convertir el evento a JSON", e);
            throw new RuntimeException("No se pudo serializar el evento");
        }
    }

    @Override
    public List<OutboxEvent> getPendingEvents() {
        return outboxRepository.findByProcessedFalse();
    }

    @Override
    public void markAsProcessed(Long id) {
        outboxRepository.findById(id).ifPresent(event -> {
            event.setProcessed(true);
            event.setProcessedAt(LocalDateTime.now());
            event.setStatus(OutboxStatus.PROCESSED);

            outboxRepository.save(event);
            log.info("Event marked as processed. ID: {}", id);
        });
    }
}