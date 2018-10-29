package com.agaroth.world.content.skill.impl.crafting;

import com.agaroth.model.Animation;
import com.agaroth.model.Skill;
import com.agaroth.world.entity.impl.player.Player;

public class GemTips
{
  static enum GEM_DATA
  {
    SAPPHIRE(1607, 9189, 56, 400, new Animation(888)),
    EMERALD(1605, 9190, 58, 800, new Animation(889)),
    RUBY(1603, 9191, 63, 1100, new Animation(892)),
    DIAMOND(1601, 9192, 65, 1350, new Animation(886)),
    DRAGONSTONE(1615, 9193, 71, 2500, new Animation(885));
    
    private int cutGem;
    private int boltTips;
    private int levelReq;
    private int xpReward;
    private Animation animation;
    
    private GEM_DATA(int cutGem, int boltTips, int levelReq, int xpReward, Animation animation)
    {
      this.cutGem = cutGem;
      this.boltTips = boltTips;
      this.levelReq = levelReq;
      this.xpReward = xpReward;
      this.animation = animation;
    }
    
    public int getCutGem()
    {
      return this.cutGem;
    }
    
    public int getboltTips()
    {
      return this.boltTips;
    }
    
    public int getLevelReq()
    {
      return this.levelReq;
    }
    
    public int getXpReward()
    {
      return this.xpReward;
    }
    
    public Animation getAnimation()
    {
      return this.animation;
    }
    
    public static GEM_DATA forcutGem(int cutGem)
    {
      GEM_DATA[] arrayOfGEM_DATA;
      int j = (arrayOfGEM_DATA = values()).length;
      for (int i = 0; i < j; i++)
      {
        GEM_DATA data = arrayOfGEM_DATA[i];
        if (data.getCutGem() == cutGem) {
          return data;
        }
      }
      return null;
    }
  }
  
  public static void makeBolt(Player player, int item1, int item2)
  {
    player.getSkillManager().stopSkilling();
    if (((item1 == 9189) && (item2 == 9142)) || ((item1 == 9142) && (item2 == 9189))) {
      if (player.getSkillManager().getCurrentLevel(Skill.FLETCHING) >= 56)
      {
        if ((player.getInventory().getAmount(item1) >= 12) && (player.getInventory().getAmount(item2) >= 12))
        {
          player.getInventory().delete(item1, 12);
          player.getInventory().delete(item2, 12);
          player.getInventory().add(9240, 12);
          player.getSkillManager().addExperience(Skill.FLETCHING, 1250);
        }
        else
        {
          player.getPacketSender().sendMessage("You must have at least 12 of each supply to make enchanted bolts.");
        }
      }
      else {
        player.getPacketSender().sendMessage("You need a Fletching level of at least 56 to fletch this.");
      }
    }
    if (((item1 == 9190) && (item2 == 9142)) || ((item1 == 9142) && (item2 == 9190))) {
      if (player.getSkillManager().getCurrentLevel(Skill.FLETCHING) >= 58)
      {
        if ((player.getInventory().getAmount(item1) >= 12) && (player.getInventory().getAmount(item2) >= 12))
        {
          player.getInventory().delete(item1, 12);
          player.getInventory().delete(item2, 12);
          player.getInventory().add(9241, 12);
          player.getSkillManager().addExperience(Skill.FLETCHING, 1500);
        }
        else
        {
          player.getPacketSender().sendMessage("You must have at least 12 of each supply to make enchanted bolts.");
        }
      }
      else {
        player.getPacketSender().sendMessage("You need a Fletching level of at least 58 to fletch this.");
      }
    }
    if (((item1 == 9191) && (item2 == 9143)) || ((item1 == 9143) && (item2 == 9191))) {
      if (player.getSkillManager().getCurrentLevel(Skill.FLETCHING) >= 63)
      {
        if ((player.getInventory().getAmount(item1) >= 12) && (player.getInventory().getAmount(item2) >= 12))
        {
          player.getInventory().delete(item1, 12);
          player.getInventory().delete(item2, 12);
          player.getInventory().add(9242, 12);
          player.getSkillManager().addExperience(Skill.FLETCHING, 2000);
        }
        else
        {
          player.getPacketSender().sendMessage("You must have at least 12 of each supply to make enchanted bolts.");
        }
      }
      else {
        player.getPacketSender().sendMessage("You need a Fletching level of at least 63 to fletch this.");
      }
    }
    if (((item1 == 9192) && (item2 == 9143)) || ((item1 == 9143) && (item2 == 9192))) {
      if (player.getSkillManager().getCurrentLevel(Skill.FLETCHING) >= 65)
      {
        if ((player.getInventory().getAmount(item1) >= 12) && (player.getInventory().getAmount(item2) >= 12))
        {
          player.getInventory().delete(item1, 12);
          player.getInventory().delete(item2, 12);
          player.getInventory().add(9243, 12);
          player.getSkillManager().addExperience(Skill.FLETCHING, 2250);
        }
        else
        {
          player.getPacketSender().sendMessage("You must have at least 12 of each supply to make enchanted bolts.");
        }
      }
      else {
        player.getPacketSender().sendMessage("You need a Fletching level of at least 65 to fletch this.");
      }
    }
    if (((item1 == 9193) && (item2 == 9144)) || ((item1 == 9144) && (item2 == 9193))) {
      if (player.getSkillManager().getCurrentLevel(Skill.FLETCHING) >= 71)
      {
        if ((player.getInventory().getAmount(item1) >= 12) && (player.getInventory().getAmount(item2) >= 12))
        {
          player.getInventory().delete(item1, 12);
          player.getInventory().delete(item2, 12);
          player.getInventory().add(9244, 12);
          player.getSkillManager().addExperience(Skill.FLETCHING, 2500);
        }
        else
        {
          player.getPacketSender().sendMessage("You must have at least 12 of each supply to make enchanted bolts.");
        }
      }
      else {
        player.getPacketSender().sendMessage("You need a Fletching level of at least 71 to fletch this.");
      }
    }
  }
  
  public static void cutGem(Player player, int cutGem)
  {
    player.getPacketSender().sendInterfaceRemoval();
    player.getSkillManager().stopSkilling();
    GEM_DATA data = GEM_DATA.forcutGem(cutGem);
    if (data == null) {
      return;
    }
    if (!player.getInventory().contains(cutGem)) {
      return;
    }
    player.performAnimation(data.getAnimation());
    player.getInventory().delete(cutGem, 1);
    player.getInventory().add(data.getboltTips(), 12);
    player.getSkillManager().addExperience(Skill.FLETCHING, data.getXpReward());
  }
}
