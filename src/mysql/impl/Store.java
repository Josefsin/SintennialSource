package mysql.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import com.agaroth.GameServer;
import com.agaroth.world.content.MemberScrolls;
import com.agaroth.world.entity.impl.player.Player;

public class Store {

	public void claim(Player player){
		
		if(player.getSqlTimer().elapsed() <= 30000) {
			player.getPacketSender().sendMessage("You can only do this once every 30 seconds.");
			return;
		}
		
		GameServer.getLoader().getEngine().submit(() -> {
			
			player.getSqlTimer().reset();
			
			try {
				
				URL oracle = new URL("http://lunarisle.org/claim.php?username="+URLEncoder.encode(player.getUsername().replaceAll(" ", "_").toLowerCase(), "UTF-8"));
		        BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
		        String string = in.readLine();
		        int amount = Integer.parseInt(string);
		        
		        if(amount <= 0) {
		        	player.getPacketSender().sendMessage("There were no donator points found to be added to your account.");
		        	return;
		        }
		        
		        player.getPointsHandler().DonatorPoints += amount;
				player.incrementAmountDonated(amount);
				player.getPacketSender().sendMessage("Thank you for your contribution. Please relog for changes to take effect.");
				player.getPacketSender().sendMessage("@red@ "+amount+" Donator Points has been added to your account. Donated total: "+ player.getAmountDonated());
	        
				MemberScrolls.checkForRankUpdate(player);
				
			} catch(Throwable t) {
				player.getPacketSender().sendMessage("An error occured while using the auth code command.");
			}
			
		});
		
	}					
}
