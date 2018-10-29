package com.agaroth.world.content.combat;

import java.util.Optional;

import com.agaroth.GameSettings;
import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.engine.task.impl.CombatSkullEffect;
import com.agaroth.model.Animation;
import com.agaroth.model.CombatIcon;
import com.agaroth.model.Flag;
import com.agaroth.model.Graphic;
import com.agaroth.model.GraphicHeight;
import com.agaroth.model.Hit;
import com.agaroth.model.Hitmask;
import com.agaroth.model.Item;
import com.agaroth.model.Locations;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.model.Projectile;
import com.agaroth.model.Skill;
import com.agaroth.model.Locations.Location;
import com.agaroth.model.container.impl.Equipment;
import com.agaroth.model.movement.MovementQueue;
import com.agaroth.model.movement.PathFinder;
import com.agaroth.util.Misc;
import com.agaroth.world.clip.region.RegionClipping;
import com.agaroth.world.content.BonusManager;
import com.agaroth.world.content.ItemDegrading;
import com.agaroth.world.content.ItemDegrading.DegradingItem;
import com.agaroth.world.content.combat.effect.CombatPoisonEffect;
import com.agaroth.world.content.combat.effect.EquipmentBonus;
import com.agaroth.world.content.combat.effect.CombatPoisonEffect.PoisonType;
import com.agaroth.world.content.combat.magic.CombatAncientSpell;
import com.agaroth.world.content.combat.prayer.CurseHandler;
import com.agaroth.world.content.combat.prayer.PrayerHandler;
import com.agaroth.world.content.combat.range.CombatRangedAmmo.RangedWeaponData;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.content.combat.strategy.impl.Nex;
import com.agaroth.world.content.combat.weapon.CombatSpecial;
import com.agaroth.world.content.combat.weapon.FightStyle;
import com.agaroth.world.content.transportation.TeleportHandler;
import com.agaroth.world.content.transportation.TeleportType;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.npc.NPCMovementCoordinator.CoordinateState;
import com.agaroth.world.entity.impl.player.Player;
import com.agaroth.world.content.Kraken.KrakenInstance;

public final class CombatFactory {

	public static final long DAMAGE_CACHE_TIMEOUT = 60000;
	public static final double PRAYER_DAMAGE_REDUCTION = .20;
	public static final double PRAYER_ACCURACY_REDUCTION = .255;
	public static final double REDEMPTION_PRAYER_HEAL = .25;
	public static final int MAXIMUM_RETRIBUTION_DAMAGE = 150;
	public static final int RETRIBUTION_RADIUS = 5;

	private CombatFactory() {
		throw new UnsupportedOperationException(
				"This class cannot be instantiated!");
	}

	public static boolean fullVeracs(Character entity) {
		return entity.isNpc() ? ((NPC) entity).getDefinition().getName().equals(
				"Verac the Defiled")
				: ((Player) entity).getEquipment().containsAll(4753, 4757, 4759,
						4755);
	}

	public static boolean fullDharoks(Character entity) {
		return entity.isNpc() ? ((NPC) entity).getDefinition().getName().equals(
				"Dharok the Wretched")
				: ((Player) entity).getEquipment().containsAll(4716, 4720, 4722,
						4718);
	}

	public static boolean fullKarils(Character entity) {
		return entity.isNpc() ? ((NPC) entity).getDefinition().getName().equals(
				"Karil the Tainted")
				: ((Player) entity).getEquipment().containsAll(4732, 4736, 4738,
						4734);
	}

	public static boolean fullAhrims(Character entity) {
		return entity.isNpc() ? ((NPC) entity).getDefinition().getName().equals(
				"Ahrim the Blighted")
				: ((Player) entity).getEquipment().containsAll(4708, 4712, 4714,
						4710);
	}

	public static boolean fullTorags(Character entity) {
		return entity.isNpc() ? ((NPC) entity).getDefinition().getName().equals(
				"Torag the Corrupted")
				: ((Player) entity).getEquipment().containsAll(4745, 4749, 4751,
						4747);
	}

	public static boolean fullGuthans(Character entity) {
		return entity.isNpc() ? ((NPC) entity).getDefinition().getName().equals(
				"Guthan the Infested")
				: ((Player) entity).getEquipment().containsAll(4724, 4728, 4730,
						4726);
	}

	public static boolean crystalBow(Player player) {
		Item item = player.getEquipment().get(Equipment.WEAPON_SLOT);
		if (item == null)
			return false;
		return item.getDefinition().getName().toLowerCase().contains(
				"crystal bow");
	}

	public static boolean darkBow(Player player) {
		Item item = player.getEquipment().get(Equipment.WEAPON_SLOT);
		if (item == null)
			return false;
		return item.getDefinition().getName().toLowerCase().contains(
				"dark bow");
	}

	public static boolean arrowsEquipped(Player player) {
		Item item;
		if ((item = player.getEquipment().get(Equipment.AMMUNITION_SLOT)) == null) {
			return false;
		}

		return !(!item.getDefinition().getName().endsWith("arrow") && !item.getDefinition().getName().endsWith(
				"arrowp") && !item.getDefinition().getName().endsWith(
						"arrow(p+)") && !item.getDefinition().getName().endsWith(
								"arrow(p++)"));
	}

	public static boolean boltsEquipped(Player player) {
		Item item;
		if ((item = player.getEquipment().get(Equipment.AMMUNITION_SLOT)) == null) {
			return false;
		}
		return item.getDefinition().getName().toLowerCase().contains("bolts");
	}

	public static void poisonEntity(Character entity, Optional<PoisonType> poisonType) {
		if (entity.isPoisoned() || !poisonType.isPresent()) {
			return;
		}

		if (entity.isPlayer()) {
			Player player = (Player) entity;
			if (player.getPoisonImmunity() > 0)
				return;
			player.getPacketSender().sendMessage("You have been poisoned!");
		}

		entity.setPoisonDamage(poisonType.get().getDamage());
		TaskManager.submit(new CombatPoisonEffect(entity));
	}

	public static void poisonEntity(Character entity, PoisonType poisonType) {
		poisonEntity(entity, Optional.ofNullable(poisonType));
	}

	public static void skullPlayer(Player player) {
		if (player.getSkullTimer() > 0) {
			return;
		}
		player.setSkullTimer(300);
		player.setSkullIcon(1);
		player.getPacketSender().sendMessage("@red@You have been skulled! You now lose all unprotected items!");
		TaskManager.submit(new CombatSkullEffect(player));
		player.getUpdateFlag().flag(Flag.APPEARANCE);
	}

	public static int combatLevelDifference(int combatLevel,
			int otherCombatLevel) {
		if (combatLevel > otherCombatLevel) {
			return (combatLevel - otherCombatLevel);
		} else if (otherCombatLevel > combatLevel) {
			return (otherCombatLevel - combatLevel);
		} else {
			return 0;
		}
	}

