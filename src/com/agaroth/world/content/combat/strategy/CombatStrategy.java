package com.agaroth.world.content.combat.strategy;

import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.entity.impl.Character;

public interface CombatStrategy {
    public boolean canAttack(Character entity, Character victim);
    public CombatContainer attack(Character entity, Character victim);
    public boolean customContainerAttack(Character entity, Character victim);
    public int attackDelay(Character entity);
    public int attackDistance(Character entity);
    public CombatType getCombatType();
}
