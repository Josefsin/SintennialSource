package com.agaroth.world.content.combat;

import com.agaroth.model.Locations;
import com.agaroth.model.Locations.Location;
import com.agaroth.world.entity.impl.Character;

public class CombatDistanceSession {
	private CombatBuilder builder;
	private Character victim;
	
	public CombatDistanceSession(CombatBuilder builder, Character victim) {
		this.builder = builder;
		this.victim = victim;
	}

	public void process() {
		builder.determineStrategy();
		builder.attackTimer = 0;

		if(builder.getVictim() != null && !builder.getVictim().equals(victim)) {
			builder.reset(true);
			stop();
			return;
		}
				
		if(!Location.ignoreFollowDistance(builder.getCharacter())) {
			if (!Locations.goodDistance(builder.getCharacter().getPosition(), victim.getPosition(), 40)) {
				builder.reset(true);
				stop();
				return;
			}
		}
		
		if(Locations.goodDistance(builder.getCharacter().getPosition(), victim.getPosition(), builder.getStrategy().attackDistance(builder.getCharacter()))) {
			sucessFul();
			stop();
			return;
		}
	}
	
	public void stop() {
		builder.setDistanceSession(null);
	}
	
	private void sucessFul() {
		builder.getCharacter().getMovementQueue().reset();
		builder.setVictim(victim);
        builder.setCombatSession(new CombatSession(builder));
	}
}
