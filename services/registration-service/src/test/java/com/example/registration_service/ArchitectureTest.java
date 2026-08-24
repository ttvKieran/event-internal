package com.example.registration_service;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
    packages = "com.example.registration_service",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    @ArchTest
    static final ArchRule DOMAIN_DOES_NOT_DEPEND_ON_OUTER_LAYERS =
        noClasses()
            .that().resideInAnyPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..application..",
                "..presentation..",
                "..infrastructure.."
            );

    @ArchTest
    static final ArchRule DOMAIN_HAS_NO_FRAMEWORK_DEPENDENCIES =
        noClasses()
            .that().resideInAnyPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..",
                "jakarta.persistence..",
                "com.fasterxml.jackson..",
                "org.apache.kafka.."
            );

    @ArchTest
    static final ArchRule APPLICATION_DOES_NOT_DEPEND_ON_OUTER_PACKAGES =
        noClasses()
            .that().resideInAnyPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..presentation..",
                "..infrastructure.."
            );

    @ArchTest
    static final ArchRule PRESENTATION_DOES_NOT_DEPEND_ON_INFRASTRUCTURE =
        noClasses()
            .that().resideInAnyPackage("..presentation..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..infrastructure.."
            );

    @ArchTest
    static final ArchRule PRESENTATION_DOES_NOT_EXPOSE_DOMAIN_MODELS =
        noClasses()
            .that().resideInAnyPackage("..presentation..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..domain.."
            );

    @ArchTest
    static final ArchRule INPUT_ADAPTERS_ONLY_USE_INPUT_BOUNDARIES =
        noClasses()
            .that().resideInAnyPackage(
                "..presentation.rest..",
                "..infrastructure.messaging.kafka.listener.."
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                "..application.port.out..",
                "..application.service..",
                "..infrastructure.persistence..",
                "..domain.."
            );

    @ArchTest
    static final ArchRule OUTPUT_ADAPTERS_DO_NOT_USE_INPUT_ADAPTERS =
        noClasses()
            .that().resideInAnyPackage(
                "..infrastructure.persistence..",
                "..infrastructure.redis..",
                "..infrastructure.messaging.kafka.adapter.."
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                "..presentation..",
                "..infrastructure.messaging.kafka.listener.."
            );

    @ArchTest
    static final ArchRule APPLICATION_DOES_NOT_DEPEND_ON_MESSAGING_FRAMEWORKS =
        noClasses()
            .that().resideInAnyPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.fasterxml.jackson..",
                "org.apache.kafka..",
                "org.springframework.kafka.."
            );

    @ArchTest
    static final ArchRule KAFKA_LISTENERS_DO_NOT_DEPEND_ON_PERSISTENCE =
        noClasses()
            .that().resideInAnyPackage(
                "..infrastructure.messaging.kafka.listener.."
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                "..infrastructure.persistence..",
                "org.springframework.data..",
                "jakarta.persistence.."
            );

    @ArchTest
    static final ArchRule PAYMENT_LISTENER_DOES_NOT_USE_OUTPUT_PORTS =
        noClasses()
            .that().haveSimpleName("PaymentKafkaListener")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..application.port.out..",
                "..infrastructure.persistence.."
            );

    @ArchTest
    static final ArchRule EVENT_LISTENER_DOES_NOT_USE_OUTPUT_PORTS =
        noClasses()
            .that().haveSimpleName("EventKafkaListener")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..application.port.out..",
                "..infrastructure.persistence.."
            );

    @ArchTest
    static final ArchRule KAFKA_LISTENERS_ONLY_CALL_INPUT_BOUNDARIES =
        noClasses()
            .that().resideInAnyPackage(
                "..infrastructure.messaging.kafka.listener.."
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                "..application.port.out..",
                "..application.service..",
                "..infrastructure.persistence..",
                "..domain.."
            );

    @ArchTest
    static final ArchRule PAYMENT_LISTENER_HAS_NO_TRANSACTION_DEPENDENCY =
        noClasses()
            .that().haveSimpleName("PaymentKafkaListener")
            .should().dependOnClassesThat().resideInAnyPackage(
                "jakarta.transaction..",
                "org.springframework.transaction.."
            );

    @ArchTest
    static final ArchRule KAFKA_LISTENERS_DO_NOT_MANAGE_TRANSACTIONS =
        noClasses()
            .that().resideInAnyPackage(
                "..infrastructure.messaging.kafka.listener.."
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                "jakarta.transaction..",
                "org.springframework.transaction.."
            );

    @ArchTest
    static final ArchRule CAMPAIGN_APPLICATION_SERVICE_IS_FRAMEWORK_FREE =
        noClasses()
            .that().haveSimpleName(
                "RegistrationCampaignServiceImpl"
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..",
                "jakarta.transaction..",
                "jakarta.persistence.."
            );

    @ArchTest
    static final ArchRule APPLICATION_IS_FRAMEWORK_FREE =
        noClasses()
            .that().resideInAnyPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..",
                "jakarta.persistence..",
                "jakarta.transaction..",
                "jakarta.servlet..",
                "com.fasterxml.jackson..",
                "org.apache.kafka.."
            );
}
