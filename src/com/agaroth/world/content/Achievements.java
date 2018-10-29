package com.agaroth.world.content;

import com.agaroth.util.Misc;
import com.agaroth.world.entity.impl.player.Player;

public class Achievements {

	public enum AchievementData {

		ENTER_THE_LOTTERY(Difficulty.EASY, "Enter The Lottery", 37005, null),
		FILL_WELL_OF_GOODWILL_1M(Difficulty.EASY, "Pour 1M Into The Well", 37006, null),
		CUT_AN_OAK_TREE(Difficulty.EASY, "Cut An Oak Tree", 37007, null),
		BURN_AN_OAK_LOG(Difficulty.EASY, "Burn An Oak Log", 37008, null),
		FISH_A_SALMON(Difficulty.EASY, "Fish A Salmon", 37009, null),
		COOK_A_SALMON(Difficulty.EASY, "Cook A Salmon", 37010, null),
		EAT_A_SALMON(Difficulty.EASY, "Eat A Salmon", 37011, null),
		MINE_SOME_IRON(Difficulty.EASY, "Mine Some Iron", 37012, null),
		SMELT_AN_IRON_BAR(Difficulty.EASY, "Smelt An Iron Bar", 37013, null),
		HARVEST_A_CROP(Difficulty.EASY, "Harvest A Crop", 37014, null),
		CATCH_A_YOUNG_IMPLING(Difficulty.EASY, "Catch A Young Impling", 37015, null),
		CRAFT_A_PAIR_OF_LEATHER_BOOTS(Difficulty.EASY, "Craft A Pair of Leather Boots", 37016, null),
		CLIMB_AN_AGILITY_OBSTACLE(Difficulty.EASY, "Climb An Agility Obstacle", 37017, null),
		FLETCH_SOME_ARROWS(Difficulty.EASY, "Fletch Some headless arrows", 37018, null),
		STEAL_A_RING(Difficulty.EASY, "Steal A Ring", 37019, null),
		MIX_A_POTION(Difficulty.EASY, "Mix A Potion", 37020, null),
		RUNECRAFT_SOME_RUNES(Difficulty.EASY, "Runecraft Some Runes", 37021, null),
		BURY_A_BIG_BONE(Difficulty.EASY, "Bury A Big Bone", 37022, null),
		COMPLETE_A_SLAYER_TASK(Difficulty.EASY, "Complete A Slayer Task", 37023, null),
		KILL_A_MONSTER_USING_MELEE(Difficulty.EASY, "Kill a Monster Using Melee", 37024, null),
		KILL_A_MONSTER_USING_RANGED(Difficulty.EASY, "Kill a Monster Using Ranged", 37025, null),
		KILL_A_MONSTER_USING_MAGIC(Difficulty.EASY, "Kill a Monster Using Magic", 37026, null),
		DEAL_EASY_DAMAGE_USING_MELEE(Difficulty.EASY, "Deal 1000 Melee Damage", 37027, new int[]{0, 1000}),
		DEAL_EASY_DAMAGE_USING_RANGED(Difficulty.EASY, "Deal 1000 Ranged Damage", 37028, new int[]{1, 1000}),
		DEAL_EASY_DAMAGE_USING_MAGIC(Difficulty.EASY, "Deal 1000 Magic Damage", 37029, new int[]{2, 1000}),
		PERFORM_A_SPECIAL_ATTACK(Difficulty.EASY, "Perform a Special Attack", 37030, null),
		FIGHT_ANOTHER_PLAYER(Difficulty.EASY, "Fight Another Player", 37031, null),
	//	TRIVIA_NOVICE(Difficulty.EASY, "Trivia Novice", 37032, new int[]{53, 10}),

