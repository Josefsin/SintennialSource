/*package com.agaroth.world.content.combat.range;

import com.agaroth.world.entity.impl.player.Player;

public class ToxicBlowpipe {
	
	public static boolean loadPipe(Player player, int dartType, int toxicBlowpipe){
		

		return false;
	}
	
	
} */
package com.agaroth.world.content.combat.range;
import com.agaroth.world.entity.impl.player.Player;

/*
 * Author @Tim
 */
public class ToxicBlowpipe {

	public static final int BLOWPIPE = 12926;
	public static final int BRONZE_DART = 806;
	public static final int IRON_DART = 807;
	public static final int STEEL_DART = 808;
	public static final int MITH_DART = 809;
	public static final int ADDY_DART = 810;
	public static final int RUNE_DART = 811;
	public static final int DRAGON_DART = 11230;

	public static void addDart(Player c, int usedId, int usedWith) {
		if ((c.dart != 0 && c.dartType == getDartType(usedId) || c.dartType == getDartType(usedWith)) || c.darts == 0) {
			if (usedWith == BRONZE_DART || usedId == BRONZE_DART) {
				handleDartCreation(c, "bronze");
			}
			if (usedWith == IRON_DART || usedId == IRON_DART) {
				handleDartCreation(c, "iron");
			}
			if (usedWith == STEEL_DART || usedId == STEEL_DART) {
				handleDartCreation(c, "steel");
			}
			if (usedWith == MITH_DART || usedId == MITH_DART) {
				handleDartCreation(c, "mith");
			}
			if (usedWith == ADDY_DART || usedId == ADDY_DART) {
				handleDartCreation(c, "addy");
			}
			if (usedWith == RUNE_DART || usedId == RUNE_DART) {
				handleDartCreation(c, "rune");
			}
			if (usedWith == DRAGON_DART || usedId == DRAGON_DART) {
				handleDartCreation(c, "dragon");
			}
		} else {
			c.getPacketSender().sendMessage("You must remove the current darts to add more!");
			return;
		}
	/*	if (c.dartType != 0 && c.darts != 0) {
			c.sendMessage("You must remove the current darts to add more!");
			return;
		} else {
			if (usedWith == BRONZE_DART || usedId == BRONZE_DART) {
				handleDartCreation(c, "bronze");
			}
			if (usedWith == IRON_DART || usedId == IRON_DART) {
				handleDartCreation(c, "iron");
			}
			if (usedWith == STEEL_DART || usedId == STEEL_DART) {
				handleDartCreation(c, "steel");
			}
			if (usedWith == MITH_DART || usedId == MITH_DART) {
				handleDartCreation(c, "mith");
			}
			if (usedWith == ADDY_DART || usedId == ADDY_DART) {
				handleDartCreation(c, "addy");
			}
			if (usedWith == RUNE_DART || usedId == RUNE_DART) {
				handleDartCreation(c, "rune");
			}
			if (usedWith == DRAGON_DART || usedId == DRAGON_DART) {
				handleDartCreation(c, "dragon");
			}
		}*/
	}
	
	public static final int getDartType(int itemId) {
		switch (itemId) {
		case BRONZE_DART:
			return 1;
		case IRON_DART:
			return 2;
		case STEEL_DART:
			return 3;
		case MITH_DART:
			return 4;
		case ADDY_DART:
			return 5;
		case RUNE_DART:
			return 6;
		case DRAGON_DART:
			return 7;
		}
		return 0;
	}

