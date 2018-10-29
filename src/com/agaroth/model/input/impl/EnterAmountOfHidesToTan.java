package com.agaroth.model.input.impl;

import com.agaroth.model.input.EnterAmount;
import com.agaroth.world.content.skill.impl.crafting.Tanning;
import com.agaroth.world.entity.impl.player.Player;

public class EnterAmountOfHidesToTan extends EnterAmount {

	private int button;
	public EnterAmountOfHidesToTan(int button) {
		this.button = button;
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		Tanning.tanHide(player, button, amount);
	}

}
