package com.agaroth.world.content;

import com.agaroth.model.Animation;
import com.agaroth.world.content.skill.impl.herblore.IngridientsBook;
import com.agaroth.world.entity.impl.player.Player;

public class DropTable {
	public static boolean tableopen = false;
	
	public static void openTable(Player player, int pageIndex, boolean interfaceAllowed) {
		if(player.getInterfaceId() != -1 && !interfaceAllowed) {
			player.getPacketSender().sendMessage("Please close the interface you have open before opening a new one.");
			return;
		}
		if(pageIndex < 0)
			pageIndex = 0;
		if(pageIndex > 10)
			pageIndex = 12;
		player.getMovementQueue().reset();
		player.performAnimation(new Animation(1350));
		player.getPacketSender().sendString(903, "Sintennial's Drop Table");
		for(int i = 0; i < pages[0].length; i++)
			player.getPacketSender().sendString(843+i, pages[pageIndex][i]);
		for(int i = 0; i < pages[1].length; i++)
			player.getPacketSender().sendString(843+11+i, pages[pageIndex+1][i]);
		player.getPacketSender().sendString(14165, "- "+pageIndex+" - ");
		player.getPacketSender().sendString(14166, "- "+(pageIndex+1)+" - ");
		player.getPacketSender().sendInterface(837); 
		player.setCurrentBookPage(pageIndex);
		IngridientsBook.bookopen = false;
		
	}
	
	private static final String[][] pages = {
		{"@red@King Black Dragon:",
		"Pet King Black Dragon",
		"Dragon Boots",
		"Draconic Visage ",
		"",
		"@red@Dagannoth Kings:",
		"Pet Rex, Prime, Supreme",
		"Dragon Hatchet ",
		
		"Berserker's Ring(i)",
		"Archer's Ring(i)",
		"Seer's Ring(i)",
		"",},{
		"@red@Frost Dragons:",
		"Pet Frost Dragon",
		"Draconic Visage",
		"Frost Dragon Mask ",
			
		"",
		"@red@Slash Bash:",
		"Pet Slash Bash",
		"Amulet of Fury ",
			
		"Dragon Bracelet",
		"",
		"",}, 
		{"@red@Phoenix:",
		"Pet Phoenix",
		"Dragon Pickaxe",
		"Dragon Hatchet ",
		"Ring of Fire",
		"",
		"@red@Tormented Demons:",
		"Pet Tormented Demon ",
		
		"Dragon Claws",
		"Dragon Boots",
		"Ruined Dragon Armour",
		"",},
		{"@red@Bandos Avatar:",
		"Pet Bandos Avatar",
		"Ring of Wealth",
		"Amulet of Ranging ",
		"",
		"@red@General Graardor:",
		"Pet General Graardor",
		"Bandos Chestplate ",
		
		"Bandos Tassets",
		"Bandos Boots",
		"Bandos Hilt",
		"",},{"@red@Kree'arra:",
		"Pet Kree'arra",
		"Armadyl Chestplate",
		"Armadyl Chainskirt ",
		"Armadyl Helmet",
		"Armadyl Hilt",
		"",
		"@red@Commander Zilyana: ",
		
		"Pet Commander Zilyana",
		"Saradomin Hilt/Sword",
		"Armadyl Crossbow",
		"",},{"@red@K'ril Tsutsaroth:",
		"Pet K'ril Tsutsaroth",
		"Zamorak Hilt",
		" ",
		"@red@Corporeal Beast:",
		"Pet Corporeal Beast",
		"Spectral Sigil",
		"Arcane Sigil ",
			
		"Elysian Sigil",
		"Divine Sigil",
		"Holy Elixir",
		"",},{"@red@Blood Reavers:",
		"Drygore Rapier",
		"Drygore Longsword",
		"Drygore Mace ",
		"",
		"@red@Glacors:",
		"Steadfast Boots",
		"Glavien Boots ",
			
		"Ragefire Boots",
		"",
		"",
		"",},{"@red@Nex:",
		"Pet Nex",
		"Torva Full Helm",
		"Torva Platebody ",
		"Torva Platelegs",
		"Pernix Cowl",
		"Pernix Body",
		"Pernix Chaps ",
				
		"Virtus Mask",
		"Virtus Robe Top",
		"Virtus Robe Legs",
		"",},{"@red@Kalphite Queen:",
		"Pet Kalphite Queen",
		"Dragon 2h Sword",
		" ",
		"@red@Scorpia:",
		"Staff of Gods",
		"Malediction Ward",
		"Odium Ward ",
				
		"Occult Necklace",
		"Ward Upgrade Kit",
		"Dragon Boots",
		"",},{"@red@Chaos Ele/Revanants:",
		"Pet Chaos Ele(CE only)",
		"Statius's Warhammer",
		"Statius's (FH/PB/PL) ",
		"Zuriel's Staff",
		"Zuriel's (H/RT/RB)",
		"Vesta's Longsword",
		"Vesta's Spear ",
				
		"Vesta's (CB/PK)",
		"Morrigan's Axe/Javelin",
		"Morrigan's (C/LB/LC)",
		"",}
		,{"@red@Venenatis:",
		"Dragonbone Full Helm",
		"Dragonbone Platebody",
		"Dragonbone Platelegs ",
		"Dragonbone Gloves",
		"Dragonbone Boots",
		"",
		"@red@Kraken: ",
					
		"Penance Master Trident",
		"Ring of Wealth",
		"Superior Death Lotus",
		"",},{		"",
			"",
			"",
		" ",
		"",
		"**END OF TABLE**",
		"",
		" ",
					
		"",
		"",
		"",
		"", "",},{"",
			"",
			"",
			"",
			"",
			"",
		"",
		"",
		"",
		"", "", ""},{"",
		"",
		"",
		"",
		"",
		"",
		" ",
		" ",			
		" ",
		" ",
		" ",
		" ","", ""}
		};
}