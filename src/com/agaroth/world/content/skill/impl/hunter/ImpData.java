package com.agaroth.world.content.skill.impl.hunter;

public enum ImpData {
	BABY( "Baby Impling", 11238, 5577, 1, 6055 ),
	YOUNG( "Young Impling", 11240, 6355, 17, 6056 ),
	GOURMET( "Gourmet Impling", 11242, 6980, 34, 6057),
	EARTH( "Earth Impling", 11244, 6944, 34, 6058 ),
	ESSENCE( "Essence Impling", 11246, 7211, 40, 6059 ),
	ELECTIC( "Electic Impling", 11248, 7900, 50, 6060),
	NATURE( "Nature Impling", 11250, 8352, 58,6061 ),
	MAGPIE( "Magpie Impling", 11252, 8876, 65, 6062 ),
	NINJA( "Ninja Impling", 11254, 9554, 74, 6063 ), 
	DRAGON( "Dragon Impling", 11256, 17888, 83, 6064 ), 
	KINGLY( "Kingly Impling", 15517, 54020, 91, 7903 );

	public String name;
	public int impJar, XPReward, levelReq, npcId;

	ImpData(String name, int JarAdded, int XPAdded, int LevelNeed, int Npc) {
		this.name = name;
		this.impJar = JarAdded;
		this.XPReward = XPAdded;
		this.levelReq = LevelNeed;
		this.npcId = Npc;
	}
	
	public static ImpData forId(int npcId) {
		for(ImpData imps : ImpData.values()) {
			if(imps.npcId == npcId) {
				return imps;
			}
		}
		return null;
	}
}