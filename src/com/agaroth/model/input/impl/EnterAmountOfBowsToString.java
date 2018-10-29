package com.agaroth.model.input.impl;

import com.agaroth.model.input.EnterAmount;
import com.agaroth.world.content.skill.impl.fletching.Fletching;
import com.agaroth.world.entity.impl.player.Player;

public class EnterAmountOfBowsToString extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		Fletching.stringBow(player, amount);
	}

}
