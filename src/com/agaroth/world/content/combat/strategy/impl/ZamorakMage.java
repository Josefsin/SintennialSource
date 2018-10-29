package com.agaroth.world.content.combat.strategy.impl;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Projectile;
import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class ZamorakMage implements CombatStrategy {

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return victim.isPlayer() && ((Player)victim).getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom();
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC ZamorakMage = (NPC)entity;
		
		if(victim.getConstitution() <= 0) {
			return true;
		}
		if(ZamorakMage.isChargingAttack()) {
			return true;
		}
		
		ZamorakMage.performAnimation(new Animation(ZamorakMage.getDefinition().getAttackAnimation()));
		ZamorakMage.performGraphic(new Graphic(1202));
		ZamorakMage.setChargingAttack(true);

		ZamorakMage.getCombatBuilder().setContainer(new CombatContainer(ZamorakMage, victim, 1, 3, CombatType.MAGIC, true));
		
		TaskManager.submit(new Task(1, ZamorakMage, false) {
			int tick = 0;
			@Override
			public void execute() {
				if(tick == 1) {
					new Projectile(ZamorakMage, victim, 1203, 44, 3, 43, 43, 0).sendProjectile();
					ZamorakMage.setChargingAttack(false);
					stop();
				}
				tick++;
			}
		});
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 6;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MAGIC;
	}
}
