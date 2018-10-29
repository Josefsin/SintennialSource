package com.agaroth.net.packet.impl;

import com.agaroth.GameSettings;
import com.agaroth.model.Locations.Location;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.model.container.impl.Bank;
import com.agaroth.model.definitions.WeaponInterfaces.WeaponInterface;
import com.agaroth.model.input.impl.EnterClanChatToJoin;
import com.agaroth.model.input.impl.EnterSyntaxToBankSearchFor;
import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.content.Achievements;
import com.agaroth.world.content.BankPin;
import com.agaroth.world.content.BonusManager;
import com.agaroth.world.content.Consumables;
import com.agaroth.world.content.DropLog;
import com.agaroth.world.content.DropTable;
import com.agaroth.world.content.Emotes;
import com.agaroth.world.content.EnergyHandler;
import com.agaroth.world.content.ExperienceLamps;
import com.agaroth.world.content.ItemsKeptOnDeath;
import com.agaroth.world.content.KillsTracker;
import com.agaroth.world.content.LoyaltyProgramme;
import com.agaroth.world.content.MoneyPouch;
import com.agaroth.world.content.PlayerPanel;
import com.agaroth.world.content.PlayersOnlineInterface;
import com.agaroth.world.content.Sounds;
import com.agaroth.world.content.WellOfGoodwill;
import com.agaroth.world.content.clan.ClanChat;
import com.agaroth.world.content.clan.ClanChatManager;
import com.agaroth.world.content.combat.magic.Autocasting;
import com.agaroth.world.content.combat.magic.MagicSpells;
import com.agaroth.world.content.combat.prayer.CurseHandler;
import com.agaroth.world.content.combat.prayer.PrayerHandler;
import com.agaroth.world.content.combat.weapon.CombatSpecial;
import com.agaroth.world.content.combat.weapon.FightType;
import com.agaroth.world.content.dialogue.Dialogue;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.content.dialogue.DialogueOptions;
import com.agaroth.world.content.dialogue.impl.DungPartyInvitation;
import com.agaroth.world.content.dialogue.impl.ExplorerJack;
import com.agaroth.world.content.grandexchange.GrandExchange;
import com.agaroth.world.content.minigames.impl.Dueling;
import com.agaroth.world.content.minigames.impl.Nomad;
import com.agaroth.world.content.minigames.impl.PestControl;
import com.agaroth.world.content.minigames.impl.RecipeForDisaster;
import com.agaroth.world.content.skill.ChatboxInterfaceSkillAction;
import com.agaroth.world.content.skill.impl.crafting.LeatherMaking;
import com.agaroth.world.content.skill.impl.crafting.Tanning;
import com.agaroth.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.agaroth.world.content.skill.impl.dungeoneering.DungeoneeringParty;
import com.agaroth.world.content.skill.impl.dungeoneering.ItemBinding;
import com.agaroth.world.content.skill.impl.fletching.Fletching;
import com.agaroth.world.content.skill.impl.herblore.IngridientsBook;
import com.agaroth.world.content.skill.impl.runecrafting.Runecrafting;
import com.agaroth.world.content.skill.impl.runecrafting.RunecraftingData;
import com.agaroth.world.content.skill.impl.slayer.Slayer;
import com.agaroth.world.content.skill.impl.smithing.SmithingData;
import com.agaroth.world.content.skill.impl.summoning.PouchMaking;
import com.agaroth.world.content.skill.impl.summoning.SummoningTab;
import com.agaroth.world.content.transportation.TeleportHandler;
import com.agaroth.world.content.transportation.TeleportType;
import com.agaroth.world.entity.impl.player.Player;

public class ButtonClickPacketListener implements PacketListener {
  public static final int OPCODE = 185;
  
