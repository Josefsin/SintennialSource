package com.agaroth.net;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.netty.channel.Channel;

import com.agaroth.GameSettings;
import com.agaroth.net.packet.Packet;
import com.agaroth.net.packet.PacketBuilder;
import com.agaroth.net.packet.PacketConstants;
import com.agaroth.net.packet.PacketListener;
import com.agaroth.net.packet.codec.PacketDecoder;
import com.agaroth.net.packet.impl.ButtonClickPacketListener;
import com.agaroth.net.packet.impl.EquipPacketListener;
import com.agaroth.net.packet.impl.ItemActionPacketListener;
import com.agaroth.world.entity.impl.player.Player;

/**
 * The session handler dedicated to a player that will handle input and output
 * operations.
 *
 * @author lare96 <http://github.com/lare96>
 * @author blakeman8192
 */
public final class PlayerSession {

	/**
	 * The queue of messages that will be handled on the next sequence.
	 */
	private final Queue<Packet> prioritizedMessageQueue = new ConcurrentLinkedQueue<>();
	private final Queue<Packet> messageQueue = new ConcurrentLinkedQueue<>();

	/**
	 * The channel that will manage the connection for this player.
	 */
	private final Channel channel;

	/**
	 * The player I/O operations will be executed for.
	 */
	private Player player;

	/**
	 * The current state of this I/O session.
	 */
	private SessionState state = SessionState.CONNECTED;

	/**
	 * The amount of packets added in this cycle
	 */
	private int addedPackets;

	/**
	 * Creates a new {@link PlayerSession}.
	 *
	 * @param key
	 *            the selection key registered to the selector.
	 * @param response
	 *            the current login response for this session.
	 */
	public PlayerSession(Channel channel) {
		this.channel = channel;
	}

	/**
	 * Queues the {@code msg} for this session to be encoded and sent to the
	 * client.
	 *
	 * @param msg
	 *            the message to queue.
	 */
	public void queueMessage(PacketBuilder msg) {
		try {
			if (!channel.isOpen())
				return;
			channel.write(msg.toPacket());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Processes all of the queued messages from the {@link PacketDecoder} by
	 * polling the internal queue, and then handling them via the handleInputMessage.
	 */
	public void handleQueuedMessages() {
		Packet msg;
		while ((msg = prioritizedMessageQueue.poll()) != null) {
			handleInputMessage(msg);
		}
		while ((msg = messageQueue.poll()) != null) {
			handleInputMessage(msg);
		}
		addedPackets = 0;
	}

	/**
	 * Handles an incoming message.
	 * @param msg	The message to handle.
	 */
	public void handleInputMessage(Packet msg) {
		int op = msg.getOpcode();
		PacketListener listener = PacketConstants.PACKETS[op];
		listener.handleMessage(player, msg);
	}

	/**
	 * Uses state-machine to handle upstream messages from Netty.
	 * 
	 * @param msg
	 *            the message to handle.
	 */
	public void handleIncomingMessage(Packet msg) {
		if (addedPackets <= GameSettings.DECODE_LIMIT) {

			if(prioritizedPacket(msg.getOpcode())) {
				prioritizedMessageQueue.add(msg);
			} else {
				messageQueue.add(msg);
			}

			addedPackets++;
		} else {
			System.out.println("Refuse to add more packets to queue for "+player.getUsername()+". Already added "+addedPackets+" this cycle!!!");
			clearMessages();
		}
	}

	public void clearMessages() {
		prioritizedMessageQueue.clear();
		messageQueue.clear();
		addedPackets = 0;
	}

	
	public static boolean prioritizedPacket(int op) {
		return op == EquipPacketListener.OPCODE || op == ItemActionPacketListener.FIRST_ITEM_ACTION_OPCODE || op == ButtonClickPacketListener.OPCODE;
	}

	/**
	 * Gets the player I/O operations will be executed for.
	 *
	 * @return the player I/O operations.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the current state of this I/O session.
	 *
	 * @return the current state.
	 */
	public SessionState getState() {
		return state;
	}

	/**
	 * Sets the value for {@link PlayerSession#state}.
	 *
	 * @param state
	 *            the new value to set.
	 */
	public void setState(SessionState state) {
		this.state = state;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
