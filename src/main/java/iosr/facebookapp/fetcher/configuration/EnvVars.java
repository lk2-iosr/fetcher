package iosr.facebookapp.fetcher.configuration;

import java.util.Map;
import java.util.Optional;

class EnvVars {
    private final Map<String, String> variables;

    EnvVars() {
        this.variables = System.getenv();
    }

    String getRequired(final String name) {
        return getOptional(name).orElseThrow(() -> new IllegalArgumentException("Missing variable: " + name));
    }

    Optional<String> getOptional(final String name) {
        return Optional.ofNullable(this.variables.get(name)).filter(s -> !s.isEmpty());
    }
}
