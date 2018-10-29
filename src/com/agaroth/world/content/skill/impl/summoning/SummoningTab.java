package com.agaroth.world.content.skill.impl.summoning;

import java.util.concurrent.TimeUnit;

import com.agaroth.model.Animation;
import com.agaroth.model.CombatIcon;
import com.agaroth.model.Damage;
import com.agaroth.model.Graphic;
import com.agaroth.model.Hit;
import com.agaroth.model.Hitmask;
import com.agaroth.model.Locations;
import com.agaroth.model.Locations.Location;
import com.agaroth.util.Misc;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class SummoningTab {
	
	public static void handleDismiss(Player c, boolean dismiss) {
		if(!dismiss && c.busy()) {
			c.getPacketSender().sendMessage("Please finish what you're doing first.");
			return;
		}
		c.getPacketSender().sendInterfaceRemoval();
		if(dismiss) {
			if(c.getSummoning().getFamiliar() != null) {
				c.getSummoning().unsummon(true, true);
				c.getPacketSender().sendMessage("You've dismissed your familiar.");
			} else {
				c.getPacketSender().sendMessage("You don't have a familiar to dismiss.");
			}
		} else {
			DialogueManager.start(c, 4);
			c.setDialogueActionId(4);
		}
	}
	
	public static void callFollower(final Player c) {
		if(c.getSummoning().getFamiliar() != null && c.getSummoning().getFamiliar().getSummonNpc() != null) {
			if(!c.getLastSummon().elapsed(30000)) {
				c.getPacketSender().sendMessage("You must wait another "+Misc.getTimeLeft(c.getLastSummon().elapsed(), 30, TimeUnit.SECONDS)+" seconds before being able to do this again.");
				return;
			}
			c.getSummoning().moveFollower(false);
		} else {
			c.getPacketSender().sendMessage("You don't have a familiar to call.");
		}
	}

	public static void renewFamiliar(Player c) {
		if(c.getSummoning().getFamiliar() != null) {
			int pouchRequired = FamiliarData.forNPCId(c.getSummoning().getFamiliar().getSummonNpc().getId()).getPouchId();
			if(c.getInventory().contains(pouchRequired)) {
				c.getSummoning().summon(FamiliarData.forNPCId(c.getSummoning().getFamiliar().getSummonNpc().getId()), true, false);
			} else {
				c.getPacketSender().sendMessage("You don't have the pouch required to renew this familiar.");
			}
		} else {
			c.getPacketSender().sendMessage("You don't have a familiar to renew.");
		}
	}
}
