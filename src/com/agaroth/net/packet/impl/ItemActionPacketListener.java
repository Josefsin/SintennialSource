package com.agaroth.net.packet.impl;

import com.agaroth.model.GameMode;
import com.agaroth.model.GameObject;
import com.agaroth.model.Item;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.util.Misc;
import com.agaroth.world.content.Consumables;
import com.agaroth.world.content.Digging;
import com.agaroth.world.content.DropTable;
import com.agaroth.world.content.Effigies;
import com.agaroth.world.content.ExperienceLamps;
import com.agaroth.world.content.Gambling;
import com.agaroth.world.content.MemberScrolls;
import com.agaroth.world.content.MoneyPouch;
import com.agaroth.world.content.VoteReward;
import com.agaroth.world.content.combat.range.DwarfMultiCannon;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.content.skill.impl.dungeoneering.ItemBinding;
import com.agaroth.world.content.skill.impl.herblore.Herblore;
import com.agaroth.world.content.skill.impl.herblore.IngridientsBook;
import com.agaroth.world.content.skill.impl.hunter.BoxTrap;
import com.agaroth.world.content.skill.impl.hunter.Hunter;
import com.agaroth.world.content.skill.impl.hunter.JarData;
import com.agaroth.world.content.skill.impl.hunter.PuroPuro;
import com.agaroth.world.content.skill.impl.hunter.SnareTrap;
import com.agaroth.world.content.skill.impl.hunter.Trap.TrapState;
import com.agaroth.world.content.skill.impl.prayer.Prayer;
import com.agaroth.world.content.skill.impl.runecrafting.RunecraftingPouches;
import com.agaroth.world.content.skill.impl.runecrafting.RunecraftingPouches.RunecraftingPouch;
import com.agaroth.world.content.skill.impl.slayer.SlayerDialogues;
import com.agaroth.world.content.skill.impl.slayer.SlayerTasks;
import com.agaroth.world.content.skill.impl.summoning.CharmingImp;
import com.agaroth.world.content.skill.impl.summoning.SummoningData;
import com.agaroth.world.content.skill.impl.woodcutting.BirdNests;
import com.agaroth.world.content.transportation.JewelryTeleporting;
import com.agaroth.world.content.transportation.TeleportHandler;
import com.agaroth.world.content.transportation.TeleportType;
import com.agaroth.world.entity.impl.player.Player;


public class ItemActionPacketListener implements PacketListener {

