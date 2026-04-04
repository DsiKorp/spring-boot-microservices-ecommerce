package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventsListener {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = "notification-queue")
    public void handleOrderConfirmedEvent(OrderPlacedEvent orderPlacedEvent) {
        log.info("OrderPlacedEvent confirmed: {}", orderPlacedEvent);
        log.info("Event received in Notification for order confirmed: {}", orderPlacedEvent.orderNumber());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("orders@dsklabs.com");
        message.setTo(orderPlacedEvent.email());
        message.setSubject("Order Confirmation: " + orderPlacedEvent.orderNumber());

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("🎉 Thank you for your purchase!\n\n");
        emailBody.append("Dear Customer,\n\n");
        emailBody.append("We are pleased to inform you that your order has been received and successfully confirmed.\n\n");
        emailBody.append("📦 Order Number: ").append(orderPlacedEvent.orderNumber()).append("\n\n");
        emailBody.append("═══════════════════════════════════════\n");
        emailBody.append("           🛒 ORDER DETAILS\n");
        emailBody.append("═══════════════════════════════════════\n\n");

        BigDecimal orderTotal = BigDecimal.ZERO;
        int itemNumber = 1;
        DecimalFormat df = new DecimalFormat("#,##0.00");

        for (var item : orderPlacedEvent.items()) {
            BigDecimal price = new BigDecimal(item.price());
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.quantity()));
            orderTotal = orderTotal.add(subtotal);

            emailBody.append(String.format("  ✨ Item #%d: %s\n", itemNumber++, item.sku()));
            emailBody.append(String.format("     📦 Quantity: %d\n", item.quantity()));
            emailBody.append(String.format("     💰 Unit Price: $%s\n", df.format(price)));
            emailBody.append(String.format("     💵 Subtotal: $%s\n\n", df.format(subtotal)));
        }

        emailBody.append("═══════════════════════════════════════\n");
        emailBody.append(String.format("  💳 TOTAL AMOUNT: $%s\n", df.format(orderTotal)));
        emailBody.append("═══════════════════════════════════════\n\n");
        emailBody.append("📮 We will keep you updated on your shipping status.\n\n");
        emailBody.append("If you have any questions, please do not hesitate to contact us.\n\n");
        emailBody.append("🙏 Thank you for choosing us!\n\n");
        emailBody.append("Best regards,\n");
        emailBody.append("═══════════════════════════════════════\n");
        emailBody.append("  🏢 DskLabs - Your Trusted Store\n");
        emailBody.append("  📧 support@dsklabs.com\n");
        emailBody.append("═══════════════════════════════════════\n");

        message.setText(emailBody.toString());

        try {
            mailSender.send(message);
            log.info("✅\u200B Email sent to: {} for Order {}", orderPlacedEvent.email(), orderPlacedEvent.orderNumber());
        } catch (Exception e) {
            log.error("❌\u200B Error sending email to {}: {}", orderPlacedEvent.email(), e.getMessage());
        }
    }
}
