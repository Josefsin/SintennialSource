package com.agaroth.model.input.impl;

import com.agaroth.model.input.Input;
import com.agaroth.world.content.clan.ClanChatManager;
import com.agaroth.world.entity.impl.player.Player;

public class EnterClanChatToJoin extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		if(syntax.length() <= 1) {
			player.getPacketSender().sendMessage("Invalid syntax entered.");
			return;
		}
		ClanChatManager.join(player, syntax);
	}
}
