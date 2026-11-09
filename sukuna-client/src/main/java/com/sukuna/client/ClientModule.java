package com.sukuna.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ClientModule {
    public enum Category { COMBAT, MOVEMENT, RENDERING, PLAYER, WORLD, MISCELLANEOUS, HUD }
    private final String name;
    private final Category category;
    private final String description;
    private final List<ModuleSetting> settings = new ArrayList<>();
    private boolean enabled;
    private int keyCode = -1;

    public ClientModule(String name, Category category, String description) {
        this.name=name; this.category=category; this.description=description;
    }
    public String name(){return name;}
    public Category category(){return category;}
    public String description(){return description;}
    public boolean enabled(){return enabled;}
    public void setEnabled(boolean enabled){this.enabled=enabled;}
    public void toggle(){this.enabled=!this.enabled;}
    public int keyCode(){return keyCode;}
    public void setKeyCode(int keyCode){this.keyCode=keyCode;}
    public List<ModuleSetting> settings(){return Collections.unmodifiableList(settings);}
    public void addSetting(ModuleSetting setting){settings.add(setting);}
}