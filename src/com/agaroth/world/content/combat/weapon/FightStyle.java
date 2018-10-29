package com.agaroth.world.content.combat.weapon;

import com.agaroth.model.Skill;
import com.agaroth.world.content.combat.CombatType;

public enum FightStyle {
    ACCURATE() {
        @Override
        public int[] skill(CombatType type) {
            return type == CombatType.RANGED ? new int[] { Skill.RANGED.ordinal() }
                : new int[] { Skill.ATTACK.ordinal() };
        }
    },
    AGGRESSIVE() {
        @Override
        public int[] skill(CombatType type) {
            return type == CombatType.RANGED ? new int[] { Skill.RANGED.ordinal() }
                : new int[] { Skill.STRENGTH.ordinal() };
        }
    },
    DEFENSIVE() {
        @Override
        public int[] skill(CombatType type) {
            return type == CombatType.RANGED ? new int[] { Skill.RANGED.ordinal(),
                    Skill.DEFENCE.ordinal() } : new int[] { Skill.DEFENCE.ordinal() };
        }
    },
    CONTROLLED() {
        @Override
        public int[] skill(CombatType type) {
            return new int[] { Skill.ATTACK.ordinal(), Skill.STRENGTH.ordinal(), Skill.DEFENCE.ordinal() };
        }
    };
    public abstract int[] skill(CombatType type);
}