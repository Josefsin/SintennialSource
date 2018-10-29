package com.agaroth.world.content.combat.strategy.impl;


import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Animation;
import com.agaroth.model.Graphic;
import com.agaroth.model.GraphicHeight;
import com.agaroth.model.Position;
import com.agaroth.model.Projectile;
import com.agaroth.util.Misc;
import com.agaroth.util.Stopwatch;
import com.agaroth.world.World;
import com.agaroth.world.content.combat.CombatContainer;
import com.agaroth.world.content.combat.CombatType;
import com.agaroth.world.content.combat.HitQueue.CombatHit;
import com.agaroth.world.content.combat.strategy.CombatStrategy;
import com.agaroth.world.entity.impl.Character;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.npc.NPCMovementCoordinator.Coordinator;
import com.agaroth.world.entity.impl.player.Player;

public class Zulrah implements CombatStrategy {

	public static NPC ZULRAH;
	private static int zulrahId, zulrahHp;
	private static boolean firstForm, isDiving, venom;
	
	private static Stopwatch venomTime = new Stopwatch();
	
	private static int[] randomXCoords =
		{2268, 2259, 2277, 2274, 2270};
	private static int[] randomYCoords =
		{3074, 3070, 3074, 3067, 3065};
	private static int randomCoord;
	private static Position zulrahPosition, venomPosition, snakelingPosition;
	
	private static NPC TILE, TILE2, SNAKELING;
	
	private Animation shoot = new Animation(5069);
	private Animation charge = new Animation(5806);
	private Animation melee_attack = new Animation(5807);
	private static Animation dive = new Animation(5072);
	private static Animation rise = new Animation(5073);
	private Graphic fire = new Graphic(78);
	private Graphic snakeling_summon = new Graphic(281);

	public static void spawn() {
		zulrahId = 2043;
		zulrahHp = 2000;
		zulrahPosition = new Position(2268, 3073);
		firstForm = true;
		ZULRAH = new NPC(zulrahId, zulrahPosition);
		ZULRAH.getMovementCoordinator().setCoordinator(new Coordinator(true, 3));
		World.register(ZULRAH);
		ZULRAH.performAnimation(rise);
		ZULRAH.setConstitution(zulrahHp);
		System.out.println("FIRST CALL");
	}

