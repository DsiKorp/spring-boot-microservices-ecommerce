package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventsListener {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = "notification-queue")
    public void handleOrderPlacedEvent(OrderPlacedEvent orderPlacedEvent) {
        log.info("OrderPlacedEvent received: {}", orderPlacedEvent);
        log.info("Event received in Notification for order received: {}", orderPlacedEvent.orderNumber());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("orders@dsklabs.com");
        message.setTo(orderPlacedEvent.email());
        message.setSubject("Order Confirmation: " + orderPlacedEvent.orderNumber());

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Hello!\n\n");
        emailBody.append("Your order number is: ").append(orderPlacedEvent.orderNumber()).append("\n\n");
        emailBody.append("Order Details:\n");
        emailBody.append("─────────────────────────────────\n");

        BigDecimal orderTotal = BigDecimal.ZERO;
        int itemNumber = 1;

        for (var item : orderPlacedEvent.items()) {
            BigDecimal price = new BigDecimal(item.price());
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.quantity()));
            orderTotal = orderTotal.add(subtotal);

            emailBody.append(String.format("%d. %s\n", itemNumber++, item.sku()));
            emailBody.append(String.format("   Quantity: %d\n", item.quantity()));
            emailBody.append(String.format("   Price: $%s\n", item.price()));
            emailBody.append(String.format("   Subtotal: $%s\n\n", subtotal));
        }

        emailBody.append("─────────────────────────────────\n");
        emailBody.append(String.format("TOTAL: $%s\n\n", orderTotal));
        emailBody.append("Thank you for your order!\n");
        emailBody.append("DskLabs");

        message.setText(emailBody.toString());

        try {
            mailSender.send(message);
            log.info("✅\u200B Email sent to: {} for Order {}", orderPlacedEvent.email(), orderPlacedEvent.orderNumber());
        } catch (Exception e) {
            log.error("❌\u200B Error sending email to {}: {}", orderPlacedEvent.email(), e.getMessage());
        }
    }
}
