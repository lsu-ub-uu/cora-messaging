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

	@Test
	public void testInitParentMessageRoutingInfo() {
		String hostname = "messaging.alvin-portal.org";
		String port = "5672";
		String routingKey = "alvin.updates.#";

		MessageRoutingInfo routingInfo = new MessageRoutingInfo(hostname, port, routingKey);

		assertEquals(routingInfo.hostname, hostname);
		assertEquals(routingInfo.port, port);
		assertEquals(routingInfo.routingKey, routingKey);
	}

	@Test
	public void testInitJMSMessageRoutingInfo() throws Exception {
		String hostname = "messaging.alvin-portal.org";
		String port = "5672";
		String routingKey = "alvin.updates.#";
		String username = "admin";
		String password = "admin";

		JmsMessageRoutingInfo routingInfo = new JmsMessageRoutingInfo(hostname, port, routingKey,
				username, password);

		assertEquals(routingInfo.hostname, hostname);
		assertEquals(routingInfo.port, port);
		assertEquals(routingInfo.routingKey, routingKey);
		assertEquals(routingInfo.username, username);
		assertEquals(routingInfo.password, password);
	}

	@Test
	public void testInitAMQPMessageRoutingInfo() throws Exception {
		// TODO test for AMQP
		String hostname = "messaging.alvin-portal.org";
		String port = "5672";
		String routingKey = "alvin.updates.#";
		String virtualHost = "alvin";
		String exchange = "index";

		AmqpMessageRoutingInfo routingInfo = new AmqpMessageRoutingInfo(hostname, port, virtualHost,
				exchange, routingKey);

		assertEquals(routingInfo.hostname, hostname);
		assertEquals(routingInfo.port, port);
		assertEquals(routingInfo.routingKey, routingKey);
		assertEquals(routingInfo.virtualHost, virtualHost);
		assertEquals(routingInfo.exchange, exchange);
	}

}
