package com.agaroth.world.content.combat.effect;

import com.agaroth.engine.task.Task;
import com.agaroth.world.entity.impl.player.Player;

public class CombatTeleblockEffect extends Task {

    private Player player;

    public CombatTeleblockEffect(Player player) {
        super(1, false);
        super.bind(player);
        this.player = player;
    }
    @Override
    public void execute() {

        if (player.getTeleblockTimer() <= 0) {
            player.getPacketSender().sendMessage(
                "You are no longer teleblocked.");
            this.stop();
            return;
        }
        player.decrementTeleblockTimer();
    }
}
