package com.agaroth.world.content.combat.strategy.impl;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Locations;
import com.agaroth.model.Position;
import com.agaroth.model.Projectile;
import com.agaroth.util.Misc;
import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class LizardMan implements CombatStrategy {

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
		NPC lizardman = (NPC)entity;
		if(lizardman.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		if(Misc.getRandom(15) <= 2){
			int hitAmount = 1;
			lizardman.performGraphic(new Graphic(69));
			lizardman.setConstitution(lizardman.getConstitution() + hitAmount);
		}
		if(Locations.goodDistance(lizardman.getPosition().copy(), victim.getPosition().copy(), 3) && Misc.getRandom(5) <= 3) {
			lizardman.performAnimation(new Animation(lizardman.getDefinition().getAttackAnimation()));
			lizardman.getCombatBuilder().setContainer(new CombatContainer(lizardman, victim, 1, 1, CombatType.MELEE, true));
			if(Misc.getRandom(5) <= 2) {
				victim.moveTo(new Position(3232 + Misc.getRandom(3), 3647 + Misc.getRandom(3)));
				lizardman.performAnimation(new Animation(7192));
				victim.performAnimation(new Animation(534));
			}
		} else {
			lizardman.setChargingAttack(true);
			lizardman.performAnimation(new Animation(7193));
			lizardman.getCombatBuilder().setContainer(new CombatContainer(lizardman, victim, 1, 3, CombatType.MAGIC, true));
			TaskManager.submit(new Task(1, lizardman, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick == 0) {
						new Projectile(lizardman, victim, 69, 44, 3, 41, 31, 0).sendProjectile();
					} else if(tick == 1) {
						lizardman.setChargingAttack(false);
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
		return 5;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}