package com.agaroth.world.content.skill.impl.thieving;

import com.agaroth.model.Animation;
import com.agaroth.model.Skill;
import com.agaroth.world.entity.impl.player.Player;

public class Pickpocket {
  public static void stealFromNPC(Player player, int lvlreq, int xp, int reward, String message) {
    if (player.getInventory().getFreeSlots() < 1) {
      player.getPacketSender().sendMessage("You need some more inventory space to do this.");
      return;
    }
    if (player.getCombatBuilder().isBeingAttacked()) {
      player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
      return;
    }
    if (!player.getClickDelay().elapsed(2000)) {
      return;
    }
    if (player.getSkillManager().getMaxLevel(Skill.THIEVING) < lvlreq) {
      player.getPacketSender().sendMessage("You need a Thieving level of at least " + lvlreq + " to steal from this NPC.");
      return;
    }
    player.performAnimation(new Animation(881));
    player.getPacketSender().sendMessage(message);
    player.getPacketSender().sendInterfaceRemoval();
    player.getSkillManager().addExperience(Skill.THIEVING, xp);
    player.getClickDelay().reset();
    player.getInventory().add(reward, 1);
    player.getSkillManager().stopSkilling();
  }
}