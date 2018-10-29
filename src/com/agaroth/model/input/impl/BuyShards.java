package com.agaroth.model.input.impl;

import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.model.input.EnterAmount;
import com.agaroth.util.Misc;
import com.agaroth.world.entity.impl.player.Player;

public class BuyShards extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		player.getPacketSender().sendInterfaceRemoval();
		long cost = (long) ItemDefinition.forId(18016).getValue() * amount;
		long moneyAmount = player.getMoneyInPouch() + player.getInventory().getAmount(995);
		long canBeBought = moneyAmount / (ItemDefinition.forId(18016).getValue());
		if(canBeBought >= amount)
			canBeBought = amount;
		if(canBeBought >= Integer.MAX_VALUE) {
			canBeBought = Integer.MAX_VALUE;
		}
		if(canBeBought == 0) {
			player.getPacketSender().sendMessage("You do not have enough money to buy that amount.");
			return;
		}
		cost = (long) ItemDefinition.forId(18016).getValue() * (int) canBeBought;
		if(moneyAmount < cost) {
			player.getPacketSender().sendMessage("You do not have enough money to buy that amount.");
			return;
		}
		long totalCost = cost;
		int inventoryCost = cost <= Integer.MAX_VALUE ? (int) cost : Integer.MAX_VALUE;
		if(player.getInventory().getAmount(995) >= inventoryCost) {
			player.getInventory().delete(995, inventoryCost);
			totalCost -= inventoryCost;
		}
		if(totalCost > 0) {
			player.setMoneyInPouch(player.getMoneyInPouch() - totalCost);
			player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch());
		}
		player.getInventory().add(18016, (int) canBeBought);
		player.getPacketSender().sendMessage("You've bought "+canBeBought+" Spirit Shards for "+Misc.insertCommasToNumber(""+cost)+" coins.");
	}

}
