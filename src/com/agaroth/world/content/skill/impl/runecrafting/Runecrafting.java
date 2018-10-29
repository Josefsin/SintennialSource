package com.agaroth.world.content.skill.impl.runecrafting;

import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Skill;
import com.agaroth.world.content.Achievements;
import com.agaroth.world.entity.impl.player.Player;

public class Runecrafting
{
  public static void craftRunes(Player player, RunecraftingData.RuneData rune)
  {
    if (!canRuneCraft(player, rune)) {
      return;
    }
    int essence = -1;
    if ((player.getInventory().contains(1436)) && (!rune.pureRequired())) {
      essence = 1436;
    }
    if ((player.getInventory().contains(7936)) && (essence < 0)) {
      essence = 7936;
    }
    if (essence == -1) {
      return;
    }
    player.performGraphic(new Graphic(186));
    player.performAnimation(new Animation(791));
    int amountToMake = RunecraftingData.getMakeAmount(rune, player);
    int amountMade = 0;
    for (int i = 28; i > 0; i--)
    {
      if (!player.getInventory().contains(essence)) {
        break;
      }
      player.getInventory().delete(essence, 1);
      player.getInventory().add(rune.getRuneID(), amountToMake);
      amountMade += amountToMake;
      player.getSkillManager().addExperience(Skill.RUNECRAFTING, rune.getXP());
    }
    if (rune == RunecraftingData.RuneData.BLOOD_RUNE)
    {
      Achievements.doProgress(player, Achievements.AchievementData.RUNECRAFT_500_BLOOD_RUNES, amountMade);
      Achievements.doProgress(player, Achievements.AchievementData.RUNECRAFT_8000_BLOOD_RUNES, amountMade);
    }
    player.performGraphic(new Graphic(129));
    player.getSkillManager().addExperience(Skill.RUNECRAFTING, rune.getXP());
    player.getPacketSender().sendMessage("You bind the altar's power into " + rune.getName() + "s..");
    Achievements.finishAchievement(player, Achievements.AchievementData.RUNECRAFT_SOME_RUNES);
    player.getClickDelay().reset();
  }
  
  public static boolean canRuneCraft(Player player, RunecraftingData.RuneData rune)
  {
    if (rune == null) {
      return false;
    }
    if (player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < rune.getLevelRequirement())
    {
      player.getPacketSender().sendMessage("You need a Runecrafting level of at least " + rune.getLevelRequirement() + " to craft this.");
      return false;
    }
    if ((rune.pureRequired()) && (!player.getInventory().contains(7936)) && (!player.getInventory().contains(1436)))
    {
      player.getPacketSender().sendMessage("You do not have any Pure essence in your inventory.");
      return false;
    }
    if ((rune.pureRequired()) && (!player.getInventory().contains(7936)) && (player.getInventory().contains(1436)))
    {
      player.getPacketSender().sendMessage("Only Pure essence has the power to bind this altar's energy.");
      return false;
    }
    if ((!player.getInventory().contains(7936)) && (!player.getInventory().contains(1436)))
    {
      player.getPacketSender().sendMessage("You do not have any Rune or Pure essence in your inventory.");
      return false;
    }
    if (!player.getClickDelay().elapsed(4500)) {
      return false;
    }
    return true;
  }
  
  public static boolean runecraftingButton(Player player, int ID) {
	    return (ID >= 26102 && ID < 26116);
	}
}
