package com.agaroth.world.content.skill.impl.farming;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Item;
import com.agaroth.model.Skill;
import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.util.Misc;
import com.agaroth.world.content.Achievements;
import com.agaroth.world.entity.impl.player.Player;
import java.util.Calendar;

public class Plant
{
  public int patch;
  public int plant;
  public int minute;
  public int hour;
  public int day;
  public int year;
  public byte stage = 0;
  public byte disease = -1;
  public byte watered = 0;
  private boolean dead = false;
  public byte harvested = 0;
  boolean harvesting = false;
  
  public Plant(int patchId, int plantId)
  {
    this.patch = patchId;
    this.plant = plantId;
  }
  
  public boolean doDisease()
  {
    return false;
  }
  
  public boolean doWater()
  {
    return false;
  }
  
  public void water(Player player, int item)
  {
    if (item == 5332) {
      return;
    }
    if (player.getClickDelay().elapsed(2000L))
    {
      if (isWatered())
      {
        player.getPacketSender().sendMessage("Your plants have already been watered.");
        return;
      }
      if (item == 5331)
      {
        player.getPacketSender().sendMessage("Your watering can is empty.");
        return;
      }
      player.getPacketSender().sendMessage("You water the plant.");
      player.getFarming().nextWateringCan(item);
      player.performAnimation(new Animation(2293));
      this.watered = -1;
      doConfig(player);
      player.getClickDelay().reset();
    }
  }
  
  public void setTime()
  {
    this.minute = Calendar.getInstance().get(12);
    this.hour = Calendar.getInstance().get(11);
    this.day = Calendar.getInstance().get(6);
    this.year = Calendar.getInstance().get(1);
  }
  
  public void click(Player player, int option)
  {
    if (option == 1)
    {
      if (this.dead)
      {
        player.getPacketSender().sendMessage("Oh dear, your plants have died!");
      }
      else if (isDiseased())
      {
        player.getPacketSender().sendMessage("Your plants are diseased!");
      }
      else if (this.stage == Plants.values()[this.plant].stages)
      {
        harvest(player);
      }
      else
      {
        String s = "Your plants are healthy";
        if (!isWatered()) {
          s = s + " but need some water to survive";
        } else {
          s = s + " and are currently growing";
        }
        s = s + ".";
        player.getPacketSender().sendMessage(s);
      }
    }
    else if ((option == 2) && (this.stage == Plants.values()[this.plant].stages)) {
      player.getPacketSender().sendMessage("Your plants are healthy and ready to harvest.");
    }
  }
  
  public void harvest(final Player player)
  {
    if (this.harvesting) {
      return;
    }
    if (player.getInventory().contains(FarmingPatches.values()[this.patch].harvestItem))
    {
      final Plant instance = this;
      player.performAnimation(new Animation(FarmingPatches.values()[this.patch].harvestAnimation));
      this.harvesting = true;
      TaskManager.submit(new Task(1, player, true)
      {
    	  @Override
        public void execute()
        {
          if (player.getMovementQueue().isMoving())
          {
            stop();
            return;
          }
          if (player.getInventory().getFreeSlots() == 0)
          {
            player.getInventory().full();
            stop();
            return;
          }
          player.performAnimation(new Animation(FarmingPatches.values()[Plant.this.patch].harvestAnimation));
          Item add = null;
          int id = Plants.values()[Plant.this.plant].harvest;
          add = ItemDefinition.forId(id).isNoted() ? new Item(id - 1, 1) : new Item(id, 1);
          player.getInventory().add(add.getId(), add.getAmount());
          String name = ItemDefinition.forId(Plants.values()[Plant.this.plant].harvest).getName();
          if (name.endsWith("s")) {
            name = name.substring(0, name.length() - 1);
          }
          player.getPacketSender().sendMessage("You harvest " + Misc.anOrA(name) + " " + name + ".");
          player.getSkillManager().addExperience(Skill.FARMING, (int)Plants.values()[Plant.this.plant].harvestExperience);
          Achievements.finishAchievement(player, Achievements.AchievementData.HARVEST_A_CROP);
          if ((Plant.this.harvested == 3) && (player.getInventory().contains(18336)) && (Misc.getRandom(4) == 0))
          {
            player.getPacketSender().sendMessage("You receive a seed back from your Scroll of life.");
            player.getInventory().add(Plants.values()[Plant.this.plant].seed, 1);
          }
          if (id == 219)
          {
            Achievements.doProgress(player, Achievements.AchievementData.HARVEST_10_TORSTOLS);
            Achievements.doProgress(player, Achievements.AchievementData.HARVEST_1000_TORSTOLS);
          }
          Plant tmp374_371 = Plant.this;tmp374_371.harvested = ((byte)(tmp374_371.harvested + 1));
          if ((Plant.this.harvested >= 3) && (Misc.getRandom(4) <= 1))
          {
            player.getFarming().remove(instance);
            stop();
            return;
          }
        }
    	  @Override
        public void stop()
        {
          Plant.this.harvesting = false;
          setEventRunning(false);
          player.performAnimation(new Animation(65535));
        }
      });
    }
    else
    {
      String name = ItemDefinition.forId(FarmingPatches.values()[this.patch].harvestItem).getName();
      player.getPacketSender().sendMessage("You need " + Misc.anOrA(name) + " " + name + " to harvest these plants.");
    }
  }
  
