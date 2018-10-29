package com.agaroth.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import com.agaroth.engine.task.TaskManager;
import com.agaroth.world.World;
import com.agaroth.world.content.clan.ClanChatManager;
import com.agaroth.world.content.grandexchange.GrandExchangeOffers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public final class GameEngine implements Runnable {

	private final ScheduledExecutorService logicService = GameEngine.createLogicService();   
	
	@Override
	public void run() {
		try {
			TaskManager.sequence();
			World.sequence();
		} catch (Throwable e) {
			e.printStackTrace();
			World.savePlayers();
			GrandExchangeOffers.save();
			ClanChatManager.save();
		}
	}
	public void submit(Runnable t) {
		try {
			logicService.execute(t);
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

	/** STATIC **/

	public static ScheduledExecutorService createLogicService() {
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		executor.setRejectedExecutionHandler(new CallerRunsPolicy());
		executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("LogicServiceThread").build());
		executor.setKeepAliveTime(45, TimeUnit.SECONDS);
		executor.allowCoreThreadTimeOut(true);
		return Executors.unconfigurableScheduledExecutorService(executor);
	}
}
