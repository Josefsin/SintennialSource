package com.agaroth.world.content.dialogue;

import com.agaroth.engine.task.impl.BonusExperienceTask;
import com.agaroth.model.GameMode;
import com.agaroth.model.GameObject;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.model.Skill;
import com.agaroth.model.Locations.Location;
import com.agaroth.model.MagicSpellbook;
import com.agaroth.model.container.impl.Shop.ShopManager;
import com.agaroth.model.input.impl.BuyShards;
import com.agaroth.model.input.impl.ChangeUserTitle;
import com.agaroth.model.input.impl.DonateToWell;
import com.agaroth.model.input.impl.SellShards;
import com.agaroth.net.packet.impl.RiggedSeeds;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.content.Artifacts;
import com.agaroth.world.content.BankPin;
import com.agaroth.world.content.CustomObjects;
import com.agaroth.world.content.Effigies;
import com.agaroth.world.content.Gambling;
import com.agaroth.world.content.Kraken;
import com.agaroth.world.content.Lottery;
import com.agaroth.world.content.LoyaltyProgramme;
import com.agaroth.world.content.MemberScrolls;
import com.agaroth.world.content.PkSets;
import com.agaroth.world.content.PlayerPanel;
import com.agaroth.world.content.Scoreboards;
import com.agaroth.world.content.WellOfGoodwill;
import com.agaroth.world.content.Achievements.AchievementData;
import com.agaroth.world.content.clan.ClanChatManager;
import com.agaroth.world.content.combat.magic.Autocasting;
import com.agaroth.world.content.dialogue.impl.AgilityTicketExchange;
import com.agaroth.world.content.dialogue.impl.Sloane;
import com.agaroth.world.content.dialogue.impl.Tutorial;
import com.agaroth.world.content.minigames.impl.Graveyard;
import com.agaroth.world.content.minigames.impl.Nomad;
import com.agaroth.world.content.minigames.impl.RecipeForDisaster;
import com.agaroth.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.agaroth.world.content.skill.impl.dungeoneering.DungeoneeringFloor;
import com.agaroth.world.content.skill.impl.mining.Mining;
import com.agaroth.world.content.skill.impl.slayer.Slayer;
import com.agaroth.world.content.skill.impl.slayer.SlayerDialogues;
import com.agaroth.world.content.skill.impl.slayer.SlayerMaster;
import com.agaroth.world.content.skill.impl.summoning.CharmingImp;
import com.agaroth.world.content.skill.impl.summoning.SummoningTab;
import com.agaroth.world.content.transportation.JewelryTeleporting;
import com.agaroth.world.content.transportation.TeleportHandler;
import com.agaroth.world.content.transportation.TeleportType;
import com.agaroth.world.entity.impl.npc.NpcAggression;
import com.agaroth.world.entity.impl.player.Player;

public class DialogueOptions {
	
