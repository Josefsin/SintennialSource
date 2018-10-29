package com.agaroth;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class CoresManager {

	protected static boolean shutdown;
	//public static WorldProcessor worldProcessor;
	public static ScheduledExecutorService fastExecutor;
	public static Timer fastExecutor1;
	public static ScheduledExecutorService slowExecutor;

	public static ExecutorService serverBossChannelExecutor;
	public static ExecutorService serverWorkerChannelExecutor;
	public static int serverWorkersCount;

	public static final void init() {

	//	worldProcessor = new WorldProcessor(Thread.MAX_PRIORITY);

		int availableProcessors = Runtime.getRuntime().availableProcessors();
		serverWorkersCount = availableProcessors >= 6 ? availableProcessors - (availableProcessors >= 12 ? 6 : 4) : 2;
		serverWorkerChannelExecutor = serverWorkersCount > 1
				? Executors.newFixedThreadPool(serverWorkersCount, new DecoderThreadFactory())
				: Executors.newSingleThreadExecutor(new DecoderThreadFactory());
		serverBossChannelExecutor = Executors.newSingleThreadExecutor(new DecoderThreadFactory());

		if (Runtime.getRuntime().availableProcessors() >= 6) { // 7
			// fastExecutor = Executors.newScheduledThreadPool(2);
			fastExecutor1 = new Timer("Fast Executor");
			fastExecutor = Executors.newSingleThreadScheduledExecutor(); // for now we dont need more than 1 vcore for
																			// this
			slowExecutor = Executors.newScheduledThreadPool(2);
		} else {
			fastExecutor1 = new Timer("Fast Executor");
			fastExecutor = Executors.newSingleThreadScheduledExecutor();
			slowExecutor = Executors.newSingleThreadScheduledExecutor();
		}
	//	worldProcessor.start();
	}

	public static final void shutdown() {
		shutdown = true;
		serverBossChannelExecutor.shutdown();
		serverWorkerChannelExecutor.shutdown();

		fastExecutor.shutdown();
		slowExecutor.shutdown();
	}

	private CoresManager() {

	}
}