	public static void despawn() {
		ZULRAH.performAnimation(dive);
		TaskManager.submit(new Task(1, ZULRAH, false) {
			int tick = 0;
			@Override
			public void execute() {
				if(tick == 3){
					zulrahHp = ZULRAH.getConstitution();
					if(ZULRAH != null && ZULRAH.isRegistered())
					World.deregister(ZULRAH);
				this.stop();
				}
				tick++;
			} 
		});
	}

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return true;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		Player p = (Player)victim;
		if(p.getConstitution() <= 0 || ZULRAH.getConstitution() <= 0) {
			return true;
		}
		if(ZULRAH.isChargingAttack() || p.getConstitution() <= 0) {
			return true;
		}
		if(isDiving){
			return true;
		}
		if(venom){
			if(venomTime.elapsed(10000))
				venom = false;
			//p.getPacketSender().sendVenomGraphic(p, toxic_cloud, venomPosition, 70);
			if(p.getPosition() == venomPosition)
				venomAttack(p);
			return true;
		}
			int rnd = Misc.getRandom(25);
			System.out.println(rnd);
			if(rnd <= 5 && firstForm && zulrahId != 2044)
			{
				ZULRAH.performAnimation(shoot);
				ZULRAH.setChargingAttack(true);
				TaskManager.submit(new Task(1, ZULRAH, true) {
					int ticks;
					boolean two = false;
					Position twoPos;
					@Override
					public void execute() {
		
						if(ticks == 0){
							venomPosition = p.getPosition();
							TILE = new NPC(1, venomPosition);
							World.register(TILE);
							if(Misc.getRandom(1) == 0){
								twoPos = venomPosition.add(Misc.getRandom(2), Misc.getRandom(1));
								TILE2 = new NPC(1, twoPos);
								World.register(TILE2);
								two = true;
							}
						}
						if(ticks == 1){
							new Projectile(ZULRAH, TILE, 500, 44, 1, 43, 43, 0).sendProjectile();
							TILE.appendDeath();	
						}
						if(ticks == 2){
							venom = true;
							//p.getPacketSender().sendVenomGraphic(	);
							ZULRAH.setChargingAttack(false);
							if(two){
								new Projectile(ZULRAH, TILE2, 500, 44, 1, 43, 43, 0).sendProjectile();
								TaskManager.submit(new Task(2, ZULRAH, false) {
									@Override
									public void execute() {
										//p.getPacketSender().sendVenomGraphic(p, toxic_cloud, twoPos, 70);
										TILE2.appendDeath();
										stop();
									}
								});
							}	
							stop();
						}
						ticks++;
					} 
				});				
				return true;
			}
			if((rnd > 5 && rnd < 10) && zulrahId == 2043)
			{
				new CombatHit(ZULRAH.getCombatBuilder(), new CombatContainer(ZULRAH, p, 1, 4, CombatType.RANGED, true)).handleAttack();
				ZULRAH.performAnimation(shoot);	
				ZULRAH.setChargingAttack(true);
					TaskManager.submit(new Task(1, ZULRAH, true) {
						int tick;
						@Override
						public void execute() {
							if(tick == 1){
								new Projectile(ZULRAH, victim, 1120, 44, 3, 43, 43, 0).sendProjectile();
								new Projectile(ZULRAH, victim, 1101, 44, 3, 43, 43, 0).sendProjectile();
							}
							if(tick == 2){
								p.getPacketSender().sendGraphic(new Graphic(1103, GraphicHeight.MIDDLE), p.getPosition());
								ZULRAH.setChargingAttack(false);
								stop();
							}
							tick++;
						} 
					});
				return true;
			}
			if(rnd >= 10 && rnd < 19 && zulrahId != 2044){
				new CombatHit(ZULRAH.getCombatBuilder(), new CombatContainer(ZULRAH, p, 1, 4, CombatType.MAGIC, true)).handleAttack();
				ZULRAH.performAnimation(shoot);	
				ZULRAH.setChargingAttack(true);
					TaskManager.submit(new Task(1, ZULRAH, true) {
						int tick;
						@Override
						public void execute() {
							if(tick == 1){
								new Projectile(ZULRAH, victim, 2148, 44, 3, 43, 43, 0).sendProjectile();
							}
							if(tick == 2){
								p.getPacketSender().sendGraphic(fire, p.getPosition());
								ZULRAH.setChargingAttack(false);
								stop();
							}
							tick++;
						} 
					});
				return true;
			}
			if(rnd >= 5 && rnd < 19 && zulrahId == 2044){
				ZULRAH.performAnimation(charge);	
				ZULRAH.setChargingAttack(true);
					TaskManager.submit(new Task(4, ZULRAH, false) {
						@Override
						public void execute() {
								new CombatHit(ZULRAH.getCombatBuilder(), new CombatContainer(ZULRAH, p, 1, 4, CombatType.MELEE, true)).handleAttack();
								ZULRAH.performAnimation(melee_attack);	
								ZULRAH.setChargingAttack(false);
								stop();
						} 
					});
				return true;
			}
			if(rnd > 19 && rnd < 23){
				if(zulrahId == 2043){
					snakelingPosition = new Position(p.getPosition().getX()+Misc.getRandom(2), p.getPosition().getY());
					TILE = new NPC(1, snakelingPosition);
					World.register(TILE);
					TILE.appendDeath();
					//System.out.println(tile.getPosition());
					ZULRAH.setChargingAttack(true);
					ZULRAH.performAnimation(new Animation(5069));
					new CombatHit(ZULRAH.getCombatBuilder(), new CombatContainer(ZULRAH, p, 1, 4, CombatType.MAGIC, true)).handleAttack();
						TaskManager.submit(new Task(1, ZULRAH, true) {
						boolean hasAttacked = false;
						int tick = 0;
						
						@Override
						public void execute() {
							
							if(tick == 1){
								if(!hasAttacked){
									new Projectile(ZULRAH, TILE, 280, 44, 3, 43, 43, 0).sendProjectile();
									hasAttacked = true;
								}
							}
							if(tick == 2){
								ZULRAH.setChargingAttack(false);
								p.getPacketSender().sendGraphic(snakeling_summon, snakelingPosition);
								SNAKELING = new NPC(2127, snakelingPosition);
								World.register(SNAKELING);
								SNAKELING.setConstitution(10);
								SNAKELING.getAggressionDistance();
								SNAKELING.performAnimation(rise);
								SNAKELING.getMovementCoordinator().setCoordinator(new Coordinator(false, -1));
								this.stop();
							}
							tick++;
						} 
					});
				} 
				return true;
			}
			else {
				isDiving = true;
				ZULRAH.performAnimation(dive);
				TaskManager.submit(new Task(2, ZULRAH, false) {
					@Override
					public void execute() {
						if(ZULRAH != null && ZULRAH.isRegistered())
							World.deregister(ZULRAH);
						TaskManager.submit(new Task(4, ZULRAH, false) {
							@Override
							public void execute() {
									zulrahHp = ZULRAH.getConstitution();
									randomCoord = Misc.getRandom(4); 
									zulrahPosition = new Position(randomXCoords[randomCoord], randomYCoords[randomCoord]);
									zulrahId = 2042 + (Misc.getRandom(2));
									System.out.println(zulrahId);
									isDiving = false;
									ZULRAH = new NPC(zulrahId, zulrahPosition);
									ZULRAH.getMovementCoordinator().setCoordinator(new Coordinator(true, 3));
									World.register(ZULRAH);
									ZULRAH.performAnimation(new Animation(5073));
									ZULRAH.setConstitution(zulrahHp);
									stop();						
							}
						});
					}
				});
				
			}
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 8;
	}


	/** MISC **/

	public static void dealtDamage(final Player p, final int damage) {
		
	}

	public static void takeDamage(Player from, int damage) {
	
	}

	public static void handleDeath() {
		despawn();
	}
	
	public static void venomAttack(final Player p) {
		TaskManager.submit(new Task(1, p , false) {
			int ticks = 0;
			@Override
			public void execute() {
				if (ticks >= 5 || p.getConstitution() <= 0) {
					this.stop();
					return;
				}
				p.forceChat("Cough..");
				//p.touchedVenom();
				//CombatFactory.poisonEntity(p, PoisonType.VENOM);
				ticks++;
			}
			@Override
			public void stop() {
				setEventRunning(false);
			}
		});
	}
	
	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}