	public static void handleDartCreation(Player c, String type) {
		switch (type) {
		case "bronze":
			if (c.getInventory().contains(BRONZE_DART)
					&& c.getInventory().contains(BLOWPIPE)) {
				c.getPacketSender().sendMessage("You have added "
						+ c.getInventory().getAmount(BRONZE_DART)
						+ "x Bronze Darts to your Blowpipe.");
				c.darts += c.getInventory().getAmount(BRONZE_DART);
				c.getInventory().delete(BRONZE_DART,
						c.getInventory().getAmount(BRONZE_DART));

				c.dartType = 1;
			}
			break;
		case "iron":
			if (c.getInventory().contains(IRON_DART)
					&& c.getInventory().contains(BLOWPIPE)) {
				c.getPacketSender().sendMessage("You have added "
						+ c.getInventory().getAmount(IRON_DART)
						+ "x Iron Darts to your Blowpipe.");
				c.darts += c.getInventory().getAmount(IRON_DART);
				c.getInventory().delete(IRON_DART,
						c.getInventory().getAmount(IRON_DART));
				c.dartType = 2;
			}
			break;
		case "steel":
			if (c.getInventory().contains(STEEL_DART)
					&& c.getInventory().contains(BLOWPIPE)) {
				c.getPacketSender().sendMessage("You have added "
						+ c.getInventory().getAmount(STEEL_DART)
						+ "x Steel Darts to your Blowpipe.");
				c.darts += c.getInventory().getAmount(STEEL_DART);
				c.getInventory().delete(STEEL_DART,
						c.getInventory().getAmount(STEEL_DART));

				c.dartType = 3;
			}
			break;
		case "mith":
			if (c.getInventory().contains(MITH_DART)
					&& c.getInventory().contains(BLOWPIPE)) {
				c.getPacketSender().sendMessage("You have added "
						+ c.getInventory().getAmount(MITH_DART)
						+ "x Mith Darts to your Blowpipe.");
				c.darts += c.getInventory().getAmount(MITH_DART);
				c.getInventory().delete(MITH_DART,
						c.getInventory().getAmount(MITH_DART));

				c.dartType = 4;
			}
			break;
		case "addy":
			if (c.getInventory().contains(ADDY_DART)
					&& c.getInventory().contains(BLOWPIPE)) {
				c.getPacketSender().sendMessage("You have added "
						+ c.getInventory().getAmount(ADDY_DART)
						+ "x Addy Darts to your Blowpipe.");
				c.darts += c.getInventory().getAmount(ADDY_DART);
				c.getInventory().delete(ADDY_DART,
						c.getInventory().getAmount(ADDY_DART));

				c.dartType = 5;
			}
			break;
		case "rune":
			if (c.getInventory().contains(RUNE_DART)
					&& c.getInventory().contains(BLOWPIPE)) {
				c.getPacketSender().sendMessage("You have added "
						+ c.getInventory().getAmount(RUNE_DART)
						+ "x Rune Darts to your Blowpipe.");
				c.darts += c.getInventory().getAmount(RUNE_DART);
				c.getInventory().delete(RUNE_DART,
						c.getInventory().getAmount(RUNE_DART));

				c.dartType = 6;
			}
			break;
		case "dragon":
			if (c.getInventory().contains(DRAGON_DART)
					&& c.getInventory().contains(BLOWPIPE)) {
				c.getPacketSender().sendMessage("You have added "
						+ c.getInventory().getAmount(DRAGON_DART)
						+ "x Dragon Darts to your Blowpipe.");
				c.darts += c.getInventory().getAmount(DRAGON_DART);
				c.getInventory().delete(DRAGON_DART,
						c.getInventory().getAmount(DRAGON_DART));

				c.dartType = 7;
			}
			break;
		}
	}

	public static void handleOperate(Player c) {
		if (c.darts == 0) {
			c.getPacketSender().sendMessage("You current don't have any darts loaded!");
		} else if (c.dartType == 1) {
			c.getPacketSender().sendMessage("You currently have " + c.darts
					+ "x Bronze Darts loaded.");
		} else if (c.dartType == 2) {
			c.getPacketSender().sendMessage("You currently have " + c.darts
					+ "x Iron Darts loaded.");
		} else if (c.dartType == 3) {
			c.getPacketSender().sendMessage("You currently have " + c.darts
					+ "x Steel Darts loaded.");
		} else if (c.dartType == 4) {
			c.getPacketSender().sendMessage("You currently have " + c.darts
					+ "x Mith Darts loaded.");
		} else if (c.dartType == 5) {
			c.getPacketSender().sendMessage("You currently have " + c.darts
					+ "x Addy Darts loaded.");
		} else if (c.dartType == 6) {
			c.getPacketSender().sendMessage("You currently have " + c.darts
					+ "x Rune Darts loaded.");
		} else if (c.dartType == 7) {
			c.getPacketSender().sendMessage("You currently have " + c.darts
					+ "x Dragon Darts loaded.");
		}
	}
	
	public static final int getDart(Player c) {
		if (c.dartType == 1) {
			return BRONZE_DART;
		} else if (c.dartType == 2) {
			return IRON_DART;
		} else if (c.dartType == 3) {
			return STEEL_DART;
		} else if (c.dartType == 4) {
			return MITH_DART;
		} else if (c.dartType == 5) {
			return ADDY_DART;
		} else if (c.dartType == 6) {
			return RUNE_DART;
		} else if (c.dartType == 7) {
			return DRAGON_DART;
		}
		return -1;
	}
	
	public static final String getType(Player c) {
		if (c.dartType == 1) {
			return "Bronze Darts";
		} else if (c.dartType == 2) {
			return "Iron Darts";
		} else if (c.dartType == 3) {
			return "Steel Darts";
		} else if (c.dartType == 4) {
			return "Mith Darts";
		} else if (c.dartType == 5) {
			return "Addy Darts";
		} else if (c.dartType == 6) {
			return "Rune Darts";
		} else if (c.dartType == 7) {
			return"Dragon Darts";
		}
		return "";
	}
	
	public static void handleEmpty(Player c) {
		if (c.darts > 0 && c.dartType != 0) {
			if (c.getInventory().getFreeSlots() > 0) {
				c.getInventory().add(getDart(c), c.darts);
				c.getPacketSender().sendMessage("You have removed "+c.darts+"x "+getType(c)+" from your quiver.");
				c.darts = 0;
				c.dartType = 0;
			} else {
				c.getPacketSender().sendMessage("You don't have enough inventory space to do this.");
				return;
			}
		}
	}

}