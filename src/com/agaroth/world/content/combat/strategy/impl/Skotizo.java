package com.agaroth.world.content.combat.strategy.impl;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Locations;
import com.agaroth.model.Projectile;
import com.agaroth.util.Misc;
import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;

public class Skotizo implements CombatStrategy {

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return true;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC skotizo = (NPC)entity;
		if(skotizo.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		if(Misc.getRandom(15) <= 2){
			int hitAmount = 1;
			skotizo.performGraphic(new Graphic(605));
			skotizo.setConstitution(skotizo.getConstitution() + hitAmount);
		}
		if(Locations.goodDistance(skotizo.getPosition().copy(), victim.getPosition().copy(), 3) && Misc.getRandom(5) <= 3) {
			skotizo.performAnimation(new Animation(skotizo.getDefinition().getAttackAnimation()));
			skotizo.getCombatBuilder().setContainer(new CombatContainer(skotizo, victim, 1, 1, CombatType.MELEE, true));
			if(Misc.getRandom(10) <= 2) {
				new Projectile(skotizo, victim, 971, 44, 3, 43, 43, 0).sendProjectile();
			}
		} else {
			skotizo.setChargingAttack(true);
			skotizo.performAnimation(new Animation(64));
			skotizo.getCombatBuilder().setContainer(new CombatContainer(skotizo, victim, 1, 3, CombatType.MAGIC, true));
			TaskManager.submit(new Task(1, skotizo, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick == 0) {
						new Projectile(skotizo, victim, 971, 44, 3, 41, 43, 0).sendProjectile();
					} else if(tick == 1) {
						skotizo.setChargingAttack(false);
						stop();
					}
					tick++;
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
		return 4;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
