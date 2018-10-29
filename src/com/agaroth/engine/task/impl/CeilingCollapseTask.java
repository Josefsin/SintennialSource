package com.agaroth.engine.task.impl;

import com.agaroth.engine.task.Task;
import com.agaroth.model.CombatIcon;
import com.agaroth.model.Graphic;
import com.agaroth.model.Hit;
import com.agaroth.model.Hitmask;
import com.agaroth.model.Locations.Location;
import com.agaroth.util.Misc;
import com.agaroth.world.entity.impl.player.Player;

public class CeilingCollapseTask extends Task {

	public CeilingCollapseTask(Player player) {
		super(9, player, false);
		this.player = player;
	}

	private Player player;

	@Override
	public void execute() {
		if(player == null || !player.isRegistered() || player.getLocation() != Location.BARROWS && player.getLocation() != Location.KRAKEN || player.getLocation() == Location.BARROWS && player.getPosition().getY() < 8000) {
			player.getPacketSender().sendCameraNeutrality();
			stop();
			return;
		}
		player.performGraphic(new Graphic(60));
		player.getPacketSender().sendMessage("Some rocks fall from the ceiling and hit you.");
		player.forceChat("Ouch!");
		player.dealDamage(new Hit(70 + Misc.getRandom(70), Hitmask.RED, CombatIcon.BLOCK));
	}
}
