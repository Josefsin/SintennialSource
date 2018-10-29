package com.agaroth.world.content.skill.impl.runecrafting;

import com.agaroth.model.Skill;
import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.world.entity.impl.player.Player;

public class RunecraftingData
{
  public static enum RuneData
  {
	  AIR_RUNE(556, 1, 120, 26102, false),
	  MIND_RUNE(558, 2, 140, 26103, false),
	  WATER_RUNE(555, 5, 150, 26104, false),
	  EARTH_RUNE(557, 9, 170, 26105, false),
	  FIRE_RUNE(554, 14, 210, 26106, false),
	  BODY_RUNE(559, 20, 245, 26107, false),
	  COSMIC_RUNE(564, 27, 350, 26108, true),
	  CHAOS_RUNE(562, 35, 450, 26109, true),
	  ASTRAL_RUNE(9075, 40, 480, 26110, true),
	  NATURE_RUNE(561, 44, 510, 26111, true),
	  LAW_RUNE(563, 54, 612, 26112, true),
	  DEATH_RUNE(560, 65, 757, 26113, true),
	  BLOOD_RUNE(565, 77, 990, 26114, true),
	  SOUL_RUNE(566, 90, 1177, 26115, true),
	  ARMADYL_RUNE(21083, 99, 1810, 26116, true);
    
    private int runeID;
    private int levelReq;
    private int xpReward;
    private int altarObjectID;
    private boolean pureRequired;
    
    private RuneData(int rune, int levelReq, int xpReward, int altarObjectID, boolean pureRequired)
    {
      this.runeID = rune;
      this.levelReq = levelReq;
      this.xpReward = xpReward;
      this.altarObjectID = altarObjectID;
      this.pureRequired = pureRequired;
    }
    
    public int getRuneID()
    {
      return this.runeID;
    }
    
    public int getLevelRequirement()
    {
      return this.levelReq;
    }
    
    public int getXP()
    {
      return this.xpReward;
    }
    
    public int getAltarID()
    {
      return this.altarObjectID;
    }
    
    public boolean pureRequired()
    {
      return this.pureRequired;
    }
    
    public String getName()
    {
      return ItemDefinition.forId(this.runeID).getName();
    }
    
    public static RuneData forId(int objectId)
    {
      RuneData[] arrayOfRuneData;
      int j = (arrayOfRuneData = values()).length;
      for (int i = 0; i < j; i++)
      {
        RuneData runes = arrayOfRuneData[i];
        if (runes.getAltarID() == objectId) {
          return runes;
        }
      }
      return null;
    }
  }
  
  public static int getMakeAmount(RuneData rune, Player player) {
		int amount = 1;
		switch(rune) {
		case AIR_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 11)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 22)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 33)
				amount = 4;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 44)
				amount = 5;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 55)
				amount = 6;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 66)
				amount = 7;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 77)
				amount = 8;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 88)
				amount = 9;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 99)
				amount = 10;
			break;
		case ASTRAL_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 82)
				amount = 2;
			break;
		case BLOOD_RUNE:
			break;
		case BODY_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 46)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 92)
				amount = 3;
			break;
		case CHAOS_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 74)
				amount = 2;
			break;
		case COSMIC_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 59)
				amount = 2;
			break;
		case DEATH_RUNE:
			break;
		case EARTH_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 26)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 52)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 78)
				amount = 4;
			break;
		case FIRE_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 35)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 70)
				amount = 3;
			break;
		case LAW_RUNE:
			break;
		case MIND_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 14)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 28)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 42)
				amount = 4;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 56)
				amount = 5;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 70)
				amount = 6;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 84)
				amount = 7;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 98)
				amount = 8;
			break;
		case NATURE_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 91)
				amount = 2;
			break;
		case WATER_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 19)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 38)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 57)
				amount = 4;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 76)
				amount = 5;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 95)
				amount = 6;
			break;
		default:
			break;
		}
		return amount;
	}
}