  public boolean useItemOnPlant(final Player player, int item)
  {
    if (item == 952)
    {
      player.performAnimation(new Animation(830));
      player.getFarming().remove(this);
      TaskManager.submit(new Task(2, player, false)
      {
    	  @Override
        public void execute()
        {
          player.getPacketSender().sendMessage("You remove your plants from the plot.");
          player.performAnimation(new Animation(65535));
          stop();
        }
      });
      return true;
    }
    if (item == 6036)
    {
      if (this.dead)
      {
        player.getPacketSender().sendMessage("Your plant is dead!");
      }
      else if (isDiseased())
      {
        player.getPacketSender().sendMessage("You cure the plant.");
        player.performAnimation(new Animation(2288));
        player.getInventory().delete(6036, 1);
        this.disease = -1;
        doConfig(player);
      }
      else
      {
        player.getPacketSender().sendMessage("Your plant does not need this.");
      }
      return true;
    }
    if ((item >= 5331) && (item <= 5340))
    {
      water(player, item);
      return true;
    }
    return false;
  }
  
  public void process(Player player)
  {
    if ((this.dead) || (this.stage >= Plants.values()[this.plant].stages)) {
      return;
    }
    int elapsed = Misc.getMinutesElapsed(this.minute, this.hour, this.day, this.year) * 6;
    int grow = Plants.values()[this.plant].minutes;
    if (elapsed >= grow)
    {
      for (int i = 0; i < elapsed / grow; i++) {
        if (isWatered())
        {
          this.stage = ((byte)(this.stage + 1));
          player.getFarming().doConfig();
          if (this.stage >= Plants.values()[this.plant].stages)
          {
            player.getPacketSender().sendMessage("<img=11> <shad=996699>A seed which you planted has finished growing!");
            return;
          }
        }
      }
      setTime();
    }
  }
  
  public void doConfig(Player player)
  {
    player.getFarming().doConfig();
  }
  
  public int getConfig()
  {
    if ((Plants.values()[this.plant].type == SeedType.ALLOTMENT) && (this.stage == 0) && (isWatered())) {
      return (Plants.values()[this.plant].healthy + this.stage + 64) * FarmingPatches.values()[this.patch].mod;
    }
    return (Plants.values()[this.plant].healthy + this.stage) * FarmingPatches.values()[this.patch].mod;
  }
  
  public FarmingPatches getPatch()
  {
    return FarmingPatches.values()[this.patch];
  }
  
  public boolean isDiseased()
  {
    return this.disease > -1;
  }
  
  public boolean isWatered()
  {
    return this.watered == -1;
  }
}
