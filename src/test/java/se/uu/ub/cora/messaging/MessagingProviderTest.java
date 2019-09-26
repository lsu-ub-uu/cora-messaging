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
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

public class MessagingProviderTest {

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<MessagingProvider> constructor = MessagingProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));

	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<MessagingProvider> constructor = MessagingProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testSetMessagingFactoryForUsingInAnotherTest() throws Exception {

		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactorySpy);

		String hostname = "messaging.alvin-portal.org";
		String port = "5672";
		String channel = "alvin.updates.#";

		ChannelInfo channelInfo = new ChannelInfo(hostname, port, channel);
		MessageSender messageSender = MessagingProvider.getTopicMessageSender(channelInfo);

		assertEquals(messagingFactorySpy.channelInfo, channelInfo);
		assertEquals(messageSender, messagingFactorySpy.messageSender);
	}

}
