package com.agaroth.world.content.skill.impl.hunter;

import com.agaroth.model.GameObject;
import com.agaroth.world.entity.impl.player.Player;

public class Trap {

	public static enum TrapState {

		SET, CAUGHT;
	}

	private GameObject gameObject;
	private int ticks;
	private TrapState trapState;

	public Trap(GameObject object, TrapState state, int ticks, Player owner) {
		gameObject = object;
		trapState = state;
		this.ticks = ticks;
		this.player = owner;
	}

	public GameObject getGameObject() {
		return gameObject;
	}

	public int getTicks() {
		return ticks;
	}

	public TrapState getTrapState() {
		return trapState;
	}

	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public void setTrapState(TrapState state) {
		trapState = state;
	}
	
	private Player player;
	
	public Player getOwner() {
		return player;
	}
	
	public void setOwner(Player player) {
		this.player = player;
	}
}