package com.agaroth.world.content.skill.impl.runecrafting;

import com.agaroth.GameSettings;
import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.CombatIcon;
import com.agaroth.model.Graphic;
import com.agaroth.model.GraphicHeight;
import com.agaroth.model.Hit;
import com.agaroth.model.Hitmask;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.model.Projectile;
import com.agaroth.model.Skill;
import com.agaroth.model.movement.MovementQueue;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class DesoSpan
{
  private static final Animation SIPHONING_ANIMATION = new Animation(9368);
  static enum Energy
  {
    GREEN_ENERGY(8028, 50, 1554, 912, 551, 999),
    YELLOW_ENERGY(8022, 75, 3224, 913, 554, 1006);
    
    public int npcId;
    public int levelReq;
    public int experience;
    public int playerGraphic;
    public int projectileGraphic;
    public int npcGraphic;
    
    private Energy(int npcId, int levelReq, int experience, int playerGraphic, int projectileGraphic, int npcGraphic)
    {
      this.npcId = npcId;
      this.levelReq = levelReq;
      this.experience = experience;
      this.playerGraphic = playerGraphic;
      this.projectileGraphic = projectileGraphic;
      this.npcGraphic = npcGraphic;
    }
    
    static Energy forId(int npc)
    {
      Energy[] arrayOfEnergy;
      int j = (arrayOfEnergy = values()).length;
      for (int i = 0; i < j; i++)
      {
        Energy e = arrayOfEnergy[i];
        if (e.npcId == npc) {
          return e;
        }
      }
      return null;
    }
  }
  
  public static void spawn()
  {
    int lastX = 0;
    for (int i = 0; i < 6; i++)
    {
      int randomX = 2595 + Misc.getRandom(12);
      if ((randomX == lastX) || (randomX == lastX + 1) || (randomX == lastX - 1)) {
        randomX++;
      }
      int randomY = 4772 + Misc.getRandom(8);
      lastX = randomX;
      World.register(new NPC(i <= 3 ? 8028 : 8022, new Position(randomX, randomY)));
    }
  }
  
  public static void siphon(final Player player, final NPC n)
  {
    final Energy energyType = Energy.forId(n.getId());
    if (energyType != null)
    {
      player.getSkillManager().stopSkilling();
      if (player.getPosition().equals(n.getPosition())) {
        MovementQueue.stepAway(player);
      }
      player.setEntityInteraction(n);
      if (player.getSkillManager().getCurrentLevel(Skill.RUNECRAFTING) < energyType.levelReq)
      {
        player.getPacketSender().sendMessage("You need a Runecrafting level of at least " + energyType.levelReq + " to siphon this energy source.");
        return;
      }
      if ((!player.getInventory().contains(13653)) && (player.getInventory().getFreeSlots() == 0))
      {
        player.getPacketSender().sendMessage("You need some free inventory space to do this.");
        return;
      }
      player.performAnimation(SIPHONING_ANIMATION);
      new Projectile(player, n, energyType.projectileGraphic, 15, 44, 43, 31, 0).sendProjectile();
      int cycle = 2 + Misc.getRandom(2);
      player.setCurrentTask(new Task(cycle, player, false)
      {
    	  @Override
        public void execute()
        {
          if (n.getConstitution() <= 0)
          {
            player.getPacketSender().sendMessage("This energy source has died out.");
            stop();
            return;
          }
          player.getSkillManager().addExperience(Skill.RUNECRAFTING, energyType.experience + Misc.getRandom(30));
          player.performGraphic(new Graphic(energyType.playerGraphic, GraphicHeight.HIGH));
          n.performGraphic(new Graphic(energyType.npcGraphic, GraphicHeight.HIGH));
          n.dealDamage(new Hit(Misc.getRandom(12), Hitmask.RED, CombatIcon.MAGIC));
          if (Misc.getRandom(30) <= 10)
          {
            player.dealDamage(new Hit(1 + DesoSpan.damage(player), Hitmask.RED, CombatIcon.DEFLECT));
            player.getPacketSender().sendMessage("You accidently attempt to siphon too much energy, and get hurt.");
          }
          else
          {
            player.getPacketSender().sendMessage("You siphon some energy ..");
            player.getInventory().add(13653, 1);
          }
          if ((n.getConstitution() > 0) && (player.getConstitution() > 0)) {
            DesoSpan.siphon(player, n);
          }
          stop();
        }
      });
      TaskManager.submit(player.getCurrentTask());
    }
  }
  
  private static int damage(Player player) {
	if (player.getAmountDonated() <= 6) {
      return Misc.getRandom(35);
    }
	if (player.getAmountDonated() >= 7 && player.getAmountDonated() <= 49) {
      return Misc.getRandom(30);
    }
	if (player.getAmountDonated() >= 50 && player.getAmountDonated() <= 99) {
      return Misc.getRandom(27);
    }
	if (player.getAmountDonated() >= 100 && player.getAmountDonated() <= 249) {
      return Misc.getRandom(25);
    }
	if (player.getAmountDonated() >= 250 && player.getAmountDonated() <= 499) {
      return Misc.getRandom(22);
    }
	if (player.getAmountDonated() >= 500) {
      return Misc.getRandom(20);
    }
    return 0;
  }
}
