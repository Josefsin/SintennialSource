package com.agaroth.world.content.skill.impl.prayer;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Skill;
import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.model.input.impl.EnterAmountOfBonesToSacrifice;
import com.agaroth.world.content.Achievements;
import com.agaroth.world.entity.impl.player.Player;

public class BonesOnAltar
{
  public static void openInterface(Player player, int itemId)
  {
    player.getSkillManager().stopSkilling();
    player.setSelectedSkillingItem(itemId);
    player.setInputHandling(new EnterAmountOfBonesToSacrifice());
    player.getPacketSender().sendString(2799, ItemDefinition.forId(itemId).getName()).sendInterfaceModel(1746, itemId, 150).sendChatboxInterface(4429);
    player.getPacketSender().sendString(2800, "How many would you like to offer?");
  }
  
  public static void offerBones(final Player player, final int amount)
  {
    final int boneId = player.getSelectedSkillingItem();
    player.getSkillManager().stopSkilling();
    final BonesData currentBone = BonesData.forId(boneId);
    if (currentBone == null) {
      return;
    }
    player.getPacketSender().sendInterfaceRemoval();
    player.setCurrentTask(new Task(2, player, true)
    {
      int amountSacrificed = 0;
      @Override
      public void execute()
      {
        if (this.amountSacrificed >= amount)
        {
          stop();
          return;
        }
        if (!player.getInventory().contains(boneId))
        {
          player.getPacketSender().sendMessage("You have run out of " + ItemDefinition.forId(boneId).getName() + ".");
          stop();
          return;
        }
        if (player.getInteractingObject() != null)
        {
          player.setPositionToFace(player.getInteractingObject().getPosition().copy());
          player.getInteractingObject().performGraphic(new Graphic(624));
        }
        if (currentBone == BonesData.BIG_BONES)
        {
          Achievements.finishAchievement(player, Achievements.AchievementData.BURY_A_BIG_BONE);
        }
        else if (currentBone == BonesData.FROSTDRAGON_BONES)
        {
          Achievements.doProgress(player, Achievements.AchievementData.BURY_25_FROST_DRAGON_BONES);
          Achievements.doProgress(player, Achievements.AchievementData.BURY_500_FROST_DRAGON_BONES);
        }
        this.amountSacrificed += 1;
        player.getInventory().delete(boneId, 1);
        player.performAnimation(new Animation(713));
        player.getSkillManager().addExperience(Skill.PRAYER, (int)(currentBone.getBuryingXP() * BonesOnAltar.MemberBonus(player)));
      }
      @Override
      public void stop()
      {
        setEventRunning(false);
        player.getPacketSender().sendMessage("You have pleased the gods with your " + (this.amountSacrificed == 1 ? "sacrifice" : "sacrifices") + ".");
      }
    });
    TaskManager.submit(player.getCurrentTask());
  }
  
  private static double MemberBonus(Player player) {
	  if (player.getAmountDonated() <= 6) {
	      return 1.4;
	    }
		if (player.getAmountDonated() >= 7 && player.getAmountDonated() <= 49) {
		      return 1.5;
	    }
		if (player.getAmountDonated() >= 50 && player.getAmountDonated() <= 99) {
		      return 1.6;
	    }
		if (player.getAmountDonated() >= 100 && player.getAmountDonated() <= 249) {
		      return 1.7;
	    }
		if (player.getAmountDonated() >= 250 && player.getAmountDonated() <= 499) {
		      return 1.8;
	    }
		if (player.getAmountDonated() >= 500) {
		      return 1.9;
	    }
	    return 0;
  }
}
