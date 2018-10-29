package com.agaroth.world.entity.impl.player;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.agaroth.GameSettings;
import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.engine.task.impl.PlayerDeathTask;
import com.agaroth.engine.task.impl.WalkToTask;
import com.agaroth.model.Animation;
import com.agaroth.model.Appearance;
import com.agaroth.model.CharacterAnimations;
import com.agaroth.model.ChatMessage;
import com.agaroth.model.DwarfCannon;
import com.agaroth.model.Flag;
import com.agaroth.model.GameMode;
import com.agaroth.model.GameObject;
import com.agaroth.model.Item;
import com.agaroth.model.MagicSpellbook;
import com.agaroth.model.PlayerInteractingOption;
import com.agaroth.model.PlayerRelations;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Position;
import com.agaroth.model.Prayerbook;
import com.agaroth.model.Skill;
import com.agaroth.model.container.impl.Bank;
import com.agaroth.model.container.impl.Equipment;
import com.agaroth.model.container.impl.Inventory;
import com.agaroth.model.container.impl.PriceChecker;
import com.agaroth.model.container.impl.Shop;
import com.agaroth.model.container.impl.Bank.BankSearchAttributes;
import com.agaroth.model.definitions.WeaponAnimations;
import com.agaroth.model.definitions.WeaponInterfaces;
import com.agaroth.model.definitions.WeaponInterfaces.WeaponInterface;
import com.agaroth.model.input.Input;
import com.agaroth.net.PlayerSession;
import com.agaroth.net.SessionState;
import com.agaroth.net.packet.PacketSender;
import com.agaroth.util.FrameUpdater;
import com.agaroth.util.Stopwatch;
import com.agaroth.world.content.BonusManager;
import com.agaroth.world.content.PointsHandler;
import com.agaroth.world.content.Trading;
import com.agaroth.world.content.Achievements.AchievementAttributes;
import com.agaroth.world.content.BankPin.BankPinAttributes;
import com.agaroth.world.content.DropLog.DropLogEntry;
import com.agaroth.world.content.KillsTracker.KillsEntry;
import com.agaroth.world.content.LoyaltyProgramme.LoyaltyTitles;
import com.agaroth.world.content.clan.ClanChat;
import com.agaroth.world.content.combat.CombatFactory;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.effect.CombatPoisonEffect.CombatPoisonData;
import com.agaroth.world.content.combat.magic.CombatSpell;
import com.agaroth.world.content.combat.magic.CombatSpells;
import com.agaroth.world.content.combat.prayer.CurseHandler;
import com.agaroth.world.content.combat.prayer.PrayerHandler;
import com.agaroth.world.content.combat.pvp.PlayerKillingAttributes;
import com.agaroth.world.content.combat.range.CombatRangedAmmo.RangedWeaponData;
import com.agaroth.world.content.combat.strategy.CombatStrategies;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.content.combat.weapon.CombatSpecial;
import com.agaroth.world.content.combat.weapon.FightType;
import com.agaroth.world.content.dialogue.Dialogue;
import com.agaroth.world.content.grandexchange.GrandExchangeSlot;
import com.agaroth.world.content.minigames.MinigameAttributes;
import com.agaroth.world.content.minigames.impl.Dueling;
import com.agaroth.world.content.skill.SkillManager;
import com.agaroth.world.content.skill.impl.farming.Farming;
import com.agaroth.world.content.skill.impl.slayer.Slayer;
import com.agaroth.world.content.skill.impl.summoning.Pouch;
import com.agaroth.world.content.skill.impl.summoning.Summoning;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;


public class Player extends Character {

	public Player(PlayerSession playerIO) {
		super(GameSettings.DEFAULT_POSITION.copy());
		this.session = playerIO;
	}


	@Override
	public void appendDeath() {
		if(!isDying) {
			isDying = true;
			TaskManager.submit(new PlayerDeathTask(this));
		}
	}

	@Override
	public int getConstitution() {
		return getSkillManager().getCurrentLevel(Skill.CONSTITUTION);
	}

	@Override
	public Character setConstitution(int constitution) {
		if(isDying) {
			return this;
		}
		skillManager.setCurrentLevel(Skill.CONSTITUTION, constitution);
		packetSender.sendSkill(Skill.CONSTITUTION);
		if(getConstitution() <= 0 && !isDying)
			appendDeath();
		return this;
	}

	@Override
	public void heal(int amount) {
		int level = skillManager.getMaxLevel(Skill.CONSTITUTION);
		if ((skillManager.getCurrentLevel(Skill.CONSTITUTION) + amount) >= level) {
			setConstitution(level);
		} else {
			setConstitution(skillManager.getCurrentLevel(Skill.CONSTITUTION) + amount);
		}
	}

	@Override
	public int getBaseAttack(CombatType type) {
		if (type == CombatType.RANGED)
			return skillManager.getCurrentLevel(Skill.RANGED);
		else if (type == CombatType.MAGIC)
			return skillManager.getCurrentLevel(Skill.MAGIC);
		return skillManager.getCurrentLevel(Skill.ATTACK);
	}

	@Override
	public int getBaseDefence(CombatType type) {
		if (type == CombatType.MAGIC)
			return skillManager.getCurrentLevel(Skill.MAGIC);
		return skillManager.getCurrentLevel(Skill.DEFENCE);
	}

