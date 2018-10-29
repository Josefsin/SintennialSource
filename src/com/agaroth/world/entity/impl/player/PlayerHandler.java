package com.agaroth.world.entity.impl.player;

import java.util.ArrayList;
//import java.util.concurrent.TimeUnit;

//import com.agaroth.CoresManager;
import com.agaroth.GameServer;
import com.agaroth.GameSettings;
import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.engine.task.impl.BonusExperienceTask;
import com.agaroth.engine.task.impl.CombatSkullEffect;
import com.agaroth.engine.task.impl.FireImmunityTask;
import com.agaroth.engine.task.impl.OverloadPotionTask;
import com.agaroth.engine.task.impl.PlayerSkillsTask;
import com.agaroth.engine.task.impl.PlayerSpecialAmountTask;
import com.agaroth.engine.task.impl.PrayerRenewalPotionTask;
import com.agaroth.engine.task.impl.StaffOfLightSpecialAttackTask;
import com.agaroth.model.Flag;
//import com.agaroth.model.GameMode;
import com.agaroth.model.Locations;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Skill;
import com.agaroth.model.container.impl.Bank;
import com.agaroth.model.container.impl.Equipment;
import com.agaroth.model.definitions.WeaponAnimations;
import com.agaroth.model.definitions.WeaponInterfaces;
import com.agaroth.net.PlayerSession;
import com.agaroth.net.SessionState;
import com.agaroth.net.security.ConnectionHandler;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.content.Achievements;
import com.agaroth.world.content.BonusManager;
import com.agaroth.world.content.Lottery;
import com.agaroth.world.content.LoyaltyProgramme;
import com.agaroth.world.content.MemberScrolls;
import com.agaroth.world.content.PlayerLogs;
import com.agaroth.world.content.PlayerPanel;
import com.agaroth.world.content.PlayersOnlineInterface;
import com.agaroth.world.content.WellOfGoodwill;
import com.agaroth.world.content.LoyaltyProgramme.LoyaltyTitles;
import com.agaroth.world.content.clan.ClanChatManager;
import com.agaroth.world.content.combat.effect.CombatPoisonEffect;
import com.agaroth.world.content.combat.effect.CombatTeleblockEffect;
import com.agaroth.world.content.combat.magic.Autocasting;
import com.agaroth.world.content.combat.prayer.CurseHandler;
import com.agaroth.world.content.combat.prayer.PrayerHandler;
import com.agaroth.world.content.combat.pvp.BountyHunter;
import com.agaroth.world.content.combat.range.DwarfMultiCannon;
import com.agaroth.world.content.combat.weapon.CombatSpecial;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.content.grandexchange.GrandExchange;
import com.agaroth.world.content.minigames.impl.Barrows;
import com.agaroth.world.content.skill.impl.hunter.Hunter;
import com.agaroth.world.content.skill.impl.slayer.Slayer;
//import com.agaroth.world.content.minigames.impl.TriviaBot;
//import com.agaroth.world.content.TriviaBot2;