	@SuppressWarnings("unused")
	private static void firstAction(final Player player, Packet packet) {
		int interfaceId = packet.readUnsignedShort();
		int slot = packet.readShort();
		int itemId = packet.readShort();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		player.setInteractingItem(player.getInventory().getItems()[slot]);
		if (Prayer.isBone(itemId)) {
			Prayer.buryBone(player, itemId);
			return;
		}
		if (Consumables.isFood(player, itemId, slot))
			return;
		if(Consumables.isPotion(itemId)) {
			Consumables.handlePotion(player, itemId, slot);
			return;
		}
		if(BirdNests.isNest(itemId)) {
			BirdNests.searchNest(player, itemId);
			return;
		}
		if (Herblore.cleanHerb(player, itemId))
			return;
		if(MemberScrolls.handleScroll(player, itemId))
			return;
		if(Effigies.isEffigy(itemId)) {
			Effigies.handleEffigy(player, itemId);
			return;
		}
		if(ExperienceLamps.handleLamp(player, itemId)) {
			return;
		}
		switch(itemId) {
		case 8007: //varrock teleport tab
			TeleportHandler.teleportPlayer(player, new Position(3214, 3424), TeleportType.TELE_TAB);
			player.getInventory().delete(8007, 1);
			break;
		case 8008: //lumbridge teleport tab
			TeleportHandler.teleportPlayer(player, new Position(3223, 3218), TeleportType.TELE_TAB);
			player.getInventory().delete(8008, 1);
			break;
		case 8009: //falador teleport tab
			TeleportHandler.teleportPlayer(player, new Position(2964, 3378), TeleportType.TELE_TAB);
			player.getInventory().delete(8009, 1);
			break;
		case 8010: //camelot teleport tab
			TeleportHandler.teleportPlayer(player, new Position(2757, 3477), TeleportType.TELE_TAB);
			player.getInventory().delete(8010, 1);
			break;
		case 8011: //ardougne teleport tab
			TeleportHandler.teleportPlayer(player, new Position(2662, 3305), TeleportType.TELE_TAB);
			player.getInventory().delete(8011, 1);
			break;
		case 18809: //rimmington teleport tab
			TeleportHandler.teleportPlayer(player, new Position(2957, 3216), TeleportType.TELE_TAB);
			player.getInventory().delete(18809, 1);
			break;
		case 18812: //relleka teleport tab
			TeleportHandler.teleportPlayer(player, new Position(2659, 3661), TeleportType.TELE_TAB);
			player.getInventory().delete(18812, 1);
			break;
		case 18814: //yanille teleport tab
			TeleportHandler.teleportPlayer(player, new Position(2559, 3089), TeleportType.TELE_TAB);
			player.getInventory().delete(18814, 1);
			break;
			
		case 2722: //yanille clue
			player.getPacketSender().sendInterface(9043);
			break;
		case 2723: //wizard's guild clue
			player.getPacketSender().sendInterface(9275);
			break;
		case 2725: //goblin village north of gamble clue
			player.getPacketSender().sendInterface(9454);
			break;
		case 2729: //clocktower ardounge clue
			player.getPacketSender().sendInterface(9720);
			break;
		case 2731: //north falador clue
			player.getPacketSender().sendInterface(17537);
			break;
		case 2733: //wilderness clue
			player.getPacketSender().sendInterface(17620);
			break;
		case 2724: //casket (hard)
			int clueRewards[][] = {
					{ 2587, 10690, 2585, 3472, 2589, 2595, 3473, 2597, 2605, 2599, 2601, 3474, 2603, 2613, 10698, 2609, 3475, 2611, 2627, 2623, 2625, 3477, 2629, 2619, 10798, 2617, 3476, 2621 }, 
			        { 10400, 10402, 10404, 10406, 10408, 10410, 10756, 10418, 2657, 2653, 2655, 3478, 2659 }, 
			        { 2665, 2661, 2663, 3479, 2667, 2673, 2669, 2671, 3480, 2675, 3486, 3481, 3483, 3485, 3488 }, 
			        { 10334, 10330, 10332, 10336, 10342, 10338, 10340, 19314, 19317, 19320, 19311, 19308, 10350, 10348, 10346, 10352, 10344 }
			};
			double clueNumGen = Math.random();
			int clueRewardGrade = clueNumGen >= 0.60 ? 0 : clueNumGen >= 0.30 ? 1 : clueNumGen >= 0.10 ? 2 : 3;
			int clueRewardPos = Misc.getRandom(clueRewards[clueRewardGrade].length-1);
			player.getInventory().delete(2724, 1);
			player.getInventory().add(clueRewards[clueRewardGrade][clueRewardPos], 1).refreshItems();
			break;
		case 20935:
			VoteReward.open(player, itemId);
			break;
		case 13663:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
				return;
			}
			player.setUsableObject(new Object[2]).setUsableObject(0, "reset");
			player.getPacketSender().sendString(38006, "Choose stat to reset!").sendMessage("@red@Please select a skill you wish to reset and then click on the 'Confim' button.").sendString(38090, "Which skill would you like to reset?");
			player.getPacketSender().sendInterface(38000);
			break;
		case 20104: 
		      if (player.busy()) {
		        player.getPacketSender().sendMessage("You can not do this right now.");
		        return;
		      }
		      if (player.getGameMode() != GameMode.NORMAL) {
		        player.getInventory().delete(20104, 1);
		        player.getInventory().add(1153, 1).add(1115, 1).add(1067, 1).add(1323, 1).add(1191, 1).add(841, 1).add(882, 50).add(1167, 1).add(1129, 1).add(1095, 1).add(1063, 1).add(579, 1).add(577, 1).add(1011, 1).add(1379, 1).add(556, 50).add(558, 50).add(557, 50).add(555, 50).add(1351, 1).add(1265, 1).add(1712, 1).add(11118, 1).add(1007, 1).add(1061, 1).add(330, 100);
		      }
		      else {
		        player.getInventory().delete(20104, 1);
		        player.getInventory().add(1153, 1).add(1115, 1).add(1067, 1).add(1323, 1).add(1191, 1).add(841, 1).add(882, 1000).add(1167, 1).add(1129, 1).add(1095, 1).add(1063, 1).add(579, 1).add(577, 1).add(1011, 1).add(1379, 1).add(556, 1000).add(558, 1000).add(557, 1000).add(555, 1000).add(1351, 1).add(1265, 1).add(1712, 1).add(11118, 1).add(1007, 1).add(1061, 1).add(386, 100);
		      }
		      break;
		case 7956:
			player.getInventory().delete(7956, 1);
			int[] rewards = { 200, 202, 204, 206, 208, 210, 212, 214, 216, 218, 220, 2486, 3052, 1624, 1622, 1620, 1618, 1632, 1516, 1514, 454, 448, 450, 452, 378, 372, 7945, 384, 390, 15271, 533, 535, 537, 18831, 556, 558, 555, 554, 557, 559, 564, 562, 566, 9075, 563, 561, 560, 565, 888, 890, 892, 11212, 9142, 9143, 9144, 9341, 9244, 866, 867, 868, 2, 10589, 10564, 6809, 4131, 15126, 4153, 1704, 1149 };
		    int[] rewardsAmount = { 200, 200, 200, 120, 50, 100, 70, 60, 90, 40, 30, 15, 10, 230, 140, 70, 20, 10, 400, 200, 400, 250, 100, 100, 1000, 800, 500, 200, 100, 50, 150, 100, 50, 5, 1500, 1500, 1500, 1500, 1500, 1500, 1000, 1000, 500, 500, 500, 500, 500, 500, 3000, 2500, 800, 300, 3500, 3500, 500, 150, 80, 3000, 1500, 400, 500, 1, 1, 1, 1, 1, 1, 1, 1 };
			int rewardPos = Misc.getRandom(rewards.length-1);
			player.getInventory().add(rewards[rewardPos], (int)((rewardsAmount[rewardPos]*0.5) + (Misc.getRandom(rewardsAmount[rewardPos]))));
			break;
		case 15387:
			player.getInventory().delete(15387, 1);
			rewards = new int[] {1377, 1149, 7158, 3000, 219, 5016, 6293, 6889, 2205, 3051, 269, 329, 3779, 6371, 2442, 347, 247};
			player.getInventory().add(rewards[Misc.getRandom(rewards.length-1)], 1);
			break;
		case 407:
			player.getInventory().delete(407, 1);
			if (Misc.getRandom(3) < 3) {
				player.getInventory().add(409, 1);
			} else if(Misc.getRandom(4) < 4) {
				player.getInventory().add(411, 1);
			} else 
				player.getInventory().add(413, 1);
			break;
		case 405:
			player.getInventory().delete(405, 1);
			if (Misc.getRandom(1) < 1) {
				int coins = Misc.getRandom(250000);
				player.getInventory().add(995, coins);
				player.getPacketSender().sendMessage("The casket contained "+coins+" coins!");
			} else
				player.getPacketSender().sendMessage("The casket was empty.");
			break;
		case 15084:
			if(player.getRights() == PlayerRights.DONATOR || player.getRights() == PlayerRights.SUPER_DONATOR || player.getRights() == PlayerRights.EXTREME_DONATOR || player.getRights() == PlayerRights.LEGENDARY_DONATOR  || player.getRights() == PlayerRights.UBER_DONATOR || player.getRights() == PlayerRights.SUPPORT || player.getRights() == PlayerRights.MODERATOR || player.getRights() == PlayerRights.GLOBAL_MODERATOR || player.getRights() == PlayerRights.ADMINISTRATOR || player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.OWNER) {
				Gambling.rollDice(player);
			} else {
				player.getPacketSender().sendMessage("You must be a donator to use this item.");
				return;
			}	
			break;
		case 299:
			if(player.getRights() == PlayerRights.DONATOR || player.getRights() == PlayerRights.SUPER_DONATOR || player.getRights() == PlayerRights.EXTREME_DONATOR || player.getRights() == PlayerRights.LEGENDARY_DONATOR  || player.getRights() == PlayerRights.UBER_DONATOR || player.getRights() == PlayerRights.SUPPORT || player.getRights() == PlayerRights.MODERATOR || player.getRights() == PlayerRights.GLOBAL_MODERATOR || player.getRights() == PlayerRights.ADMINISTRATOR || player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.OWNER) {
				Gambling.plantSeed(player);
			} else {
				player.getPacketSender().sendMessage("You must be a donator to use this item.");
				return;
			}		
			break;
		case 19670:
			RiggedSeeds.open(player, 0);
			break;
		case 4155:
			if(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendMessage("Your Enchanted gem will only work if you have a Slayer task.");
				return;
			}
			DialogueManager.start(player, SlayerDialogues.dialogue(player));
			break;
		case 11858:
		case 11860:
		case 11862:
		case 11848:
		case 11856:
		case 11850:
		case 11854:
		case 11852:
		case 11846:
			if(!player.getClickDelay().elapsed(2000) || !player.getInventory().contains(itemId))
				return;
			if(player.busy()) {
				player.getPacketSender().sendMessage("You cannot open this right now.");
				return;
			}

			int[] items = itemId == 11858 ? new int[] {10350, 10348, 10346, 10352} : 
				itemId == 11860 ? new int[]{10334, 10330, 10332, 10336} : 
					itemId == 11862 ? new int[]{10342, 10338, 10340, 10344} : 
						itemId == 11848 ? new int[]{4716, 4720, 4722, 4718} : 
							itemId == 11856 ? new int[]{4753, 4757, 4759, 4755} : 
								itemId == 11850 ? new int[]{4724, 4728, 4730, 4726} : 
									itemId == 11854 ? new int[]{4745, 4749, 4751, 4747} : 
										itemId == 11852 ? new int[]{4732, 4734, 4736, 4738} : 
											itemId == 11846 ? new int[]{4708, 4712, 4714, 4710} :
												new int[]{itemId};

											if(player.getInventory().getFreeSlots() < items.length) {
												player.getPacketSender().sendMessage("You do not have enough space in your inventory.");
												return;
											}
											player.getInventory().delete(itemId, 1);
											for(int i : items) {
												player.getInventory().add(i, 1);
											}
											player.getPacketSender().sendMessage("You open the set and find items inside.");
											player.getClickDelay().reset();
											break;
		case 952:
			Digging.dig(player);
			break;
		case 10006:
			Hunter.layTrap(player, new SnareTrap(new GameObject(19175, new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())), TrapState.SET, 200, player));
			break;
		case 10008:			
			Hunter.layTrap(player, new BoxTrap(new GameObject(19187, new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())), TrapState.SET, 200, player));
			break;
		case 5509:
		case 5510:
		case 5512:
			RunecraftingPouches.fill(player, RunecraftingPouch.forId(itemId));
			break;
		case 292:
			IngridientsBook.bookopen = true;
			IngridientsBook.readBook(player, 0, false);
			break;
		case 7681:
			DropTable.openTable(player, 0, false);
			break;
		case 6199:
			int rewards2[][] = {
					{ 15501, 15272, 2503, 10499, 6326, 861, 1163, 1201, 6111, 544, 542, 5574, 5575, 5576, 1215, 3105, 13734, 7400, 2572, 11118 }, 
			        { 15501, 11133, 15126, 10828, 3751, 3753, 10589, 10564, 6809, 4587, 1249, 3204, 1305, 1377, 1434, 6528, 7158, 4153, 6, 8, 10, 12, 4675, 6914, 6889 }, 
			        { 6739, 15259, 15332, 2579, 6920, 6922, 13879, 13883, 15241, 15243 }
			};
			double numGen = Math.random();
			int rewardGrade = numGen >= 0.5 ? 0 : numGen >= 0.20 ? 1 : 2;
			rewardPos = Misc.getRandom(rewards2[rewardGrade].length-1);
			player.getInventory().delete(6199, 1);
			player.getInventory().add(rewards2[rewardGrade][rewardPos], 1).refreshItems();
			break;
		case 15501:
			int superiorRewards[][] = {
					{ 11133, 15126, 10828, 3751, 3753, 10589, 10564, 6809, 4587, 1249, 3204, 1305, 1377, 1434, 6528, 7158, 4153, 6, 8, 10, 12, 4675, 6914, 6889 }, 
			        { 6739, 15259, 15332, 2579, 6920, 6922, 15241, 11882, 11884, 11906, 20084 }, 
			        { 6570, 15018, 15019, 15020, 15220, 11730, 18349, 18353, 13896, 18357, 13899, 10551, 4151, 2577 }, 
			        { 11235, 14484, 11696, 11698, 11700, 13262, 15486, 19336, 19337, 19338, 19339, 19340, 14009, 14010, 14008 }
			};
			double superiorNumGen = Math.random();
			int superiorRewardGrade = superiorNumGen >= 0.60 ? 0 : superiorNumGen >= 0.30 ? 1 : superiorNumGen >= 0.10 ? 2 : 3;
			int superiorRewardPos = Misc.getRandom(superiorRewards[superiorRewardGrade].length-1);
			player.getInventory().delete(15501, 1);
			player.getInventory().add(superiorRewards[superiorRewardGrade][superiorRewardPos], 1).refreshItems();
			break;
		case 11882:
			player.getInventory().delete(11882, 1);
			player.getInventory().add(2595, 1).refreshItems();
			player.getInventory().add(2591, 1).refreshItems();
			player.getInventory().add(3473, 1).refreshItems();
			player.getInventory().add(2597, 1).refreshItems();
			break;
		case 11884:
			player.getInventory().delete(11884, 1);
			player.getInventory().add(2595, 1).refreshItems();
			player.getInventory().add(2591, 1).refreshItems();
			player.getInventory().add(2593, 1).refreshItems();
			player.getInventory().add(2597, 1).refreshItems();
			break;
		case 11906:
			player.getInventory().delete(11906, 1);
			player.getInventory().add(7394, 1).refreshItems();
			player.getInventory().add(7390, 1).refreshItems();
			player.getInventory().add(7386, 1).refreshItems();
			break;
		case 15262:
			if(!player.getClickDelay().elapsed(1000))
				return;
			player.getInventory().delete(15262, 1);
			player.getInventory().add(18016, 10000).refreshItems();
			player.getClickDelay().reset();
			break;
		case 6:
			DwarfMultiCannon.setupCannon(player);
			//player.getPacketSender().sendMessage("Dwarf cannon has been temporary disabled due to bug.");
		}
	}

	@SuppressWarnings("unused")
	public static void secondAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShortA();
		int slot = packet.readLEShort();
		int itemId = packet.readShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		if (SummoningData.isPouch(player, itemId, 2))
			return;
		switch(itemId) {
		case 1450: //blood talisman
			TeleportHandler.teleportPlayer(player, new Position(2464, 4892, 1), TeleportType.RING_TELE);
			break;
		case 1456: //death talisman
			TeleportHandler.teleportPlayer(player, new Position(2207, 4834), TeleportType.RING_TELE);
			break;
		case 1452: //chaos talisman
			TeleportHandler.teleportPlayer(player, new Position(2273, 4840), TeleportType.RING_TELE);
			break;
		case 1454: //cosmic talisman
			TeleportHandler.teleportPlayer(player, new Position(2144, 4831), TeleportType.RING_TELE);
			break;
		case 1462: //nature talisman
			TeleportHandler.teleportPlayer(player, new Position(2398, 4839), TeleportType.RING_TELE);
			break;
		case 1458: //law talisman
			TeleportHandler.teleportPlayer(player, new Position(2462, 4830), TeleportType.RING_TELE);
			break;
		case 1448: //mind talisman
			TeleportHandler.teleportPlayer(player, new Position(2788, 4839), TeleportType.RING_TELE);
			break;
		case 1446: //body talisman
			TeleportHandler.teleportPlayer(player, new Position(2523, 4830), TeleportType.RING_TELE);
			break;
		case 1444: //water talisman
			TeleportHandler.teleportPlayer(player, new Position(2718, 4834), TeleportType.RING_TELE);
			break;
		case 1442: //fire talisman
			TeleportHandler.teleportPlayer(player, new Position(2583, 4840), TeleportType.RING_TELE);
			break;
		case 1440: //earth talisman
			TeleportHandler.teleportPlayer(player, new Position(2656, 4839), TeleportType.RING_TELE);
			break;
		case 1438: //air talisman
			TeleportHandler.teleportPlayer(player, new Position(2842, 4832), TeleportType.RING_TELE);
			break;
		case 6500:
			if(player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked()) {
				player.getPacketSender().sendMessage("You cannot configure this right now.");
				return;
			}
			player.getPacketSender().sendInterfaceRemoval();
			DialogueManager.start(player, 101);
			player.setDialogueActionId(60);
			break;
		case 1712:
		case 1710:
		case 1708:
		case 1706:
		case 11118:
		case 11120:
		case 11122:
		case 11124:
			JewelryTeleporting.rub(player, itemId);
			break;
		case 1704:
			player.getPacketSender().sendMessage("Your amulet has run out of charges.");
			break;
		case 15492: 
		      if (player.getInventory().getFreeSlots() < 4) {
		        player.getPacketSender().sendMessage("You need at least 4 free inventory slots to disassemble helm.");
		        return;
		      }
		      if (player.getInventory().contains(15492)) {
		        player.getInventory().delete(15492, 1);
		        player.getInventory().add(15490, 1);
		        player.getInventory().add(15488, 1);
		        player.getInventory().add(13263, 1);
		      }
		      break;
		case 11126:
			player.getPacketSender().sendMessage("Your bracelet has run out of charges.");
			break;
		case 13281:
		case 13282:
		case 13283:
		case 13284:
		case 13285:
		case 13286:
		case 13287:
		case 13288:
			player.getSlayer().handleSlayerRingTP(itemId);
			break;
		case 5509:
		case 5510:
		case 5512:
			RunecraftingPouches.check(player, RunecraftingPouch.forId(itemId));
			break;
		case 995:
			MoneyPouch.depositMoney(player, player.getInventory().getAmount(995));
			break;
		}
	}

	@SuppressWarnings("unused")
	public void thirdClickAction(Player player, Packet packet) {
		int itemId = packet.readShortA();
		int slot = packet.readLEShortA();
		int interfaceId = packet.readLEShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		if(JarData.forJar(itemId) != null) {
			PuroPuro.lootJar(player, new Item(itemId, 1), JarData.forJar(itemId));
			return;
		}
		if (SummoningData.isPouch(player, itemId, 3)) {
			return;
		}
		if(ItemBinding.isBindable(itemId)) {
			ItemBinding.bindItem(player, itemId);
			return;
		}
		switch(itemId) {
		case 6500:
			CharmingImp.sendConfig(player);
			break;
		case 4155:
			player.getPacketSender().sendInterfaceRemoval();
			DialogueManager.start(player, 103);
			player.setDialogueActionId(65);
			break;
		case 13281:
		case 13282:
		case 13283:
		case 13284:
		case 13285:
		case 13286:
		case 13287:
		case 13288:
			player.getPacketSender().sendInterfaceRemoval();
			player.getPacketSender().sendMessage(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK ? ("You do not have a Slayer task.") : ("Your current task is to kill another "+(player.getSlayer().getAmountToSlay())+" "+Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"s."));
			break;
		case 6570:
			if(player.getInventory().contains(6570) && player.getInventory().getAmount(6529) >= 50000) {
				player.getInventory().delete(6570, 1).delete(6529, 50000).add(19111, 1);
				player.getPacketSender().sendMessage("You have upgraded your Fire cape into a TokHaar-Kal cape!");
			} else {
				player.getPacketSender().sendMessage("You need at least 50.000 Tokkul to upgrade your Fire Cape into a TokHaar-Kal cape.");
			}
			break;
		case 15262:
			if(!player.getClickDelay().elapsed(1300))
				return;
			int amt = player.getInventory().getAmount(15262);
			if(amt > 0)
				player.getInventory().delete(15262, amt).add(18016, 10000 * amt);
			player.getClickDelay().reset();
			break;
		case 5509:
		case 5510:
		case 5512:
			RunecraftingPouches.empty(player, RunecraftingPouch.forId(itemId));
			break;
		case 11283:
			player.getPacketSender().sendMessage("Your Dragonfire shield has "+player.getDfsCharges()+"/20 dragon-fire charges.");
			break;
		}
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		switch (packet.getOpcode()) {
		case SECOND_ITEM_ACTION_OPCODE:
			secondAction(player, packet);
			break;
		case FIRST_ITEM_ACTION_OPCODE:
			firstAction(player, packet);
			break;
		case THIRD_ITEM_ACTION_OPCODE:
			thirdClickAction(player, packet);
			break;
		}
	}

	public static final int SECOND_ITEM_ACTION_OPCODE = 75;
	public static final int FIRST_ITEM_ACTION_OPCODE = 122;
	public static final int THIRD_ITEM_ACTION_OPCODE = 16;
}