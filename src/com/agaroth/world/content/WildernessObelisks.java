package com.agaroth.world.content;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.GameObject;
import com.agaroth.model.Locations;
import com.agaroth.model.Position;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.entity.impl.player.Player;

public class WildernessObelisks {
	public static boolean handleObelisk(int objectId) {
		final int index = getObeliskIndex(objectId);
		if (index >= 0) {
			if (!OBELISK_ACTIVATED[index]) {
				OBELISK_ACTIVATED[index] = true;
        obelisks[0] = new GameObject(14825, new Position(OBELISK_COORDS[index][0], OBELISK_COORDS[index][1]));
        obelisks[1] = new GameObject(14825, new Position(OBELISK_COORDS[index][0] + 4, OBELISK_COORDS[index][1]));
        obelisks[2] = new GameObject(14825, new Position(OBELISK_COORDS[index][0], OBELISK_COORDS[index][1] + 4));
        obelisks[3] = new GameObject(14825, new Position(OBELISK_COORDS[index][0] + 4, OBELISK_COORDS[index][1] + 4));
        for (int i = 0; i < obelisks.length; i++)
        {
          int obeliskX = (i == 1) || (i == 3) ? OBELISK_COORDS[index][0] + 4 : OBELISK_COORDS[index][0];
          int obeliskY = i >= 2 ? OBELISK_COORDS[index][1] + 4 : OBELISK_COORDS[index][1];
          CustomObjects.globalObjectRespawnTask(obelisks[i], new GameObject(OBELISK_IDS[index], new Position(obeliskX, obeliskY)), 4);
        }
        
        TaskManager.submit(new Task(8, false)
        {
        	@Override
          public void execute()
          {
            WildernessObelisks.handleTeleport(index);
            stop();
          }
          
          @Override
			public void stop() {
				setEventRunning(false);
				OBELISK_ACTIVATED[index] = false;
			}
        });
      }
      return true;
    }
    return false;
  }
  
  public static void handleTeleport(int index)
  {
    int random = Misc.getRandom(5);
    while (random == index) {
      random = Misc.getRandom(5);
    }
    for (Player player : World.getPlayers()) {
      if ((player != null) && (player.getLocation() != null) && (player.getLocation() == Locations.Location.WILDERNESS))
      {
        if (player.getTeleblockTimer() > 0)
        {
          player.getPacketSender().sendMessage("A magical spell is blocking you from teleporting.");
          return;
        }
        if (Locations.goodDistance(player.getPosition().copy(), new Position(OBELISK_COORDS[index][0] + 2, OBELISK_COORDS[index][1] + 2), 1)) {
          player.moveTo(new Position(OBELISK_COORDS[random][0] + 2, OBELISK_COORDS[random][1] + 2));
        }
      }
    }
  }
  
  public static int getObeliskIndex(int id)
  {
    for (int j = 0; j < OBELISK_IDS.length; j++) {
      if (OBELISK_IDS[j] == id) {
        return j;
      }
    }
    return -1;
  }
  
  private static final int[] OBELISK_IDS = {
    14829, 14830, 
    14827, 14828, 
    14826, 14831 };
  public static final GameObject[] obelisks = new GameObject[4];
  private static final boolean[] OBELISK_ACTIVATED = new boolean[OBELISK_IDS.length];
  private static final int[][] OBELISK_COORDS = {
    { 3154, 3618 }, { 3225, 3665 }, 
    { 3033, 3730 }, { 3104, 3792 }, 
    { 2978, 3864 }, { 3305, 3914 } };
}
