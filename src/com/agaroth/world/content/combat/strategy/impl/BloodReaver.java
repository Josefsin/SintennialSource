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

public class BloodReaver implements CombatStrategy {
	
	  private static final Graphic gfx1 = new Graphic(375, 3, GraphicHeight.MIDDLE);
	  private static final Graphic gfx2 = new Graphic(375, 3, GraphicHeight.MIDDLE);
	  
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
	    final NPC reaver = (NPC)entity;
	    if (victim.getConstitution() <= 0) {
	      return true;
	    }
	    if (reaver.isChargingAttack()) {
	      return true;
	    }
	    if ((Locations.goodDistance(reaver.getPosition().copy(), victim.getPosition().copy(), 1)) && (Misc.getRandom(6) <= 3)) {
	      reaver.performAnimation(new Animation(7004));
	      reaver.getCombatBuilder().setContainer(new CombatContainer(reaver, victim, 1, 2, CombatType.MELEE, true));
	    }
	    else if (Misc.getRandom(10) <= 6) {
	      reaver.performAnimation(new Animation(7004));
	      reaver.performGraphic(gfx1);
	      reaver.getCombatBuilder().setContainer(new CombatContainer(reaver, victim, 1, 2, CombatType.RANGED, true));
	      TaskManager.submit(new Task(1, reaver, false) {
	        protected void execute() {
	          new Projectile(reaver, victim, 330, 44, 3, 43, 31, 0).sendProjectile();
	          reaver.setChargingAttack(false).getCombatBuilder().setAttackTimer(reaver.getDefinition().getAttackSpeed() - 1);
	          stop();
	        }
	      });
	    }
	    else {
	      reaver.performAnimation(new Animation(7004));
	      victim.performGraphic(gfx2);
	      reaver.getCombatBuilder().setContainer(new CombatContainer(reaver, victim, 1, 2, CombatType.MAGIC, true));
	      TaskManager.submit(new Task(1, reaver, false) {
	        protected void execute() {
	          new Projectile(reaver, victim, 195, 44, 3, 43, 31, 0).sendProjectile();
	          reaver.setChargingAttack(false).getCombatBuilder().setAttackTimer(reaver.getDefinition().getAttackSpeed() - 1);
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
	    return 3;
	  }
	  
	  @Override
	  public CombatType getCombatType() {
	    return CombatType.MIXED;
	  }
}
