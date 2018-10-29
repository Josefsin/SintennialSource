package com.agaroth.world.content;

import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.engine.task.impl.CeilingCollapseTask;
import com.agaroth.model.Position;
import com.agaroth.model.RegionInstance;
import com.agaroth.world.World;
import com.agaroth.world.content.transportation.TeleportHandler;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;

public class Kraken {

	private static enum WhirpoolData {

		SMALL_POOL_1(2895, new Position(3679, 9884)),
		SMALL_POOL_2(2900, new Position(3676, 9884)),
		SMALL_POOL_3(2902, new Position(3676, 9891)),
		SMALL_POOL_4(2903, new Position(3679, 9891)),
		BIG_POOL(2891, new Position(3677, 9887));

		WhirpoolData(int npc, Position spawn) {
			this.npc = npc;
			this.spawn = spawn;
		}

		int npc;
		Position spawn;

		static WhirpoolData getPool(int npc) {
			for(WhirpoolData d : WhirpoolData.values()) {
				if(d.npc == npc) {
					return d;
				}
			}
			return null;
		}
	}

	public static void enter(Player player) {
		KrakenInstance kInstance = new KrakenInstance(player);
		player.getPacketSender().sendInterfaceRemoval().sendMessage("").sendMessage("@dre@You will lose items if you die here! You have been warned!.");
		for(WhirpoolData d : WhirpoolData.values()) {
			NPC whirpool = new NPC(d.npc, new Position(d.spawn.getX(), d.spawn.getY(), player.getPosition().getZ()));
			kInstance.getNpcsList().add(whirpool);
			World.register(whirpool);
		}

		player.setRegionInstance(kInstance);
	}

	public static boolean isWhirpool(NPC n) {
		int npc = n.getId();
		return npc == 2895 || npc == 2900 || npc == 2902 || npc == 2903 || npc == 2891;
	}

	public static void attackPool(Player player, NPC npc) {
		WhirpoolData d = WhirpoolData.getPool(npc.getId());
		if(d != null) {
			if(((KrakenInstance)player.getRegionInstance()).disturbedPool(d.ordinal()))
				return;
			player.getRegionInstance().getNpcsList().remove(npc);
			((KrakenInstance)player.getRegionInstance()).setDisturbedPool(d.ordinal(), true);
			World.deregister(npc);

			final boolean kraken = d == WhirpoolData.BIG_POOL;
			TaskManager.submit(new Task(1, player, false) {
				@Override
				protected void execute() {
					int npcToSpawn = kraken ? 2007 : 3580;
					Position positionToSpawn = kraken ? new Position(3677, 9887, player.getPosition().getZ()) : new Position(d.spawn.getX() + 2, d.spawn.getY() + 1, player.getPosition().getZ());
					NPC spawn = new NPC(npcToSpawn, positionToSpawn);
					player.getRegionInstance().getNpcsList().add(spawn);
					World.register(spawn);
					spawn.getCombatBuilder().attack(player);
					stop();
					
					if(kraken) {
						player.getPacketSender().sendCameraShake(3, 2, 3, 2);
						player.getPacketSender().sendMessage("The cave begins to collapse...");
						TaskManager.submit(new CeilingCollapseTask(player));
					}
				}
			});
		}
	}

	public static class KrakenInstance extends RegionInstance {

		public KrakenInstance(Player p) {
			super(p, RegionInstanceType.KRAKEN);

		}

		private boolean[] disturbedPool = new boolean[5];

		public boolean disturbedPool(int index) {
			return disturbedPool[index];
		}

		public void setDisturbedPool(int index, boolean disturbed) {
			this.disturbedPool[index] = disturbed;
		}
	}
	public static void PayFee(Player p) {
		boolean usePouch = p.getMoneyInPouch() >= 750000;
		if(p.getInventory().getAmount(995) < 750000 && !usePouch) {
			p.getPacketSender().sendInterfaceRemoval().sendMessage("").sendMessage("You do not have enough coins in your pouch or invetory to pay the fee.");
			return;
		}
		if(usePouch) {
			p.setMoneyInPouch(p.getMoneyInPouch() - 750000);
			p.getPacketSender().sendString(8135, ""+p.getMoneyInPouch());
		} else
			p.getInventory().delete(995, 750000);
		TeleportHandler.teleportPlayer(p, new Position(3683, 9888, p.getIndex() * 4), p.getSpellbook().getTeleportType());
	}
}
