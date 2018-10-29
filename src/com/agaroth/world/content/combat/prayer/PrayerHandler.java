package com.agaroth.world.content.combat.prayer;

import java.util.HashMap;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Prayerbook;
import com.agaroth.model.Skill;
import com.agaroth.model.Locations.Location;
import com.agaroth.model.container.impl.Equipment;
import com.agaroth.util.NameUtils;
import com.agaroth.world.content.Sounds;
import com.agaroth.world.content.Sounds.Sound;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.minigames.impl.Dueling;
import com.agaroth.world.content.minigames.impl.Dueling.DuelRule;
import com.agaroth.world.entity.impl.player.Player;

public class PrayerHandler {
	private enum PrayerData {
		THICK_SKIN(1, 1, 25000, 83),
		BURST_OF_STRENGTH(4, 1, 25002, 84),
		CLARITY_OF_THOUGHT(7, 1, 25004, 85),
		SHARP_EYE(8, 1, 25006, 601),
		MYSTIC_WILL(9, 1, 25008, 602),
		ROCK_SKIN(10, 2, 25010, 86),
		SUPERHUMAN_STRENGTH(13, 2, 25012, 87),
		IMPROVED_REFLEXES(16, 2, 25014, 88),
		RAPID_RESTORE(19, .4, 25016, 89),
		RAPID_HEAL(22, .6, 25018, 90),
		PROTECT_ITEM(25, .6, 25020, 91),
		HAWK_EYE(26, 1.5, 25022, 603),
		MYSTIC_LORE(27, 2, 25024, 604),
		STEEL_SKIN(28, 4, 25026, 92),
		ULTIMATE_STRENGTH(31, 4, 25028, 93),
		INCREDIBLE_REFLEXES(34, 4, 25030, 94),
		PROTECT_FROM_MAGIC(37, 4, 25032, 95, 2),
		PROTECT_FROM_MISSILES(40, 4, 25034, 96, 1),
		PROTECT_FROM_MELEE(43, 4, 25036, 97, 0),
		EAGLE_EYE(44, 4, 25038, 605),
		MYSTIC_MIGHT(45, 4, 25040, 606),
		RETRIBUTION(46, 1, 25042, 98, 4),
		REDEMPTION(49, 2, 25044, 99, 5),
		SMITE(52, 6, 25046, 100, 685, 6),
		CHIVALRY(60, 8, 25048, 607),
		PIETY(70, 10, 25050, 608),
		RIGOUR(80, 11, 25104, 609),
		AUGURY(80, 11, 25108, 610);

		private PrayerData(int requirement, double drainRate, int buttonId, int configId, int... hint) {
			this.requirement = requirement;
			this.drainRate = drainRate;
			this.buttonId = buttonId;
			this.configId = configId;
			if (hint.length > 0)
				this.hint = hint[0];
		}
		 private int requirement;
		 private int buttonId;
		 private int configId;
		 private double drainRate;
		 private int hint = -1;
		 private String name;
		 private final String getPrayerName() {
			 if (name == null)
				 return NameUtils.capitalizeWords(toString().toLowerCase().replaceAll("_", " "));
			 return name;
		 }
		 private static HashMap <Integer, PrayerData> prayerData = new HashMap <Integer, PrayerData> ();
		 private static HashMap <Integer, PrayerData> actionButton = new HashMap <Integer, PrayerData> ();
		 static {
			 for (PrayerData pd : PrayerData.values()) {
				 prayerData.put(pd.ordinal(), pd);
				 actionButton.put(pd.buttonId, pd);
			 }
		 }
	}

	public static int getProtectingPrayer(CombatType type) {
		switch (type) {
		case MELEE:
			return PROTECT_FROM_MELEE;
		case MAGIC:
		case DRAGON_FIRE:
			return PROTECT_FROM_MAGIC;
		case RANGED:
			return PROTECT_FROM_MISSILES;
		default:
			throw new IllegalArgumentException("Invalid combat type: " + type);
		}
	}

	public static boolean isActivated(Player player, int prayer) {
		return player.getPrayerActive()[prayer];
	}

	public static void togglePrayerWithActionButton(Player player, final int buttonId) {
		for (PrayerData pd : PrayerData.values()) {
			if (buttonId == pd.buttonId) {
				if (!player.getPrayerActive()[pd.ordinal()])
					activatePrayer(player, pd.ordinal());
				else
					deactivatePrayer(player, pd.ordinal());
			}
		}
	}