		FILL_WELL_OF_GOODWILL_50M(Difficulty.MEDIUM, "Pour 50M Into The Well", 37038, new int[]{4, 50000000}),
		CUT_100_MAGIC_LOGS(Difficulty.MEDIUM, "Cut 100 Magic Logs", 37039, new int[]{5, 100}),
		BURN_100_MAGIC_LOGS(Difficulty.MEDIUM, "Burn 100 Magic Logs", 37040, new int[]{6, 100}),
		FISH_25_ROCKTAILS(Difficulty.MEDIUM, "Fish 25 Rocktails", 37041, new int[]{7, 25}),
		COOK_25_ROCKTAILS(Difficulty.MEDIUM, "Cook 25 Rocktails", 37042, new int[]{8, 25}),
		MINE_25_RUNITE_ORES(Difficulty.MEDIUM, "Mine 25 Runite Ores", 37043, new int[]{9, 25}),
		SMELT_25_RUNE_BARS(Difficulty.MEDIUM, "Smelt 25 Rune Bars", 37044, new int[]{10, 25}),
		HARVEST_10_TORSTOLS(Difficulty.MEDIUM, "Harvest 10 Torstols", 37045, new int[]{11, 10}),
		INFUSE_25_TITAN_POUCHES(Difficulty.MEDIUM, "Infuse 25 Steel Titans", 37046, new int[]{12, 25}),
		COMPLETE_A_HARD_SLAYER_TASK(Difficulty.MEDIUM, "Complete A Hard Slayer Task", 37047, null),
		CRAFT_20_BLACK_DHIDE_BODIES(Difficulty.MEDIUM, "Craft 20 Black D'hide Bodies", 37048, new int[]{14, 20}),
		FLETCH_450_RUNE_ARROWS(Difficulty.MEDIUM, "Fletch 450 Rune Arrows", 37049, new int[]{15, 450}),
		STEAL_140_SCIMITARS(Difficulty.MEDIUM, "Steal 140 Scimitars", 37050, new int[]{16, 140}),
		MIX_AN_OVERLOAD_POTION(Difficulty.MEDIUM, "Mix An Overload Potion", 37051, null),
		ASSEMBLE_A_GODSWORD(Difficulty.MEDIUM, "Assemble A Godsword", 37052, null),
		CLIMB_50_AGILITY_OBSTACLES(Difficulty.MEDIUM, "Climb 50 Agility Obstacles", 37053, new int[]{17, 50}),
		RUNECRAFT_500_BLOOD_RUNES(Difficulty.MEDIUM, "Runecraft 500 Blood Runes", 37054, new int[]{18, 500}),
		BURY_25_FROST_DRAGON_BONES(Difficulty.MEDIUM, "Bury 25 Frost Dragon Bones", 37055, new int[]{19, 25}),
		DEAL_MEDIUM_DAMAGE_USING_MELEE(Difficulty.MEDIUM, "Deal 100K Melee Damage", 37056, new int[]{21, 100000}),
		DEAL_MEDIUM_DAMAGE_USING_RANGED(Difficulty.MEDIUM, "Deal 100K Ranged Damage", 37057, new int[]{22, 100000}),
		DEAL_MEDIUM_DAMAGE_USING_MAGIC(Difficulty.MEDIUM, "Deal 100K Magic Damage", 37058, new int[]{23, 100000}),
		DEFEAT_THE_KING_BLACK_DRAGON(Difficulty.MEDIUM, "Defeat The King Black Dragon", 37059, null),
		DEFEAT_THE_CHAOS_ELEMENTAL(Difficulty.MEDIUM, "Defeat The Chaos Elemental", 37060, null),
		DEFEAT_A_TORMENTED_DEMON(Difficulty.MEDIUM, "Defeat A Tormented Demon", 37061, null),
		DEFEAT_THE_CULINAROMANCER(Difficulty.MEDIUM, "Defeat The Culinaromancer", 37062, null),
		DEFEAT_NOMAD(Difficulty.MEDIUM, "Defeat Nomad", 37063, null),
	//	TRIVIA_APPRENTICE(Difficulty.MEDIUM, "Trivia Apprentice", 37064, new int[]{54, 50}),
		
