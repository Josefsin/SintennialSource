package com.agaroth.world.content;

import com.agaroth.util.Misc;
import com.agaroth.world.content.minigames.impl.Nomad;
import com.agaroth.world.content.minigames.impl.RecipeForDisaster;
import com.agaroth.world.content.randomevents.EvilTree;
import com.agaroth.world.content.randomevents.ShootingStar;
import com.agaroth.world.content.skill.impl.slayer.SlayerTasks;
import com.agaroth.world.entity.impl.player.Player;

public class PlayerPanel
{
  public static void refreshPanel(Player player)
  {
    player.PanelOpen = 0;
    player.getPacketSender().sendString(39159, "");
    player.getPacketSender().sendString(39164, "@whi@ Distraction & Diversion");
    player.getPacketSender().sendString(39165, "@whi@ Account Information");
    player.getPacketSender().sendString(39166, "@whi@ Player Statistics");
    player.getPacketSender().sendString(39167, "@whi@ Slayer information");
    player.getPacketSender().sendString(39168, "@whi@ Player MiniQuests");
    player.getPacketSender().sendString(39170, "@whi@ Links & Commands");
    player.getPacketSender().sendString(39171, "");
    player.getPacketSender().sendString(39172, "");
    player.getPacketSender().sendString(39173, "");
    player.getPacketSender().sendString(39174, "");
    player.getPacketSender().sendString(39175, "");
    player.getPacketSender().sendString(39176, "");
    player.getPacketSender().sendString(39177, "");
    player.getPacketSender().sendString(39178, "");
    player.getPacketSender().sendString(39179, "");
    player.getPacketSender().sendString(39181, "");
    player.getPacketSender().sendString(39182, "");
    player.getPacketSender().sendString(39183, "");
    player.getPacketSender().sendString(39184, "");
    player.getPacketSender().sendString(39185, "");
    player.getPacketSender().sendString(39186, "");
    player.getPacketSender().sendString(39187, "");
    player.getPacketSender().sendString(39188, "");
    player.getPacketSender().sendString(39189, "");
    player.getPacketSender().sendString(39190, "");
    player.getPacketSender().sendString(39180, "");
  }
  
  public static void DD(Player player)
  {
    player.PanelOpen = 1;
    player.getPacketSender().sendTabInterface(2, 55000);
    player.getPacketSender().sendString(55001, "Distraction & Diversion");
    player.getPacketSender().sendString(55020, "");
    if (ShootingStar.CRASHED_STAR == null) {
      player.getPacketSender().sendString(55021, "@or2@Crashed star: @yel@N/A");
    } else {
      player.getPacketSender().sendString(55021, "@or2@Crashed star: @yel@" + ShootingStar.CRASHED_STAR.getStarLocation().playerPanelFrame);
    }
    if (EvilTree.SPAWN_EVIL_TREE == null) {
      player.getPacketSender().sendString(55022, "@or2@Evil Tree: @yel@N/A");
    } else {
      player.getPacketSender().sendString(55022, "@or2@Evil Tree: @yel@" + EvilTree.SPAWN_EVIL_TREE.getTreeLocation().playerPanelFrame);
    }
    if (WellOfGoodwill.isActive()) {
    player.getPacketSender().sendString(55023, "@or2@Well of Goodwill: @yel@Active");
    } else {
    	player.getPacketSender().sendString(55023, "@or2@Well of Goodwill: @yel@N/A");
    }
    player.getPacketSender().sendString(55024, "");
    player.getPacketSender().sendString(55025, "");
    player.getPacketSender().sendString(55026, "");
    player.getPacketSender().sendString(55027, "");
    player.getPacketSender().sendString(55028, "");
    player.getPacketSender().sendString(55029, "");
    player.getPacketSender().sendString(55030, "");
    player.getPacketSender().sendString(55031, "");
    player.getPacketSender().sendString(55032, "");
    player.getPacketSender().sendString(55033, "");
    player.getPacketSender().sendString(55034, "");
    player.getPacketSender().sendString(55035, "");
    player.getPacketSender().sendString(55036, "");
    player.getPacketSender().sendString(55037, "");
    player.getPacketSender().sendString(55038, "");
    player.getPacketSender().sendString(55039, "");
    player.getPacketSender().sendString(55040, "");
    player.getPacketSender().sendString(55031, "");
    player.getPacketSender().sendString(55042, "");
    player.getPacketSender().sendString(55043, "");
    player.getPacketSender().sendString(55044, "");
    player.getPacketSender().sendString(55045, "");
  }
  
