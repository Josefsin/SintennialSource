package com.agaroth.world.content.combat.strategy.impl;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Projectile;
import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;

public class AberrantSpectre implements CombatStrategy {
	
  @Override
  public boolean canAttack(Character entity, Character victim) {
    return true;
  }
  
  @Override
  public CombatContainer attack(Character entity, Character victim) {
    return null;
  }
  
  @Override
  public boolean customContainerAttack(Character entity, final Character victim) {
    final NPC spectre = (NPC)entity;
    if ((spectre.isChargingAttack()) || (victim.getConstitution() <= 0) || (spectre.getConstitution() <= 0)) {
      return true;
    }
    spectre.performAnimation(new Animation(spectre.getDefinition().getAttackAnimation()));
    spectre.setChargingAttack(true);
    spectre.getCombatBuilder().setContainer(new CombatContainer(spectre, victim, 1, 3, CombatType.MAGIC, true));
    TaskManager.submit(new Task(1, spectre, false) {
      int tick = 0;
      public void execute() {
        if (this.tick == 1) {
          new Projectile(spectre, victim, 2718, 44, 3, 43, 43, 0).sendProjectile();
          spectre.setChargingAttack(false);
          stop();
        }
        this.tick += 1;
      }
    });
    return true;
  }
  
  @Override
  public int attackDelay(Character entity)
  {
    return entity.getAttackSpeed();
  }
  
  @Override
  public int attackDistance(Character entity)
  {
    return 6;
  }
  
  @Override
  public CombatType getCombatType()
  {
    return CombatType.MAGIC;
  }
}