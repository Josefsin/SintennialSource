package com.agaroth.world.content.combat.strategy.impl;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Locations;
import com.agaroth.model.Projectile;
import com.agaroth.model.Locations.Location;
import com.agaroth.util.Misc;
import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;

public class Vennatis implements CombatStrategy {
	@Override
	public boolean canAttack(Character entity, Character victim) {
		return victim.getLocation() == Location.WILDERNESS;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}
	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC revenant = (NPC)entity;
		if(revenant.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		final CombatType attkType = Misc.getRandom(5) <= 2 && Locations.goodDistance(revenant.getPosition(), revenant.getPosition(), 4) ? CombatType.MELEE : Misc.getRandom(10) <= 5 ? CombatType.MAGIC : CombatType.RANGED;
		switch(attkType) {
		case MELEE:
			revenant.performAnimation(new Animation(revenant.getDefinition().getAttackAnimation()));
			revenant.getCombatBuilder().setContainer(new CombatContainer(revenant, victim, 1, 1, CombatType.MELEE, true));
			break;
		case MAGIC:
		case RANGED:
			revenant.setChargingAttack(true);
			revenant.getCombatBuilder().setContainer(new CombatContainer(revenant, victim, 1, 2, attkType, true));
			TaskManager.submit(new Task(1, revenant, false) {
				int tick = 0;
				@Override
				public void execute() {
					switch(tick) {
					case 1:
						new Projectile(revenant, victim, (attkType == CombatType.RANGED ? 970 : 280), 44, 3, 43, 43, 0).sendProjectile();
						break;
					case 3:
						revenant.setChargingAttack(false);
						stop();
						break;
					}
					tick++;
				}
			});
			break;
		}
		return true;
	}


	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 8;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