  public static void AccountInfo(Player player)
  {
    player.PanelOpen = 2;
    player.getPacketSender().sendTabInterface(2, 55000);
    player.getPacketSender().sendString(55001, "Account information");
    player.getPacketSender().sendString(55020, "@or2@Username:  @yel@" + player.getUsername());
    player.getPacketSender().sendString(55022, "@or2@Claimed:  @yel@$" + player.getAmountDonated());
    player.getPacketSender().sendString(55023, "@or2@Rank:  @yel@" + Misc.formatText(player.getRights().toString().toLowerCase()));
    player.getPacketSender().sendString(55024, "@or2@Email:  @yel@" + ((player.getEmailAddress() == null) || (player.getEmailAddress().equals("null")) ? "-" : player.getEmailAddress()));
    player.getPacketSender().sendString(55025, "@or2@Music:  @yel@" + (player.musicActive() ? "On" : "Off"));
    player.getPacketSender().sendString(55026, "@or2@Sounds:  @yel@" + (player.soundsActive() ? "On" : "Off"));
    player.getPacketSender().sendString(55027, "@or2@Exp Lock:  @yel@" + (player.experienceLocked() ? "Locked" : "Unlocked"));
    player.getPacketSender().sendString(55028, "@or2@Donator points:  @yel@" + player.getPointsHandler().getDonatorPoints());
    player.getPacketSender().sendString(55029, "@or2@Ironman points:  @yel@" + player.getPointsHandler().getIronManPoints());
    player.getPacketSender().sendString(55030, "");
    player.getPacketSender().sendString(55031, "");
    player.getPacketSender().sendString(55032, "");
    player.getPacketSender().sendString(55033, "");
    player.getPacketSender().sendString(55034, "");
    player.getPacketSender().sendString(55035, "");
    player.getPacketSender().sendString(55036, "");
    player.getPacketSender().sendString(55037, "");
    player.getPacketSender().sendString(55038, "");
    player.getPacketSender().sendString(55039, "");
    player.getPacketSender().sendString(55040, "");
    player.getPacketSender().sendString(55031, "");
    player.getPacketSender().sendString(55042, "");
    player.getPacketSender().sendString(55043, "");
    player.getPacketSender().sendString(55044, "");
    player.getPacketSender().sendString(55045, "");
  }
  
  public static void Statistics(Player player)
  {
    player.PanelOpen = 3;
    player.getPacketSender().sendTabInterface(2, 55000);
    player.getPacketSender().sendString(55001, "Personal statistics");
    player.getPacketSender().sendString(55021, "@or2@Prestige Points: @yel@" + player.getPointsHandler().prestigePoints);
    player.getPacketSender().sendString(55022, "@or2@Commendations: @yel@ " + player.getPointsHandler().commendations);
    player.getPacketSender().sendString(55023, "@or2@Loyalty Points: @yel@" + (int)player.getPointsHandler().loyaltyPoints);
    player.getPacketSender().sendString(55024, "@or2@Dung. Tokens: @yel@ " + player.getPointsHandler().dungTokens);
    player.getPacketSender().sendString(55025, "@or2@Voting Points: @yel@ " + player.getPointsHandler().votingPoints);
    player.getPacketSender().sendString(55026, "@or2@Slayer Points: @yel@" + player.getPointsHandler().slayerPoints);
    player.getPacketSender().sendString(55027, "@or2@Pk Points: @yel@" + player.getPointsHandler().pkPoints);
    player.getPacketSender().sendString(55028, "@or2@Wilderness Killstreak: @yel@" + player.getPlayerKillingAttributes().getPlayerKillStreak());
    player.getPacketSender().sendString(55029, "@or2@Wilderness Kills: @yel@" + player.getPlayerKillingAttributes().getPlayerKills());
    player.getPacketSender().sendString(55030, "@or2@Wilderness Deaths: @yel@" + player.getPlayerKillingAttributes().getPlayerDeaths());
    player.getPacketSender().sendString(55031, "@or2@Arena Victories: @yel@" + player.getDueling().arenaStats[0]);
    player.getPacketSender().sendString(55032, "@or2@Arena Losses: @yel@" + player.getDueling().arenaStats[1]);
    player.getPacketSender().sendString(55033, "");
    player.getPacketSender().sendString(55034, "");
    player.getPacketSender().sendString(55035, "");
    player.getPacketSender().sendString(55036, "");
    player.getPacketSender().sendString(55037, "");
    player.getPacketSender().sendString(55038, "");
    player.getPacketSender().sendString(55039, "");
    player.getPacketSender().sendString(55040, "");
    player.getPacketSender().sendString(55031, "");
    player.getPacketSender().sendString(55042, "");
    player.getPacketSender().sendString(55043, "");
    player.getPacketSender().sendString(55044, "");
    player.getPacketSender().sendString(55045, "");
  }
  