  public void handleMessage(Player player, Packet packet) {
    int id = packet.readShort();
    if (player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.OWNER) {
      player.getPacketSender().sendConsoleMessage("Clicked button: " + id);
    }
    if (checkHandlers(player, id)) {
      return;
    }
    if(Runecrafting.runecraftingButton(player, id)) {
		RunecraftingData.RuneData rune = RunecraftingData.RuneData.forId(id);
		if(rune == null)
			return;
		Runecrafting.craftRunes(player, rune);
		return;
	}
    switch (id) {
    case 26250:
    	player.getPacketSender().sendMessage("@red@ Please use command @blu@::invite <Player name> @red@to send invitation.");
    	break;
	case 26003:
		player.getPacketSender().sendInterfaceRemoval();
		break;
	case -26376:
		PlayersOnlineInterface.showInterface(player);
		break;
    case -26372: 
      PlayerPanel.DD(player);
      break;
    case -26371: 
      PlayerPanel.AccountInfo(player);
      break;
    case -26370: 
      PlayerPanel.Statistics(player);
      break;
    case -26369: 
      PlayerPanel.Slayer(player);
      break;
    case -26368: 
      PlayerPanel.Quest(player);
      break;
    case -26366: 
      PlayerPanel.Links(player);
      break;
    case -27534: 
    case -27454: 
    case 5384: 
      player.getPacketSender().sendInterfaceRemoval();
      break;
    case 1036: 
      player.getMovementQueue().reset();
      EnergyHandler.rest(player);
      break;
    case 27229: 
      DungeoneeringParty.create(player);
      break;
    case 26226: 
    case 26229: 
      if (Dungeoneering.doingDungeoneering(player))
      {
        DialogueManager.start(player, 114);
        player.setDialogueActionId(71);
      }
      else
      {
        Dungeoneering.leave(player, false, true);
      }
      break;
    case 26244: 
    case 26247: 
      if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
        if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().getUsername().equals(player.getUsername()))
        {
          DialogueManager.start(player, id == 26247 ? 106 : 105);
          player.setDialogueActionId(id == 26247 ? 68 : 67);
        }
        else
        {
          player.getPacketSender().sendMessage("Only the party owner can change this setting.");
        }
      }
      break;
    case 14176: 
      player.setUntradeableDropItem(null);
      player.getPacketSender().sendInterfaceRemoval();
      break;
    case 14175: 
      player.getPacketSender().sendInterfaceRemoval();
      if ((player.getUntradeableDropItem() != null) && (player.getInventory().contains(player.getUntradeableDropItem().getId())))
      {
        ItemBinding.unbindItem(player, player.getUntradeableDropItem().getId());
        player.getInventory().delete(player.getUntradeableDropItem());
        player.getPacketSender().sendMessage("Your item vanishes as it hits the floor.");
        Sounds.sendSound(player, Sounds.Sound.DROP_ITEM);
      }
      player.setUntradeableDropItem(null);
      break;
    case 1013: 
      player.getSkillManager().setTotalGainedExp(0L);
      break;
    case -10507: 
  /*    if (player.PanelOpen == 6)
      {
        player.getPacketSender().sendString(1, "www.lunarisle.org/community/index.php?/forum/59-feedback-suggestions/");
        player.getPacketSender().sendMessage("Attempting to open: lunarisle.org/forums/suggestions");
      }
      break; */
    case -10508: 
      if (player.PanelOpen == 6)
      {
        player.getPacketSender().sendString(1, "http://sintennialps.freeforums.net/board/7/suggestions");
        player.getPacketSender().sendMessage("Attempting to open Sintennial suggestions page...");
      }
      break;
    case -10509: 
      if (player.PanelOpen == 6)
      {
        player.getPacketSender().sendString(1, "https://discord.gg/P8bmfCe");
        player.getPacketSender().sendMessage("Attempting to join Sintennial's discord server...");
      }
      if (player.PanelOpen == 4)
      {
        KillsTracker.open(player);
      }
      break;
    case -10510: 
      if (player.PanelOpen == 6)
      {
        player.getPacketSender().sendString(1, "http://sintennialps.freeforums.net/thread/9/game-rules");
        player.getPacketSender().sendMessage("Attempting to open Sintennial game rules...");
      }
      break;
    case -10511:
      if (player.PanelOpen == 6) {
          player.getPacketSender().sendString(1, "http://sintennial.everythingrs.com/services/hiscores");
        player.getPacketSender().sendMessage("Attempting to open Sintennial's Hiscores...");
      }
      break;
    case -10512: 
      if (player.PanelOpen == 6)
      {
        player.getPacketSender().sendString(1, "http://sintennial.everythingrs.com/services/store");
        player.getPacketSender().sendMessage("Attempting to open Sintennial donation page...");
      }
      break;
    case -10513: 
      if (player.PanelOpen == 6)
      {
        player.getPacketSender().sendString(1, "http://sintennial.everythingrs.com/services/vote");
        player.getPacketSender().sendMessage("Attempting to open voting portal...");
      }
      break;
    case -10514: 
      if (player.PanelOpen == 1)
      {
        if (WellOfGoodwill.isActive()) {
          player.getPacketSender().sendMessage("<img=11> <col=A52A2A>The Well of Goodwill is granting 30% bonus experience for another " + WellOfGoodwill.getMinutesRemaining() + " minutes.");
        } else {
          player.getPacketSender().sendMessage("<img=11> <col=A52A2A>The Well of Goodwill needs another " + Misc.insertCommasToNumber(new StringBuilder().append(WellOfGoodwill.getMissingAmount()).toString()) + " coins before becoming full.");
        }
      }
      else if (player.PanelOpen == 4)
      {
        DropLog.open(player);
      }
      else if (player.PanelOpen == 5)
      {
        Nomad.openQuestLog(player);
      }
      else if (player.PanelOpen == 6)
      {
        player.getPacketSender().sendString(1, "http://sintennialps.freeforums.net/");
        player.getPacketSender().sendMessage("Attempting to open Sintennial forums...");
      }
      break;
    case -10515: 
      if (player.PanelOpen == 4)
      {
        KillsTracker.open(player);
      }
      else if (player.PanelOpen == 5)
      {
        RecipeForDisaster.openQuestLog(player);
      }
      else if (player.PanelOpen == 6)
      {
        player.getPacketSender().sendString(1, "www.lunarisle.org/");
        player.getPacketSender().sendMessage("Attempting to open: ");
      }
      break;
    case -10531: 
      player.setKillsTrackerOpen(false);
      player.getPacketSender().sendTabInterface(2, 639);
      PlayerPanel.refreshPanel(player);
      break;
    case 11014: 
      player.setDialogueActionId(151);
      DialogueManager.start(player, 151);
      break;
    case -26333: 
      player.getPacketSender().sendString(1, "www.lunarisle.org/community/");
      player.getPacketSender().sendMessage("Attempting to open: lunarisle.org/community");
      break;
    case -26332: 
      player.getPacketSender().sendString(1, "www.lunarisle.org/community/index.php?/forum/36-agaroth-official-rules/");
      player.getPacketSender().sendMessage("Attempting to open: lunarisle.org/rules");
      break;
    case -26331: 
      player.getPacketSender().sendString(1, "www.lunarisle.org/token.php");
      player.getPacketSender().sendMessage("Attempting to open: lunarisle.org/store");
      break;
    case -26330: 
      player.getPacketSender().sendString(1, "www.lunarisle.org/vote");
      player.getPacketSender().sendMessage("Attempting to open: lunarisle.org/vote");
      break;
    case -26329: 
      player.getPacketSender().sendString(1, "www.lunarisle.org/highscores/");
      player.getPacketSender().sendMessage("Attempting to open: lunarisle.org/hiscores");
      break;
    case -26328: 
      player.getPacketSender().sendString(1, "www.lunarisle.org/community/index.php?/forum/63-report-player-abuse/");
      player.getPacketSender().sendMessage("Attempting to open: lunarisle.org/report");
      break;
    case 350: 
      player.getPacketSender().sendMessage("To autocast a spell, please right-click it and choose the autocast option.").sendTab(6).sendConfig(108, player.getAutocastSpell() == null ? 3 : 1);
      break;
    case 29335: 
      if (player.getInterfaceId() > 0)
      {
        player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
        return;
      }
      DialogueManager.start(player, 60);
      player.setDialogueActionId(27);
      break;
    case 29455: 
      if (player.getInterfaceId() > 0)
      {
        player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
        return;
      }
      ClanChatManager.toggleLootShare(player);
      break;
    case 11001: 
      TeleportHandler.teleportPlayer(player, GameSettings.DEFAULT_POSITION.copy(), player.getSpellbook().getTeleportType());
      break;
    case 1159: 
    case 15877: 
    case 30306: 
      MagicSpells.handleMagicSpells(player, id);
      break;
    case 10001: 
      if (player.getInterfaceId() == -1) {
        Consumables.handleHealAction(player);
      } else {
        player.getPacketSender().sendMessage("You cannot heal yourself right now.");
      }
      break;
    case 18025: 
      if (PrayerHandler.isActivated(player, 27)) {
        PrayerHandler.deactivatePrayer(player, 27);
      } else {
        PrayerHandler.activatePrayer(player, 27);
      }
      break;
    case 18018: 
      if (PrayerHandler.isActivated(player, 26)) {
        PrayerHandler.deactivatePrayer(player, 26);
      } else {
        PrayerHandler.activatePrayer(player, 26);
      }
      break;
    case 950: 
    case 10000: 
      if (player.getInterfaceId() < 0) {
        player.getPacketSender().sendInterface(40030);
      } else {
        player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
      }
      break;
    case 3420: 
    case 3546: 
      if (System.currentTimeMillis() - player.getTrading().lastAction <= 300L) {
        return;
      }
      player.getTrading().lastAction = System.currentTimeMillis();
      if (player.getTrading().inTrade()) {
        player.getTrading().acceptTrade(id == 3546 ? 2 : 1);
      } else {
        player.getPacketSender().sendInterfaceRemoval();
      }
      break;
    case -18269: 
    case 10162: 
      player.getPacketSender().sendInterfaceRemoval();
      break;
    case 841:
    	if (player.getInterfaceId() != -1 && DropTable.tableopen == true) {
    	DropTable.openTable(player, player.getCurrentBookPage() + 2, true);
    	} else if (player.getInterfaceId() != -1 && IngridientsBook.bookopen == true) {
    	IngridientsBook.readBook(player, player.getCurrentBookPage() + 2, true);
    	}
      break;
    case 839: 
    	if (player.getInterfaceId() != -1 && DropTable.tableopen == true) {
    	DropTable.openTable(player, player.getCurrentBookPage() - 2, true);
        } else if (player.getInterfaceId() != -1 && IngridientsBook.bookopen == true) {	
        IngridientsBook.readBook(player, player.getCurrentBookPage() - 2, true);
        }
      break;
    case 14922: 
      player.getPacketSender().sendClientRightClickRemoval().sendInterfaceRemoval();
      break;
    case 14921: 
      player.getPacketSender().sendMessage("Please visit the forums and ask for help in the support section.");
      break;
    case 5294: 
        player.getPacketSender().sendClientRightClickRemoval().sendInterfaceRemoval();
        player.setDialogueActionId(player.getBankPinAttributes().hasBankPin() ? 8 : 7);
        DialogueManager.start(player, (Dialogue)DialogueManager.getDialogues().get(Integer.valueOf(player.getBankPinAttributes().hasBankPin() ? 12 : 9)));
        break;
    case 15002:
		if(!player.busy() && !player.getCombatBuilder().isBeingAttacked() && !Dungeoneering.doingDungeoneering(player)) {
			player.getSkillManager().stopSkilling();
			player.getPriceChecker().open();
		} else {
			player.getPacketSender().sendMessage("You cannot open this right now.");
		}
		break;
    case 1511: 
    case 2735: 
      if (player.getSummoning().getBeastOfBurden() != null)
      {
        player.getSummoning().toInventory();
        player.getPacketSender().sendInterfaceRemoval();
      }
      else
      {
        player.getPacketSender().sendMessage("You do not have a familiar who can hold items.");
      }
      break;
    case -11507: 
    case -11504: 
    case -11501: 
    case -11498: 
    case 1018: 
    case 1019: 
    case 1020: 
    case 1021: 
      if (id == -11504) {
        SummoningTab.renewFamiliar(player);
      } else if (id == -11501) {
        SummoningTab.callFollower(player);
      } else if (id == -11498) {
        SummoningTab.handleDismiss(player, false);
      } else if (id == 54029) {
        player.getSummoning().store();
      } else if (id == -11507) {
        player.getSummoning().toInventory();
      }
      break;
    case 11004: 
      DialogueManager.start(player, 127);
      player.setDialogueActionId(127);
      break;
    case 11008: 
      player.setDialogueActionId(0);
      DialogueManager.start(player, 0);
      break;
    case 11017: 
      DialogueManager.start(player, 34);
      player.setDialogueActionId(15);
      break;
    case 11011: 
      DialogueManager.start(player, 22);
      player.setDialogueActionId(14);
      break;
    case 11020: 
      DialogueManager.start(player, 21);
      player.setDialogueActionId(12);
      break;
    case 8656: //Mining teleport skill tab  
        TeleportHandler.teleportPlayer(player, new Position(3023, 9740), TeleportType.NORMAL);
        break;
    case 8658: //Agility teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(2480, 3435), TeleportType.NORMAL);
    	break;
    case 8659: //Smithing teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(3023, 9740), TeleportType.NORMAL);
    	break;
    case 8861: //Herblore teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(2914, 3450), TeleportType.NORMAL);
    	break;
    case 8662: //Fishing teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(2345, 3698), TeleportType.NORMAL);
    	break;
    case 8664: //Thieving teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(3094, 3502), TeleportType.NORMAL);
    	break;
    case 8665: //Cooking teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(2816, 3440), TeleportType.NORMAL);
    	break;
    case 8667: //Crafting teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(2742, 3443), TeleportType.NORMAL);
    	break;
    case 8668: //Firemaking teleport skill tab
        TeleportHandler.teleportPlayer(player, new Position(2709, 3437), TeleportType.NORMAL);
        break;
    case 8670: //Fletching teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(2717, 3499), TeleportType.NORMAL);
    	break;
    case 8671: //Woodcutting teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(2725, 3485), TeleportType.NORMAL);
    	break;
    case 8672: //Runecrafting teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(2596, 4772), TeleportType.NORMAL);
    	break;
    case 12162: //Slayer teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(3146, 9914), TeleportType.NORMAL);
    	break;
    case 13928: //Farming teleport skill tab	
    	TeleportHandler.teleportPlayer(player, new Position(3052, 3304), TeleportType.NORMAL);
    	break;
    case 28178: //Hunter teleport skill tab   	
    	TeleportHandler.teleportPlayer(player, new Position(2589, 4319), TeleportType.NORMAL);
    	break;
    case 28179: //Summoning teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(2209, 5348), TeleportType.NORMAL);
    	break;
    case 28180: //Dungeoneering teleport skill tab
    	TeleportHandler.teleportPlayer(player, new Position(3450, 3716), TeleportType.NORMAL);
    	break;
    case 1747: 
    case 1748: 
    case 2798: 
    case 2799: 
    case 8871: 
    case 8875: 
    case 8886: 
    case 8890: 
    case 8894: 
      ChatboxInterfaceSkillAction.handleChatboxInterfaceButtons(player, id);
      break;
    case 14873: 
    case 14874: 
    case 14875: 
    case 14876: 
    case 14877: 
    case 14878: 
    case 14879: 
    case 14880: 
    case 14881: 
    case 14882: 
      BankPin.clickedButton(player, id);
      break;
    case 22012: 
    case 27005: 
      if ((!player.isBanking()) || (player.getInterfaceId() != 5292)) {
        return;
      }
      Bank.depositItems(player, id == 27005 ? player.getEquipment() : player.getInventory(), false);
      break;
    case 27023: 
      if ((!player.isBanking()) || (player.getInterfaceId() != 5292)) {
        return;
      }
      if (player.getSummoning().getBeastOfBurden() == null)
      {
        player.getPacketSender().sendMessage("You do not have a familiar which can hold items.");
        return;
      }
      Bank.depositItems(player, player.getSummoning().getBeastOfBurden(), false);
      break;
    case 22008: 
      if ((!player.isBanking()) || (player.getInterfaceId() != 5292)) {
        return;
      }
      player.setNoteWithdrawal(!player.withdrawAsNote());
      break;
    case 21000: 
      if ((!player.isBanking()) || (player.getInterfaceId() != 5292)) {
        return;
      }
      player.setSwapMode(false);
      player.getPacketSender().sendConfig(304, 0).sendMessage("This feature is coming soon!");
      
