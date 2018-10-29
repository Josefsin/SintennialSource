package com.agaroth.net.packet.impl;

import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.world.World;
import com.agaroth.world.entity.impl.player.Player;
/**
 * Handles the follow player packet listener
 * Sets the player to follow when the packet is executed
 * @author Gabriel Hannason 
 */
public class FollowPlayerPacketListener implements PacketListener {


	@Override
	public void handleMessage(Player player, Packet packet) {
		if(player.getConstitution() <= 0)
			return;
		int otherPlayersIndex = packet.readLEShort();
		if(otherPlayersIndex < 0 || otherPlayersIndex > World.getPlayers().capacity())
			return;
		Player leader =	World.getPlayers().get(otherPlayersIndex);
		if(leader == null)
			return;
		if(leader.getConstitution() <= 0 || player.getConstitution() <= 0 || !player.getLocation().isFollowingAllowed()) {
			player.getPacketSender().sendMessage("You cannot follow other players right now.");
			return;
		}
		player.setEntityInteraction(leader);
		player.getMovementQueue().setFollowCharacter(leader);
	}

}
