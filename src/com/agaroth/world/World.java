package com.agaroth.world;

import java.util.Iterator;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import mysql.impl.Voting;

import com.agaroth.GameSettings;
import com.agaroth.model.MessageType;
import com.agaroth.model.PlayerRights;
import com.agaroth.util.Misc;
import com.agaroth.world.content.minigames.impl.FightPit;
import com.agaroth.world.content.minigames.impl.PestControl;
import com.agaroth.world.content.randomevents.EvilTree;
import com.agaroth.world.content.randomevents.ShootingStar;
import com.agaroth.world.entity.Entity;
import com.agaroth.world.entity.EntityHandler;
import com.agaroth.world.entity.impl.CharacterList;
import com.agaroth.world.entity.impl.npc.NPC;
import com.agaroth.world.entity.impl.player.Player;
import com.agaroth.world.entity.impl.player.PlayerHandler;
import com.agaroth.world.entity.updating.NpcUpdateSequence;
import com.agaroth.world.entity.updating.PlayerUpdateSequence;
import com.agaroth.world.entity.updating.UpdateSequence;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class World {

	private static CharacterList<Player> players = new CharacterList<>(1000);
	private static CharacterList<NPC> npcs = new CharacterList<>(2027);
	private static Phaser synchronizer = new Phaser(1);
	private static ExecutorService updateExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("UpdateThread").setPriority(Thread.MAX_PRIORITY).build());
    private static Queue<Player> logins = new ConcurrentLinkedQueue<>();
    private static Queue<Player> logouts = new ConcurrentLinkedQueue<>();
    private static Queue<Player> voteRewards = new ConcurrentLinkedQueue<>();

    public static void register(Entity entity) {
		EntityHandler.register(entity);
	}

	public static void deregister(Entity entity) {
		EntityHandler.deregister(entity);
	}

	public static Player getPlayerByName(String username) {
		Optional<Player> op = players.search(p -> p != null && p.getUsername().equals(Misc.formatText(username)));
		return op.isPresent() ? op.get() : null;
	}

	public static Player getPlayerByLong(long encodedName) {
		Optional<Player> op = players.search(p -> p != null && p.getLongUsername().equals(encodedName));
		return op.isPresent() ? op.get() : null;
	}

	public static void sendMessage(String message) {
		players.forEach(p -> p.getPacketSender().sendMessage(message));
	}
	public static void sendMessage(MessageType type, String message) {
		players.forEach(p -> p.getPacketSender().sendMessage(type, message));
	}
	public static void sendStaffMessage(String message) {
		players.stream().filter(p -> p != null && (p.getRights() == PlayerRights.OWNER || p.getRights() == PlayerRights.DEVELOPER || p.getRights() == PlayerRights.ADMINISTRATOR || p.getRights() == PlayerRights.MODERATOR || p.getRights() == PlayerRights.SUPPORT)).forEach(p -> p.getPacketSender().sendMessage(message));
	}
	
	public static void updateServerTime() {
		players.forEach(p -> p.getPacketSender().sendString(39161, "@or2@Server time: @or2@[ @yel@"+Misc.getCurrentServerTime()+"@or2@ ]"));
	}

	public static void updatePlayersOnline() {
		players.forEach(p -> p.getPacketSender().sendString(39160, "@or2@Players online:   @or2@[ @yel@"+(int)(players.size() * 1)+"@or2@ ]"));
		players.forEach(p -> p.getPacketSender().sendString(57003, "Players:  @gre@"+(int)(World.getPlayers().size() * 1)+""));
	}

	public static void savePlayers() {
		players.forEach(p -> p.save());
	}

	public static CharacterList<Player> getPlayers() {
		return players;
	}

	public static CharacterList<NPC> getNpcs() {
		return npcs;
	}
	
	public static void sequence() {
		        for (int amount = 0; amount < GameSettings.LOGIN_THRESHOLD; amount++) {
            Player player = logins.poll();
            if (player == null)
                break;
            PlayerHandler.handleLogin(player);
        }
        int amount = 0;
        Iterator<Player> $it = logouts.iterator();
        while ($it.hasNext()) {
            Player player = $it.next();
            if (player == null || amount >= GameSettings.LOGOUT_THRESHOLD)
                break;
            if (PlayerHandler.handleLogout(player)) {
                $it.remove();
                amount++;
            }
        }
                for(int i = 0; i < GameSettings.VOTE_REWARDING_THRESHOLD; i++) {
            Player player = voteRewards.poll();
            if (player == null)
                break;
            Voting.handleQueuedReward(player);
        }
        
        FightPit.sequence();
		PestControl.sequence();
		ShootingStar.sequence();
		EvilTree.sequence();
		UpdateSequence<Player> playerUpdate = new PlayerUpdateSequence(synchronizer, updateExecutor);
		UpdateSequence<NPC> npcUpdate = new NpcUpdateSequence();
		players.forEach(playerUpdate::executePreUpdate);
		npcs.forEach(npcUpdate::executePreUpdate);
		synchronizer.bulkRegister(players.size());
		players.forEach(playerUpdate::executeUpdate);
		synchronizer.arriveAndAwaitAdvance();
		players.forEach(playerUpdate::executePostUpdate);
		npcs.forEach(npcUpdate::executePostUpdate);
	}
	
	public static Queue<Player> getLoginQueue() {
		return logins;
	}
	
	public static Queue<Player> getLogoutQueue() {
		return logouts;
	}
	
	public static Queue<Player> getVoteRewardingQueue() {
		return voteRewards;
	}
}