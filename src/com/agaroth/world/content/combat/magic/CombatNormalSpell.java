package com.agaroth.world.content.combat.magic;

import com.agaroth.world.entity.impl.Character;

public abstract class CombatNormalSpell extends CombatSpell {

    @Override
    public void finishCast(Character cast, Character castOn, boolean accurate,
        int damage) {}
}