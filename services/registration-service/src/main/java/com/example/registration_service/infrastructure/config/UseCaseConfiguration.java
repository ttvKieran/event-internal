package com.example.registration_service.infrastructure.config;

import com.example.registration_service.application.port.in.*;
import com.example.registration_service.application.port.out.ProcessedMessageStore;
import com.example.registration_service.application.port.out.RegistrationCampaignRepository;
import com.example.registration_service.application.port.out.RegistrationIdGenerator;
import com.example.registration_service.application.port.out.RegistrationMessagePort;
import com.example.registration_service.application.port.out.RegistrationRepository;
import com.example.registration_service.application.port.out.SagaCommandPort;
import com.example.registration_service.application.port.out.TicketCachePort;
import com.example.registration_service.application.service.*;
import com.example.registration_service.infrastructure.transaction.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
public class UseCaseConfiguration {

    @Bean
    Clock systemClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    RegistrationIdGenerator registrationIdGenerator(){
        return new RegistrationIdGenerator() {
            @Override
            public UUID nextId() {
                return UUID.randomUUID();
            }
        };
    }

    @Bean
    HandlePaymentResult handlePaymentResult(
        ProcessedMessageStore processedMessages, SagaCommandPort sagaCommands,
        Clock clock, TransactionTemplate applicationTransactionTemplate) {

        HandlePaymentResult interactor =
            new HandlePaymentResultInteractor(processedMessages, sagaCommands, clock);

        return new TransactionalHandlePaymentResult(interactor, applicationTransactionTemplate);
    }

    @Bean
    HandleEventLifecycle handleEventLifecycle(
        ProcessedMessageStore processedMessages, RegistrationCampaignUseCase campaigns,
        Clock clock, TransactionTemplate applicationTransactionTemplate) {

        HandleEventLifecycle interactor =
            new HandleEventLifecycleInteractor(processedMessages, campaigns, clock);

        return new TransactionalHandleEventLifecycle(interactor, applicationTransactionTemplate);
    }

    @Bean
    HandleRegistrationCommand handleRegistrationCommand(
        ProcessedMessageStore processedMessages, RegistrationCommandUseCase registrations,
        Clock clock, TransactionTemplate applicationTransactionTemplate) {

        HandleRegistrationCommand interactor =
            new HandleRegistrationCommandInteractor(processedMessages, registrations, clock);

        return new TransactionalHandleRegistrationCommand(interactor, applicationTransactionTemplate);
    }

    @Bean
    TransactionTemplate applicationTransactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    TransactionalRegistrationCampaignUseCase registrationCampaignUseCases(
        RegistrationCampaignRepository campaignRepository, TicketCachePort ticketCache,
        Clock clock, TransactionTemplate applicationTransactionTemplate) {

        RegistrationCampaignServiceImpl interactor =
            new RegistrationCampaignServiceImpl(campaignRepository, ticketCache, clock);

        return new TransactionalRegistrationCampaignUseCase(interactor, interactor, applicationTransactionTemplate);
    }

    @Bean
    TransactionalRegistrationUseCase registrationUseCases(
        RegistrationCampaignRepository campaignRepository, RegistrationRepository registrationRepository,
        RegistrationMessagePort messagePort, TicketCachePort ticketCache,
        RegistrationIdGenerator idGenerator, Clock clock, TransactionTemplate applicationTransactionTemplate) {

        RegistrationServiceImpl interactor =
            new RegistrationServiceImpl(campaignRepository,
                registrationRepository, messagePort, ticketCache,
                idGenerator, clock);

        return new TransactionalRegistrationUseCase(interactor, interactor, applicationTransactionTemplate);
    }
}