	public static void activatePrayer(Player player, final int prayerId) {
		if(player.getPrayerbook() == Prayerbook.CURSES)
			return;
		if (player.getPrayerActive()[prayerId])
			return;
		if(Dueling.checkRule(player, DuelRule.NO_PRAYER)) {
			player.getPacketSender().sendMessage("Prayer has been disabled in this duel.");		
			CurseHandler.deactivateAll(player);
			PrayerHandler.deactivateAll(player);
			return;
		}
		if(player.getLocation() == Location.RECIPE_FOR_DISASTER) {
			player.getPacketSender().sendMessage("For some reason, your prayers do not have any effect in here.");		
			CurseHandler.deactivateAll(player);
			PrayerHandler.deactivateAll(player);
			return;
		}
		PrayerData pd = PrayerData.prayerData.get(prayerId);
		if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You do not have enough Prayer points. You can recharge your points at an altar.");
			return;
		}
		if (player.getSkillManager().getMaxLevel(Skill.PRAYER) < (pd.requirement * 10)) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Prayer level of at least " + pd.requirement + " to use " + pd.getPrayerName() + ".");
			return;
		}
		if (prayerId == CHIVALRY && player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 60) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Defence level of at least 60 to use Chivalry.");
			return;
		}
		if (prayerId == PIETY && player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 70) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Defence level of at least 70 to use Piety.");
			return;
		}
		if (prayerId == RIGOUR && player.getSkillManager().getMaxLevel(Skill.DUNGEONEERING) < 78) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Dungeoneering level of at least 78 to use Rigour.");
			return;
		}
		if (prayerId == AUGURY && player.getSkillManager().getMaxLevel(Skill.DUNGEONEERING) < 78) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Dungeoneering level of at least 78 to use Augury.");
			return;
		}
		switch (prayerId) {
		case THICK_SKIN:
		case ROCK_SKIN:
		case STEEL_SKIN:
			resetPrayers(player, DEFENCE_PRAYERS, prayerId);
			break;
		case BURST_OF_STRENGTH:
		case SUPERHUMAN_STRENGTH:
		case ULTIMATE_STRENGTH:
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case CLARITY_OF_THOUGHT:
		case IMPROVED_REFLEXES:
		case INCREDIBLE_REFLEXES:
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case SHARP_EYE:
		case HAWK_EYE:
		case EAGLE_EYE:
		case MYSTIC_WILL:
		case MYSTIC_LORE:
		case MYSTIC_MIGHT:
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case CHIVALRY:
		case PIETY:
			resetPrayers(player, DEFENCE_PRAYERS, prayerId);
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case PROTECT_FROM_MAGIC:
		case PROTECT_FROM_MISSILES:
		case PROTECT_FROM_MELEE:
			resetPrayers(player, OVERHEAD_PRAYERS, prayerId);
			break;
		case RIGOUR:
		case AUGURY:
			resetPrayers(player, DEFENCE_PRAYERS, prayerId);
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case RETRIBUTION:
		case REDEMPTION:
		case SMITE:
			resetPrayers(player, OVERHEAD_PRAYERS, prayerId);
			break;
		}
		player.setPrayerActive(prayerId, true);
		player.getPacketSender().sendConfig(pd.configId, 1);
		if (hasNoPrayerOn(player, prayerId) && !player.isDrainingPrayer())
			startDrain(player);
		if (pd.hint != -1) {
			int hintId = getHeadHint(player);
			player.getAppearance().setHeadHint(hintId);
		}
		Sounds.sendSound(player, Sound.ACTIVATE_PRAYER_OR_CURSE);
	}

	public static void deactivatePrayer(Player player, int prayerId) {
		if (!player.getPrayerActive()[prayerId])
			return;
		PrayerData pd = PrayerData.prayerData.get(prayerId);
		player.getPrayerActive()[prayerId] = false;
		player.getPacketSender().sendConfig(pd.configId, 0);
		if (pd.hint != -1) {
			int hintId = getHeadHint(player);
			player.getAppearance().setHeadHint(hintId);
		}
		Sounds.sendSound(player, Sound.DEACTIVATE_PRAYER_OR_CURSE);
	}

	public static void deactivatePrayers(Player player) {
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i]) {
				deactivatePrayer(player, i);
			}
		}
	}

	public static void deactivateAll(Player player) {
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			PrayerData pd = PrayerData.prayerData.get(i);
			if(pd == null)
				continue;
			player.getPrayerActive()[i] = false;
			player.getPacketSender().sendConfig(pd.configId, 0);
			if (pd.hint != -1) {
				int hintId = getHeadHint(player);
				player.getAppearance().setHeadHint(hintId);
			}
		}
	}

	private static int getHeadHint(Player player) {
		boolean[] prayers = player.getPrayerActive();
		if (prayers[PROTECT_FROM_MELEE])
			return 0;
		if (prayers[PROTECT_FROM_MISSILES])
			return 1;
		if (prayers[PROTECT_FROM_MAGIC])
			return 2;
		if (prayers[RETRIBUTION])
			return 3;
		if (prayers[SMITE])
			return 4;
		if (prayers[REDEMPTION])
			return 5;
		return -1;
	}

	private static void startDrain(final Player player) {
		if (getDrain(player) <= 0 && !player.isDrainingPrayer())
			return;
		player.setDrainingPrayer(true);
		TaskManager.submit(new Task(1, player, true) {
			@Override
			public void execute() {
				if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
					for (int i = 0; i < player.getPrayerActive().length; i++) {
						if (player.getPrayerActive()[i])
							deactivatePrayer(player, i);
					}
					Sounds.sendSound(player, Sound.RUN_OUT_OF_PRAYER_POINTS);
					player.getPacketSender().sendMessage("You have run out of Prayer points!");
					this.stop();
					return;
				}
				double drainAmount = getDrain(player);
				if (drainAmount <= 0) {
					this.stop();
					return;
				}
				int total = (int) (player.getSkillManager().getCurrentLevel(Skill.PRAYER) - drainAmount);
				player.getSkillManager().setCurrentLevel(Skill.PRAYER, total, true);
			}
			@Override
			public void stop() {
				setEventRunning(false);
				player.setDrainingPrayer(false);
			}
		});
	}

	private static final double getDrain(Player player) {
		double toRemove = 0.0;
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i]) {
				PrayerData prayerData = PrayerData.prayerData.get(i);
				toRemove += prayerData.drainRate / 10;
			}
		}
		if (toRemove > 0) {
			toRemove /= (1 + (0.05 * player.getBonusManager().getOtherBonus()[2]));		
		}
		return toRemove;
	}

	private final static boolean hasNoPrayerOn(Player player, int exceptionId) {
		int prayersOn = 0;
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i] && i != exceptionId)
				prayersOn++;
		}
		return prayersOn == 0;
	}

	public static void resetPrayers(Player player, int[] prayers, int prayerID) {
		for (int i = 0; i < prayers.length; i++) {
			if (prayers[i] != prayerID)
				deactivatePrayer(player, prayers[i]);
		}
	}

	public static final boolean isButton(final int actionButtonID) {
		return PrayerData.actionButton.containsKey(actionButtonID);
	}

	public static final int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3, MYSTIC_WILL = 4,
			ROCK_SKIN = 5, SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7, RAPID_RESTORE = 8, RAPID_HEAL = 9, 
			PROTECT_ITEM = 10, HAWK_EYE = 11, MYSTIC_LORE = 12, STEEL_SKIN = 13, ULTIMATE_STRENGTH = 14,
			INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16, PROTECT_FROM_MISSILES = 17,
			PROTECT_FROM_MELEE = 18, EAGLE_EYE = 19, MYSTIC_MIGHT = 20, RETRIBUTION = 21, REDEMPTION = 22, SMITE = 23, CHIVALRY = 24,
			PIETY = 25, RIGOUR = 26, AUGURY = 27;
	
	private static final int[] DEFENCE_PRAYERS = {THICK_SKIN, ROCK_SKIN, STEEL_SKIN, CHIVALRY, PIETY, RIGOUR, AUGURY};
	private static final int[] STRENGTH_PRAYERS = {BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CHIVALRY, PIETY};
	private static final int[] ATTACK_PRAYERS = {CLARITY_OF_THOUGHT, IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY, PIETY};
	private static final int[] RANGED_PRAYERS = {SHARP_EYE, HAWK_EYE, EAGLE_EYE, RIGOUR};
	private static final int[] MAGIC_PRAYERS = {MYSTIC_WILL, MYSTIC_LORE, MYSTIC_MIGHT, AUGURY};
	public static final int[] OVERHEAD_PRAYERS = {PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE, RETRIBUTION, REDEMPTION, SMITE};
}