	public static void handle(Player player, int id) {
		if(player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.OWNER) {
			player.getPacketSender().sendMessage("Dialogue button id: "+id+", action id: "+player.getDialogueActionId()).sendConsoleMessage("Dialogue button id: "+id+", action id: "+player.getDialogueActionId());
		}
		if(Effigies.handleEffigyAction(player, id)) {
			return;
		}
		if (id == FIRST_OPTION_OF_FIVE) {
		      switch (player.getDialogueActionId()) {
		      case 157: 
			        DialogueManager.start(player, 165);
			        player.setDialogueActionId(158);
			        break;
	          case 158:
	        	  	Kraken.PayFee(player);
	        	  break;
		      case 94:
		    	  RiggedSeeds.TrollPlant(player, 0);
		    	  break;
		      case 95:
		    	  RiggedSeeds.TrollPlant(player, 4);
		    	  break;
		      case 152: 
		          TeleportHandler.teleportPlayer(player, new Position(2273, 4681, 0), player.getSpellbook().getTeleportType());
		    break;
		      case 156: 
		            TeleportHandler.teleportPlayer(player, new Position(3290, 3919), player.getSpellbook().getTeleportType());
		            break;
		      case 153: 
		         TeleportHandler.teleportPlayer(player, new Position(2520, 4647, 0), player.getSpellbook().getTeleportType());
		      break;
		      case 0: 
		        TeleportHandler.teleportPlayer(player, new Position(2695, 3714), player.getSpellbook().getTeleportType());
		        break;
		      case 151: 
		    	  player.setDialogueActionId(152);
			      DialogueManager.start(player, 152);
			    break;
		      case 1: 
		        TeleportHandler.teleportPlayer(player, new Position(3420, 3510), player.getSpellbook().getTeleportType());
		        break;
		      case 2: 
		        TeleportHandler.teleportPlayer(player, new Position(3235, 3295), player.getSpellbook().getTeleportType());
		        break;
		      case 9: 
		        DialogueManager.start(player, 16);
		        break;
		      case 10: 
		        Artifacts.sellArtifacts(player);
		        break;
		      case 11: 
		        Scoreboards.open(player, Scoreboards.TOP_KILLSTREAKS);
		        break;
		      case 12: 
		        TeleportHandler.teleportPlayer(player, new Position(3087, 3504), player.getSpellbook().getTeleportType());
		        break;
		      case 13: 
		        player.setDialogueActionId(78);
		        DialogueManager.start(player, 124);
		        break;
		      case 14: 
		        TeleportHandler.teleportPlayer(player, new Position(3097 + Misc.getRandom(1), 9869 + Misc.getRandom(1)), player.getSpellbook().getTeleportType());
		        break;
		      case 15:
					player.getPacketSender().sendInterfaceRemoval();
					int total = player.getSkillManager().getMaxLevel(Skill.ATTACK) + player.getSkillManager().getMaxLevel(Skill.STRENGTH);
					boolean has99 = player.getSkillManager().getMaxLevel(Skill.ATTACK) >= 99 || player.getSkillManager().getMaxLevel(Skill.STRENGTH) >= 99;
					if(total < 130 && !has99) {
					player.getPacketSender().sendMessage("");
					player.getPacketSender().sendMessage("You do not meet the requirements of a Warrior.");
					player.getPacketSender().sendMessage("You need to have a total Attack and Strength level of at least 140.");
					player.getPacketSender().sendMessage("Having level 99 in either is fine aswell.");
					return;
					}
					TeleportHandler.teleportPlayer(player, new Position(2855, 3543), player.getSpellbook().getTeleportType());
					break;
		      case 29: 
		        SlayerMaster.changeSlayerMaster(player, SlayerMaster.VANNAKA);
		        break;
		      case 36: 
		        TeleportHandler.teleportPlayer(player, new Position(2871, 5318, 2), player.getSpellbook().getTeleportType());
		        break;
		      case 38: 
		        TeleportHandler.teleportPlayer(player, new Position(2273, 4681), player.getSpellbook().getTeleportType());
		        break;
		      case 41: 
			    TeleportHandler.teleportPlayer(player, new Position(2903, 5204), player.getSpellbook().getTeleportType());
			    break;
		      case 40: 
		        TeleportHandler.teleportPlayer(player, new Position(3476, 9502), player.getSpellbook().getTeleportType());
		        break;
		      case 48: 
		        JewelryTeleporting.teleport(player, new Position(3088, 3506));
		        break;
		      case 59: 
		        if (!player.getClickDelay().elapsed(1500L)) {
		          break;
		        }
		        PkSets.buySet(player, PkSets.PURE_SET);
		        
		        break;
		      case 60: 
		        player.setDialogueActionId(61);
		        DialogueManager.start(player, 102);
		        break;
		      case 67: 
		        player.getPacketSender().sendInterfaceRemoval();
		        if ((player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) || 
		          (!player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().getUsername().equals(player.getUsername()))) {
		          break;
		        }
		        player.getMinigameAttributes().getDungeoneeringAttributes().getParty().setDungeoneeringFloor(DungeoneeringFloor.FIRST_FLOOR);
		        player.getMinigameAttributes().getDungeoneeringAttributes().getParty().sendMessage("The party leader has changed floor.");
		        player.getMinigameAttributes().getDungeoneeringAttributes().getParty().refreshInterface();
		        
		        break;
		      case 68: 
		        player.getPacketSender().sendInterfaceRemoval();
		        if ((player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) || 
		          (!player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().getUsername().equals(player.getUsername()))) {
		          break;
		        }
		        player.getMinigameAttributes().getDungeoneeringAttributes().getParty().setComplexity(1);
		        player.getMinigameAttributes().getDungeoneeringAttributes().getParty().sendMessage("The party leader has changed complexity.");
		        player.getMinigameAttributes().getDungeoneeringAttributes().getParty().refreshInterface();
		        
		        break;
		      case 127: 
		        DialogueManager.start(player, 128);
		        break;
		      case 130: 
		        TeleportHandler.teleportPlayer(player, new Position(3024, 9741), player.getSpellbook().getTeleportType());
		        break;
		      case 134: 
		        ShopManager.getShops().get(48).open(player);
		        break;
		      }
		    } else if (id == SECOND_OPTION_OF_FIVE) {
		        switch (player.getDialogueActionId()) {
		        case 94:
			    	  RiggedSeeds.TrollPlant(player, 1);
			    	  break;
		        case 95:
			    	  RiggedSeeds.TrollPlant(player, 5);
			    	  break;
		        case 152: 
				      TeleportHandler.teleportPlayer(player, new Position(1908, 4367, 0), player.getSpellbook().getTeleportType());
				    break;
		        case 156: 
		            TeleportHandler.teleportPlayer(player, new Position(3206, 3643), player.getSpellbook().getTeleportType());
		            break;
		        case 153: 
			         TeleportHandler.teleportPlayer(player, new Position(2891, 4767, 0), player.getSpellbook().getTeleportType());
			      break;
		        case 151: 
		        	player.setDialogueActionId(153);
					DialogueManager.start(player, 153);
				break;
		        case 0: 
		          TeleportHandler.teleportPlayer(player, new Position(3557 + Misc.getRandom(2), 9946 + Misc.getRandom(2)), player.getSpellbook().getTeleportType());
		          break;
		        case 1: 
		          TeleportHandler.teleportPlayer(player, new Position(2933, 9849), player.getSpellbook().getTeleportType());
		          break;
		        case 2: 
		          TeleportHandler.teleportPlayer(player, new Position(2802, 9148), player.getSpellbook().getTeleportType());
		          break;
		        case 9: 
		          Lottery.enterLottery(player);
		          break;
		        case 10:
		        	if (player.getGameMode() != GameMode.NORMAL) {
		        		player.getPacketSender().sendMessage("Ironmen and Ultimate Ironmen cannot buy PVP sets.");
		        		return;
		        	}
		          player.setDialogueActionId(59);
		          DialogueManager.start(player, 100);
		          break;
		        case 11: 
		          Scoreboards.open(player, Scoreboards.TOP_PKERS);
		          break;
		        case 12: 
		          TeleportHandler.teleportPlayer(player, new Position(2980 + Misc.getRandom(3), 3596 + Misc.getRandom(3)), player.getSpellbook().getTeleportType());
		          break;
		        case 13:
					player.getPacketSender().sendInterfaceRemoval();
					for(AchievementData d : AchievementData.values()) {
						if(!player.getAchievementAttributes().getCompletion()[d.ordinal()]) {
							player.getPacketSender().sendMessage("You must have completed all achievements in order to buy this cape.");
							return;
						}
					}
					boolean usePouch = player.getMoneyInPouch() >= 100000000;
					if(!usePouch && player.getInventory().getAmount(995) < 100000000) {
						player.getPacketSender().sendMessage("You do not have enough coins.");
						return;
					}
					if(usePouch) {
						player.setMoneyInPouch(player.getMoneyInPouch() - 100000000);
						player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch());
					} else
						player.getInventory().delete(995, 100000000);
					player.getInventory().add(14022, 1);
					player.getPacketSender().sendMessage("You've purchased an Completionist cape.");
					break;
		        case 14: 
		          TeleportHandler.teleportPlayer(player, new Position(3558 + Misc.getRandom(1), 9948 + Misc.getRandom(1)), player.getSpellbook().getTeleportType());
		          break;
		        case 15: 
		          TeleportHandler.teleportPlayer(player, new Position(2663 + Misc.getRandom(1), 2651 + Misc.getRandom(1)), player.getSpellbook().getTeleportType());
		          break;
		        case 17: 
		          if (player.getBankPinAttributes().hasBankPin())
		          {
		            DialogueManager.start(player, 12);
		            player.setDialogueActionId(8);
		          }
		          else
		          {
		            BankPin.init(player, false);
		          }
		          break;
		        case 29: 
		          SlayerMaster.changeSlayerMaster(player, SlayerMaster.DURADEL);
		          break;
		        case 36: 
		          TeleportHandler.teleportPlayer(player, new Position(1908, 4367), player.getSpellbook().getTeleportType());
		          break;
		        case 38: 
		          DialogueManager.start(player, 70);
		          player.setDialogueActionId(39);
		          break;
		        case 41: 
				    TeleportHandler.teleportPlayer(player, new Position(2885, 4373), player.getSpellbook().getTeleportType());
				    break;
		        case 40: 
		          TeleportHandler.teleportPlayer(player, new Position(2839, 9557), player.getSpellbook().getTeleportType());
		          break;
		        case 48: 
		          JewelryTeleporting.teleport(player, new Position(3213, 3423));
		          break;
		        case 59: 
		          if (!player.getClickDelay().elapsed(1500L)) {
		            break;
		          }
		          PkSets.buySet(player, PkSets.MELEE_MAIN_SET);
		          break;
		        case 60: 
		          player.setDialogueActionId(62);
		          DialogueManager.start(player, 102);
		          break;
		        case 68: 
		          player.getPacketSender().sendInterfaceRemoval();
		          if ((player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) || 
		            (!player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().getUsername().equals(player.getUsername()))) {
		            break;
		          }
		          player.getMinigameAttributes().getDungeoneeringAttributes().getParty().setComplexity(2);
		          player.getMinigameAttributes().getDungeoneeringAttributes().getParty().sendMessage("The party leader has changed complexity.");
		          player.getMinigameAttributes().getDungeoneeringAttributes().getParty().refreshInterface();
		          
		          break;
		        case 127: 
		          DialogueManager.start(player, 129);
		          break;
		        case 130: 
		          TeleportHandler.teleportPlayer(player, new Position(2742, 3443), player.getSpellbook().getTeleportType());
		          break;
		        case 134: 
		          ShopManager.getShops().get(49).open(player);
		          break;
		        }
		    } else if (id == THIRD_OPTION_OF_FIVE) {
		        switch (player.getDialogueActionId()) {
		        case 157:
		        	TeleportHandler.teleportPlayer(player, new Position(2464, 4773), player.getSpellbook().getTeleportType());
		        	break;
		        case 94:
			    	  RiggedSeeds.TrollPlant(player, 2);
			    	  break;
		        case 95:
			    	  RiggedSeeds.TrollPlant(player, 6);
			    	  break;
		        case 152: 
			          player.setDialogueActionId(37);
			          DialogueManager.start(player, 70);
				  break;
		        case 156: 
		            TeleportHandler.teleportPlayer(player, new Position(3650, 3485), player.getSpellbook().getTeleportType());
		            break;
		        case 153: 
			         TeleportHandler.teleportPlayer(player, new Position(2871, 5318, 2), player.getSpellbook().getTeleportType());
			      break;
		        case 151: 
			     player.setDialogueActionId(155);
				 DialogueManager.start(player, 155);
				  break;
		        case 0: 
		          TeleportHandler.teleportPlayer(player, new Position(3204 + Misc.getRandom(2), 3263 + Misc.getRandom(2)), player.getSpellbook().getTeleportType());
		          break;
		        case 1: 
		          TeleportHandler.teleportPlayer(player, new Position(2480, 5175), player.getSpellbook().getTeleportType());
		          break;
		        case 2: 
		          TeleportHandler.teleportPlayer(player, new Position(2793, 2773), player.getSpellbook().getTeleportType());
		          break;
		        case 9: 
		          DialogueManager.start(player, Lottery.Dialogues.getCurrentPot(player));
		          break;
		        case 10: 
		          DialogueManager.start(player, Sloane.getDialogue(player));
		          break;
		        case 11: 
		          Scoreboards.open(player, Scoreboards.TOP_TOTAL_EXP);
		          break;
		        case 12: 
		          TeleportHandler.teleportPlayer(player, new Position(3239 + Misc.getRandom(2), 3619 + Misc.getRandom(2)), player.getSpellbook().getTeleportType());
		          break;
		        case 13:
					player.getPacketSender().sendInterfaceRemoval();
					if(!player.getUnlockedLoyaltyTitles()[LoyaltyProgramme.LoyaltyTitles.VETERAN.ordinal()]) {
						player.getPacketSender().sendMessage("You must have unlocked the 'Veteran' Loyalty Title in order to buy this cape.");
						return;
					}
					boolean usePouch = player.getMoneyInPouch() >= 75000000;
					if(!usePouch && player.getInventory().getAmount(995) < 75000000) {
						player.getPacketSender().sendMessage("You do not have enough coins.");
						return;
					}
					if(usePouch) {
						player.setMoneyInPouch(player.getMoneyInPouch() - 75000000);
						player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch());
					} else
						player.getInventory().delete(995, 75000000);
					player.getInventory().add(14021, 1);
					player.getPacketSender().sendMessage("You've purchased a Veteran cape.");
					DialogueManager.start(player, 122);
					player.setDialogueActionId(76);
					break;
		        case 14: 
		          TeleportHandler.teleportPlayer(player, new Position(2713, 9564), player.getSpellbook().getTeleportType());
		          break;
		        case 15: 
		          TeleportHandler.teleportPlayer(player, new Position(3368 + Misc.getRandom(5), 3267 + Misc.getRandom(3)), player.getSpellbook().getTeleportType());
		          break;
		        case 17: 
		          player.getPacketSender().sendInterfaceRemoval();
		          if (player.getBankPinAttributes().hasBankPin())
		          {
		            player.getPacketSender().sendMessage("Please visit the nearest bank and enter your pin before doing this.");
		            return;
		          }
		          if (player.getSummoning().getFamiliar() != null)
		          {
		            player.getPacketSender().sendMessage("Please dismiss your familiar first.");
		            return;
		          }
		          if (player.getGameMode() == GameMode.NORMAL)
		          {
		            DialogueManager.start(player, 83);
		          }
		          else
		          {
		            player.setDialogueActionId(46);
		            DialogueManager.start(player, 84);
		          }
		          break;
		        case 29: 
		          SlayerMaster.changeSlayerMaster(player, SlayerMaster.KURADEL);
		          break;
		        case 36: 
		          player.setDialogueActionId(37);
		          DialogueManager.start(player, 70);
		          break;
		        case 38: 
		          TeleportHandler.teleportPlayer(player, new Position(2547, 9448), player.getSpellbook().getTeleportType());
		          break;
		        case 41: 
				  TeleportHandler.teleportPlayer(player, new Position(3217, 3921), player.getSpellbook().getTeleportType());
				  break;
		        case 40: 
		          TeleportHandler.teleportPlayer(player, new Position(2891, 4767), player.getSpellbook().getTeleportType());
		          break;
		        case 48: 
		          JewelryTeleporting.teleport(player, new Position(3368, 3267));
		          break;
		        case 59: 
		          if (!player.getClickDelay().elapsed(1500L)) {
		            break;
		          }
		          PkSets.buySet(player, PkSets.RANGE_MAIN_SET);
		          
		          break;
		        case 60: 
		          player.setDialogueActionId(63);
		          DialogueManager.start(player, 102);
		          break;
		        case 68: 
		          player.getPacketSender().sendInterfaceRemoval();
		          if ((player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) || 
		            (!player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().getUsername().equals(player.getUsername()))) {
		            break;
		          }
		          player.getMinigameAttributes().getDungeoneeringAttributes().getParty().setComplexity(3);
		          player.getMinigameAttributes().getDungeoneeringAttributes().getParty().sendMessage("The party leader has changed complexity.");
		          player.getMinigameAttributes().getDungeoneeringAttributes().getParty().refreshInterface();
		          
		          break;
		        case 127: 
		          player.setDialogueActionId(130);
		          DialogueManager.start(player, 130);
		          break;
		        case 130: 
		          TeleportHandler.teleportPlayer(player, new Position(2717, 3499), player.getSpellbook().getTeleportType());
		          break;
		        case 134: 
		          ShopManager.getShops().get(50).open(player);
		        }
		      } else if (id == FOURTH_OPTION_OF_FIVE) {
		          switch (player.getDialogueActionId()) {
		          case 94:
			    	  RiggedSeeds.TrollPlant(player, 3);
			    	  break;
		          case 95:
			    	  RiggedSeeds.open(player, 0);
			    	  break;
		          case 152: 
			          TeleportHandler.teleportPlayer(player, new Position(2547, 9445, 0), player.getSpellbook().getTeleportType());
			    break;
		          case 153: 
				         TeleportHandler.teleportPlayer(player, new Position(1891, 3177, 0), player.getSpellbook().getTeleportType());
				      break;
		          case 151: 
			    	  player.setDialogueActionId(156);
				      DialogueManager.start(player, 156);
				    break;
		          case 0: 
		            TeleportHandler.teleportPlayer(player, new Position(3173 - Misc.getRandom(2), 2981 + Misc.getRandom(2)), player.getSpellbook().getTeleportType());
		            break;
		          case 1: 
		            TeleportHandler.teleportPlayer(player, new Position(3279, 2964), player.getSpellbook().getTeleportType());
		            break;
		          case 2: 
		            TeleportHandler.teleportPlayer(player, new Position(3085, 9672), player.getSpellbook().getTeleportType());
		            break;
		          case 9: 
		            DialogueManager.start(player, Lottery.Dialogues.getLastWinner(player));
		            break;
		          case 10: 
		            ShopManager.getShops().get(26).open(player);
		            break;
		          case 11: 
		            Scoreboards.open(player, Scoreboards.TOP_ACHIEVER);
		            break;
		          case 12: 
		            TeleportHandler.teleportPlayer(player, new Position(3329 + Misc.getRandom(2), 3660 + Misc.getRandom(2), 0), player.getSpellbook().getTeleportType());
		            break;
		          case 13:
						player.getPacketSender().sendInterfaceRemoval();
						if(!player.getUnlockedLoyaltyTitles()[LoyaltyProgramme.LoyaltyTitles.MAXED.ordinal()]) {
							player.getPacketSender().sendMessage("You must have unlocked the 'Maxed' Loyalty Title in order to buy this cape.");
							return;
						}
						boolean usePouch = player.getMoneyInPouch() >= 50000000;
						if(!usePouch && player.getInventory().getAmount(995) < 50000000) {
							player.getPacketSender().sendMessage("You do not have enough coins.");
							return;
						}
						if(usePouch) {
							player.setMoneyInPouch(player.getMoneyInPouch() - 50000000);
							player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch());
						} else
							player.getInventory().delete(995, 50000000);
						player.getInventory().add(14019, 1);
						player.getPacketSender().sendMessage("You've purchased a Max cape.");
						break;
		          case 14: 
		            TeleportHandler.teleportPlayer(player, new Position(2884, 9797), player.getSpellbook().getTeleportType());
		            break;
		          case 15: 
		            TeleportHandler.teleportPlayer(player, new Position(3565, 3313), player.getSpellbook().getTeleportType());
		            break;
		          case 29: 
		            SlayerMaster.changeSlayerMaster(player, SlayerMaster.SUMONA);
		            break;
		          case 36: 
		            TeleportHandler.teleportPlayer(player, new Position(2520, 4647), player.getSpellbook().getTeleportType());
		            break;
		          case 38: 
		            TeleportHandler.teleportPlayer(player, new Position(1891, 3177), player.getSpellbook().getTeleportType());
		            break;
		          case 41: 
					 TeleportHandler.teleportPlayer(player, new Position(3206, 3643), player.getSpellbook().getTeleportType());
					 break;
		          case 40: 
		            TeleportHandler.teleportPlayer(player, new Position(3050, 9573), player.getSpellbook().getTeleportType());
		            break;
		          case 48: 
		            JewelryTeleporting.teleport(player, new Position(2447, 5169));
		            break;
		          case 59: 
		            if (!player.getClickDelay().elapsed(1500L)) {
		              break;
		            }
		            PkSets.buySet(player, PkSets.MAGIC_MAIN_SET);
		            
		            break;
		          case 60: 
		            player.setDialogueActionId(64);
		            DialogueManager.start(player, 102);
		            break;
		          case 68: 
		            player.getPacketSender().sendInterfaceRemoval();
		            if ((player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) || 
		              (!player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().getUsername().equals(player.getUsername()))) {
		              break;
		            }
		            player.getMinigameAttributes().getDungeoneeringAttributes().getParty().setComplexity(4);
		            player.getMinigameAttributes().getDungeoneeringAttributes().getParty().sendMessage("The party leader has changed complexity.");
		            player.getMinigameAttributes().getDungeoneeringAttributes().getParty().refreshInterface();
		            
		            break;
		          case 127: 
		            player.setDialogueActionId(131);
		            DialogueManager.start(player, 131);
		            break;
		          case 130: 
		            player.getPacketSender().sendMessage("@red@Construction coming soon!");
		            player.getPacketSender().sendInterfaceRemoval();
		            break;
		          case 134: 
		            ShopManager.getShops().get(51).open(player);
		          }
		       } else if (id == FIFTH_OPTION_OF_FIVE) {
		    	    switch (player.getDialogueActionId()) {
		    	    case 154:
		    	    	TeleportHandler.teleportPlayer(player, new Position(3056, 9553, 0), player.getSpellbook().getTeleportType());
		    	    	break;
		    	    case 94:
				    	  RiggedSeeds.open(player, 1);
				    	  break;
		    	    case 95:
				    	  RiggedSeeds.open(player, 2);
				    	  break;
		    	    case 152: 
				          TeleportHandler.teleportPlayer(player, new Position(2839, 9557, 0), player.getSpellbook().getTeleportType());
				    break;
		    	    case 153: 
		    	    	player.setDialogueActionId(154);
					      DialogueManager.start(player, 154);
				      break;
		    	    case 151: 
				    	  player.setDialogueActionId(157);
					      DialogueManager.start(player, 157);
					    break;
		    	      case 0: 
		    	        player.setDialogueActionId(1);
		    	        DialogueManager.next(player);
		    	        break;
		    	      case 1: 
		    	        player.setDialogueActionId(2);
		    	        DialogueManager.next(player);
		    	        break;
		    	      case 2: 
		    	        player.setDialogueActionId(0);
		    	        DialogueManager.start(player, 0);
		    	        break;
		    	      case 9: 
		    	      case 10: 
		    	      case 11: 
		    	      case 13: 
		    	      case 17: 
		    	      case 29: 
		    	      case 48: 
		    	      case 60: 
		    	      case 67: 
		    	      case 68: 
		    	        player.getPacketSender().sendInterfaceRemoval();
		    	        break;
		    	      case 12: 
		    	        TeleportHandler.teleportPlayer(player, new Position(2539, 4716, 0), player.getSpellbook().getTeleportType());
		    	        break;
		    	      case 14: 
		    	        DialogueManager.start(player, 23);
		    	        player.setDialogueActionId(14);
		    	        break;
		    	      case 15: 
		    	        DialogueManager.start(player, 32);
		    	        player.setDialogueActionId(18);
		    	        break;
		    	      case 36: 
		    	        DialogueManager.start(player, 66);
		    	        player.setDialogueActionId(38);
		    	        break;
		    	      case 38: 
		    	        DialogueManager.start(player, 68);
		    	        player.setDialogueActionId(40);
		    	        break;
		    	      case 41: 
		  			    TeleportHandler.teleportPlayer(player, new Position(2717, 9805), player.getSpellbook().getTeleportType());
		  			    break;
		    	      case 40: 
		    	        DialogueManager.start(player, 69);
		    	        player.setDialogueActionId(41);
		    	        break;
		    	      case 59: 
		    	        if (!player.getClickDelay().elapsed(1500L)) {
		    	          break;
		    	        }
		    	        PkSets.buySet(player, PkSets.HYBRIDING_MAIN_SET);
		    	        
		    	        break;
		    	      case 127: 
		    	        DialogueManager.start(player, 132);
		    	        break;
		    	      case 130: 
		    	        TeleportHandler.teleportPlayer(player, new Position(2709, 3437), player.getSpellbook().getTeleportType());
		    	        break;
		    	      case 134: 
		    	        ShopManager.getShops().get(52).open(player);
		    	      }
		    } else if (id == FIRST_OPTION_OF_FOUR) {
		        switch (player.getDialogueActionId()) {
		        case 154: 
		            TeleportHandler.teleportPlayer(player, new Position(2885, 4373), player.getSpellbook().getTeleportType());
		            break;
		        case 131: 
		          player.setDialogueActionId(26);
		          DialogueManager.start(player, 55);
		          break;
		        case 8: 
		          ShopManager.getShops().get(27).open(player);
		          break;
		        case 14: 
		          TeleportHandler.teleportPlayer(player, new Position(2871, 5318, 2), player.getSpellbook().getTeleportType());
		          break;
		        case 18: 
		          TeleportHandler.teleportPlayer(player, new Position(2439 + Misc.getRandom(2), 5171 + Misc.getRandom(2)), player.getSpellbook().getTeleportType());
		          break;
		        case 26: 
		          TeleportHandler.teleportPlayer(player, new Position(2480, 3435), player.getSpellbook().getTeleportType());
		          break;
		        case 27: 
		          ClanChatManager.createClan(player);
		          break;
		        case 28: 
		          player.setDialogueActionId(29);
		          DialogueManager.start(player, 62);
		          break;
		        case 30: 
		          player.getSlayer().assignTask();
		          break;
		        case 31: 
		          DialogueManager.start(player, SlayerDialogues.findAssignment(player));
		          break;
		        case 41: 
		          DialogueManager.start(player, 76);
		          break;
		        case 45: 
		          GameMode.set(player, GameMode.NORMAL, false);
		          break;
		        case 127: 
		          TeleportHandler.teleportPlayer(player, new Position(2816, 3440), player.getSpellbook().getTeleportType());
		        }
		      } else if (id == SECOND_OPTION_OF_FOUR) {
		        switch (player.getDialogueActionId()) {
		        case 154: 
		            TeleportHandler.teleportPlayer(player, new Position(2717, 9805), player.getSpellbook().getTeleportType());
		            break;
		        case 5: 
		          DialogueManager.start(player, MemberScrolls.getTotalFunds(player));
		          break;
		        case 131: 
		          player.setDialogueActionId(25);
		          DialogueManager.start(player, 54);
		          break;
		        case 8: 
		        	ShopManager.getShops().get(54).open(player);
		          break;
		        case 14: 
		          player.getPacketSender().sendInterfaceRemoval();
		          if (player.getSkillManager().getCurrentLevel(Skill.SLAYER) < 50)
		          {
		            player.getPacketSender().sendMessage("You need a Slayer level of at least 50 to visit this dungeon.");
		            return;
		          }
		          TeleportHandler.teleportPlayer(player, new Position(2731, 5095), player.getSpellbook().getTeleportType());
		          break;
		        case 18: 
		          TeleportHandler.teleportPlayer(player, new Position(2399, 5177), player.getSpellbook().getTeleportType());
		          break;
		        case 26: 
		          player.getPacketSender().sendInterfaceRemoval();
		          if (player.getSkillManager().getMaxLevel(Skill.AGILITY) < 35)
		          {
		            player.getPacketSender().sendMessage("You need an Agility level of at least level 35 to use this course.");
		            return;
		          }
		          TeleportHandler.teleportPlayer(player, new Position(2552, 3556), player.getSpellbook().getTeleportType());
		          break;
		        case 27: 
		          ClanChatManager.clanChatSetupInterface(player, true);
		          break;
		        case 28: 
		          if (player.getSlayer().getSlayerMaster().getPosition() == null) {
		            break;
		          }
		          TeleportHandler.teleportPlayer(player, new Position(player.getSlayer().getSlayerMaster().getPosition().getX(), player.getSlayer().getSlayerMaster().getPosition().getY(), player.getSlayer().getSlayerMaster().getPosition().getZ()), player.getSpellbook().getTeleportType());
		          if (player.getSkillManager().getCurrentLevel(Skill.SLAYER) > 50) {
		            break;
		          }
		          player.getPacketSender().sendMessage("").sendMessage("You can train Slayer with a friend by using a Slayer gem on them.").sendMessage("Slayer gems can be bought from all Slayer masters.");
		          
		          break;
		        case 31: 
		          DialogueManager.start(player, SlayerDialogues.resetTaskDialogue(player));
		          break;
		        case 41: 
		          WellOfGoodwill.lookDownWell(player);
		          break;
		        case 45: 
		          GameMode.set(player, GameMode.IRONMAN, false);
		          break;
		        case 127: 
		          TeleportHandler.teleportPlayer(player, new Position(3052, 3304), player.getSpellbook().getTeleportType());
		        }
		      } else if (id == THIRD_OPTION_OF_FOUR) {
		        switch (player.getDialogueActionId()) {
		        case 154: 
		            TeleportHandler.teleportPlayer(player, new Position(3050, 9573), player.getSpellbook().getTeleportType());
		            break;
		        case 131: 
		          TeleportHandler.teleportPlayer(player, new Position(3094, 3502), player.getSpellbook().getTeleportType());
		          break;
		        case 5: 
		          player.getPacketSender().sendInterfaceRemoval();
		          if (player.getAmountDonated() <= 6) {
		            player.getPacketSender().sendMessage("You need to be a member to teleport to this zone.").sendMessage("To become a donator, visit agaroth.com and donate for rank.");
		            return;
		          }
		          TeleportHandler.teleportPlayer(player, new Position(3424, 2919), player.getSpellbook().getTeleportType());
		          break;
		        case 8: 
		          LoyaltyProgramme.reset(player);
		          player.getPacketSender().sendInterfaceRemoval();
		          break;
		        case 9:
		        	player.getPacketSender().sendInterfaceRemoval();
		        	break;
		        case 14: 
		          TeleportHandler.teleportPlayer(player, new Position(1745, 5325), player.getSpellbook().getTeleportType());
		          break;
		        case 18: 
		          TeleportHandler.teleportPlayer(player, new Position(3503, 3562), player.getSpellbook().getTeleportType());
		          break;
		        case 26: 
		          player.getPacketSender().sendInterfaceRemoval();
		          if (player.getSkillManager().getMaxLevel(Skill.AGILITY) < 55)
		          {
		            player.getPacketSender().sendMessage("You need an Agility level of at least level 55 to use this course.");
		            return;
		          }
		          TeleportHandler.teleportPlayer(player, new Position(2998, 3914), player.getSpellbook().getTeleportType());
		          break;
		        case 27: 
		          ClanChatManager.deleteClan(player);
		          break;
		        case 28: 
		          TeleportHandler.teleportPlayer(player, new Position(3427, 3537, 0), player.getSpellbook().getTeleportType());
		          break;
		        case 31: 
		          DialogueManager.start(player, SlayerDialogues.totalPointsReceived(player));
		          break;
		        case 41: 
		          player.setInputHandling(new DonateToWell());
		          player.getPacketSender().sendInterfaceRemoval().sendEnterAmountPrompt("How much money would you like to contribute with?");
		          break;
		        case 45: 
		          GameMode.set(player, GameMode.HARDCORE_IRONMAN, false);
		          break;
		        case 127: 
		          TeleportHandler.teleportPlayer(player, new Position(2914, 3450), player.getSpellbook().getTeleportType());
		        }
		      } else if (id == FOURTH_OPTION_OF_FOUR) {
		        switch (player.getDialogueActionId()) {
		        case 154: 
		            TeleportHandler.teleportPlayer(player, new Position(2901, 5204), player.getSpellbook().getTeleportType());
		            break;
		        case 131: 
		          player.setDialogueActionId(28);
		          DialogueManager.start(player, 61);
		          break;
		        case 5: 
		        case 8: 
		        case 9: 
		        case 26: 
		        case 27: 
		        case 28: 
		        case 41: 
		          player.getPacketSender().sendInterfaceRemoval();
		          break;
		        case 14: 
			          TeleportHandler.teleportPlayer(player, new Position(2807, 10002), player.getSpellbook().getTeleportType());
			      break;
		        case 18: 
		          DialogueManager.start(player, 25);
		          player.setDialogueActionId(15);
		          break;
		        case 30: 
		        case 31: 
		          player.getPacketSender().sendInterfaceRemoval();
		          if (player.getSlayer().getDuoPartner() == null) {
		            break;
		          }
		          Slayer.resetDuo(player, World.getPlayerByName(player.getSlayer().getDuoPartner()));
		          
		          break;
		        case 45: 
		          player.getPacketSender().sendString(1, "www.agaroth.com/community/index.php?/topic/163-agaroth-ironman-gamemode/");
		          break;
		        case 127: 
		          DialogueManager.start(player, 133);
		          player.setDialogueActionId(128);
		        }
		      } else if (id == FIRST_OPTION_OF_TWO) {
		          switch (player.getDialogueActionId()) {
			      case 200:
			          player.setInputHandling(new ChangeUserTitle());
			          player.getPacketSender().sendInterfaceRemoval().sendEnterInputPrompt("Enter your new title:");
			    	  break;
		          case 157: 
				        DialogueManager.start(player, 165);
				        player.setDialogueActionId(158);
				        break;
		          case 158:
		        	  	Kraken.PayFee(player);
		        	  break;
		          case 155: 
			            TeleportHandler.teleportPlayer(player, new Position(3477, 9502), player.getSpellbook().getTeleportType());
			            break;
		          case 136: 
		            player.getPacketSender().sendInterfaceRemoval().sendMessage("You clear your inventory.");
		            player.getSkillManager().stopSkilling();
		            player.getInventory().resetItems().refreshItems();
		            break;
		          case 127: 
		            TeleportHandler.teleportPlayer(player, new Position(3450, 3715), player.getSpellbook().getTeleportType());
		            break;
		          case 135: 
		            player.setPlayerLocked(true);
		            DialogueManager.start(player, Tutorial.get(player, 14));
		            break;
		          case 3: 
		            ShopManager.getShops().get(22).open(player);
		            break;
		          case 4: 
		            SummoningTab.handleDismiss(player, true);
		            break;
		          case 7: 
		            BankPin.init(player, false);
		            break;
		          case 8: 
		            BankPin.deletePin(player);
		            break;
		          case 16: 
		            player.getPacketSender().sendInterfaceRemoval();
		            if (player.getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount() < 5)
		            {
		              player.getPacketSender().sendMessage("You must have a killcount of at least 5 to enter the tunnel.");
		              return;
		            }
		            player.moveTo(new Position(3552, 9692));
		            break;
		          case 20: 
		            player.getPacketSender().sendInterfaceRemoval();
		            DialogueManager.start(player, 39);
		            player.getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(0, true);
		            PlayerPanel.refreshPanel(player);
		            break;
		          case 23: 
		            DialogueManager.start(player, 50);
		            player.getMinigameAttributes().getNomadAttributes().setPartFinished(0, true);
		            player.setDialogueActionId(24);
		            PlayerPanel.refreshPanel(player);
		            break;
		          case 24: 
		            player.getPacketSender().sendInterfaceRemoval();
		            break;
		          case 33: 
		            player.getPacketSender().sendInterfaceRemoval();
		            player.getSlayer().resetSlayerTask();
		            break;
		          case 34: 
		            player.getPacketSender().sendInterfaceRemoval();
		            player.getSlayer().handleInvitation(true);
		            break;
		          case 37: 
		            TeleportHandler.teleportPlayer(player, new Position(2961, 3882), player.getSpellbook().getTeleportType());
		            break;
		          case 39: 
		            TeleportHandler.teleportPlayer(player, new Position(3281, 3914), player.getSpellbook().getTeleportType());
		            break;
		          case 42: 
		            player.getPacketSender().sendInterfaceRemoval();
		            if ((player.getInteractingObject() == null) || (player.getInteractingObject().getDefinition() == null) || (!player.getInteractingObject().getDefinition().getName().equalsIgnoreCase("flowers")) || 
		              (!CustomObjects.objectExists(player.getInteractingObject().getPosition()))) {
		              break;
		            }
		            player.getInventory().add(Gambling.FlowersData.forObject(player.getInteractingObject().getId()).itemId, 1);
		            CustomObjects.deleteGlobalObject(player.getInteractingObject());
		            player.setInteractingObject(null);
		            
		            break;
		          case 44: 
		            player.getPacketSender().sendInterfaceRemoval();
		            player.getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(true);
		            player.moveTo(new Position(2911, 5203));
		            player.getPacketSender().sendMessage("You enter Nex's lair..");
		            NpcAggression.target(player);
		            break;
		          case 46: 
		            player.getPacketSender().sendInterfaceRemoval();
		            DialogueManager.start(player, 82);
		            player.setPlayerLocked(true).setDialogueActionId(45);
		            break;
		          case 57: 
		            Graveyard.start(player);
		            break;
		          case 66: 
		            player.getPacketSender().sendInterfaceRemoval();
		            if ((player.getLocation() == Location.DUNGEONEERING) && (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) && 
		              (player.getMinigameAttributes().getDungeoneeringAttributes().getPartyInvitation() != null)) {
		              player.getMinigameAttributes().getDungeoneeringAttributes().getPartyInvitation().add(player);
		            }
		            player.getMinigameAttributes().getDungeoneeringAttributes().setPartyInvitation(null);
		            break;
		          case 71: 
		            if ((!player.getClickDelay().elapsed(1000L)) || 
		              (!Dungeoneering.doingDungeoneering(player))) {
		              break;
		            }
		            Dungeoneering.leave(player, false, true);
		            player.getClickDelay().reset();
		            
		            break;
		          case 72: 
		            if ((!player.getClickDelay().elapsed(1000L)) || 
		              (!Dungeoneering.doingDungeoneering(player))) {
		              break;
		            }
		            Dungeoneering.leave(player, false, player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner() != player);
		            player.getClickDelay().reset();
		            
		            break;
		          case 73: 
		            player.getPacketSender().sendInterfaceRemoval();
		            player.moveTo(new Position(3653, player.getPosition().getY()));
		            break;
		          case 74: 
		            player.getPacketSender().sendMessage("The ghost teleports you away.");
		            player.getPacketSender().sendInterfaceRemoval();
		            player.moveTo(new Position(3651, 3486));
		            break;
		          case 76: 
		            player.getPacketSender().sendInterfaceRemoval();
		            if (player.getRights().isStaff())
		            {
		              player.getPacketSender().sendMessage("You cannot change your rank.");
		              return;
		            }
		            player.setRights(PlayerRights.GLOBAL_MODERATOR);
		            player.getPacketSender().sendRights();
		            break;
		          case 78: 
		            player.getPacketSender().sendString(38006, "Select a skill...").sendString(38090, "Which skill would you like to prestige?");
		            player.getPacketSender().sendInterface(38000);
		            player.setUsableObject(new Object[2]).setUsableObject(0, "prestige");
		            break;
		          }
		        } else if (id == SECOND_OPTION_OF_TWO) {
		          switch (player.getDialogueActionId()) {
			        case 200:
			        	player.getPacketSender().sendInterfaceRemoval();
			        	break;
		          case 158: 
		        	  player.getPacketSender().sendInterfaceRemoval();
						break;
		          case 155: 
			            TeleportHandler.teleportPlayer(player, new Position(3217, 3921), player.getSpellbook().getTeleportType());
			            break;
		          case 136: 
		            player.getPacketSender().sendInterfaceRemoval();
		            player.getSkillManager().stopSkilling();
		            break;
		          case 135: 
		            player.setPlayerLocked(true);
		            DialogueManager.start(player, Tutorial.get(player, 0));
		            break;
		          case 3: 
		            ShopManager.getShops().get(23).open(player);
		            break;
		          case 4: 
		          case 16: 
		          case 20: 
		          case 23: 
		          case 33: 
		          case 37: 
		          case 39: 
		          case 42: 
		          case 44: 
		          case 46: 
		          case 57: 
		          case 71: 
		          case 72: 
		          case 73: 
		          case 74: 
		          case 76: 
		          case 78: 
		            player.getPacketSender().sendInterfaceRemoval();
		            break;
		          case 127: 
		            TeleportHandler.teleportPlayer(player, new Position(2209, 5348), player.getSpellbook().getTeleportType());
		            break;
		          case 7: 
		          case 8: 
		            player.getPacketSender().sendInterfaceRemoval();
		            player.getBank(player.getCurrentBankTab()).open();
		            break;
		          case 24: 
		            Nomad.startFight(player);
		            break;
		          case 34: 
		            player.getPacketSender().sendInterfaceRemoval();
		            player.getSlayer().handleInvitation(false);
		            break;
		          case 66: 
		            player.getPacketSender().sendInterfaceRemoval();
		            if ((player.getMinigameAttributes().getDungeoneeringAttributes().getPartyInvitation() != null) && (player.getMinigameAttributes().getDungeoneeringAttributes().getPartyInvitation().getOwner() != null)) {
		              player.getMinigameAttributes().getDungeoneeringAttributes().getPartyInvitation().getOwner().getPacketSender().sendMessage(player.getUsername() + " has declined your invitation.");
		            }
		            player.getMinigameAttributes().getDungeoneeringAttributes().setPartyInvitation(null);
		            break;
		          }
		        } else if (id == FIRST_OPTION_OF_THREE) {
		            switch (player.getDialogueActionId()) {
		            case 96:
				    	  RiggedSeeds.TrollPlant(player, 7);
				    	  break;
		            case 9: 
				          DialogueManager.start(player, 14);
				          break;
		            case 158: 
			              ShopManager.getShops().get(57).open(player);
			              player.getPacketSender().sendMessage("<img=11> <col=660000>Items on sale here are untradable until release!");
			              break;
		            case 128: 
			            TeleportHandler.teleportPlayer(player, new Position(3272, 4859), player.getSpellbook().getTeleportType());
			            break;
		            case 141: 
		            	 if (player.getGameMode() != GameMode.NORMAL) {
		            		 ShopManager.getShops().get(55).open(player);
		            		 player.getPacketSender().sendMessage("<img=11> <col=660000>You currently have "+player.getPointsHandler().IronManPoints+" Ironman points.");
		            	 } else {
				              DialogueManager.start(player, 146);
		            	 }
		            	break;
		            case 127: 
		              TeleportHandler.teleportPlayer(player, new Position(2725, 3485), player.getSpellbook().getTeleportType());
		              break;
		            case 137: 
		              player.setSpellbook(MagicSpellbook.NORMAL);
		              player.getPacketSender().sendTabInterface(6, player.getSpellbook().getInterfaceId()).sendMessage("Your magic spellbook is changed..");
		              Autocasting.resetAutocast(player, true);
		              player.SwitchBookTimer = System.currentTimeMillis();
		              break;
		            case 15: 
		              DialogueManager.start(player, 35);
		              player.setDialogueActionId(19);
		              break;
		            case 19: 
		              DialogueManager.start(player, 33);
		              player.setDialogueActionId(21);
		              break;
		            case 21: 
		              TeleportHandler.teleportPlayer(player, new Position(3080, 3498), player.getSpellbook().getTeleportType());
		              break;
		            case 22: 
		              TeleportHandler.teleportPlayer(player, new Position(1891, 3177), player.getSpellbook().getTeleportType());
		              break;
		            case 25: 
		              TeleportHandler.teleportPlayer(player, new Position(2589, 4319), TeleportType.PURO_PURO);
		              break;
		            case 35: 
		              player.getPacketSender().sendEnterAmountPrompt("How many shards would you like to buy? (You can use K, M, B prefixes)");
		              player.setInputHandling(new BuyShards());
		              break;
		            case 41: //Corporeal beast
		              TeleportHandler.teleportPlayer(player, new Position(2884 + Misc.getRandom(1), 4374 + Misc.getRandom(1)), player.getSpellbook().getTeleportType());
		              break;
		            case 47: 
		              TeleportHandler.teleportPlayer(player, new Position(2911, 4832), player.getSpellbook().getTeleportType());
		              break;
		            case 48: 
		              if (player.getInteractingObject() != null) {
		                Mining.startMining(player, new GameObject(24444, player.getInteractingObject().getPosition()));
		              }
		              player.getPacketSender().sendInterfaceRemoval();
		              break;
		            case 56: 
		              TeleportHandler.teleportPlayer(player, new Position(2717, 3499), player.getSpellbook().getTeleportType());
		              break;
		            case 58: 
		              DialogueManager.start(player, AgilityTicketExchange.getDialogue(player));
		              break;
		            case 61: 
		              CharmingImp.changeConfig(player, 0, 0);
		              break;
		            case 62: 
		              CharmingImp.changeConfig(player, 1, 0);
		              break;
		            case 63: 
		              CharmingImp.changeConfig(player, 2, 0);
		              break;
		            case 64: 
		              CharmingImp.changeConfig(player, 3, 0);
		              break;
		            case 65: 
		              player.getPacketSender().sendInterfaceRemoval();
		              if (player.getSlayer().getDuoPartner() != null)
		              {
		                player.getPacketSender().sendMessage(
		                  "You already have a duo partner.");
		                return;
		              }
		              player.getPacketSender().sendMessage("<img=11> To do Social slayer, simply use your Slayer gem on another player.");
		              break;
		            case 69: 
		              ShopManager.getShops().get(44).open(player);
		              player.getPacketSender().sendMessage("<img=11> <col=660000>You currently have " + player.getPointsHandler().getDungeoneeringTokens() + " Dungeoneering tokens.");
		              break;
		            case 70: 
		            case 71: 
		              if ((!player.getInventory().contains(19670)) || (!player.getClickDelay().elapsed(700L))) {
		                break;
		              }
		              int amt = player.getDialogueActionId() == 70 ? 1 : player.getInventory().getAmount(19670);
		              player.getPacketSender().sendInterfaceRemoval();
		              player.getInventory().delete(19670, amt);
		              player.getPacketSender().sendMessage("You claim the " + (amt > 1 ? "scrolls" : "scroll") + " and receive your reward.");
		              int minutes = player.getGameMode() == GameMode.NORMAL ? 10 : 5;
		              BonusExperienceTask.addBonusXp(player, minutes * amt);
		              player.getClickDelay().reset();
		            }
		          } else if (id == SECOND_OPTION_OF_THREE) {
		            switch (player.getDialogueActionId()) {
		            case 96:
				    	  RiggedSeeds.TrollPlant(player, 8);
				    	  break;
		            case 9: 
				          ShopManager.getShops().get(41).open(player);
				          break;
		            case 158: 
		            	if (player.getAmountDonated() >= 250 || player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.ADMINISTRATOR || player.getRights() == PlayerRights.MODERATOR) {
		            		DialogueManager.start(player, 161);
		            	} else {
		            	 DialogueManager.start(player, 164);
		            	}
			            break;
		            case 128: 
			            TeleportHandler.teleportPlayer(player, new Position(2595, 4772), player.getSpellbook().getTeleportType());
			            break;
		            case 141: 
		            	if (player.getGameMode() != GameMode.NORMAL) {
			            	ShopManager.getShops().get(56).open(player);
		            		 player.getPacketSender().sendMessage("<img=11> <col=660000>You currently have "+player.getPointsHandler().IronManPoints+" Ironman points.");
		            	 } else {
				              DialogueManager.start(player, 146);
		            	 }
			          break;
		            case 137: 
		              player.setSpellbook(MagicSpellbook.ANCIENT);
		              player.getPacketSender().sendTabInterface(6, player.getSpellbook().getInterfaceId()).sendMessage("Your magic spellbook is changed..");
		              Autocasting.resetAutocast(player, true);
		              player.SwitchBookTimer = System.currentTimeMillis();
		              break;
		            case 127: 
		              DialogueManager.start(player, 86);
		              player.setDialogueActionId(47);
		              break;
		            case 15: 
		              DialogueManager.start(player, 25);
		              player.setDialogueActionId(15);
		              break;
		            case 21: 
		              RecipeForDisaster.openQuestLog(player);
		              break;
		            case 19: 
		              DialogueManager.start(player, 33);
		              player.setDialogueActionId(22);
		              break;
		            case 22: 
		              Nomad.openQuestLog(player);
		              break;
		            case 25: 
		              player.getPacketSender().sendInterfaceRemoval();
		              if (player.getSkillManager().getCurrentLevel(Skill.HUNTER) < 23)
		              {
		                player.getPacketSender().sendMessage("You need a Hunter level of at least 23 to visit the hunting area.");
		                return;
		              }
		              TeleportHandler.teleportPlayer(player, new Position(2922, 2885), player.getSpellbook().getTeleportType());
		              break;
		            case 35: 
		              player.getPacketSender().sendEnterAmountPrompt("How many shards would you like to sell? (You can use K, M, B prefixes)");
		              player.setInputHandling(new SellShards());
		              break;
		            case 41: //nex
		              TeleportHandler.teleportPlayer(player, new Position(2903, 5204), player.getSpellbook().getTeleportType());
		              break;
		            case 47: 
		              TeleportHandler.teleportPlayer(player, new Position(3023, 9740), player.getSpellbook().getTeleportType());
		              break;
		            case 48: 
		              if (player.getInteractingObject() != null) {
		                Mining.startMining(player, new GameObject(24445, player.getInteractingObject().getPosition()));
		              }
		              player.getPacketSender().sendInterfaceRemoval();
		              break;
		            case 56: 
		              player.getPacketSender().sendInterfaceRemoval();
		              if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < 60)
		              {
		                player.getPacketSender().sendMessage("You need a Woodcutting level of at least 60 to teleport there.");
		                return;
		              }
		              TeleportHandler.teleportPlayer(player, new Position(2711, 3463), player.getSpellbook().getTeleportType());
		              break;
		            case 58: 
		              ShopManager.getShops().get(39).open(player);
		              break;
		            case 61: 
		              CharmingImp.changeConfig(player, 0, 1);
		              break;
		            case 62: 
		              CharmingImp.changeConfig(player, 1, 1);
		              break;
		            case 63: 
		              CharmingImp.changeConfig(player, 2, 1);
		              break;
		            case 64: 
		              CharmingImp.changeConfig(player, 3, 1);
		              break;
		            case 65: 
		              player.getPacketSender().sendInterfaceRemoval();
		              if (player.getSlayer().getDuoPartner() == null) {
		                break;
		              }
		              Slayer.resetDuo(player, World.getPlayerByName(player.getSlayer().getDuoPartner()));
		              
		              break;
		            case 69: 
		              if (!player.getClickDelay().elapsed(1000L)) {
		                break;
		              }
		              Dungeoneering.start(player);
		              
		              break;
		            case 70: 
		            case 71: 
		              boolean all = player.getDialogueActionId() == 71;
		              player.getPacketSender().sendInterfaceRemoval();
		              if (player.getInventory().getFreeSlots() == 0)
		              {
		                player.getPacketSender().sendMessage("You do not have enough free inventory space to do that.");
		                return;
		              }
		              if ((!player.getInventory().contains(19670)) || (!player.getClickDelay().elapsed(700L))) {
		                break;
		              }
		              int amt = !all ? 1 : player.getInventory().getAmount(19670);
		              player.getInventory().delete(19670, amt);
		              player.getPointsHandler().incrementVotingPoints(amt);
		              player.getPointsHandler().refreshPanel();
		              if (player.getGameMode() == GameMode.NORMAL) {
		                player.getInventory().add(995, 500000 * amt);
		              } else {
		                player.getInventory().add(995, 100000 * amt);
		              }
		              player.getPacketSender().sendMessage("You claim the " + (amt > 1 ? "scrolls" : "scroll") + " and receive your reward.");
		              player.getClickDelay().reset();
		            }
		          } else if (id == THIRD_OPTION_OF_THREE) {
		            switch (player.getDialogueActionId()) {
		            case 96:
				    	  RiggedSeeds.open(player, 1);
				    	  break;
		            case 158: 
		            	if (player.getAmountDonated() >= 250 || player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.ADMINISTRATOR || player.getRights() == PlayerRights.MODERATOR) {
		            		DialogueManager.start(player, 161);
		            	} else {
		            	 DialogueManager.start(player, 164);
		            	}
			            break;
		            case 128: 
			            TeleportHandler.teleportPlayer(player, new Position(2911, 4832), player.getSpellbook().getTeleportType());
			            break;
		            case 141: 
		            	if (player.getGameMode() != GameMode.NORMAL) {
				            DialogueManager.start(player, 142);
		            		 player.getPacketSender().sendMessage("<img=11> <col=660000>You currently have "+player.getPointsHandler().IronManPoints+" Ironman points.");
		            	 } else {
				              DialogueManager.start(player, 146);
		            	 }
			          break;
		            case 137: 
		              player.setSpellbook(MagicSpellbook.LUNAR);
		              player.getPacketSender().sendTabInterface(6, player.getSpellbook().getInterfaceId()).sendMessage("Your magic spellbook is changed..");
		              Autocasting.resetAutocast(player, true);
		              player.SwitchBookTimer = System.currentTimeMillis();
		              break;
		            case 5: 
		            case 10: 
		            case 15: 
		            case 19: 
		            case 21: 
		            case 22: 
		            case 25: 
		            case 35: 
		            case 47: 
		            case 48: 
		            case 56: 
		            case 58: 
		            case 61: 
		            case 62: 
		            case 63: 
		            case 64: 
		            case 65: 
		            case 69: 
		            case 70: 
		            case 71: 
		            case 77: 
		            case 9:
		              player.getPacketSender().sendInterfaceRemoval();
		              break;
		            case 41: 
		              player.setDialogueActionId(36);
		              DialogueManager.start(player, 65);
		              break;
		            case 127: 
		              TeleportHandler.teleportPlayer(player, new Position(2345, 3698), player.getSpellbook().getTeleportType());
		            }
		          }
		       }
	public static int FIRST_OPTION_OF_FIVE = 2494;
	public static int SECOND_OPTION_OF_FIVE = 2495;
	public static int THIRD_OPTION_OF_FIVE = 2496;
	public static int FOURTH_OPTION_OF_FIVE = 2497;
	public static int FIFTH_OPTION_OF_FIVE = 2498;

	public static int FIRST_OPTION_OF_FOUR = 2482;
	public static int SECOND_OPTION_OF_FOUR = 2483;
	public static int THIRD_OPTION_OF_FOUR = 2484;
	public static int FOURTH_OPTION_OF_FOUR = 2485;


	public static int FIRST_OPTION_OF_THREE = 2471;
	public static int SECOND_OPTION_OF_THREE = 2472;
	public static int THIRD_OPTION_OF_THREE = 2473;

	public static int FIRST_OPTION_OF_TWO = 2461;
	public static int SECOND_OPTION_OF_TWO = 2462;

}
