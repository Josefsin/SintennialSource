package com.agaroth.world.content.dialogue;

public abstract class Dialogue {

	public abstract DialogueType type();
	public abstract DialogueExpression animation();
	public abstract String[] dialogue();
	public Dialogue nextDialogue() {
		return null;
	}
	public int nextDialogueId() {
		return -1;
	}
	public int id() {
		return DialogueManager.getDefaultId();
	}
	public int npcId() {
		return -1;
	}
	public String[] item() {
		return null;
	}
	public void specialAction() {
		
	}
}