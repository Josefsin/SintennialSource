package com.agaroth.model.input.impl;

import com.agaroth.model.input.EnterAmount;
import com.agaroth.world.content.skill.impl.summoning.PouchMaking;
import com.agaroth.world.entity.impl.player.Player;

public class EnterAmountToInfuse extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getInterfaceId() != 63471) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		PouchMaking.infusePouches(player, amount);
	}

}
