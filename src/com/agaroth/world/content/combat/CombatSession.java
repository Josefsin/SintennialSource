package com.agaroth.world.content.combat;

import com.agaroth.model.container.impl.Equipment;
import com.agaroth.world.content.Sounds;
import com.agaroth.world.content.combat.HitQueue.CombatHit;
import com.agaroth.world.content.combat.range.CombatRangedAmmo.RangedWeaponData;
import com.agaroth.world.content.combat.strategy.impl.DefaultRangedCombatStrategy;
import com.agaroth.world.content.combat.weapon.CombatSpecial;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class CombatSession {

	private CombatBuilder builder;
	
	public CombatSession(CombatBuilder builder) {
		this.builder = builder;
	}
	
	public void process() {

		if (builder.isCooldown()) {
			builder.cooldown--;
			builder.attackTimer--;

			if (builder.cooldown == 0) {
				builder.reset(true);
			}
			return;
		}

		if (!CombatFactory.checkHook(builder.getCharacter(), builder.getVictim())) {
			return;
		}
		
		if (builder.getCharacter().isPlayer()) {
			builder.determineStrategy();
		}
		builder.attackTimer--;
		if (builder.attackTimer < 1) {
			// Check if the attacker is close enough to attack.
			if (!CombatFactory.checkAttackDistance(builder)) {
				if(builder.getCharacter().isNpc() && builder.getVictim().isPlayer()) {
					if(builder.getLastAttack().elapsed(4500)) {
						((NPC)builder.getCharacter()).setFindNewTarget(true);
					}
				}
				return;
			}
			if (!builder.getStrategy().canAttack(builder.getCharacter(), builder.getVictim())) {
				builder.getCharacter().getCombatBuilder().reset(builder.getCharacter().isNpc() ? true : false);
				return;
			}
			builder.getStrategy().customContainerAttack(builder.getCharacter(), builder.getVictim());
			CombatContainer container = builder.getContainer();
			builder.getCharacter().setEntityInteraction(builder.getVictim());
			if (builder.getCharacter().isPlayer()) {
				Player player = (Player) builder.getCharacter();
				player.getPacketSender().sendInterfaceRemoval();

				if (player.isSpecialActivated() && player.getCastSpell() == null) {
					container = player.getCombatSpecial().container(player, builder.getVictim());
					boolean magicShortbowSpec = player.getCombatSpecial() != null && player.getCombatSpecial() == CombatSpecial.MAGIC_SHORTBOW;
					CombatSpecial.drain(player, player.getCombatSpecial().getDrainAmount());

					Sounds.sendSound(player, Sounds.specialSounds(player.getEquipment().get(Equipment.WEAPON_SLOT).getId()));

					if (player.getCombatSpecial().getCombatType() == CombatType.RANGED) {
						DefaultRangedCombatStrategy.decrementAmmo(player, builder.getVictim().getPosition());
						if(CombatFactory.darkBow(player) || player.getRangedWeaponData() == RangedWeaponData.MAGIC_SHORTBOW && magicShortbowSpec) {
							DefaultRangedCombatStrategy.decrementAmmo(player, builder.getVictim().getPosition());
						}
					}
				}
			}
			if (container != null && container.getCombatType() != null) {
				builder.getVictim().getCombatBuilder().setLastAttacker(builder.getCharacter());
				builder.getVictim().getLastCombat().reset();
				if (container.getCombatType() == CombatType.MAGIC && builder.getCharacter().isPlayer()) {
					Player player = (Player) builder.getCharacter();

					if (!player.isAutocast()) {
						if (!player.isSpecialActivated())
							player.getCombatBuilder().cooldown = 10;
						player.setCastSpell(null);
						player.getMovementQueue().setFollowCharacter(null);
						builder.determineStrategy();
					}
				}
				builder.getHitQueue().append(new CombatHit(builder, container, container.getHitDelay()));

				builder.setContainer(null);
			}

			builder.attackTimer = builder.getStrategy() != null ? builder.getStrategy().attackDelay(builder.getCharacter()) : builder.getCharacter().getAttackSpeed();
			builder.getLastAttack().reset();
			builder.getCharacter().setEntityInteraction(builder.getVictim());
		}
	}
}