		FILL_WELL_OF_GOODWILL_250M(Difficulty.HARD, "Pour 250M Into The Well", 37070, new int[]{25, 250000000}),
		CUT_5000_MAGIC_LOGS(Difficulty.HARD, "Cut 5000 Magic Logs", 37071, new int[]{26, 5000}),
		BURN_2500_MAGIC_LOGS(Difficulty.HARD, "Burn 2500 Magic Logs", 37072, new int[]{27, 2500}),
		FISH_2000_ROCKTAILS(Difficulty.HARD, "Fish 2000 Rocktails", 37073, new int[]{28, 2000}),
		COOK_1000_ROCKTAILS(Difficulty.HARD, "Cook 1000 Rocktails", 37074, new int[]{29, 1000}),
		MINE_2000_RUNITE_ORES(Difficulty.HARD, "Mine 2000 Runite Ores", 37075, new int[]{30, 2000}),
		SMELT_1000_RUNE_BARS(Difficulty.HARD, "Smelt 1000 Rune Bars", 37076, new int[]{31, 1000}),
		HARVEST_1000_TORSTOLS(Difficulty.HARD, "Harvest 1000 Torstols", 37077, new int[]{32, 1000}),
		INFUSE_500_STEEL_TITAN_POUCHES(Difficulty.HARD, "Infuse 500 Steel Titans", 37078, new int[]{33, 500}),
		CRAFT_1000_DIAMOND_GEMS(Difficulty.HARD, "Craft 1000 Diamond Gems", 37079, new int[]{34, 1000}),
		FLETCH_5000_RUNE_ARROWS(Difficulty.HARD, "Fletch 5000 Rune Arrows", 37080, new int[]{36, 5000}),
		STEAL_5000_SCIMITARS(Difficulty.HARD, "Steal 5000 Scimitars", 37081, new int[]{37, 5000}),
		RUNECRAFT_8000_BLOOD_RUNES(Difficulty.HARD, "Runecraft 8000 Blood Runes", 37082, new int[]{38, 8000}),
		BURY_500_FROST_DRAGON_BONES(Difficulty.HARD, "Bury 500 Frost Dragon Bones", 37083, new int[]{39, 500}),
		MIX_100_OVERLOAD_POTIONS(Difficulty.HARD, "Mix 100 Overload Potions", 37084, new int[]{41, 100}),
		COMPLETE_AN_ELITE_SLAYER_TASK(Difficulty.HARD, "Complete An Elite Slayer Task", 37085, null),
		ASSEMBLE_5_GODSWORDS(Difficulty.HARD, "Assemble 5 Godswords", 37086, new int[]{42, 5}),
		DEAL_HARD_DAMAGE_USING_MELEE(Difficulty.HARD, "Deal 10M Melee Damage", 37087, new int[]{43, 10000000}),
		DEAL_HARD_DAMAGE_USING_RANGED(Difficulty.HARD, "Deal 10M Ranged Damage", 37088, new int[]{44, 10000000}),
		DEAL_HARD_DAMAGE_USING_MAGIC(Difficulty.HARD, "Deal 10M Magic Damage", 37089, new int[]{45, 10000000}),
		DEFEAT_JAD(Difficulty.HARD, "Defeat Jad", 37090, null),
		DEFEAT_BANDOS_AVATAR(Difficulty.HARD, "Defeat Bandos Avatar", 37091, null),
		DEFEAT_GENERAL_GRAARDOR(Difficulty.HARD, "Defeat General Graardor", 37092, null),
		DEFEAT_KREE_ARRA(Difficulty.HARD, "Defeat Kree'Arra", 37093, null),
		DEFEAT_COMMANDER_ZILYANA(Difficulty.HARD, "Defeat Commander Zilyana", 37094, null),
		DEFEAT_KRIL_TSUTSAROTH(Difficulty.HARD, "Defeat K'ril Tsutsaroth", 37095, null),
		DEFEAT_THE_CORPOREAL_BEAST(Difficulty.HARD, "Defeat The Corporeal Beast", 37096, null),
		DEFEAT_NEX(Difficulty.HARD, "Defeat Nex", 37097, null),
	//	TRIVIA_MASTER(Difficulty.HARD, "Trivia Master", 37098, new int[]{55, 100}),

