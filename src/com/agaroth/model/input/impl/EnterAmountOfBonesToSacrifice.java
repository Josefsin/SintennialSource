package com.agaroth.model.input.impl;

import com.agaroth.model.input.EnterAmount;
import com.agaroth.world.content.skill.impl.prayer.BonesOnAltar;
import com.agaroth.world.entity.impl.player.Player;

public class EnterAmountOfBonesToSacrifice extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		BonesOnAltar.offerBones(player, amount);
	}

}
