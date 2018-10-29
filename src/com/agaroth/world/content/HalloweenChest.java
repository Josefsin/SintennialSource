package com.agaroth.world.content;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.GameObject;
import com.agaroth.model.Item;
import com.agaroth.util.Misc;
import com.agaroth.world.entity.impl.player.Player;

public class HalloweenChest {
	
	public static void handleChest(final Player p, final GameObject chest) {
		if(!p.getClickDelay().elapsed(600)) 
			return;
		if(!p.getInventory().contains(11797)) {
			p.getPacketSender().sendMessage("This chest can only be opened with a Death key.");
			return;
		}
		if(p.getInventory().getFreeSlots() < 5) {
			p.getPacketSender().sendMessage("You do not have enough space in your inventory");
			return;
		}
		p.performAnimation(new Animation(827));
		p.getInventory().delete(11797, 1);
		p.getPacketSender().sendMessage("Your key crumbles to dust after touching the chest...");
		TaskManager.submit(new Task(1, p, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 1:
					Item[] loot = itemRewards[Misc.getRandom(itemRewards.length - 1)];
					for(Item item : loot) {
						p.getInventory().add(item);
					}
					p.getPacketSender().sendMessage("...you've received something, @or2@Happy Halloween!");
					CustomObjects.objectRespawnTask(p, new GameObject(2404, chest.getPosition().copy(), 10, 0), chest, 10);
					stop();
					break;
				}
				tick++;
			}
		});
		p.getClickDelay().reset();
	}

	private static final Item[][] itemRewards =  {
			{new Item(995, 100000000)}, //set 1 100m
			{new Item(1632, 1000)}, //set 2 1k Uncut dragonstone
			{new Item(1514, 2500)}, //set 3 2.5k Magic logs
			{new Item(537, 1000)}, //set 4 1k Dragon bones
			{new Item(19335, 1), new Item(995, 50000000)}, //set 5 Amulet of Fury (or) & 50m 
			{new Item(7937, 5000)}, //set 6 5k Pure Essence
			{new Item(986, 10), new Item(995, 25000000)}, //set 7 10 Tooth halves & 25m
			{new Item(988, 10), new Item(995, 25000000)}, //set 8 10 Loop halves & 25m
			{new Item(2364, 500)}, //set 9 500 Rune bars
			{new Item(21650, 1)}, //set 10 Lava H'ween Mask
			{new Item(12158, 600), new Item (995, 25000000)}, //set 11 600 gold charms & 25m
			{new Item(12159, 500), new Item(995, 25000000)}, //set 12 500 green charms & 25m
			{new Item(12160, 400), new Item(995, 25000000)}, //set 13 400 crimson charms & 25m
			{new Item(12163, 300), new Item(995, 25000000)}, //set 14 300 blue charms & 25m
			{new Item(19111, 1)}, //set 15 Tok-Haar-Kal
			{new Item(11710, 1), new Item(11712, 1), new Item(11714, 1)}, //set 16 Godsword shard 1, 2, 3
			{new Item(11704, 1)}, //set 17 Bandos Hilt
			{new Item(11708, 1)}, //set 18 Zamorak Hilt
			{new Item(11706, 1)}, //set 19 Saradomin Hilt
			{new Item(11702, 1)}, //set 20 Armadyl Hilt
			{new Item(11846, 1)}, //set 21 Ahrim's set
			{new Item(11848, 1)}, //set 22 Dharok's set
			{new Item(11850, 1)}, //set 23 Guthan's set
			{new Item(11852, 1)}, //set 24 Karil's set
			{new Item(21644, 1)}, //set 25 Black H'ween Mask
			{new Item(11854, 1)}, //set 26 Torag's set
			{new Item(11856, 1)}, //set 27 Verac's set
			{new Item(20157, 5)}, //set 28 5 God Wars KC key
			{new Item(18831, 500)}, //set 29 500 Frost Dragon bones
			{new Item(17273, 1)}, //set 30 Flameburst Defender
			{new Item(560, 200000), new Item(565, 200000), new Item(566, 50000)}, //set 31 50k casts of Blood Barrage
			{new Item(560, 200000), new Item(565, 100000), new Item(555, 300000)}, //set 32 50k casts of Ice Barrage
			{new Item(13727, 1000)}, //set 33 1k Stardust
			{new Item(1960, 5000)}, //set 34 5k Pumpkins
			{new Item(2578, 1), new Item(2581, 1)}, //set 35 Ranger Boots & Robin Hood Hat
			{new Item(212, 500)}, //set 36 500 Grimy Avantoe
			{new Item(2486, 500)}, //set 500 Grimy Lantadyme
			{new Item(218, 500)}, //set 38 500 Grimy Dwarf Weed
			{new Item(12539, 2500)}, //set 39 2.5k Grenwall Spikes
			{new Item(1419, 1)}, //set 40 Scythe
			{new Item(2572, 1)}, //set 41 Ring of Wealth
			{new Item(11789, 1)}, //set 42 Grim Reaper Hood
			{new Item(10728, 1), new Item(10727, 1), new Item(10726, 1), new Item(10725, 1), new Item(10724, 1)}, //set 43 Skeletal set
			{new Item(10723, 1)}, //set 44 Jack Lantern Mask
			{new Item(15352, 1)}, //set 45 Web Cloak
			{new Item(14076, 1), new Item(14077, 1), new Item(14081, 1)} //set 46 Warlock set
		};
	
}



