package com.sukuna.client;

import java.util.List;

public final class ModuleSetting {
    public enum Type { TOGGLE, SLIDER, MODE }
    private final String name;
    private final Type type;
    private final double min, max, step;
    private final List<String> modes;
    private double number;
    private int mode;
    private boolean enabled;

    private ModuleSetting(String name, Type type, boolean enabled, double number, double min, double max, double step, List<String> modes) {
        this.name=name; this.type=type; this.enabled=enabled; this.number=number;
        this.min=min; this.max=max; this.step=step; this.modes=modes;
    }
    public static ModuleSetting toggle(String name, boolean value) { return new ModuleSetting(name,Type.TOGGLE,value,0,0,0,0,List.of()); }
    public static ModuleSetting slider(String name,double value,double min,double max,double step) { return new ModuleSetting(name,Type.SLIDER,false,value,min,max,step,List.of()); }
    public static ModuleSetting mode(String name,String... values) { return new ModuleSetting(name,Type.MODE,false,0,0,0,0,List.of(values)); }
    public String name(){return name;}
    public Type type(){return type;}
    public boolean enabled(){return enabled;}
    public double number(){return number;}
    public String displayValue(){
        return switch(type) {
            case TOGGLE -> enabled ? "ON" : "OFF";
            case SLIDER -> String.valueOf(number);
            case MODE -> modes.isEmpty() ? "-" : modes.get(mode);
        };
    }
    public void cycle(){
        switch(type) {
            case TOGGLE -> enabled=!enabled;
            case SLIDER -> { number += step; if(number > max) number=min; }
            case MODE -> { if(!modes.isEmpty()) mode=(mode+1)%modes.size(); }
        }
    }
}
