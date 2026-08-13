package com.serverpilot.integration;

public enum IntegrationStatus {
    ACTIVE("Active"),
    INSTALLED_INACTIVE("Installed, inactive"),
    NOT_INSTALLED("Not installed"),
    PLANNED("Planned"),
    FAILED("Failed"),
    DISABLED("Disabled in config");

    private final String label;

    IntegrationStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
