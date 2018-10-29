package com.agaroth.world.content.skill.impl.crafting;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Skill;
import com.agaroth.model.input.impl.EnterAmountToSpin;
import com.agaroth.world.entity.impl.player.Player;

public class Flax
{
  public static void showSpinInterface(Player player)
  {
    player.getPacketSender().sendInterfaceRemoval();
    player.getSkillManager().stopSkilling();
    if (!player.getInventory().contains(1779))
    {
      player.getPacketSender().sendMessage("You do not have any Flax to spin.");
      return;
    }
    player.setInputHandling(new EnterAmountToSpin());
    player.getPacketSender().sendString(2799, "Flax").sendInterfaceModel(1746, 1779, 150).sendChatboxInterface(4429);
    player.getPacketSender().sendString(2800, "How many would you like to make?");
  }
  
  public static void spinFlax(final Player player, final int amount)
  {
    if (amount <= 0) {
      return;
    }
    player.getSkillManager().stopSkilling();
    player.getPacketSender().sendInterfaceRemoval();
    player.setCurrentTask(new Task(2, player, true)
    {
      int amountSpan = 0;
      @Override
      public void execute() {
        if (!player.getInventory().contains(1779))
        {
          stop();
          return;
        }
        player.getSkillManager().addExperience(Skill.CRAFTING, 324);
        player.performAnimation(new Animation(896));
        player.getInventory().delete(1779, 1);
        player.getInventory().add(1777, 1);
        this.amountSpan += 1;
        if (this.amountSpan >= amount) {
          stop();
        }
      }
    });
    TaskManager.submit(player.getCurrentTask());
  }
}