package com.agaroth.world.content.combat.strategy.impl;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Locations;
import com.agaroth.model.MessageType;
import com.agaroth.model.Position;
import com.agaroth.model.Projectile;
import com.agaroth.util.Misc;
import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class Callisto implements CombatStrategy {

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
		NPC callisto = (NPC)entity;
		if(callisto.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		if(Misc.getRandom(15) <= 2){
			int hitAmount = 1;
			callisto.performGraphic(new Graphic(1));
			callisto.setConstitution(callisto.getConstitution() + hitAmount);
			((Player)victim).getPacketSender().sendMessage(MessageType.NPC_ALERT, "Callisto absorbs his next attack, healing himself a bit.");
		}
		if(Locations.goodDistance(callisto.getPosition().copy(), victim.getPosition().copy(), 3) && Misc.getRandom(5) <= 3) {
			callisto.performAnimation(new Animation(callisto.getDefinition().getAttackAnimation()));
			callisto.getCombatBuilder().setContainer(new CombatContainer(callisto, victim, 1, 1, CombatType.MELEE, true));
			if(Misc.getRandom(10) <= 2) {
				victim.moveTo(new Position(3156 + Misc.getRandom(3), 3804 + Misc.getRandom(3)));
				victim.performAnimation(new Animation(534));
				((Player)victim).getPacketSender().sendMessage(MessageType.NPC_ALERT, "You have been knocked back!");
			}
		} else {
			callisto.setChargingAttack(true);
			callisto.performAnimation(new Animation(4928));
			callisto.getCombatBuilder().setContainer(new CombatContainer(callisto, victim, 1, 3, CombatType.MAGIC, true));
			TaskManager.submit(new Task(1, callisto, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick == 0) {
						new Projectile(callisto, victim, 395, 44, 3, 41, 31, 0).sendProjectile();
					} else if(tick == 1) {
						callisto.setChargingAttack(false);
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
		return 3;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
