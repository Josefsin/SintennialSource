package com.agaroth.world.content.skill.impl.agility;

import com.agaroth.model.GameObject;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.model.Skill;
import com.agaroth.util.Misc;
import com.agaroth.world.content.Achievements;
import com.agaroth.world.entity.impl.player.Player;

public class Agility
{
	static void MemberBonus(Player player) {
	    int random = 0;
	  	if (player.getAmountDonated() >= 7 && player.getAmountDonated() <= 49) {
	  		random = Misc.getRandom(50);
	  		}
	  	if (player.getAmountDonated() >= 50 && player.getAmountDonated() <= 99) {
	  		random = Misc.getRandom(40);
	  		}
	  	if (player.getAmountDonated() >= 100 && player.getAmountDonated() <= 249) {
	  		random = Misc.getRandom(30);
	  		}
	  	if (player.getAmountDonated() >= 250 && player.getAmountDonated() <= 499) {
	  		random = Misc.getRandom(20);
	  		}
	  	if (player.getAmountDonated() >= 500) {
	  		random = Misc.getRandom(10);
	  		}
	    if (random <= 10 && player.getAmountDonated() >= 7) {
	      player.getInventory().add(2996, 1 + Misc.getRandom(2));
	      player.getPacketSender().sendMessage("@dre@You received extra agility ticket(s) as donator bonus.");
	    }
	  }
  public static boolean handleObject(Player p, GameObject object)
  {
    if ((object.getId() == 2309) && 
      (p.getSkillManager().getMaxLevel(Skill.AGILITY) < 55))
    {
      p.getPacketSender().sendMessage("You need an Agility level of at least 55 to enter this course.");
      return true;
    }
    if ((object.getId() == 9326) && (p.getPosition().getX() == 2775))
    {
      if (p.getSkillManager().getMaxLevel(Skill.AGILITY) < 70)
      {
        p.getPacketSender().sendMessage("You need an Agility level of at least 70 to pass this obsticle.");
        return true;
      }
      p.moveTo(new Position(2768, 10002));
      return true;
    }
    if ((object.getId() == 9326) && (p.getPosition().getX() == 2768))
    {
      if (p.getSkillManager().getMaxLevel(Skill.AGILITY) < 70)
      {
        p.getPacketSender().sendMessage("You need an Agility level of at least 70 to pass this obsticle.");
        return true;
      }
      p.moveTo(new Position(2775, 10003));
      return true;
    }
    if ((object.getId() == 9321) && (p.getPosition().getX() == 2735))
    {
      if (p.getSkillManager().getMaxLevel(Skill.AGILITY) < 85)
      {
        p.getPacketSender().sendMessage("You need an Agility level of at least 85 to pass this obsticle.");
        return true;
      }
      p.moveTo(new Position(2730, 10008));
      return true;
    }
    if ((object.getId() == 9321) && (p.getPosition().getX() == 2730))
    {
      if (p.getSkillManager().getMaxLevel(Skill.AGILITY) < 85)
      {
        p.getPacketSender().sendMessage("You need an Agility level of at least 85 to pass this obsticle.");
        return true;
      }
      p.moveTo(new Position(2735, 10008));
      return true;
    }
    ObstacleData agilityObject = ObstacleData.forId(object.getId());
    if (agilityObject != null)
    {
      if (p.isCrossingObstacle()) {
        return true;
      }
      p.setPositionToFace(object.getPosition());
      p.setResetPosition(p.getPosition());
      p.setCrossingObstacle(true);
      agilityObject.cross(p);
      Achievements.finishAchievement(p, Achievements.AchievementData.CLIMB_AN_AGILITY_OBSTACLE);
      Achievements.doProgress(p, Achievements.AchievementData.CLIMB_50_AGILITY_OBSTACLES);
    }
    return false;
  }
  
  public static boolean passedAllObstacles(Player player)
  {
    boolean[] arrayOfBoolean;
    int j = (arrayOfBoolean = player.getCrossedObstacles()).length;
    for (int i = 0; i < j; i++)
    {
      boolean crossedObstacle = arrayOfBoolean[i];
      if (!crossedObstacle) {
        return false;
      }
    }
    return true;
  }
  
  public static void resetProgress(Player player)
  {
    for (int i = 0; i < player.getCrossedObstacles().length; i++) {
      player.setCrossedObstacle(i, false);
    }
  }
  
  public static boolean isSucessive(Player player)
  {
    return Misc.getRandom(player.getSkillManager().getCurrentLevel(Skill.AGILITY) / 2) > 1;
  }
  
  public static void addExperience(Player player, int experience)
  {
    boolean agile = (player.getEquipment().get(4).getId() == 14936) && (player.getEquipment().get(7).getId() == 14938);
    player.getSkillManager().addExperience(Skill.AGILITY, agile ? (experience = (int)(experience * 1.5D)) : experience);
  }
}
