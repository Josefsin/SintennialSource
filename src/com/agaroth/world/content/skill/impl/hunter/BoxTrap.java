package com.agaroth.world.content.skill.impl.hunter;

import com.agaroth.model.GameObject;
import com.agaroth.world.entity.impl.player.Player;

public class BoxTrap extends Trap {

	private TrapState state;

	public BoxTrap(GameObject obj, TrapState state, int ticks, Player p) {
		super(obj, state, ticks, p);
	}

	public TrapState getState() {
		return state;
	}

	public void setState(TrapState state) {
		this.state = state;
	}

}