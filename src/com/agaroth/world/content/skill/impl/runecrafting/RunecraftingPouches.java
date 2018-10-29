package com.agaroth.world.content.skill.impl.runecrafting;

import com.agaroth.world.entity.impl.player.Player;

public class RunecraftingPouches
{
  public static enum RunecraftingPouch
  {
    SMALL(5509, 7, 7),
    MEDIUM_POUCH(5510, 16, 16),
    LARGE_POUCH(5512, 28, 28);
    
    private int id;
    private int maxRuneEss;
    private int maxPureEss;
    
    private RunecraftingPouch(int id, int maxRuneEss, int maxPureEss)
    {
      this.id = id;
      this.maxRuneEss = maxRuneEss;
      this.maxPureEss = maxPureEss;
    }
    
    public static RunecraftingPouch forId(int id)
    {
      RunecraftingPouch[] arrayOfRunecraftingPouch;
      int j = (arrayOfRunecraftingPouch = values()).length;
      for (int i = 0; i < j; i++)
      {
        RunecraftingPouch pouch = arrayOfRunecraftingPouch[i];
        if (pouch.id == id) {
          return pouch;
        }
      }
      return null;
    }
  }
  
  public static void fill(Player p, RunecraftingPouch pouch)
  {
    if (p.getInterfaceId() > 0)
    {
      p.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
      return;
    }
    int rEss = p.getInventory().getAmount(1436);
    int pEss = p.getInventory().getAmount(7936);
    if ((rEss == 0) && (pEss == 0))
    {
      p.getPacketSender().sendMessage("You do not have any essence in your inventory.");
      return;
    }
    rEss = rEss > pouch.maxRuneEss ? pouch.maxRuneEss : rEss;
    pEss = pEss > pouch.maxPureEss ? pouch.maxPureEss : pEss;
    int stored = 0;
    if (p.getStoredRuneEssence() >= pouch.maxRuneEss) {
      p.getPacketSender().sendMessage("Your pouch can not hold any more Rune essence.");
    }
    if (p.getStoredPureEssence() >= pouch.maxPureEss) {
      p.getPacketSender().sendMessage("Your pouch can not hold any more Pure essence.");
    }
    do
    {
      p.getInventory().delete(1436, 1);
      p.setStoredRuneEssence(p.getStoredRuneEssence() + 1);
      stored++;
      if ((rEss <= 0) || (p.getStoredRuneEssence() >= pouch.maxRuneEss)) {
        break;
      }
    } while (p.getInventory().contains(1436));
    while ((pEss > 0) && (p.getStoredPureEssence() < pouch.maxPureEss) && (p.getInventory().contains(7936)))
    {
      p.getInventory().delete(7936, 1);
      p.setStoredPureEssence(p.getStoredPureEssence() + 1);
      stored++;
    }
    if (stored > 0) {
      p.getPacketSender().sendMessage("You fill your pouch with " + stored + " essence..");
    }
  }
  
  public static void empty(Player p, RunecraftingPouch pouch)
  {
    if (p.getInterfaceId() > 0)
    {
      p.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
    }
    else
    {
      do
      {
        p.getInventory().add(1436, 1);
        p.setStoredRuneEssence(p.getStoredRuneEssence() - 1);
        if (p.getStoredRuneEssence() <= 0) {
          break;
        }
      } while (p.getInventory().getFreeSlots() > 0);
      while ((p.getStoredPureEssence() > 0) && (p.getInventory().getFreeSlots() > 0))
      {
        p.getInventory().add(7936, 1);
        p.setStoredPureEssence(p.getStoredPureEssence() - 1);
      }
    }
  }
  
  public static void check(Player p, RunecraftingPouch pouch)
  {
    p.getPacketSender().sendMessage("Your pouch currently contains " + p.getStoredRuneEssence() + "/" + pouch.maxRuneEss + " Rune essence and " + p.getStoredPureEssence() + "/" + pouch.maxPureEss + " Pure essence.");
  }
}
