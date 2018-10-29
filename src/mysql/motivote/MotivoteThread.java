package mysql.motivote;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public final class MotivoteThread extends Thread
{
	private final Motivote<?> motivote;
	private boolean reward = false;
	private boolean running;

	public MotivoteThread(Motivote<?> motivote)
	{
		this.motivote = motivote;
		this.setName("Motivote");
		this.running = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run()
	{
		while(running) {
			try {

				/** FINALIZING VOTES **/
				if(!motivote.finalized.isEmpty()) {
					String ids = "";
					for(int i : motivote.finalized) {
						ids += i + ",";
					}
					ids = ids.substring(0, ids.length() - 1);

					HttpURLConnection httpcon = (HttpURLConnection)new URL(motivote.pageURL() + "?do=finalize&type=" + (reward ? "rewards" : "votes") + "&key=" + motivote.securityKey() + "&ids=" + ids).openConnection();
					httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
					BufferedReader r  = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), Charset.forName("UTF-8")));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = r.readLine()) != null) {
						sb.append(line);
					}
					String out = sb.toString();
					r.close();

					if (out.equalsIgnoreCase("success"))
					{
						Motivote.print("Finalized " + motivote.finalized.size() + " " + (reward ? "rewards" : "votes"));
						motivote.finalized.clear();
					}
					else
					{
						Motivote.print(out);
					}
				}

				/** ADDING VOTES **/


				HttpURLConnection httpcon = (HttpURLConnection) new URL(motivote.pageURL() + "?do=pending&key=" + motivote.securityKey()).openConnection();
				httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
				BufferedReader r  = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), Charset.forName("UTF-8")));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
					sb.append(line);
				}
				String out = sb.toString();

				JSONParser parser = new JSONParser();
				Object dat = parser.parse(out);

				if (dat instanceof JSONObject)
				{
					JSONObject obj = (JSONObject)dat;

					if (obj.containsKey("error"))
					{
						Motivote.print("Error: " + ((JSONObject)obj).get("error"));
					}
					else
					{

						reward = (boolean)obj.get("reward");

						synchronized(motivote.pending)
						{
							synchronized(motivote.finalized)
							{
								JSONArray dataArray = null;

								if (reward && obj.containsKey("rewards"))
								{
									dataArray = (JSONArray)obj.get("rewards");
								}
								else if (obj.containsKey("votes"))
								{
									dataArray = (JSONArray)obj.get("votes");
								}

								if (dataArray != null)
								{
									JSONObject[] datas = (JSONObject[])dataArray.toArray(new JSONObject[0]);

									for (JSONObject v : datas)
									{
										int internalID = Integer.parseInt((String)v.get("id"));

										if (!motivote.finalized.contains(internalID) && !motivote.pending.contains(internalID))
										{
											motivote.pending.add(internalID);
											String user = (String)v.get("user");
											String ip = (String)v.get("ip");

											if (reward)
											{
												Reward re = new Reward(motivote, internalID, Integer.parseInt((String)v.get("incentive")), user, ip, (String)v.get("name"), Integer.parseInt((String)v.get("amount")));
												((Motivote<Reward>)motivote).handler().onCompletion(re);
											}
											else
											{
												Vote vo = new Vote(motivote, internalID, Integer.parseInt((String)v.get("site")), user, ip);
												((Motivote<Vote>)motivote).handler().onCompletion(vo);
											}

											//motivote.pending.remove((Integer)internalID);
										}
									}
								}

								motivote.pending.clear();
							}
						}
					}
				}
				else
				{
					Motivote.print(dat);
				}

				/** 5 seconds sleep **/
				Thread.sleep(5000);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
