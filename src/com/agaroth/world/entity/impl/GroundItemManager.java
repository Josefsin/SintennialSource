package com.agaroth.world.entity.impl;

import com.agaroth.engine.task.impl.GroundItemsTask;
import com.agaroth.model.GameMode;
import com.agaroth.model.GroundItem;
import com.agaroth.model.Item;
import com.agaroth.model.Locations.Location;
import com.agaroth.model.Position;
import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.world.World;
import com.agaroth.world.content.Sounds;
import com.agaroth.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.agaroth.world.entity.impl.player.Player;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroundItemManager
{
  @SuppressWarnings({ "unchecked", "rawtypes" })
private static CopyOnWriteArrayList<GroundItem> groundItems = new CopyOnWriteArrayList();
  
  public static void remove(GroundItem groundItem, boolean delistGItem)
  {
    if (groundItem != null)
    {
      if (groundItem.isGlobal())
      {
        for (Player p : World.getPlayers()) {
          if (p != null) {
            if (p.getPosition().distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120.0D) {
              p.getPacketSender().removeGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(), groundItem.getPosition().getY(), groundItem.getItem().getAmount());
            }
          }
        }
      }
      else
      {
        Player person = World.getPlayerByName(groundItem.getOwner());
        if ((person != null) && (person.getPosition().distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120.0D)) {
          person.getPacketSender().removeGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(), groundItem.getPosition().getY(), groundItem.getItem().getAmount());
        }
      }
      if (delistGItem) {
        groundItems.remove(groundItem);
      }
    }
  }
  
  public static void spawnGroundItem(Player p, GroundItem g)
  {
    if (p == null) {
      return;
    }
    Item item = g.getItem();
    if ((item.getId() > ItemDefinition.getMaxAmountOfItems()) || (item.getId() <= 0)) {
      return;
    }
    if ((item.getId() >= 2412) && (item.getId() <= 2414))
    {
      p.getPacketSender().sendMessage("The cape vanishes as it touches the ground.");
      return;
    }
    if (Dungeoneering.doingDungeoneering(p))
    {
      g = new GroundItem(item, g.getPosition(), "Dungeoneering", true, -1, false, -1);
      p.getMinigameAttributes().getDungeoneeringAttributes().getParty().getGroundItems().add(g);
      if (item.getId() == 17489) {
        p.getMinigameAttributes().getDungeoneeringAttributes().getParty().setGatestonePosition(g.getPosition().copy());
      }
    }
    if (ItemDefinition.forId(item.getId()).isStackable())
    {
      GroundItem it = getGroundItem(p, item, g.getPosition());
      if (it != null)
      {
        it.getItem().setAmount(it.getItem().getAmount() + g.getItem().getAmount() > Integer.MAX_VALUE ? Integer.MAX_VALUE : it.getItem().getAmount() + g.getItem().getAmount());
        if (it.getItem().getAmount() <= 0) {
          remove(it, true);
        } else {
          it.setRefreshNeeded(true);
        }
        return;
      }
    }
    add(g, true);
  }
  
  public static void add(GroundItem groundItem, boolean listGItem)
  {
    if (groundItem.isGlobal())
    {
      for (Player p : World.getPlayers()) {
        if (p != null) {
          if ((groundItem.getPosition().getZ() == p.getPosition().getZ()) && (p.getPosition().distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120.0D)) {
            p.getPacketSender().createGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(), groundItem.getPosition().getY(), groundItem.getItem().getAmount());
          }
        }
      }
    }
    else
    {
      Player person = World.getPlayerByName(groundItem.getOwner());
      if ((person != null) && (groundItem.getPosition().getZ() == person.getPosition().getZ()) && (person.getPosition().distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120.0D)) {
        person.getPacketSender().createGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(), groundItem.getPosition().getY(), groundItem.getItem().getAmount());
      }
    }
    if (listGItem)
    {
      if (Location.getLocation(groundItem) == Location.DUNGEONEERING) {
        groundItem.setShouldProcess(false);
      }
      groundItems.add(groundItem);
      GroundItemsTask.fireTask();
    }
  }
  
  public static void pickupGroundItem(Player p, Item item, Position position)
  {
    if (!p.getLastItemPickup().elapsed(500L)) {
      return;
    }
    boolean canAddItem = (p.getInventory().getFreeSlots() > 0) || ((item.getDefinition().isStackable()) && (p.getInventory().contains(item.getId())));
    if (!canAddItem)
    {
      p.getInventory().full();
      return;
    }
    GroundItem gt = getGroundItem(p, item, position);
    if ((gt == null) || (gt.hasBeenPickedUp()) || (!groundItems.contains(gt))) {
      return;
    }
    if ((p.getGameMode() != GameMode.NORMAL) && (!Dungeoneering.doingDungeoneering(p)) && 
      (gt.getOwner() != null) && (!gt.getOwner().equals("null")) && (!gt.getOwner().equals(p.getUsername())))
    {
      p.getPacketSender().sendMessage("You cannot pick this item up because it was not spawned for you.");
      return;
    }
    if ((item.getId() == 17489) && (Dungeoneering.doingDungeoneering(p))) {
      p.getMinigameAttributes().getDungeoneeringAttributes().getParty().setGatestonePosition(null);
    }
    item = gt.getItem();
    gt.setPickedUp(true);
    remove(gt, true);
    p.getInventory().add(item);
    p.getLastItemPickup().reset();
    Sounds.sendSound(p, Sounds.Sound.PICKUP_ITEM);
  }
  
  public static void handleRegionChange(Player p)
  {
    for (GroundItem gi : groundItems) {
      if (gi != null) {
        p.getPacketSender().removeGroundItem(gi.getItem().getId(), gi.getPosition().getX(), gi.getPosition().getY(), gi.getItem().getAmount());
      }
    }
    for (GroundItem gi : groundItems) {
      if ((gi != null) && (p.getPosition().getZ() == gi.getPosition().getZ()) && (p.getPosition().distanceToPoint(gi.getPosition().getX(), gi.getPosition().getY()) <= 120.0D)) {
        if ((gi.isGlobal()) || ((!gi.isGlobal()) && (gi.getOwner().equals(p.getUsername())))) {
          p.getPacketSender().createGroundItem(gi.getItem().getId(), gi.getPosition().getX(), gi.getPosition().getY(), gi.getItem().getAmount());
        }
      }
    }
  }
  
  public static GroundItem getGroundItem(Player p, Item item, Position position)
  {
    for (GroundItem l : groundItems) {
      if ((l != null) && (l.getPosition().getZ() == position.getZ())) {
        if ((l.getPosition().equals(position)) && (l.getItem().getId() == item.getId()))
        {
          if (l.isGlobal()) {
            return l;
          }
          if (p != null)
          {
            Player owner = World.getPlayerByName(l.getOwner());
            if ((owner != null) && (owner.getIndex() == p.getIndex())) {
              return l;
            }
          }
        }
      }
    }
    return null;
  }
  
  public static void clearArea(Position pos, String owner)
  {
    for (GroundItem l : groundItems) {
      if ((l != null) && (l.getPosition().getZ() == pos.getZ())) {
        if ((l.getPosition().equals(pos)) && (l.getOwner().equals(owner))) {
          remove(l, true);
        }
      }
    }
  }
  
  public static CopyOnWriteArrayList<GroundItem> getGroundItems()
  {
    return groundItems;
  }
}