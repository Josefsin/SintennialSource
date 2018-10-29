package com.agaroth.world.content.dialogue;

import com.agaroth.model.Animation;
public enum DialogueExpression {
	NO_EXPRESSION(9760),
	NO_EXPRESSION_TWO(9772),
	SAD(9764),
	SAD_TWO(9768),
	WHY(9776),
	SCARED(9780),
	MIDLY_ANGRY(9784),
	ANGRY(9788),
	VERY_ANGRY(9792),
	ANGRY_TWO(9796),
	MANIC_FACE(9800),
	JUST_LISTEN(9804),
	PLAIN_TALKING(9808),
	LOOK_DOWN(9812),
	CONFUSED(9816),
	CONFUSED_TWO(9820),
	WIDEN_EYES(9824),
	CROOKED_HEAD(9828),
	GLANCE_DOWN(9832),
	UNSURE(9836),
	LISTEN_LAUGH(9840),
	TALK_SWING(9844),
	NORMAL(9847),
	GOOFY_LAUGH(9851),
	NORMAL_STILL(9855),
	THINKING_STILL(9859),
	LOOKING_UP(9862), CALM(9805);
	private DialogueExpression(int animationId) {
		animation = new Animation(animationId);
	}
	private final Animation animation;
	public Animation getAnimation() {
		return animation;
	}
}