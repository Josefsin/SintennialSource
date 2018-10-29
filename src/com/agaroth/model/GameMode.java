package com.agaroth.model;

import com.agaroth.world.content.dialogue.impl.Tutorial;
import com.agaroth.world.entity.impl.player.Player;

public enum GameMode {
  NORMAL,  HARDCORE_IRONMAN,  IRONMAN;
  
  public static void set(Player player, GameMode newMode, boolean death) {
	  if(!death && !player.getClickDelay().elapsed(1000)) {
			return;
	}
    player.getClickDelay().reset();
    player.getPacketSender().sendInterfaceRemoval();
    player.setGameMode(newMode);
    player.getPacketSender().sendIronmanMode(newMode.ordinal());
    if (!death) {
      player.getPacketSender().sendMessage("").sendMessage("You've set your gamemode to " + newMode.name().toLowerCase().replaceAll("_", " ") + ".").sendMessage("If you wish to change it, please talk to the town crier at Home.");
    } else {
      player.getPacketSender().sendMessage("Your account progress has been reset.");
    }
    if (player.newPlayer()) {
      Tutorial.OptionalTutorial(player);
      player.setPlayerLocked(true);
    }
    else {
      player.setPlayerLocked(false);
    }
  }
}