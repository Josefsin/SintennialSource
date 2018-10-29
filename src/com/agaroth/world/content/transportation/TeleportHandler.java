package com.agaroth.world.content.transportation;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Locations.Location;
import com.agaroth.model.Position;
import com.agaroth.world.content.Kraken;
import com.agaroth.world.content.Sounds;
import com.agaroth.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.agaroth.world.entity.impl.player.Player;

public class TeleportHandler
{
  public static void teleportPlayer(final Player player, final Position targetLocation, final TeleportType teleportType) {
    if ((teleportType != TeleportType.LEVER) && 
      (!checkReqs(player, targetLocation))) {
      return;
    }
    if ((!player.getClickDelay().elapsed(4500)) || (player.getMovementQueue().isLockMovement())) {
      return;
    }
    player.setTeleporting(true).getMovementQueue().setLockMovement(true).reset();
    cancelCurrentActions(player);
    player.performAnimation(teleportType.getStartAnimation());
    player.performGraphic(teleportType.getStartGraphic());
    Sounds.sendSound(player, Sounds.Sound.TELEPORT);
    TaskManager.submit(new Task(1, player, true) {
      int tick = 0;
      public void execute() {
        switch (teleportType) {
        case RING_TELE: 
          if (this.tick == 0) {
            player.performAnimation(new Animation(2140));
          }
          else if (this.tick == 2) {
            player.performAnimation(new Animation(8939, 20));
            player.performGraphic(new Graphic(1576));
          }
          else if (this.tick == 4) {
            player.performAnimation(new Animation(8941));
            player.performGraphic(new Graphic(1577));
            player.moveTo(targetLocation).setPosition(targetLocation);
            player.getMovementQueue().setLockMovement(false).reset();
            stop();
          }
          break;
        default: 
          if (this.tick == teleportType.getStartTick()) {
            TeleportHandler.cancelCurrentActions(player);
            player.performAnimation(teleportType.getEndAnimation());
            player.performGraphic(teleportType.getEndGraphic());
            if (Dungeoneering.doingDungeoneering(player)) {
            	if(Dungeoneering.doingDungeoneering(player) && player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getGatestonePosition() != null) {
					player.moveTo(player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getGatestonePosition());
					player.setEntityInteraction(null);
					player.getPacketSender().sendMessage("You are teleported to your party's gatestone.");
					player.performGraphic(new Graphic(1310));
				} else
					player.getPacketSender().sendMessage("Your party must drop a Gatestone somewhere in the dungeon to use this teleport.");
            }
            else {
              player.moveTo(targetLocation).setPosition(targetLocation);
            }
            onArrival(player, targetLocation);
            player.setTeleporting(false);
          }
          else if (this.tick == teleportType.getStartTick() + 3) {
            player.getMovementQueue().setLockMovement(false).reset();
          }
          else if (this.tick == teleportType.getStartTick() + 4) {
            stop();
          }
          break;
        }
        this.tick = (this.tick + 1);
      }
      public void stop() {
        setEventRunning(false);
        player.setTeleporting(false);
        player.getClickDelay().reset(0L);
      }
    });
    player.getClickDelay().reset();
  }
  public static void onArrival(Player player, Position targetLocation) {
		if(targetLocation.getX() == 3683 && targetLocation.getY() == 9888) { //Kraken
			Kraken.enter(player);
		}
	}
  public static boolean interfaceOpen(Player player) {
    if ((player.getInterfaceId() > 0) && (player.getInterfaceId() != 50100)) {
      player.getPacketSender().sendMessage("Please close the interface you have open before opening another.");
      return true;
    }
    return false;
  }
  
  public static boolean checkReqs(Player player, Position targetLocation) {
    if (player.getConstitution() <= 0) {
      return false;
    }
    if (player.getTeleblockTimer() > 0) {
      player.getPacketSender().sendMessage("A magical spell is blocking you from teleporting.");
      return false;
    }
    if ((player.getLocation() != null) && (!player.getLocation().canTeleport(player))) {
      return false;
    }
    if ((player.isPlayerLocked()) || (player.isCrossingObstacle())) {
      player.getPacketSender().sendMessage("You cannot teleport right now.");
      return false;
    }
    return true;
  }
  
  public static boolean checkArea(Player player) {
    if (player.getConstitution() <= 0) {
      return false;
    }
    if ((player.getLocation() != null) && (!player.getLocation().canTeleport(player))) {
      return false;
    }
    if ((player.isPlayerLocked()) || (player.isCrossingObstacle())) {
      player.getPacketSender().sendMessage("You cannot teleport right now.");
      return false;
    }
    if (player.getLocation() == Location.DUNGEONEERING) {
      player.getPacketSender().sendMessage("Player standing in protected area.");
      return false;
    }
    if (player.getLocation() == Location.RECIPE_FOR_DISASTER) {
      player.getPacketSender().sendMessage("Player standing in protected area.");
      return false;
    }
    return true;
  }
  
  public static void cancelCurrentActions(Player player) {
    player.getPacketSender().sendInterfaceRemoval();
    player.setTeleporting(false);
    player.setWalkToTask(null);
    player.setInputHandling(null);
    player.getSkillManager().stopSkilling();
    player.setEntityInteraction(null);
    player.getMovementQueue().setFollowCharacter(null);
    player.getCombatBuilder().cooldown(false);
    player.setResting(false);
  }
}