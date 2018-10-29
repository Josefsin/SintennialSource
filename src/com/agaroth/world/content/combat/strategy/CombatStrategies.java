package com.agaroth.world.content.combat.strategy;

import java.util.HashMap;
import java.util.Map;

import com.agaroth.world.content.combat.strategy.impl.AberrantSpectre;
import com.agaroth.world.content.combat.strategy.impl.Aviansie;
import com.agaroth.world.content.combat.strategy.impl.BandosAvatar;
import com.agaroth.world.content.combat.strategy.impl.BloodReaver;
import com.agaroth.world.content.combat.strategy.impl.ChaosElemental;
import com.agaroth.world.content.combat.strategy.impl.CorporealBeast;
import com.agaroth.world.content.combat.strategy.impl.DagannothSupreme;
import com.agaroth.world.content.combat.strategy.impl.DefaultMagicCombatStrategy;
import com.agaroth.world.content.combat.strategy.impl.DefaultMeleeCombatStrategy;
import com.agaroth.world.content.combat.strategy.impl.DefaultRangedCombatStrategy;
import com.agaroth.world.content.combat.strategy.impl.Dragon;
import com.agaroth.world.content.combat.strategy.impl.Geerin;
import com.agaroth.world.content.combat.strategy.impl.Glacor;
import com.agaroth.world.content.combat.strategy.impl.Graardor;
import com.agaroth.world.content.combat.strategy.impl.Grimspike;
import com.agaroth.world.content.combat.strategy.impl.Gritch;
import com.agaroth.world.content.combat.strategy.impl.Growler;
import com.agaroth.world.content.combat.strategy.impl.Jad;
import com.agaroth.world.content.combat.strategy.impl.KalphiteQueen;
import com.agaroth.world.content.combat.strategy.impl.KreeArra;
import com.agaroth.world.content.combat.strategy.impl.Kreeyath;
import com.agaroth.world.content.combat.strategy.impl.Nex;
import com.agaroth.world.content.combat.strategy.impl.Nomad;
import com.agaroth.world.content.combat.strategy.impl.PlaneFreezer;
import com.agaroth.world.content.combat.strategy.impl.Revenant;
import com.agaroth.world.content.combat.strategy.impl.Scorpia;
import com.agaroth.world.content.combat.strategy.impl.Spinolyp;
import com.agaroth.world.content.combat.strategy.impl.Steelwill;
import com.agaroth.world.content.combat.strategy.impl.TormentedDemon;
import com.agaroth.world.content.combat.strategy.impl.Tsutsuroth;
import com.agaroth.world.content.combat.strategy.impl.Vennatis;
import com.agaroth.world.content.combat.strategy.impl.WingmanSkree;
import com.agaroth.world.content.combat.strategy.impl.Zilyana;

public class CombatStrategies {

	private static final DefaultMeleeCombatStrategy defaultMeleeCombatStrategy = new DefaultMeleeCombatStrategy();
	private static final DefaultMagicCombatStrategy defaultMagicCombatStrategy = new DefaultMagicCombatStrategy();
	private static final DefaultRangedCombatStrategy defaultRangedCombatStrategy = new DefaultRangedCombatStrategy();
	private static final Map<Integer, CombatStrategy> STRATEGIES = new HashMap<Integer, CombatStrategy>();
	
