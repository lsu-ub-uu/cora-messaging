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

public class ChannelInfoTest {

	@Test
	public void testCompleteChannelInfo() {
		String hostname = "messaging.alvin-portal.org";
		String port = "5672";
		String virtualHost = "alvin";
		String exchange = "index";
		String routingKey = "alvin.updates.#";
		MessageRoutingInfo routingInfo = new MessageRoutingInfo(hostname, port, virtualHost, exchange,
				routingKey);
		assertEquals(routingInfo.hostname, hostname);
		assertEquals(routingInfo.port, port);
		assertEquals(routingInfo.virtualHost, virtualHost);
		assertEquals(routingInfo.exchange, exchange);
		assertEquals(routingInfo.routingKey, routingKey);

	}

}
