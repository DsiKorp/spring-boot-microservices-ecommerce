package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderCancelledEvent;
import com.ecommerce.notification_service.event.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventsListener {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = "notification-confirmed-queue")
    public void handleOrderConfirmedEvent(OrderConfirmedEvent orderConfirmedEvent) {
        log.info("OrderConfirmedEvent confirmed: {}", orderConfirmedEvent);
        log.info("Event received in Notification for order confirmed: {}", orderConfirmedEvent.orderNumber());

        // For the retry pattern to work, it must not have a try catch.
        //throw new RuntimeException("Simulating error: SMTP server not available");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("orders@dsklabs.com");
        message.setTo(orderConfirmedEvent.email());
        message.setSubject("Order Confirmation: " + orderConfirmedEvent.orderNumber());

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("🎉 Thank you for your purchase!\n\n");
        emailBody.append("Dear Customer,\n\n");
        emailBody.append("We are pleased to inform you that your order has been received and successfully confirmed.\n\n");
        emailBody.append("📦 Order Number: ").append(orderConfirmedEvent.orderNumber()).append("\n\n");
        emailBody.append("═══════════════════════════════════════\n");
        emailBody.append("           🛒 ORDER DETAILS\n");
        emailBody.append("═══════════════════════════════════════\n\n");

        BigDecimal orderTotal = BigDecimal.ZERO;
        int itemNumber = 1;
        DecimalFormat df = new DecimalFormat("#,##0.00");

        for (var item : orderConfirmedEvent.items()) {
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

        mailSender.send(message);
        log.info("✅ Email sent to: {} for Order {}", orderConfirmedEvent.email(), orderConfirmedEvent.orderNumber());
    }

    @RabbitListener(queues = "notification-cancelled-queue")
    public void handleOrderCancelledEvent(OrderCancelledEvent orderCancelledEvent) {
        log.info("OrderCancelledEvent received: {}", orderCancelledEvent);
        log.info("Event received in Notification for order cancelled: {}", orderCancelledEvent.orderNumber());

        //try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("orders@dsklabs.com");
        message.setTo(orderCancelledEvent.email());
        message.setSubject("Order Cancellation Confirmation: " + orderCancelledEvent.orderNumber());

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("📦 Order Cancellation Notice\n\n");
        emailBody.append("Dear Customer,\n\n");
        emailBody.append("We regret to inform you that your order has been cancelled.\n\n");
        emailBody.append("═══════════════════════════════════════\n");
        emailBody.append("           🚫 CANCELLATION DETAILS\n");
        emailBody.append("═══════════════════════════════════════\n\n");
        emailBody.append("📦 Order Number: ").append(orderCancelledEvent.orderNumber()).append("\n");
        emailBody.append("📝 Reason: ").append(orderCancelledEvent.reason() != null ? orderCancelledEvent.reason() : "Not specified").append("\n\n");
        emailBody.append("═══════════════════════════════════════\n\n");
        emailBody.append("💰 If you have already been charged, a full refund will be processed within 3-5 business days.\n\n");
        emailBody.append("We apologize for any inconvenience this may have caused.\n\n");
        emailBody.append("If you have any questions or need further assistance, please contact our support team.\n\n");
        emailBody.append("We hope to serve you again in the future.\n\n");
        emailBody.append("Best regards,\n");
        emailBody.append("═══════════════════════════════════════\n");
        emailBody.append("  🏢 DskLabs - Your Trusted Store\n");
        emailBody.append("  📧 support@dsklabs.com\n");
        emailBody.append("═══════════════════════════════════════\n");

        message.setText(emailBody.toString());

        mailSender.send(message);
        log.info("✅ Cancellation email sent to: {} for Order {}", orderCancelledEvent.email(), orderCancelledEvent.orderNumber());
    }
}