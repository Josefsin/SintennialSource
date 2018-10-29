package com.agaroth.world.entity.impl.npc;

import com.agaroth.model.Locations;
import com.agaroth.model.Locations.Location;
import com.agaroth.world.content.combat.CombatFactory;
import com.agaroth.world.content.combat.strategy.impl.Nex;
import com.agaroth.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.agaroth.world.entity.impl.player.Player;

public final class NpcAggression {

	public static final int NPC_TOLERANCE_SECONDS = 1200; //5 mins

	public static void target(Player player) {

		if(player.isPlayerLocked())
			return;
		
		final boolean dung = Dungeoneering.doingDungeoneering(player);
		for (NPC npc : player.getLocalNpcs()) {

			if(npc == null || npc.getConstitution() <= 0)
				continue;
			
			NPCFacing.updateFacing(player, npc);
			
			if(!(dung && npc.getId() != 11226) && !npc.getDefinition().isAggressive()) {
				continue;
			}
			
			if(npc.getSpawnedFor() != null && npc.getSpawnedFor() != player)
				continue;
			
			if(!npc.findNewTarget()) {
				if(npc.getCombatBuilder().isAttacking() || npc.getCombatBuilder().isBeingAttacked()) {
					continue;
				}
			}

			/** GWD **/
			boolean gwdMob = Nex.nexMob(npc.getId()) || npc.getId() == 6260 || npc.getId() == 6261 || npc.getId() == 6263 || npc.getId() == 6265 || npc.getId() == 6222 || npc.getId() == 6223 || npc.getId() == 6225 || npc.getId() == 6227 || npc.getId() == 6203 || npc.getId() == 6208 || npc.getId() == 6204 || npc.getId() == 6206 || npc.getId() == 6247 || npc.getId() == 6248 || npc.getId() == 6250 || npc.getId() == 6252;
			if(gwdMob) {
				if(!player.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
					continue;
				}
			}

			// Check if the entity is within distance.
			if (Locations.goodDistance(npc.getPosition(), player.getPosition(), npc.getAggressionDistance()) || gwdMob) {
		
				if (player.getTolerance().elapsed() > (NPC_TOLERANCE_SECONDS * 1000) && player.getLocation() != Location.GODWARS_DUNGEON && player.getLocation() != Location.DAGANNOTH_DUNGEON && !dung) {
					break;
				}

				boolean multi = Location.inMulti(player);

				if(player.isTargeted()) {
					if(!player.getCombatBuilder().isBeingAttacked()) {
						player.setTargeted(false);
					} else if(!multi) {
						break;
					}
				}


				if (player.getSkillManager().getCombatLevel() > (npc.getDefinition().getCombatLevel() * 2) && player.getLocation() != Location.WILDERNESS && !dung) {
					continue;
				}

				if(Location.ignoreFollowDistance(npc) || gwdMob || npc.getDefaultPosition().getDistance(player.getPosition()) < 7 + npc.getMovementCoordinator().getCoordinator().getRadius() || dung) {
					if(CombatFactory.checkHook(npc, player)) {
						player.setTargeted(true);
						npc.getCombatBuilder().attack(player);
						npc.setFindNewTarget(false);
						break;
					}
				}
			}
		}
	}

}
