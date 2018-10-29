package com.agaroth.world.content;

import com.agaroth.model.Item;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.entity.impl.player.Player;

/**
 * 
 * @author Nick Hartskeerl <apachenick@hotmail.com>
 *
 */
public final class VoteReward {

	private VoteReward() {
		throw new UnsupportedOperationException();
	}

	public static final int REWARD_BOOK_ID = 20935;

	private static final Item[] REWARDS = { new Item(15273, 25),
			new Item(200, 100), new Item(202, 100), new Item(204, 100),
			new Item(206, 100), new Item(208, 25), new Item(210, 100),
			new Item(212, 100), new Item(214, 50), new Item(216, 100),
			new Item(218, 50), new Item(220, 25), new Item(565, 100),
			new Item(560, 100), new Item(9075, 100), new Item(557, 100),
			new Item(555, 100), new Item(4716, 1), new Item(4718, 1),
			new Item(4720, 1), new Item(4722, 1), new Item(4732, 1),
			new Item(4734, 1), new Item(4736, 1), new Item(4738, 1),
			new Item(4745, 1), new Item(4747, 1), new Item(4749, 1),
			new Item(4751, 1), new Item(4708, 1), new Item(4710, 1),
			new Item(4712, 1), new Item(4714, 1), new Item(4151, 1),
			new Item(15486, 1) };

	private static final Item[] RARE_REWARDS = { new Item(14484, 1),//1-200
			new Item(11694, 1), new Item(11286, 1), new Item(19335, 1), 
	        new Item(11728, 1), new Item(11724, 1), new Item(11726, 1) };
    
	      
	private static final Item[] ALMOSTVERYRARE_REWARDS = {//1-500
			new Item(14008, 1), new Item(14009, 1),
	        new Item(14010, 1), new Item(14011, 1), new Item(14012, 1),
	        new Item(14013, 1), new Item(14014, 1), new Item(14015, 1),
	        new Item(14016, 1), new Item(10344, 1)
	};
	
	private static final Item[] VERYRARE_REWARDS = {  //1-2000
			new Item(1038, 1),
			new Item(1040, 1),  new Item(1042, 1),  new Item(1044, 1),
	        new Item(1046, 1),  new Item(1048, 1),  new Item(21651, 1),
	        new Item(21652, 1),  new Item(21653, 1),  new Item(21654, 1),
	        new Item(1050, 1), new Item(1053, 1), new Item(1055, 1),
	        new Item(1057, 1), new Item(21655, 1), new Item(21638, 1), 
	        new Item(21639, 1), new Item(21640, 1), new Item(21641, 1),
	        new Item(21642, 1), new Item(21643, 1), new Item(21644, 1),
	        new Item(21645, 1), new Item(21646, 1), new Item(21647, 1),
	        new Item(21648, 1), new Item(21649, 1), new Item(21650, 1),
	        new Item(10348, 1), new Item(10350, 1), new Item(10352, 1),
	        new Item(10346, 1)};
	        
	public static boolean open(Player player, int id) {
		
		if (id != REWARD_BOOK_ID) {
			return false;
		}
		
		if (!player.getInventory().contains(id)) {
			return false;
		}
		
		if (!player.getInventory().contains(995) && player.getInventory().getFreeSlots() == 0) {
			player.getPacketSender().sendMessage("You need to have at least one free inventory slot to open this.");
			return false;
		}

		int random = Misc.inclusiveRandom(500);
		double percent = random >> 2;
		boolean rare = Misc.inclusiveRandom(200) == 1;
		boolean almostVeryRare = Misc.inclusiveRandom(500) == 1;
		boolean veryRare = Misc.inclusiveRandom(2000) == 1;
		
		boolean showMessage = veryRare || almostVeryRare || rare;
		Item[] rewards = veryRare ? VERYRARE_REWARDS : (almostVeryRare ? ALMOSTVERYRARE_REWARDS : (rare ? RARE_REWARDS : REWARDS));
		random = Misc.inclusiveRandom(rewards.length);
		Item reward = rewards[random == 0 ? 0 : random - 1];

		int slot = player.getInventory().getSlot(id);

		assert slot != -1;

		player.getInventory().delete(new Item(id), slot);
		player.getInventory().add(reward, true);

		int votePoints = 1;
		
		switch(player.getRights()) {
		case DONATOR:
			votePoints = 2;
			break;
		case SUPER_DONATOR:
			votePoints = 3;
			break;
		case EXTREME_DONATOR:
			votePoints = 3;
			break;
		case LEGENDARY_DONATOR:
			votePoints = 4;
			break;
		case UBER_DONATOR:
			votePoints = 5;
			break;
		}
		
		player.getPointsHandler().votingPoints += votePoints;

		player.getPacketSender().sendMessage("You have received a " + reward.getDefinition().getName() + " from the reward book!");
		player.getPacketSender().sendMessage("You have received " + votePoints + " voting points.");
		
		if(showMessage) {
		
			for(Player other : World.getPlayers()) {
				
				if(other == null) {
					continue;
				}
				
				other.getPacketSender().sendMessage("<col=6600CC>"+player.getUsername()+" has received a "+reward.getDefinition().getName().toLowerCase()+" from voting!");
				
			}

		}
		
		return true;
		
	}

}
