package com.agaroth.world.content.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.agaroth.model.DamageDealer;
import com.agaroth.util.Misc;
import com.agaroth.util.Stopwatch;
import com.agaroth.world.content.combat.CombatContainer.ContainerHit;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class CombatBuilder {
	private Character character;
	private Character victim;
	private Character lastAttacker;
	private HitQueue hitQueue = new HitQueue();
	private CombatSession combatSession;
	private CombatDistanceSession distanceSession;
	private CombatContainer container;
	private Map<Player, CombatDamageCache> damageMap = new HashMap<>();
	private Stopwatch lastAttack = new Stopwatch();
	private CombatStrategy strategy;
	protected int attackTimer;
	protected int cooldown;
	private boolean retaliated;
	
	public CombatBuilder(Character entity) {
		this.character = entity;
	}
	
	public void process() {
		hitQueue.process();
		if(distanceSession != null) {
			distanceSession.process();
		}
		if(combatSession != null) {
			combatSession.process();
		}
	}


	public void attack(Character target) {
		if (character.equals(target)) {
			return;
		}

		if (target.equals(victim)) {
			determineStrategy();

			if (!character.getPosition().equals(victim.getPosition()) && character.getPosition().isWithinDistance(victim.getPosition(), strategy.attackDistance(character))) {
				character.getMovementQueue().reset();
			}
		}

		character.getMovementQueue().setFollowCharacter(target);
		if(character.getInteractingEntity() != target)
			character.setEntityInteraction(target);
		if (combatSession != null) {
			victim = target;
			
			if(lastAttacker == null || lastAttacker != victim) {
				setDidAutoRetaliate(false);
			}
			
			if (character.isPlayer()) {
				Player player = (Player) character;
				if (player.isAutocast() || player.getCastSpell() == null || attackTimer < 1) {
					cooldown = 0;
				}
			}
			return;
		}
		distanceSession = new CombatDistanceSession(this, target);
	}

	public void reset(boolean resetAttack) {
		victim = null;
		distanceSession = null;
		combatSession = null;
		container = null;
		if(resetAttack) {
			attackTimer = 0;
		}
		strategy = null;
		cooldown = 0;
		character.setEntityInteraction(null);
		character.getMovementQueue().setFollowCharacter(null);
		
		if(character.isPlayer()) {
			((Player)character).getPacketSender().sendHideCombatBox();
		}
	}

	public void cooldown(boolean resetAttack) {
		if (strategy == null)
			return;
		cooldown = 10;
		character.getMovementQueue().setFollowCharacter(null);
		character.setEntityInteraction(null);
		if (resetAttack) {
			attackTimer = strategy.attackDelay(character);
		}
	}

	public void resetCooldown() {
		this.cooldown = 0;
	}

	public DamageDealer getTopDamageDealer(boolean clearMap, List<String> ignores) {
		if (damageMap.size() == 0) {
			return null;
		}
		int damage = 0;
		Player killer = null;

		for (Entry<Player, CombatDamageCache> entry : damageMap.entrySet()) {
			if (entry == null) {
				continue;
			}
			long timeout = entry.getValue().getStopwatch().elapsed();
			if (timeout > CombatFactory.DAMAGE_CACHE_TIMEOUT) {
				continue;
			}
			Player player = entry.getKey();
			if (player.getConstitution() <= 0 || !player.isRegistered()) {
				continue;
			}
			if(ignores != null && ignores.contains(player.getUsername())) {
				continue;
			}
			if (entry.getValue().getDamage() > damage) {
				damage = entry.getValue().getDamage();
				killer = entry.getKey();
			}
		}
		if (clearMap)
			damageMap.clear();
		return new DamageDealer(killer, damage);
	}
	
	public List<DamageDealer> getTopKillers(NPC npc) {
		List<DamageDealer> list = new ArrayList<DamageDealer>();
		List<String> ignores = new ArrayList<String>();
		for(int i = 0; i < 5; i++) {
			DamageDealer damageDealer = getTopDamageDealer(false, ignores);
			if(damageDealer == null || damageDealer.getPlayer() == null) {
				break;
			}
			list.add(damageDealer);
			ignores.add(damageDealer.getPlayer().getUsername());
		}
		return list;
	}

	public void addDamage(Character entity, int amount) {
		if (amount < 1 || entity.isNpc()) {
			return;
		}
		if(this.character.isNpc()) {
			((NPC)character).setFetchNewDamageMap(true);
		}
		Player player = (Player) entity;
		if (damageMap.containsKey(player)) {
			damageMap.get(player).incrementDamage(amount);
			return;
		}

		damageMap.put(player, new CombatDamageCache(amount));
	}

	public boolean isAttacking() {
		return victim != null;
	}

	public boolean isBeingAttacked() {
		return !character.getLastCombat().elapsed(5000);
	}
	
	public boolean logOutDelay() {
		return !character.getLastCombat().elapsed(12000);
	}

	public Character getCharacter() {
		return character;
	}

	public Character getVictim() {
		return victim;
	}

	public void setVictim(Character victim) {
		this.victim = victim;
	}

	public boolean isCooldown() {
		return cooldown > 0;
	}

	public void setAttackTimer(int attackTimer) {
		this.attackTimer = attackTimer;
	}

	public CombatBuilder incrementAttackTimer(int amount) {
		this.attackTimer += amount;
		return this;
	}

	public int getAttackTimer() {
		return this.attackTimer;
	}

	public Character getLastAttacker() {
		return lastAttacker;
	}
	
	public void setLastAttacker(Character lastAttacker) {
		this.lastAttacker = lastAttacker;
	}

	public CombatStrategy getStrategy() {
		return strategy;
	}

	public CombatSession getCombatSession() {
		return combatSession;
	}

	public CombatDistanceSession getDistanceSession() {
		return distanceSession;
	}

	public HitQueue getHitQueue() {
		return hitQueue;
	}

	public void setCombatSession(CombatSession combatTask) {
		this.combatSession = combatTask;
	}

	public void setDistanceSession(CombatDistanceSession distanceTask) {
		this.distanceSession = distanceTask;
	}

	public void determineStrategy() {
		this.strategy = character.determineStrategy();
	}

	public CombatContainer getContainer() {
		if(this.container != null)
			return container;
		return strategy.attack(character, victim);
	}
	
	public boolean didAutoRetaliate() {
		return retaliated;
	}
	
	public void setDidAutoRetaliate(boolean retaliated) {
		this.retaliated = retaliated;
	}
	
	public Stopwatch getLastAttack() {
		return lastAttack;
	}

	public void setContainer(CombatContainer customContainer) {
		if(customContainer != null && customContainer.getHits() != null && this.container != null) {
			ContainerHit[] totalHits = Misc.concat(this.container.getHits(), customContainer.getHits());
			this.container = customContainer;
			if(!(totalHits.length > 4 || totalHits.length < 0)) {
				this.container.setHits(totalHits);
			}
		} else
			this.container = customContainer;
	}

	private static class CombatDamageCache {
		private int damage;
		private final Stopwatch stopwatch;
		
		public CombatDamageCache(int damage) {
			this.damage = damage;
			this.stopwatch = new Stopwatch().reset();
		}

		public int getDamage() {
			return damage;
		}

		public void incrementDamage(int damage) {
			this.damage += damage;
			this.stopwatch.reset();
		}

		public Stopwatch getStopwatch() {
			return stopwatch;
		}
	}
	public void instant() {
		combatSession.process();
	}
}