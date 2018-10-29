package com.agaroth.world.content.skill.impl.smithing;

import java.util.stream.IntStream;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Item;
import com.agaroth.model.Skill;
import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.world.content.Sounds;
import com.agaroth.world.content.Sounds.Sound;
import com.agaroth.world.entity.impl.player.Player;

public class EquipmentMaking {
	
	public static void handleAnvil(Player player) {
		String bar = searchForBars(player);
		if(bar == null) {
			player.getPacketSender().sendMessage("You do not have any bars in your inventory to smith.");
			return;
		} else {
			switch(bar.toLowerCase()) {
			case "bronze bar":
				player.setSelectedSkillingItem(2349);
				SmithingData.showBronzeInterface(player);
				break;
			case "iron bar":
				player.setSelectedSkillingItem(2351);
				SmithingData.makeIronInterface(player);
				break;
			case "steel bar":
				player.setSelectedSkillingItem(2353);
				SmithingData.makeSteelInterface(player);
				break;
			case "mithril bar":
				player.setSelectedSkillingItem(2359);
				SmithingData.makeMithInterface(player);
				break;
			case "adamant bar":
				player.setSelectedSkillingItem(2361);
				SmithingData.makeAddyInterface(player);
				break;
			case "rune bar":
				player.setSelectedSkillingItem(2363);
				SmithingData.makeRuneInterface(player);
				break;
			}
		}
	}
	
	public static String searchForBars(Player player) {
		for(int bar : SmithingData.SMELT_BARS) {
			if(player.getInventory().contains(bar)) {
				return ItemDefinition.forId(bar).getName();
			}
		}
		return null;
	}
	
	public static void smithItem(final Player player, final Item bar, final Item itemToSmith, final int x) {
		int[] list = new int[] {1205, 1351, 1422, 1139, 9375, 1277, 4819, 1794, 819, 39, 1321, 1265, 1291, 9420, 1155, 864, 1173,
				1337, 1375, 1103, 1189, 3095, 1307, 1087, 1075, 1117, 1203, 15298, 1420, 7225, 1137, 9140, 1279, 4820, 820, 40, 1323, 1267,
				1293, 1153, 863, 1175, 9423, 1335, 1363, 1101, 4540, 1191, 3096, 1309, 1081, 1067, 1115, 1207, 1353, 1424, 1141, 9141, 1539,
				1281, 821, 41, 1325, 1269, 1295, 2370, 9425, 1157, 865, 1177, 1339, 1365, 1105, 1193, 3097, 1311, 1084, 1069, 1119, 1209,
				1355, 1428, 1143, 9142, 1285, 4822, 822, 42, 1329, 1273, 1299, 9427, 1159, 866, 1181, 1343, 9416, 1369, 1109, 1197, 3099,
				1315, 1085, 1071, 1121, 1211, 1357, 1430, 1145, 9143, 1287, 4823, 823, 43, 1331, 1271, 1301, 9429, 1161, 867, 1183, 1345, 1371,
				1111, 1199, 3100, 1317, 1091, 1073, 1123, 1213, 1359, 1432, 1147, 9144, 1289, 4824, 824, 44, 1333, 1275, 1303, 9431, 1163, 868,
				1185, 1347, 1373, 1113, 1201, 3101, 1319, 1093, 1079, 1127, 2};
		if(bar.getId() < 0)
			return;
		player.getSkillManager().stopSkilling();
		if(!player.getInventory().contains(2347)) {
			player.getPacketSender().sendMessage("You need a Hammer to smith items.");
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(player.getInventory().getAmount(bar.getId()) < bar.getAmount() || x <= 0) {
			player.getPacketSender().sendMessage("You do not have enough bars to smith this item.");
			return;
		}
		if(SmithingData.getData(itemToSmith, "reqLvl") > player.getSkillManager().getCurrentLevel(Skill.SMITHING)) {
			player.getPacketSender().sendMessage("You need a Smithing level of at least "+SmithingData.getData(itemToSmith, "reqLvl")+" to make this item.");
			return;
		}
		player.getPacketSender().sendInterfaceRemoval();
		player.setCurrentTask(new Task(3, player, true) {
			int amountMade = 0;
			@Override
			public void execute() {
				if(player.getInventory().getAmount(bar.getId()) < bar.getAmount() || !player.getInventory().contains(2347) || amountMade >= x) {
					this.stop();
					return;
				}
				if(player.getInteractingObject() != null)
					if (IntStream.of(list).anyMatch(id -> id == itemToSmith.getId()) || itemToSmith.getDefinition().getName().toLowerCase().contains("plateskirt")) {
						
					
					player.getInteractingObject().performGraphic(new Graphic(2123));
				player.performAnimation(new Animation(898));
				amountMade++;
				Sounds.sendSound(player, Sound.SMITH_ITEM);
				player.getInventory().delete(bar);
				player.getInventory().add(itemToSmith);
				player.getInventory().refreshItems();
				player.getSkillManager().addExperience(Skill.SMITHING, (int) (SmithingData.getData(itemToSmith, "xp") * 1.5));
					} else {
						player.getPacketSender().sendMessage("You have been caught using cheating software goodbye - "+itemToSmith.getId()+".");
						//PlayerPunishment.addBannedIP(player.getHostAddress());
				}
			}
		});
		TaskManager.submit(player.getCurrentTask());
	}
}