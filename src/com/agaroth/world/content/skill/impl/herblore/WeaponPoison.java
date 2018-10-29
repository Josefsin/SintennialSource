package com.agaroth.world.content.skill.impl.herblore;

import java.util.HashMap;

import com.agaroth.model.container.impl.Equipment;
import com.agaroth.util.Misc;
import com.agaroth.world.content.combat.CombatFactory;
import com.agaroth.world.content.combat.effect.CombatPoisonEffect.PoisonType;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.player.Player;

public class WeaponPoison {

	private enum Weapon {

		DRAGON_DAGGER(1215, new int[][] { { 5940, 5698 }, { 5937, 5680 } }),
		RUNE_DAGGER(1213, new int[][] { { 5940, 5696 }, { 5937, 5678 } }),
		ADAMANT_DAGGER(1211, new int[][] { { 5940, 5694 }, { 5937, 5676 } }),
		MITHRIL_DAGGER(1209, new int[][] { { 5940, 5692 }, { 5937, 5674 } }),
		BLACK_DAGGER(1217, new int[][] { { 5940, 5700 }, { 5937, 5682 } }),
		STEEL_DAGGER(1207, new int[][] { { 5940, 5690 }, { 5937, 5672 } }),
		IRON_DAGGER(1203, new int[][] { { 5940, 5686 }, { 5937, 5668 } }),
		BRONZE_DAGGER(1205, new int[][] { { 5940, 5688 }, { 5937, 5670 } });
		
		private Weapon(int itemId, int[][] newItemId) {
			this.itemId = itemId;
			this.newItemId = newItemId;
		}

		public int getItemId() {
			return itemId;
		}

		private int itemId;
		private int[][] newItemId;
		public static HashMap<Integer, Weapon> weapon = new HashMap<Integer, Weapon>();

		@SuppressWarnings("unused")
		public static Weapon forId(int id) {
			return weapon.get(id);
		}

		public int[][] getNewItemId() {
			return newItemId;
		}

		static {
			for (Weapon w : Weapon.values())

				weapon.put(w.getItemId(), w);

		}
	}

	public static void execute(final Player player, int itemUse, int useWith) {
		final Weapon weapon = Weapon.weapon.get(useWith);
		if (weapon != null) {
			for (int element[] : weapon.getNewItemId())
				if (itemUse == element[0] && player.getInventory().contains(itemUse)) {
					player.getPacketSender().sendMessage("You poison your weapon..");
					player.getInventory().delete(element[0], 1);
					player.getInventory().delete(weapon.getItemId(), 1);
					player.getInventory().add(229, 1);
					player.getInventory().add(element[1], 1);
				}
		}
	}

	public static void handleWeaponPoison(Player p, Character target) {
		int plrWeapon = p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
		for(Weapon w : Weapon.weapon.values()) {
			if(w != null) {
				int random = 0;
				if(w.getNewItemId()[0][1] == plrWeapon) //Player has p++
					random = 5;
				else if(w.getNewItemId()[1][1] == plrWeapon) //Player has p+
					random = 10;
				if(random > 0) {
					if(Misc.getRandom(random) == 1)
						CombatFactory.poisonEntity(target, random == 5 ? PoisonType.EXTRA : PoisonType.MILD);
					break;
				}
			}
		}
	}
}
