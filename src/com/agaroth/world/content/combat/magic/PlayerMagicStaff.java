package com.agaroth.world.content.combat.magic;

import com.agaroth.model.Item;
import com.agaroth.model.definitions.WeaponInterfaces.WeaponInterface;
import com.agaroth.world.entity.impl.player.Player;

public enum PlayerMagicStaff {

    AIR(new int[] { 1381, 1397, 1405 }, new int[] { 556 }),
    WATER(new int[] { 1383, 1395, 1403 }, new int[] { 555 }),
    EARTH(new int[] { 1385, 1399, 1407 }, new int[] { 557 }),
    FIRE(new int[] { 1387, 1393, 1401 }, new int[] { 554 }),
    MUD(new int[] { 6562, 6563 }, new int[] { 555, 557 }),
    LAVA(new int[] { 3053, 3054 }, new int[] { 554, 557 });

    private int[] staves;
    private int[] runes;

    private PlayerMagicStaff(int[] itemIds, int[] runeIds) {
        this.staves = itemIds;
        this.runes = runeIds;
    }

    public static Item[] suppressRunes(Player player, Item[] runesRequired) {
        if (player.getWeapon() == WeaponInterface.STAFF) {
            for (PlayerMagicStaff m : values()) {
                if (player.getEquipment().containsAny(m.staves)) {
                    for (int id : m.runes) {
                        for (int i = 0; i < runesRequired.length; i++) {
                            if (runesRequired[i] != null && runesRequired[i].getId() == id) {
                                runesRequired[i] = null;
                            }
                        }
                    }
                }
            }
            return runesRequired;
        }
        return runesRequired;
    }
}