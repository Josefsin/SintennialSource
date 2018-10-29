package com.agaroth.world.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.agaroth.GameServer;
import com.agaroth.model.PlayerRights;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.content.Achievements.AchievementData;
import com.agaroth.world.content.dialogue.Dialogue;
import com.agaroth.world.content.dialogue.DialogueExpression;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.content.dialogue.DialogueType;
import com.agaroth.world.entity.impl.player.Player;

public class Lottery {

	private static final List<String> CONTESTERS = new ArrayList<String>();
	private static final File CONTESTERS_FILE_LOCATION = new File("./data/saves/lottery/lottery.txt");
	private static final File LAST_WINNER_FILE_LOCATION = new File("./data/saves/lottery/lotterywin.txt");
	
	private static boolean LOTTERY_ENABLED = true;
	private static final int PRICE_TO_ENTER = 1000000;

	public static final int getPot() {
		if(CONTESTERS.size() == 0) {
			return 0;
		}
		return (CONTESTERS.size() * (PRICE_TO_ENTER - 250000));
	}

	private static String LAST_WINNER = "Agaroth";

	public static String getLastWinner() {
		return LAST_WINNER;
	}

	private static boolean LAST_WINNER_REWARDED = true;

	public static String getRandomWinner() {
		String winner = null;
		int listSize = CONTESTERS.size();
		if(listSize >= 4)
			winner = CONTESTERS.get(Misc.getRandom(listSize - 1));
		return winner;
	}

	public static void enterLottery(Player p) {
		if(!LOTTERY_ENABLED) {
			p.getPacketSender().sendInterfaceRemoval().sendMessage("The lottery is currently not active. Try again soon!");
			return;
		}
		if(CONTESTERS.contains(p.getUsername())) {
			DialogueManager.start(p, 17);
			return;
		}
		boolean usePouch = p.getMoneyInPouch() >= PRICE_TO_ENTER;
		if(p.getInventory().getAmount(995) < PRICE_TO_ENTER && !usePouch || p.getRights() == PlayerRights.ADMINISTRATOR || p.getRights() == PlayerRights.OWNER) {
			p.getPacketSender().sendInterfaceRemoval().sendMessage("").sendMessage("You do not have enough money in your inventory to enter this week's lottery.").sendMessage("The lottery for this week costs "+Misc.insertCommasToNumber(""+PRICE_TO_ENTER+"")+" coins to enter.");
			return;
		}
		if(usePouch) {
			p.setMoneyInPouch(p.getMoneyInPouch() - PRICE_TO_ENTER);
			p.getPacketSender().sendString(8135, ""+p.getMoneyInPouch());
		} else
			p.getInventory().delete(995, PRICE_TO_ENTER);
		p.getAchievementAttributes().setCoinsGambled(p.getAchievementAttributes().getCoinsGambled() + PRICE_TO_ENTER);
		addToLottery(p.getUsername());
		p.getPacketSender().sendMessage("You have entered the lottery!").sendMessage("A winner is announced every Friday.");
		DialogueManager.start(p, 18);
		Achievements.finishAchievement(p, AchievementData.ENTER_THE_LOTTERY);
	}

	public static void addToLottery(String user) {
		CONTESTERS.add(user);
		GameServer.getLoader().getEngine().submit(() -> {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(CONTESTERS_FILE_LOCATION, true));
				writer.write(""+user+"");
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static void init() {
		try {
			BufferedReader r = new BufferedReader(new FileReader(CONTESTERS_FILE_LOCATION));
			while(true) {
				String line = r.readLine();
				if(line == null) {
					break;
				} else {
					line = line.trim();
				}
				if(line.length() > 0) {
					if(!CONTESTERS.contains(line)) //user might have gotten on list twice somehow.. don't give them extra chance of winning
						CONTESTERS.add(line);
				}
			}
			r.close();

			BufferedReader r2 = new BufferedReader(new FileReader(LAST_WINNER_FILE_LOCATION));
			while(true) {
				String line = r2.readLine();
				if(line == null) {
					break;
				} else {
					line = line.trim();
				}
				if(line.length() > 0) {
					if(!line.contains("NOT REWARDED. NEEDS REWARD!"))
						LAST_WINNER = line;
					else
						LAST_WINNER_REWARDED = false;
				}
			}
			r2.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void restartLottery() {
		if(!LOTTERY_ENABLED)
			return;
		try {
			String winner = getRandomWinner();
			if(winner != null) {
				LAST_WINNER = winner;
				Player player = World.getPlayerByName(winner);
				BufferedWriter writer = new BufferedWriter(new FileWriter(LAST_WINNER_FILE_LOCATION));
				writer.write(winner);
				writer.newLine();
				if(player != null) {
					rewardPlayer(player, true);
				} else {
					LAST_WINNER_REWARDED = false;
					writer.write("NOT REWARDED. NEEDS REWARD!");
					System.out.println("Player "+winner+" won the lottery but wasn't online.");
				}
				CONTESTERS.clear();
				writer.close();
				writer = new BufferedWriter(new FileWriter(CONTESTERS_FILE_LOCATION));
				writer.write("");
				writer.close();
				World.sendMessage("<col=D9D919><shad=0>This week's lottery winner is "+winner+"! Congratulations!");
			} else
				World.sendMessage("<col=D9D919><shad=0>The lottery needs some more contesters before a winner can be selected.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void rewardPlayer(Player player, boolean ignore) throws IOException {
		if((!LAST_WINNER_REWARDED || ignore) && LAST_WINNER.equalsIgnoreCase(player.getUsername())) {
			LAST_WINNER_REWARDED = true;
			player.setMoneyInPouch(player.getMoneyInPouch() + getPot());
			player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch());
			player.getPacketSender().sendMessage("You've won the lottery for this week! Congratulations!");
			player.getPacketSender().sendMessage("The reward has been added to your money pouch.");
			BufferedWriter writer = new BufferedWriter(new FileWriter(LAST_WINNER_FILE_LOCATION));
			writer.write(player.getUsername());
			writer.close();
			PlayerLogs.log(player.getUsername(), "Player got "+getPot()+" from winning the lottery!");
		}
	}

	public static void onLogin(Player p) {
		try {
			rewardPlayer(p, false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static class Dialogues {

		public static Dialogue getCurrentPot(Player p) {
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public int npcId() {
					return 4249;
				}

				@Override
				public String[] dialogue() {
					return new String[] {"The pot is currently at:", ""+Misc.insertCommasToNumber(""+Lottery.getPot())+" coins."};
				}

				@Override
				public Dialogue nextDialogue() {
					return DialogueManager.getDialogues().get(15);
				}
			};
		}

		public static Dialogue getLastWinner(Player p) {
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public int npcId() {
					return 4249;
				}

				@Override
				public String[] dialogue() {
					return new String[] {"Last week's winner was "+Lottery.getLastWinner()+"."};
				}

				@Override
				public Dialogue nextDialogue() {
					return DialogueManager.getDialogues().get(15);
				}
			};
		}
	}
}