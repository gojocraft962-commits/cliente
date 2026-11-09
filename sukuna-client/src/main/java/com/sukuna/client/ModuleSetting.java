package com.sukuna.client;
import java.util.List;
public final class ModuleSetting {
 public enum Type { TOGGLE, SLIDER, MODE }
 private final String name; private final Type type; private final double min,max,step;
 private final List<String> modes; private double number; private int mode; private boolean enabled;
 private ModuleSetting(String n,Type t,boolean e,double v,double min,double max,double step,List<String> modes){name=n;type=t;enabled=e;number=v;this.min=min;this.max=max;this.step=step;this.modes=modes;}
 public static ModuleSetting toggle(String n,boolean v){return new ModuleSetting(n,Type.TOGGLE,v,0,0,0,0,List.of());}
 public static ModuleSetting slider(String n,double v,double min,double max,double step){return new ModuleSetting(n,Type.SLIDER,false,v,min,max,step,List.of());}
 public static ModuleSetting mode(String n,String... values){return new ModuleSetting(n,Type.MODE,false,0,0,0,0,List.of(values));}
 public String name(){return name;} public Type type(){return type;} public boolean enabled(){return enabled;} public double number(){return number;}
 public String modeValue(){return modes.isEmpty()?"":modes.get(mode);}
 public String displayValue(){return switch(type){case TOGGLE->enabled?"ON":"OFF";case SLIDER->String.format(java.util.Locale.ROOT,"%.2f",number);case MODE->modeValue();};}
 public void cycle(){switch(type){case TOGGLE->enabled=!enabled;case SLIDER->{number+=step;if(number>max+1e-8)number=min;}case MODE->{if(!modes.isEmpty())mode=(mode+1)%modes.size();}}}
}