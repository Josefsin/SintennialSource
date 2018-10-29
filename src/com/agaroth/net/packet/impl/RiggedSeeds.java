package com.agaroth.net.packet.impl;

import java.util.Arrays;

import com.agaroth.model.Animation;
import com.agaroth.model.GameObject;
import com.agaroth.model.Graphic;
import com.agaroth.model.Locations.Location;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.movement.MovementQueue;
import com.agaroth.util.Misc;
import com.agaroth.world.content.CustomObjects;
import com.agaroth.world.content.Gambling.FlowersData;
import com.agaroth.world.content.clan.ClanChatManager;
import com.agaroth.world.content.dialogue.Dialogue;
import com.agaroth.world.content.dialogue.DialogueExpression;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.content.dialogue.DialogueType;
import com.agaroth.world.content.dialogue.impl.DungPartyInvitation;
import com.agaroth.world.content.skill.impl.slayer.SlayerTasks;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class RiggedSeeds
{
	
	public static void open(Player player, int option) {
		
		switch(option) {
		case 0: 
			DialogueManager.start(player, new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public int npcId() {
					return -1;
				}
				
				@Override
				public DialogueExpression animation() {
					return null;
				}
				
				@Override
				public String[] dialogue() {
					
					String[] names = new String[5];
					FlowersData[] data = Arrays.copyOfRange(RiggedSeeds.FlowersData.values(), 0, names.length - 1);
					
					for(int i = 0; i < names.length - 1; i++) {
						names[i] = Misc.formatPlayerName(data[i].name());
					}
					
					names[4] = "Next";
					
					return names;
					
				}
				
				@Override
				public void specialAction() {
					player.setDialogueActionId(94);
				}
			});
			break;
		case 1:
			DialogueManager.start(player, new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public int npcId() {
					return -1;
				}
				
				@Override
				public DialogueExpression animation() {
					return null;
				}
				
				@Override
				public String[] dialogue() {
					
					String[] names = new String[5];
					int offset = 4;
					FlowersData[] data = Arrays.copyOfRange(RiggedSeeds.FlowersData.values(), offset, offset + names.length - 2);
					
					for(int i = 0; i < names.length - 2; i++) {
						names[i] = Misc.formatPlayerName(data[i].name());
					}
					
					names[3] = "Previous";
					names[4] = "Next";
					
					return names;
					
				}
				
				@Override
				public void specialAction() {
					player.setDialogueActionId(95);
				}
			});
			break;
		case 2:
			DialogueManager.start(player, new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public int npcId() {
					return -1;
				}
				
				@Override
				public DialogueExpression animation() {
					return null;
				}
				
				@Override
				public String[] dialogue() {
					
					String[] names = new String[3];
					int offset = 7;
					FlowersData[] data = Arrays.copyOfRange(RiggedSeeds.FlowersData.values(), offset, offset + names.length - 1);
					
					for(int i = 0; i < names.length - 1; i++) {
						names[i] = Misc.formatPlayerName(data[i].name());
					}
					
					names[2] = "Previous";
					
					return names;
					
				}
				
				@Override
				public void specialAction() {
					player.setDialogueActionId(96);
				}
			});
			break;
		}
		
	}
	
  public static void TrollPlant(Player player, int ordinal)
  {
    /*if (player.getAmountDonated() <= 6)
    {
      player.getPacketSender().sendMessage("You need to be a member to use this item.");
      return;
    }*/
    if (player.getLocation() == Location.GAMBLE) 
    if	(player.getLocation() == Location.EXTREME_ZONE)
    if	(player.getLocation() == Location.MEMBER_ZONE) 
    
    {
	      player.getPacketSender().sendMessage("").sendMessage("This seed can only be planted in the gambling area").sendMessage("To get there, talk to the gambler.");
	      return;
	    }
	    if (!player.getClickDelay().elapsed(2000)) {
	      return;
	    }
    for (NPC npc : player.getLocalNpcs()) {
      if ((npc != null) && (npc.getPosition().equals(player.getPosition())))
      {
        player.getPacketSender().sendMessage("You cannot plant a seed right here.");
        return;
      }
    }
    if (CustomObjects.objectExists(player.getPosition().copy()))
    {
      player.getPacketSender().sendMessage("You cannot plant a seed right here.");
      return;
    }
    FlowersData flowers = FlowersData.values()[ordinal];
    //System.out.println(ordinal+" - "+flowers.objectId);
    GameObject flower = new GameObject(flowers.objectId, player.getPosition().copy());
    player.getMovementQueue().reset();
    player.getInventory().delete(299, 1);
    player.performAnimation(new Animation(827));
    player.getPacketSender().sendMessage("You plant the seed..");
    player.getMovementQueue().reset();
    player.setDialogueActionId(42);
    player.setInteractingObject(flower);
    DialogueManager.start(player, 78);
    MovementQueue.stepAway(player);
    CustomObjects.globalObjectRemovalTask(flower, 90);
    player.setPositionToFace(flower.getPosition());
    player.getClickDelay().reset();
  }
  
  public static enum FlowersData
  {
	  PASTEL_FLOWERS(2980, 2460),  RED_FLOWERS(2981, 2462),  BLUE_FLOWERS(2982, 2464),  YELLOW_FLOWERS(2983, 2466),  PURPLE_FLOWERS(2984, 2468),  ORANGE_FLOWERS(2985, 2470),  RAINBOW_FLOWERS(2986, 2472),  WHITE_FLOWERS(2987, 2474),  BLACK_FLOWERS(2988, 2476);
	    
    public int objectId;
    public int itemId;
    
    private FlowersData(int objectId, int itemId)
    {
      this.objectId = objectId;
      this.itemId = itemId;
    }
    
    public static FlowersData forObject(int object)
    {
      FlowersData[] arrayOfFlowersData;
      int j = (arrayOfFlowersData = values()).length;
      for (int i = 0; i < j; i++)
      {
        FlowersData data = arrayOfFlowersData[i];
        if (data.objectId == object) {
          return data;
        }
      }
      return null;
    }
    
    public static FlowersData generate()
    {
      double RANDOM = Math.random() * 100.0;
      if (RANDOM >= 1.0) {
        return values()[Misc.getRandom(1)];
      }
      return Misc.getRandom(2) == 1 ? WHITE_FLOWERS : BLACK_FLOWERS;
    }
  }
}
