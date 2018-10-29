package com.agaroth.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


public enum PlayerRights {
	 PLAYER(-1, null, 1.0D, 1.0),
	 MODERATOR(-1, "<col=CCCCCC><shad=0>", 1.0, 1.0),//20B2AA
	 ADMINISTRATOR(-1, "<col=FFFF64><shad=0>", 1.0, 1.0),
	 OWNER(-1, "<col=B40404>", 1.0, 1.0),
	 DEVELOPER(-1, "<shad=B40404>", 1.0, 1.0),
	 DONATOR(60, "<shad=FF7F00>", 1.5, 1.1),
	 SUPER_DONATOR(40, "<shad=0><col=ADD8E6>", 1.5, 1.25),//787878
	 EXTREME_DONATOR(20, "<shad=0><col=00FF00>", 2.0, 1.5),//787878
	 LEGENDARY_DONATOR(10, "<shad=551A8B>", 2.5, 1.7),//697998
	 UBER_DONATOR(0, "<shad=FFFF00>", 3.0, 2.0),//0EBFE9
	 GLOBAL_MODERATOR(30, "<col=006400>", 1.0, 1.0),//CD661D
	 SUPPORT(-1, "<col=0000FF><shad=0>", 1.0, 1.0);//FF0000


	PlayerRights(int yellDelaySeconds, String yellHexColorPrefix, double loyaltyPointsGainModifier, double experienceGainModifier) {
		this.yellDelay = yellDelaySeconds;
		this.yellHexColorPrefix = yellHexColorPrefix;
		this.loyaltyPointsGainModifier = loyaltyPointsGainModifier;
		this.experienceGainModifier = experienceGainModifier;
	}
	
	private static final ImmutableSet<PlayerRights> STAFF = Sets.immutableEnumSet(SUPPORT, MODERATOR, ADMINISTRATOR, OWNER, DEVELOPER, GLOBAL_MODERATOR);
	private static final ImmutableSet<PlayerRights> MEMBERS = Sets.immutableEnumSet(DONATOR, SUPER_DONATOR, EXTREME_DONATOR, LEGENDARY_DONATOR, UBER_DONATOR);

	private int yellDelay;
	private String yellHexColorPrefix;
	private double loyaltyPointsGainModifier;
	private double experienceGainModifier;
	
	public int getYellDelay() {
		return yellDelay;
	}

	public String getYellPrefix() {
		return yellHexColorPrefix;
	}

	public double getLoyaltyPointsGainModifier() {
		return loyaltyPointsGainModifier;
	}
	
	public double getExperienceGainModifier() {
		return experienceGainModifier;
	}
	
	public boolean isStaff() {
		return STAFF.contains(this);
	}
	
	public boolean isMember() {
		return MEMBERS.contains(this);
	}

	public static PlayerRights forId(int id) {
		for (PlayerRights rights : PlayerRights.values()) {
			if (rights.ordinal() == id) {
				return rights;
			}
		}
		return null;
	}
}