public class PlayerHandler {
	private static void serverMessage(final Player player) {
	    TaskManager.submit(new Task(5000, player, false) {
	      public void execute() {
	        int r3 = 0;
	        r3 = Misc.getRandom(9);
	        if (r3 == 0) {
	          player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ Remember to vote for Sintennial daily! Your vote brings more players!");
	        } else if (r3 == 1) {
	          player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ Post all your suggestions using command :: suggest!");
	        } else if (r3 == 2) {
	          player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ Sintennial staff are here to help!");
	        } else if (r3 == 3) {
	          player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ You may vote every 12 hours!");
	        } else if (r3 == 4) {
	          player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ You can find most recent updates using command :: recent!");
	        } else if (r3 == 5) {
		      player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ You can find Sintennial highscores by using command ::hiscores!");
		    } else if (r3 == 6) {
		          player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ Sintennial's Drop Table is located in the home bank!");
		    } else if (r3 == 7) {
		          player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ Contact Juice to donate or simply use the donate command!");
		    } else if (r3 == 8) {
		          player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ Sintennial is developed and updated every day!");
		    } else if (r3 == 9) {
		          player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ Every first week of the month, all donator items are discounted!");
		    }
	      }
	    });
	 }
	
/*	private static void triviaMessage(final Player player) {
		TaskManager.submit(new Task(1000, player, false) {
			public void execute() {
				int tq = 0;
				tq = Misc.getRandom(71);
				if (tq == 0) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ Who is the Owner of Sintennial?"); //Juice
				} else if (tq == 1) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What color is Uber Donator?"); //blue
				} else if (tq == 2) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ The Barrows Brother, Verac, hits through which prayer?"); //protect from melee
				} else if (tq == 3) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ How many Developers contribute to Sintennial?"); //2, two
				} else if (tq == 4) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ Which MOB has four enourmous tentacles?"); //kraken
				} else if (tq == 5) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ How many vote points is a Crystal Key?"); //5, five
				} else if (tq == 6) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ I am a rock that turns into a crab, what am I?"); //rock crab
				} else if (tq == 7) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What is the max level possible in any Sintennial skill?"); //99
				} else if (tq == 8) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ Which NPC lets you set a Bank-Pin?"); //town crier
				} else if (tq == 9) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ How many Onyx Bolts(e) does Corporeal Beast drop?"); //1205
				} else if (tq == 10) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What is the name of the NPC that drops wyvern bones?"); //skeletal wyverns
				} else if (tq == 11) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What is the maximum combat level you can achieve on Sintennial?"); //138
				} else if (tq == 12) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ Which of the Barrows Brothers fight with Magic?"); //ahrim
				} else if (tq == 13) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What level is required to fish Sharks?"); //76
				} else if (tq == 14) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What Smithing level is required to make a DFS?"); //90
				} else if (tq == 15) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ How much special attack does the Magic Shortbow require?"); //55
				} else if (tq == 16) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ The Berseker Ring is dropped by which NPC?"); //dagannoth rex
				} else if (tq == 17) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ Solve this anagram: odsanb"); //bandos
				} else if (tq == 18) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ Where are Spiritual Mages found?"); //godwars, godwars dungeon
				} else if (tq == 19) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What is the required level for Smite?"); //52
				} else if (tq == 20) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What Attack level is required to wield a GodSword?"); //75
				} else if (tq == 21) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ How many Barrows Brothers are there?"); //6
				} else if (tq == 22) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ Type this backwards: voteforsintennialeveryday"); //yadyrevelainnetnisrofetov
				} else if (tq == 23) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ Which NPC helps Ironman accounts?"); //gaius
				} else if (tq == 24) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ How many total Achievements are there?"); //89
				} else if (tq == 25) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ How many prayers are in the normal prayerbook?"); //28
				} else if (tq == 26) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ Where is home located?"); //edgeville
				} else if (tq == 27) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What is the name of the NPC you can get Skillcapes from?"); //wise old man
				} else if (tq == 28) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What color PartyHat does the Wise Old Man wear?"); //blue
				} else if (tq == 29) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What skill involves burning logs?"); //firemaking
				} else if (tq == 30) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What minigame can you earn Void Knight Armour from?"); //pest control
				} else if (tq == 31) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ How many Thieving Stalls are there at home?"); //5 
				} else if (tq == 32) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What is the best crossbow in the game?"); //armadyl crossbow
				} else if (tq == 33) {
					player.getPacketSender().sendMessage("<img=11><col=6600CC>@red@ What is the best staff in the game?"); //staff of gods
				}
			}
		});
	} */
	
	public static boolean questionAnswered;
	public static boolean questionActive;
	public String correctAnswer;
	
	public static ArrayList<Player> wrongAnswers = new ArrayList<Player>();
	
	public static PlayerHandler instance;
	public static PlayerHandler getInstance() {
		if (instance == null)
			instance = new PlayerHandler();
		return instance;
	}
	
	
		private static void triviaMessage(final Player player) {
		    TaskManager.submit(new Task(6000, player, false) {
		      public void execute() {
		    	  
				if (World.getPlayers().size() >= 1 && questionActive == false) {
					wrongAnswers.clear();
					sendTrivia(generateQuestion());
					questionActive = true;
					questionAnswered = false;
				}
				else if((System.currentTimeMillis() - player.getLastCorrectTrivia() > Misc.HALF_A_DAY_IN_MILLIS / 12)) { //3600000
					wrongAnswers.clear();
					sendTrivia(generateQuestion());
					questionActive = true;
					questionAnswered = false;
					} 
				}
		    });
		         
		 }
	
	
	
