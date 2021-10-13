/*
 * Copyright 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.messaging;

import java.util.Map;

/**
 * MessageReceiver handles incoming messages from a {@link MessageListener}.
 * <p>
 * Implementations of MessageReciever MUST be threadsafe.
 */
public interface MessageReceiver {
	/**
	 * receiveMessage gets a call for each message recieved by the {@link MessageListener} it is
	 * connected to.
	 * <p>
	 * Implementing classes use this method to handle incoming messages.
	 * 
	 * <p>
	 * <em>Note, that multiple calls can be made simultaniously to this method</em>
	 * 
	 * @param headers
	 *            Map with the JMS message headers.
	 * @param message
	 *            String with the JMS message body.
	 */
	void receiveMessage(Map<String, String> headers, String message);

	/**
	 * topicClosed might be called by implementing messaging systems when a connection to the
	 * specified message queue is closed
	 */
	void topicClosed();

}
