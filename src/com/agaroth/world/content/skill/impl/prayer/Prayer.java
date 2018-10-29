package com.agaroth.world.content.skill.impl.prayer;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Item;
import com.agaroth.model.Skill;
import com.agaroth.world.content.Achievements;
import com.agaroth.world.content.Sounds;
import com.agaroth.world.entity.impl.player.Player;

public class Prayer
{
  public static boolean isBone(int bone)
  {
    return BonesData.forId(bone) != null;
  }
  
  public static void buryBone(final Player player, int itemId)
  {
    if (!player.getClickDelay().elapsed(2000L)) {
      return;
    }
    final BonesData currentBone = BonesData.forId(itemId);
    if (currentBone == null) {
      return;
    }
    player.getSkillManager().stopSkilling();
    player.getPacketSender().sendInterfaceRemoval();
    player.performAnimation(new Animation(827));
    player.getPacketSender().sendMessage("You dig a hole in the ground..");
    final Item bone = new Item(itemId);
    player.getInventory().delete(bone);
    TaskManager.submit(new Task(3, player, false)
    {
    	@Override
      public void execute()
      {
        player.getPacketSender().sendMessage("..and bury the " + bone.getDefinition().getName() + ".");
        player.getSkillManager().addExperience(Skill.PRAYER, currentBone.getBuryingXP());
        Sounds.sendSound(player, Sounds.Sound.BURY_BONE);
        if (currentBone == BonesData.BIG_BONES)
        {
          Achievements.finishAchievement(player, Achievements.AchievementData.BURY_A_BIG_BONE);
        }
        else if (currentBone == BonesData.FROSTDRAGON_BONES)
        {
          Achievements.doProgress(player, Achievements.AchievementData.BURY_25_FROST_DRAGON_BONES);
          Achievements.doProgress(player, Achievements.AchievementData.BURY_500_FROST_DRAGON_BONES);
        }
        stop();
      }
    });
    player.getClickDelay().reset();
  }
}
