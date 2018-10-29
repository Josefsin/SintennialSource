package com.agaroth.world.content.combat.strategy.impl;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.GraphicHeight;
import com.agaroth.model.Locations;
import com.agaroth.model.Projectile;
import com.agaroth.util.Misc;
import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;

public class Scorpia implements CombatStrategy {
	
  private static final Graphic gfx1 = new Graphic(330, 3, GraphicHeight.MIDDLE);
  private static final Graphic gfx2 = new Graphic(195, 3, GraphicHeight.MIDDLE);
  
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
    final NPC scorpia = (NPC)entity;
    if (victim.getConstitution() <= 0) {
      return true;
    }
    if (scorpia.isChargingAttack()) {
      return true;
    }
    if ((Locations.goodDistance(scorpia.getPosition().copy(), victim.getPosition().copy(), 1)) && (Misc.getRandom(6) <= 3)) {
      scorpia.performAnimation(new Animation(6254));
      scorpia.getCombatBuilder().setContainer(new CombatContainer(scorpia, victim, 1, 2, CombatType.MELEE, true));
    }
    else if (Misc.getRandom(10) <= 6) {
      scorpia.performAnimation(new Animation(6254));
      scorpia.performGraphic(gfx1);
      scorpia.getCombatBuilder().setContainer(new CombatContainer(scorpia, victim, 1, 2, CombatType.RANGED, true));
      TaskManager.submit(new Task(1, scorpia, false) {
        protected void execute() {
          new Projectile(scorpia, victim, 330, 44, 3, 43, 31, 0).sendProjectile();
          scorpia.setChargingAttack(false).getCombatBuilder().setAttackTimer(scorpia.getDefinition().getAttackSpeed() - 1);
          stop();
        }
      });
    }
    else {
      scorpia.performAnimation(new Animation(6254));
      victim.performGraphic(gfx2);
      scorpia.getCombatBuilder().setContainer(new CombatContainer(scorpia, victim, 1, 2, CombatType.MAGIC, true));
      TaskManager.submit(new Task(1, scorpia, false) {
        protected void execute() {
          new Projectile(scorpia, victim, 195, 44, 3, 43, 31, 0).sendProjectile();
          scorpia.setChargingAttack(false).getCombatBuilder().setAttackTimer(scorpia.getDefinition().getAttackSpeed() - 1);
          stop();
        }
      });
    }
    return true;
  }
  
  @Override
  public int attackDelay(Character entity) {
    return entity.getAttackSpeed();
  }
  
  @Override
  public int attackDistance(Character entity) {
    return 5;
  }
  
  @Override
  public CombatType getCombatType() {
    return CombatType.MIXED;
  }
}