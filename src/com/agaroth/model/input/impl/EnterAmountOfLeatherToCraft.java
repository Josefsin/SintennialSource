package com.agaroth.model.input.impl;

import com.agaroth.model.input.EnterAmount;
import com.agaroth.world.content.skill.impl.crafting.LeatherMaking;
import com.agaroth.world.content.skill.impl.crafting.leatherData;
import com.agaroth.world.entity.impl.player.Player;

public class EnterAmountOfLeatherToCraft extends EnterAmount {
	
	@Override
	public void handleAmount(Player player, int amount) {
		for (final leatherData l : leatherData.values()) {
			if (player.getSelectedSkillingItem() == l.getLeather()) {
				LeatherMaking.craftLeather(player, l, amount);
				break;
			}
		}
	}
}