      break;
    case 27009: 
      MoneyPouch.toBank(player);
      break;
    case 27014: 
    case 27015: 
    case 27016: 
    case 27017: 
    case 27018: 
    case 27019: 
    case 27020: 
    case 27021: 
    case 27022: 
      if (!player.isBanking()) {
        return;
      }
      if (player.getBankSearchingAttribtues().isSearchingBank()) {
        Bank.BankSearchAttributes.stopSearch(player, true);
      }
      int bankId = id - 27014;
      boolean empty = bankId > 0 ? Bank.isEmpty(player.getBank(bankId)) : false;
      if ((!empty) || (bankId == 0))
      {
        player.setCurrentBankTab(bankId);
        player.getPacketSender().sendString(5385, "scrollreset");
        player.getPacketSender().sendString(27002, Integer.toString(player.getCurrentBankTab()));
        player.getPacketSender().sendString(27000, "1");
        player.getBank(bankId).open();
      }
      else
      {
        player.getPacketSender().sendMessage("To create a new tab, please drag an item here.");
      }
      break;
    case 22004: 
      if (!player.isBanking()) {
        return;
      }
      if (!player.getBankSearchingAttribtues().isSearchingBank())
      {
        player.getBankSearchingAttribtues().setSearchingBank(true);
        player.setInputHandling(new EnterSyntaxToBankSearchFor());
        player.getPacketSender().sendEnterInputPrompt("What would you like to search for?");
      }
      else
      {
        Bank.BankSearchAttributes.stopSearch(player, true);
      }
      break;
    case 150: 
    case 22845: 
    case 24010: 
    case 24041: 
    case 24115: 
      player.setAutoRetaliate(!player.isAutoRetaliate());
      break;
    case 29332: 
      ClanChat clan = player.getCurrentClanChat();
      if (clan == null)
      {
        player.getPacketSender().sendMessage("You are not in a clanchat channel.");
        return;
      }
      ClanChatManager.leave(player, false);
      player.setClanChatName(null);
      break;
    case 29329: 
      if (player.getInterfaceId() > 0)
      {
        player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
        return;
      }
      player.setInputHandling(new EnterClanChatToJoin());
      player.getPacketSender().sendEnterInputPrompt("Enter the name of the clanchat channel you wish to join:");
      break;
    case 152: 
    case 19158: 
      if (player.getRunEnergy() <= 1)
      {
        player.getPacketSender().sendMessage("You do not have enough energy to do this.");
        player.setRunning(false);
      }
      else
      {
        player.setRunning(!player.isRunning());
      }
      player.getPacketSender().sendRunStatus();
      break;
    case 15004: 
      player.setExperienceLocked(!player.experienceLocked());
      String type = player.experienceLocked() ? "locked" : "unlocked";
      player.getPacketSender().sendMessage("Your experience is now " + type + ".");
      PlayerPanel.refreshPanel(player);
      break;
    case 21341: 
    case 27651: 
	case 15001:
		if(player.getInterfaceId() == -1) {
			player.getSkillManager().stopSkilling();
			BonusManager.update(player);
			player.getPacketSender().sendInterface(21172);
		} else 
			player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
		break;
	case 15003:
		if(player.getInterfaceId() > 0) {
			player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
			return;
		}
		player.getSkillManager().stopSkilling();
		ItemsKeptOnDeath.sendInterface(player);
		break;
    case 2458: 
      if (player.logout()) {
        World.getPlayers().remove(player);
      }
      break;
    case 7462: 
    case 7473: 
    case 7487: 
    case 7537: 
    case 7548: 
    case 7562: 
    case 7587: 
    case 7612: 
    case 7637: 
    case 7662: 
    case 7687: 
    case 7788: 
    case 8481: 
    case 10003: 
    case 12311: 
    case 12322: 
    case 29038: 
    case 29063: 
    case 29113: 
    case 29138: 
    case 29163: 
    case 29188: 
    case 29213: 
    case 29238: 
    case 30007: 
    case 30108: 
    case 33033: 
    case 48023: 
      CombatSpecial.activate(player);
      break;
    case 1772: 
      if (player.getWeapon() == WeaponInterface.SHORTBOW) {
        player.setFightType(FightType.SHORTBOW_ACCURATE);
      } else if (player.getWeapon() == WeaponInterface.LONGBOW) {
        player.setFightType(FightType.LONGBOW_ACCURATE);
      } else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
        player.setFightType(FightType.CROSSBOW_ACCURATE);
      }
      break;
    case 1771: 
      if (player.getWeapon() == WeaponInterface.SHORTBOW) {
        player.setFightType(FightType.SHORTBOW_RAPID);
      } else if (player.getWeapon() == WeaponInterface.LONGBOW) {
        player.setFightType(FightType.LONGBOW_RAPID);
      } else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
        player.setFightType(FightType.CROSSBOW_RAPID);
      }
      break;
    case 1770: 
      if (player.getWeapon() == WeaponInterface.SHORTBOW) {
        player.setFightType(FightType.SHORTBOW_LONGRANGE);
      } else if (player.getWeapon() == WeaponInterface.LONGBOW) {
        player.setFightType(FightType.LONGBOW_LONGRANGE);
      } else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
        player.setFightType(FightType.CROSSBOW_LONGRANGE);
      }
      break;
    case 2282: 
      if (player.getWeapon() == WeaponInterface.DAGGER) {
        player.setFightType(FightType.DAGGER_STAB);
      } else if (player.getWeapon() == WeaponInterface.SWORD) {
        player.setFightType(FightType.SWORD_STAB);
      }
      break;
    case 2285: 
      if (player.getWeapon() == WeaponInterface.DAGGER) {
        player.setFightType(FightType.DAGGER_LUNGE);
      } else if (player.getWeapon() == WeaponInterface.SWORD) {
        player.setFightType(FightType.SWORD_LUNGE);
      }
      break;
    case 2284: 
      if (player.getWeapon() == WeaponInterface.DAGGER) {
        player.setFightType(FightType.DAGGER_SLASH);
      } else if (player.getWeapon() == WeaponInterface.SWORD) {
        player.setFightType(FightType.SWORD_SLASH);
      }
      break;
    case 2283: 
      if (player.getWeapon() == WeaponInterface.DAGGER) {
        player.setFightType(FightType.DAGGER_BLOCK);
      } else if (player.getWeapon() == WeaponInterface.SWORD) {
        player.setFightType(FightType.SWORD_BLOCK);
      }
      break;
    case 2429: 
      if (player.getWeapon() == WeaponInterface.SCIMITAR) {
        player.setFightType(FightType.SCIMITAR_CHOP);
      } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
        player.setFightType(FightType.LONGSWORD_CHOP);
      }
      break;
    case 2432: 
      if (player.getWeapon() == WeaponInterface.SCIMITAR) {
        player.setFightType(FightType.SCIMITAR_SLASH);
      } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
        player.setFightType(FightType.LONGSWORD_SLASH);
      }
      break;
    case 2431: 
      if (player.getWeapon() == WeaponInterface.SCIMITAR) {
        player.setFightType(FightType.SCIMITAR_LUNGE);
      } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
        player.setFightType(FightType.LONGSWORD_LUNGE);
      }
      break;
    case 2430: 
      if (player.getWeapon() == WeaponInterface.SCIMITAR) {
        player.setFightType(FightType.SCIMITAR_BLOCK);
      } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
        player.setFightType(FightType.LONGSWORD_BLOCK);
      }
      break;
    case 3802: 
      player.setFightType(FightType.MACE_POUND);
      break;
    case 3805: 
      player.setFightType(FightType.MACE_PUMMEL);
      break;
    case 3804: 
      player.setFightType(FightType.MACE_SPIKE);
      break;
    case 3803: 
      player.setFightType(FightType.MACE_BLOCK);
      break;
    case 4454: 
      if (player.getWeapon() == WeaponInterface.KNIFE) {
        player.setFightType(FightType.KNIFE_ACCURATE);
      } else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
        player.setFightType(FightType.THROWNAXE_ACCURATE);
      } else if (player.getWeapon() == WeaponInterface.DART) {
        player.setFightType(FightType.DART_ACCURATE);
      } else if (player.getWeapon() == WeaponInterface.JAVELIN) {
        player.setFightType(FightType.JAVELIN_ACCURATE);
      }
      break;
    case 4453: 
      if (player.getWeapon() == WeaponInterface.KNIFE) {
        player.setFightType(FightType.KNIFE_RAPID);
      } else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
        player.setFightType(FightType.THROWNAXE_RAPID);
      } else if (player.getWeapon() == WeaponInterface.DART) {
        player.setFightType(FightType.DART_RAPID);
      } else if (player.getWeapon() == WeaponInterface.JAVELIN) {
        player.setFightType(FightType.JAVELIN_RAPID);
      }
      break;
    case 4452: 
      if (player.getWeapon() == WeaponInterface.KNIFE) {
        player.setFightType(FightType.KNIFE_LONGRANGE);
      } else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
        player.setFightType(FightType.THROWNAXE_LONGRANGE);
      } else if (player.getWeapon() == WeaponInterface.DART) {
        player.setFightType(FightType.DART_LONGRANGE);
      } else if (player.getWeapon() == WeaponInterface.JAVELIN) {
        player.setFightType(FightType.JAVELIN_LONGRANGE);
      }
      break;
    case 4685: 
      player.setFightType(FightType.SPEAR_LUNGE);
      break;
    case 4688: 
      player.setFightType(FightType.SPEAR_SWIPE);
      break;
    case 4687: 
      player.setFightType(FightType.SPEAR_POUND);
      break;
    case 4686: 
      player.setFightType(FightType.SPEAR_BLOCK);
      break;
    case 4711: 
      player.setFightType(FightType.TWOHANDEDSWORD_CHOP);
      break;
    case 4714: 
      player.setFightType(FightType.TWOHANDEDSWORD_SLASH);
      break;
    case 4713: 
      player.setFightType(FightType.TWOHANDEDSWORD_SMASH);
      break;
    case 4712: 
      player.setFightType(FightType.TWOHANDEDSWORD_BLOCK);
      break;
    case 5576: 
      player.setFightType(FightType.PICKAXE_SPIKE);
      break;
    case 5579: 
      player.setFightType(FightType.PICKAXE_IMPALE);
      break;
    case 5578: 
      player.setFightType(FightType.PICKAXE_SMASH);
      break;
    case 5577: 
      player.setFightType(FightType.PICKAXE_BLOCK);
      break;
    case 7768: 
      player.setFightType(FightType.CLAWS_CHOP);
      break;
    case 7771: 
      player.setFightType(FightType.CLAWS_SLASH);
      break;
    case 7770: 
      player.setFightType(FightType.CLAWS_LUNGE);
      break;
    case 7769: 
      player.setFightType(FightType.CLAWS_BLOCK);
      break;
    case 8466: 
      player.setFightType(FightType.HALBERD_JAB);
      break;
    case 8468: 
      player.setFightType(FightType.HALBERD_SWIPE);
      break;
    case 8467: 
      player.setFightType(FightType.HALBERD_FEND);
      break;
    case 5862: 
      player.setFightType(FightType.UNARMED_PUNCH);
      break;
    case 5861: 
      player.setFightType(FightType.UNARMED_KICK);
      break;
    case 5860: 
      player.setFightType(FightType.UNARMED_BLOCK);
      break;
    case 12298: 
      player.setFightType(FightType.WHIP_FLICK);
      break;
    case 12297: 
      player.setFightType(FightType.WHIP_LASH);
      break;
    case 12296: 
      player.setFightType(FightType.WHIP_DEFLECT);
      break;
    case 336: 
      player.setFightType(FightType.STAFF_BASH);
      break;
    case 335: 
      player.setFightType(FightType.STAFF_POUND);
      break;
    case 334: 
      player.setFightType(FightType.STAFF_FOCUS);
      break;
    case 433: 
      player.setFightType(FightType.WARHAMMER_POUND);
      break;
    case 432: 
      player.setFightType(FightType.WARHAMMER_PUMMEL);
      break;
    case 431: 
      player.setFightType(FightType.WARHAMMER_BLOCK);
      break;
    case 782: 
      player.setFightType(FightType.SCYTHE_REAP);
      break;
    case 784: 
      player.setFightType(FightType.SCYTHE_CHOP);
      break;
    case 785: 
      player.setFightType(FightType.SCYTHE_JAB);
      break;
    case 783: 
      player.setFightType(FightType.SCYTHE_BLOCK);
      break;
    case 1704: 
      player.setFightType(FightType.BATTLEAXE_CHOP);
      break;
    case 1707: 
      player.setFightType(FightType.BATTLEAXE_HACK);
      break;
    case 1706: 
      player.setFightType(FightType.BATTLEAXE_SMASH);
      break;
    case 1705: 
      player.setFightType(FightType.BATTLEAXE_BLOCK);
    }
  }
  
  private boolean checkHandlers(Player player, int id)
  {
    switch (id)
    {
    case 2461: 
    case 2462: 
    case 2471: 
    case 2472: 
    case 2473: 
    case 2482: 
    case 2483:
    case 2484: 
    case 2485:
    case 2486:
    case 2494: 
    case 2495: 
    case 2496:
    case 2497: 
    case 2498: 
      DialogueOptions.handle(player, id);
      return true;
    }
    if ((player.isPlayerLocked()) && (id != 2458)) {
      return true;
    }
    if (Achievements.handleButton(player, id)) {
      return true;
    }
    if (Sounds.handleButton(player, id)) {
      return true;
    }
    if (PrayerHandler.isButton(id))
    {
      PrayerHandler.togglePrayerWithActionButton(player, id);
      return true;
    }
    if (CurseHandler.isButton(player, id)) {
      return true;
    }
    if (Autocasting.handleAutocast(player, id)) {
      return true;
    }
    if (SmithingData.handleButtons(player, id)) {
      return true;
    }
    if (PouchMaking.pouchInterface(player, id)) {
      return true;
    }
    if (LoyaltyProgramme.handleButton(player, id)) {
      return true;
    }
    if (Fletching.fletchingButton(player, id)) {
      return true;
    }
    if ((LeatherMaking.handleButton(player, id)) || (Tanning.handleButton(player, id))) {
      return true;
    }
    if (Emotes.doEmote(player, id)) {
      return true;
    }
    if (PestControl.handleInterface(player, id)) {
      return true;
    }
    if ((player.getLocation() == Location.DUEL_ARENA) && (Dueling.handleDuelingButtons(player, id))) {
      return true;
    }
    if (Slayer.handleRewardsInterface(player, id)) {
      return true;
    }
    if (ExperienceLamps.handleButton(player, id)) {
      return true;
    }
	if(PlayersOnlineInterface.handleButton(player, id)) {
		return true;
    }
    if (GrandExchange.handleButton(player, id)) {
      return true;
    }
    if (ClanChatManager.handleClanChatSetupButton(player, id)) {
      return true;
    }
    return false;
  }
}
