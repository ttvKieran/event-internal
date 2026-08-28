package com.example.registration_service.infrastructure.observability;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;

import java.util.HashMap;
import java.util.Map;

public final class CurrentTraceContext {

    private CurrentTraceContext() {
    }

    public static String traceParent() {
        Map<String, String> carrier = new HashMap<>();

        GlobalOpenTelemetry.getPropagators()
            .getTextMapPropagator()
            .inject(
                Context.current(),
                carrier,
                (map, key, value) -> map.put(key, value)
            );

        return carrier.get("traceparent");
    }
}
