package com.agaroth.world.content;

import com.agaroth.model.Flag;
import com.agaroth.model.Item;
import com.agaroth.model.container.impl.Equipment;
import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.world.entity.impl.player.Player;

public class ItemDegrading {

	public static boolean handleItemDegrading(Player p, DegradingItem d) {
		int equipId = p.getEquipment().getItems()[d.equipSlot].getId();
		if(equipId == d.nonDeg || equipId == d.deg) {
			int maxCharges = d.degradingCharges;
			int currentCharges = getAndIncrementCharge(p, d, false);
			boolean degradeCompletely = currentCharges >= maxCharges;
			if(equipId == d.deg && !degradeCompletely) {
				return true;
			}
			degradeCompletely = degradeCompletely && equipId == d.deg;
			p.getEquipment().setItem(d.equipSlot, new Item(degradeCompletely ? - 1 : d.deg)).refreshItems();
			getAndIncrementCharge(p, d, true);
			p.getUpdateFlag().flag(Flag.APPEARANCE);
			String ext = !degradeCompletely ? "degraded slightly" : "turned into dust";
			p.getPacketSender().sendMessage("Your "+ItemDefinition.forId(equipId).getName().replace(" (deg)", "")+" has "+ext+"!");
			return true;
		} else {
			return false;
		}
	}

	public static int getAndIncrementCharge(Player p, DegradingItem d, boolean reset) {
		switch(d) {
		case BRAWLING_GLOVES_COOKING:
		case BRAWLING_GLOVES_FIREMAKING:
		case BRAWLING_GLOVES_FISHING:
		case BRAWLING_GLOVES_HUNTER:
		case BRAWLING_GLOVES_MINING:
		case BRAWLING_GLOVES_PRAYER:
		case BRAWLING_GLOVES_SMITHING:
		case BRAWLING_GLOVES_THIEVING:
		case BRAWLING_GLOVES_WOODCUTTING:
			int index = d.ordinal() - 1;
			if(reset) {
				return p.getBrawlerChargers()[index] = 0;
			} else {
				return p.getBrawlerChargers()[index]++;
			}
		case RING_OF_RECOIL:
			if(reset) {
				return p.setRecoilCharges(0);
			} else {
				return p.setRecoilCharges(p.getRecoilCharges() + 1);
			}
		}
		return d.degradingCharges;
	}

	public enum DegradingItem {
		RING_OF_RECOIL(2550, 2550, Equipment.RING_SLOT, 100),
		BRAWLING_GLOVES_SMITHING(13855, 13855, Equipment.HANDS_SLOT, 600),
		BRAWLING_GLOVES_PRAYER(13848, 13848, Equipment.HANDS_SLOT, 600),
		BRAWLING_GLOVES_COOKING(13857, 13857, Equipment.HANDS_SLOT, 600),
		BRAWLING_GLOVES_FISHING(13856, 13856, Equipment.HANDS_SLOT, 600),
		BRAWLING_GLOVES_THIEVING(13854, 13854, Equipment.HANDS_SLOT, 600),
		BRAWLING_GLOVES_HUNTER(13853, 13853, Equipment.HANDS_SLOT, 600),
		BRAWLING_GLOVES_MINING(13852, 13852, Equipment.HANDS_SLOT, 600),
		BRAWLING_GLOVES_FIREMAKING(13851, 13851, Equipment.HANDS_SLOT, 600),
		BRAWLING_GLOVES_WOODCUTTING(13850, 13850, Equipment.HANDS_SLOT, 600);

		DegradingItem(int nonDeg, int deg, int equipSlot, int degradingCharges) {
			this.nonDeg = nonDeg;
			this.deg = deg;
			this.equipSlot = equipSlot;
			this.degradingCharges = degradingCharges;
		}

		private int nonDeg, deg;
		private int equipSlot;
		private int degradingCharges;
		
		public static DegradingItem forNonDeg(int item) {
			for(DegradingItem d : DegradingItem.values()) {
				if(d.nonDeg == item) {
					return d;
				}
			}
			return null;
		}
	}
}
