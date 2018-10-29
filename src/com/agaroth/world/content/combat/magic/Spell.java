package com.agaroth.world.content.combat.magic;

import java.util.Arrays;
import java.util.Optional;

import com.agaroth.model.Item;
import com.agaroth.model.Skill;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.player.Player;

public abstract class Spell {

	public boolean canCast(Player player, boolean delete) {
		if (player.getSkillManager().getCurrentLevel(Skill.MAGIC) < levelRequired()) {
			player.getPacketSender().sendMessage("You need a Magic level of " + levelRequired() + " to cast this spell.");
			player.getCombatBuilder().reset(true);
			return false;
		}
		if (itemsRequired(player).isPresent()) {
			Item[] items = PlayerMagicStaff.suppressRunes(player,
					itemsRequired(player).get());
			if (!player.getInventory().containsAll(items)) {
				player.getPacketSender().sendMessage("You do not have the required items to cast this spell.");
				resetPlayerSpell(player);
				player.getCombatBuilder().reset(true);
				return false;
			}
			if(delete) {
				for(Item it : Arrays.asList(items)) {
					if(it != null)
						player.getInventory().delete(it);
				}
			}
		}
		if (equipmentRequired(player).isPresent()) {
			if (!player.getEquipment().containsAll(
					equipmentRequired(player).get())) {
				player.getPacketSender().sendMessage("You do not have the required equipment to cast this spell.");
				resetPlayerSpell(player);
				player.getCombatBuilder().reset(true);
				return false;
			}
		}
		return true;
	}

	private void resetPlayerSpell(Player player) {
		if (player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked() && player.isAutocast()) {
			player.setCastSpell(null);
		}
	}

	public abstract int spellId();
	public abstract int levelRequired();
	public abstract int baseExperience();
	public abstract Optional<Item[]> itemsRequired(Player player);
	public abstract Optional<Item[]> equipmentRequired(Player player);
	public abstract void startCast(Character cast, Character castOn);
}
