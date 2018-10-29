package com.agaroth.world.content.combat;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import com.agaroth.model.CombatIcon;
import com.agaroth.model.Hit;
import com.agaroth.model.Hitmask;
import com.agaroth.util.Misc;
import com.agaroth.world.content.combat.weapon.CombatSpecial;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.player.Player;

public class CombatContainer {

	private Character attacker;
	private Character victim;
	private ContainerHit[] hits;
	private int[] experience;
	private CombatType combatType;
	private boolean checkAccuracy;
	private boolean accurate;
	private int modifiedDamage;
	private int hitDelay;

	public CombatContainer(Character attacker, Character victim, int hitAmount, CombatType hitType, boolean checkAccuracy) {
		this.attacker = attacker;
		this.victim = victim;
		this.combatType = hitType;
		this.checkAccuracy = checkAccuracy;
		this.hits = prepareHits(hitAmount);
		this.experience = getSkills(hitType);
		this.hitDelay = hitType == CombatType.MELEE ? 0 : hitType == CombatType.RANGED ? 1 : hitType == CombatType.MAGIC || hitType == CombatType.DRAGON_FIRE ? 2 : 1;
	}
	
	public CombatContainer(Character attacker, Character victim, int hitAmount, int hitDelay, CombatType hitType, boolean checkAccuracy) {
		this.attacker = attacker;
		this.victim = victim;
		this.combatType = hitType;
		this.checkAccuracy = checkAccuracy;
		this.hits = prepareHits(hitAmount);
		this.experience = getSkills(hitType);
		this.hitDelay = hitDelay;
	}

	public CombatContainer(Character attacker, Character victim, CombatType hitType, boolean checkAccuracy) {
		this(attacker, victim, 0, hitType, checkAccuracy);
	}

	private final ContainerHit[] prepareHits(int hitAmount) {
		if (hitAmount > 4) {
			throw new IllegalArgumentException(
					"Illegal number of hits! The maximum number of hits per turn is 4.");
		} else if (hitAmount < 0) {
			throw new IllegalArgumentException(
					"Illegal number of hits! The minimum number of hits per turn is 0.");
		}
		if (hitAmount == 0) {
			accurate = checkAccuracy ? CombatFactory.rollAccuracy(attacker, victim, combatType) : true;
			return new ContainerHit[] {};
		}

		ContainerHit[] array = new ContainerHit[hitAmount];
		for (int i = 0; i < array.length; i++) {
			boolean accuracy = checkAccuracy ? CombatFactory.rollAccuracy(attacker, victim, combatType) : true;
			array[i] = new ContainerHit(CombatFactory.getHit(attacker, victim, combatType), accuracy);
			if (array[i].isAccurate()) {
				accurate = true;
			}
		}

		if(attacker.isPlayer() && ((Player)attacker).isSpecialActivated()) {
			if(((Player)attacker).getCombatSpecial() == CombatSpecial.DRAGON_CLAWS && hitAmount == 4) {
				int first = array[0].getHit().getDamage();
				if(first > 360) {
					first = 360 + Misc.getRandom(10);
				}
				int second = first <= 0 ? array[1].getHit().getDamage() : (int) (first/2);
				int third = first <= 0 && second > 0 ? (int) (second/2) :  first <= 0 && second <= 0 ? array[2].getHit().getDamage() :  Misc.getRandom(second);
				int fourth = first <= 0 && second <= 0 && third <= 0 ? (int) array[3].getHit().getDamage() + Misc.getRandom(7) : first <= 0 && second <= 0 ? array[3].getHit().getDamage() : third;
				array[0].getHit().setDamage(first);
				array[1].getHit().setDamage(second);
				array[2].getHit().setDamage(third);
				array[3].getHit().setDamage(fourth);
			} else if(((Player)attacker).getCombatSpecial() == CombatSpecial.DARK_BOW && hitAmount == 2) {
				for(int i = 0; i < hitAmount; i++) {
					if(array[i].getHit().getDamage() < 80) {
						array[i].getHit().setDamage(80);
					}
					array[i].setAccurate(true);
				}
			}
		}
		return array;
	}

	public void setHits(ContainerHit[] hits) {
		this.hits = hits;
		prepareHits(hits.length);
	}

	protected final void allHits(Consumer<ContainerHit> c) {
		Arrays.stream(hits).filter(Objects::nonNull).forEach(c);
	}

	public final int getDamage() {
		int damage = 0;
		for (ContainerHit hit : hits) {
			if (hit == null)
				continue;
			if (!hit.accurate) {
				int absorb = hit.getHit().getAbsorb();
				hit.hit = new Hit(0, Hitmask.RED, CombatIcon.BLOCK);
				hit.hit.setAbsorb(absorb);
			}
			damage += hit.hit.getDamage();
		}
		return damage;
	}
	
	public final void dealDamage() {
		if (hits.length == 1) {
			victim.dealDamage(hits[0].getHit());
		} else if (hits.length == 2) {
			victim.dealDoubleDamage(hits[0].getHit(), hits[1].getHit());
		} else if (hits.length == 3) {
			victim.dealTripleDamage(hits[0].getHit(), hits[1].getHit(), hits[2].getHit());
		} else if (hits.length == 4) {
			victim.dealQuadrupleDamage(hits[0].getHit(), hits[1].getHit(), hits[2].getHit(), hits[3].getHit());
		}
	}

	private final int[] getSkills(CombatType type) {
		if (attacker.isNpc()) {
			return new int[] {};
		}
		return ((Player) attacker).getFightType().getStyle().skill(type);
	}
	
	public void setModifiedDamage(int modifiedDamage) {
		this.modifiedDamage = modifiedDamage;
	}
	
	public int getModifiedDamage() {
		return modifiedDamage;
	}

	public void onHit(int damage, boolean accurate) {}
	public final ContainerHit[] getHits() {
		return hits;
	}
	
	public final int[] getExperience() {
		return experience;
	}

	public final void setHitAmount(int hitAmount) {
		this.hits = prepareHits(hitAmount);
	}

	public final CombatType getCombatType() {
		return combatType;
	}

	public final void setCombatType(CombatType combatType) {
		this.combatType = combatType;
	}

	public final boolean isCheckAccuracy() {
		return checkAccuracy;
	}

	public final void setCheckAccuracy(boolean checkAccuracy) {
		this.checkAccuracy = checkAccuracy;
	}

	public final boolean isAccurate() {
		return accurate;
	}

	public int getHitDelay() {
		return hitDelay;
	}

	public static class ContainerHit {
		private Hit hit;
		private boolean accurate;
		public ContainerHit(Hit hit, boolean accurate) {
			this.hit = hit;
			this.accurate = accurate;
		}

		public Hit getHit() {
			return hit;
		}

		public void setHit(Hit hit) {
			this.hit = hit;
		}

		public boolean isAccurate() {
			return accurate;
		}

		public void setAccurate(boolean accurate) {
			this.accurate = accurate;
		}
	}
}