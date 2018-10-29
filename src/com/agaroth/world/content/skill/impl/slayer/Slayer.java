package com.agaroth.world.content.skill.impl.slayer;

import com.agaroth.model.Item;
import com.agaroth.model.Locations;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.model.Skill;
import com.agaroth.model.container.impl.Shop;
import com.agaroth.model.definitions.NpcDefinition;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.content.Achievements;
import com.agaroth.world.content.PlayerPanel;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.content.transportation.TeleportHandler;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class Slayer
{
  public Slayer(Player p)
  {
    this.player = p;
  }
  
  private SlayerTasks slayerTask = SlayerTasks.NO_TASK;
  private SlayerTasks lastTask = SlayerTasks.NO_TASK;
  private SlayerMaster slayerMaster = SlayerMaster.VANNAKA;
  
  public void assignTask()
  {
    boolean hasTask = (getSlayerTask() != SlayerTasks.NO_TASK) && (this.player.getSlayer().getLastTask() != getSlayerTask());
    boolean duoSlayer = this.duoPartner != null;
    if ((duoSlayer) && (!this.player.getSlayer().assignDuoSlayerTask())) {
      return;
    }
    if (hasTask)
    {
      this.player.getPacketSender().sendInterfaceRemoval();
      return;
    }
    int[] taskData = SlayerTasks.getNewTaskData(this.slayerMaster);
    int slayerTaskId = taskData[0];int slayerTaskAmount = taskData[1];
    SlayerTasks taskToSet = SlayerTasks.forId(slayerTaskId);
    if ((taskToSet == this.player.getSlayer().getLastTask()) || (NpcDefinition.forId(taskToSet.getNpcId()).getSlayerLevel() > this.player.getSkillManager().getMaxLevel(Skill.SLAYER)))
    {
      assignTask();
      return;
    }
    this.player.getPacketSender().sendInterfaceRemoval();
    this.amountToSlay = slayerTaskAmount;
    this.slayerTask = taskToSet;
    DialogueManager.start(this.player, SlayerDialogues.receivedTask(this.player, getSlayerMaster(), getSlayerTask()));
    PlayerPanel.refreshPanel(this.player);
    if (duoSlayer)
    {
      Player duo = World.getPlayerByName(this.duoPartner);
      duo.getSlayer().setSlayerTask(taskToSet);
      duo.getSlayer().setAmountToSlay(slayerTaskAmount);
      duo.getPacketSender().sendInterfaceRemoval();
      DialogueManager.start(duo, SlayerDialogues.receivedTask(duo, this.slayerMaster, taskToSet));
      PlayerPanel.refreshPanel(duo);
    }
  }
  
  public void resetSlayerTask() {
    SlayerTasks task = getSlayerTask();
    if (task == SlayerTasks.NO_TASK) {
      return;
    }
    if (player.getAmountDonated() >= 7 && player.getAmountDonated() <= 49) {
      if (this.player.getPointsHandler().getSlayerPoints() <= 4) {
        this.player.getPacketSender().sendMessage("You dont have enough slayer points to cancel task (4).");
        
        return;
      }
      this.slayerTask = SlayerTasks.NO_TASK;
      this.amountToSlay = 0;
      this.taskStreak = 0;
      this.player.getPointsHandler().setSlayerPoints(this.player.getPointsHandler().getSlayerPoints() - 4, false);
      PlayerPanel.refreshPanel(this.player);
      Player duo = this.duoPartner == null ? null : World.getPlayerByName(this.duoPartner);
      if (duo != null) {
        duo.getSlayer().setSlayerTask(SlayerTasks.NO_TASK).setAmountToSlay(0).setTaskStreak(0);
        duo.getPacketSender().sendMessage("Your partner exchanged 4 Slayer points to reset your team's Slayer task.");
        PlayerPanel.refreshPanel(duo);
        this.player.getPacketSender().sendMessage("You've successfully reset your team's Slayer task.");
      }
      else {
        this.player.getPacketSender().sendMessage("Your Slayer task has been reset.");
      }
    }
    if (player.getAmountDonated() >= 50 && player.getAmountDonated() <= 99) {
      if (this.player.getPointsHandler().getSlayerPoints() <= 3) {
        this.player.getPacketSender().sendMessage("You dont have enough slayer points to cancel task (3).");
        
        return;
      }
      this.slayerTask = SlayerTasks.NO_TASK;
      this.amountToSlay = 0;
      this.taskStreak = 0;
      this.player.getPointsHandler().setSlayerPoints(this.player.getPointsHandler().getSlayerPoints() - 3, false);
      PlayerPanel.refreshPanel(this.player);
      Player duo = this.duoPartner == null ? null : World.getPlayerByName(this.duoPartner);
      if (duo != null) {
        duo.getSlayer().setSlayerTask(SlayerTasks.NO_TASK).setAmountToSlay(0).setTaskStreak(0);
        duo.getPacketSender().sendMessage("Your partner exchanged 3 Slayer points to reset your team's Slayer task.");
        PlayerPanel.refreshPanel(duo);
        this.player.getPacketSender().sendMessage("You've successfully reset your team's Slayer task.");
      }
      else {
        this.player.getPacketSender().sendMessage("Your Slayer task has been reset.");
      }
    }
    if (player.getAmountDonated() >= 100 && player.getAmountDonated() <= 249) {
      if (this.player.getPointsHandler().getSlayerPoints() <= 2) {
        this.player.getPacketSender().sendMessage("You dont have enough slayer points to cancel task (2).");
        
        return;
      }
      this.slayerTask = SlayerTasks.NO_TASK;
      this.amountToSlay = 0;
      this.taskStreak = 0;
      this.player.getPointsHandler().setSlayerPoints(this.player.getPointsHandler().getSlayerPoints() - 2, false);
      PlayerPanel.refreshPanel(this.player);
      Player duo = this.duoPartner == null ? null : World.getPlayerByName(this.duoPartner);
      if (duo != null) {
        duo.getSlayer().setSlayerTask(SlayerTasks.NO_TASK).setAmountToSlay(0).setTaskStreak(0);
        duo.getPacketSender().sendMessage("Your partner exchanged 2 Slayer points to reset your team's Slayer task.");
        PlayerPanel.refreshPanel(duo);
        this.player.getPacketSender().sendMessage("You've successfully reset your team's Slayer task.");
      }
      else {
        this.player.getPacketSender().sendMessage("Your Slayer task has been reset.");
      }
    }
    if (player.getAmountDonated() >= 250 && player.getAmountDonated() <= 499) {
      if (this.player.getPointsHandler().getSlayerPoints() <= 1) {
        this.player.getPacketSender().sendMessage("You dont have enough slayer points to cancel task (1).");
        
        return;
      }
      this.slayerTask = SlayerTasks.NO_TASK;
      this.amountToSlay = 0;
      this.taskStreak = 0;
      this.player.getPointsHandler().setSlayerPoints(this.player.getPointsHandler().getSlayerPoints() - 1, false);
      PlayerPanel.refreshPanel(this.player);
      Player duo = this.duoPartner == null ? null : World.getPlayerByName(this.duoPartner);
      if (duo != null) {
        duo.getSlayer().setSlayerTask(SlayerTasks.NO_TASK).setAmountToSlay(0).setTaskStreak(0);
        duo.getPacketSender().sendMessage("Your partner exchanged 1 Slayer point to reset your team's Slayer task.");
        PlayerPanel.refreshPanel(duo);
        this.player.getPacketSender().sendMessage("You've successfully reset your team's Slayer task.");
      }
      else {
        this.player.getPacketSender().sendMessage("Your Slayer task has been reset.");
      }
    }
    if (player.getAmountDonated() >= 500) {
      this.slayerTask = SlayerTasks.NO_TASK;
      this.amountToSlay = 0;
      this.taskStreak = 0;
      PlayerPanel.refreshPanel(this.player);
      Player duo = this.duoPartner == null ? null : World.getPlayerByName(this.duoPartner);
      if (duo != null)
      {
        duo.getSlayer().setSlayerTask(SlayerTasks.NO_TASK).setAmountToSlay(0).setTaskStreak(0);
        duo.getPacketSender().sendMessage("Your partner reset your team's Slayer task.");
        PlayerPanel.refreshPanel(duo);
        this.player.getPacketSender().sendMessage("You've successfully reset your team's Slayer task.");
      }
      else {
        this.player.getPacketSender().sendMessage("Your Slayer task has been reset.");
      }
    }
    if (player.getAmountDonated() <= 6) {
      if (this.player.getPointsHandler().getSlayerPoints() <= 5) {
        this.player.getPacketSender().sendMessage("You dont have enough slayer points to cancel task (5).");
        
        return;
      }
      this.slayerTask = SlayerTasks.NO_TASK;
      this.amountToSlay = 0;
      this.taskStreak = 0;
      this.player.getPointsHandler().setSlayerPoints(this.player.getPointsHandler().getSlayerPoints() - 5, false);
      PlayerPanel.refreshPanel(this.player);
      Player duo = this.duoPartner == null ? null : World.getPlayerByName(this.duoPartner);
      if (duo != null) {
        duo.getSlayer().setSlayerTask(SlayerTasks.NO_TASK).setAmountToSlay(0).setTaskStreak(0);
        duo.getPacketSender().sendMessage("Your partner exchanged 5 Slayer points to reset your team's Slayer task.");
        PlayerPanel.refreshPanel(duo);
        this.player.getPacketSender().sendMessage("You've successfully reset your team's Slayer task.");
      }
      else {
        this.player.getPacketSender().sendMessage("Your Slayer task has been reset.");
      }
    }
  }
  
  public void killedNpc(NPC npc)
  {
    if ((this.slayerTask != SlayerTasks.NO_TASK) && 
      (this.slayerTask.getNpcId() == npc.getId()))
    {
      handleSlayerTaskDeath(true);
      if (this.duoPartner != null)
      {
        Player duo = World.getPlayerByName(this.duoPartner);
        if (duo != null) {
          if (checkDuoSlayer(this.player, false)) {
            duo.getSlayer().handleSlayerTaskDeath(Locations.goodDistance(this.player.getPosition(), duo.getPosition(), 20));
          } else {
            resetDuo(this.player, duo);
          }
        }
      }
    }
  }
  
  public void handleSlayerTaskDeath(boolean giveXp)
  {
    int xp = this.slayerTask.getXP() + Misc.getRandom(this.slayerTask.getXP() / 5);
    if (this.amountToSlay > 1)
    {
      this.amountToSlay -= 1;
    }
    else
    {
      this.player.getPacketSender().sendMessage("").sendMessage("You've completed your Slayer task! Return to a Slayer master for another one.");
      this.taskStreak += 1;
      Achievements.finishAchievement(this.player, Achievements.AchievementData.COMPLETE_A_SLAYER_TASK);
      if (this.slayerTask.getTaskMaster() == SlayerMaster.KURADEL) {
        Achievements.finishAchievement(this.player, Achievements.AchievementData.COMPLETE_A_HARD_SLAYER_TASK);
      } else if (this.slayerTask.getTaskMaster() == SlayerMaster.SUMONA) {
        Achievements.finishAchievement(this.player, Achievements.AchievementData.COMPLETE_AN_ELITE_SLAYER_TASK);
      }
      this.lastTask = this.slayerTask;
      this.slayerTask = SlayerTasks.NO_TASK;
      this.amountToSlay = 0;
      givePoints(this.slayerMaster);
    }
    if (giveXp) {
      this.player.getSkillManager().addExperience(Skill.SLAYER, this.doubleSlayerXP ? xp * 2 : xp);
    }
    PlayerPanel.refreshPanel(this.player);
  }
  
  @SuppressWarnings("incomplete-switch")
	public void givePoints(SlayerMaster master) {
		int pointsReceived = 4;
		switch(master) {
		case DURADEL:
			pointsReceived = 7;
			break;
		case KURADEL:
			pointsReceived = 10;
			break;
		case SUMONA:
			pointsReceived = 16;
			break;
		}
		int per5 = pointsReceived * 3;
		int per10 = pointsReceived * 5;
		if(player.getSlayer().getTaskStreak() == 5) {
			player.getPointsHandler().setSlayerPoints(per5, true);
			player.getPacketSender().sendMessage("You received "+per5+" Slayer points.");
		} else if(player.getSlayer().getTaskStreak() == 10) {
			player.getPointsHandler().setSlayerPoints(per10, true);
			player.getPacketSender().sendMessage("You received "+per10+" Slayer points and your Task Streak has been reset.");
			player.getSlayer().setTaskStreak(0);
		} else if(player.getSlayer().getTaskStreak() >= 0 && player.getSlayer().getTaskStreak() < 5 || player.getSlayer().getTaskStreak() >= 6 && player.getSlayer().getTaskStreak() < 10) {
			player.getPointsHandler().setSlayerPoints(pointsReceived, true);
			player.getPacketSender().sendMessage("You received "+pointsReceived+" Slayer points.");
		}
		player.getPointsHandler().refreshPanel();
	}
  
  public boolean assignDuoSlayerTask()
  {
    this.player.getPacketSender().sendInterfaceRemoval();
    if (this.player.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK)
    {
      this.player.getPacketSender().sendMessage("You already have a Slayer task.");
      return false;
    }
    Player partner = World.getPlayerByName(this.duoPartner);
    if (partner == null)
    {
      this.player.getPacketSender().sendMessage("");
      this.player.getPacketSender().sendMessage("You can only get a new Slayer task when your duo partner is online.");
      return false;
    }
    if ((partner.getSlayer().getDuoPartner() == null) || (!partner.getSlayer().getDuoPartner().equals(this.player.getUsername())))
    {
      resetDuo(this.player, null);
      return false;
    }
    if (partner.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK)
    {
      this.player.getPacketSender().sendMessage("Your partner already has a Slayer task.");
      return false;
    }
    if (partner.getSlayer().getSlayerMaster() != this.player.getSlayer().getSlayerMaster())
    {
      this.player.getPacketSender().sendMessage("You and your partner need to have the same Slayer master.");
      return false;
    }
    if (partner.getInterfaceId() > 0)
    {
      this.player.getPacketSender().sendMessage("Your partner must close all their open interfaces.");
      return false;
    }
    return true;
  }
  
  public static boolean checkDuoSlayer(Player p, boolean login)
  {
    if (p.getSlayer().getDuoPartner() == null) {
      return false;
    }
    Player partner = World.getPlayerByName(p.getSlayer().getDuoPartner());
    if (partner == null) {
      return false;
    }
    if ((partner.getSlayer().getDuoPartner() == null) || (!partner.getSlayer().getDuoPartner().equals(p.getUsername())))
    {
      resetDuo(p, null);
      return false;
    }
    if (partner.getSlayer().getSlayerMaster() != p.getSlayer().getSlayerMaster())
    {
      resetDuo(p, partner);
      return false;
    }
    if (login)
    {
      p.getSlayer().setSlayerTask(partner.getSlayer().getSlayerTask());
      p.getSlayer().setAmountToSlay(partner.getSlayer().getAmountToSlay());
    }
    return true;
  }
  
  public static void resetDuo(Player player, Player partner)
  {
    if ((partner != null) && 
      (partner.getSlayer().getDuoPartner() != null) && (partner.getSlayer().getDuoPartner().equals(player.getUsername())))
    {
      partner.getSlayer().setDuoPartner(null);
      partner.getPacketSender().sendMessage("Your Slayer duo team has been disbanded.");
      PlayerPanel.refreshPanel(partner);
    }
    player.getSlayer().setDuoPartner(null);
    player.getPacketSender().sendMessage("Your Slayer duo team has been disbanded.");
    PlayerPanel.refreshPanel(player);
  }
  
  public void handleInvitation(boolean accept)
  {
    if (this.duoInvitation != null)
    {
      Player inviteOwner = World.getPlayerByName(this.duoInvitation);
      if (inviteOwner != null)
      {
        if (accept)
        {
          if (this.duoPartner != null)
          {
            this.player.getPacketSender().sendMessage("You already have a Slayer duo partner.");
            inviteOwner.getPacketSender().sendMessage(this.player.getUsername() + " already has a Slayer duo partner.");
            return;
          }
          inviteOwner.getPacketSender().sendMessage(this.player.getUsername() + " has joined your duo Slayer team.").sendMessage("Seek respective Slayer master for a task.");
          inviteOwner.getSlayer().setDuoPartner(this.player.getUsername());
          PlayerPanel.refreshPanel(inviteOwner);
          this.player.getPacketSender().sendMessage("You have joined " + inviteOwner.getUsername() + "'s duo Slayer team.");
          this.player.getSlayer().setDuoPartner(inviteOwner.getUsername());
          PlayerPanel.refreshPanel(this.player);
        }
        else
        {
          this.player.getPacketSender().sendMessage("You've declined the invitation.");
          inviteOwner.getPacketSender().sendMessage(this.player.getUsername() + " has declined your invitation.");
        }
      }
      else {
        this.player.getPacketSender().sendMessage("Failed to handle the invitation.");
      }
    }
  }
  
  public void handleSlayerRingTP(int itemId)
  {
    if (!this.player.getClickDelay().elapsed(4500L)) {
      return;
    }
    if (this.player.getMovementQueue().isLockMovement()) {
      return;
    }
    SlayerTasks task = getSlayerTask();
    if (task == SlayerTasks.NO_TASK) {
      return;
    }
    Position slayerTaskPos = new Position(task.getTaskPosition().getX(), task.getTaskPosition().getY(), task.getTaskPosition().getZ());
    if (!TeleportHandler.checkReqs(this.player, slayerTaskPos)) {
      return;
    }
    TeleportHandler.teleportPlayer(this.player, slayerTaskPos, this.player.getSpellbook().getTeleportType());
    Item slayerRing = new Item(itemId);
    this.player.getInventory().delete(slayerRing);
    if (slayerRing.getId() < 13288) {
      this.player.getInventory().add(slayerRing.getId() + 1, 1);
    } else {
      this.player.getPacketSender().sendMessage("Your Ring of Slaying crumbles to dust.");
    }
  }
  
  public int getAmountToSlay()
  {
    return this.amountToSlay;
  }
  
  public Slayer setAmountToSlay(int amountToSlay)
  {
    this.amountToSlay = amountToSlay;
    return this;
  }
  
  public int getTaskStreak()
  {
    return this.taskStreak;
  }
  
  public Slayer setTaskStreak(int taskStreak)
  {
    this.taskStreak = taskStreak;
    return this;
  }
  
  public SlayerTasks getLastTask()
  {
    return this.lastTask;
  }
  
  public void setLastTask(SlayerTasks lastTask)
  {
    this.lastTask = lastTask;
  }
  
  public boolean doubleSlayerXP = false;
  private Player player;
  private int amountToSlay;
  private int taskStreak;
  private String duoPartner;
  private String duoInvitation;
  
  public Slayer setDuoPartner(String duoPartner)
  {
    this.duoPartner = duoPartner;
    return this;
  }
  
  public String getDuoPartner()
  {
    return this.duoPartner;
  }
  
  public SlayerTasks getSlayerTask()
  {
    return this.slayerTask;
  }
  
  public Slayer setSlayerTask(SlayerTasks slayerTask)
  {
    this.slayerTask = slayerTask;
    return this;
  }
  
  public SlayerMaster getSlayerMaster()
  {
    return this.slayerMaster;
  }
  
  public void setSlayerMaster(SlayerMaster master)
  {
    this.slayerMaster = master;
  }
  
  public void setDuoInvitation(String player)
  {
    this.duoInvitation = player;
  }
  
  public static boolean handleRewardsInterface(Player player, int button)
  {
    if (player.getInterfaceId() == 36000)
    {
      switch (button)
      {
      case -29534: 
        player.getPacketSender().sendInterfaceRemoval();
        break;
      case -29522: 
        if (player.getPointsHandler().getSlayerPoints() < 10)
        {
          player.getPacketSender().sendMessage("You do not have 10 Slayer points.");
          return true;
        }
        player.getPointsHandler().refreshPanel();
        player.getPointsHandler().setSlayerPoints(-10, true);
        player.getSkillManager().addExperience(Skill.SLAYER, 10000);
        player.getPacketSender().sendMessage("You've bought 10000 Slayer XP for 10 Slayer points.");
        break;
      case -29519: 
        if (player.getPointsHandler().getSlayerPoints() < 300)
        {
          player.getPacketSender().sendMessage("You do not have 300 Slayer points.");
          return true;
        }
        if (player.getSlayer().doubleSlayerXP)
        {
          player.getPacketSender().sendMessage("You already have this buff.");
          return true;
        }
        player.getPointsHandler().setSlayerPoints(-300, true);
        player.getSlayer().doubleSlayerXP = true;
        player.getPointsHandler().refreshPanel();
        player.getPacketSender().sendMessage("You will now permanently receive double Slayer experience.");
        break;
      case -29531: 
        ((Shop)Shop.ShopManager.getShops().get(Integer.valueOf(47))).open(player);
      }
      player.getPacketSender().sendString(36030, "Current Points:   " + player.getPointsHandler().getSlayerPoints());
      return true;
    }
    return false;
  }
}
