package com.agaroth;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mysql.MySQLController;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;

import com.agaroth.engine.GameEngine;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.engine.task.impl.ServerTimeUpdateTask;
import com.agaroth.model.container.impl.Shop.ShopManager;
import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.model.definitions.NPCDrops;
import com.agaroth.model.definitions.NpcDefinition;
import com.agaroth.model.definitions.WeaponInterfaces;
import com.agaroth.net.PipelineFactory;
import com.agaroth.net.security.ConnectionHandler;
import com.agaroth.world.clip.region.RegionClipping;
import com.agaroth.world.content.CustomObjects;
import com.agaroth.world.content.Lottery;
import com.agaroth.world.content.PlayerPunishment;
import com.agaroth.world.content.Scoreboards;
import com.agaroth.world.content.WellOfGoodwill;
import com.agaroth.world.content.clan.ClanChatManager;
import com.agaroth.world.content.combat.effect.CombatPoisonEffect.CombatPoisonData;
import com.agaroth.world.content.combat.strategy.CombatStrategies;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.content.grandexchange.GrandExchangeOffers;
import com.agaroth.world.entity.impl.npc.NPC;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public final class GameLoader {

	private final ExecutorService serviceLoader = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("GameLoadingThread").build());
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("GameThread").build());
	private final GameEngine engine;
	private final int port;

	protected GameLoader(int port) {
		this.port = port;
		this.engine = new GameEngine();
	}

	public void init() {
		Preconditions.checkState(!serviceLoader.isShutdown(), "The bootstrap has been bound already!");
		executeServiceLoad();
		serviceLoader.shutdown();
	}

	public void finish() throws IOException, InterruptedException {
		if (!serviceLoader.awaitTermination(15, TimeUnit.MINUTES))
			throw new IllegalStateException("The background service load took too long!");
		ExecutorService networkExecutor = Executors.newCachedThreadPool();
		ServerBootstrap serverBootstrap = new ServerBootstrap (new NioServerSocketChannelFactory(networkExecutor, networkExecutor));
        serverBootstrap.setPipelineFactory(new PipelineFactory(new HashedWheelTimer()));
        serverBootstrap.bind(new InetSocketAddress(port));
		executor.scheduleAtFixedRate(engine, 0, GameSettings.ENGINE_PROCESSING_CYCLE_RATE, TimeUnit.MILLISECONDS);
		TaskManager.submit(new ServerTimeUpdateTask());
	}

	private void executeServiceLoad() {
		if (GameSettings.MYSQL_ENABLED) {
			serviceLoader.execute(() -> MySQLController.init());
		}
		serviceLoader.execute(() -> ConnectionHandler.init());
		serviceLoader.execute(() -> PlayerPunishment.init());
		serviceLoader.execute(() -> RegionClipping.init());
		serviceLoader.execute(() -> CustomObjects.init());
		serviceLoader.execute(() -> ItemDefinition.init());
		serviceLoader.execute(() -> Lottery.init());
		serviceLoader.execute(() -> GrandExchangeOffers.init());
		serviceLoader.execute(() -> Scoreboards.init());
		serviceLoader.execute(() -> WellOfGoodwill.init());
		serviceLoader.execute(() -> ClanChatManager.init());
		serviceLoader.execute(() -> CombatPoisonData.init());
		serviceLoader.execute(() -> CombatStrategies.init());
		serviceLoader.execute(() -> NpcDefinition.parseNpcs().load());
		serviceLoader.execute(() -> NPCDrops.parseDrops().load());
		serviceLoader.execute(() -> WeaponInterfaces.parseInterfaces().load());
		serviceLoader.execute(() -> ShopManager.parseShops().load());
		serviceLoader.execute(() -> DialogueManager.parseDialogues().load());
		serviceLoader.execute(() -> NPC.init());
	}

	public GameEngine getEngine() {
		return engine;
	}
}