package com.agaroth.net.packet.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import com.agaroth.GameServer;
import com.agaroth.GameSettings;
import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Flag;
import com.agaroth.model.GameObject;
import com.agaroth.model.Graphic;
import com.agaroth.model.Item;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.model.Skill;
import com.agaroth.model.Locations.Location;
import com.agaroth.model.container.impl.Bank;
import com.agaroth.model.container.impl.Equipment;
import com.agaroth.model.container.impl.Shop.ShopManager;
import com.agaroth.model.definitions.WeaponAnimations;
import com.agaroth.model.definitions.WeaponInterfaces;
import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.net.security.ConnectionHandler;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.content.BonusManager;
import com.agaroth.world.content.DropTable;
import com.agaroth.world.content.Lottery;
import com.agaroth.world.content.LoyaltyProgramme.LoyaltyTitles;
import com.agaroth.world.content.PlayerLogs;
import com.agaroth.world.content.PlayerPunishment;
import com.agaroth.world.content.PlayersOnlineInterface;
import com.agaroth.world.content.WellOfGoodwill;
import com.agaroth.world.content.clan.ClanChatManager;
import com.agaroth.world.content.combat.CombatFactory;
import com.agaroth.world.content.combat.DesolaceFormulas;
import com.agaroth.world.content.combat.weapon.CombatSpecial;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.content.grandexchange.GrandExchangeOffers;
import com.agaroth.world.content.randomevents.EvilTree;
import com.agaroth.world.content.randomevents.ShootingStar;
import com.agaroth.world.content.skill.SkillManager;
import com.agaroth.world.content.transportation.TeleportHandler;
import com.agaroth.world.content.transportation.TeleportType;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;
import com.agaroth.world.entity.impl.player.PlayerHandler;
import com.agaroth.world.entity.impl.player.PlayerSaving;

import mysql.MySQLController;
import mysql.impl.Store;


