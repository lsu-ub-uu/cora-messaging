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
 * MessageSender is used to send messages to a messaging system such as AMQP or JMS. Implementations
 * are normally created through a factory in an implementing package.
 */
public interface MessageSender {
	/**
	 * sendMessage sends a message to a messaging server, the server details, such as hostname, port
	 * etc. are specified when creating an instance of the implementing class.
	 * 
	 * @param headers
	 *            A Map<String, Object> that are sent as headers in the message
	 * @param message
	 *            A String with the body of the message
	 */
	void sendMessage(Map<String, Object> headers, String message);
}
