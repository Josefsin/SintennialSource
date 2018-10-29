package com.agaroth.engine.task.impl;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.agaroth.GameSettings;
import com.agaroth.engine.task.Task;
import com.agaroth.model.Animation;
import com.agaroth.model.DamageDealer;
import com.agaroth.model.Flag;
import com.agaroth.model.GameMode;
import com.agaroth.model.GroundItem;
import com.agaroth.model.Item;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.model.Skill;
import com.agaroth.model.Locations.Location;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.content.ItemsKeptOnDeath;
import com.agaroth.world.content.combat.strategy.impl.Nex;
import com.agaroth.world.entity.impl.GroundItemManager;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

/**
 * Represents a player's death task, through which the process of dying is handled,
 * the animation, dropping items, etc.
 * 
 * @author relex lawl, redone by Gabbe.
 */

public class PlayerDeathTask extends Task {

	/**
	 * The PlayerDeathTask constructor.
	 * @param player	The player setting off the task.
	 */
	public PlayerDeathTask(Player player) {
		super(1, player, false);
		this.player = player;
	}

	private Player player;
	private int ticks = 5;
	private boolean dropItems = true;
	Position oldPosition;
	Location loc;
	ArrayList<Item> itemsToKeep = null; 
	NPC death;

	@Override
	public void execute() {
		if(player == null) {
			stop();
			return;
		}
		try {
			switch (ticks) {
			case 5:
				player.getPacketSender().sendInterfaceRemoval();
				player.getMovementQueue().setLockMovement(true).reset();
				break;
			case 3:
				player.performAnimation(new Animation(0x900));
				player.getPacketSender().sendMessage("Oh dear, you are dead!");
				break;
			case 1:
				this.oldPosition = player.getPosition().copy();
				this.loc = player.getLocation();
				if(loc != Location.DUNGEONEERING && loc != Location.PEST_CONTROL_GAME && loc != Location.DUEL_ARENA && loc != Location.FREE_FOR_ALL_ARENA && loc != Location.FREE_FOR_ALL_WAIT && loc != Location.SOULWARS && loc != Location.FIGHT_PITS && loc != Location.FIGHT_PITS_WAIT_ROOM && loc != Location.FIGHT_CAVES && loc != Location.RECIPE_FOR_DISASTER && loc != Location.GRAVEYARD) {

					DamageDealer damageDealer = player.getCombatBuilder().getTopDamageDealer(true, null);
					Player killer = damageDealer == null ? null : damageDealer.getPlayer();
					
					boolean npc = player.getCombatBuilder().getLastAttacker() != null && player.getCombatBuilder().getLastAttacker().isNpc();
					
					if(player.getRights().equals(PlayerRights.OWNER) || player.getRights().equals(PlayerRights.DEVELOPER))
						dropItems = false;
					if(loc == Location.DEFAULT) {
						dropItems = false;
					}
					if(loc == Location.WILDERNESS) {
						if(killer != null && (killer.getRights().equals(PlayerRights.OWNER) || killer.getRights().equals(PlayerRights.DEVELOPER) || killer.getGameMode().equals(GameMode.IRONMAN) || killer.getGameMode().equals(GameMode.HARDCORE_IRONMAN)))
							dropItems = false;
					}
					if(killer != null) {
						if( killer.getRights().equals(PlayerRights.OWNER) || killer.getRights().equals(PlayerRights.DEVELOPER) || killer.getGameMode().equals(GameMode.IRONMAN) || killer.getGameMode().equals(GameMode.HARDCORE_IRONMAN)) {
							dropItems = false;
						}
					}
					if (npc) {
						NPC killedByNPC = (NPC) player.getCombatBuilder().getLastAttacker();
						boolean boss = killedByNPC.getDefaultConstitution() > 2000 && !Nex.nexMinion(killedByNPC.getId()) && killedByNPC.getId() != 1158 && !(killedByNPC.getId() >= 3493 && killedByNPC.getId() <= 3497);
						if (boss) {
							dropItems = false;
						}
					}
					boolean spawnItems = loc != Location.NOMAD && !(loc == Location.GODWARS_DUNGEON && player.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom());
					if(dropItems) {
						itemsToKeep = ItemsKeptOnDeath.getItemsToKeep(player);
						final CopyOnWriteArrayList<Item> playerItems = new CopyOnWriteArrayList<Item>();
						playerItems.addAll(player.getInventory().getValidItems());
						playerItems.addAll(player.getEquipment().getValidItems());
						final Position position = player.getPosition();
						for (Item item : playerItems) {
							if(!item.tradeable() || itemsToKeep.contains(item)) {
								if(!itemsToKeep.contains(item)) {
									itemsToKeep.add(item);
								}
								continue;
							}
							if(spawnItems) {
								if(item != null && item.getId() > 0 && item.getAmount() > 0) {
									GroundItemManager.spawnGroundItem((killer != null && killer.getGameMode() == GameMode.NORMAL ? killer : player), new GroundItem(item, position, killer != null ? killer.getUsername() : player.getUsername(), player.getHostAddress(), false, 150, true, 150));
								}
							}
						}
						if(killer != null) {
							killer.getPlayerKillingAttributes().add(player);
							player.getPlayerKillingAttributes().setPlayerDeaths(player.getPlayerKillingAttributes().getPlayerDeaths() + 1);
							player.getPlayerKillingAttributes().setPlayerKillStreak(0);
							player.getPointsHandler().refreshPanel();
						}
						player.getInventory().resetItems().refreshItems();
						player.getEquipment().resetItems().refreshItems();
					}
				} else
					dropItems = false;
				player.getPacketSender().sendInterfaceRemoval();
				player.setEntityInteraction(null);
				player.getMovementQueue().setFollowCharacter(null);
				player.getCombatBuilder().cooldown(false);
				player.setTeleporting(false);
				player.setWalkToTask(null);
				player.getSkillManager().stopSkilling();
				break;
			case 0:
				if(dropItems) {
					if(player.getGameMode() == GameMode.HARDCORE_IRONMAN) {
						GameMode.set(player, player.getGameMode(), true);
					} else {
						if(itemsToKeep != null) {
							for(Item it : itemsToKeep) {
								player.getInventory().add(it.getId(), 1);
							}
							itemsToKeep.clear();
						}
					}
				}
				if(death != null) {
					World.deregister(death);
				}
				player.restart();
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				loc.onDeath(player);
				if(loc != Location.DUNGEONEERING) {
					if(player.getPosition().equals(oldPosition))
						player.moveTo(GameSettings.DEFAULT_POSITION.copy());
				}
				player = null;
				oldPosition = null;
				stop();
				break;
			}
			ticks--;
		} catch(Exception e) {
			setEventRunning(false);
			e.printStackTrace();
			if(player != null) {
				player.moveTo(GameSettings.DEFAULT_POSITION.copy());
				player.setConstitution(player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
			}	
		}
	}
}
