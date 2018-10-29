package com.agaroth.world.content.randomevents;

import com.agaroth.model.Animation;
import com.agaroth.model.GameObject;
import com.agaroth.model.Position;
import com.agaroth.util.Misc;
import com.agaroth.util.Stopwatch;
import com.agaroth.world.World;
import com.agaroth.world.content.CustomObjects;
import com.agaroth.world.entity.impl.player.Player;

public class EvilTree {
	private static final int TIME = 1800000;
	public static final int MAXIMUM_CUT_AMOUNT = 800;
	
	private static Stopwatch timer = new Stopwatch().reset();
	public static SpawnEvilTree SPAWN_EVIL_TREE = null;
	private static LocationData LAST_LOCATION = null;
	
	public static class SpawnEvilTree {
		
		public SpawnEvilTree(GameObject treeObject, LocationData treeLocation) {
			this.treeObject = treeObject;
			this.treeLocation = treeLocation;
		}
		
		private GameObject treeObject;
		private LocationData treeLocation;
		
		public GameObject getTreeObject() {
			return treeObject;
		}
		
		public LocationData getTreeLocation() {
			return treeLocation;
		}
	}

	public static enum LocationData {
		LOCATION_1(new Position(3053, 3301), "south of the Falador Farming patches", "Farming"),
		LOCATION_2(new Position(3094, 3484), "south of the Edgeville bank", "Edgeville"),
		LOCATION_3(new Position(2480, 3433), "at the Gnome Agility Course", "Gnome Course"),
		LOCATION_4(new Position(2745, 3445), "in the middle of the Flax field", "Flax Field"),
		LOCATION_5(new Position(3363, 3270), "in the Duel Arena", "Duel Arena"),
		LOCATION_6(new Position(2594, 4326), "in Puro Puro", "Puro Puro"),
		LOCATION_7(new Position(2731, 5092), "in the Strykewyrm cavern", "Strykewyrms"),
		LOCATION_8(new Position(1746, 5327), "in the Ancient cavern", "Ancient Cavern"),
		LOCATION_9(new Position(2882, 9800), "in the Taverly dungeon", "Taverly Dung."),
		LOCATION_10(new Position(2666, 2648), "at the Void knight island", "Pest Control"),
		LOCATION_11(new Position(3566, 3297), "on the Barrows hills", "Barrows"),
		LOCATION_12(new Position(2986, 3599), "in the Wilderness (near the western dragons)", "West Dragons"),
		LOCATION_13(new Position(3664, 3493), "in the Wilderness (Ghost Town)", "Ghost Town"),
		LOCATION_18(new Position(2995, 3911), "outside the Wilderness Agility Course", "Wild. Course");

		private LocationData(Position spawnPos, String clue, String playerPanelFrame) {
			this.spawnPos = spawnPos;
			this.clue = clue;
			this.playerPanelFrame = playerPanelFrame;
		}

		private Position spawnPos;
		private String clue;
		public String playerPanelFrame;
	}

	public static LocationData getRandom() {
		LocationData tree = LocationData.values()[Misc.getRandom(LocationData.values().length - 1)];
		return tree;
	}

	public static void sequence() {
		if(SPAWN_EVIL_TREE == null) {
			if(timer.elapsed(TIME)) {
				LocationData locationData = getRandom();
				if(LAST_LOCATION != null) {
					if(locationData == LAST_LOCATION) {
						locationData = getRandom();
					}
				}
				LAST_LOCATION = locationData;
				SPAWN_EVIL_TREE = new SpawnEvilTree(new GameObject(11922, locationData.spawnPos), locationData);
				CustomObjects.spawnGlobalObject(SPAWN_EVIL_TREE.treeObject);
				World.sendMessage("<img=11> <shad=1><col=FF9933>An Evil Tree has sprouted "+locationData.clue+"!");
				World.getPlayers().forEach(p -> p.getPacketSender().sendString(39162, "@or2@Evil Tree: @yel@"+EvilTree.SPAWN_EVIL_TREE.getTreeLocation().playerPanelFrame+""));
				timer.reset();
			}
		} else {
			if(SPAWN_EVIL_TREE.treeObject.getPickAmount() >= MAXIMUM_CUT_AMOUNT) {
				despawn(false);
				timer.reset();
			}
		}
	}

	public static void despawn(boolean respawn) {
		if(respawn) {
			timer.reset(0);
		} else {
			timer.reset();
		}
		if(SPAWN_EVIL_TREE != null) {
			for(Player p : World.getPlayers()) {
				if(p == null) {
					continue;
				}
				p.getPacketSender().sendString(39162, "@or2@Evil Tree: @or2@[ @yel@N/A@or2@ ]");
				if(p.getInteractingObject() != null && p.getInteractingObject().getId() == SPAWN_EVIL_TREE.treeObject.getId()) {
					p.performAnimation(new Animation(65535));
					p.getPacketSender().sendClientRightClickRemoval();
					p.getSkillManager().stopSkilling();
					p.getPacketSender().sendMessage("The Evil Tree has been chopped completely.");
				}
			}
			CustomObjects.deleteGlobalObject(SPAWN_EVIL_TREE.treeObject);
			SPAWN_EVIL_TREE = null;
		}
	}
}