	public static int getLevelDifference(Player player, boolean up) {
		int max = player.getLocation() == Location.WILDERNESS ? 126 : 138;
		int wildLevel = player.getWildernessLevel() + 5;
		int combatLevel = player.getSkillManager().getCombatLevel();
		int difference = up ? combatLevel + wildLevel : combatLevel - wildLevel;
		return difference < 3 ? 3 : difference > max && up ? max : difference;
	}

	public static Hit getHit(Character entity, Character victim, CombatType type) {
		switch (type) {
		case MELEE:
			return new Hit(Misc.inclusiveRandom(1, DesolaceFormulas.calculateMaxMeleeHit(entity, victim)), Hitmask.RED, CombatIcon.MELEE);
		case RANGED:
			return new Hit(Misc.inclusiveRandom(1, CombatFactory.calculateMaxRangedHit(entity, victim)), Hitmask.RED, CombatIcon.RANGED);
		case MAGIC:
			return new Hit(Misc.inclusiveRandom(1, DesolaceFormulas.getMagicMaxhit(entity)), Hitmask.RED, CombatIcon.MAGIC);
		case DRAGON_FIRE:
			return new Hit(Misc.inclusiveRandom(0, CombatFactory.calculateMaxDragonFireHit(entity, victim)), Hitmask.RED, CombatIcon.MAGIC);
		default:
			throw new IllegalArgumentException("Invalid combat type: " + type);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public static boolean rollAccuracy(Character attacker, Character victim, CombatType type) {

		if(attacker.isPlayer() && victim.isPlayer()) {
			Player p1 = (Player)attacker;
			Player p2 = (Player)victim;
			switch(type) {
			case MAGIC:
				int mageAttk = DesolaceFormulas.getMagicAttack(p1);
				return Misc.getRandom(DesolaceFormulas.getMagicDefence(p2)) < Misc.getRandom((mageAttk / 2)) + Misc.getRandom((int) (mageAttk/2.1));
			case MELEE:
				int def = 1 + DesolaceFormulas.getMeleeDefence(p2);
				return Misc.getRandom(def) < Misc.getRandom(1 + DesolaceFormulas.getMeleeAttack(p1)) + (def/4.5);
			case RANGED:
				return Misc.getRandom(10 + DesolaceFormulas.getRangedDefence(p2)) < Misc.getRandom(15 + DesolaceFormulas.getRangedAttack(p1));
			}
		} else if(attacker.isPlayer() && victim.isNpc() && type != CombatType.MAGIC) {
			Player p1 = (Player)attacker;
			NPC n = (NPC)victim;
			switch(type) {
			/*	case MAGIC:
			case KORASI:
				int mageAttk = DesolaceFormulas.getMagicAttack(p1);
				return Misc.getRandom(n.getDefinition().getDefenceMage()) < Misc.getRandom((mageAttk / 2)) + Misc.getRandom((int) (mageAttk/2.1));
			 */
			case MELEE:
				int def = 1 + n.getDefinition().getDefenceMelee();
				return Misc.getRandom(def) < Misc.getRandom(5 + DesolaceFormulas.getMeleeAttack(p1)) + (def/4);
			case RANGED:
				return Misc.getRandom(5 + n.getDefinition().getDefenceRange()) < Misc.getRandom(5 + DesolaceFormulas.getRangedAttack(p1));
			}//ToDo magic thing
		}

		boolean veracEffect = false;

		if (type == CombatType.MELEE) {
			if (CombatFactory.fullVeracs(attacker)) {
				if (Misc.RANDOM.nextInt(8) == 3) {
					veracEffect = true;
				}
			}
		}

		if(type == CombatType.DRAGON_FIRE)
			type = CombatType.MAGIC;

		double prayerMod = 1;
		double equipmentBonus = 1;
		double specialBonus = 1;
		int styleBonus = 0;
		int bonusType = -1;
		if (attacker.isPlayer()) {
			Player player = (Player) attacker;

			equipmentBonus = type == CombatType.MAGIC ? player.getBonusManager().getAttackBonus()[BonusManager.ATTACK_MAGIC]
					: player.getBonusManager().getAttackBonus()[player.getFightType().getBonusType()];
			bonusType = player.getFightType().getCorrespondingBonus();

			if (type == CombatType.MELEE) {
				if (PrayerHandler.isActivated(player,
						PrayerHandler.CLARITY_OF_THOUGHT)) {
					prayerMod = 1.05;
				} else if (PrayerHandler.isActivated(player,
						PrayerHandler.IMPROVED_REFLEXES)) {
					prayerMod = 1.10;
				} else if (PrayerHandler.isActivated(player,
						PrayerHandler.INCREDIBLE_REFLEXES)) {
					prayerMod = 1.15;
				} else if (PrayerHandler.isActivated(player,
						PrayerHandler.CHIVALRY)) {
					prayerMod = 1.15;
				} else if (PrayerHandler.isActivated(player,
						PrayerHandler.PIETY)) {
					prayerMod = 1.20;
				}  else if (PrayerHandler.isActivated(player,
						PrayerHandler.RIGOUR)) {
					prayerMod = 1.20;
				}  else if (PrayerHandler.isActivated(player,
						PrayerHandler.AUGURY)) {
					prayerMod = 1.20;
				} else if (CurseHandler.isActivated(player, CurseHandler.LEECH_ATTACK)) {
					prayerMod = 1.05 + + (player.getLeechedBonuses()[0] * 0.01);
				} else if (CurseHandler.isActivated(player, CurseHandler.TURMOIL)) {
					prayerMod = 1.15 + + (player.getLeechedBonuses()[2] * 0.01);
				}
			} else if (type == CombatType.RANGED) {
				if (PrayerHandler.isActivated(player, PrayerHandler.SHARP_EYE)) {
					prayerMod = 1.05;
				} else if (PrayerHandler.isActivated(player, PrayerHandler.HAWK_EYE)) {
					prayerMod = 1.10;
				} else if (PrayerHandler.isActivated(player, PrayerHandler.EAGLE_EYE)) {
					prayerMod = 1.15;
				}  else if (PrayerHandler.isActivated(player, PrayerHandler.RIGOUR)) {
					prayerMod = 1.22;
				} else if (CurseHandler.isActivated(player, CurseHandler.LEECH_RANGED)) {
					prayerMod = 1.05 + + (player.getLeechedBonuses()[4] * 0.01);
				}
			} else if (type == CombatType.MAGIC) {
				if (PrayerHandler.isActivated(player, PrayerHandler.MYSTIC_WILL)) {
					prayerMod = 1.05;
				} else if (PrayerHandler.isActivated(player, PrayerHandler.MYSTIC_LORE)) {
					prayerMod = 1.10;
				} else if (PrayerHandler.isActivated(player, PrayerHandler.MYSTIC_MIGHT)) {
					prayerMod = 1.15;
				} else if (PrayerHandler.isActivated(player, PrayerHandler.AUGURY)) {
					prayerMod = 1.22;
				} else if (CurseHandler.isActivated(player, CurseHandler.LEECH_MAGIC)) {
					prayerMod = 1.05 + + (player.getLeechedBonuses()[6] * 0.01);
				}
			}

			if (player.getFightType().getStyle() == FightStyle.ACCURATE) {
				styleBonus = 3;
			} else if (player.getFightType().getStyle() == FightStyle.CONTROLLED) {
				styleBonus = 1;
			}

			if (player.isSpecialActivated()) {
				specialBonus = player.getCombatSpecial().getAccuracyBonus();
			}
		}

		double attackCalc = Math.floor(equipmentBonus + attacker.getBaseAttack(type)) + 8;

		attackCalc *= prayerMod;
		attackCalc += styleBonus;

		if (equipmentBonus < -67) {
			attackCalc = Misc.exclusiveRandom(8) == 0 ? attackCalc : 0;
		}
		attackCalc *= specialBonus;

		equipmentBonus = 1;
		prayerMod = 1;
		styleBonus = 0;
		if (victim.isPlayer()) {
			Player player = (Player) victim;

			if (bonusType == -1) {
				equipmentBonus = type == CombatType.MAGIC ? player.getBonusManager().getDefenceBonus()[BonusManager.DEFENCE_MAGIC]
						: player.getSkillManager().getCurrentLevel(Skill.DEFENCE);
			} else {
				equipmentBonus = type == CombatType.MAGIC ? player.getBonusManager().getDefenceBonus()[BonusManager.DEFENCE_MAGIC]
						: player.getBonusManager().getDefenceBonus()[bonusType];
			}

			if (PrayerHandler.isActivated(player, PrayerHandler.THICK_SKIN)) {
				prayerMod = 1.05;
			} else if (PrayerHandler.isActivated(player, PrayerHandler.ROCK_SKIN)) {
				prayerMod = 1.10;
			} else if (PrayerHandler.isActivated(player, PrayerHandler.STEEL_SKIN)) {
				prayerMod = 1.15;
			} else if (PrayerHandler.isActivated(player, PrayerHandler.CHIVALRY)) {
				prayerMod = 1.20;
			} else if (PrayerHandler.isActivated(player, PrayerHandler.PIETY)) {
				prayerMod = 1.25;
			} else if (PrayerHandler.isActivated(player, PrayerHandler.RIGOUR)) {
				prayerMod = 1.25;
			} else if (PrayerHandler.isActivated(player, PrayerHandler.AUGURY)) {
				prayerMod = 1.25;
			} else if (CurseHandler.isActivated(player, CurseHandler.LEECH_DEFENCE)) {
				prayerMod = 1.05 + + (player.getLeechedBonuses()[1] * 0.01);
			} else if (CurseHandler.isActivated(player,
					CurseHandler.TURMOIL)) {
				prayerMod = 1.15 + + (player.getLeechedBonuses()[1] * 0.01);
			}

			if (player.getFightType().getStyle() == FightStyle.DEFENSIVE) {
				styleBonus = 3;
			} else if (player.getFightType().getStyle() == FightStyle.CONTROLLED) {
				styleBonus = 1;
			}
		}

		double defenceCalc = Math.floor(equipmentBonus + victim.getBaseDefence(type)) + 8;
		defenceCalc *= prayerMod;
		defenceCalc += styleBonus;

		if (equipmentBonus < -67) {
			defenceCalc = Misc.exclusiveRandom(8) == 0 ? defenceCalc : 0;
		}
		if (veracEffect) {
			defenceCalc = 0;
		}
		double A = Math.floor(attackCalc);
		double D = Math.floor(defenceCalc);
		double hitSucceed = A < D ? (A - 1.0) / (2.0 * D)
				: 1.0 - (D + 1.0) / (2.0 * A);
		hitSucceed = hitSucceed >= 1.0 ? 0.99 : hitSucceed <= 0.0 ? 0.01
				: hitSucceed;
		return hitSucceed >= Misc.RANDOM.nextDouble();
	}

	
	@SuppressWarnings("incomplete-switch")
	public static int calculateMaxRangedHit(Character entity, Character victim) {
		int maxHit = 0;
		if (entity.isNpc()) {
			NPC npc = (NPC) entity;
			maxHit = npc.getDefinition().getMaxHit();
			if (npc.getStrengthWeakened()[0]) {
				maxHit -= (int) ((0.10) * (maxHit));
			} else if (npc.getStrengthWeakened()[1]) {
				maxHit -= (int) ((0.20) * (maxHit));
			} else if (npc.getStrengthWeakened()[2]) {
				maxHit -= (int) ((0.30) * (maxHit));
			}
			return maxHit;
		}
		Player player = (Player) entity;
		double specialMultiplier = 1;
		double prayerMultiplier = 1;
		double otherBonusMultiplier = 1;
		int rangedStrength = ((int) player.getBonusManager().getAttackBonus()[4] / 10);
		if(player.getRangedWeaponData() != null)
			rangedStrength += (RangedWeaponData.getAmmunitionData(player).getStrength());
		int rangeLevel = player.getSkillManager().getCurrentLevel(Skill.RANGED);
		int combatStyleBonus = 0;
		switch (player.getFightType().getStyle()) {
		case ACCURATE:
			combatStyleBonus = 3;
			break;
		}
		if (EquipmentBonus.wearingVoid(player, CombatType.RANGED)) {
			otherBonusMultiplier = 1.1;
		}
		int effectiveRangeDamage = (int) ((rangeLevel * prayerMultiplier * otherBonusMultiplier) + combatStyleBonus);
		double baseDamage = 1.3 + (effectiveRangeDamage / 10) + (rangedStrength / 80) + ((effectiveRangeDamage * rangedStrength) / 640);
		if (player.isSpecialActivated()) {
			specialMultiplier = player.getCombatSpecial().getStrengthBonus();
		}
		maxHit = (int) (baseDamage * specialMultiplier);
		if (victim != null && victim.isNpc()) {
			NPC npc = (NPC) victim;
			if (npc.getDefenceWeakened()[0]) {
				maxHit += (int) ((0.10) * (maxHit));
			} else if (npc.getDefenceWeakened()[1]) {
				maxHit += (int) ((0.20) * (maxHit));
			} else if (npc.getDefenceWeakened()[2]) {
				maxHit += (int) ((0.30) * (maxHit));
			}
			if(npc.getId() == player.getSlayer().getSlayerTask().getNpcId()) {
				if(player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 13263) {
					maxHit *= 1.12;
				}
			}
		}
		maxHit *= 10;
		return maxHit;
	}

	public static int calculateMaxDragonFireHit(Character e, Character v) {
		int baseMax = 250;
		if(e.isNpc() && v.isPlayer()) {
			Player victim = (Player)v;
			NPC npc = (NPC)e;
			baseMax = (int) (npc.getDefinition().getMaxHit() * 2.5);
			if(victim.getFireImmunity() > 0 || victim.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 1540 || victim.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 11283) {

				if(victim.getFireDamageModifier() == 100) {
					return 0;
				} else if(victim.getFireDamageModifier() == 50) {
					baseMax /= 2;
				} else {
					baseMax /= 3; //Shields
				}

			}
		}
		if(baseMax > 450) {
			baseMax = 450 + Misc.getRandom(9);
		}
		return baseMax;
	}

	public static boolean checkHook(Character entity, Character victim) {
		if (!victim.isRegistered() || !entity.isRegistered() || entity.getConstitution() <= 0 || victim.getConstitution() <= 0) {
			entity.getCombatBuilder().reset(true);
			return false;
		}
		if (victim.isPlayer()) {
			if (((Player) victim).isTeleporting() || !Location.ignoreFollowDistance(entity) && !Locations.goodDistance(victim.getPosition(), entity.getPosition(), 40) || ((Player) victim).isPlayerLocked()) {
				entity.getCombatBuilder().cooldown = 10;
				entity.getMovementQueue().setFollowCharacter(null);
				return false;
			}
		}

		if(victim.isNpc() && entity.isPlayer()) {
			NPC npc = (NPC)victim;
			if(npc.getSpawnedFor() != null && npc.getSpawnedFor().getIndex() != ((Player)entity).getIndex()) {
				((Player)entity).getPacketSender().sendMessage("That's not your enemy to fight.");
				entity.getCombatBuilder().reset(true);
				return false;
			}
			if(npc.isSummoningNpc()) {
				Player player = ((Player)entity);
				if(player.getLocation() != Location.WILDERNESS) {
					player.getPacketSender().sendMessage("You can only attack familiars in the wilderness.");
					player.getCombatBuilder().reset(true);
					return false;
				} else if(npc.getLocation() != Location.WILDERNESS) {
					player.getPacketSender().sendMessage("That familiar is not in the wilderness.");
					player.getCombatBuilder().reset(true);
					return false;
				}
				if(player.getSummoning().getFamiliar() != null && player.getSummoning().getFamiliar().getSummonNpc() != null && player.getSummoning().getFamiliar().getSummonNpc().getIndex() == npc.getIndex()) {
					return false;
				}
			}
			if(Nex.nexMob(npc.getId()) || npc.getId() == 6260 || npc.getId() == 6261 || npc.getId() == 6263 || npc.getId() == 6265 || npc.getId() == 6222 || npc.getId() == 6223 || npc.getId() == 6225 || npc.getId() == 6227 || npc.getId() == 6203 || npc.getId() == 6208 || npc.getId() == 6204 || npc.getId() == 6206 || npc.getId() == 6247 || npc.getId() == 6248 || npc.getId() == 6250 || npc.getId() == 6252) {
				if(!((Player)entity).getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
					((Player)entity).getPacketSender().sendMessage("You must enter the room before being able to attack.");
					entity.getCombatBuilder().reset(true);
					return false;
				}
			}
			 else if(npc.getId() == 2891) {
					for(int i = 0; i < 4; i++) {
						if(!((KrakenInstance)((Player)entity).getRegionInstance()).disturbedPool(i)) {
							((Player)entity).getPacketSender().sendMessage("You need to disturb all the small whirpools first.");
							entity.getCombatBuilder().reset(true);
							return false;
						}
					}
				}
			if(Nex.nexMob(npc.getId())) {
				if(!Nex.checkAttack(((Player)entity), npc.getId())) {
					entity.getCombatBuilder().reset(true);
					return false;
				}
			} else if(npc.getId() == 6222) { //Kree'arra
				if(entity.getCombatBuilder().getStrategy().getCombatType() == CombatType.MELEE) {
					((Player)entity).getPacketSender().sendMessage("Kree'arra is resistant to melee attacks.");
					entity.getCombatBuilder().reset(true);
					return false;
				}
			}
			if(npc.getLocation() != Location.DUNGEONEERING && npc.getDefinition().getSlayerLevel() > ((Player)entity).getSkillManager().getCurrentLevel(Skill.SLAYER)) {
				((Player)entity).getPacketSender().sendMessage("You need a Slayer level of at least "+npc.getDefinition().getSlayerLevel()+" to attack this creature.");
				entity.getCombatBuilder().reset(true);
				return false;
			}
			if(npc.getId() == 2000 && !CombatFactory.fullVeracs((Player)entity)) {
				((Player)entity).getPacketSender().sendMessage("@red@You can damage Venenatis only with Verac`s armour and flail!");
				entity.getCombatBuilder().reset(true);
				return false;
			}
			if(npc.getId() == 13458 && ((Player)entity).getSlayer().getSlayerTask().getNpcId() != 13458) {
				((Player)entity).getPacketSender().sendMessage("@red@You cannot damage blood reavers without reaves assigned as slayer task.");
				((Player)entity).getPacketSender().sendMessage("@red@Blood Reavers can only be assigned by Sumona.");
				entity.getCombatBuilder().reset(true);
				return false;
			}
			if(npc.getId() == 13465 || npc.getId() == 13469 || npc.getId() == 13474 || npc.getId() == 13478 || npc.getId() == 13479) {
				if(entity.getLocation() != Location.WILDERNESS) {
					((Player)entity).getPacketSender().sendMessage("You cannot reach that.");
					entity.getCombatBuilder().reset(true);
					return false;
				}
			}
			if(npc.getId() == 4291 && entity.getPosition().getZ() == 2 && !((Player)entity).getMinigameAttributes().getWarriorsGuildAttributes().enteredTokenRoom()) {
				((Player)entity).getPacketSender().sendMessage("You cannot reach that.");
				entity.getCombatBuilder().reset(true);
				return false;
			}
		}
		if (entity.getCombatBuilder().getLastAttacker() != null && !Location.inMulti(entity) && entity.getCombatBuilder().isBeingAttacked() && !victim.equals(entity.getCombatBuilder().getLastAttacker())) {
			if (entity.isPlayer())
				((Player) entity).getPacketSender().sendMessage("You are already under attack!");
			entity.getCombatBuilder().reset(true);
			return false;
		}
		if(!(entity.isNpc() && ((NPC)entity).isSummoningNpc())) {
			boolean allowAttack = false;
			if (victim.getCombatBuilder().getLastAttacker() != null && !Location.inMulti(entity) && victim.getCombatBuilder().isBeingAttacked() && !victim.getCombatBuilder().getLastAttacker().equals(entity)) {
				if(victim.getCombatBuilder().getLastAttacker().isNpc()) {
					NPC npc = (NPC)victim.getCombatBuilder().getLastAttacker();
					if(npc.isSummoningNpc()) {
						if(entity.isPlayer()) {
							Player player = (Player)entity;
							if(player.getSummoning().getFamiliar() != null && player.getSummoning().getFamiliar().getSummonNpc() != null && player.getSummoning().getFamiliar().getSummonNpc().getIndex() == npc.getIndex()) {
								allowAttack = true;
							}
						}
					}
				}
				
				if(!allowAttack) {
					if (entity.isPlayer())
						((Player) entity).getPacketSender().sendMessage("They are already under attack!");
					entity.getCombatBuilder().reset(true);
					return false;
				}
			}
		}
		if(entity.isPlayer()) {
			if(victim.isPlayer()) {
				if (!properLocation((Player)entity, (Player)victim)) {
					entity.getCombatBuilder().reset(true);
					entity.setPositionToFace(victim.getPosition());
					return false;
				}
			}
			if(((Player) entity).isCrossingObstacle()) {
				entity.getCombatBuilder().reset(true);
				return false;
			}
		}
		if (entity.isNpc()) {
			NPC n = (NPC) entity;
			if(!Location.ignoreFollowDistance(n) && !Nex.nexMob(n.getId()) && !n.isSummoningNpc()) { //Stops combat for npcs if too far away
				if(n.getPosition().isWithinDistance(victim.getPosition(), 1)) {
					return true;
				}
				if(!n.getPosition().isWithinDistance(n.getDefaultPosition(), 10 + n.getMovementCoordinator().getCoordinator().getRadius())) {
					n.getMovementQueue().reset();
					n.getMovementCoordinator().setCoordinateState(CoordinateState.AWAY);
					return false;
				}
			}
		}

		return true;
	}

	public static boolean checkAttackDistance(CombatBuilder builder) {
		return checkAttackDistance(builder.getCharacter(), builder.getVictim());
	}

	public static boolean checkAttackDistance(Character a, Character b) {
		Position attacker = a.getPosition();
		Position victim = b.getPosition();
		if(a.isNpc() && ((NPC)a).isSummoningNpc()) {
			return Locations.goodDistance(attacker, victim, a.getSize());
		}
		if(a.getCombatBuilder().getStrategy() == null)
			a.getCombatBuilder().determineStrategy();
		CombatStrategy strategy = a.getCombatBuilder().getStrategy();
		int distance = strategy.attackDistance(a);
		if(a.isPlayer() && strategy.getCombatType() != CombatType.MELEE) {
			if(b.getSize() >= 2)
				distance += b.getSize()-1;
		}
		MovementQueue movement = a.getMovementQueue();
		MovementQueue otherMovement = b.getMovementQueue();
		if (!movement.isMovementDone() && !otherMovement.isMovementDone() && !movement.isLockMovement() && !a.isFrozen()) {
			distance += 1;
			if (movement.isRunToggled()) {
				distance += 2;
			}
		}

		boolean sameSpot = attacker.equals(victim) && !a.getMovementQueue().isMoving() && !b.getMovementQueue().isMoving();
		boolean goodDistance = !sameSpot && Locations.goodDistance(attacker.getX(), attacker.getY(), victim.getX(), victim.getY(), distance);
		boolean projectilePathBlocked = false;
		if(a.isPlayer() && (strategy.getCombatType() == CombatType.RANGED || strategy.getCombatType() == CombatType.MAGIC && ((Player)a).getCastSpell() != null && !(((Player)a).getCastSpell() instanceof CombatAncientSpell))|| a.isNpc() && strategy.getCombatType() == CombatType.MELEE) {
			if(!RegionClipping.canProjectileAttack(b, a))
				projectilePathBlocked = true;
		}
		if(!projectilePathBlocked && goodDistance) {
			if(strategy.getCombatType() == CombatType.MELEE && RegionClipping.isInDiagonalBlock(b, a)) {
				PathFinder.findPath(a, victim.getX(), victim.getY() + 1, true, 1, 1);
				return false;
			} else
				a.getMovementQueue().reset();
			return true;
		} else if(projectilePathBlocked || !goodDistance) {
			a.getMovementQueue().setFollowCharacter(b);
			return false;
		}
		return attacker.isWithinDistance(victim, distance);
	}

	protected static void applyPrayerProtection(CombatContainer container, CombatBuilder builder) {
		if (!container.isCheckAccuracy() || builder.getVictim() == null) {
			return;
		}
		if(builder.getVictim().isPlayer()) {
			Player victim = (Player) builder.getVictim();
			if(victim.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 13740) {
				container.allHits(context -> {
					if(context.getHit().getDamage() > 0) {
						if(victim.getSkillManager().getCurrentLevel(Skill.PRAYER) > 0) {
							int prayerLost;
							if (victim.getAmountDonated() >= 100) {
								prayerLost = 0;
							} else {
								prayerLost = (int) (context.getHit().getDamage() * 0.07);
							}
							if(victim.getSkillManager().getCurrentLevel(Skill.PRAYER) >= prayerLost) {
								context.getHit().incrementAbsorbedDamage((int)(context.getHit().getDamage() - (context.getHit().getDamage() * 0.65)));
								victim.getSkillManager().setCurrentLevel(Skill.PRAYER, victim.getSkillManager().getCurrentLevel(Skill.PRAYER) - prayerLost);
							}
						}
					}
				});
			}
			if(victim.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() ==  13742) {
                container.allHits(context -> {
                    if (context.getHit().getDamage() > 0 && Misc.getRandom(10) >= 4) {
                        context.getHit().incrementAbsorbedDamage((int)((double)context.getHit().getDamage() - (double)context.getHit().getDamage() * 0.75));
                    }
                });
            }
			if(builder.getCharacter().isNpc()) {
				NPC attacker = (NPC) builder.getCharacter();
				if (attacker.getId() == 2030) {
					return;
				}
				if (PrayerHandler.isActivated(victim, PrayerHandler.getProtectingPrayer(container.getCombatType())) || CurseHandler.isActivated(victim, CurseHandler.getProtectingPrayer(container.getCombatType()))) {
					container.allHits(context -> {
						int hit = context.getHit().getDamage();
						if(attacker.getId() == 2745) { //Jad
							context.setAccurate(false);
							context.getHit().incrementAbsorbedDamage(hit);
						} else {
							double reduceRatio = attacker.getId() == 1158 || attacker.getId() == 1160 ? 0.4 : 0.8;
							double mod = Math.abs(1 - reduceRatio);
							context.getHit().incrementAbsorbedDamage((int)(hit - (hit * mod)));
							mod = Math.round(Misc.RANDOM.nextDouble() * 100.0) / 100.0;
							if (mod <= CombatFactory.PRAYER_ACCURACY_REDUCTION) {
								context.setAccurate(false);
							}
						}
					});
				}
			} else if(builder.getCharacter().isPlayer()) {
				Player attacker = (Player) builder.getCharacter();
				if (CombatFactory.fullVeracs(attacker)) {
					return;
				}
				if (PrayerHandler.isActivated(victim, PrayerHandler.getProtectingPrayer(container.getCombatType())) || CurseHandler.isActivated(victim, CurseHandler.getProtectingPrayer(container.getCombatType()))) {
					container.allHits(context -> {
						int hit = context.getHit().getDamage();
						double mod = Math.abs(1 - 0.5);
						context.getHit().incrementAbsorbedDamage((int)(hit - (hit * mod)));
						mod = Math.round(Misc.RANDOM.nextDouble() * 100.0) / 100.0;
						if (mod <= CombatFactory.PRAYER_ACCURACY_REDUCTION) {
							context.setAccurate(false);
						}
					});
				}
			}
		} else if(builder.getVictim().isNpc() && builder.getCharacter().isPlayer()) {
			Player attacker = (Player) builder.getCharacter();
			NPC npc = (NPC) builder.getVictim();
			if(npc.getId() == 8349 && container.getCombatType() == CombatType.MELEE) {
				container.allHits(context -> {
					int hit = context.getHit().getDamage();
					double mod = Math.abs(1 - 0.5);
					context.getHit().incrementAbsorbedDamage((int)(hit - (hit * mod)));
					mod = Math.round(Misc.RANDOM.nextDouble() * 100.0) / 100.0;
					if (mod <= CombatFactory.PRAYER_ACCURACY_REDUCTION) {
						context.setAccurate(false);
					}
				});
			} else if(npc.getId() == 1158 && (container.getCombatType() == CombatType.MAGIC || container.getCombatType() == CombatType.RANGED) || npc.getId() == 1160 && container.getCombatType() == CombatType.MELEE) {
				container.allHits(context -> {
					int hit = context.getHit().getDamage();
					double mod = Math.abs(1 - 0.95);
					context.getHit().incrementAbsorbedDamage((int)(hit - (hit * mod)));
					mod = Math.round(Misc.RANDOM.nextDouble() * 100.0) / 100.0;
					if (mod <= CombatFactory.PRAYER_ACCURACY_REDUCTION) {
						context.setAccurate(false);
					}
				});
				attacker.getPacketSender().sendMessage("Your "+(container.getCombatType() == CombatType.MAGIC ? "magic" : container.getCombatType() == CombatType.RANGED ? "ranged" : "melee")+" attack has"+(!container.getHits()[0].isAccurate() ? "" : " close to")+" no effect on the queen.");
			} else if(npc.getId() == 13347 && Nex.zarosStage()) {
				container.allHits(context -> {
					int hit = context.getHit().getDamage();
					double mod = Math.abs(1 - 0.4);
					context.getHit().incrementAbsorbedDamage((int)(hit - (hit * mod)));
					mod = Math.round(Misc.RANDOM.nextDouble() * 100.0) / 100.0;
					if (mod <= CombatFactory.PRAYER_ACCURACY_REDUCTION) {
						context.setAccurate(false);
					}
				});
			}
		}
	}

	protected static void giveExperience(CombatBuilder builder, CombatContainer container, int damage) {
		if (container.getExperience().length == 0 && container.getCombatType() != CombatType.MAGIC) {
			return;
		}
		if (builder.getCharacter().isPlayer()) {
			Player player = (Player) builder.getCharacter();

			if (container.getCombatType() == CombatType.MAGIC) {
				if(player.getCurrentlyCasting() != null)
					player.getSkillManager().addExperience(Skill.MAGIC, (int) (((damage * .90)  * Skill.MAGIC.getExperienceMultiplier()) / container.getExperience().length) + builder.getCharacter().getCurrentlyCasting().baseExperience());
			} else {
				for (int i : container.getExperience()) {
					Skill skill = Skill.forId(i);
					player.getSkillManager().addExperience(skill, (int) (((damage * .90)  * skill.getExperienceMultiplier()) / container.getExperience().length));
				}
			}

			player.getSkillManager().addExperience(Skill.CONSTITUTION, (int) (damage * .70) * Skill.CONSTITUTION.getExperienceMultiplier());
		}
	}

	protected static void handleArmorEffects(Character attacker, Character target, int damage, CombatType combatType) {
		if(attacker.getConstitution() > 0 && damage > 0) {
			if(target != null && target.isPlayer()) {
				Player t2 = (Player)target;
				if(t2.getEquipment().getItems()[Equipment.RING_SLOT].getId() == 2550) {
					int recDamage = (int) (damage * 0.10);
					if (recDamage <= 0)
						return;
					if (recDamage > t2.getConstitution())
						recDamage = t2.getConstitution();
					attacker.dealDamage(new Hit(recDamage, Hitmask.RED, CombatIcon.DEFLECT));
					ItemDegrading.handleItemDegrading(t2, DegradingItem.RING_OF_RECOIL);
				}
				else if(t2.getEquipment().getItems()[Equipment.AMULET_SLOT].getId() == 11090 && t2.getLocation() != Location.DUEL_ARENA) {
					int restore = (int) (t2.getSkillManager().getMaxLevel(Skill.CONSTITUTION) * .3);
					if (t2.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) <= t2.getSkillManager().getMaxLevel(Skill.CONSTITUTION) * .2) {
						t2.performGraphic(new Graphic(1690));
						t2.getEquipment().delete(t2.getEquipment().getItems()[Equipment.AMULET_SLOT]);
						t2.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, t2.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) + restore);
						t2.getPacketSender().sendMessage("Your Phoenix Necklace restored your Constitution, but was destroyed in the process.");
						t2.getUpdateFlag().flag(Flag.APPEARANCE);
					}
				}
				else if(t2.getEquipment().getItems()[Equipment.RING_SLOT].getId() == 2570 && t2.getLocation() != Location.DUEL_ARENA && t2.getLocation() != Location.WILDERNESS) {
					if (t2.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) <= t2.getSkillManager().getMaxLevel(Skill.CONSTITUTION) * .1) {
						t2.getEquipment().delete(t2.getEquipment().getItems()[Equipment.RING_SLOT]);
						TeleportHandler.teleportPlayer(t2, GameSettings.DEFAULT_POSITION.copy(), TeleportType.RING_TELE);
						t2.getPacketSender().sendMessage("Your Ring of Life tried to teleport you away, but was destroyed in the process.");
					}
				}
			}
		}
		if (Misc.exclusiveRandom(4) == 0) {
			if (CombatFactory.fullGuthans(attacker)) {
				target.performGraphic(new Graphic(398));
				attacker.heal(damage);
				return;
			}	
		}
	}

	protected static void handlePrayerEffects(Character attacker, Character target, int damage, CombatType combatType) {
		if(attacker == null || target == null)
			return;
		if (target.isPlayer() && damage > 0) {
			Player victim = (Player) target;
			if (PrayerHandler.isActivated(victim, PrayerHandler.REDEMPTION) && victim.getConstitution() <= (victim.getSkillManager().getMaxLevel(Skill.CONSTITUTION) / 10)) {
				int amountToHeal = (int) (victim.getSkillManager().getMaxLevel(Skill.PRAYER) * .25);
				victim.performGraphic(new Graphic(436));
				victim.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
				victim.getSkillManager().updateSkill(Skill.PRAYER);
				victim.getSkillManager().setCurrentLevel(Skill.CONSTITUTION,
						victim.getConstitution() + amountToHeal);
				victim.getSkillManager().updateSkill(Skill.CONSTITUTION);
				victim.getPacketSender().sendMessage("You've run out of prayer points!");
				PrayerHandler.deactivateAll(victim);
				return;
			}
			if (attacker.isPlayer()) {
				Player p = (Player) attacker;
				if (PrayerHandler.isActivated(victim, PrayerHandler.RETRIBUTION) && victim.getConstitution() < 1) {
					victim.performGraphic(new Graphic(437));
					if (p.getPosition().isWithinDistance(victim.getPosition(),CombatFactory.RETRIBUTION_RADIUS)) {
						p.dealDamage(new Hit(Misc.inclusiveRandom(CombatFactory.MAXIMUM_RETRIBUTION_DAMAGE), Hitmask.RED, CombatIcon.DEFLECT));
					}
				} else if (CurseHandler.isActivated(victim, CurseHandler.WRATH) && victim.getConstitution() < 1) {
					victim.performGraphic(new Graphic(2259));
					victim.performAnimation(new Animation(12583));
					if (p.getPosition().isWithinDistance(victim.getPosition(), CombatFactory.RETRIBUTION_RADIUS)) {
						p.performGraphic(new Graphic(2260));
						p.dealDamage(new Hit(Misc.inclusiveRandom(CombatFactory.MAXIMUM_RETRIBUTION_DAMAGE), Hitmask.RED, CombatIcon.DEFLECT));
					}
				}
				if (PrayerHandler.isActivated((Player) attacker,PrayerHandler.SMITE)) {
					victim.getSkillManager().setCurrentLevel(Skill.PRAYER,victim.getSkillManager().getCurrentLevel(Skill.PRAYER) - damage / 4);
					if(victim.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0)
					victim.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
					victim.getSkillManager().updateSkill(Skill.PRAYER);
				}
			}
		}

		if (attacker.isPlayer()) {
			Player p = (Player) attacker;
			if(CurseHandler.isActivated(p, CurseHandler.TURMOIL)) {
				if (Misc.getRandom(5) >= 3) {
					int increase = Misc.getRandom(2);
					if(p.getLeechedBonuses()[increase]+1 < 30) {
						p.getLeechedBonuses()[increase] += 1;
						BonusManager.sendCurseBonuses(p);
					}
				}
			}
			if(CurseHandler.isActivated(p, CurseHandler.SOUL_SPLIT) && damage > 0) {
				final int form = damage / 4;
				new Projectile(attacker, target, 2263, 44, 3, 43, 31, 0).sendProjectile();
				TaskManager.submit(new Task(1, p, false) {
					@Override
					public void execute() {
						if(!(attacker == null || target == null || attacker.getConstitution() <= 0)) {
							target.performGraphic(new Graphic(2264, GraphicHeight.LOW));
							p.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, p.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) + form);
							if(p.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) > p.getSkillManager().getMaxLevel(Skill.CONSTITUTION))
								p.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, p.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
							if(target.isPlayer()) {
								Player victim = (Player) target;
								victim.getSkillManager().setCurrentLevel(Skill.PRAYER, victim.getSkillManager().getCurrentLevel(Skill.PRAYER) - form);
								if (victim.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0) {
									victim.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
									CurseHandler.deactivateCurses(victim);
									PrayerHandler.deactivatePrayers(victim);
								}
								victim.getSkillManager().updateSkill(Skill.PRAYER);
							}
						}
						stop();
					}
				});
			}
			if(p.getCurseActive()[CurseHandler.LEECH_ATTACK] || p.getCurseActive()[CurseHandler.LEECH_DEFENCE] || p.getCurseActive()[CurseHandler.LEECH_STRENGTH] || p.getCurseActive()[CurseHandler.LEECH_MAGIC] || p.getCurseActive()[CurseHandler.LEECH_RANGED] || p.getCurseActive()[CurseHandler.LEECH_SPECIAL_ATTACK] || p.getCurseActive()[CurseHandler.LEECH_ENERGY]) {
				int i, gfx, projectileGfx; i = gfx = projectileGfx = -1;
				if(Misc.getRandom(10) >= 7 && p.getCurseActive()[CurseHandler.LEECH_ATTACK]) {
					i = 0;
					projectileGfx = 2252;
					gfx = 2253;
				} else if(Misc.getRandom(15) >= 11 && p.getCurseActive()[CurseHandler.LEECH_DEFENCE]) {
					i = 1;
					projectileGfx = 2248;
					gfx = 2250;
				} else if(Misc.getRandom(11) <= 3 && p.getCurseActive()[CurseHandler.LEECH_STRENGTH]) {
					i = 2;
					projectileGfx = 2236;
					gfx = 2238;
				} else if(Misc.getRandom(20) >= 16 && p.getCurseActive()[CurseHandler.LEECH_RANGED]) {
					i = 4;
					projectileGfx = 2236;
					gfx = 2238;
				} else if(Misc.getRandom(30) >= 24 && p.getCurseActive()[CurseHandler.LEECH_MAGIC]) {
					i = 6;
					projectileGfx = 2244;
					gfx = 2242;
				} else if(Misc.getRandom(30) <= 4 && p.getCurseActive()[CurseHandler.LEECH_SPECIAL_ATTACK]) {
					i = 7;
					projectileGfx = 2256;
					gfx = 2257;
				} else if(Misc.getRandom(30) <= 4 && p.getCurseActive()[CurseHandler.LEECH_ENERGY]) {
					i = 8;
					projectileGfx = 2256;
					gfx = 2257;
				}
				if(i != -1) {
					p.performAnimation(new Animation(12575));
					if(i != 7 && i != 8) {
						if(p.getLeechedBonuses()[i] < 2)
							p.getLeechedBonuses()[i] += Misc.getRandom(2);
						BonusManager.sendCurseBonuses(p);
					}
					if(target.isPlayer()) {
						Player victim = (Player) target;
						new Projectile(attacker, target, projectileGfx, 44, 3, 43, 31, 0).sendProjectile();
						victim.performGraphic(new Graphic(gfx));
						if(i != 7 && i != 8) {
							CurseHandler.handleLeech(victim, i, 2, -25, true);
							BonusManager.sendCurseBonuses((Player) victim);
						} else if(i == 7) {
							//Leech spec
							boolean leeched = false;
							if((victim.getSpecialPercentage() - 10) >= 0) {
								victim.setSpecialPercentage(victim.getSpecialPercentage() - 10);
								CombatSpecial.updateBar(victim);
								victim.getPacketSender().sendMessage("Your Special Attack has been leeched by an enemy curse!");
								leeched = true;
							}
							if(leeched) {
								p.setSpecialPercentage(p.getSpecialPercentage() + 10);
								if(p.getSpecialPercentage() > 100)
									p.setSpecialPercentage(100);
							}
						} else if(i == 8) {
							boolean leeched = false;
							if((victim.getRunEnergy() - 30) >= 0) {
								victim.setRunEnergy(victim.getRunEnergy() - 30);
								victim.getPacketSender().sendMessage("Your energy has been leeched by an enemy curse!");
								leeched = true;
							}
							if(leeched) {
								p.setRunEnergy(p.getRunEnergy() + 30);
								if(p.getRunEnergy() > 100)
									p.setRunEnergy(100);
							}
						}
					}
					p.getPacketSender().sendMessage("You manage to leech your target's "+(i == 8 ? ("energy") : i == 7 ? ("Special Attack") : Misc.formatText(Skill.forId(i).toString().toLowerCase()))+".");
				}
			} else {
				boolean sapWarrior = p.getCurseActive()[CurseHandler.SAP_WARRIOR];
				boolean sapRanger = p.getCurseActive()[CurseHandler.SAP_RANGER];
				boolean sapMage = p.getCurseActive()[CurseHandler.SAP_MAGE];
				if(sapWarrior || sapRanger || sapMage) {
					if(sapWarrior && Misc.getRandom(8) <= 2) {
						CurseHandler.handleLeech(target, 0, 1, -10, true);
						CurseHandler.handleLeech(target, 1, 1, -10, true);
						CurseHandler.handleLeech(target, 2, 1, -10, true);
						p.performGraphic(new Graphic(2214));
						p.performAnimation(new Animation(12575));
						new Projectile(p, target, 2215, 44, 3, 43, 31, 0).sendProjectile();
						p.getPacketSender().sendMessage("You decrease the your Attack, Strength and Defence level..");
					} else if(sapRanger && Misc.getRandom(16) >= 9) {
						CurseHandler.handleLeech(target, 4, 1, -10, true);
						CurseHandler.handleLeech(target, 1, 1, -10, true);
						p.performGraphic(new Graphic(2217));
						p.performAnimation(new Animation(12575));
						new Projectile(p, target, 2218, 44, 3, 43, 31, 0).sendProjectile();
						p.getPacketSender().sendMessage("You decrease your target's Ranged and Defence level..");
					} else if(sapMage && Misc.getRandom(15) >= 10) {
						CurseHandler.handleLeech(target, 6, 1, -10, true);
						CurseHandler.handleLeech(target, 1, 1, -10, true);
						p.performGraphic(new Graphic(2220));
						p.performAnimation(new Animation(12575));
						new Projectile(p, target, 2221, 44, 3, 43, 31, 0).sendProjectile();
						p.getPacketSender().sendMessage("You decrease your target's Magic and Defence level..");
					}
				}
			}
		}
		if(target.isPlayer()) {
			Player victim = (Player) target;
			if(damage > 0 && Misc.getRandom(10) <= 4) {
				int deflectDamage = -1;
				if(CurseHandler.isActivated(victim, CurseHandler.DEFLECT_MAGIC) && combatType == CombatType.MAGIC) {
					victim.performGraphic(new Graphic(2228, GraphicHeight.MIDDLE));
					victim.performAnimation(new Animation(12573));
					deflectDamage = (int) (damage * 0.20);
				} else if(CurseHandler.isActivated(victim, CurseHandler.DEFLECT_MISSILES) && combatType == CombatType.RANGED) {
					victim.performGraphic(new Graphic(2229, GraphicHeight.MIDDLE));
					victim.performAnimation(new Animation(12573));
					deflectDamage = (int) (damage * 0.20);
				} else if(CurseHandler.isActivated(victim, CurseHandler.DEFLECT_MELEE) && combatType == CombatType.MELEE) {
					victim.performGraphic(new Graphic(2230, GraphicHeight.MIDDLE));
					victim.performAnimation(new Animation(12573));
					deflectDamage = (int) (damage * 0.20);
				}
				if(deflectDamage > 0) {
					if (deflectDamage > attacker.getConstitution())
						deflectDamage = attacker.getConstitution();
					final int toDeflect = deflectDamage;
					TaskManager.submit(new Task(1, victim, false) {
						@Override
						public void execute() {
							if(attacker == null || attacker.getConstitution() <= 0) {
								stop();
							} else
								attacker.dealDamage(new Hit(toDeflect, Hitmask.RED, CombatIcon.DEFLECT));
							stop();
						}
					});
				}
			}
		}

	}

	protected static void handleSpellEffects(Character attacker, Character target, int damage, CombatType combatType) {
		if(damage <= 0)
			return;
		if(target.isPlayer()) {
			Player t = (Player)target;
			if(t.hasVengeance()) {
				t.setHasVengeance(false);
				t.forceChat("Taste Vengeance!");
				int returnDamage = (int) (damage * 0.75);
				if(attacker.getConstitution() < returnDamage)
					returnDamage = attacker.getConstitution();
				attacker.dealDamage(new Hit(returnDamage, Hitmask.RED, CombatIcon.DEFLECT));
			}
		}
	}

	public static void chargeDragonFireShield(Player player) {
		if(player.getDfsCharges() >= 30) {
			player.getPacketSender().sendMessage("Your Dragonfire shield is fully charged and can be operated.");
			return;
		}
		player.performAnimation(new Animation(6695));
		player.performGraphic(new Graphic(1164));
		player.incrementDfsCharges(1);
		BonusManager.update(player);
		player.getPacketSender().sendMessage("Your shield absorbs some of the Dragon's fire..");
	}

	public static void handleDragonFireShield(final Player player, final Character target) {
		if(player == null || target == null || target.getConstitution() <= 0 || player.getConstitution() <= 0)
			return;
		player.getCombatBuilder().cooldown(false);
		player.setEntityInteraction(target);
		player.performAnimation(new Animation(6696));
		player.performGraphic(new Graphic(1165));
		TaskManager.submit(new Task(1, player, false) {
			int ticks = 0;
			@Override
			public void execute() {
				switch(ticks) {
				case 3:
					new Projectile(player, target, 1166, 44, 3, 43, 31, 0).sendProjectile();
					break;
				case 4:
					Hit h = new Hit(50 + Misc.getRandom(150), Hitmask.RED, CombatIcon.MAGIC);
					target.dealDamage(h);
					target.performGraphic(new Graphic(1167, GraphicHeight.HIGH));
					target.getCombatBuilder().addDamage(player, h.getDamage());
					target.getLastCombat().reset();
					stop();
					break;
				}
				ticks++;
			}
		});
		player.incrementDfsCharges(-1);
		BonusManager.update(player);
	}

	public static boolean properLocation(Player player, Player player2) {
		return player.getLocation().canAttack(player, player2);
	}
}