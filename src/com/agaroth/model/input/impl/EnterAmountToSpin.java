package com.agaroth.model.input.impl;

import com.agaroth.model.input.EnterAmount;
import com.agaroth.world.content.skill.impl.crafting.Flax;
import com.agaroth.world.entity.impl.player.Player;

public class EnterAmountToSpin extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		Flax.spinFlax(player, amount);
	}

}
