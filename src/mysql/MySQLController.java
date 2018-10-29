package mysql;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.agaroth.GameSettings;

import mysql.impl.Store;
import mysql.impl.Voting;
import mysql.motivote.Motivote;
/**
 * @author Gabriel Hannason
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MySQLController {
	
	public static final ExecutorService SQL_SERVICE = Executors.newSingleThreadExecutor();   
	
	public static void toggle() {
		if(GameSettings.MYSQL_ENABLED) {
			MySQLProcessor.terminate();
			VOTE.terminate();
			CONTROLLER = null;
			DATABASES = null;
			GameSettings.MYSQL_ENABLED = false;
		} else if(!GameSettings.MYSQL_ENABLED) { 
			init();
			GameSettings.MYSQL_ENABLED = true;
		}
	}

	private static MySQLController CONTROLLER;
	private static Store STORE = new Store();
	private static Motivote VOTE;
	
	public static void init() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e) {
			e.printStackTrace();
		}
		CONTROLLER = new MySQLController();
	}

	public static MySQLController getController() {
		return CONTROLLER;
	}

	public static Store getStore() {
		return STORE;
	}

	public enum Database {
		HIGHSCORES,
		RECOVERY,
		GRAND_EXCHANGE,
		PLAYERS_ONLINE,
	}

	/* NON STATIC CLASS START */

	private static MySQLDatabase[] DATABASES = new MySQLDatabase[2];

	public MySQLDatabase getDatabase(Database database) {
		return DATABASES[database.ordinal()];
	}

	
	public MySQLController() {
		/* DATABASES */
		DATABASES = new MySQLDatabase[]{//how do you want to test this, its on port 43595 erm, shall i err.. whats the ip 1
				new MySQLDatabase("lunarisle.org", 3306, "lunarisl_hs", "lunarisl_hs", "lmao123"),
				null,//new MySQLDatabase("lunarisle.org", 3306, "amuliusp_recovery", "root", "VM2Cmh&1si^NeO"),
				null,//new MySQLDatabase("50.87.76.145", 3306, "rusepsco_grande", "rusepsco_grandeu", "MMNNBBHHYY123_c!!!"),
				null,//new MySQLDatabase("lunarisle.org", 3306, "amuliusp_world", "root", "VM2Cmh&1si^NeO"),
		};
		
                //netbeans can't affect that though, it sounds more reasonable that the database is just down
                //but it wasn't down before, here lets check shitclipse, close your eyesok
		/* VOTING */
		//VOTE = new Motivote(new Voting(), "http://amuliusrsps.com/vote/", "e176eb91");
		//VOTE.start();
	
		/*
		 * Start the process
		 */
		MySQLProcessor.process();
	}

	private static class MySQLProcessor {

		private static boolean running;
		
		private static void terminate() {
			running = false;
		}

		public static void process() {
			if(running) {
				return;
			}
			running = true;
			SQL_SERVICE.submit(new Runnable() {
				public void run() {
					try {
						while(running) {
							if(!GameSettings.MYSQL_ENABLED) {
								terminate();
								return;
							}
							for(MySQLDatabase database : DATABASES) {
								
								if(database == null) {
									continue;
								}
								
								if(!database.active) {
									continue;
								}

								if(database.connectionAttempts >= 5) {
									database.active = false;
								}

								Connection connection = database.getConnection();
								try {
									connection.createStatement().execute("/* ping */ SELECT 1");
								} catch (Exception e) {
									database.createConnection();
								}
							}
							Thread.sleep(25000);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
