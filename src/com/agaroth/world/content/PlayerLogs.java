package com.agaroth.world.content;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.agaroth.GameServer;

public class PlayerLogs {

	private static final String FILE_PATH = "./data/saves/logs/";
	private static String getTime() {
		Date getDate = new Date();
		String timeFormat = "M/d/yy hh:mma";
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		return "[" + sdf.format(getDate) + "]\t";
	}

	public static void log(String file, String writable) {
		GameServer.getLoader().getEngine().submit(() -> {
			try {
				FileWriter fw = new FileWriter(FILE_PATH + file + ".txt", true);
				if(fw != null) {
					fw.write(getTime() + writable + "\t");
					fw.write(System.lineSeparator());
					fw.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void eraseFile(String name) {
		try {
			File file = new File(FILE_PATH + name + ".txt");
			file.delete();
			log(name,"\t <----------------- File automatically cleaned ----------------->");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
