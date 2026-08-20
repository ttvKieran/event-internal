package com.example.notification_service.infrastructure.email;

import com.example.notification_service.application.dto.message.EventCancelledMessage;
import com.example.notification_service.application.dto.message.PaidRegistrationRolledBackMessage;
import com.example.notification_service.application.dto.message.RegistrationConfirmedMessage;
import com.example.notification_service.application.port.out.NotificationTemplatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class ThymeleafTemplateAdapter implements NotificationTemplatePort {

    private final TemplateEngine templateEngine;

    @Override
    public String renderTicketConfirmed(RegistrationConfirmedMessage message) {
        Context ctx = new Context();
        ctx.setVariable("registrationId", message.getRegistrationId());
        ctx.setVariable("campaignId", message.getCampaignId());
        ctx.setVariable("confirmedAt", message.getConfirmedAt());
        return templateEngine.process("ticket-confirmed", ctx);
    }

    @Override
    public String renderTicketCancelled(PaidRegistrationRolledBackMessage message) {
        Context ctx = new Context();
        ctx.setVariable("registrationId", message.getRegistrationId());
        ctx.setVariable("reason", message.getReason());
        ctx.setVariable("rolledBackAt", message.getRolledBackAt());
        return templateEngine.process("ticket-cancelled", ctx);
    }

    @Override
    public String renderEventCancelled(EventCancelledMessage message) {
        Context ctx = new Context();
        ctx.setVariable("eventId", message.getEventId());
        ctx.setVariable("reason", message.getReason());
        ctx.setVariable("cancelledAt", message.getCancelledAt());
        return templateEngine.process("event-cancelled", ctx);
    }
}