public class CommandPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		String command = Misc.readString(packet.getBuffer());
		String[] parts = command.toLowerCase().split(" ");
		if(command.contains("\r") || command.contains("\n")) {
			return;
		}
		try {
			switch (player.getRights()) {
			case PLAYER:
				playerCommands(player, parts, command);
				break;
			case MODERATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				break;
			case ADMINISTRATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				administratorCommands(player, parts, command);
				break;
			case OWNER:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				administratorCommands(player, parts, command);
				ownerCommands(player, parts, command);
				developerCommands(player, parts, command);
				break;
			case DEVELOPER:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				administratorCommands(player, parts, command);
				ownerCommands(player, parts, command);
				developerCommands(player, parts, command);
				break;
			case DONATOR:
			case SUPER_DONATOR:
			case EXTREME_DONATOR:
			case LEGENDARY_DONATOR:
			case UBER_DONATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				break;
			case GLOBAL_MODERATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				break;
			case SUPPORT:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				break;
			default:
				break;
			}
		} catch (Exception exception) {
			if(player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.OWNER) {
				player.getPacketSender().sendConsoleMessage("Error executing that command.");
			} else {
				player.getPacketSender().sendMessage("Error executing that command.");
			}
		}
	}
	private static void playerCommands(Player player, String[] command, String wholeCommand) {
		 if ((wholeCommand.equalsIgnoreCase("home")) || (wholeCommand.equalsIgnoreCase("Home"))) {
		      TeleportHandler.teleportPlayer(player, new Position(3087, 3500), player.getSpellbook().getTeleportType());
		    }
		 	if(command[0].equals("hiscores")) {
		 		player.getPacketSender().sendString(1, "http://sintennial.everythingrs.com/services/hiscores");
		 		player.getPacketSender().sendMessage("Attempting to open Sintennial's Hiscores...");
		 	}
		 	
		 	if (command[0].equals("drops")) {
		 		DropTable.openTable(player, 0, false);
		 	}

			if (command[0].equals("answer")) {
				if (player.hasDisabledTrivia()) {
					player.getPacketSender().sendMessage("Please re-enable trivia by typing ::trivia");
					return;
				}
				String name = "";
				for (int i = 1; i < command.length; i++) {
					name += command[i] + ((i == command.length - 1) ? "" : " ");
				}
				PlayerHandler.getInstance().verify(player, name);
				return;
			}
			
			if (command[0].equals("trivia")) {
				player.setDisableTrivia(!player.hasDisabledTrivia());
				player.getPacketSender().sendMessage("Trivia questions are now "+(player.hasDisabledTrivia() ? "hidden" : "visible")+"");
				return;
			}
		 if (command[0].equals("discord")) {
		      player.getPacketSender().sendString(1, "http://discord.gg/P8bmfCe");
		      player.getPacketSender().sendMessage("Attempting to join Sintennial's Discord server...");
		 }
		 if (command[0].equals("donate")) {
		      player.getPacketSender().sendString(1, "http://sintennial.everythingrs.com/services/store");
		      player.getPacketSender().sendMessage("Attempting to open Donation Store...");
		 }
		 if (command[0].equalsIgnoreCase("claim")) {
				new java.lang.Thread() {
					public void run() {
						try {
							com.everythingrs.donate.Donation[] donations = com.everythingrs.donate.Donation.donations("qxjv349m1511r2jf0fd5nrk92gncaov4n8vbo3yyix0t2ihpvi2f8mrr9p2zdvutoonyacfh6w29", 
									player.getUsername());
							if (donations.length == 0) {
								player.getPacketSender().sendMessage("You currently don't have any items waiting. You must donate first!");
								return;
							}
							if (donations[0].message != null) {
								player.getPacketSender().sendMessage(donations[0].message);
								return;
							}
							for (com.everythingrs.donate.Donation donate : donations) {
								player.getInventory().add(new Item(donate.product_id, donate.product_amount));
							}
							player.getPacketSender().sendMessage("Thank you for donating!");
						} catch (Exception e) {
							player.getPacketSender().sendMessage("Api Services are currently offline. Please check back shortly");
							e.printStackTrace();
						}	
					}
				}.start();
			}
		 if (command[0].startsWith("reward")) {
				if (command.length == 1) {
					player.getPacketSender().sendMessage("Please use [::reward id], [::reward id amount], or [::reward id all].");
					return;
				}
				final String playerName = player.getUsername();
				final String id = command[1];
				final String amount = command.length == 3 ? command[2] : "1";

				com.everythingrs.vote.Vote.service.execute(new Runnable() {
					@Override
					public void run() {
						try {
							com.everythingrs.vote.Vote[] reward = com.everythingrs.vote.Vote.reward("qxjv349m1511r2jf0fd5nrk92gncaov4n8vbo3yyix0t2ihpvi2f8mrr9p2zdvutoonyacfh6w29",
									playerName, id, amount);
							if (reward[0].message != null) {
								player.getPacketSender().sendMessage(reward[0].message);
								return;
							}
							player.getInventory().add(reward[0].reward_id, reward[0].give_amount);
							player.getPacketSender().sendMessage("Thank you for voting! You now have " + reward[0].vote_points + " vote points.");
						} catch (Exception e) {
							player.getPacketSender().sendMessage("Api Services are currently offline. Please check back shortly");
							e.printStackTrace();
						}
					}

				});
			}
		if(command[0].equalsIgnoreCase("auth")) {
			
			if(command.length >= 2) {
				
				String auth = command[1];
				
				if(auth == null || auth.length() < 6) {
					player.getPacketSender().sendMessage("The syntax for this command is ::auth 123456.");
					return;
				}
				
				if(player.getInventory().getFreeSlots() < 1) {
					player.getPacketSender().sendMessage("You need at least one single inventory slot to use the auth command.");
					return;
				}
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						
						try {
						
							URL oracle = new URL("http://lunarisle.org/vote/claimauth.php?key=97b0d559&auth="+URLEncoder.encode(auth, "UTF-8"));
					        BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
					        String string = in.readLine();
					        
					        switch(string.toLowerCase()) {
					        case "success":
					        	player.getInventory().add(20935, 1);
					        	player.getPacketSender().sendMessage("You received a reward for voting.");
					        	break;
					        default:
					        	player.getPacketSender().sendMessage("That auth code could not be found in our database.");
					        	break;
					        }
				        
						} catch(Throwable t) {
							player.getPacketSender().sendMessage("An error occured while using the auth code command.");
						}
				        
					}
					
				}).start();
				
			} else {
				player.getPacketSender().sendMessage("The syntax for this command is ::auth 123456.");
			}
			
			return;
		}
		if(command[0].equalsIgnoreCase("claimdonation")) {
			if (System.currentTimeMillis() - player.claimTimer < 30000) {
				player.getPacketSender().sendMessage("You can check for Donator points every 30 seconds.");
				player.getPacketSender().sendInterfaceRemoval();
				return;
			}
			player.claimTimer = System.currentTimeMillis();
			new Store().claim(player);
			return;
		}
		if ((wholeCommand.equalsIgnoreCase("gamble")) || (wholeCommand.equalsIgnoreCase("Gamble"))) {
			player.getPacketSender().sendMessage("<col=FF0066>[WARNING!]");
			 player.getPacketSender().sendMessage("<col=6600FF>Make sure to use a TRUSTED Mm or staff.");
			 player.getPacketSender().sendMessage("<col=6600FF>Record every bet.");
			 player.getPacketSender().sendMessage("<col=6600FF>Don't risk what you aren't willing to lose.");
		      TeleportHandler.teleportPlayer(player, new Position(2440, 3088), player.getSpellbook().getTeleportType());
		    }
		 if (command[0].equalsIgnoreCase("invite")) {
		      Player player2 = World.getPlayerByName(wholeCommand.substring(7));
					player.getMinigameAttributes().getDungeoneeringAttributes().getParty().invite(player2);
						player.getPacketSender().sendInterfaceRemoval();
		 }
		if (wholeCommand.equalsIgnoreCase("bug")) {
		      player.getPacketSender().sendString(1, "http://sintennialps.freeforums.net/board/17/report-bug");
		      player.getPacketSender().sendMessage("Attempting to open 'Report a Bug' forums section...");
		}
	/*	if (wholeCommand.equalsIgnoreCase("discount")) {
		      player.getPacketSender().sendString(1, "www.lunarisle.org/community/index.php?/topic/375-agaroth-weekly-donator-store-discount/?p=1586");
		      player.getPacketSender().sendMessage("Attempting to open: www.lunarisle.org/community/");
		} */
		if (command[0].equalsIgnoreCase("changepass")) {
		      String pass = Misc.formatText(wholeCommand.substring(11));
		      player.setPassword(pass.trim());
		      player.getPacketSender().sendMessage("Your new password is: "+pass+"");
		    }
	/*	if (wholeCommand.equalsIgnoreCase("event")) {
	      player.getPacketSender().sendString(1, "www.lunarisle.org/community/index.php?/topic/313-heavy-gains-monday/#entry1283");
	      player.getPacketSender().sendMessage("Attempting to open: www.lunarisle.org/community/");
	    } */
		    if (wholeCommand.equalsIgnoreCase("suggest")) {
			      player.getPacketSender().sendString(1, "http://sintennialps.freeforums.net/board/7/suggestions");
			      player.getPacketSender().sendMessage("Attempting to open forums suggestions page...");
			    }
	  /*  if (wholeCommand.equalsIgnoreCase("fundraise")) {
		      player.getPacketSender().sendString(1, "www.lunarisle.org/community/index.php?/topic/221-fundraising-event/#entry951");
		      player.getPacketSender().sendMessage("Attempting to open: www.lunarisle.org/forums/");
		    } */
	    if (wholeCommand.equalsIgnoreCase("recent")) {
	      player.getPacketSender().sendString(1, "http://sintennialps.freeforums.net/board/1/news-announcements");
	      player.getPacketSender().sendMessage("Attempting to open forum updates section...");
	    }
	    if (command[0].equalsIgnoreCase("attacks")) {
	      int attack = DesolaceFormulas.getMeleeAttack(player);
	      int range = DesolaceFormulas.getRangedAttack(player);
	      int magic = DesolaceFormulas.getMagicAttack(player);
	      player.getPacketSender().sendMessage("@bla@Melee attack: @or2@" + attack + "@bla@, ranged attack: @or2@" + range + "@bla@, magic attack: @or2@" + magic);
	    }
	    if(command[0].equals("roll") && player.getUsername().equalsIgnoreCase("pure gold")) {
			if(player.getClanChatName() == null) {
				player.getPacketSender().sendMessage("You need to be in a clanchat channel to roll a dice.");
				return;
			} else if(player.getClanChatName().equalsIgnoreCase("help")) {
				player.getPacketSender().sendMessage("You can't roll a dice in this clanchat channel!");
				return;
			}
			int dice = Integer.parseInt(command[1]);
			player.getMovementQueue().reset();
			player.performAnimation(new Animation(11900));
			player.performGraphic(new Graphic(2075));
			ClanChatManager.sendMessage(player.getCurrentClanChat(), "@bla@[ClanChat] @whi@"+player.getUsername()+" just rolled @bla@" +dice+ "@whi@ on the percentile dice.");
		}
	    if (command[0].equals("save")) {
	      player.save();
	      player.getPacketSender().sendMessage("Your progress has been saved.");
	    }
