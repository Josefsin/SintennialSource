package com.agaroth.world.entity;

import com.agaroth.GameSettings;
import com.agaroth.model.Animation;
import com.agaroth.model.GameObject;
import com.agaroth.model.Graphic;
import com.agaroth.model.Position;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class Entity {

	public Entity(Position position) {
		setPosition(position);
		lastKnownRegion = position;
	}

	private int index;
	private int size = 1;

	private Position position = GameSettings.DEFAULT_POSITION.copy();
	private Position lastKnownRegion;
	
	public int getIndex() {
		return index;
	}

	public Entity setIndex(int index) {
		this.index = index;
		return this;
	}

	public Position getLastKnownRegion() {
		return lastKnownRegion;
	}

	public Entity setLastKnownRegion(Position lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
		return this;
	}

	public Entity setPosition(Position position) {
		this.position = position;
		return this;
	}

	public Position getPosition() {
		return position;
	}

	public void performAnimation(Animation animation) {

	}

	public void performGraphic(Graphic graphic) {

	}

	public int getSize() {
		return size;
	}

	public Entity setSize(int size) {
		this.size = size;
		return this;
	}

	public boolean isNpc() {
		return this instanceof NPC;
	}

	public boolean isPlayer() {
		return this instanceof Player;
	}

	public boolean isGameObject() {
		return this instanceof GameObject;
	}
}
