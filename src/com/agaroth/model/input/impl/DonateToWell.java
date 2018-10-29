package com.agaroth.model.input.impl;

import com.agaroth.model.input.EnterAmount;
import com.agaroth.world.content.WellOfGoodwill;
import com.agaroth.world.entity.impl.player.Player;

public class DonateToWell extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		WellOfGoodwill.donate(player, amount);
	}

}