/*	    if (command[0].equals("hs")) {
	      player.getPacketSender().sendString(1, "www.lunarisle.org/hiscores/");
	      player.getPacketSender().sendMessage("Attempting to open: www.lunarisle.org/hiscores/");
	    } */
	    if (command[0].equals("vote")) {
	      player.getPacketSender().sendString(1, "http://sintennial.everythingrs.com/services/vote");
	      player.getPacketSender().sendMessage("Attempting to open voting portal...");
	    }
	    if (command[0].equals("help")) {
	      if (player.getLastYell().elapsed(30000)) {
	        World.sendStaffMessage("<col=FF0066><img=11> [HELP SYSTEM]<col=6600FF> " + player.getUsername() + " has requested help. Please help them!");
	        player.getLastYell().reset();
	        player.getPacketSender().sendMessage("<col=663300>Your help request has been sent to online staff. Please be patient.");
	      }
	      else {
	        player.getPacketSender().sendMessage("").sendMessage("<col=663300>You need to wait 30 seconds before using this again.").sendMessage("<col=663300>If it's an emergency, please private message a staff member directly instead.");
	      }
	    }
	    if (command[0].equals("empty")) {
	      DialogueManager.start(player, 136);
	      player.setDialogueActionId(136);
	    }
	      if(command[0].equals("players")) {
		  player.getPacketSender().sendInterfaceRemoval();
		   PlayersOnlineInterface.showInterface(player);
	}	    if ((command[0].equalsIgnoreCase("[cn]")) && 
	      (player.getInterfaceId() == 40172)) {
	      ClanChatManager.setName(player, wholeCommand.substring(wholeCommand.indexOf(command[1])));
	   }
		/*  if ((command[0].equalsIgnoreCase("commands"))) {
				player.getPacketSender().sendInterfaceRemoval();
				CommandsInterface.showInterface(player);
			} */
	}

	private static void memberCommands(Player player, String[] command, String wholeCommand)
	  {
		if(wholeCommand.toLowerCase().startsWith("yell")) {
			if(PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
				player.getPacketSender().sendMessage("You are muted and cannot yell.");
				return;
			}
			int delay = player.getRights().getYellDelay();
			if(!player.getLastYell().elapsed((delay*1000))) {
				player.getPacketSender().sendMessage("You must wait at least "+delay+" seconds between every yell-message you send.");
				return;
			}
			String yellMessage = wholeCommand.substring(4, wholeCommand.length());
			String yellTitle = player.getYellTitle();
			if (yellTitle == null) {
				yellTitle = Misc.formatPlayerName(player.getRights().name()).replace("_Donator", "");
				if (yellTitle.equals("Regular")) {
					yellTitle = "Donator";
				}
			}
			if (player.getRights().isStaff()) {
				World.sendMessage(player.getRights().getYellPrefix()+"[<img="+player.getRights().ordinal()+">"+yellTitle+"<img="+player.getRights().ordinal()+">]</shad></col> "+player.getUsername()+":"+yellMessage);
			} else {
				World.sendMessage("<img="+player.getRights().ordinal()+">"+player.getRights().getYellPrefix()+"["+yellTitle+"]</shad></col> "+player.getUsername()+":"+yellMessage);
			}
			player.getLastYell().reset();
			}
	    if ((wholeCommand.equalsIgnoreCase("dzone")) || (wholeCommand.equalsIgnoreCase("donorzone")) || (wholeCommand.equalsIgnoreCase("dz"))) {
	      TeleportHandler.teleportPlayer(player, new Position(3363, 9649), player.getSpellbook().getTeleportType());
	    }
	 if ((wholeCommand.equalsIgnoreCase("ezone")) || (wholeCommand.equalsIgnoreCase("extremezone")) || (wholeCommand.equalsIgnoreCase("ez"))) {
	      if(player.getRights() == PlayerRights.EXTREME_DONATOR || player.getRights() == PlayerRights.LEGENDARY_DONATOR || player.getRights() == PlayerRights.UBER_DONATOR || player.getRights() == PlayerRights.MODERATOR || player.getRights() == PlayerRights.SUPPORT || player.getRights() == PlayerRights.GLOBAL_MODERATOR || player.getRights() == PlayerRights.ADMINISTRATOR || player.getRights() == PlayerRights.OWNER || player.getRights() == PlayerRights.DEVELOPER) {
	    	  TeleportHandler.teleportPlayer(player, new Position(3363, 9649), player.getSpellbook().getTeleportType());
	      }
	    }
	  }

	private static void helperCommands(Player player, String[] command, String wholeCommand)
	  {
	    if (command[0].equalsIgnoreCase("jail")) {
	      Player player2 = World.getPlayerByName(wholeCommand.substring(5));
	      if (player2 != null) {
	        if (PlayerPunishment.Jail.isJailed(player2)) {
	          player.getPacketSender().sendConsoleMessage("That player is already jailed!");
	          return;
	        }
	        if (PlayerPunishment.Jail.jailPlayer(player2)) {
	          player2.getSkillManager().stopSkilling();
	          PlayerLogs.log(player.getUsername(), player.getUsername() + " just jailed " + player2.getUsername() + "!");
	          player.getPacketSender().sendMessage("Jailed player: " + player2.getUsername());
	          player2.getPacketSender().sendMessage("You have been jailed by " + player.getUsername() + ".");
	        }
	        else {
	          player.getPacketSender().sendConsoleMessage("Jail is currently full.");
	        }
	      }
	      else {
	        player.getPacketSender().sendConsoleMessage("Could not find that player online.");
	      }
	    }
	    if (command[0].equals("remind")) {
	      World.sendMessage("<img=11> <col=008FB2>Remember to collect rewards by using the ::vote command every 12 hours!");
	    }
	    if (command[0].equalsIgnoreCase("unjail")) {
	      Player player2 = World.getPlayerByName(wholeCommand.substring(7));
	      if (player2 != null) {
	        PlayerPunishment.Jail.unjail(player2);
	        PlayerLogs.log(player.getUsername(), player.getUsername() + " just unjailed " + player2.getUsername() + "!");
	        player.getPacketSender().sendMessage("Unjailed player: " + player2.getUsername());
	        player2.getPacketSender().sendMessage("You have been unjailed by " + player.getUsername() + ".");
	      }
	      else {
	        player.getPacketSender().sendConsoleMessage("Could not find that player online.");
	      }
	    }
	    if (command[0].equals("staffzone")) {
	      if ((command.length > 1) && (command[1].equals("all"))) {
	        for (Player players : World.getPlayers()) {
	          if ((players != null) && 
	            (players.getRights().isStaff())) {
	            TeleportHandler.teleportPlayer(players, new Position(2846, 5147), TeleportType.NORMAL);
	          }
	        }
	      } else {
	        TeleportHandler.teleportPlayer(player, new Position(2846, 5147), TeleportType.NORMAL);
	      }
	    }
	    if (command[0].equalsIgnoreCase("saveall")) {
	      World.savePlayers();
	      player.getPacketSender().sendMessage("Saved players!");
	    }
	    if (command[0].equalsIgnoreCase("teleto")) {
	      String playerToTele = wholeCommand.substring(7);
	      Player player2 = World.getPlayerByName(playerToTele);
	      if (player2 == null)
	      {
	        player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
	        return;
	      }
	      boolean canTele = (TeleportHandler.checkReqs(player, player2.getPosition().copy())) && (player.getRegionInstance() == null) && (player2.getRegionInstance() == null);
	      if (canTele) {
	        TeleportHandler.teleportPlayer(player, player2.getPosition().copy(), TeleportType.NORMAL);
	        player.getPacketSender().sendConsoleMessage("Teleporting to player: " + player2.getUsername());
	      }
	      else {
	        player.getPacketSender().sendConsoleMessage("You can not teleport to this player at the moment. Minigame maybe?");
	      }
	    }
	    if (command[0].equalsIgnoreCase("movehome")) {
	      String player2 = command[1];
	      player2 = Misc.formatText(player2.replaceAll("_", " "));
	      if ((command.length >= 3) && (command[2] != null)) {
	        player2 = player2 + " " + Misc.formatText(command[2].replaceAll("_", " "));
	      }
	      Player playerToMove = World.getPlayerByName(player2);
	      if (!TeleportHandler.checkArea(playerToMove)) {
	        player.getPacketSender().sendMessage("Player standing in protected area.");
	        return;
	      }
	      if (playerToMove != null) {
	        playerToMove.moveTo(GameSettings.DEFAULT_POSITION.copy());
	        playerToMove.getPacketSender().sendMessage("You've been teleported home by " + player.getUsername() + ".");
	        player.getPacketSender().sendConsoleMessage("Sucessfully moved " + playerToMove.getUsername() + " to home.");
	      }
	    }
	    if (command[0].equalsIgnoreCase("mute")) {
	      String player2 = Misc.formatText(wholeCommand.substring(5));
	      if (!PlayerSaving.playerExists(player2)) {
	        player.getPacketSender().sendConsoleMessage("Player " + player2 + " does not exist.");
	        return;
	      }
	      if (PlayerPunishment.muted(player2)) {
	        player.getPacketSender().sendConsoleMessage("Player " + player2 + " already has an active mute.");
	        return;
	      }
	      PlayerLogs.log(player.getUsername(), player.getUsername() + " just muted " + player2 + "!");
	      PlayerPunishment.mute(player2);
	      player.getPacketSender().sendConsoleMessage("Player " + player2 + " was successfully muted. Command logs written.");
	      Player plr = World.getPlayerByName(player2);
	      if (plr != null) {
	        plr.getPacketSender().sendMessage("You have been muted by " + player.getUsername() + ".");
	      }
	    }
	  }

	private static void moderatorCommands(Player player, String[] command, String wholeCommand) {
	    if (command[0].equalsIgnoreCase("unmute")) {
	      String player2 = wholeCommand.substring(7);
	      if (!PlayerSaving.playerExists(player2)) {
	        player.getPacketSender().sendConsoleMessage("Player " + player2 + " does not exist.");
	        return;
	      }
	      if (!PlayerPunishment.muted(player2)) {
	        player.getPacketSender().sendConsoleMessage("Player " + player2 + " is not muted!");
	        return;
	      }
	      PlayerLogs.log(player.getUsername(), player.getUsername() + " just unmuted " + player2 + "!");
	      PlayerPunishment.unmute(player2);
	      player.getPacketSender().sendConsoleMessage("Player " + player2 + " was successfully unmuted. Command logs written.");
	      Player plr = World.getPlayerByName(player2);
	      if (plr != null) {
	        plr.getPacketSender().sendMessage("You have been unmuted by " + player.getUsername() + ".");
	      }
	    }
	    if (command[0].equalsIgnoreCase("ipmute")) {
	      Player player2 = World.getPlayerByName(wholeCommand.substring(7));
	      if (player2 == null) {
	        player.getPacketSender().sendConsoleMessage("Could not find that player online.");
	        return;
	      }
	      if (PlayerPunishment.IPMuted(player2.getHostAddress())) {
	        player.getPacketSender().sendConsoleMessage("Player " + player2.getUsername() + "'s IP is already IPMuted. Command logs written.");
	        return;
	      }
	      String mutedIP = player2.getHostAddress();
	      PlayerPunishment.addMutedIP(mutedIP);
	      player.getPacketSender().sendConsoleMessage("Player " + player2.getUsername() + " was successfully IPMuted. Command logs written.");
	      player2.getPacketSender().sendMessage("You have been IPMuted by " + player.getUsername() + ".");
	      PlayerLogs.log(player.getUsername(), player.getUsername() + " just IPMuted " + player2.getUsername() + "!");
	    }
	    if (command[0].equalsIgnoreCase("ban")) {
	      String playerToBan = wholeCommand.substring(4);
	      if (!PlayerSaving.playerExists(playerToBan)) {
	        player.getPacketSender().sendConsoleMessage("Player " + playerToBan + " does not exist.");
	        return;
	      }
	      if (PlayerPunishment.banned(playerToBan)) {
	        player.getPacketSender().sendConsoleMessage("Player " + playerToBan + " already has an active ban.");
	        return;
	      }
	      PlayerLogs.log(player.getUsername(), player.getUsername() + " just banned " + playerToBan + "!");
	      PlayerPunishment.ban(playerToBan);
	      player.getPacketSender().sendConsoleMessage("Player " + playerToBan + " was successfully banned. Command logs written.");
	      Player toBan = World.getPlayerByName(playerToBan);
	      if (toBan != null) {
	        World.deregister(toBan);
	      }
	    }
	    if (command[0].equalsIgnoreCase("unban")) {
	      String playerToBan = wholeCommand.substring(6);
	      if (!PlayerSaving.playerExists(playerToBan)) {
	        player.getPacketSender().sendConsoleMessage("Player " + playerToBan + " does not exist.");
	        return;
	      }
	      if (!PlayerPunishment.banned(playerToBan)) {
	        player.getPacketSender().sendConsoleMessage("Player " + playerToBan + " is not banned!");
	        return;
	      }
	      PlayerLogs.log(player.getUsername(), player.getUsername() + " just unbanned " + playerToBan + "!");
	      PlayerPunishment.unban(playerToBan);
	      player.getPacketSender().sendConsoleMessage("Player " + playerToBan + " was successfully unbanned. Command logs written.");
	    }
	    if (command[0].equals("sql")) {
	      MySQLController.toggle();
	      if (player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.OWNER) {
	        player.getPacketSender().sendConsoleMessage("Sql toggled to status: " + GameSettings.MYSQL_ENABLED);
	      } else {
	        player.getPacketSender().sendMessage("Sql toggled to status: " + GameSettings.MYSQL_ENABLED + ".");
	      }
	    }
	    if (command[0].equalsIgnoreCase("cpuban")) {
	      Player player2 = World.getPlayerByName(wholeCommand.substring(7));
	      if ((player2 != null) && (!player2.getSerialNumber().equals("null"))) {
	        World.deregister(player2);
	        ConnectionHandler.banComputer(player2.getUsername(), player2.getSerialNumber());
	        PlayerPunishment.ban(player2.getUsername());
	        player.getPacketSender().sendConsoleMessage("CPU Banned player.");
	        PlayerLogs.log(player.getUsername(), player.getUsername() + " just CPUBanned " + player2.getUsername() + "!");
	      }
	      else {
	        player.getPacketSender().sendConsoleMessage("Could not CPU-ban that player.");
	      }
	    }
	    if (command[0].equalsIgnoreCase("toggleinvis")) {
	      player.setNpcTransformationId(player.getNpcTransformationId() > 0 ? -1 : 8254);
	      player.getUpdateFlag().flag(Flag.APPEARANCE);
	    }
	    if (command[0].equalsIgnoreCase("ipban")) {
	      Player player2 = World.getPlayerByName(wholeCommand.substring(6));
	      if (player2 == null) {
	        player.getPacketSender().sendConsoleMessage("Could not find that player online.");
	        return;
	      }
	      if (PlayerPunishment.IPBanned(player2.getHostAddress())) {
	        player.getPacketSender().sendConsoleMessage("Player " + player2.getUsername() + "'s IP is already banned. Command logs written.");
	        return;
	      }
	      String bannedIP = player2.getHostAddress();
	      PlayerPunishment.addBannedIP(bannedIP);
	      player.getPacketSender().sendConsoleMessage("Player " + player2.getUsername() + "'s IP was successfully banned. Command logs written.");
	      for (Player playersToBan : World.getPlayers()) {
	        if (playersToBan != null) {
	          if (playersToBan.getHostAddress() == bannedIP) {
	            PlayerLogs.log(player.getUsername(), player.getUsername() + " just IPBanned " + playersToBan.getUsername() + "!");
	            World.deregister(playersToBan);
	            if (player2.getUsername() != playersToBan.getUsername()) {
	              player.getPacketSender().sendConsoleMessage("Player " + playersToBan.getUsername() + " was successfully IPBanned. Command logs written.");
	            }
	          }
	        }
	      }
	    }
	    if (command[0].equalsIgnoreCase("unipmute")) {
	      player.getPacketSender().sendConsoleMessage("Unipmutes can only be handled manually.");
	    }
	    if (command[0].equalsIgnoreCase("teletome")) {
	      String playerToTele = wholeCommand.substring(9);
	      Player player2 = World.getPlayerByName(playerToTele);
	      if (player2 == null) {
	        player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
	        return;
	      }
	      boolean canTele = (TeleportHandler.checkReqs(player, player2.getPosition().copy())) && (player.getRegionInstance() == null) && (player2.getRegionInstance() == null);
	      if (canTele) {
	        TeleportHandler.teleportPlayer(player2, player.getPosition().copy(), TeleportType.NORMAL);
	        player.getPacketSender().sendConsoleMessage("Teleporting player to you: " + player2.getUsername());
	        player2.getPacketSender().sendMessage("You're being teleported to " + player.getUsername() + "...");
	      }
	      else {
	        player.getPacketSender().sendConsoleMessage("You can not teleport that player at the moment. Maybe you or they are in a minigame?");
	      }
	    }
	    if (command[0].equalsIgnoreCase("movetome")) {
	      String playerToTele = wholeCommand.substring(9);
	      Player player2 = World.getPlayerByName(playerToTele);
	      if (player2 == null)
	      {
	        player.getPacketSender().sendConsoleMessage("Cannot find that player..");
	        return;
	      }
	      if (!TeleportHandler.checkArea(player2)) {
	        player.getPacketSender().sendMessage("Player standing in protected area.");
	        return;
	      }
	      boolean canTele = (TeleportHandler.checkReqs(player, player2.getPosition().copy())) && (player.getRegionInstance() == null) && (player2.getRegionInstance() == null);
	      if (canTele) {
	        player.getPacketSender().sendConsoleMessage("Moving player: " + player2.getUsername());
	        player2.getPacketSender().sendMessage("You've been moved to " + player.getUsername());
	        player2.moveTo(player.getPosition().copy());
	      }
	      else {
	        player.getPacketSender().sendConsoleMessage("Failed to move player to your coords. Are you or them in a minigame?");
	      }
	    }
	    if (command[0].equalsIgnoreCase("kick")) {
	      String player2 = wholeCommand.substring(5);
	      Player playerToKick = World.getPlayerByName(player2);
	      if (playerToKick == null) {
	        player.getPacketSender().sendConsoleMessage("Player " + player2 + " couldn't be found on Sintennial.");
	        return;
	      }
	      if (playerToKick.getLocation() != Location.WILDERNESS) {
	        World.deregister(playerToKick);
	        player.getPacketSender().sendConsoleMessage("Kicked " + playerToKick.getUsername() + ".");
	        PlayerLogs.log(player.getUsername(), player.getUsername() + " just kicked " + playerToKick.getUsername() + "!");
	      }
	    }
	  }

	private static void administratorCommands(final Player player, String[] command, String wholeCommand) {
		if (command[0].equalsIgnoreCase("cpuban")) {
		      Player player2 = World.getPlayerByName(wholeCommand.substring(7));
		      if ((player2 != null) && (!player2.getSerialNumber().equals("null"))) {
		        World.deregister(player2);
		        ConnectionHandler.banComputer(player2.getUsername(), player2.getSerialNumber());
		        PlayerPunishment.ban(player2.getUsername());
		        player.getPacketSender().sendConsoleMessage("CPU Banned player.");
		        PlayerLogs.log(player.getUsername(), player.getUsername() + " just CPUBanned " + player2.getUsername() + "!");
		      }
		      else {
		        player.getPacketSender().sendConsoleMessage("Could not CPU-ban that player.");
		      }
		    }
		    if (command[0].equalsIgnoreCase("toggleinvis")) {
		      player.setNpcTransformationId(player.getNpcTransformationId() > 0 ? -1 : 8254);
		      player.getUpdateFlag().flag(Flag.APPEARANCE);
		    }
		if (command[0].equals("reset")) {
			for (Skill skill : Skill.values()) {
				int level = skill.equals(Skill.CONSTITUTION) ? 100 : skill.equals(Skill.PRAYER) ? 10 : 1;
				player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
			}
			player.getPacketSender().sendConsoleMessage("Your skill levels have now been reset.");
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		if (command[0].equals("master")) {
			for (Skill skill : Skill.values()) {
				int level = SkillManager.getMaxAchievingLevel(skill);
				player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level == 120 ? 120 : 99));
			}
			player.getPacketSender().sendConsoleMessage("You are now a master of all skills.");
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		if (command[0].equals("setlevel") && !player.getUsername().equalsIgnoreCase("Jack")) {
			int skillId = Integer.parseInt(command[1]);
			int level = Integer.parseInt(command[2]);
			if(level > 15000) {
				player.getPacketSender().sendConsoleMessage("You can only have a maxmium level of 15000.");
				return;
			}
			Skill skill = Skill.forId(skillId);
			player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level));
			player.getPacketSender().sendConsoleMessage("You have set your " + skill.getName() + " level to " + level);
		}
		if (command[0].equals("item")) {
			int id = Integer.parseInt(command[1]);		
			int amount = (command.length == 2 ? 1 : Integer.parseInt(command[2].trim().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000")));
			if(amount > Integer.MAX_VALUE) {
				amount = Integer.MAX_VALUE;
			}
			Item item = new Item(id, amount);
			player.getInventory().add(item, true);

			player.getPacketSender().sendItemOnInterface(47052, 11694, 1);
		}
		if (command[0].contains("pure")) {
			int[][] data = 
					new int[][]{
					{Equipment.HEAD_SLOT, 1153},
					{Equipment.CAPE_SLOT, 10499},
					{Equipment.AMULET_SLOT, 1725},
					{Equipment.WEAPON_SLOT, 4587},
					{Equipment.BODY_SLOT, 1129},
					{Equipment.SHIELD_SLOT, 1540},
					{Equipment.LEG_SLOT, 2497},
					{Equipment.HANDS_SLOT, 7459},
					{Equipment.FEET_SLOT, 3105},
					{Equipment.RING_SLOT, 2550},
					{Equipment.AMMUNITION_SLOT, 9244}
			};
			for (int i = 0; i < data.length; i++) {
				int slot = data[i][0], id = data[i][1];
				player.getEquipment().setItem(slot, new Item(id, id == 9244 ? 500 : 1));
			}
			BonusManager.update(player);
			WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
			WeaponAnimations.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
			player.getEquipment().refreshItems();
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.getInventory().resetItems();
			player.getInventory().add(1216, 1000).add(9186, 1000).add(862, 1000).add(892, 10000).add(4154, 5000).add(2437, 1000).add(2441, 1000).add(2445, 1000).add(386, 1000).add(2435, 1000);
			player.getSkillManager().newSkillManager();
			player.getSkillManager().setMaxLevel(Skill.ATTACK, 60).setMaxLevel(Skill.STRENGTH, 85).setMaxLevel(Skill.RANGED, 85).setMaxLevel(Skill.PRAYER, 520).setMaxLevel(Skill.MAGIC, 70).setMaxLevel(Skill.CONSTITUTION, 850);
			for(Skill skill : Skill.values()) {
				player.getSkillManager().setCurrentLevel(skill, player.getSkillManager().getMaxLevel(skill)).setExperience(skill, SkillManager.getExperienceForLevel(player.getSkillManager().getMaxLevel(skill)));
			}
		}
		if(command[0].equals("pray")) {
			player.getSkillManager().setCurrentLevel(Skill.PRAYER, 15000);
		}
		if (command[0].equals("tele")) {
			int x = Integer.valueOf(command[1]), y = Integer.valueOf(command[2]);
			int z = player.getPosition().getZ();
			if (command.length > 3)
				z = Integer.valueOf(command[3]);
			Position position = new Position(x, y, z);
			player.moveTo(position);
			player.getPacketSender().sendConsoleMessage("Teleporting to " + position.toString());
		}
		if (command[0].equals("bank")) {
			player.getBank(player.getCurrentBankTab()).open();
		}
		if(command[0].equals("spec")) {
			player.setSpecialPercentage(100);
			CombatSpecial.updateBar(player);
		}
		if(command[0].equals("runes")) {
			for(Item t : ShopManager.getShops().get(0).getItems()) {
				if(t != null) {
					player.getInventory().add(new Item(t.getId(), 200000));
				}
			}
		}
		if (command[0].contains("gear")) {
			int[][] data = wholeCommand.contains("pk") ? 
					new int[][]{
				{Equipment.HEAD_SLOT, 1050},
				{Equipment.CAPE_SLOT, 12170},
				{Equipment.AMULET_SLOT, 15126},
				{Equipment.WEAPON_SLOT, 15444},
				{Equipment.BODY_SLOT, 14012},
				{Equipment.SHIELD_SLOT, 13740},
				{Equipment.LEG_SLOT, 14013},
				{Equipment.HANDS_SLOT, 7462},
				{Equipment.FEET_SLOT, 11732},
				{Equipment.RING_SLOT, 15220}
			} : wholeCommand.contains("range") ? 
					new int[][]{
				{Equipment.HEAD_SLOT, 3749},
				{Equipment.CAPE_SLOT, 10499},
				{Equipment.AMULET_SLOT, 15126},
				{Equipment.WEAPON_SLOT, 18357},
				{Equipment.BODY_SLOT, 2503},
				{Equipment.SHIELD_SLOT, 13740},
				{Equipment.LEG_SLOT, 2497},
				{Equipment.HANDS_SLOT, 7462},
				{Equipment.FEET_SLOT, 11732},
				{Equipment.RING_SLOT, 15019},
				{Equipment.AMMUNITION_SLOT, 9244},
			}:
				new int[][]{
						{Equipment.HEAD_SLOT, 1163},
						{Equipment.CAPE_SLOT, 19111},
						{Equipment.AMULET_SLOT, 6585},
						{Equipment.WEAPON_SLOT, 4151},
						{Equipment.BODY_SLOT, 1127},
						{Equipment.SHIELD_SLOT, 13262},
						{Equipment.LEG_SLOT, 1079},
						{Equipment.HANDS_SLOT, 7462},
						{Equipment.FEET_SLOT, 11732},
						{Equipment.RING_SLOT, 2550}
				};
				for (int i = 0; i < data.length; i++) {
					int slot = data[i][0], id = data[i][1];
					player.getEquipment().setItem(slot, new Item(id, id == 9244 ? 500 : 1));
				}
				BonusManager.update(player);
				WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
				WeaponAnimations.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
				player.getEquipment().refreshItems();
				player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
	}
	private static void ownerCommands(Player player, String[] command, String wholeCommand)
	{
     	if (wholeCommand.equals("heal")) {
     		player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 990);
     	}
     	if (wholeCommand.equals("run")) {
     		player.setRunEnergy(100);
     	}
	    if (wholeCommand.equals("afk")) {
	      World.sendMessage("<img=11> <col=FF0000><shad=0>" + player.getUsername() + ": I am now away, please don't message me; I won't reply.");
	    }
	    if ((wholeCommand.equals("lottery")) && (player.getUsername().equals("Juice"))) {
	      Lottery.restartLottery();
	    }
	    String rss;
	    if (command[0].equals("giveitem")) {
	      int item = Integer.parseInt(command[1]);
	      int amount = Integer.parseInt(command[2]);
	      rss = command[3];
	      if (command.length > 4) {
	        rss = rss + " " + command[4];
	      }
	      if (command.length > 5) {
	        rss = rss + " " + command[5];
	      }
	      Player target = World.getPlayerByName(rss);
	      if (target == null)
	      {
	        player.getPacketSender().sendMessage("Player must be online to give them stuff!");
	      }
	      else
	      {
	        player.getPacketSender().sendMessage("Item sent successfully.");
	        target.getInventory().add(item, amount);
	      }
	    }
	    if (command[0].equals("update")) {
			int time = Integer.parseInt(command[1]);
			if(time > 0) {
				GameServer.setUpdating(true);
				for (Player players : World.getPlayers()) {
					if (players == null)
						continue;
					players.getPacketSender().sendSystemUpdate(time);
				}
				TaskManager.submit(new Task(time) {
					@Override
					protected void execute() {
						for (Player player : World.getPlayers()) {
							if (player != null) {
								World.deregister(player);
							}
						}
						WellOfGoodwill.save();
						GrandExchangeOffers.save();
						ClanChatManager.save();
						GameServer.getLogger().info("Update task finished!");
						stop();
					}
				});
			}
		}
	    if (command[0].contains("host")) {
	      String plr = wholeCommand.substring(command[0].length() + 1);
	      Player playr2 = World.getPlayerByName(plr);
	      if (playr2 != null) {
	        player.getPacketSender().sendConsoleMessage(playr2.getUsername() + " host IP: " + playr2.getHostAddress() + ", serial number: " + playr2.getSerialNumber());
	      } else {
	        player.getPacketSender().sendConsoleMessage("Could not find player: " + plr);
	      }
	    }
	  }
	  
	  private static void developerCommands(Player player, String[] command, String wholeCommand)
	  {
		  if(command[0].equalsIgnoreCase("enabledxp")) {
			  int multiplier = command.length >= 2 ? Integer.parseInt(command[1]) : 2;
			  GameSettings.BONUS_MULTIPLIER = multiplier;
			  player.getPacketSender().sendMessage("You have enabled double xp with a "+multiplier+"x multiplier.");
			  return;
		  }
		  if(command[0].equalsIgnoreCase("disabledxp")) {
			  GameSettings.BONUS_MULTIPLIER = 1;
			  player.getPacketSender().sendMessage("You have disabled double xp.");
			  return;
		  }
	    if (command[0].equals("sendstring"))
	    {
	      int child = Integer.parseInt(command[1]);
	      String string = command[2];
	      player.getPacketSender().sendString(child, string);
	    }
	    if (command[0].equals("tasks")) {
	      player.getPacketSender().sendConsoleMessage("Found " + TaskManager.getTaskAmount() + " tasks.");
	    }
	    if (command[0].equals("reloadcpubans")) {
	      ConnectionHandler.reloadUUIDBans();
	      player.getPacketSender().sendConsoleMessage("UUID bans reloaded!");
	    }
	    if (command[0].equals("reloadipbans")) {
	      PlayerPunishment.reloadIPBans();
	      player.getPacketSender().sendConsoleMessage("IP bans reloaded!");
	    }
	    if (command[0].equals("reloadipmutes")) {
	      PlayerPunishment.reloadIPMutes();
	      player.getPacketSender().sendConsoleMessage("IP mutes reloaded!");
	    }
	    if (command[0].equalsIgnoreCase("cpuban2")) {
	      String serial = wholeCommand.substring(8);
	      ConnectionHandler.banComputer("cpuban2", serial);
	      player.getPacketSender().sendConsoleMessage(serial + " cpu was successfully banned. Command logs written.");
	    }
	    if (command[0].equalsIgnoreCase("ipban2")) {
	      String ip = wholeCommand.substring(7);
	      PlayerPunishment.addBannedIP(ip);
	      player.getPacketSender().sendConsoleMessage(ip + " IP was successfully banned. Command logs written.");
	    }
	    if (command[0].equals("memory")) {
	      long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	      player.getPacketSender().sendConsoleMessage("Heap usage: " + Misc.insertCommasToNumber(new StringBuilder().append(used).toString()) + " bytes!");
	    }
	    if (command[0].equals("tree")) {
		      EvilTree.despawn(true);
		      player.getPacketSender().sendConsoleMessage("tree method called.");
		    }
	    if (command[0].equals("star")) {
	      ShootingStar.despawn(true);
	      player.getPacketSender().sendConsoleMessage("star method called.");
	    }
	    if (command[0].equals("save")) {
	      player.save();
	    }
	    if (command[0].equals("saveall")) {
	      World.savePlayers();
	    }
	    if (command[0].equals("v10")) {
	      World.sendMessage("<img=11> <col=008FB2>Another 10 voters have been rewarded! Vote now using the ::vote command!");
	    }
	    if (command[0].equalsIgnoreCase("frame")) {
	      int frame = Integer.parseInt(command[1]);
	      String text = command[2];
	      player.getPacketSender().sendString(frame, text);
	    }
	    if (command[0].equals("pos")) {
	    	player.getPacketSender().sendConsoleMessage("Current location: "+player.getLocation().toString()+", coords: "+player.getPosition());
	    }
	    if (command[0].equals("npc")) {
	      int id = Integer.parseInt(command[1]);
	      NPC npc = new NPC(id, new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()));
	      World.register(npc);
	      npc.setConstitution(20000);
	      player.getPacketSender().sendEntityHint(npc);
	    }
	    if (command[0].equals("skull")) {
	      if (player.getSkullTimer() > 0) {
	        player.setSkullTimer(0);
	        player.setSkullIcon(0);
	        player.getUpdateFlag().flag(Flag.APPEARANCE);
	      }
	      else {
	        CombatFactory.skullPlayer(player);
	      }
	    }
	    if (command[0].equals("fillinv")) {
	      for (int i = 0; i < 28; i++) {
	        int it = Misc.getRandom(10000);
	        player.getInventory().add(it, 1);
	      }
	    }
	    if (command[0].equals("playnpc")) {
	      player.setNpcTransformationId(Integer.parseInt(command[1]));
	      player.getUpdateFlag().flag(Flag.APPEARANCE);
	    }
	    else if (command[0].equals("playobject")) {
	      player.getPacketSender().sendObjectAnimation(new GameObject(2283, player.getPosition().copy()), new Animation(751));
	      player.getUpdateFlag().flag(Flag.APPEARANCE);
	    }
	    if (command[0].equals("interface")) {
	      int id = Integer.parseInt(command[1]);
	      player.getPacketSender().sendInterface(id);
	    }
	    if (command[0].equals("walkableinterface")) {
	      int id = Integer.parseInt(command[1]);
	      player.getPacketSender().sendWalkableInterface(id);
	    }
	    if (command[0].equals("anim")) {
	      int id = Integer.parseInt(command[1]);
	      player.performAnimation(new Animation(id));
	      player.getPacketSender().sendConsoleMessage("Sending animation: " + id);
	    }
	    if (command[0].equals("dung")) {
	      player.DungPrestige += 1.0;
	    }
	    if (command[0].equals("gfx")) {
	      int id = Integer.parseInt(command[1]);
	      player.performGraphic(new Graphic(id));
	      player.getPacketSender().sendConsoleMessage("Sending graphic: " + id);
	    }
	    if (command[0].equals("object")) {
	      int id = Integer.parseInt(command[1]);
	      player.getPacketSender().sendObject(new GameObject(id, player.getPosition(), 10, 3));
	      player.getPacketSender().sendConsoleMessage("Sending object: " + id);
	    }
	    if (command[0].equals("config")) {
	      int id = Integer.parseInt(command[1]);
	      int state = Integer.parseInt(command[2]);
	      player.getPacketSender().sendConfig(id, state).sendConsoleMessage("Sent config.");
	    }
	    if (command[0].equals("checkbank")) {
	      Player plr = World.getPlayerByName(wholeCommand.substring(10));
	      if (plr != null) {
	        player.getPacketSender().sendConsoleMessage("Loading bank..");
	        Bank[] arrayOfBank;
	        int j = (arrayOfBank = player.getBanks()).length;
	        for (int i = 0; i < j; i++) {
	          Bank b = arrayOfBank[i];
	          if (b != null) {
	            b.resetItems();
	          }
	        }
	        for (int i = 0; i < plr.getBanks().length; i++) {
	          Item[] arrayOfItem;
	          int k = (arrayOfItem = plr.getBank(i).getItems()).length;
	          for (j = 0; j < k; j++) {
	            Item it = arrayOfItem[j];
	            if (it != null) {
	              player.getBank(i).add(it, false);
	            }
	          }
	        }
	        player.getBank(0).open();
	      }
	      else {
	        player.getPacketSender().sendConsoleMessage("Player is offline!");
	      }
	    }
	    if (command[0].equals("checkinv")) {
	      Player player2 = World.getPlayerByName(wholeCommand.substring(9));
	      if (player2 == null)
	      {
	        player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
	        return;
	      }
	      player.getInventory().setItems(player2.getInventory().getCopiedItems()).refreshItems();
	    }
	    if (command[0].equals("checkequip")) {
	      Player player2 = World.getPlayerByName(wholeCommand.substring(11));
	      if (player2 == null) {
	        player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
	        return;
	      }
	      player.getEquipment().setItems(player2.getEquipment().getCopiedItems()).refreshItems();
	      WeaponInterfaces.assign(player, player.getEquipment().get(3));
	      WeaponAnimations.assign(player, player.getEquipment().get(3));
	      BonusManager.update(player);
	      player.getUpdateFlag().flag(Flag.APPEARANCE);
	    }
	  }
	}
