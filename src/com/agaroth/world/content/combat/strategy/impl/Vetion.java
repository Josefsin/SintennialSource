package com.agaroth.world.content.combat.strategy.impl;

import java.util.List;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Locations;
import com.agaroth.model.Position;
import com.agaroth.util.Misc;
import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.HitQueue.CombatHit;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class Vetion implements CombatStrategy {

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
		NPC vetion = (NPC)entity;
		if(vetion.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		if(Locations.goodDistance(vetion.getPosition().copy(), victim.getPosition().copy(), 3) && Misc.getRandom(5) <= 3) {
			vetion.performAnimation(new Animation(5487));
			vetion.getCombatBuilder().setContainer(new CombatContainer(vetion, victim, 1, 1, CombatType.MELEE, true));
		} else {
			vetion.setChargingAttack(true);
			vetion.performAnimation(new Animation(vetion.getDefinition().getAttackAnimation()));
		
			final Position start = victim.getPosition().copy();
			final Position second = new Position(start.getX() + 2, start.getY() + Misc.getRandom(2));
			final Position last = new Position (start.getX() - 2, start.getY() - Misc.getRandom(2));
			
			final Player p = (Player)victim;
			final List<Player> list = Misc.getCombinedPlayerList(p);
			
			TaskManager.submit(new Task(1, vetion, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick == 0) {
						p.getPacketSender().sendGlobalGraphic(new Graphic(281), start);
						p.getPacketSender().sendGlobalGraphic(new Graphic(281), second);
						p.getPacketSender().sendGlobalGraphic(new Graphic(281), last);
					} else if(tick == 3) {
						for(Player t : list) {
							if(t == null)
								continue;
							if(t.getPosition().equals(start) || t.getPosition().equals(second) || t.getPosition().equals(last)) {
								new CombatHit(vetion.getCombatBuilder(), new CombatContainer(vetion, t, 3, CombatType.MAGIC, true)).handleAttack();
							}
						}
						vetion.setChargingAttack(false);
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
