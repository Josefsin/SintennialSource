package com.agaroth.model.input.impl;

import com.agaroth.model.container.impl.Bank.BankSearchAttributes;
import com.agaroth.model.input.Input;
import com.agaroth.world.entity.impl.player.Player;

public class EnterSyntaxToBankSearchFor extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		boolean searchingBank = player.isBanking() && player.getBankSearchingAttribtues().isSearchingBank();
		if(searchingBank)
			BankSearchAttributes.beginSearch(player, syntax);
	}
}
