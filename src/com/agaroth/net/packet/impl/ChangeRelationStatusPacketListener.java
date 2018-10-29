package com.agaroth.net.packet.impl;

import com.agaroth.model.PlayerRelations.PrivateChatStatus;
import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.world.entity.impl.player.Player;

public class ChangeRelationStatusPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int actionId = packet.readInt();
		player.getRelations().setStatus(PrivateChatStatus.forActionId(actionId), true);
	}

}
