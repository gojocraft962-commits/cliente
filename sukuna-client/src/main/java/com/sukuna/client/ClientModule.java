package com.sukuna.client;

public final class ClientModule {
    public enum Category { COMBAT, MOVEMENT, RENDERING, PLAYER, WORLD, MISCELLANEOUS, HUD }

    private final String name;
    private final Category category;
    private final String description;
    private boolean enabled;

    public ClientModule(String name, Category category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public String name() { return name; }
    public Category category() { return category; }
    public String description() { return description; }
    public boolean enabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void toggle() { this.enabled = !this.enabled; }
}
