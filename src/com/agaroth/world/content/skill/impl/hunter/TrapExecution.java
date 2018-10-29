package com.agaroth.world.content.skill.impl.hunter;

import com.agaroth.model.Skill;
import com.agaroth.util.Misc;
import com.agaroth.world.entity.impl.npc.NPC;

public class TrapExecution
{
  public static void setTrapProcess(Trap trap)
  {
    for (NPC npc : Hunter.HUNTER_NPC_LIST) {
      if ((npc != null) && (npc.isVisible())) {
        if ((!(trap instanceof BoxTrap)) || (npc.getId() == 5079) || (npc.getId() == 5080) || (npc.getId() == 5081)) {
          if ((!(trap instanceof SnareTrap)) || ((npc.getId() != 5079) && (npc.getId() != 5080) && (npc.getId() != 5081))) {
            if ((npc.getPosition().isWithinDistance(trap.getGameObject().getPosition(), 1)) && 
              (Misc.getRandom(100) < successFormula(trap, npc)))
            {
              Hunter.catchNPC(trap, npc);
              return;
            }
          }
        }
      }
    }
  }
  
  public static int successFormula(Trap trap, NPC npc)
  {
    if (trap.getOwner() == null) {
      return 0;
    }
    int chance = 70;
    if (Hunter.hasLarupia(trap.getOwner())) {
      chance += 10;
    }
    chance = chance + 
      (int)(trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) / 1.5D) + 
      10;
    if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 25) {
      chance = (int)(chance * 1.5D) + 8;
    }
    if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 40) {
      chance = (int)(chance * 1.4D) + 3;
    }
    if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 50) {
      chance = (int)(chance * 1.3D) + 1;
    }
    if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 55) {
      chance = (int)(chance * 1.2D);
    }
    if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 60) {
      chance = (int)(chance * 1.1D);
    }
    if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 65) {
      chance = (int)(chance * 1.05D) + 3;
    }
    return chance;
  }
  
  public static boolean trapTimerManagement(Trap trap)
  {
    if (trap.getTicks() > 0) {
      trap.setTicks(trap.getTicks() - 1);
    }
    if (trap.getTicks() <= 0)
    {
      Hunter.deregister(trap);
      if (trap.getOwner() != null) {
        trap.getOwner().getPacketSender().sendMessage(
          "You left your trap for too long, and it collapsed.");
      }
    }
    return true;
  }
}
