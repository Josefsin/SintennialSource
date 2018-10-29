package com.agaroth.net.packet.impl;

import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.util.Misc;
import com.agaroth.world.entity.impl.player.Player;

public class EnterInputPacketListener implements PacketListener {


	@Override
	public void handleMessage(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case ENTER_SYNTAX_OPCODE:
			String name = Misc.readString(packet.getBuffer());
			if(name == null)
				return;
			if(player.getInputHandling() != null)
				player.getInputHandling().handleSyntax(player, name);
			player.setInputHandling(null);
			break;
			case ENTER_AMOUNT_OPCODE:
			int amount = packet.readInt();
			if(amount <= 0)
				return;
			if(player.getInputHandling() != null)
				player.getInputHandling().handleAmount(player, amount);
			player.setInputHandling(null);
			break;
		}
	}

	public static final int ENTER_AMOUNT_OPCODE = 208, ENTER_SYNTAX_OPCODE = 60;
}
