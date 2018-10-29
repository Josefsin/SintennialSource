package com.agaroth.model.input.impl;

import com.agaroth.model.input.EnterAmount;
import com.agaroth.world.content.skill.impl.crafting.Gems;
import com.agaroth.world.entity.impl.player.Player;

public class EnterAmountOfGemsToCut extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getSelectedSkillingItem() > 0)
			Gems.cutGem(player, amount, player.getSelectedSkillingItem());
	}

}
