package com.agaroth.world.entity;

import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.GameObject;
import com.agaroth.net.PlayerSession;
import com.agaroth.net.SessionState;
import com.agaroth.world.World;
import com.agaroth.world.clip.region.RegionClipping;
import com.agaroth.world.content.CustomObjects;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class EntityHandler {

	public static void register(Entity entity) {
		if(entity.isPlayer()) {
			Player player = (Player) entity;
			PlayerSession session = player.getSession();
			if (session.getState() == SessionState.LOGGING_IN && !World.getLoginQueue().contains(player)) {
				World.getLoginQueue().add(player);
			}
		} if(entity.isNpc()) {
			NPC npc = (NPC) entity;
			World.getNpcs().add(npc);
		} else if(entity.isGameObject()) {
			GameObject gameObject = (GameObject) entity;
			RegionClipping.addObject(gameObject);
			CustomObjects.spawnGlobalObjectWithinDistance(gameObject);
		}
	}

	public static void deregister(Entity entity) {
		if(entity.isPlayer()) {
			Player player = (Player)entity;
			World.getPlayers().remove(player);
		} else if(entity.isNpc()) {
			NPC npc = (NPC)entity;
			TaskManager.cancelTasks(npc.getCombatBuilder());
			TaskManager.cancelTasks(entity);
			World.getNpcs().remove(npc);
		} else if(entity.isGameObject()) {
			GameObject gameObject = (GameObject) entity;
			RegionClipping.removeObject(gameObject);
			CustomObjects.deleteGlobalObjectWithinDistance(gameObject);
		}
	}
}