	public static void sendTrivia(String message) {
		for (Player p : World.getPlayers()) {
			if (p == null || p.hasDisabledTrivia()) {
				continue;
			}
			p.getPacketSender().sendMessage("<img=11> <col=0066FF><shad=222222>[Trivia] "+ message +"");
		}
	}
	
/*	public void sendMessage(Player player, String message) {
		player.getPacketSender().sendMessage("<img=11> <col=0066FF><shad=222222>[Trivia Bot] " + message);
	} */
	
	public boolean verify(Player player, String answer) {
		if (player.getRights()== PlayerRights.ADMINISTRATOR) {
			player.getPacketSender().sendMessage("You are not allowed to answer trivia questions.");
			return false;
		}
		
		if (World.getPlayers().size() < 2) {
			player.getPacketSender().sendMessage("<col=FF0000>There must be atleast 2 players online before being able to answer trivia.");
			return false;
		}
		
		if (questionAnswered) {
			player.getPacketSender().sendMessage("<img=11> <col=0066FF><shad=222222>[Trivia] The question has already been answered. Better luck next time!");
			return false;
		}
		
		if (correctAnswer == null) {
			player.getPacketSender().sendMessage("<img=11> <col=0066FF><shad=222222>[Trivia] There is not current a question needing to be answered.");
			return false;
		}
		
		if (wrongAnswers.contains(player) && player.getRights() != PlayerRights.OWNER) {
			player.getPacketSender().sendMessage("<img=11> <col=0066FF><shad=222222>[Trivia] You've already guess this question wrong. Better luck next time :)");
			return false;
		}
		
		if (answer.equalsIgnoreCase(correctAnswer)) {
			if (!player.getInventory().isFull()) {
				int reward = Misc.getRandom(10000000);
				player.getInventory().add(995, reward);
				player.getInventory().add(6199, 1);
				questionAnswered = true;
				player.setLastAnswer(System.currentTimeMillis());
				questionActive = false;
				sendTrivia("<col=0066ff>"+player.getUsername()+"</col><col=006FFF> has answered correctly! Well done!");
				player.getPacketSender().sendMessage("@red@ You've won "+Misc.format(reward)+" coins & a Mystery Box! Good Job!");
			} else if (player.getInventory().capacity() < 2) {
				player.getPacketSender().sendMessage("You don't have enough room in your inventory!");
			}
			return true;
		}
		wrongAnswers.add(player);
		player.getPacketSender().sendMessage("<img=11> <col=0066FF><shad=222222>[Trivia] Sorry, you've answered the trivia incorrectly!");
		return false;
	}
	
	public static void setAnswer(String answer) {
		if (answer == null)
			return;
		getInstance().correctAnswer = answer;
	}
	
	private static String[][] questions = { 
		{ "juice", "Who is the Owner of Sintennial?" },
		{ "gold", "What color is Uber Donator?" },
		{ "protect from melee", "The Barrows Brother, Verac, hits through which prayer?" },
		{ "2", "How many Developers contribute to Sintennial?" },
		{ "kraken", "Which MOB has four enourmous tentacles?" },
		{ "5", "How many vote points is a Crystal Key?" }, //5, five
		{ "99", "What is the max level possible in any Sintennial skill?" },
		{ "town crier", "Which NPC lets you set a Bank-Pin?" },
		{ "1205", "How many Onyx Bolts(e) does Corporeal Beast drop?" },
		{ "skeletal wyvern", "What is the name of the NPC that drops wyvern bones?" },
		{ "138", "What is the maximum combat level you can achieve on Sintennial?" },
		{ "ahrim", "Which of the Barrows Brothers fight with Magic?" },
		{ "76", "What level is required to fish Sharks?" },
		{ "90", "What Smithing level is required to make a DFS?" },
		{ "55", "How much special attack does the Magic Shortbow require?" },
		{ "dagannoth rex", "The Berseker Ring is dropped by which NPC?" },
		{ "bandos", "Solve this anagram: odsanb" },
		{ "godwars", "Where are Spiritual Mages found?" }, //godwars dungeon
		{ "52", "What is the required level for Smite?" },
		{ "75", "What Attack level is required to wield a GodSword?" },
		{ "6", "How many Barrows Brothers are there?" }, //six
		{ "yadyrevelainnetnisrofetov", "Type this backwards: voteforsintennialeveryday" },
		{ "gaius", "Which NPC helps Ironman accounts?" },
		{ "89", "How many total Achievements are there?" },
		{ "28", "How many prayers are in the normal prayerbook?" },
		{ "edgeville", "Where is home located?" },
		{ "wise old man", "What is the name of the NPC you can get Skillcapes from?" },
		{ "blue", "What color PartyHat does the Wise Old Man wear?" },
		{ "firemaking", "What skill involves burning logs?" },
		{ "pest control", "What minigame can you earn Void Knight Armour from?" },
		{ "5", "How many Thieving Stalls are there at home?" }, //five
		{ "armadyl crossbow", "What is the best crossbow in the game?" },
		{ "staff of gods", "What is the best staff in the game?" },

		
	};

	
	public static String generateQuestion() {
		int random = Misc.getRandom(questions.length-1); //.exclusiveRandom(0, questions.length - 1);
		PlayerHandler.setAnswer(questions[random][0]);
		return questions[random][1];
	}
	


