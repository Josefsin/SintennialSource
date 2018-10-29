package com.agaroth.net.packet.impl;

import com.agaroth.model.RegionInstance.RegionInstanceType;
import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.world.clip.region.RegionClipping;
import com.agaroth.world.content.CustomObjects;
import com.agaroth.world.content.Sounds;
import com.agaroth.world.content.skill.impl.hunter.Hunter;
import com.agaroth.world.entity.impl.GroundItemManager;
import com.agaroth.world.entity.impl.player.Player;


public class RegionChangePacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		if(player.isAllowRegionChangePacket()) {
			RegionClipping.loadRegion(player.getPosition().getX(), player.getPosition().getY());
			player.getPacketSender().sendMapRegion();
			CustomObjects.handleRegionChange(player);
			GroundItemManager.handleRegionChange(player);
			Sounds.handleRegionChange(player);
			player.getTolerance().reset();
			Hunter.handleRegionChange(player);
			if(player.getRegionInstance() != null && player.getPosition().getX() != 1 && player.getPosition().getY() != 1) {
				if(player.getRegionInstance().equals(RegionInstanceType.BARROWS) || player.getRegionInstance().equals(RegionInstanceType.WARRIORS_GUILD))
					player.getRegionInstance().destruct();
			}
			player.getNpcFacesUpdated().clear();
			player.setRegionChange(false).setAllowRegionChangePacket(false);
		}
	}
}
