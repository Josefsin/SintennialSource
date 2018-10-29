package com.agaroth.world.content.skill.impl.crafting;

import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.model.input.impl.EnterAmountOfHidesToTan;
import com.agaroth.world.entity.impl.player.Player;

public class Tanning
{
  public static void selectionInterface(Player player)
  {
    player.getPacketSender().sendInterface(14670);
    player.getSkillManager().stopSkilling();
    tanningData[] arrayOftanningData;
    int j = (arrayOftanningData = tanningData.values()).length;
    for (int i = 0; i < j; i++)
    {
      tanningData t = arrayOftanningData[i];
      player.getPacketSender().sendInterfaceModel(t.getItemFrame(), t.getLeatherId(), 250);
      player.getPacketSender().sendString(t.getNameFrame(), t.getName());
      if (player.getInventory().getAmount(995) >= t.getPrice()) {
        player.getPacketSender().sendString(t.getCostFrame(), "@gre@Price: " + t.getPrice());
      } else {
        player.getPacketSender().sendString(t.getCostFrame(), "@red@Price: " + t.getPrice());
      }
    }
  }
  
  public static void tanHide(Player player, int buttonId, int amount)
  {
    tanningData[] arrayOftanningData;
    int j = (arrayOftanningData = tanningData.values()).length;
    for (int i = 0; i < j; i++)
    {
      tanningData t = arrayOftanningData[i];
      if (buttonId == t.getButtonId(buttonId))
      {
        int invAmt = player.getInventory().getAmount(t.getHideId());
        if (amount > invAmt) {
          amount = invAmt;
        }
        if (amount == 0)
        {
          player.getPacketSender().sendMessage("You do not have any " + ItemDefinition.forId(t.getHideId()).getName() + " to tan.");
          return;
        }
        if (amount > t.getAmount(buttonId)) {
          amount = t.getAmount(buttonId);
        }
        int price = amount * t.getPrice();
        boolean usePouch = player.getMoneyInPouch() > price;
        int coins = usePouch ? player.getMoneyInPouchAsInt() : player.getInventory().getAmount(995);
        if (coins == 0)
        {
          player.getPacketSender().sendMessage("You do not have enough coins to tan this hide.");
          return;
        }
        amount = price / t.getPrice();
        int hide = t.getHideId();
        int leather = t.getLeatherId();
        if (coins >= price)
        {
          if (player.getInventory().contains(hide))
          {
            player.getInventory().delete(hide, amount);
            player.getPacketSender().sendInterfaceRemoval();
            if (usePouch)
            {
              player.setMoneyInPouch(player.getMoneyInPouch() - price);
              player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch()); //Update the money pouch
            }
            else
            {
              player.getInventory().delete(995, price);
            }
            player.getInventory().add(leather, amount);
          }
          else
          {
            player.getPacketSender().sendMessage("You do not have any hides to tan.");
          }
        }
        else
        {
          player.getPacketSender().sendMessage("You do not have enough coins to tan this hide.");
          return;
        }
      }
    }
  }
  
  public static boolean handleButton(Player player, int id)
  {
    tanningData[] arrayOftanningData;
    int j = (arrayOftanningData = tanningData.values()).length;
    for (int i = 0; i < j; i++)
    {
      tanningData t = arrayOftanningData[i];
      if (id == t.getButtonId(id))
      {
        if (t.getAmount(id) == 29)
        {
          player.setInputHandling(new EnterAmountOfHidesToTan(id));
          player.getPacketSender().sendEnterAmountPrompt("How many would you like to tan?");
          return true;
        }
        tanHide(player, id, player.getInventory().getAmount(t.getHideId()));
        return true;
      }
    }
    return false;
  }
}
