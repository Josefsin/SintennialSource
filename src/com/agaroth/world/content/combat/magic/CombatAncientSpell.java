package com.agaroth.world.content.combat.magic;

import java.util.Iterator;
import java.util.Optional;

import com.agaroth.model.CombatIcon;
import com.agaroth.model.Hit;
import com.agaroth.model.Hitmask;
import com.agaroth.model.Item;
import com.agaroth.model.Locations;
import com.agaroth.model.Locations.Location;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public abstract class CombatAncientSpell extends CombatSpell {

    @Override
    public void finishCast(Character cast, Character castOn, boolean accurate,
        int damage) {
        if (!accurate || damage <= 0) {
            return;
        }
        spellEffect(cast, castOn, damage);
        if (spellRadius() == 0 || !Locations.Location.inMulti(castOn)) {
            return;
        }
        Iterator<? extends Character> it = null;
        if (cast.isPlayer() && castOn.isPlayer()) {
            it = ((Player) cast).getLocalPlayers().iterator();
        } else if (cast.isPlayer() && castOn.isNpc()) {
            it = ((Player) cast).getLocalNpcs().iterator();
        } else if (cast.isNpc() && castOn.isNpc()) {
            it = World.getNpcs().iterator();
        } else if (cast.isNpc() && castOn.isPlayer()) {
            it = World.getPlayers().iterator();
        }

        for (Iterator<? extends Character> $it = it; $it.hasNext();) {
            Character next = $it.next();

            if (next == null) {
                continue;
            }
            
            if(next.isNpc()) {
            	NPC n = (NPC)next;
            	if(!n.getDefinition().isAttackable() || n.isSummoningNpc()) {
            		continue;
            	}
            } else {
            	Player p = (Player)next;
            	if(p.getLocation() != Location.WILDERNESS || !Location.inMulti(p)) {
            		continue;
            	}
            }
            

            if (next.getPosition().isWithinDistance(castOn.getPosition(),
                spellRadius()) && !next.equals(cast) && !next.equals(castOn) && next.getConstitution() > 0 && next.getConstitution() > 0) {
                cast.getCurrentlyCasting().endGraphic().ifPresent(next::performGraphic);
                int calc = Misc.inclusiveRandom(0, maximumHit());
                next.dealDamage(new Hit(calc, Hitmask.RED, CombatIcon.MAGIC));
                next.getCombatBuilder().addDamage(cast, calc);
                spellEffect(cast, next, calc);
            }
        }
    }

    @Override
    public Optional<Item[]> equipmentRequired(Player player) {
        return Optional.empty();
    }

    public abstract void spellEffect(Character cast, Character castOn, int damage);
    public abstract int spellRadius();
}
