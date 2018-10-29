package com.agaroth.world.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.agaroth.model.Item;
import com.agaroth.util.Misc;
import com.agaroth.world.content.combat.prayer.CurseHandler;
import com.agaroth.world.content.combat.prayer.PrayerHandler;
import com.agaroth.world.entity.impl.player.Player;

public class ItemsKeptOnDeath {

	public static void sendInterface(Player player) {
		clearInterfaceData(player); //To prevent sending multiple layers of items.
		sendInterfaceData(player); //Send info on the interface.
		player.getPacketSender().sendInterface(17100); //Open the interface.
	}

	public static void sendInterfaceData(Player player) {

		int size = getAmountToKeep(player);
		String[] infoToFill = new String[6];
		switch(size) {
		case 0: 
			infoToFill[0] = "You are skulled and will"; infoToFill[1] = "not keep any items on"; infoToFill[2] = "death.";
			break;
		case 1: 
		case 4: 
			infoToFill[0] = "You have the Protect Item"; infoToFill[1] = "prayer/curse active."; infoToFill[2] = "Therefore`, you will keep"; infoToFill[3] = "an extra item on death.";
			break;
		case 2:
			break;
		case 3:
			infoToFill[0] = "You will keep 3 items"; infoToFill[1] = "on death since you"; infoToFill[2] = "have no factors active.";
			break;
		}
		infoToFill[4] = "@red@All untradeable items"; infoToFill[5] = "@red@are automatically kept.";
		for(int i = 0; i < infoToFill.length; i++)
			if(infoToFill[i] == null)
				player.getPacketSender().sendString(17143 + i, "");
			else
				player.getPacketSender().sendString(17143 + i, infoToFill[i]);
		player.getPacketSender().sendMessage("@red@Note: All untradeable items are automatically kept on death!");
		ArrayList<Item> toKeep = getItemsToKeep(player);
		for(int i = 0; i < toKeep.size(); i++) {
			player.getPacketSender().sendItemOnInterface(17108+i, toKeep.get(i).getId(), 1);
		}
		int toSend = 17112;
		for(Item item : Misc.concat(player.getInventory().getItems(), player.getEquipment().getItems())) {
			if(item == null || item.getId() <= 0 || item.getAmount() <= 0 || !item.tradeable() || toKeep.contains(item)) {
				continue;
			}
			if(toSend >= 17142)
				toSend = 17150;
			if(toSend >= 17160)
				break;
			player.getPacketSender().sendItemOnInterface(toSend, item.getId(), item.getAmount());
			toSend++;
		}
		infoToFill = null;
	}

	public static void clearInterfaceData(Player player) {
		player.getPacketSender().sendString(17107, "");
		for(int i = 17108; i <= 17142; i++)
			player.getPacketSender().sendItemOnInterface(i, -1, 1);
		for(int i = 17150; i <= 17160; i++)
			player.getPacketSender().sendItemOnInterface(i, -1, 1);
	}

	public static ArrayList<Item> getItemsToKeep(Player player) {
		ArrayList<Item> items = new ArrayList<Item>();
		for(Item item : Misc.concat(player.getInventory().getItems(), player.getEquipment().getItems())) {
			if(item == null || item.getId() <= 0 || item.getAmount() <= 0 || !item.tradeable()) {
				continue;
			}
			items.add(item);
		}
		Collections.sort(items, new Comparator<Item>() {
			@Override
			public int compare(Item item, Item item2) {
				int value1 = item.getDefinition().getValue();
				int value2 = item2.getDefinition().getValue();
				if (value1 == value2) {
					return 0;
				} else if (value1 > value2) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		ArrayList<Item> toKeep = new ArrayList<Item>();
		int amountToKeep = getAmountToKeep(player);
		for(int i = 0; i < amountToKeep && i < items.size(); i++) {
			toKeep.add(items.get(i));
		}
		return toKeep;
	}

	public static int getAmountToKeep(Player player) {
		boolean keepExtraItem = PrayerHandler.isActivated(player, PrayerHandler.PROTECT_ITEM) || CurseHandler.isActivated(player, CurseHandler.PROTECT_ITEM);
		return (player.getSkullTimer() > 0 ? 0 : 3) + (keepExtraItem ? 1 : 0);
	}
}