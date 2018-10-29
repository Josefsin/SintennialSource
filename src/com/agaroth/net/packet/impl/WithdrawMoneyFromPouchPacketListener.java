package com.agaroth.net.packet.impl;

import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.world.content.MoneyPouch;
import com.agaroth.world.entity.impl.player.Player;

public class WithdrawMoneyFromPouchPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int amount = packet.readInt();
		MoneyPouch.withdrawMoney(player, amount);
	}

}
