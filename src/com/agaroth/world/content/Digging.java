package com.agaroth.world.content;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Position;
import com.agaroth.world.entity.impl.player.Player;

public class Digging {
	
	public static void dig(final Player player) {
		if(!player.getClickDelay().elapsed(2000))
			return;
		player.getMovementQueue().reset();
		player.getPacketSender().sendMessage("You start digging..");
		player.performAnimation(new Animation(830));
		TaskManager.submit(new Task(2, player, false) {
			@Override
			public void execute() {
				Position targetPosition = null;
				if (inArea(player.getPosition(), 3553, 3301, 3561, 3294))
					targetPosition = new Position(3578, 9706, -1);
				else if (inArea(player.getPosition(), 3550, 3287, 3557, 3278))
					targetPosition = new Position(3568, 9683, -1);
				else if (inArea(player.getPosition(), 3561, 3292, 3568, 3285))
					targetPosition = new Position(3557, 9703, -1);
				else if (inArea(player.getPosition(), 3570, 3302, 3579, 3293))
					targetPosition = new Position(3556, 9718, -1);
				else if (inArea(player.getPosition(), 3571, 3285, 3582, 3278))
					targetPosition = new Position(3534, 9704, -1);
				else if (inArea(player.getPosition(), 3562, 3279, 3569, 3273))
					targetPosition = new Position(3546, 9684, -1);
				else if (inArea(player.getPosition(), 2986, 3370, 3013, 3388))
					targetPosition = new Position(3546, 9684, -1);
				if(targetPosition != null)
					player.moveTo(targetPosition);

             	if (player.getPosition().getX() == 2616 && player.getPosition().getY() == 3077 && player.getInventory().contains(2722)) {
                  player.getPacketSender().sendMessage("@red@ You find a Casket!");
                  player.getInventory().delete(2722, 1);
                  player.getInventory().add(2724, 1);
                 
              } else if (inOneSpot(player.getPosition(), 3109, 3152) && player.getInventory().contains(2723)) {
                  player.getPacketSender().sendMessage("@red@ You find a Casket!");
                  player.getInventory().delete(2723, 1);
                  player.getInventory().add(2724, 1);
                
              } else if (inOneSpot(player.getPosition(), 2459, 3180) && player.getInventory().contains(2725)) {
                  player.getPacketSender().sendMessage("@red@ You find a Casket!");
                  player.getInventory().delete(2725, 1);
                  player.getInventory().add(2724, 1);
                  
              } else if (inOneSpot(player.getPosition(), 2565, 3249) && player.getInventory().contains(2729)) {
                  player.getPacketSender().sendMessage("@red@ You find a Casket!");
                  player.getInventory().delete(2729, 1);
                  player.getInventory().add(2724, 1);
                 
              } else if (inOneSpot(player.getPosition(), 2970, 3414) && player.getInventory().contains(2731)) {
                  player.getPacketSender().sendMessage("@red@ You find a Casket!");
                  player.getInventory().delete(2731, 1);
                  player.getInventory().add(2724, 1);
                  
              } else if (inOneSpot(player.getPosition(), 3021, 3912) && player.getInventory().contains(2733)) {
                  player.getPacketSender().sendMessage("@red@ You find a Casket!");
                  player.getInventory().delete(2733, 1);
                  player.getInventory().add(2724, 1);
                  System.out.println("inOneSpot");
              }
				else
					player.getPacketSender().sendMessage("You find nothing of interest.");
				targetPosition = null;
				stop();
			}
		});
		player.getClickDelay().reset();
	}

	private static boolean inArea(Position pos, int x, int y, int x1, int y1) {
		return pos.getX() > x && pos.getX() < x1 && pos.getY() < y && pos.getY() > y1;
	}
	static boolean hasClue(Player player) {
    	if(player.getInventory().containsAny(2722, 2723, 2725, 2729, 2731, 2733));
    	return true;
    }

    private static boolean inOneSpot(Position pos, int x, int y) {
    	return pos.getX() == x && pos.getY() == y;
    	}
}
