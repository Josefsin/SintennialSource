package com.agaroth.world.entity.impl.player;

import com.agaroth.model.GameMode;
import com.agaroth.util.Misc;
import com.agaroth.world.content.LoyaltyProgramme;
import com.agaroth.world.content.combat.pvp.BountyHunter;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.entity.impl.GroundItemManager;

public class PlayerProcess {
	private Player player;
	private int loyaltyTick;
	private int timerTick;
	private int previousHeight;

	public PlayerProcess(Player player) {
		this.player = player;
		this.previousHeight = player.getPosition().getZ();
	}

	public void sequence() {
		player.getCombatBuilder().process();
		if(player.shouldProcessFarming()) {
			player.getFarming().sequence();
		}
		if(previousHeight != player.getPosition().getZ()) {
			GroundItemManager.handleRegionChange(player);
			previousHeight = player.getPosition().getZ();
		}
		if(!player.isInActive()) {
			if(loyaltyTick >= 6) {
				LoyaltyProgramme.incrementPoints(player);
				loyaltyTick = 0;
			}
			loyaltyTick++;
		}
		boolean warning = 3634 <= player.getPosition().getX() && player.getPosition().getX() <= 3637 && player.getPosition().getY() <= 3539 && 3521 <= player.getPosition().getY(); 
		if(warning) {
			DialogueManager.start(player, 138);
	    }
	    if (Misc.getMinutesPlayed(this.player) == 15 && player.Restricted2 && player.getGameMode() != GameMode.IRONMAN && player.getGameMode() != GameMode.HARDCORE_IRONMAN) {
	    	player.getPacketSender().sendMessage("@red@You have played more than 15 minutes. All restrictions has been removed.");
	    	player.Restricted2 = false;
	    }
		if(timerTick >= 1) {
			player.getPacketSender().sendString(39162, "@or2@Time played:  @yel@"+Misc.getTimePlayed((player.getTotalPlayTime() + player.getRecordedLogin().elapsed())));
			timerTick = 0;
		}
		timerTick++;
		BountyHunter.sequence(player);
	}
}