package com.sukuna.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import java.util.Comparator;

public final class CombatModuleController {
 private static long nextClickAt;
 private CombatModuleController(){}
 public static void tick(Minecraft client){
  if(client.player==null||client.level==null||client.gameMode==null||client.screen!=null)return;
  ClientModule aura=ModuleRegistry.find("Aura");
  if(aura!=null&&aura.enabled()) tickAura(client,aura);
  ClientModule clicker=ModuleRegistry.find("AutoClicker");
  if(clicker!=null&&clicker.enabled()) tickClicker(client,clicker);
 }
 private static void tickAura(Minecraft mc,ClientModule module){
  boolean players=value(module,"Target Players",true), mobs=value(module,"Target Mobs",false);
  double range=number(module,"Range",4.0), cps=number(module,"CPS",8.0);
  LivingEntity target=mc.level.getEntitiesOfClass(LivingEntity.class,mc.player.getBoundingBox().inflate(range),
   e->e!=mc.player&&e.isAlive()&&e.distanceTo(mc.player)<=range&&((players&&e instanceof Player)||(mobs&&e instanceof Mob)))
   .stream().min(Comparator.comparingDouble(e->e.distanceToSqr(mc.player))).orElse(null);
  if(target!=null&&System.currentTimeMillis()>=nextClickAt){
   mc.gameMode.attack(mc.player,target);
   mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
   nextClickAt=System.currentTimeMillis()+(long)(1000.0/Math.max(1.0,cps));
  }
 }
 private static void tickClicker(Minecraft mc,ClientModule module){
  String button=mode(module,"Button","Left");
  boolean pressed=button.equals("Right")?mc.options.keyUse.isDown():mc.options.keyAttack.isDown();
  if(!pressed||System.currentTimeMillis()<nextClickAt)return;
  if(button.equals("Right")){
   if(mc.hitResult instanceof EntityHitResult hit) mc.gameMode.interact(mc.player,hit.getEntity(),net.minecraft.world.InteractionHand.MAIN_HAND);
  } else if(mc.hitResult instanceof EntityHitResult hit){
   Entity target=hit.getEntity();
   mc.gameMode.attack(mc.player,target);
   mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
  }
  nextClickAt=System.currentTimeMillis()+(long)(1000.0/Math.max(1.0,number(module,"CPS",10.0)));
 }
 private static boolean value(ClientModule m,String n,boolean d){return m.settings().stream().filter(s->s.name().equals(n)).findFirst().map(ModuleSetting::enabled).orElse(d);}
 private static double number(ClientModule m,String n,double d){return m.settings().stream().filter(s->s.name().equals(n)).findFirst().map(ModuleSetting::number).orElse(d);}
 private static String mode(ClientModule m,String n,String d){return m.settings().stream().filter(s->s.name().equals(n)).findFirst().map(ModuleSetting::modeValue).orElse(d);}
}