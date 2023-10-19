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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class MessageRoutingInfoTest {

	private static final String QUEUE_NAME = "someQueue";
	private static final String HOSTNAME = "messaging.alvin-portal.org";
	private static final int PORT = 5672;
	private static final String VHOST = "alvin";
	private static final String EXCHANGE = "index";
	private static final String ROUTING_KEY = "alvin.updates.#";

	@Test
	public void testInitJMSMessageRoutingInfo() throws Exception {
		String username = "admin";
		String password = "admin";

		JmsMessageRoutingInfo routingInfo = new JmsMessageRoutingInfo(HOSTNAME, PORT, ROUTING_KEY,
				username, password);

		assertEquals(routingInfo.hostname, HOSTNAME);
		assertEquals(routingInfo.port, PORT);
		assertEquals(routingInfo.routingKey, ROUTING_KEY);
		assertEquals(routingInfo.username, username);
		assertEquals(routingInfo.password, password);
	}

	@Test
	public void testInitAMQPMessageSenderRoutingInfo() throws Exception {
		// TODO test for AMQP

		AmqpMessageSenderRoutingInfo routingInfo = new AmqpMessageSenderRoutingInfo(HOSTNAME, PORT,
				VHOST, EXCHANGE, ROUTING_KEY);

		assertEquals(routingInfo.hostname, HOSTNAME);
		assertEquals(routingInfo.port, PORT);
		assertEquals(routingInfo.routingKey, ROUTING_KEY);
		assertEquals(routingInfo.virtualHost, VHOST);
		assertEquals(routingInfo.exchange, EXCHANGE);
	}

	@Test
	public void testInitAMQPMessageListnerRoutingInfo() throws Exception {
		// TODO test for AMQP

		AmqpMessageListenerRoutingInfo routingInfo = new AmqpMessageListenerRoutingInfo(HOSTNAME,
				PORT, VHOST, QUEUE_NAME);

		assertEquals(routingInfo.hostname, HOSTNAME);
		assertEquals(routingInfo.port, PORT);
		assertEquals(routingInfo.virtualHost, VHOST);
		assertEquals(routingInfo.queueName, QUEUE_NAME);
	}

}