	public static void handleLogin(Player player) {
		System.out.println("[World] Registering player - [username, host] : [" + player.getUsername() + ", " + player.getHostAddress() + "]");
		ConnectionHandler.add(player.getHostAddress());
		World.getPlayers().add(player);
		World.updatePlayersOnline();
		PlayersOnlineInterface.add(player);
		player.getSession().setState(SessionState.LOGGED_IN);
		player.getPacketSender().sendMapRegion().sendDetails();
		player.getRecordedLogin().reset();
		player.getPacketSender().sendTabs();
		for(int i = 0; i < player.getBanks().length; i++) {
			if(player.getBank(i) == null) {
				player.setBank(i, new Bank(player));
			}
		}
		player.getInventory().refreshItems();
		player.getEquipment().refreshItems();
		WeaponAnimations.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
		WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
		CombatSpecial.updateBar(player);
		BonusManager.update(player);
		player.getSummoning().login();
		player.getFarming().load();
		Slayer.checkDuoSlayer(player, true);
		for (Skill skill : Skill.values()) {
			player.getSkillManager().updateSkill(skill);
		}
		player.getRelations().setPrivateMessageId(1).onLogin(player).updateLists(true);
		player.getPacketSender().sendConfig(172, player.isAutoRetaliate() ? 1 : 0)
		.sendTotalXp(player.getSkillManager().getTotalGainedExp())
		.sendConfig(player.getFightType().getParentId(), player.getFightType().getChildId())
		.sendRunStatus()
		.sendRunEnergy(player.getRunEnergy())
		.sendString(8135, ""+player.getMoneyInPouch())
		.sendInteractionOption("Follow", 3, false)
		.sendInteractionOption("Trade With", 4, false)
		.sendInterfaceRemoval().sendString(39161, "@or2@Server time: @or2@[ @yel@"+Misc.getCurrentServerTime()+"@or2@ ]");
		Autocasting.onLogin(player);
		PrayerHandler.deactivateAll(player);
		CurseHandler.deactivateAll(player);
		BonusManager.sendCurseBonuses(player);
		Achievements.updateInterface(player);
		Barrows.updateInterface(player);
		serverMessage(player);
		//TriviaBot.startup(player);
	    triviaMessage(player);
		TaskManager.submit(new PlayerSkillsTask(player));
		if (player.isPoisoned()) {
			TaskManager.submit(new CombatPoisonEffect(player));
		}
		if(player.getPrayerRenewalPotionTimer() > 0) {
			TaskManager.submit(new PrayerRenewalPotionTask(player));
		}
		if(player.getOverloadPotionTimer() > 0) {
			TaskManager.submit(new OverloadPotionTask(player));
		}
		if (player.getTeleblockTimer() > 0) {
			TaskManager.submit(new CombatTeleblockEffect(player));
		}
		if (player.getSkullTimer() > 0) {
			player.setSkullIcon(1);
			TaskManager.submit(new CombatSkullEffect(player));
		}
		if(player.getFireImmunity() > 0) {
			FireImmunityTask.makeImmune(player, player.getFireImmunity(), player.getFireDamageModifier());
		}
		if(player.getSpecialPercentage() < 100) {
			TaskManager.submit(new PlayerSpecialAmountTask(player));
		}
		if(player.hasStaffOfLightEffect()) {
			TaskManager.submit(new StaffOfLightSpecialAttackTask(player));
		}
		if(player.getMinutesBonusExp() >= 0) {
			TaskManager.submit(new BonusExperienceTask(player));
		}
		player.getUpdateFlag().flag(Flag.APPEARANCE);
		Lottery.onLogin(player);
		Locations.login(player);
		if(player.experienceLocked()) {
			player.getPacketSender().sendMessage("@red@Warning: your experience is currently locked.");
		}
		if(GameSettings.BONUS_MULTIPLIER > 1) {
			player.getPacketSender().sendMessage("@red@Double XP is enabled with a "+GameSettings.BONUS_MULTIPLIER+"x multiplier!");
		}
		player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ Welcome to @red@Sintennial, @dre@" + player.getUsername() + ".");
		player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ Recent: @red@Mr. Death is awaiting travelers...talk to the Draynor Spirit at home..");
		player.getPacketSender().sendMessage("<img=11><col=6600CC>@blu@ News: Shift dropping has been added!");

		MemberScrolls.checkForRankUpdate(player);
	    if (player.experienceLocked()) {
	      player.getPacketSender().sendMessage("@red@Warning: your experience is currently locked.");
	    }
	    ClanChatManager.handleLogin(player);
	    if (WellOfGoodwill.isActive()) {
	      player.getPacketSender().sendMessage("<img=11> <col=008FB2>The Well of Goodwill is granting 30% bonus experience for another " + WellOfGoodwill.getMinutesRemaining() + " minutes.");
	    }
		PlayerPanel.refreshPanel(player);
		if(player.newPlayer()) {
			 World.sendMessage("<img=11><col=6600CC> New player, " + player.getUsername() + " has just logged in, welcome to Sintennial.");
			player.setPlayerLocked(true).setDialogueActionId(45);
			DialogueManager.start(player, 81);
		}

		player.getPacketSender().updateSpecialAttackOrb().sendIronmanMode(player.getGameMode().ordinal());

		if(player.getRights() == PlayerRights.SUPPORT || player.getRights() == PlayerRights.MODERATOR || player.getRights() == PlayerRights.ADMINISTRATOR || player.getRights() == PlayerRights.OWNER || player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.GLOBAL_MODERATOR) 
			World.sendMessage("<img=11><col=6600CC> "+Misc.formatText(player.getRights().toString().toLowerCase())+" "+player.getUsername()+" has just logged in, feel free to message them for support.");
		LoyaltyProgramme.unlock(player, LoyaltyTitles.IMMORTAL);
		LoyaltyProgramme.unlock(player, LoyaltyTitles.GENOCIDAL);
		LoyaltyProgramme.unlock(player, LoyaltyTitles.SLAUGHTERER);
		LoyaltyProgramme.unlock(player, LoyaltyTitles.KILLER);
		GrandExchange.onLogin(player);
		
		if(player.getPointsHandler().getAchievementPoints() == 0) {
			Achievements.setPoints(player);
		}
		
		PlayerLogs.log(player.getUsername(), "Login from host "+player.getHostAddress()+", serial number: "+player.getSerialNumber());
	}

