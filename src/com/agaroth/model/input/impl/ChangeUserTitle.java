package com.agaroth.model.input.impl;

import com.agaroth.model.Item;
import com.agaroth.model.input.Input;
import com.agaroth.util.Misc;
import com.agaroth.world.entity.impl.player.Player;

public class ChangeUserTitle extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		if(syntax == null || syntax.length() <= 1) {
			player.getPacketSender().sendMessage("Invalid syntax entered.");
			return;
		}
		if (Misc.containsCensoredWord(syntax) || Misc.blockedWord(syntax)) {
			player.getPacketSender().sendMessage("You can't use this kind of text in your yell title.");
			return;
		}
		Item item = new Item(995, 100000000);
		if (!player.getInventory().contains(item)) {
			player.getPacketSender().sendMessage("You don't have enough money in your inventory.");
			return;
		}
		player.getInventory().delete(item);
		player.setYellTitle(syntax);
	}

}
