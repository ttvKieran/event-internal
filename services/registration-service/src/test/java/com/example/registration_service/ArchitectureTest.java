package com.example.registration_service;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(
    packages = "com.example.registration_service",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    @ArchTest
    static final ArchRule DEPENDENCIES_POINT_INWARD =
        layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Presentation").definedBy("..presentation..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .whereLayer("Domain")
                .mayOnlyBeAccessedByLayers(
                    "Application",
                    "Infrastructure"
                )
            .whereLayer("Application")
                .mayOnlyBeAccessedByLayers(
                    "Presentation",
                    "Infrastructure"
                )
            .whereLayer("Presentation")
                .mayNotBeAccessedByAnyLayer()
            .whereLayer("Infrastructure")
                .mayNotBeAccessedByAnyLayer()
            .because(
                "source-code dependencies must point toward " +
                "application and domain policies"
            );

    @ArchTest
    static final ArchRule INNER_LAYERS_ARE_FRAMEWORK_FREE =
        noClasses()
            .that().resideInAnyPackage(
                "..domain..",
                "..application.."
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..",
                "jakarta.persistence..",
                "jakarta.transaction..",
                "jakarta.servlet..",
                "com.fasterxml.jackson..",
                "org.apache.kafka.."
            )
            .because(
                "business policies must be independent of frameworks"
            );

    @ArchTest
    static final ArchRule INPUT_ADAPTERS_USE_INPUT_BOUNDARIES =
        noClasses()
            .that().resideInAnyPackage(
                "..presentation.rest..",
                "..infrastructure.messaging.kafka.listener.."
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                "..application.port.out..",
                "..application.service..",
                "..domain..",
                "..infrastructure.persistence..",
                "..infrastructure.redis..",
                "..infrastructure.messaging.kafka.adapter.."
            )
            .because(
                "input adapters must enter the application through " +
                "input ports instead of calling output details"
            );

    @ArchTest
    static final ArchRule OUTPUT_ADAPTERS_DO_NOT_USE_INPUT_SIDE =
        noClasses()
            .that().resideInAnyPackage(
                "..infrastructure.persistence..",
                "..infrastructure.redis..",
                "..infrastructure.messaging.kafka.adapter.."
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                "..application.port.in..",
                "..application.service..",
                "..presentation..",
                "..infrastructure.messaging.kafka.listener.."
            )
            .because(
                "output adapters must implement output ports without " +
                "calling input adapters or use-case implementations"
            );
}