  public static void Slayer(Player player)
  {
    player.PanelOpen = 4;
    player.getPacketSender().sendTabInterface(2, 55000);
    player.getPacketSender().sendString(55001, "Slayer statistics");
    player.getPacketSender().sendString(55021, "");
    player.getPacketSender().sendString(55022, "@or2@Open Drop Log");
    player.getPacketSender().sendString(55023, "@or2@Master:  @yel@" + Misc.formatText(player.getSlayer().getSlayerMaster().toString().toLowerCase().replaceAll("_", " ")));
    if (player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK) {
      player.getPacketSender().sendString(55024, "@or2@Task:  @yel@" + Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " ")));
    } else {
      player.getPacketSender().sendString(55024, "@or2@Task:  @yel@" + Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " ")) + "s");
    }
    player.getPacketSender().sendString(55025, "@or2@Task Streak:  @yel@" + player.getSlayer().getTaskStreak());
    player.getPacketSender().sendString(55025, "@or2@Task Amount:  @yel@" + player.getSlayer().getAmountToSlay());
    if (player.getSlayer().getDuoPartner() != null) {
      player.getPacketSender().sendString(55026, "@or2@Duo Partner:  @yel@" + player.getSlayer().getDuoPartner());
    } else {
      player.getPacketSender().sendString(55026, "@or2@Duo Partner:");
    }
    player.getPacketSender().sendString(55027, "@or2@Open Kills Tracker");
    player.getPacketSender().sendString(55028, "");
    player.getPacketSender().sendString(55029, "");
    player.getPacketSender().sendString(55030, "");
    player.getPacketSender().sendString(55031, "");
    player.getPacketSender().sendString(55032, "");
    player.getPacketSender().sendString(55033, "");
    player.getPacketSender().sendString(55034, "");
    player.getPacketSender().sendString(55035, "");
    player.getPacketSender().sendString(55036, "");
    player.getPacketSender().sendString(55037, "");
    player.getPacketSender().sendString(55038, "");
    player.getPacketSender().sendString(55039, "");
    player.getPacketSender().sendString(55040, "");
    player.getPacketSender().sendString(55031, "");
    player.getPacketSender().sendString(55042, "");
    player.getPacketSender().sendString(55043, "");
    player.getPacketSender().sendString(55044, "");
    player.getPacketSender().sendString(55045, "");
  }
  
  public static void Quest(Player player)
  {
    player.PanelOpen = 5;
    player.getPacketSender().sendTabInterface(2, 55000);
    player.getPacketSender().sendString(55001, "Player Mini Quests");
    player.getPacketSender().sendString(55021, RecipeForDisaster.getQuestTabPrefix(player) + "Recipe For Disaster");
    player.getPacketSender().sendString(55022, Nomad.getQuestTabPrefix(player) + "Nomad's Requeim");
    player.getPacketSender().sendString(55023, "");
    player.getPacketSender().sendString(55024, "");
    player.getPacketSender().sendString(55025, "");
    player.getPacketSender().sendString(55026, "");
    player.getPacketSender().sendString(55027, "");
    player.getPacketSender().sendString(55028, "");
    player.getPacketSender().sendString(55029, "");
    player.getPacketSender().sendString(55030, "");
    player.getPacketSender().sendString(55031, "");
    player.getPacketSender().sendString(55032, "");
    player.getPacketSender().sendString(55033, "");
    player.getPacketSender().sendString(55034, "");
    player.getPacketSender().sendString(55035, "");
    player.getPacketSender().sendString(55036, "");
    player.getPacketSender().sendString(55037, "");
    player.getPacketSender().sendString(55038, "");
    player.getPacketSender().sendString(55039, "");
    player.getPacketSender().sendString(55040, "");
    player.getPacketSender().sendString(55031, "");
    player.getPacketSender().sendString(55042, "");
    player.getPacketSender().sendString(55043, "");
    player.getPacketSender().sendString(55044, "");
    player.getPacketSender().sendString(55045, "");
  }
  
  public static void Links(Player player)
  {
    player.PanelOpen = 6;
    player.getPacketSender().sendTabInterface(2, 55000);
    player.getPacketSender().sendString(55001, "Links & Commands");
    player.getPacketSender().sendString(55020, "");
    player.getPacketSender().sendString(55021, "");
    player.getPacketSender().sendString(55022, "@whi@ Website");
    player.getPacketSender().sendString(55023, "@whi@ Vote");
    player.getPacketSender().sendString(55024, "@whi@ Donate");
    player.getPacketSender().sendString(55025, "@whi@ Hiscores");
    player.getPacketSender().sendString(55026, "@whi@ Rules");
    player.getPacketSender().sendString(55027, "@whi@ Discord");
    player.getPacketSender().sendString(55028, "@whi@ Suggestions");
    player.getPacketSender().sendString(55029, "");
    player.getPacketSender().sendString(55030, "@yel@ Player Commands");
    player.getPacketSender().sendString(55020, "");
    player.getPacketSender().sendString(55032, "home");
    player.getPacketSender().sendString(55033, "reward");
    player.getPacketSender().sendString(55034, "claim");
    player.getPacketSender().sendString(55035, "gamble");
    player.getPacketSender().sendString(55036, "discord");
    player.getPacketSender().sendString(55037, "bug");
    player.getPacketSender().sendString(55038, "changepass");
    player.getPacketSender().sendString(55039, "recent");
    player.getPacketSender().sendString(55040, "donate");
    player.getPacketSender().sendString(55031, "attacks");
    player.getPacketSender().sendString(55042, "roll");
    player.getPacketSender().sendString(55043, "vote");
    player.getPacketSender().sendString(55044, "help");
    player.getPacketSender().sendString(55045, "players");
    player.getPacketSender().sendString(55046, "hiscores");
  }
 
}
