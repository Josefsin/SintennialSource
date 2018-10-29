package com.agaroth.world.content;

import com.agaroth.world.entity.impl.player.Player;

public class PointsHandler {

	private Player p;
	
	public PointsHandler(Player p) {
		this.p = p;
	}

	public PointsHandler refreshPanel() {
		p.getPacketSender().sendString(55021, "@or2@Prestige Points: @yel@" + prestigePoints);
	    p.getPacketSender().sendString(55022, "@or2@Commendations: @yel@ " + commendations);
	    p.getPacketSender().sendString(55023, "@or2@Loyalty Points: @yel@" + (int)loyaltyPoints);
	    p.getPacketSender().sendString(55024, "@or2@Dung. Tokens: @yel@ " + dungTokens);
	    p.getPacketSender().sendString(55025, "@or2@Voting Points: @yel@ " + votingPoints);
	    p.getPacketSender().sendString(55026, "@or2@Slayer Points: @yel@" + slayerPoints);
	    p.getPacketSender().sendString(55027, "@or2@Pk Points: @yel@" + pkPoints);
	    p.getPacketSender().sendString(55028, "@or2@Wilderness Killstreak: @yel@" + p.getPlayerKillingAttributes().getPlayerKillStreak());
	    p.getPacketSender().sendString(55029, "@or2@Wilderness Kills: @yel@" + p.getPlayerKillingAttributes().getPlayerKills());
	    p.getPacketSender().sendString(55030, "@or2@Wilderness Deaths: @yel@" + p.getPlayerKillingAttributes().getPlayerDeaths());
	    p.getPacketSender().sendString(55031, "@or2@Arena Victories: @yel@" + p.getDueling().arenaStats[0]);
	    p.getPacketSender().sendString(55032, "@or2@Arena Losses: @yel@" + p.getDueling().arenaStats[1]);
		
		return this;
	}

	public int prestigePoints;
	public int slayerPoints;
	public int commendations;
	public int dungTokens;
	public int pkPoints;
	public double loyaltyPoints;
	public int votingPoints;
	public int achievementPoints;
	public int DonatorPoints;
	public int IronManPoints;
	
	  public void setIronManPoints(int points, boolean add) {
		  if (add) {
		      this.IronManPoints += points;
		    } else {
		      this.IronManPoints = points;
		    }
	  }

	  public int getIronManPoints() {
		return this.IronManPoints;
	  }
	  
	public int getDonatorPoints() {
	    return this.DonatorPoints;
	}

	  public void setDonatorPoints(int points, boolean add) {
	    if (add) {
	      this.DonatorPoints += points;
	    } else {
	      this.DonatorPoints = points;
	    }
	}
	  
	public int getPrestigePoints() {
		return prestigePoints;
	}
	
	public void setPrestigePoints(int points, boolean add) {
		if(add)
			this.prestigePoints += points;
		else
			this.prestigePoints = points;
	}

	public int getSlayerPoints() {
		return slayerPoints;
	}

	public void setSlayerPoints(int slayerPoints, boolean add) {
		if(add)
			this.slayerPoints += slayerPoints;
		else
			this.slayerPoints = slayerPoints;
	}

	public int getCommendations() {
		return this.commendations;
	}

	public void setCommendations(int commendations, boolean add) {
		if(add)
			this.commendations += commendations;
		else
			this.commendations = commendations;
	}

	public int getLoyaltyPoints() {
		return (int)this.loyaltyPoints;
	}

	public void setLoyaltyPoints(int points, boolean add) {
		if(add)
			this.loyaltyPoints += points;
		else
			this.loyaltyPoints = points;
	}
	
	public void incrementLoyaltyPoints(double amount) {
		this.loyaltyPoints += amount;
	}
	
	public int getPkPoints() {
		return this.pkPoints;
	}

	public void setPkPoints(int points, boolean add) {
		if(add)
			this.pkPoints += points;
		else
			this.pkPoints = points;
	}
	
	public int getDungeoneeringTokens() {
		return dungTokens;
	}

	public void setDungeoneeringTokens(int dungTokens, boolean add) {
		if(add)
			this.dungTokens += dungTokens;
		else
			this.dungTokens = dungTokens;
	}
	
	public int getVotingPoints() {
		return votingPoints;
	}
	
	public void setVotingPoints(int votingPoints) {
		this.votingPoints = votingPoints;
	}
	
	public void incrementVotingPoints() {
		this.votingPoints++;
	}
	
	public void incrementVotingPoints(int amt) {
		this.votingPoints += amt;
	}
	
	public void setVotingPoints(int points, boolean add) {
		if(add)
			this.votingPoints += points;
		else
			this.votingPoints = points;
	}
	
	public int getAchievementPoints() {
		return achievementPoints;
	}
	
	public void setAchievementPoints(int points, boolean add) {
		if(add)
			this.achievementPoints += points;
		else
			this.achievementPoints = points;
	}
}
