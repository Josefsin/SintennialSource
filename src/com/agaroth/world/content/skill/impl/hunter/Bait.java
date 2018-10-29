package com.agaroth.world.content.skill.impl.hunter;

public enum Bait {
	SPICY_MINCED_MEAT(9996),
	TOMATO(1982);

	private int baitId;

	Bait(int baitId) {
		this.baitId = baitId;
	}

	public int getBaitId() {
		return baitId;
	}

}
