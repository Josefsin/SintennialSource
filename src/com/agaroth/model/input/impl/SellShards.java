package com.agaroth.model.input.impl;

import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.model.input.EnterAmount;
import com.agaroth.util.Misc;
import com.agaroth.world.entity.impl.player.Player;

public class SellShards extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		player.getPacketSender().sendInterfaceRemoval();
		
		int shards = player.getInventory().getAmount(18016);
		if(amount > shards)
			amount = shards;
		if(amount == 0) {
			return;
		} else {
			long rew = (long) ItemDefinition.forId(18016).getValue() * amount;
			if(player.getMoneyInPouch() + rew > Long.MAX_VALUE) {
				player.getPacketSender().sendMessage("There is not enough space in your money pouch.");
				return;
			}
			player.getInventory().delete(18016, (int) amount);
			player.setMoneyInPouch(player.getMoneyInPouch() + rew);
			player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch());
			player.getPacketSender().sendMessage("You've sold "+amount+" Spirit Shards for "+Misc.insertCommasToNumber(""+rew)+" coins.");
		}
	}

}