		COMPLETE_ALL_HARD_TASKS(Difficulty.ELITE, "Complete All Hard Tasks", 37104, new int[]{47, 32}),
		CUT_AN_ONYX_STONE(Difficulty.ELITE, "Cut An Onyx Stone", 37105, null),
		REACH_MAX_EXP_IN_A_SKILL(Difficulty.ELITE, "Reach Max Exp In A Skill", 37106, null),
		REACH_LEVEL_99_IN_ALL_SKILLS(Difficulty.ELITE, "Reach Level 99 In All Skills", 37107, null),
		DEFEAT_10000_MONSTERS(Difficulty.ELITE, "Defeat 10,000 Monsters", 37108, new int[]{49, 10000}),
		DEFEAT_500_BOSSES(Difficulty.ELITE, "Defeat 500 Boss Monsters", 37109, new int[]{50, 500}),
		VOTE_100_TIMES(Difficulty.ELITE, "Vote 100 Times", 37110, new int[]{51, 100}),
		UNLOCK_ALL_LOYALTY_TITLES(Difficulty.ELITE, "Unlock All Loyalty Titles", 37111, new int[]{52, 11}),
	//	TRIVIA_GRANDMASTER(Difficulty.ELITE, "Trivia GrandMaster", 37112, new int[]{56, 250}),
		;
		
		AchievementData(Difficulty difficulty, String interfaceLine, int interfaceFrame, int[] progressData) {
			this.difficulty = difficulty;
			this.interfaceLine = interfaceLine;
			this.interfaceFrame = interfaceFrame;
			this.progressData = progressData;
		}

		private Difficulty difficulty;
		private String interfaceLine;
		private int interfaceFrame;
		private int[] progressData;

		public Difficulty getDifficulty() {
			return difficulty;
		}
	}

	public enum Difficulty {
		BEGINNER, EASY, MEDIUM, HARD, ELITE;
	}

	public static boolean handleButton(Player player, int button) {
		if(!(button >= -28531 && button <= -28425)) {
			return false;
		}
		int index = -1;
		if(button >= -28531 && button <= -28505) {
			index = 28531 + button;
		} else if(button >= -28498 && button <= -28471) {
			index = 30 + 28495 + button;
		} else if(button >= -28466 && button <= -28437) {
			index = 61 + 28460 + button;
		} else if(button >= -28432 && button <= -28425) {
			index = 85 + 28432 + button;
		}
		if(index >= 0 && index < AchievementData.values().length) {
			AchievementData achievement = AchievementData.values()[index];
			if(player.getAchievementAttributes().getCompletion()[achievement.ordinal()]) {
				player.getPacketSender().sendMessage("<img=11> <col=339900>You have completed the achievement: "+achievement.interfaceLine+".");
			} else if(achievement.progressData == null) {
				player.getPacketSender().sendMessage("<img=11> <col=660000>You have not started the achievement: "+achievement.interfaceLine+".");
			} else {
				int progress = player.getAchievementAttributes().getProgress()[achievement.progressData[0]];
				int requiredProgress = achievement.progressData[1];
				if(progress == 0) {
					player.getPacketSender().sendMessage("<img=11> <col=660000>You have not started the achievement: "+achievement.interfaceLine+".");
				} else if(progress != requiredProgress) {
					player.getPacketSender().sendMessage("<img=11> <col=FFFF00>Your progress for this achievement is currently at: "+Misc.insertCommasToNumber(""+progress)+"/"+Misc.insertCommasToNumber(""+requiredProgress)+".");
				}
			}
		}
		return true;
	}

