package com.sukuna.client;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ClientConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("sukuna-client.properties");
    private static final Properties VALUES = new Properties();
    public static int accentIndex, panelOpacity=96, backdropOpacity=153, uiScale=100;
    private ClientConfig() {}

    public static void load() {
        if (Files.exists(FILE)) {
            try (InputStream in=Files.newInputStream(FILE)) { VALUES.load(in); }
            catch (IOException e) { System.err.println("[Sukuna] Could not read config: "+e.getMessage()); }
        }
        accentIndex=integer("menu.accent",0); panelOpacity=integer("menu.panelOpacity",96);
        backdropOpacity=integer("menu.backdropOpacity",153); uiScale=integer("menu.uiScale",100);
        for (int i=0;i<ModuleRegistry.all().size();i++) {
            ClientModule m=ModuleRegistry.all().get(i);
            m.setEnabled(bool("module."+i+".enabled",false));
            m.setKeyCode(integer("module."+i+".key",m.keyCode()));
            for (int j=0;j<m.settings().size();j++) {
                ModuleSetting s=m.settings().get(j); String key="module."+i+".setting."+j+".";
                switch(s.type()) {
                    case TOGGLE -> s.setEnabled(bool(key+"enabled",s.enabled()));
                    case SLIDER -> s.setNumber(decimal(key+"number",s.number()));
                    case MODE -> s.setModeValue(VALUES.getProperty(key+"mode",s.modeValue()));
                }
            }
        }
    }
    public static void save() {
        VALUES.setProperty("menu.accent",Integer.toString(accentIndex));
        VALUES.setProperty("menu.panelOpacity",Integer.toString(panelOpacity));
        VALUES.setProperty("menu.backdropOpacity",Integer.toString(backdropOpacity));
        VALUES.setProperty("menu.uiScale",Integer.toString(uiScale));
        for (int i=0;i<ModuleRegistry.all().size();i++) {
            ClientModule m=ModuleRegistry.all().get(i);
            VALUES.setProperty("module."+i+".enabled",Boolean.toString(m.enabled()));
            VALUES.setProperty("module."+i+".key",Integer.toString(m.keyCode()));
            for (int j=0;j<m.settings().size();j++) {
                ModuleSetting s=m.settings().get(j); String key="module."+i+".setting."+j+".";
                switch(s.type()) {
                    case TOGGLE -> VALUES.setProperty(key+"enabled",Boolean.toString(s.enabled()));
                    case SLIDER -> VALUES.setProperty(key+"number",Double.toString(s.number()));
                    case MODE -> VALUES.setProperty(key+"mode",s.modeValue());
                }
            }
        }
        try {
            Files.createDirectories(FILE.getParent());
            try(OutputStream out=Files.newOutputStream(FILE)){VALUES.store(out,"Sukuna Client settings");}
        } catch(IOException e) { System.err.println("[Sukuna] Could not save config: "+e.getMessage()); }
    }
    private static int integer(String key,int fallback){try{return Integer.parseInt(VALUES.getProperty(key,Integer.toString(fallback)));}catch(NumberFormatException e){return fallback;}}
    private static double decimal(String key,double fallback){try{return Double.parseDouble(VALUES.getProperty(key,Double.toString(fallback)));}catch(NumberFormatException e){return fallback;}}
    private static boolean bool(String key,boolean fallback){return Boolean.parseBoolean(VALUES.getProperty(key,Boolean.toString(fallback)));}
}
