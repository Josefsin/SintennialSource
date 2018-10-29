package com.agaroth.net.packet.impl;

import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.world.entity.impl.player.Player;

public class CloseInterfacePacketListener implements PacketListener {
	
	@Override
	public void handleMessage(Player player, Packet packet) {
		player.getPacketSender().sendClientRightClickRemoval();
		player.getPacketSender().sendInterfaceRemoval();
	//	player.getPacketSender().sendTabInterface(Constants.CLAN_CHAT_TAB, 29328); //Clan chat tab
		//player.getAttributes().setSkillGuideInterfaceData(null);
	}
}