	public static void updateInterface(Player player) {
		for(AchievementData achievement : AchievementData.values()) {
			boolean completed = player.getAchievementAttributes().getCompletion()[achievement.ordinal()];
			boolean progress = achievement.progressData != null && player.getAchievementAttributes().getProgress()[achievement.progressData[0]] > 0;
			player.getPacketSender().sendString(achievement.interfaceFrame, (completed ? "@gre@" : progress ? "@yel@" : "@red@") + achievement.interfaceLine);
		}
		player.getPacketSender().sendString(37001, "Achievements: "+player.getPointsHandler().getAchievementPoints()+"/"+AchievementData.values().length);
	}
	
	public static void setPoints(Player player) {
		int points = 0;
		for(AchievementData achievement : AchievementData.values()) {
			if(player.getAchievementAttributes().getCompletion()[achievement.ordinal()]) {
				points++;
			}
		}
		player.getPointsHandler().setAchievementPoints(points, false);
	}

	public static void doProgress(Player player, AchievementData achievement) {
		doProgress(player, achievement, 1);
	}

	public static void doProgress(Player player, AchievementData achievement, int amt) {
		if(player.getAchievementAttributes().getCompletion()[achievement.ordinal()])
			return;
		if(achievement.progressData != null) {
			int progressIndex = achievement.progressData[0];
			int amountNeeded = achievement.progressData[1];
			int previousDone = player.getAchievementAttributes().getProgress()[progressIndex];
			if((previousDone+amt) < amountNeeded) {
				player.getAchievementAttributes().getProgress()[progressIndex] = previousDone+amt;
				if(previousDone == 0) 
					player.getPacketSender().sendString(achievement.interfaceFrame, "@yel@"+ achievement.interfaceLine);
			} else {
				finishAchievement(player, achievement);
			}
		}
	}

	public static void finishAchievement(Player player, AchievementData achievement) {
		if(player.getAchievementAttributes().getCompletion()[achievement.ordinal()])
			return;
		player.getAchievementAttributes().getCompletion()[achievement.ordinal()] = true;
		player.getPacketSender().sendString(achievement.interfaceFrame, ("@gre@") + achievement.interfaceLine).sendMessage("<img=11> <col=339900>You have completed the achievement "+Misc.formatText(achievement.toString().toLowerCase()+".")).sendString(37001, "Achievements: "+player.getPointsHandler().getAchievementPoints()+"/"+AchievementData.values().length);

		if(achievement.getDifficulty() == Difficulty.HARD) {
			doProgress(player, AchievementData.COMPLETE_ALL_HARD_TASKS);
		}
		
		player.getPointsHandler().setAchievementPoints(1, true);
	}

	public static class AchievementAttributes {

		public AchievementAttributes(){}

		/** ACHIEVEMENTS **/
		private boolean[] completed = new boolean[AchievementData.values().length];
		private int[] progress = new int[53];

		public boolean[] getCompletion() {
			return completed;
		}

		public void setCompletion(int index, boolean value) {
			this.completed[index] = value;
		}

		public void setCompletion(boolean[] completed) {
			this.completed = completed;
		}

		public int[] getProgress() {
			return progress;
		}

		public void setProgress(int index, int value) {
			this.progress[index] = value;
		}

		public void setProgress(int[] progress) {
			this.progress = progress;
		}

		/** MISC **/
		private int coinsGambled;
		private double totalLoyaltyPointsEarned;
		private boolean[] godsKilled = new boolean[5];

		public int getCoinsGambled() {
			return coinsGambled;
		}

		public void setCoinsGambled(int coinsGambled) {
			this.coinsGambled = coinsGambled;
		}

		public double getTotalLoyaltyPointsEarned() {
			return totalLoyaltyPointsEarned;
		}

		public void incrementTotalLoyaltyPointsEarned(double totalLoyaltyPointsEarned) {
			this.totalLoyaltyPointsEarned += totalLoyaltyPointsEarned;
		}

		public boolean[] getGodsKilled() {
			return godsKilled;
		}

		public void setGodKilled(int index, boolean godKilled) {
			this.godsKilled[index] = godKilled;
		}

		public void setGodsKilled(boolean[] b) {
			this.godsKilled = b;
		}
	}
}