	public static boolean handleLogout(Player player) {
		try {

			PlayerSession session = player.getSession();
			
			if(session.getChannel().isOpen()) {
				session.getChannel().close();
			}

			if(!player.isRegistered()) {
				return true;
			}

			boolean exception = GameServer.isUpdating() || World.getLogoutQueue().contains(player) && player.getLogoutTimer().elapsed(90000);
			if(player.logout() || exception) {
				System.out.println("[World] Deregistering player - [username, host] : [" + player.getUsername() + ", " + player.getHostAddress() + "]");
				player.getSession().setState(SessionState.LOGGING_OUT);
				ConnectionHandler.remove(player.getHostAddress());
				player.setTotalPlayTime(player.getTotalPlayTime() + player.getRecordedLogin().elapsed());
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getCannon() != null) {
					DwarfMultiCannon.pickupCannon(player, player.getCannon(), true);
				}
				if(exception && player.getResetPosition() != null) {
					player.moveTo(player.getResetPosition());
					player.setResetPosition(null);
				}
				if(player.getRegionInstance() != null) {
					player.getRegionInstance().destruct();
				}
				Hunter.handleLogout(player);
				Locations.logout(player);
				player.getSummoning().unsummon(false, false);
				player.getFarming().save();
			//	Hiscores.save(player);
				BountyHunter.handleLogout(player);
				ClanChatManager.leave(player, false);
				player.getRelations().updateLists(false);
				PlayersOnlineInterface.remove(player);
				TaskManager.cancelTasks(player.getCombatBuilder());
				TaskManager.cancelTasks(player);
				player.save();
				World.getPlayers().remove(player);
				session.setState(SessionState.LOGGED_OUT);
				World.updatePlayersOnline();
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