	@Override
	public int getAttackSpeed() {
		int speed = weapon.getSpeed();
		String weapon = equipment.get(Equipment.WEAPON_SLOT).getDefinition().getName();
		if(getCurrentlyCasting() != null) {
			if(getCurrentlyCasting() == CombatSpells.BLOOD_BLITZ.getSpell() || getCurrentlyCasting() == CombatSpells.SHADOW_BLITZ.getSpell() || getCurrentlyCasting() == CombatSpells.SMOKE_BLITZ.getSpell() || getCurrentlyCasting() == CombatSpells.ICE_BLITZ.getSpell()) {
				return 5;
			} else {
				return 6;
			}
		}
		int weaponId = equipment.get(Equipment.WEAPON_SLOT).getId();
		if(weaponId == 1419) {
			speed -= 2;
		}
		if (fightType == FightType.CROSSBOW_RAPID || fightType == FightType.LONGBOW_RAPID || weaponId == 6522 && fightType == FightType.KNIFE_RAPID || weapon.contains("rapier")) {
			if(weaponId != 11235) {
				speed--;
			}
		} else if(weaponId != 6522 && weaponId != 15241 && (fightType == FightType.SHORTBOW_RAPID || fightType == FightType.DART_RAPID || fightType == FightType.KNIFE_RAPID || fightType == FightType.THROWNAXE_RAPID || fightType == FightType.JAVELIN_RAPID) || weaponId == 11730) {
			speed -= 2;
		}
		return speed;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Player)) {
			return false;
		}

		Player p = (Player) o;
		return p.getIndex() == getIndex() || p.getUsername().equals(username);
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public void poisonVictim(Character victim, CombatType type) {
		if (type == CombatType.MELEE || weapon == WeaponInterface.DART || weapon == WeaponInterface.KNIFE || weapon == WeaponInterface.THROWNAXE || weapon == WeaponInterface.JAVELIN) {
			CombatFactory.poisonEntity(victim, CombatPoisonData.getPoisonType(equipment.get(Equipment.WEAPON_SLOT)));
		} else if (type == CombatType.RANGED) {
			CombatFactory.poisonEntity(victim, CombatPoisonData.getPoisonType(equipment.get(Equipment.AMMUNITION_SLOT)));
		}
	}

	public CombatStrategy getStrategy(int npc){
		return CombatStrategies.getStrategy(npc);
	}
	@Override
	public CombatStrategy determineStrategy() {
		if (specialActivated && castSpell == null) {
			if (combatSpecial.getCombatType() == CombatType.MELEE) {
				return CombatStrategies.getDefaultMeleeStrategy();
			} else if (combatSpecial.getCombatType() == CombatType.RANGED) {
				setRangedWeaponData(RangedWeaponData.getData(this));
				return CombatStrategies.getDefaultRangedStrategy();
			} else if (combatSpecial.getCombatType() == CombatType.MAGIC) {
				return CombatStrategies.getDefaultMagicStrategy();
			}
		}
		if (castSpell != null || autocastSpell != null) {
			return CombatStrategies.getDefaultMagicStrategy();
		}
		RangedWeaponData data = RangedWeaponData.getData(this);
		if (data != null) {
			setRangedWeaponData(data);
			return CombatStrategies.getDefaultRangedStrategy();
		}
		return CombatStrategies.getDefaultMeleeStrategy();
	}

	public void process() {
		process.sequence();
	}

	public void dispose() {
		save();
		packetSender.sendLogout();
	}

	public void save() {
		if (session.getState() != SessionState.LOGGED_IN && session.getState() != SessionState.LOGGING_OUT) {
			return;
		}
		PlayerSaving.save(this);
	}

	public boolean logout() {
		boolean debugMessage = false;
		int[] playerXP = new int[Skill.values().length];
		for (int i = 0; i <  Skill.values().length; i++) {
			playerXP[i] = this.getSkillManager().getExperience(Skill.forId(i));
		}
		com.everythingrs.hiscores.Hiscores.update("qxjv349m1511r2jf0fd5nrk92gncaov4n8vbo3yyix0t2ihpvi2f8mrr9p2zdvutoonyacfh6w29",  "Normal Mode", this.getUsername(),
				this.getRights().ordinal(), playerXP, debugMessage);
		com.everythingrs.hiscores.Hiscores.update("qxjv349m1511r2jf0fd5nrk92gncaov4n8vbo3yyix0t2ihpvi2f8mrr9p2zdvutoonyacfh6w29",  "Ironman Mode", this.getUsername(),
				this.getRights().ordinal(), playerXP, debugMessage);
		com.everythingrs.hiscores.Hiscores.update("qxjv349m1511r2jf0fd5nrk92gncaov4n8vbo3yyix0t2ihpvi2f8mrr9p2zdvutoonyacfh6w29",  "Hardcore Ironman Mode", this.getUsername(),
				this.getRights().ordinal(), playerXP, debugMessage);
		
		if (getCombatBuilder().logOutDelay()) {
			getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
			return false;
		}
		if(getConstitution() <= 0 || isDying || settingUpCannon || crossingObstacle) {
			getPacketSender().sendMessage("You cannot log out at the moment.");
			return false;
		}
		return true;
	}

	public void restart() {
		setFreezeDelay(0);
		setOverloadPotionTimer(0);
		setPrayerRenewalPotionTimer(0);
		setSpecialPercentage(100);
		setSpecialActivated(false);
		CombatSpecial.updateBar(this);
		setHasVengeance(false);
		setSkullTimer(0);
		setSkullIcon(0);
		setTeleblockTimer(0);
		setPoisonDamage(0);
		setStaffOfLightEffect(0);
		performAnimation(new Animation(65535));
		WeaponInterfaces.assign(this, getEquipment().get(Equipment.WEAPON_SLOT));
		WeaponAnimations.update(this);
		PrayerHandler.deactivateAll(this);
		CurseHandler.deactivateAll(this);
		getEquipment().refreshItems();
		getInventory().refreshItems();
		for (Skill skill : Skill.values())
			getSkillManager().setCurrentLevel(skill, getSkillManager().getMaxLevel(skill));
		setRunEnergy(100);
		setDying(false);
		getMovementQueue().setLockMovement(false).reset();
		getUpdateFlag().flag(Flag.APPEARANCE);
	}

	public boolean busy() {
		return interfaceId > 0 || isBanking || shopping || trading.inTrade() || dueling.inDuelScreen || isResting;
	}

	/*** STRINGS ***/
	public String username;
	private String password;
	private String serial_number;
	private String emailAddress;
	private String hostAddress;
	private String clanChatName;

	/*** LONGS **/
	public long claimTimer;
	public long summonSpecDelay;
	private Long longUsername;
	private long moneyInPouch;
	private long totalPlayTime;
	private long timer;
	public long DFSTimer;
	public long SwitchBookTimer;
	private long lastAnswered;
	
	/*** STOPWATCHES ***/
	private final Stopwatch sqlTimer = new Stopwatch();
	private final Stopwatch foodTimer = new Stopwatch();
	private final Stopwatch potionTimer = new Stopwatch();
	private final Stopwatch lastRunRecovery = new Stopwatch();
	private final Stopwatch clickDelay = new Stopwatch();
	private final Stopwatch lastItemPickup = new Stopwatch();
	private final Stopwatch lastYell = new Stopwatch();
	private final Stopwatch lastVengeance = new Stopwatch();
	private final Stopwatch emoteDelay = new Stopwatch();
	private final Stopwatch specialRestoreTimer = new Stopwatch();
	private final Stopwatch lastSummon = new Stopwatch();
	private final Stopwatch recordedLogin = new Stopwatch();
	private final Stopwatch tolerance = new Stopwatch();
	private final Stopwatch lougoutTimer = new Stopwatch();

	/*** INSTANCES ***/
	private final CopyOnWriteArrayList<KillsEntry> killsTracker = new CopyOnWriteArrayList<KillsEntry>();
	private final CopyOnWriteArrayList<DropLogEntry> dropLog = new CopyOnWriteArrayList<DropLogEntry>();
	private final CopyOnWriteArrayList<NPC> npc_faces_updated = new CopyOnWriteArrayList<NPC>();
	private final List<Player> localPlayers = new LinkedList<Player>();
	private final List<NPC> localNpcs = new LinkedList<NPC>();
	private PlayerSession session;
	private final PlayerProcess process = new PlayerProcess(this);
	private final PlayerKillingAttributes playerKillingAttributes = new PlayerKillingAttributes(this);
	private final MinigameAttributes minigameAttributes = new MinigameAttributes();
	private final BankPinAttributes bankPinAttributes = new BankPinAttributes();
	private final BankSearchAttributes bankSearchAttributes = new BankSearchAttributes();
	private final AchievementAttributes achievementAttributes = new AchievementAttributes();
	private CharacterAnimations characterAnimations = new CharacterAnimations();
	private final BonusManager bonusManager = new BonusManager();
	private final PointsHandler pointsHandler = new PointsHandler(this);
	private final PacketSender packetSender = new PacketSender(this);
	private final Appearance appearance = new Appearance(this);
	private final FrameUpdater frameUpdater = new FrameUpdater();
	private PlayerRights rights = PlayerRights.PLAYER;
	private SkillManager skillManager = new SkillManager(this);
	private PlayerRelations relations = new PlayerRelations(this);
	private ChatMessage chatMessages = new ChatMessage();
	private Inventory inventory = new Inventory(this);
	private Equipment equipment = new Equipment(this);
	private Equipment preSetEquipment = new Equipment(this);
	private PriceChecker priceChecker = new PriceChecker(this);
	private Trading trading = new Trading(this);
	private Dueling dueling = new Dueling(this);
	private Slayer slayer = new Slayer(this);
	private Farming farming = new Farming(this);
	private Summoning summoning = new Summoning(this);
	private Bank[] bankTabs = new Bank[9];
	private PlayerInteractingOption playerInteractingOption = PlayerInteractingOption.NONE;
	private GameMode gameMode = GameMode.NORMAL;
	private CombatType lastCombatType = CombatType.MELEE;
	private FightType fightType = FightType.UNARMED_PUNCH;
	private Prayerbook prayerbook = Prayerbook.NORMAL;
	private MagicSpellbook spellbook = MagicSpellbook.NORMAL;
	private LoyaltyTitles loyaltyTitle = LoyaltyTitles.NONE;
	private ClanChat currentClanChat;
	private Input inputHandling;
	private WalkToTask walkToTask;
	private Shop shop;
	private GameObject interactingObject;
	private Item interactingItem;
	private Dialogue dialogue;
	private DwarfCannon cannon;
	private CombatSpell autocastSpell, castSpell, previousCastSpell;
	private RangedWeaponData rangedWeaponData;
	private CombatSpecial combatSpecial;
	private WeaponInterface weapon;
	private Item untradeableDropItem;
	private Object[] usableObject;
	private GrandExchangeSlot[] grandExchangeSlots = new GrandExchangeSlot[6];
	private Task currentTask;
	private Position resetPosition;
	private Pouch selectedPouch;
	
	/*** INTS ***/
	public double DungPrestige = 1.0;
	private int[] brawlerCharges = new int[11];
	private int[] forceMovement = new int[7];
	private int[] leechedBonuses = new int[7];
	private int[] ores = new int[2];
	private int[] constructionCoords;
	public int AFKcycle;;
	private int recoilCharges;
	private int runEnergy = 100;
	private int currentBankTab;
	private int interfaceId, walkableInterfaceId, multiIcon;
	private int dialogueActionId;
	private int overloadPotionTimer, prayerRenewalPotionTimer;
	private int fireImmunity, fireDamageModifier;
	public int amountDonated;
	private int wildernessLevel;
	private int fireAmmo;
	private int specialPercentage = 100;
	private int skullIcon = -1, skullTimer;
	private int teleblockTimer;
	private int dragonFireImmunity;
	private int poisonImmunity;
	private int shadowState;
	private int effigy;
	private int dfsCharges;
	private int playerViewingIndex;
	private int staffOfLightEffect;
	private int minutesBonusExp = -1;
	private int selectedGeSlot = -1;
	private int selectedGeItem = -1;
	private int geQuantity;
	private int gePricePerItem;
	private int selectedSkillingItem;
	private int currentBookPage;
	private int currentTablePage;
	private int storedRuneEssence, storedPureEssence;
	private int trapsLaid;
	private int skillAnimation;
	public int PanelOpen;
	private int houseServant;
	private int houseServantCharges;
	private int servantItemFetch;
	private int portalSelected;
	private int constructionInterface;
	private int buildFurnitureId;
	private int buildFurnitureX;
	private int buildFurnitureY;
	private int combatRingType;
	private int barrowsKilled;
	private int clueProgress;
	public int dart;
	public int dartType;
	public int darts;
	public int correctAnswers;
	
	
	/*** BOOLEANS ***/
	public boolean NORMAL = false;
	public boolean BRONZE = false;
	public boolean SILVER = false;
	public boolean GOLD = false;
	public boolean PLAT = false;
	public boolean DIAM = false;
	
	public boolean Restricted1 = true;
	public boolean Restricted2 = true;
	private boolean unlockedLoyaltyTitles[] = new boolean[12];
	private boolean[] crossedObstacles = new boolean[7];
	private boolean processFarming;
	private boolean crossingObstacle;
	private boolean targeted;
	private boolean isBanking, noteWithdrawal, swapMode;
	private boolean regionChange, allowRegionChangePacket;
	private boolean isDying;
	private boolean isRunning = true, isResting;
	private boolean experienceLocked;
	private boolean clientExitTaskActive;
	private boolean drainingPrayer;
	private boolean shopping;
	private boolean settingUpCannon;
	private boolean hasVengeance;
	private boolean killsTrackerOpen;
	private boolean acceptingAid;
	private boolean autoRetaliate;
	private boolean autocast;
	private boolean specialActivated;
	private boolean isCoughing;
	private boolean playerLocked;
	private boolean recoveringSpecialAttack;
	private boolean soundsActive, musicActive;
	private boolean newPlayer;
	private boolean openBank;
	private boolean inActive;
	private boolean inConstructionDungeon;
	private boolean isBuildingMode;
	private boolean voteMessageSent;
	private boolean receivedStarter;
	private boolean disabledTrivia;
	

	

	public PlayerSession getSession() {
		return session;
	}

	public Inventory getInventory() {
		return inventory;
	}
	
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public Equipment getPreSetEquipment() {
		return preSetEquipment;
	}

	public void setPreSetEquipment(Equipment equipment) {
		this.preSetEquipment = equipment;
	}

	public PriceChecker getPriceChecker() {
		return priceChecker;
	}

	public String getUsername() {
		return username;
	}

	public Player setUsername(String username) {
		this.username = username;
		return this;
	}

	public Long getLongUsername() {
		return longUsername;
	}

	public Player setLongUsername(Long longUsername) {
		this.longUsername = longUsername;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setEmailAddress(String address) {
		this.emailAddress = address;
	}

	public Player setPassword(String password) {
		this.password = password;
		return this;
	}


	public String getHostAddress() {
		return hostAddress;
	}

	public Player setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
		return this;
	}

	public String getSerialNumber() {
		return serial_number;
	}

	public Player setSerialNumber(String serial_number) {
		this.serial_number = serial_number;
		return this;
	}

	public FrameUpdater getFrameUpdater() {
		return this.frameUpdater;
	}

	public PlayerRights getRights() {
		return rights;
	}

	public Player setRights(PlayerRights rights) {
		this.rights = rights;
		return this;
	}

	public ChatMessage getChatMessages() {
		return chatMessages;
	}

	public PacketSender getPacketSender() {
		return packetSender;
	}

	public SkillManager getSkillManager() {
		return skillManager;
	}

	public Appearance getAppearance() {
		return appearance;
	}
	public PlayerRelations getRelations() {
		return relations;
	}

	public PlayerKillingAttributes getPlayerKillingAttributes() {
		return playerKillingAttributes;
	}

	public PointsHandler getPointsHandler() {
		return pointsHandler;
	}

	public boolean isImmuneToDragonFire() {
		return dragonFireImmunity > 0;
	}

	public int getDragonFireImmunity() {
		return dragonFireImmunity;
	}

	public void setDragonFireImmunity(int dragonFireImmunity) {
		this.dragonFireImmunity = dragonFireImmunity;
	}

	public void incrementDragonFireImmunity(int amount) {
		dragonFireImmunity += amount;
	}

	public void decrementDragonFireImmunity(int amount) {
		dragonFireImmunity -= amount;
	}

	public int getPoisonImmunity() {
		return poisonImmunity;
	}

	public void setPoisonImmunity(int poisonImmunity) {
		this.poisonImmunity = poisonImmunity;
	}

	public void incrementPoisonImmunity(int amount) {
		poisonImmunity += amount;
	}

	public void decrementPoisonImmunity(int amount) {
		poisonImmunity -= amount;
	}

	public boolean isAutoRetaliate() {
		return autoRetaliate;
	}

	public void setAutoRetaliate(boolean autoRetaliate) {
		this.autoRetaliate = autoRetaliate;
	}

	public CombatSpell getCastSpell() {
		return castSpell;
	}

	public void setCastSpell(CombatSpell castSpell) {
		this.castSpell = castSpell;
	}

	public CombatSpell getPreviousCastSpell() {
		return previousCastSpell;
	}

	public void setPreviousCastSpell(CombatSpell previousCastSpell) {
		this.previousCastSpell = previousCastSpell;
	}

	/**
	 * @return the autocast
	 */
	public boolean isAutocast() {
		return autocast;
	}

	/**
	 * @param autocast
	 *            the autocast to set
	 */
	public void setAutocast(boolean autocast) {
		this.autocast = autocast;
	}

	/**
	 * @return the skullTimer
	 */
	public int getSkullTimer() {
		return skullTimer;
	}

	/**
	 * @param skullTimer
	 *            the skullTimer to set
	 */
	public void setSkullTimer(int skullTimer) {
		this.skullTimer = skullTimer;
	}

	public void decrementSkullTimer() {
		skullTimer -= 50;
	}

	/**
	 * @return the skullIcon
	 */
	public int getSkullIcon() {
		return skullIcon;
	}

	/**
	 * @param skullIcon
	 *            the skullIcon to set
	 */
	public void setSkullIcon(int skullIcon) {
		this.skullIcon = skullIcon;
	}

	/**
	 * @return the teleblockTimer
	 */
	public int getTeleblockTimer() {
		return teleblockTimer;
	}

	/**
	 * @param teleblockTimer
	 *            the teleblockTimer to set
	 */
	public void setTeleblockTimer(int teleblockTimer) {
		this.teleblockTimer = teleblockTimer;
	}

	public void decrementTeleblockTimer() {
		teleblockTimer--;
	}

	/**
	 * @return the autocastSpell
	 */
	public CombatSpell getAutocastSpell() {
		return autocastSpell;
	}

	/**
	 * @param autocastSpell
	 *            the autocastSpell to set
	 */
	public void setAutocastSpell(CombatSpell autocastSpell) {
		this.autocastSpell = autocastSpell;
	}

	/**
	 * @return the specialPercentage
	 */
	public int getSpecialPercentage() {
		return specialPercentage;
	}

	/**
	 * @param specialPercentage
	 *            the specialPercentage to set
	 */
	public void setSpecialPercentage(int specialPercentage) {
		this.specialPercentage = specialPercentage;
	}

	/**
	 * @return the fireAmmo
	 */
	public int getFireAmmo() {
		return fireAmmo;
	}

	/**
	 * @param fireAmmo
	 *            the fireAmmo to set
	 */
	public void setFireAmmo(int fireAmmo) {
		this.fireAmmo = fireAmmo;
	}

	public int getWildernessLevel() {
		return wildernessLevel;
	}

	public void setWildernessLevel(int wildernessLevel) {
		this.wildernessLevel = wildernessLevel;
	}

	/**
	 * @return the combatSpecial
	 */
	public CombatSpecial getCombatSpecial() {
		return combatSpecial;
	}

	/**
	 * @param combatSpecial
	 *            the combatSpecial to set
	 */
	public void setCombatSpecial(CombatSpecial combatSpecial) {
		this.combatSpecial = combatSpecial;
	}

	/**
	 * @return the specialActivated
	 */
	public boolean isSpecialActivated() {
		return specialActivated;
	}

	/**
	 * @param specialActivated
	 *            the specialActivated to set
	 */
	public void setSpecialActivated(boolean specialActivated) {
		this.specialActivated = specialActivated;
	}

	public void decrementSpecialPercentage(int drainAmount) {
		this.specialPercentage -= drainAmount;

		if (specialPercentage < 0) {
			specialPercentage = 0;
		}
	}

	public void incrementSpecialPercentage(int gainAmount) {
		this.specialPercentage += gainAmount;

		if (specialPercentage > 100) {
			specialPercentage = 100;
		}
	}

	/**
	 * @return the rangedAmmo
	 */
	public RangedWeaponData getRangedWeaponData() {
		return rangedWeaponData;
	}

	/**
	 * @param rangedAmmo
	 *            the rangedAmmo to set
	 */
	public void setRangedWeaponData(RangedWeaponData rangedWeaponData) {
		this.rangedWeaponData = rangedWeaponData;
	}

	/**
	 * @return the weapon.
	 */
	public WeaponInterface getWeapon() {
		return weapon;
	}

	/**
	 * @param weapon
	 *            the weapon to set.
	 */
	public void setWeapon(WeaponInterface weapon) {
		this.weapon = weapon;
	}

	/**
	 * @return the fightType
	 */
	public FightType getFightType() {
		return fightType;
	}

	/**
	 * @param fightType
	 *            the fightType to set
	 */
	public void setFightType(FightType fightType) {
		this.fightType = fightType;
	}

	public Bank[] getBanks() {
		return bankTabs;
	}

	public Bank getBank(int index) {
		return bankTabs[index];
	}

	public Player setBank(int index, Bank bank) {
		this.bankTabs[index] = bank;
		return this;
	}

	public boolean isAcceptAid() {
		return acceptingAid;
	}
	public void setAcceptAid(boolean acceptingAid) {
		this.acceptingAid = acceptingAid;
	}

	public Trading getTrading() {
		return trading;
	}

	public Dueling getDueling() {
		return dueling;
	}

	public CopyOnWriteArrayList<KillsEntry> getKillsTracker() {
		return killsTracker;
	}

	public CopyOnWriteArrayList<DropLogEntry> getDropLog() {
		return dropLog;
	}

	public void setWalkToTask(WalkToTask walkToTask) {
		this.walkToTask = walkToTask;
	}

	public WalkToTask getWalkToTask() {
		return walkToTask;
	}

	public Player setSpellbook(MagicSpellbook spellbook) {
		this.spellbook = spellbook;
		return this;
	}


	public MagicSpellbook getSpellbook() {
		return spellbook;
	}

	public Player setPrayerbook(Prayerbook prayerbook) {
		this.prayerbook = prayerbook;
		return this;
	}

	public Prayerbook getPrayerbook() {
		return prayerbook;
	}

	/**
	 * The player's local players list.
	 */
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}

	/**
	 * The player's local npcs list getter
	 */
	public List<NPC> getLocalNpcs() {
		return localNpcs;
	}

	public CopyOnWriteArrayList<NPC> getNpcFacesUpdated() {
		return npc_faces_updated;
	}

	public Player setInterfaceId(int interfaceId) {
		this.interfaceId = interfaceId;
		return this;
	}

	public int getInterfaceId() {
		return this.interfaceId;
	}

	public boolean isDying() {
		return isDying;
	}

	public void setDying(boolean isDying) {
		this.isDying = isDying;
	}

	public int[] getForceMovement() {
		return forceMovement;
	}

	public Player setForceMovement(int[] forceMovement) {
		this.forceMovement = forceMovement;
		return this;
	}

	/**
	 * @return the equipmentAnimation
	 */
	public CharacterAnimations getCharacterAnimations() {
		return characterAnimations;
	}

	/**
	 * @return the equipmentAnimation
	 */
	public void setCharacterAnimations(CharacterAnimations equipmentAnimation) {
		this.characterAnimations = equipmentAnimation.clone();
	}

	public LoyaltyTitles getLoyaltyTitle() {
		return loyaltyTitle;
	}

	public void setLoyaltyTitle(LoyaltyTitles loyaltyTitle) {
		this.loyaltyTitle = loyaltyTitle;
	}

	public void setWalkableInterfaceId(int interfaceId2) {
		this.walkableInterfaceId = interfaceId2;		
	}

	public PlayerInteractingOption getPlayerInteractingOption() {
		return playerInteractingOption;
	}

	public Player setPlayerInteractingOption(PlayerInteractingOption playerInteractingOption) {
		this.playerInteractingOption = playerInteractingOption;
		return this;
	}

	public int getMultiIcon() {
		return multiIcon;
	}

	public Player setMultiIcon(int multiIcon) {
		this.multiIcon = multiIcon;
		return this;
	}

	public int getWalkableInterfaceId() {
		return walkableInterfaceId;
	}

	public boolean soundsActive() {
		return soundsActive;
	}

	public void setSoundsActive(boolean soundsActive) {
		this.soundsActive = soundsActive;
	}

	public boolean musicActive() {
		return musicActive;
	}

	public void setMusicActive(boolean musicActive) {
		this.musicActive = musicActive;
	}

	public BonusManager getBonusManager() {
		return bonusManager;
	}

	public int getRunEnergy() {
		return runEnergy;
	}

	public Player setRunEnergy(int runEnergy) {
		this.runEnergy = runEnergy;
		return this;
	}

	public Stopwatch getLastRunRecovery() {
		return lastRunRecovery;
	}

	public Player setRunning(boolean isRunning) {
		this.isRunning = isRunning;
		return this;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public Player setResting(boolean isResting) {
		this.isResting = isResting;
		return this;
	}

	public boolean isResting() {
		return isResting;
	}

	public void setMoneyInPouch(long moneyInPouch) {
		this.moneyInPouch = moneyInPouch;
	}

	public long getMoneyInPouch() {
		return moneyInPouch;
	}

	public int getMoneyInPouchAsInt() {
		return moneyInPouch > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)moneyInPouch;
	}

	public boolean experienceLocked() {
		return experienceLocked;
	}

	public void setExperienceLocked(boolean experienceLocked) {
		this.experienceLocked = experienceLocked;
	}

	public void setClientExitTaskActive(boolean clientExitTaskActive) {
		this.clientExitTaskActive = clientExitTaskActive;
	}

	public boolean isClientExitTaskActive() {
		return clientExitTaskActive;
	}

	public Player setCurrentClanChat(ClanChat clanChat) {
		this.currentClanChat = clanChat;
		return this;
	}

	public ClanChat getCurrentClanChat() {
		return currentClanChat;
	}

	public String getClanChatName() {
		return clanChatName;
	}

	public Player setClanChatName(String clanChatName) {
		this.clanChatName = clanChatName;
		return this;
	}

	public void setInputHandling(Input inputHandling) {
		this.inputHandling = inputHandling;
	}

	public Input getInputHandling() {
		return inputHandling;
	}

	public boolean isDrainingPrayer() {
		return drainingPrayer;
	}

	public void setDrainingPrayer(boolean drainingPrayer) {
		this.drainingPrayer = drainingPrayer;
	}

	public Stopwatch getClickDelay() {
		return clickDelay;
	}

	public int[] getLeechedBonuses() {
		return leechedBonuses;
	}

	public Stopwatch getLastItemPickup() {
		return lastItemPickup;
	}

	public Stopwatch getLastSummon() {
		return lastSummon;
	}

	public BankSearchAttributes getBankSearchingAttribtues() {
		return bankSearchAttributes;
	}

	public AchievementAttributes getAchievementAttributes() {
		return achievementAttributes;
	}

	public BankPinAttributes getBankPinAttributes() {
		return bankPinAttributes;
	}

	public int getCurrentBankTab() {
		return currentBankTab;
	}

	public Player setCurrentBankTab(int tab) {
		this.currentBankTab = tab;
		return this;
	}

	public boolean isBanking() {
		return isBanking;
	}

	public Player setBanking(boolean isBanking) {
		this.isBanking = isBanking;
		return this;
	}

	public void setNoteWithdrawal(boolean noteWithdrawal) {
		this.noteWithdrawal = noteWithdrawal;
	}

	public boolean withdrawAsNote() {
		return noteWithdrawal;
	}

	public void setSwapMode(boolean swapMode) {
		this.swapMode = swapMode;
	}

	public boolean swapMode() {
		return swapMode;
	}
	public boolean isShopping() {
		return shopping;
	}

	public void setShopping(boolean shopping) {
		this.shopping = shopping;
	}

	public Shop getShop() {
		return shop;
	}

	public Player setShop(Shop shop) {
		this.shop = shop;
		return this;
	}

	public GameObject getInteractingObject() {
		return interactingObject;
	}

	public Player setInteractingObject(GameObject interactingObject) {
		this.interactingObject = interactingObject;
		return this;
	}

	public Item getInteractingItem() {
		return interactingItem;
	}

	public void setInteractingItem(Item interactingItem) {
		this.interactingItem = interactingItem;
	}

	public Dialogue getDialogue() {
		return this.dialogue;
	}

	public void setDialogue(Dialogue dialogue) {
		this.dialogue = dialogue;
	}

	public int getDialogueActionId() {
		return dialogueActionId;
	}

	public void setDialogueActionId(int dialogueActionId) {
		this.dialogueActionId = dialogueActionId;
	}

	public void setSettingUpCannon(boolean settingUpCannon) {
		this.settingUpCannon = settingUpCannon;
	}

	public boolean isSettingUpCannon() {
		return settingUpCannon;
	}

	public Player setCannon(DwarfCannon cannon) {
		this.cannon = cannon;
		return this;
	}

	public DwarfCannon getCannon() {
		return cannon;
	}

	public int getOverloadPotionTimer() {
		return overloadPotionTimer;
	}

	public void setOverloadPotionTimer(int overloadPotionTimer) {
		this.overloadPotionTimer = overloadPotionTimer;
	}

	public int getPrayerRenewalPotionTimer() {
		return prayerRenewalPotionTimer;
	}

	public void setPrayerRenewalPotionTimer(int prayerRenewalPotionTimer) {
		this.prayerRenewalPotionTimer = prayerRenewalPotionTimer;
	}

	public Stopwatch getSpecialRestoreTimer() {
		return specialRestoreTimer;
	}

	public boolean[] getUnlockedLoyaltyTitles() {
		return unlockedLoyaltyTitles;
	}

	public void setUnlockedLoyaltyTitles(boolean[] unlockedLoyaltyTitles) {
		this.unlockedLoyaltyTitles = unlockedLoyaltyTitles;
	}

	public void setUnlockedLoyaltyTitle(int index) {
		unlockedLoyaltyTitles[index] = true;
	}

	public Stopwatch getEmoteDelay() {
		return emoteDelay;
	}

	public MinigameAttributes getMinigameAttributes() {
		return minigameAttributes;
	}

	public int getFireImmunity() {
		return fireImmunity;
	}

	public Player setFireImmunity(int fireImmunity) {
		this.fireImmunity = fireImmunity;
		return this;
	}

	public int getFireDamageModifier() {
		return fireDamageModifier;
	}

	public Player setFireDamageModifier(int fireDamageModifier) {
		this.fireDamageModifier = fireDamageModifier;
		return this;
	}

	public boolean hasVengeance() {
		return hasVengeance;
	}

	public void setHasVengeance(boolean hasVengeance) {
		this.hasVengeance = hasVengeance;
	}

	public Stopwatch getLastVengeance() {
		return lastVengeance;
	}

	public Stopwatch getTolerance() {
		return tolerance;
	}

	public boolean isTargeted() {
		return targeted;
	}

	public void setTargeted(boolean targeted) {
		this.targeted = targeted;
	}

	public Stopwatch getLastYell() {
		return lastYell;
	}

	public int getAmountDonated() {
		return amountDonated;
	}

	public void incrementAmountDonated(int amountDonated) {
		this.amountDonated += amountDonated;
	}

	public long getTotalPlayTime() {
		return totalPlayTime;
	}

	public void setTotalPlayTime(long amount) {
		this.totalPlayTime = amount;
	}

	public Stopwatch getRecordedLogin() {
		return recordedLogin;
	}

	public Player setRegionChange(boolean regionChange) {
		this.regionChange = regionChange;
		return this;
	}

	public boolean isChangingRegion() {
		return this.regionChange;
	}

	public void setAllowRegionChangePacket(boolean allowRegionChangePacket) {
		this.allowRegionChangePacket = allowRegionChangePacket;
	}

	public boolean isAllowRegionChangePacket() {
		return allowRegionChangePacket;
	}

	public boolean isKillsTrackerOpen() {
		return killsTrackerOpen;
	}

	public void setKillsTrackerOpen(boolean killsTrackerOpen) {
		this.killsTrackerOpen = killsTrackerOpen;
	}

	public boolean isCoughing() {
		return isCoughing;
	}

	public void setCoughing(boolean isCoughing) {
		this.isCoughing = isCoughing;
	}

	public int getShadowState() {
		return shadowState;
	}

	public void setShadowState(int shadow) {
		this.shadowState = shadow;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public boolean isPlayerLocked() {
		return playerLocked;
	}

	public Player setPlayerLocked(boolean playerLocked) {
		this.playerLocked = playerLocked;
		return this;
	}

	public Stopwatch getSqlTimer() {
		return sqlTimer;
	}

	public Stopwatch getFoodTimer() {
		return foodTimer;
	}

	public Stopwatch getPotionTimer() {
		return potionTimer;
	}

	public Item getUntradeableDropItem() {
		return untradeableDropItem;
	}

	public void setUntradeableDropItem(Item untradeableDropItem) {
		this.untradeableDropItem = untradeableDropItem;
	}

	public boolean isRecoveringSpecialAttack() {
		return recoveringSpecialAttack;
	}

	public void setRecoveringSpecialAttack(boolean recoveringSpecialAttack) {
		this.recoveringSpecialAttack = recoveringSpecialAttack;
	}

	public CombatType getLastCombatType() {
		return lastCombatType;
	}

	public void setLastCombatType(CombatType lastCombatType) {
		this.lastCombatType = lastCombatType;
	}

	public int getEffigy() {
		return this.effigy;
	}

	public void setEffigy(int effigy) {
		this.effigy = effigy;
	}

	public int getDfsCharges() {
		return dfsCharges;
	}

	public void incrementDfsCharges(int amount) {
		this.dfsCharges += amount;
	}

	public void setNewPlayer(boolean newPlayer) {
		this.newPlayer = newPlayer;
	}

	public boolean newPlayer() {
		return newPlayer;
	}

	public Stopwatch getLogoutTimer() {
		return lougoutTimer;
	}

	public Player setUsableObject(Object[] usableObject) {
		this.usableObject = usableObject;
		return this;
	}

	public Player setUsableObject(int index, Object usableObject) {
		this.usableObject[index] = usableObject;
		return this;
	}

	public Object[] getUsableObject() {
		return usableObject;
	}

	public int getPlayerViewingIndex() {
		return playerViewingIndex;
	}

	public void setPlayerViewingIndex(int playerViewingIndex) {
		this.playerViewingIndex = playerViewingIndex;
	}

	public boolean hasStaffOfLightEffect() {
		return staffOfLightEffect > 0;
	}

	public int getStaffOfLightEffect() {
		return staffOfLightEffect;
	}

	public void setStaffOfLightEffect(int staffOfLightEffect) {
		this.staffOfLightEffect = staffOfLightEffect;
	}

	public void decrementStaffOfLightEffect() {
		this.staffOfLightEffect--;
	}

	public boolean openBank() {
		return openBank;
	}

	public void setOpenBank(boolean openBank) {
		this.openBank = openBank;
	}

	public int getMinutesBonusExp() {
		return minutesBonusExp;
	}

	public void setMinutesBonusExp(int minutesBonusExp, boolean add) {
		this.minutesBonusExp = (add ? this.minutesBonusExp + minutesBonusExp : minutesBonusExp);
	}

	public void setInactive(boolean inActive) {
		this.inActive = inActive;
	}

	public boolean isInActive() {
		return inActive;
	}

	public int getSelectedGeItem() {
		return selectedGeItem;
	}

	public void setSelectedGeItem(int selectedGeItem) {
		this.selectedGeItem = selectedGeItem;
	}

	public int getGeQuantity() {
		return geQuantity;
	}

	public void setGeQuantity(int geQuantity) {
		this.geQuantity = geQuantity;
	}

	public int getGePricePerItem() {
		return gePricePerItem;
	}

	public void setGePricePerItem(int gePricePerItem) {
		this.gePricePerItem = gePricePerItem;
	}

	public GrandExchangeSlot[] getGrandExchangeSlots() {
		return grandExchangeSlots;
	}

	public void setGrandExchangeSlots(GrandExchangeSlot[] GrandExchangeSlots) {
		this.grandExchangeSlots = GrandExchangeSlots;
	}

	public void setGrandExchangeSlot(int index, GrandExchangeSlot state) {
		this.grandExchangeSlots[index] = state;
	}

	public void setSelectedGeSlot(int slot) {
		this.selectedGeSlot = slot;
	}

	public int getSelectedGeSlot() {
		return selectedGeSlot;
	}

	public Task getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(Task currentTask) {
		this.currentTask = currentTask;
	}

	public int getSelectedSkillingItem() {
		return selectedSkillingItem;
	}

	public void setSelectedSkillingItem(int selectedItem) {
		this.selectedSkillingItem = selectedItem;
	}

	public boolean shouldProcessFarming() {
		return processFarming;
	}

	public void setProcessFarming(boolean processFarming) {
		this.processFarming = processFarming;
	}

	public Pouch getSelectedPouch() {
		return selectedPouch;
	}

	public void setSelectedPouch(Pouch selectedPouch) {
		this.selectedPouch = selectedPouch;
	}
	
	public int getCurrentTablePage() {
		return currentTablePage;
	}
	
	public void setCurrentTablePage(int currentTablePage) {
		this.currentTablePage = currentTablePage;
	}

	public int getCurrentBookPage() {
		return currentBookPage;
	}

	public void setCurrentBookPage(int currentBookPage) {
		this.currentBookPage = currentBookPage;
	}


	public int getStoredRuneEssence() {
		return storedRuneEssence;
	}

	public void setStoredRuneEssence(int storedRuneEssence) {
		this.storedRuneEssence = storedRuneEssence;
	}

	public int getStoredPureEssence() {
		return storedPureEssence;
	}

	public void setStoredPureEssence(int storedPureEssence) {
		this.storedPureEssence = storedPureEssence;
	}

	public int getTrapsLaid() {
		return trapsLaid;
	}

	public void setTrapsLaid(int trapsLaid) {
		this.trapsLaid = trapsLaid;
	}

	public boolean isCrossingObstacle() {
		return crossingObstacle;
	}

	public Player setCrossingObstacle(boolean crossingObstacle) {
		this.crossingObstacle = crossingObstacle;
		return this;
	}

	public boolean[] getCrossedObstacles() {
		return crossedObstacles;
	}

	public boolean getCrossedObstacle(int i) {
		return crossedObstacles[i];
	}

	public Player setCrossedObstacle(int i, boolean completed) {
		crossedObstacles[i] = completed;
		return this;
	}

	public void setCrossedObstacles(boolean[] crossedObstacles) {
		this.crossedObstacles = crossedObstacles;
	}

	public int getSkillAnimation() {
		return skillAnimation;
	}

	public Player setSkillAnimation(int animation) {
		this.skillAnimation = animation;
		return this;
	}

	public int[] getOres() {
		return ores;
	}

	public void setOres(int[] ores) {
		this.ores = ores;
	}

	public void setResetPosition(Position resetPosition) {
		this.resetPosition = resetPosition;
	}

	public Position getResetPosition() {
		return resetPosition;
	}

	public Slayer getSlayer() {
		return slayer;
	}

	public Summoning getSummoning() {
		return summoning;
	}

	public Farming getFarming() {
		return farming;
	}

	public boolean inConstructionDungeon() {
		return inConstructionDungeon;
	}

	public void setInConstructionDungeon(boolean inConstructionDungeon) {
		this.inConstructionDungeon = inConstructionDungeon;
	}

	public int getHouseServant() {
		return houseServant;
	}

	public void setHouseServant(int houseServant) {
		this.houseServant = houseServant;
	}

	public int getHouseServantCharges() {
		return this.houseServantCharges;
	}

	public void setHouseServantCharges(int houseServantCharges) {
		this.houseServantCharges = houseServantCharges;
	}

	public void incrementHouseServantCharges() {
		this.houseServantCharges++;
	}

	public int getServantItemFetch() {
		return servantItemFetch;
	}

	public void setServantItemFetch(int servantItemFetch) {
		this.servantItemFetch = servantItemFetch;
	}

	public int getPortalSelected() {
		return portalSelected;
	}

	public void setPortalSelected(int portalSelected) {
		this.portalSelected = portalSelected;
	}

	public boolean isBuildingMode() {
		return this.isBuildingMode;
	}

	public void setIsBuildingMode(boolean isBuildingMode) {
		this.isBuildingMode = isBuildingMode;
	}

	public int[] getConstructionCoords() {
		return constructionCoords;
	}

	public void setConstructionCoords(int[] constructionCoords) {
		this.constructionCoords = constructionCoords;
	}

	public int getBuildFurnitureId() {
		return this.buildFurnitureId;
	}

	public void setBuildFuritureId(int buildFuritureId) {
		this.buildFurnitureId = buildFuritureId;
	}

	public int getBuildFurnitureX() {
		return this.buildFurnitureX;
	}

	public void setBuildFurnitureX(int buildFurnitureX) {
		this.buildFurnitureX = buildFurnitureX;
	}

	public int getBuildFurnitureY() {
		return this.buildFurnitureY;
	}

	public void setBuildFurnitureY(int buildFurnitureY) {
		this.buildFurnitureY = buildFurnitureY;
	}

	public int getCombatRingType() {
		return this.combatRingType;
	}

	public void setCombatRingType(int combatRingType) {
		this.combatRingType = combatRingType;
	}

	public int getConstructionInterface() {
		return this.constructionInterface;
	}

	public void setConstructionInterface(int constructionInterface) {
		this.constructionInterface = constructionInterface;
	}

	public int[] getBrawlerChargers() {
		return this.brawlerCharges;
	}

	public void setBrawlerCharges(int[] brawlerCharges) {
		this.brawlerCharges = brawlerCharges;
	}

	public int getRecoilCharges() {
		return this.recoilCharges;
	}

	public int setRecoilCharges(int recoilCharges) {
		return this.recoilCharges = recoilCharges;
	}

	public boolean voteMessageSent() {
		return this.voteMessageSent;
	}

	public void setVoteMessageSent(boolean voteMessageSent) {
		this.voteMessageSent = voteMessageSent;
	}

	public boolean didReceiveStarter() {
		return receivedStarter;
	}

	public void setReceivedStarter(boolean receivedStarter) {
		this.receivedStarter = receivedStarter;
	}

	public int getBarrowsKilled() {
		return barrowsKilled;
	}

	public int getClueProgress() {
		return clueProgress;
	}

	public void setBarrowsKilled(int barrowsKilled) {
		this.barrowsKilled = barrowsKilled;
	}

	public void setClueProgress(int clueProgress) {
		this.clueProgress = clueProgress;
	}

	private boolean chargingAttack;
	public boolean isChargingAttack() {
		return chargingAttack;
	}

	private String yellTitle;
	
	public void setYellTitle(String yellTitle) {
		this.yellTitle = yellTitle;
	}
	public String getYellTitle() {
		return yellTitle;
	}
	public int dart() {
		return dart;
	}
	public int dartType() {
		return dartType;
	}
	public int darts() {
		return darts;
	}
	public long getTimer() {
		return timer;
	}
	public void setTimer(long time) {
		timer = time;
	}
	public long getLastCorrectTrivia() {
		return lastAnswered;
	}
	public void setLastAnswer(long time) {
		this.lastAnswered = time;
	}
	public int getCorrectAnswers() {
		return correctAnswers;
	}
	public void setCorrectAnswers(int amount) {
		this.correctAnswers = amount;
	}
	public void setDisableTrivia(boolean value) {
		this.disabledTrivia = value;
	}
	public boolean hasDisabledTrivia() {
		return disabledTrivia;
	}
	
}
