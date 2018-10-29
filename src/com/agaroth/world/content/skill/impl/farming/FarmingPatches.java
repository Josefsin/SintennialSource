package com.agaroth.world.content.skill.impl.farming;

public enum FarmingPatches {
	SOUTH_FALADOR_HERB(3058, 3310, 3060, 3313, 1, 515, 5343, 2275, 5329, SeedType.HERB),
	SOUTH_FALADOR_FLOWER(3054, 3306, 3056, 3307, 1, 508, 5343, 2275, 5329, SeedType.FLOWER),
	SOUTH_FALADOR_ALLOTMENT_WEST(3050, 3306, 3055, 3312, 1, 504, 5343, 2275, 5329, SeedType.ALLOTMENT),
	SOUTH_FALADOR_ALLOTMENT_SOUTH(3055, 3302, 3059, 3309, 1, 504, 5343, 2275, 5329, SeedType.ALLOTMENT);
	
	public final int x;
	public final int y;
	public final int x2;
	public final int y2;
	public final int mod;
	public final int config;
	public final int harvestAnimation;
	public final int harvestItem;
	public final int planter;
	public final SeedType seedType;

	private FarmingPatches(int x, int y, int x2, int y2, int mod, int config, int planter, int harvestAnimation,
			int harvestItem, SeedType seedType) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
		this.mod = mod;
		this.config = config;
		this.harvestAnimation = harvestAnimation;
		this.harvestItem = harvestItem;
		this.planter = planter;
		this.seedType = seedType;
	}
}
