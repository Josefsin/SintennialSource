package com.agaroth.model.input.impl;

import com.agaroth.model.input.EnterAmount;
import com.agaroth.world.content.grandexchange.GrandExchange;
import com.agaroth.world.entity.impl.player.Player;

public class EnterGePricePerItem extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		GrandExchange.setPricePerItem(player, amount);
	}

}
