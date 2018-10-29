package com.agaroth.world.content.combat.magic;

import java.util.Optional;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.Projectile;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;

public abstract class CombatSpell extends Spell {

	@Override
	public void startCast(Character cast, Character castOn) {

		int castAnimation = -1;

		NPC npc = cast.isNpc() ? ((NPC)cast) : null;
		if(npc != null) {
			if(npc.getId() == 3496 || npc.getId() == 6278 || npc.getId() == 2000 || npc.getId() == 109 || npc.getId() == 3580 || npc.getId() == 2007) {
				castAnimation = npc.getDefinition().getAttackAnimation();
			}
		}

		if(castAnimation().isPresent() && castAnimation == -1) {
			castAnimation().ifPresent(cast::performAnimation);
		} else {
			cast.performAnimation(new Animation(castAnimation));
		}
		if(npc != null) {
			if(npc.getId() != 2000 && npc.getId() != 109 && npc.getId() != 3580 && npc.getId() != 2007) {
				startGraphic().ifPresent(cast::performGraphic);
			}
		} else {
			startGraphic().ifPresent(cast::performGraphic);
		}
		castProjectile(cast, castOn).ifPresent(g -> {
			TaskManager.submit(new Task(2, cast.getCombatBuilder(), false) {
				@Override
				public void execute() {
					g.sendProjectile();
					this.stop();
				}
			});
		});
	}

	public abstract int spellId();
	public abstract int maximumHit();
	public abstract Optional<Animation> castAnimation();
	public abstract Optional<Graphic> startGraphic();
	public abstract Optional<Projectile> castProjectile(Character cast,Character castOn);
	public abstract Optional<Graphic> endGraphic();
	public abstract void finishCast(Character cast, Character castOn, boolean accurate, int damage);
}