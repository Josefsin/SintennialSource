package mysql.impl;

import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.content.Achievements;
import com.agaroth.world.content.Achievements.AchievementData;
import com.agaroth.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.agaroth.world.content.PlayerLogs;
import com.agaroth.world.entity.impl.player.Player;

import mysql.motivote.MotivoteHandler;
import mysql.motivote.Vote;

public class Voting extends MotivoteHandler<Vote> {

	private static int VOTES;
	
	@Override
	public void onCompletion(Vote reward) {
		Player p = World.getPlayerByName(Misc.formatText(reward.username()));
		if (p != null) {
			
			if(Dungeoneering.doingDungeoneering(p)) {
				if(!p.voteMessageSent()) {
					p.getPacketSender().sendMessage("<col=900000>You will receive your vote reward once you're done Dungeoneering.");
					p.setVoteMessageSent(true);
				}
				return;
			}
			
			reward.complete();
			
		/*	if(reward.ip() != null && !reward.ip().equals(p.getHostAddress())) {
				p.getPacketSender().sendMessage("<col=900000>Warning! Our anti-cheat system has detected an invalid vote for your account.");
				PlayerLogs.log(p.getUsername(), "Anti-cheat system detected invalid vote attempt!");
			} else  {*/
				World.getVoteRewardingQueue().add(p); //Needs to be synchronized with game tick
		//	}
		}
	}
	
	public static void handleQueuedReward(Player p) {
		p.setVoteMessageSent(false);
		p.getPacketSender().sendInterfaceRemoval().sendMessage("We've recorded a vote for your account, enjoy your reward!");
		p.getInventory().add(19670, 1);
		Achievements.doProgress(p, AchievementData.VOTE_100_TIMES);
		PlayerLogs.log(p.getUsername(), "Player received vote reward!");
		if(VOTES >= 15) {
			World.sendMessage("<img=11> <col=008FB2>Another 20 votes have been claimed! Vote now using the ::vote command!");
			VOTES = 0;
		}
		VOTES++;
	}
}