	public static void init() {
		DefaultMagicCombatStrategy defaultMagicStrategy = new DefaultMagicCombatStrategy();
	    STRATEGIES.put(13, defaultMagicStrategy);
	    STRATEGIES.put(172, defaultMagicStrategy);
	    STRATEGIES.put(174, defaultMagicStrategy);
	    STRATEGIES.put(2025, defaultMagicStrategy);
	    STRATEGIES.put(3495, defaultMagicStrategy);
	    STRATEGIES.put(3496, defaultMagicStrategy);
	    STRATEGIES.put(3491, defaultMagicStrategy);
	    STRATEGIES.put(2882, defaultMagicStrategy);
	    STRATEGIES.put(13451, defaultMagicStrategy);
	    STRATEGIES.put(13452, defaultMagicStrategy);
	    STRATEGIES.put(13453, defaultMagicStrategy);
	    STRATEGIES.put(13454, defaultMagicStrategy);
	    STRATEGIES.put(1643, defaultMagicStrategy);
	    STRATEGIES.put(6254, defaultMagicStrategy);
	    STRATEGIES.put(6257, defaultMagicStrategy);
	    STRATEGIES.put(6278, defaultMagicStrategy);
	    STRATEGIES.put(6221, defaultMagicStrategy);
	    STRATEGIES.put(2007, defaultMagicStrategy);
	    STRATEGIES.put(109, defaultMagicStrategy);
		STRATEGIES.put(3580, defaultMagicStrategy);
	    
	    DefaultRangedCombatStrategy defaultRangedStrategy = new DefaultRangedCombatStrategy();
	    STRATEGIES.put(688, defaultRangedStrategy);
	    STRATEGIES.put(2028, defaultRangedStrategy);
	    STRATEGIES.put(6220, defaultRangedStrategy);
	    STRATEGIES.put(6256, defaultRangedStrategy);
	    STRATEGIES.put(6276, defaultRangedStrategy);
	    STRATEGIES.put(6252, defaultRangedStrategy);
	    STRATEGIES.put(27, defaultRangedStrategy);
	    
	    STRATEGIES.put(13458, new BloodReaver());
	    STRATEGIES.put(2001, new Scorpia());
	    STRATEGIES.put(1604, new AberrantSpectre());
	    STRATEGIES.put(2000, new Vennatis());
	    STRATEGIES.put(2745, new Jad());
	    STRATEGIES.put(8528, new Nomad());
	    STRATEGIES.put(8349, new TormentedDemon());
	    STRATEGIES.put(3200, new ChaosElemental());
	    STRATEGIES.put(4540, new BandosAvatar());
	    STRATEGIES.put(8133, new CorporealBeast());
	    STRATEGIES.put(13447, new Nex());
	    STRATEGIES.put(2896, new Spinolyp());
	    STRATEGIES.put(2881, new DagannothSupreme());
	    STRATEGIES.put(6260, new Graardor());
	    STRATEGIES.put(6263, new Steelwill());
	    STRATEGIES.put(6265, new Grimspike());
	    STRATEGIES.put(6222, new KreeArra());
	    STRATEGIES.put(6223, new WingmanSkree());
	    STRATEGIES.put(6225, new Geerin());
	    STRATEGIES.put(6203, new Tsutsuroth());
	    STRATEGIES.put(6208, new Kreeyath());
	    STRATEGIES.put(6206, new Gritch());
	    STRATEGIES.put(6247, new Zilyana());
	    STRATEGIES.put(6250, new Growler());
	    STRATEGIES.put(1382, new Glacor());
	    STRATEGIES.put(9939, new PlaneFreezer());
	    
	    Dragon dragonStrategy = new Dragon();
	    STRATEGIES.put(50, dragonStrategy);
	    STRATEGIES.put(941, dragonStrategy);
	    STRATEGIES.put(55, dragonStrategy);
	    STRATEGIES.put(53, dragonStrategy);
	    STRATEGIES.put(54, dragonStrategy);
	    STRATEGIES.put(51, dragonStrategy);
	    STRATEGIES.put(1590, dragonStrategy);
	    STRATEGIES.put(1591, dragonStrategy);
	    STRATEGIES.put(1592, dragonStrategy);
	    STRATEGIES.put(5362, dragonStrategy);
	    STRATEGIES.put(5363, dragonStrategy);
	    
	    Aviansie aviansieStrategy = new Aviansie();
	    STRATEGIES.put(6246, aviansieStrategy);
	    STRATEGIES.put(6230, aviansieStrategy);
	    STRATEGIES.put(6231, aviansieStrategy);
	    
	    KalphiteQueen kalphiteQueenStrategy = new KalphiteQueen();
	    STRATEGIES.put(1158, kalphiteQueenStrategy);
	    STRATEGIES.put(1160, kalphiteQueenStrategy);
	    
	    Revenant revenantStrategy = new Revenant();
	    STRATEGIES.put(13465, revenantStrategy);
	    STRATEGIES.put(13469, revenantStrategy);
	    STRATEGIES.put(13474, revenantStrategy);
	    STRATEGIES.put(13478, revenantStrategy);
	    STRATEGIES.put(13479, revenantStrategy);
	}
	
	public static CombatStrategy getStrategy(int npc) {
		if(STRATEGIES.get(npc) != null) {
			return STRATEGIES.get(npc);
		}
		return defaultMeleeCombatStrategy;
	}
	
	public static CombatStrategy getDefaultMeleeStrategy() {
		return defaultMeleeCombatStrategy;
	}

	public static CombatStrategy getDefaultMagicStrategy() {
		return defaultMagicCombatStrategy;
	}


	public static CombatStrategy getDefaultRangedStrategy() {
		return defaultRangedCombatStrategy;
	}
}
