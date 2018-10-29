package com.agaroth.world.content.dialogue.impl;

import com.agaroth.GameSettings;
import com.agaroth.engine.task.Task;
import com.agaroth.engine.task.TaskManager;
import com.agaroth.model.Direction;
import com.agaroth.model.GameMode;
import com.agaroth.model.Position;
import com.agaroth.net.security.ConnectionHandler;
import com.agaroth.world.content.dialogue.Dialogue;
import com.agaroth.world.content.dialogue.DialogueExpression;
import com.agaroth.world.content.dialogue.DialogueManager;
import com.agaroth.world.content.dialogue.DialogueType;
import com.agaroth.world.entity.impl.player.Player;

public class Tutorial {
	public static void OptionalTutorial(Player player) {
	    player.setDialogueActionId(135);
	    DialogueManager.start(player, 135);
	}
	
	public static Dialogue get(Player p, int stage) {
		Dialogue dialogue = null;
		switch(stage) {
		case 0:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "Ah, a wise choice indeed! So let's get you started out,", "shall we? I'll give you a few tips and once you've finished", "listening to me, I'll give you a starter pack for your", "patience. Let's start with the most important aspect; money!" };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 1:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "You can earn money doing many different things in", "Sintennial. For example, see those Thieving stalls infront of ", "you? You can steal items from them and sell them to the", "merchant whose standing over there." };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3093, 3503));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 2:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"You can also sell items to the General store."};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3080, 3511));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 3:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.CONFUSED;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"... Or using the Grand Exchange."};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3091, 3495));
					p.setDirection(Direction.NORTH);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 4:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "The next important thing you need to learn is navigating.", "All important teleports can be found at the top of the", "Spellbook. Take a look, I've opened it for you!" };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(GameSettings.DEFAULT_POSITION.copy());
					p.setDirection(Direction.SOUTH);
					p.getPacketSender().sendTab(GameSettings.MAGIC_TAB);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 5:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "If you wish to navigate to a skill's training location,", "simply press the Skills teleport on spellbook and find skill to train." };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.getPacketSender().sendTab(GameSettings.MAGIC_TAB);				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 6:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "Enough of the boring stuff, let's show you some creatures!", "There are a bunch of bosses to fight in Sintennial.", "Every boss drops unique and good gear when killed.", "One example is the mighty Corporeal beast!" };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(2900, 4393));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 7:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "Ah.. The Ghost Town..", "Here, you can find a bunch of revenants.", "You can also fight other players." };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3666, 3486));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 8:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "Sintennial also has a lot of enjoyable minigames.", "This is the Graveyard Arena, an area that's been run over", "by Zombies. Your job is to simply to kill them all.", "Sounds like fun, don't you think?" };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3503, 3569));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 9:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "This is the donator's zone.", "Players who have a Donator rank can teleport here", "and take advantage of the resources that it has." };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3423, 2914));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 10:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "To receive a donator rank, you'd need to claim", "points worth at least $7 (for the Bronze donator rank).", "You can spend your donator points in our donator store", "which can be found at home." };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3096, 3498));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 11:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "Sintennial is a competitive game. Next to you is a scoreboard", "which you can use to track other players and their progress." };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3087, 3487));
					p.setDirection(Direction.SOUTH);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 12:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "That was almost all.", "I just want to remind you to vote for us on various", "gaming toplists. To do so, simply use the ::vote command.", "You will be rewarded!" };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(GameSettings.DEFAULT_POSITION.copy());
					p.setDirection(Direction.SOUTH);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 13:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "If you have any more questions, simply use the ::help", "command and a staff member should get back to you.", "You can also join the clanchat channel 'help' and ask", "other players for help there too. Have fun playing Sintennial!" };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 14:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
			          return new String[] { "If you have any more questions, simply use the ::help", "command and a staff member should get back to you.", "You can also join the clanchat channel 'help' and ask", "other players for help there too. Have fun playing Sintennial!" };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.setNewPlayer(false);
					if(ConnectionHandler.getStarters(p.getHostAddress()) <= GameSettings.MAX_STARTERS_PER_IP) {
						if(p.getGameMode() != GameMode.NORMAL) {
							p.getInventory().add(995, 10000).add(20104, 1);
						} else {
							p.getInventory().add(995, 5000000).add(20104, 1);
						}
						ConnectionHandler.addStarter(p.getHostAddress(), true);
						p.setReceivedStarter(true);
					} else {
						p.getPacketSender().sendMessage("Your connection has received enough starting items.");
					}
					p.getPacketSender().sendInterface(3559);
					p.getAppearance().setCanChangeAppearance(true);
					p.setPlayerLocked(false);
					TaskManager.submit(new Task(20, p, false) {
						@Override
						protected void execute() {
							if(p != null && p.isRegistered()) {
								p.getPacketSender().sendMessage("<img=11> @blu@For first 15 minutes you have item interaction.");
							}
							stop();
						}
					});
					p.save();
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		}
		return dialogue;
	}


}